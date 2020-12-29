package com.bjpowernode.p2p;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p
 * @Description: java类作用描述
 * @Author: 王少伟
 * @CreateDate: 2020/12/24 11:43
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
@Component
@Slf4j
public class TimerTest {

    @Scheduled(cron = "0/5 * * * * ?")
    public void test(){
        log.info("定时器开始了");
        log.info("定时器结束了");

    }
}
