package com.bjpowernode.p2p.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.model.Bid;
import com.bjpowernode.p2p.model.Loan;
import com.bjpowernode.p2p.service.BidService;
import com.bjpowernode.p2p.service.LoanService;
import com.bjpowernode.p2p.util.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p.web
 * @Description: java类作用描述
 * @Author: 王少伟
 * @CreateDate: 2020/12/16 09:14
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
@Controller
public class LoanController {

    @Reference(interfaceClass = LoanService.class,timeout = 20000,version = "1.0.0")
    LoanService loanService;

    @Reference(interfaceClass = BidService.class,timeout = 20000,version = "1.0.0")
    BidService bidService;


//   进入借款界面
    @GetMapping({"/loan/loan"})
    public String loan(@RequestParam(required = false,defaultValue = "2") Integer page,
                       String ptype,Model model,
                       HttpServletRequest request){

        model.addAttribute("ptype", ptype);
//        从session中读取pageInfo
        PageInfo pageInfo = (PageInfo) request.getSession().getAttribute("pageInfo");
        if (pageInfo == null) {
//            代表此时是第一次查询
            pageInfo = new PageInfo();
            request.getSession().setAttribute("pageInfo", pageInfo);
        }

//        查询页数信息(此处查询出来的是总页数信息)
        int loanCount = loanService.queryLoanCount(ptype);
        pageInfo.setPageContent(loanCount);
        if (page == null) {
            page = 2;
        } else if (page < 0) {
            page = 2;
        } else if (page > pageInfo.getPages()) {
            page = pageInfo.getPages();
        }
//        当前的页码
        pageInfo.setCurrentPage(page);
//        此处只需要传入pageInfo的信息以及ptype即可
        List<Loan> loans = loanService.queryLoanType(pageInfo,ptype);
        model.addAttribute(Constants.LOANINFO_S, loans);

//        todo:投资排行榜
        return "loan";
    }



    @GetMapping("/loan/loanInfo")
    public String loanInfo(String id,Model model){
//    根据loan的id查询loanInfo的详细信息
        Loan loan = loanService.queryLoanById(id);
        model.addAttribute("loanInfo", loan);
//    根据loanId查询相关的投资记录，同时要关联查询，查询用户的电话号码信息
        List<Bid> bidList = bidService.queryBidByLoanInfo(id);
        model.addAttribute("bidList", bidList);

        return "loanInfo";
    }
}
