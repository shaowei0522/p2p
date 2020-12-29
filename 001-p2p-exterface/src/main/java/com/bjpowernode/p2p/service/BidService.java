package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.Bid;
import com.bjpowernode.p2p.model.User;

import java.util.List;

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
public interface BidService {
    Double queryMoneyAmount();

    List<Bid> queryBidByLoanInfo(String id);

    String invest(String bidMoney, String loanId, User user);

    List<Bid> queryBidByLoanId(Integer id);
}
