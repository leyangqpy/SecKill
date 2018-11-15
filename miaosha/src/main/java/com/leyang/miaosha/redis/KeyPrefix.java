package com.leyang.miaosha.redis;

/**
 * Created by qianpyn on 2018/5/30.
 */
public interface KeyPrefix {

    public int expireSeconds();

    public String getPrefix();
}
