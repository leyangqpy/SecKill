package com.leyang.miaosha.util;

import java.util.UUID;

/**
 * Created by qianpyn on 2018/6/13.
 */
public class UUIDUtil {
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
