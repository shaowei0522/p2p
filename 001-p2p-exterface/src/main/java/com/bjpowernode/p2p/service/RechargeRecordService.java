package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.RechargeRecord;

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
public interface RechargeRecordService {
    int addRechargeRecord(Map<String, Object> parseMap);

    void modifyRechargeRecordStatus(String out_trade_no, int i);

//  修改订单状态以及修改用户的账号信息
    int updateRechargeRecordeSuccessAndFinance(Map<String, Object> paramsMap2);

//    查询订单状态为零的订单
    List<RechargeRecord> queryRechargeRecordByStatusZero();

//   根据订单编号查询订单的信息
    RechargeRecord queryRechargeRecordByRechargeNo(String out_trade_no);

//
}
