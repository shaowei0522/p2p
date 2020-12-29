package com.bjpowernode.p2p.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.model.RechargeRecord;
import com.bjpowernode.p2p.model.User;
import com.bjpowernode.p2p.service.RechargeRecordService;
import com.bjpowernode.p2p.service.RedisServer;
import com.bjpowernode.p2p.util.HttpClientUtils;
import com.bjpowernode.p2p.util.Result;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.FormattingTuple;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p.web
 * @Description: 用户充值
 * @Author: 王少伟
 * @CreateDate: 2020/12/26 19:58
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
@Controller
public class RechargeController {

    @Reference(interfaceClass = RechargeRecordService.class ,timeout = 20000,version = "1.0.0")
    RechargeRecordService rechargeRecordService;
    @Reference(interfaceClass = RedisServer.class ,timeout = 20000, version = "1.0.0")
    RedisServer redisServer;

    /**
     * 跳转至充值页面
     * @return
     */
    @RequestMapping("/loan/page/toRecharge")
    public String toRechargePage(){
        return "toRecharge";
    }


    /**
     * 第三方支付之支付宝支付
     *
     * 由于本项目是采用分布式的架构，所以为了屏蔽不同支付平台的差异，此处重新建一个pay支付module，
     * 由web调用该接口，然后该接口去调用不同的支付平台接口，降低程序的耦合度
     *
     * pay支付module主要负责调用支付宝提供的支付接口以及调用支付宝提供的查询接口
     *
     *基本流程：1、用户提交表单，服务器先生成订单，状态为未支付；
     *          2、此时向第三方支付平台支付宝发送请求，
     *          3、支付宝向用户提供付款页面，用户进行支付
     *          4、支付宝调用returnurl，然后得到数据进行验证，完成支付
     *          5、商家主动调用支付宝的查询接口进行支付状态的查询，完善用户的订单状态以及用户账号金额的更新
     *
     *
     *          6、此处因为沙箱系统的不稳定，所以returnurl并不起作用，所以需要进行主动调用支付宝的查询接口，
     *          如果得到支付成功的信息，则进行用户的数据库更新，否则还需要进行定时器的定时工作，处理那些用户已经支付的账单，但是并没有
     *          更新数据库的数据
     * @param rechargeMoney
     * @param servletRequest
     * @return
     */
    @RequestMapping("/loan/toRecharge/alipay")
    public String alipay(@RequestParam(value = "rechargeMoney",required = true) String rechargeMoney,
                         HttpServletRequest servletRequest, Model model){
        User user = (User) servletRequest.getSession(true).getAttribute(Constants.LOGIN_USER);
//        验证用户是否登录
        if (!ObjectUtils.allNotNull(user)) {
            return "redirect:/loan/page/login";
        }


//      生成订单，充值状态为未充值 0
//        订单号：时间戳+uid
        String rechargeNo = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + user.getId();

        Map<String, Object> parseMap = new HashMap<>();
        parseMap.put("uid", user.getId());
        parseMap.put("rechargeMoney", rechargeMoney);
        parseMap.put("rechargeDesc", "支付宝充值");
        parseMap.put("rechargeNo", rechargeNo);

        int i = rechargeRecordService.addRechargeRecord(parseMap);
        if (i == 0) {
            model.addAttribute("trade_msg", "订单生成失败");
            return "toRechargeBack";
        }

//        此处之所以不适用重定向的方式进行请求的主要原因是因为此处涉及了金钱交易，为了保证数据的安全，
//        所以采取的方案是先跳转至一个页面，然后进行表单的提交跳转至pay接口
//        调用pay模块向支付宝发送请求

        model.addAttribute("rechargeNo", rechargeNo);
        model.addAttribute("rechargeMoney", rechargeMoney);
        model.addAttribute("rechargeDesc", "支付宝充值");
        model.addAttribute("subject", "用户充值");

        return "toAliPay";

//        return "redirect:https://localhost:9006/006-p2p-pay/alipay/trade/pay?";

    }


    /**
     * 同步通知路径
     * @return
     */
    @RequestMapping("/loan/alipayBack")
    public String alipayBack(HttpServletRequest request,Model model) throws Exception {

        System.out.println("-----aliPayBack----");

        //获取支付宝GET过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        boolean signVerified=true;
        //调用SDK验证签名 : 验证数据安全
        // boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);

        //——请在这里编写您的程序（以下代码仅作参考）——

        if (signVerified) {
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
//            查询订单交易状态（按照支付宝的文档要求来看，此时其实并不应该进行业务的进行，
//            这一步其实只是验证签名，并没有进行交易，实际支付结果应该以异步通知为准），但是因为沙箱环境不稳定，
//            无法进行returnurl的调用，所以此处简化了做法，直接进行业务的处理
            Map<String ,Object> parseMap = new HashMap<String ,Object>();
            parseMap.put("out_trade_no", out_trade_no);
            String result = HttpClientUtils.doPost("http://localhost:9006/alipay/trade/alipayQuery", parseMap);
            System.out.println("result" + result);


            JSONObject jsonObject
                    = JSONObject.parseObject(result).getJSONObject("alipay_trade_query_response");
            String code = jsonObject.getString("code");
            if (!StringUtils.equals(code, "10000")) {
                model.addAttribute("trade_msg", "通信失败");
                return "toRechargeBack";
            }

            String tradeStatus = jsonObject.getString("trade_status");
            if (StringUtils.equals("TRADE_CLOSED", tradeStatus)) {
                model.addAttribute("trade_msg", "交易超时关闭");
//                修改订单状态为2
                rechargeRecordService.modifyRechargeRecordStatus(out_trade_no,2);
                return "toRechargeBack";
            }

//            此时表示支付宝交易成功
            if(StringUtils.equals("TRADE_SUCCESS",tradeStatus)){
                //修改订单状态为 成功 1,修改账户余额
                //查询订单信息，如果为1,不再执行

                User user=(User) request.getSession().getAttribute(Constants.LOGIN_USER);

                Map<String,Object> paramsMap2 = new HashMap<String,Object>();
                paramsMap2.put("uid",user.getId());
                paramsMap2.put("out_trade_no",out_trade_no);
                paramsMap2.put("total_amount",jsonObject.getDouble("total_amount"));


//               修改订单状态以及修改用户的账号信息
                int num= rechargeRecordService.updateRechargeRecordeSuccessAndFinance(paramsMap2);
                if(num!=1){
                    model.addAttribute("trade_msg","请等待，2个小时内到账或者联系客服");
                    return "toRechargeBack";
                }

                return "redirect:/loan/myCenter";
            }

        }else {
            System.out.println("验签失败");
        }
        //——请在这里编写您的程序（以上代码仅作参考）——

        return "";
    }



    /**
     * 第三方支付之微信支付
     * @return
     */
    @RequestMapping("/loan/toRecharge/weixinpay")
    public String weixinPay(@RequestParam(name="payType",required = true) String payType ,
                            @RequestParam(name="rechargeMoney",required = true) Double rechargeMoney,
                            HttpServletRequest request, Model model){


        System.out.println("---wxpay---"+payType);
        System.out.println("---wxpay---"+rechargeMoney);


        //验证：是否登录
        User user=(User) request.getSession().getAttribute(Constants.LOGIN_USER);
        if(!ObjectUtils.allNotNull(user)){
            return "redirect:/loan/page/login";
        }

        //生成订单：新增充值订单  状态为 充值中（0）
        //订单号:时间+uid
        String rechargeNo = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+user.getId()+redisServer.incrementNum();

        Map<String,Object> parasMap=new HashMap<String,Object>();
        parasMap.put("uid",user.getId());
        parasMap.put("rechargeMoney",rechargeMoney);
        parasMap.put("rechargeNo",rechargeNo);
        parasMap.put("rechargeDesc","微信充值");
        int num=rechargeRecordService.addRechargeRecord(parasMap);
        if(num!=1){
            model.addAttribute("trade_msg","订单生成失败");
            return "toRechargeBack";
        }

        model.addAttribute("rechargeNo",rechargeNo);
        model.addAttribute("rechargeMoney",rechargeMoney);

        return "wx";
    }


    /**
     * 生成一个二维码
     * 该请求由浏览器的二维码图片的src路径发送，
     * 此处主要是用来请求微信支付的统一支付接口
     *
     * 此处不能直接传递参数为订单编号和订单金额一起，是为了保证交易的安全
     *
     * @param response
     * @param out_trade_no
     * @throws IOException
     * @throws WriterException
     */
    @RequestMapping("/loan/generateQRCode")
    public void generateQRCode(HttpServletResponse response,
                               @RequestParam("out_trade_no") String out_trade_no) throws IOException, WriterException {

//        根据订单编号查询订单的信息
        RechargeRecord rechargeRecord = rechargeRecordService.queryRechargeRecordByRechargeNo(out_trade_no);

        Map<String ,Object> paramsMap = new HashMap<>();

        paramsMap.put("out_trade_no", out_trade_no);
        paramsMap.put("body", rechargeRecord.getRechargeDesc());
        paramsMap.put("total_fee", rechargeRecord.getRechargeMoney());

        String result = "";

//        请求pay模块的支付接口
        try {
            result = HttpClientUtils.doPost("http://localhost:9006/006-p2p-pay/pay/wxPay", paramsMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(result);

        JSONObject jsonObject = JSONObject.parseObject(result);
        String codeUrl = jsonObject.getString("code_url");
        if (ObjectUtils.allNotNull(codeUrl)) {
            //设置字符集
            Map<EncodeHintType,Object> map= new HashMap<EncodeHintType, Object>();
            map.put(EncodeHintType.CHARACTER_SET,"UTF-8");

            //创建一个矩阵对象
            BitMatrix bitMatrix = new MultiFormatWriter().encode(codeUrl, BarcodeFormat.QR_CODE, 200, 200, map);
            MatrixToImageWriter.writeToStream(bitMatrix,"jpg",response.getOutputStream());
        }

    }

}
