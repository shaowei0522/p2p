package com.bjpowernode.p2p.service;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p.service
 * @Description: java类作用描述
 * @Author: 王少伟
 * @CreateDate: 2020/12/19 21:29
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
public interface RedisServer {
//    向redis数据库中存放验证码
    void push(String phone, String verificationCode);
//    从redis数据库中取出验证码
    String pop(String phone);

//    生成一个自增的数字
    Long incrementNum();
}
