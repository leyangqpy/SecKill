package com.leyang.miaosha.redis;

/**
 * Created by qianpyn on 2018/7/19.
 */
public class OrderKey extends BasePrefix{
    public OrderKey(String prefix) {
        super( prefix);
    }

    public static OrderKey getMiaoshaOrderByUidGid  = new OrderKey("moug");
}
