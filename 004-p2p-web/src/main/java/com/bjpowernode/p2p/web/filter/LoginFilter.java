package com.bjpowernode.p2p.web.filter;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p.web.filter
 * @Description: 登录过滤器 （拦截器），对于一些内容，如果用户不登陆，就无法进行访问
 * @Author: 王少伟
 * @CreateDate: 2020/12/29 11:43
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
@WebFilter(urlPatterns = {"/"})
public class LoginFilter implements Filter {


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        
    }
}
