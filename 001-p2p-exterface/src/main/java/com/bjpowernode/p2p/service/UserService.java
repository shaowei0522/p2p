package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.FinanceAccount;
import com.bjpowernode.p2p.model.User;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p.service
 * @Description: java类作用描述
 * @Author: 王少伟
 * @CreateDate: 2020/12/15 19:10
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
public interface UserService {
    int queryUserCount();

    User verifyPhone(String phone);

    User register(User user);

    int modifyUserByRealNameVerify(User user);

    User loginSubmit(User user);

    FinanceAccount queryFinanceAccount(Integer id);
}
