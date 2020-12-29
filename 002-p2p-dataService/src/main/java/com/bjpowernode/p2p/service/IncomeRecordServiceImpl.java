package com.bjpowernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.mapper.BidMapper;
import com.bjpowernode.p2p.mapper.FinanceAccountMapper;
import com.bjpowernode.p2p.mapper.IncomeRecordMapper;
import com.bjpowernode.p2p.mapper.LoanMapper;
import com.bjpowernode.p2p.model.Bid;
import com.bjpowernode.p2p.model.FinanceAccount;
import com.bjpowernode.p2p.model.IncomeRecord;
import com.bjpowernode.p2p.model.Loan;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p.service
 * @Description: java类作用描述
 * @Author: 王少伟
 * @CreateDate: 2020/12/24 20:44
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
@Service(interfaceClass = IncomeRecordService.class,timeout = 20000,version = "1.0.0")
@org.springframework.stereotype.Service
@Transactional
public class IncomeRecordServiceImpl implements IncomeRecordService {
    @Autowired
    IncomeRecordMapper incomeRecordMapper;
    @Autowired
    LoanMapper loanMapper;
    @Autowired
    BidMapper bidMapper;
    @Autowired
    FinanceAccountMapper financeAccountMapper;


    @Override
    public void generateIncome() {
        //        查询符合满标条件的loan
        List<Loan> loans = loanMapper.selectByStatus();

//        对查询出来的loan进行遍历
        for (Loan loan : loans) {
            Integer productType = loan.getProductType();
            Integer cycle = loan.getCycle();
            Double rate = loan.getRate();
//            根据loanid查询bid_info
            List<Bid> bids = bidMapper.selectBidByLoanId(loan.getId());
//            对查询出来的bid进行遍历，===================一条bid对应一条incomeRecord
            for (Bid bid : bids) {
//                插入income
                IncomeRecord incomeRecord = new IncomeRecord();
                incomeRecord.setBidId(bid.getId());
                incomeRecord.setUid(bid.getUid());
                incomeRecord.setLoanId(bid.getLoanId());
                incomeRecord.setBidMoney(bid.getBidMoney());
                incomeRecord.setIncomeStatus(0);


//                date以及money的计算，此处需要进行判断，
                if (productType == 0) {
//                    此产品是新手宝，周期是天
                    Date date = DateUtils.addDays(new Date(), cycle);

                    incomeRecord.setIncomeDate(date);
//                    收益计算
                    incomeRecord.setIncomeMoney(rate/100/365*bid.getBidMoney()*cycle);


                } else {
                    Date date = DateUtils.addMonths(new Date(), cycle);
                    incomeRecord.setIncomeDate(date);
                    incomeRecord.setIncomeMoney(rate/100/365*bid.getBidMoney()*30*cycle);
                }

//                保存收益计划
                incomeRecordMapper.insertSelective(incomeRecord);
            }

//                修改产品状态
            loan.setProductStatus(2);
            loanMapper.updateByPrimaryKeySelective(loan);

        }
    }

    @Override
    public void genetateIncomeBack() {

//        日期等于今天，状态为零的 收益计划
        List<IncomeRecord> incomeRecords = incomeRecordMapper.selectIncomePlanByStatusAndCycle();

//        遍历每一条收益计划
        for (IncomeRecord incomeRecord : incomeRecords) {
            //3、返现
            FinanceAccount financeAccount = financeAccountMapper.selectAccountByUserId(incomeRecord.getUid());
            financeAccount.setAvailableMoney(financeAccount.getAvailableMoney()+incomeRecord.getBidMoney()+incomeRecord.getIncomeMoney());
            financeAccountMapper.updateByPrimaryKeySelective(financeAccount);

            //4、修改状态为 已返 1
            incomeRecord.setIncomeStatus(1);
            incomeRecordMapper.updateByPrimaryKeySelective(incomeRecord);
        }
    }
}
