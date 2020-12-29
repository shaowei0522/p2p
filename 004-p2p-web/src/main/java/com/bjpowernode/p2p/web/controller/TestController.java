package com.bjpowernode.p2p.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p.web
 * @Description: java类作用描述
 * @Author: 王少伟
 * @CreateDate: 2020/12/26 18:52
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
@Controller
public class TestController {

    @RequestMapping("/pathTest")
    public String pathTest(HttpServletRequest request){
        String contextPath = request.getContextPath();
        System.out.println(contextPath);
        return "index";
    }
}
