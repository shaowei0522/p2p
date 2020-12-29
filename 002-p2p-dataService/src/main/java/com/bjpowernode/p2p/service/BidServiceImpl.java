package com.bjpowernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.mapper.BidMapper;
import com.bjpowernode.p2p.mapper.FinanceAccountMapper;
import com.bjpowernode.p2p.mapper.LoanMapper;
import com.bjpowernode.p2p.mapper.UserMapper;
import com.bjpowernode.p2p.model.Bid;
import com.bjpowernode.p2p.model.FinanceAccount;
import com.bjpowernode.p2p.model.Loan;
import com.bjpowernode.p2p.model.User;
import com.bjpowernode.p2p.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.List;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p.service
 * @Description: java类作用描述
 * @Author: 王少伟
 * @CreateDate: 2020/12/15 19:25
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
@Service(interfaceClass = BidService.class ,timeout = 20000,version = "1.0.0")
@Transactional
@org.springframework.stereotype.Service
@Slf4j
public class BidServiceImpl implements BidService {

    @Autowired
    BidMapper bidMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    LoanMapper loanMapper;
    @Autowired
    FinanceAccountMapper financeAccountMapper;
    @Override
    public Double queryMoneyAmount() {
        return bidMapper.selectMoneyAmount();
    }

    @Override
    public List<Bid> queryBidByLoanInfo(String id) {
        return bidMapper.selectBidByLoanInfo(id);
    }


    /**
     * 进行投资操作，此处十分关键，涉及到资金安全
     *
     *先决条件，查询版本号version，如果修改的时候版本号发生了改变，则不进行数据库的修改
     *
     * 首先查询loan,对投资金额进行确定，看看金额是否满足最大与最小的限制，
     * 此处为后台限制，前台虽然限制过了，但是有可能被跳过，所以此处需要再次校验
     *
     * 然后插入投资记录
     *
     * 更新loan的信息
     *
     * 更新loan的状态
     *
     * 最后，扣除用户的账号金额
     *
     * @param bidMoney
     * @param loanId
     * @param user
     * @return
     */
    @Override
    public String invest(String bidMoney, String loanId, User user) {

//        查询版本号version，对mapper.xml配置文件中的SQL语句进行修改，加上一个限制条件


//        然后查询该loan剩余可投金额
        Loan loan = loanMapper.selectByPrimaryKey(Integer.valueOf(loanId));

        if (loan.getBidMinLimit() > Integer.parseInt(bidMoney)
                || loan.getBidMaxLimit() < Integer.parseInt(bidMoney)) {
            return Result.BID_INSERT_ERR;

        }

//        先向bid表中插入数据，如果插入成功的话，再修改用户的账号信息
        Bid bid = new Bid();
        bid.setUid(user.getId());
        bid.setLoanId(Integer.valueOf(loanId));
        bid.setBidTime(new Date());
        bid.setBidMoney(Double.valueOf(bidMoney));

        bid.setBidStatus(1);

        int i = bidMapper.insertSelective(bid);
        if (i == 0) {
//            此处需要抛出异常，激活springboot的事务，进行事物的回滚，当然也可以进行手动事务的回滚

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.BID_INSERT_ERR;
        }



//        此处需要更改loan的信息，还要确保leftMoney不能小于零，所以需要手动编写SQL语句，同时加上了version限制条件
        i = loanMapper.updateLoanLeftMoney(loanId,bidMoney,loan.getVersion());
        if (i == 0) {
//            正常情况下，该条语句如果不被执行，一定是因为leftmoney不足，此时也有可能是因为版本号不一致导致语句不执行
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.LEFT_MONEY_ERROR;
        }


//        查询修改过后的loan，如果leftMoney为零，则更新状态，同时还要修改loan的投资满标时间
        loan = loanMapper.selectByPrimaryKey(Integer.valueOf(loanId));
        if (loan.getLeftProductMoney() == 0) {
            loan.setProductStatus(1);
            loan.setProductFullTime(new Date());
            i = loanMapper.updateByPrimaryKeySelective(loan);
            if (i == 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Result.UPDATE_LOAN_STATE_ERROR;
            }
        }

//        最后，修改用户账号的金额
        FinanceAccount financeAccount = financeAccountMapper.selectByUid(user.getId());
        financeAccount.setAvailableMoney(financeAccount.getAvailableMoney() - Double.valueOf(bidMoney));
        i = financeAccountMapper.updateAvailableMoneyByUserId(financeAccount);
        if (i == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.AVA_MONEY_ERR;
        }


        return Result.OK;
    }


    /**
     * 根据loanid查询bid
     * @param id
     * @return
     */
    @Override
    public List<Bid> queryBidByLoanId(Integer id) {
        return bidMapper.selectBidByLoanId(id);
    }
}
