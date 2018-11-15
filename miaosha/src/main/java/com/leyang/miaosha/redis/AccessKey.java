package com.leyang.miaosha.redis;

/**
 * Created by qianpyn on 2018/5/30.
 */
public class AccessKey extends BasePrefix {

    private AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds,prefix);
    }

    public static AccessKey withExpire(int expireSeconds){
        return new AccessKey(expireSeconds,"access");
    }

}
