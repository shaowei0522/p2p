package com.bjpowernode.p2p.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p.service
 * @Description: redis缓存服务器
 * @Author: 王少伟
 * @CreateDate: 2020/12/19 21:31
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
@Service(interfaceClass = RedisServer.class ,timeout = 20000,version = "1.0.0")
@org.springframework.stereotype.Service
public class RedisServerImpl implements RedisServer {
    @Autowired
    RedisTemplate redisTemplate;
    @Override
    public void push(String phone, String verificationCode) {
//        向redis中存放数据，设置生命周期为1分钟
        redisTemplate.opsForValue().set(phone, verificationCode,1, TimeUnit.MINUTES);

    }

    @Override
    public String pop(String phone) {
        return (String) redisTemplate.opsForValue().get(phone);
    }

    @Override
    public Long incrementNum() {
        return redisTemplate.opsForValue().increment(Constants.AUTO_INCRE_NUMBER,10000);

    }

}
