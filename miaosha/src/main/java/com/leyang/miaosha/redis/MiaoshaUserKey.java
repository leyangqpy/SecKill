package com.leyang.miaosha.redis;

/**
 * Created by qianpyn on 2018/6/13.
 */
public class MiaoshaUserKey extends BasePrefix {
    public static final int TOKEN_EXPIRE = 3600 * 24* 2;

    private MiaoshaUserKey(int expireSeconds , String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPIRE, "tk");
    public static MiaoshaUserKey getById = new MiaoshaUserKey(0, "id");

}
