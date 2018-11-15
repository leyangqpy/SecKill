package com.leyang.miaosha.access;

import com.leyang.miaosha.domain.MiaoshaUser;

/**
 * Created by qianpyn on 2018/8/31.
 */
public class UserContext {
    private static ThreadLocal<MiaoshaUser> userHolder = new InheritableThreadLocal<>();

    public static void setUser(MiaoshaUser user){
        userHolder.set(user);
    }

    public static MiaoshaUser getUser(){
       return userHolder.get();
    }
}
