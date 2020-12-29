package com.bjpowernode.p2p.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p.util
 * @Description: java类作用描述
 * @Author: 王少伟
 * @CreateDate: 2020/12/18 21:24
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
public class Result {

    public static final String LEFT_MONEY_ERROR = "500";
    public static final String UPDATE_LOAN_STATE_ERROR = "501";
    public static final String AVA_MONEY_ERR = "405";
    public static final String BID_INSERT_ERR = "502";
    public static final String OK = "200";

    public static Map success(){
        Map map = new HashMap();
        map.put("code", 1);
        map.put("msg", "");
        map.put("success", true);
        return map;
    }
    public static Map success(String msg){
        Map map = new HashMap();
        map.put("code", 1);
        map.put("msg", msg);
        map.put("success", true);
        return map;
    }

    public static Map error(){
        Map map = new HashMap();
        map.put("code", 0);
        map.put("msg", "");
        map.put("success", false);
        return map;
    }

    public static Map error(String msg){
        Map map = new HashMap();
        map.put("code", 0);
        map.put("msg", msg);
        map.put("success", false);
        return map;
    }

}
