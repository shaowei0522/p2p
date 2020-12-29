package com.bjpowernode.p2p.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.model.FinanceAccount;
import com.bjpowernode.p2p.model.RechargeRecord;
import com.bjpowernode.p2p.model.User;
import com.bjpowernode.p2p.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p.web
 * @Description: java类作用描述
 * @Author: 王少伟
 * @CreateDate: 2020/12/22 21:18
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
@Controller
public class FinanceAccountController {

    @Reference(interfaceClass = UserService.class,timeout = 20000,version = "1.0.0")
    private UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * 当鼠标悬浮在个人信息上时，查询账户所剩余额
     * @param request
     * @return
     */
    @GetMapping("/loan/page/queryFinanceAccount")
    @ResponseBody
    public Object queryFinanceAccount(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute(Constants.LOGIN_USER);
        Integer id = user.getId();
        FinanceAccount financeAccount = userService.queryFinanceAccount(id);

        return financeAccount;
    }

    /**
     * 此处之所以需要在重复查询一次账户余额是因为要保证账户余额的准确性，不能有丝毫误差
     * @param request
     * @param model
     * @return
     */
    @GetMapping("/loan/myCenter")
    public String myCenter(HttpServletRequest request, Model model){
        User user = (User) request.getSession().getAttribute(Constants.LOGIN_USER);
        FinanceAccount financeAccount = userService.queryFinanceAccount(user.getId());
        model.addAttribute("financeAccount", financeAccount);

        return "myCenter";
    }





}
