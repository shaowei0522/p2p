package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.mapper.FinanceAccountMapper;
import com.bjpowernode.p2p.mapper.UserMapper;
import com.bjpowernode.p2p.model.FinanceAccount;
import com.bjpowernode.p2p.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p.service
 * @Description: java类作用描述
 * @Author: 王少伟
 * @CreateDate: 2020/12/15 19:24
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
@Service
@Transactional
@com.alibaba.dubbo.config.annotation.Service(interfaceClass = UserService.class,timeout = 20000,version = "1.0.0")
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    FinanceAccountMapper financeAccountMapper;
    @Override
    public int queryUserCount() {

//        设置redis的key的序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());

//        第一次所有的线程都有权利从redis中读取数据
        Integer registUser = (Integer) redisTemplate.opsForValue().get("registUser");

//        如果第一次查询为命中
        if (registUser == null) {

//          进入同步代码块
            synchronized (this) {
//                如果第二次查询还是未命中，进入数据库查询
                registUser = (Integer) redisTemplate.opsForValue().get("registUser");
                if (registUser == null) {
                    System.out.println("请求数据库！");
                    registUser = userMapper.selectUserCount();
                    redisTemplate.opsForValue().set("registUser",registUser, 20, TimeUnit.SECONDS);
                }
            }
        }
        return registUser;
    }

    @Override
    public User verifyPhone(String phone) {
        return userMapper.selectByPhone(phone);
    }

    @Override
    public User register(User user) {
        user.setAddTime(new Date());
        int i1 = userMapper.insertSelective(user);
        if (i1 == 0) {
            System.out.println("对不起，系统异常！");
        }

//        进行用户礼包发放
        FinanceAccount financeAccount = new FinanceAccount();
        financeAccount.setUid(user.getId());
        financeAccount.setAvailableMoney(888d);

        int i = financeAccountMapper.insertSelective(financeAccount);
        if (i == 0) {
            System.out.println("对不起，数据库插入数据失败，请进行检查！");
        }


        return user;
    }

    @Override
    public int modifyUserByRealNameVerify(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public User loginSubmit(User user) {
        User user1 = userMapper.selectUserByPassword(user);
        if (user1 != null) {
//      此时表示用户的密码与账号是正确的，所以可以开辟一个线程用来更新用户的登录时间
            new Thread(new Runnable() {
                @Override
                public void run() {
//                    修改最后一次登录时间，因为这个数据并不是特别重要，
//                    所以另外开辟一个线程来执行该语句，成功与失败都无所谓
                    user1.setLastLoginTime(new Date());
                    userMapper.updateByPrimaryKeySelective(user1);
                }
            }).start();

        }
        return user1;
    }

    @Override
    public FinanceAccount queryFinanceAccount(Integer id) {
        return financeAccountMapper.selectByUid(id);
    }

}
