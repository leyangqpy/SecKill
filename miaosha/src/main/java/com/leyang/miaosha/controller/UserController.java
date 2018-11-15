package com.leyang.miaosha.controller;

import com.leyang.miaosha.domain.MiaoshaUser;
import com.leyang.miaosha.redis.RedisService;
import com.leyang.miaosha.result.Result;
import com.leyang.miaosha.service.GoodsService;
import com.leyang.miaosha.service.MiaoshaUserService;
import com.leyang.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by qianpyn on 2018/5/24.
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private MiaoshaUserService miaoshaUserService;
    @Autowired
    private RedisService       redisService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> toInfo(Model model, MiaoshaUser user) {
        return Result.success(user);
    }


}
