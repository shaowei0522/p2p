package com.bjpowernode.p2p.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.model.User;
import com.bjpowernode.p2p.service.BidService;
import com.bjpowernode.p2p.util.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p.web
 * @Description: java类作用描述
 * @Author: 王少伟
 * @CreateDate: 2020/12/23 10:54
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
@Controller
public class BidController {

    @Reference(interfaceClass =  BidService.class ,timeout = 20000,version = "1.0.0")
    BidService bidService;


    /**
     * 进行投资
     * @param bidMoney
     * @param loanId
     * @return
     */
    @RequestMapping("/loan/invest")
    @ResponseBody
    public Object invest(String bidMoney, String loanId, HttpServletRequest request){

        User user = (User) request.getSession().getAttribute(Constants.LOGIN_USER);


//        开启50个线程
/*        ExecutorService threadPool = Executors.newFixedThreadPool(50);
        threadPool.submit(new Runnable() {
            @Override
            public void run() {


            }
        });*/

//      对于高并发的情况，尽管在SQL语句上进行了条件限制，但是仍然有可能会出现leftMoney字段为负数的情况，
//      所以需要对这种情况进行处理，为了用户访问体验，不能简单粗暴的进行加锁，这样尽管数据绝对安全了，
//      但是用户体验是很差的，所以这里使用到了数据库中的一个备用字段，version，可以在修改之前查询一下version，
//      如果执行SQL语句的时候version发生了变化， 则不执行该条SQL语句，因为数据库已经在这之前被其它线程修改了，


        String  result = bidService.invest(bidMoney, loanId, user);

        if(StringUtils.equals(result,Result.AVA_MONEY_ERR)){
            return Result.error("账户余额不足,请充值");
        }
        if(StringUtils.equals(result,Result.LEFT_MONEY_ERROR)){
            return Result.error("剩余投资金额不足");
        }
        if(StringUtils.equals(result,Result.BID_INSERT_ERR)){
            return Result.error("添加投资记录失败");
        }
        if(StringUtils.equals(result,Result.UPDATE_LOAN_STATE_ERROR)){
            return Result.error("产品状态更新失败");
        }

        return Result.success();

    }
}
