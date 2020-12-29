package com.bjpowernode.p2p;

import com.bjpowernode.p2p.util.HttpClientUtils;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p
 * @Description: 微信支付
 * @Author: 王少伟
 * @CreateDate: 2020/12/28 21:29
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
@Controller
public class WXpayController {

    /**
     * 商户系统调用该接口，请求微信的统一支付接口，然后对返回的xml文件进行解析
     * @return 微信支付系统返回的结果（map类型）
     */
    @PostMapping("/pay/wxPay")
    @ResponseBody
    public Object wxPay(@RequestParam(name="out_trade_no")String out_trade_no,
                        @RequestParam(name="body")String body,
                        @RequestParam(name="total_fee")Double total_fee) throws Exception {


//        请求微信统一支付接口所必须的参数
        Map<String, String> map=new HashMap<String, String>();
        map.put("appid","wx8a3fcf509313fd74");
        map.put("mch_id","1361137902");
        map.put("nonce_str", WXPayUtil.generateNonceStr());
        map.put("body", body);
        map.put("out_trade_no",out_trade_no);

        //BigDecimal
        BigDecimal bigDecimal=new BigDecimal(total_fee);
        bigDecimal=bigDecimal.multiply(new BigDecimal(100));

        map.put("total_fee", bigDecimal.intValue()+"");
        map.put("spbill_create_ip","127.0.0.1");
        map.put("notify_url","http://localhost:9005/005-p2p-web/loan/wxpay");
        map.put("trade_type","NATIVE");
        map.put("product_id",out_trade_no);

        //验签：保证数据的安全
        String signature = WXPayUtil.generateSignature(map, "367151c5fd0d50f1e34a68a802d6bbca");
        map.put("sign",signature);

        String xml = HttpClientUtils.doPostByXml("https://api.mch.weixin.qq.com/pay/unifiedorder", WXPayUtil.mapToXml(map));
        System.out.println("xml:"+xml);
        return WXPayUtil.xmlToMap(xml);

    }
}
