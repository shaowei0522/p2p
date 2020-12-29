package com.bjpowernode.p2p.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.service.BidService;
import com.bjpowernode.p2p.service.LoanService;
import com.bjpowernode.p2p.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p.web
 * @Description: java类作用描述
 * @Author: 王少伟
 * @CreateDate: 2020/12/15 19:16
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
@Controller
public class IndexController {

    @Reference(interfaceClass = UserService.class,timeout = 20000,version = "1.0.0")
    UserService userService;

    @Reference(interfaceClass = BidService.class,timeout = 20000,version = "1.0.0")
    BidService bidService;

    @Reference(interfaceClass = LoanService.class,timeout = 20000,version = "1.0.0")
    LoanService loanService;

    @GetMapping("/index")
    public String test(Model model){

//        首页展示
//        平台用户数,此处最好使用redis进行缓存读取，减小数据库的压力，因为此处的数据并不是十分重要
        int registUser = userService.queryUserCount();
        model.addAttribute("registUser", registUser);
//        累计成交额，此处同理，使用redis技术，减小数据库的压力
        Double moneyAmount = bidService.queryMoneyAmount();
        model.addAttribute("moneyAmount", moneyAmount);
//        动力金融网历史年化收益率==>???
        Double yearRate = loanService.queryYearRate();
        model.addAttribute("yearRate", yearRate);

//        查询不同类型的借款,需要传递的参数有类型以及limit的个数,type,start,end,
//        可以选择查询三次,或者一次查询出来然后进行数据拆分,此处我选择查询三次
        Map<String,Object> parseMap = new HashMap<>();

//        新手宝
        parseMap.put("ptype", 0);
        parseMap.put("start", 0);
        parseMap.put("end", 1);
        model.addAttribute(Constants.LOANINFO_X, loanService.queryLoanType(parseMap));

        //        优选产品
        parseMap.put("ptype", 1);
        parseMap.put("start", 0);
        parseMap.put("end", 4);
        model.addAttribute(Constants.LOANINFO_Y, loanService.queryLoanType(parseMap));

        //        散装宝
        parseMap.put("ptype", 2);
        parseMap.put("start", 0);
        parseMap.put("end", 8);
        model.addAttribute(Constants.LOANINFO_S, loanService.queryLoanType(parseMap));

        return "index";
    }




}
