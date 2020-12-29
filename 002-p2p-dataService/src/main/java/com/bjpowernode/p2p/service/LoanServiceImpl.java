package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.mapper.LoanMapper;
import com.bjpowernode.p2p.model.Loan;
import com.bjpowernode.p2p.util.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p.service
 * @Description: java类作用描述
 * @Author: 王少伟
 * @CreateDate: 2020/12/15 19:26
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
@Service
@com.alibaba.dubbo.config.annotation.Service(interfaceClass = LoanService.class,timeout = 20000,version = "1.0.0")
public class LoanServiceImpl implements LoanService {

    @Autowired
    LoanMapper loanMapper;
    @Override
    public Double queryYearRate() {
        return loanMapper.selectYearRate();
    }

    @Override
    public List<Loan> queryLoanType(PageInfo pageInfo,String ptype) {
        Integer currentPage = pageInfo.getCurrentPage();
        int pageSize = pageInfo.getPageSize();
//        此处根据当前的页码计算出limit的start信息
        int start = (currentPage - 1) * pageInfo.getPageSize();
        return loanMapper.selectLoanType(start,pageSize,ptype);
    }

    @Override
    public int queryLoanCount(String ptype) {
        return loanMapper.selectLoanCount(ptype);
    }

    @Override
    public List<Loan> queryLoanType(Map<String, Object> parseMap) {
        int start = (int) parseMap.get("start");
        int pageSize = (int) parseMap.get("end");
        String ptype = String.valueOf((int)parseMap.get("ptype"));

        return loanMapper.selectLoanType(start,pageSize,ptype);
    }

    @Override
    public Loan queryLoanById(String id) {
        return loanMapper.selectLoanById(id);
    }

    @Override
    public List<Loan> queryLoanByStatus() {
        return loanMapper.selectByStatus();
    }
}
