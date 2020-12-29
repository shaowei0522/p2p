package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.Loan;
import com.bjpowernode.p2p.util.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p.service
 * @Description: java类作用描述
 * @Author: 王少伟
 * @CreateDate: 2020/12/15 19:11
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
public interface LoanService {
    Double queryYearRate();

    List<Loan> queryLoanType(PageInfo pageInfo,String ptype);

    int queryLoanCount(String ptype);

    List<Loan> queryLoanType(Map<String, Object> parseMap);

    Loan queryLoanById(String id);

    List<Loan> queryLoanByStatus();
}
