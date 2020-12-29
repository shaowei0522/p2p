package com.bjpowernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.mapper.FinanceAccountMapper;
import com.bjpowernode.p2p.mapper.RechargeRecordMapper;
import com.bjpowernode.p2p.model.FinanceAccount;
import com.bjpowernode.p2p.model.RechargeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p.service
 * @Description: java类作用描述
 * @Author: 王少伟
 * @CreateDate: 2020/12/26 20:01
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
@Service(interfaceClass = RechargeRecordService.class ,timeout = 20000,version = "1.0.0")
@org.springframework.stereotype.Service
public class RechargeRecordServiceImpl implements RechargeRecordService {

    @Autowired
    RechargeRecordMapper rechargeRecordMapper;
    @Autowired
    FinanceAccountMapper financeAccountMapper;
    @Override
    public int addRechargeRecord(Map<String, Object> parseMap) {

        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUid(Integer.parseInt(parseMap.get("uid").toString()));

        rechargeRecord.setRechargeMoney(Double.parseDouble(parseMap.get("rechargeMoney").toString()));
        rechargeRecord.setRechargeDesc(parseMap.get("rechargeDesc").toString());
        rechargeRecord.setRechargeNo(parseMap.get("rechargeNo").toString());
        rechargeRecord.setRechargeTime(new Date());
        rechargeRecord.setRechargeStatus("0");

        return rechargeRecordMapper.insertSelective(rechargeRecord);
    }

    @Override
    public void modifyRechargeRecordStatus(String out_trade_no, int i) {

        rechargeRecordMapper.updateRechargeRecordStatus(out_trade_no,i);

    }

//  修改订单状态以及修改用户的账号信息
    @Override
    public int updateRechargeRecordeSuccessAndFinance(Map<String, Object> paramsMap2) {
//        修改订单的状态为1，即支付成功
        rechargeRecordMapper.updateRechargeRecordStatus((String) paramsMap2.get("out_trade_no"), 1);



//        修改用户的账户余额

        int i = financeAccountMapper.updateAvailableMoneyByUserIdAndTotalAmount(paramsMap2);
        if (i == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;

        }

        return i;
    }

    @Override
    public List<RechargeRecord> queryRechargeRecordByStatusZero() {
        return rechargeRecordMapper.selectRechargeRecordByStatusZero();

    }


//   根据订单编号查询订单的信息
    @Override
    public RechargeRecord queryRechargeRecordByRechargeNo(String out_trade_no) {
        return rechargeRecordMapper.selectRechargeRecordByRechargeNo(out_trade_no);
    }
}
