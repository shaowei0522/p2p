package com.bjpowernode.p2p.web.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.model.FinanceAccount;
import com.bjpowernode.p2p.model.User;
import com.bjpowernode.p2p.service.RedisServer;
import com.bjpowernode.p2p.service.UserService;
import com.bjpowernode.p2p.util.FileUtil;
import com.bjpowernode.p2p.util.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p.web
 * @Description: java类作用描述
 * @Author: 王少伟
 * @CreateDate: 2020/12/18 20:44
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
@Controller
public class UserController {

    @Reference(interfaceClass = UserService.class ,timeout = 20000, version = "1.0.0")
    UserService userService;
    @Reference(interfaceClass = RedisServer.class,timeout = 20000,version = "1.0.0")
    RedisServer redisServer;

    /**
     * 跳转至注册界面
     * @return
     */
    @GetMapping("/loan/page/register")
    public String register(){
        System.out.println("jump to register page!");
        return "register";
    }

    /**
     * 进入登录页面，登录成功之后需要重新跳转至之前浏览的页面，所以此处需要获取过来的redirectURL
     *
     * @return
     */
    @GetMapping("/loan/page/login")
    public String login(@RequestParam(required = false, name = "rediretURL") String redirectURL,
                        Model model) {

        System.out.println("--------redirectURL-------------");
        model.addAttribute("redirectURL", redirectURL);

        return "login";
    }


    @GetMapping("/loan/logout")
    public String logout(HttpServletRequest request){
//        request.getSession().invalidate();
        request.getSession().removeAttribute(Constants.LOGIN_USER);
        return "redirect:/index";

    }

    /**
     *  用户进行登录
     * @param phone
     * @param loginPassword
     * @param messageCode
     * @return
     */
    @GetMapping("/loan/page/loginSubmit")
    @ResponseBody
    public Object loginSubmit(String phone,String loginPassword,
                              String messageCode,HttpServletRequest request){

        String code = redisServer.pop(phone);
        if (!StringUtils.equals(code, messageCode)) {
            return Result.error("验证码错误，请重新输入");
        }
        User user = new User();
        user.setPhone(phone);
        user.setLoginPassword(loginPassword);
//        此处如果密码与账号不匹配，查到的user为null
        user = userService.loginSubmit(user);
        if (!ObjectUtils.allNotNull(user)) {
            return Result.error("对不起，用户名或者密码错误，请重新输入");
        }
        request.getSession().setAttribute(Constants.LOGIN_USER, user);
        return Result.success();
    }




    /**
     * 认证手机号码是否已经被注册，一个用户对应一个手机号码
     * @param phone
     * @return
     */
    @GetMapping("/loan/page/verifyPhone")
    @ResponseBody
    public Map verifyPhone(String phone){
        User user= userService.verifyPhone(phone);
        System.out.println(user);

        if (user == null) {
            return Result.success();
        } else {
            return Result.error();
        }
    }

    /**
     * 用户提交表单进行注册个人信息
     * @param user
     * @param verificationCode
     * @return
     */
    @GetMapping("/loan/page/registerSubmit")
    @ResponseBody
    public Map registerSubmit(User user,String verificationCode,HttpServletRequest request){
//        对用户的密码强度进行校验
        String pwdReg = "^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*";
        if (!user.getLoginPassword().matches(pwdReg)) {
            return Result.error("对不起，您的密码格式不正确，请重新设置密码");
        }
//        验证手机验证码是否正确
        String code = redisServer.pop(user.getPhone());
//        此处引入了lang3包
        if (!StringUtils.equals(code, verificationCode)) {
            System.out.println("请输入正确的验证码");
            return Result.error("请输入正确的验证码");
        }

//        如果都是正确的，就进行用户的注册，用户注册包含了赠送888的大礼包，处于同一个事务级别
        user = userService.register(user);
        if (user != null) {
            request.getSession().setAttribute(Constants.LOGIN_USER,user);
            return Result.success("恭喜您注册成功！");
        }
        return Result.error("注册失败");

    }


    /**
     * 用户通过浏览器请求验证码，验证码在此处生成，
     * 然后模拟一个客户端请求短信接口，来让用户接收到短信
     * @param phone
     * @return
     * @throws Exception
     */
    @GetMapping("/loan/page/getVerificationCode")
    @ResponseBody
    public Object getVerificationCode(String phone) throws Exception {
//        此处有服务器自己生成一个验证码
        Long verificationCode = Math.round(Math.random() * 10000) + 100000;

//        模拟一个客户端向服务器发送请求
        System.out.println("==========正在获取验证码");
        String url = "https://way.jd.com/kaixintong/kaixintong";

//        此处是请求接口所需要的质询参数
        Map map = new HashMap();
        map.put("mobile", phone);
        String content = "【凯信通】您的验证码是：" + verificationCode;
        System.out.println("content=" + content);
        map.put("content", content);
        map.put("appkey", "test");

//        String result = HttpClientUtils.doPost(url, map);

//      模拟报文
        String result ="{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 0,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": \"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\" ?><returnsms>\\n <returnstatus>Success</returnstatus>\\n <message>ok</message>\\n <remainpoint>-1111611</remainpoint>\\n <taskID>101609164</taskID>\\n <successCounts>1</successCounts></returnsms>\"\n" +
                "}";
//        此处获取到的是一个json字符串，所以需要使用fastjson进行数据解析
        JSONObject jsonObject = JSONObject.parseObject(result);
        String code = jsonObject.getString("code");

//        如果此处获取到的code为10000，则表示通信失败
        if (!StringUtils.equals("10000", code)) {
            return Result.error("通信失败");

        }

        String xml = jsonObject.getString("result");
//        对获取到的数据进行解析，需要用到dom4j,对xml进行解析

        Document document = DocumentHelper.parseText(xml);
        List<Node> nodes = document.selectNodes("//returnstatus/text()");

        String success = nodes.get(0).getText();
        System.out.println(success);
        if (StringUtils.equals("Success", success)) {
//            此处表示验证码发送成功
//        将解析到的验证码存放在redis服务器中，设置生命周期，即验证码的有效期
            redisServer.push(phone, String.valueOf(verificationCode));
            return Result.success(String.valueOf(verificationCode));
        }
        return Result.error();
    }

    @GetMapping("/loan/page/realNameVerify")
    public String toRealNameVerify(){
        System.out.println("进入实名认证页面");
        return "realName";
    }


    /**
     * 进行实名认证
     * @param phone
     * @param realName
     * @param idCard
     * @param messageCode
     * @param request
     * @return
     */
    @GetMapping("/loan/page/realNameVerifySubmit")
    @ResponseBody
    public Object realNameVerifySubmit(String phone,String realName,
                                       String idCard,String messageCode,
                                       HttpServletRequest request){
        String code = redisServer.pop(phone);
        if (!StringUtils.equals(code, messageCode)) {
            return Result.error("验证码输入有误");
        }
//        此处需要模拟客户端请求实名认证接口
        /*String url = "https://way.jd.com/hl/idcardcheck";
        Map paramMap = new HashMap();
        paramMap.put("cardNo", idCard);
        paramMap.put("realName", realName);
        paramMap.put("appkey", "test");

        try {
            String result = HttpClientUtils.doPost(url, paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        String result = "{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 1305,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": {\n" +
                "        \"error_code\": 0,\n" +
                "        \"reason\": \"Success\",\n" +
                "        \"result\": {\n" +
                "            \"realname\": \"张*\",\n" +
                "            \"idcard\": \"3303***********\",\n" +
                "            \"isok\": true,\n" +
                "            \"IdCardInfor\": {\n" +
                "                \"area\": \"浙江省杭州市区清徐县\",\n" +
                "                \"sex\": \"男\",\n" +
                "                \"birthday\": \"1965-3-10\"\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";

//        对返回的结果进行解析
        JSONObject jsonObject = JSONObject.parseObject(result);
        String code1 = (String) jsonObject.get("code");
        if (!StringUtils.equals(code1, "10000")) {
            return Result.error("认证通信失败，请稍后重试");
        }
        Boolean isok = jsonObject.getJSONObject("result").getJSONObject("result").getBoolean("isok");

        if (!isok) {
            return Result.error("信息有误，实名认证失败");
        }

        User user = (User) request.getSession().getAttribute(Constants.LOGIN_USER);
        user.setIdCard(idCard);
        user.setName(realName);
        int i = userService.modifyUserByRealNameVerify(user);
        if (i > 0) {
            return Result.success("实名认证成功");
        }

        return Result.error("实名认证失败");
    }


    /**
     * 用户上传头像
     * @return
     */
    @RequestMapping("/loan/uploadHeader")
    public String uploadHeader(MultipartFile header, HttpServletRequest request){


        String contentType = header.getContentType();
        String fileName = header.getOriginalFilename();
        /*System.out.println("fileName-->" + fileName);
        System.out.println("getContentType-->" + contentType);*/

/*        String path = "";
        try {
            path = ResourceUtils.getURL("classpath:").getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/


        String filePath = request.getSession().getServletContext().getRealPath("imgupload/");
        try {
            FileUtil.uploadFile(header.getBytes(), filePath, fileName);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        //返回json
        return "myCenter";



    }



}
