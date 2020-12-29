package com.bjpowernode.p2p;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.p2p.model.Bid;
import com.bjpowernode.p2p.model.IncomeRecord;
import com.bjpowernode.p2p.model.Loan;
import com.bjpowernode.p2p.model.RechargeRecord;
import com.bjpowernode.p2p.service.BidService;
import com.bjpowernode.p2p.service.IncomeRecordService;
import com.bjpowernode.p2p.service.LoanService;
import com.bjpowernode.p2p.service.RechargeRecordService;
import com.bjpowernode.p2p.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p
 * @Description: java类作用描述
 * @Author: 王少伟
 * @CreateDate: 2020/12/24 20:07
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
@Component
@Slf4j
public class TaskTimer {


    @Reference(interfaceClass = IncomeRecordService.class,timeout = 20000,version = "1.0.0")
    IncomeRecordService incomeRecordService;

    @Reference(interfaceClass = RechargeRecordService.class ,timeout = 20000,version = "1.0.0")
    RechargeRecordService rechargeRecordService;


    /**  生成收益计划  返回值 参数 都不需要，因为这是定时器执行，不是程序人员调用
     * 定时器，定时监控loan的状态，一旦满标，则生成收益计划
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void generateIncome(){

        incomeRecordService.generateIncome();

    }

    /**
     * 生成收益返现
      */
    @Scheduled(cron = "0/5 * * * * ?")
    public void generateIncomeBack(){
        incomeRecordService.genetateIncomeBack();

    }

    /**
     * 扫描订单状态为零的订单，即用户已经充值了但是并没有更新数据库的订单
     * 此处执行的其实就是阿里需要回调的哪个url，这里需要写一个定时器是因为阿里沙箱系统有一些问题无法回调
     * ，导致用户的账户以及订单并未更新，所以需要定时器进行回调
     *
     * 但是此处可能会出现重置一次加两次钱的情况，所以需要加上一个乐观锁！！！！！！！
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void scanRechargeRecord() throws Exception {

        System.out.println("-------开始---------");

        List<RechargeRecord> rechargeRecordList = rechargeRecordService.queryRechargeRecordByStatusZero();

        for(RechargeRecord rechargeRecord:rechargeRecordList){
            //if(){}
            //查询订单交易状态。。。。
            Map<String,Object> paramsMap = new HashMap<String,Object>();
            paramsMap.put("out_trade_no",rechargeRecord.getRechargeNo());
            String result= HttpClientUtils.doPost("http://localhost:9006/006-p2p-pay/alipay/trade/alipayQuery",paramsMap);
            JSONObject jsonObject = JSONObject.parseObject(result).getJSONObject("alipay_trade_query_response");

            String code = jsonObject.getString("code");
            if(StringUtils.equals("10000",code)){
                String tradeStatus = jsonObject.getString("trade_status");
                if(StringUtils.equals("TRADE_CLOSED",tradeStatus)){
                    //修改订单状态为 失败  2
                    rechargeRecordService.modifyRechargeRecordStatus(rechargeRecord.getRechargeNo(),2);
                }

                if(StringUtils.equals("TRADE_SUCCESS",tradeStatus)){
                    //修改订单状态为 成功 1,修改账户余额
                    //查询订单信息，如果为1,不再执行
                    Map<String,Object> paramsMap2 = new HashMap<String,Object>();
                    paramsMap2.put("uid",rechargeRecord.getUid());
                    paramsMap2.put("out_trade_no",rechargeRecord.getRechargeNo());
                    paramsMap2.put("total_amount",rechargeRecord.getRechargeMoney());
                    rechargeRecordService.updateRechargeRecordeSuccessAndFinance(paramsMap2);

                }
            }

        }
        System.out.println("---结束----");


    }

}
