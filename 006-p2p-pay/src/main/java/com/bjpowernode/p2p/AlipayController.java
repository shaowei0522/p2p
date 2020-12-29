package com.bjpowernode.p2p;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.bjpowernode.p2p.config.AlipayConfig;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p
 * @Description: 对第三方支付平台的接口的调用
 * @Author: 王少伟
 * @CreateDate: 2020/12/26 19:46
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
@Controller
public class AlipayController {


    /**
     * 请求支付宝的支付接口
     * @param out_trade_no
     * @param total_amount
     * @param subject
     * @param body
     * @param model
     * @return
     * @throws AlipayApiException
     */
    @RequestMapping("/alipay/trade/pay")
    public String pay(@RequestParam(required = true,name = "out_trade_no") String out_trade_no,
                      @RequestParam(required = true,name = "total_amount") String total_amount,
                      @RequestParam(required = true,name = "subject") String subject,
                      @RequestParam(required = true,name = "body") String body, Model model) throws AlipayApiException {

        System.out.println("hello to pay!!!!");
//       此时pay接口接收到了服务器发送的请求，则需要进行请求支付宝的支付接口，支付宝负责返回一个支付页面给用户

//        获得初始化的alipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);

        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);


        //商户订单号，商户网站订单系统中唯一订单号，必填
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //请求
        String result = alipayClient.pageExecute(alipayRequest).getBody();

        model.addAttribute("result", result);

//        输出
//        reponse.getWriter().println(result);

//        此处是阿里返回的一个付款页面，用户进行支付操作
        return "toAliPay";
    }


    /**
     * 需要对订单的支付情况进行查询，商家主动查询支付宝提供的查询接口
     * @return
     */
    @RequestMapping("/alipay/trade/alipayQuery")
    @ResponseBody
    public String alipayQuery(@RequestParam(required = true,value = "out_trade_no")String out_trade_no) throws AlipayApiException {

        System.out.println("out_trade_no = " + out_trade_no);


//      获得初始化的alipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
/*        request.setBizContent("{" +
                "    \"out_trade_no\":\"20150320010101001\"," +
                "    \"trade_no\":\"2014112611001004680 073956707\"" +
                "  }");*/

        request.setBizContent("{'out_trade_no':'" + out_trade_no + "'}");
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        String result = response.getBody();

        System.out.println(result);
        return result;
    }




}
