package com.leyang.miaosha.controller;

import com.leyang.miaosha.redis.RedisService;
import com.leyang.miaosha.result.Result;
import com.leyang.miaosha.service.MiaoshaUserService;
import com.leyang.miaosha.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Created by qianpyn on 2018/5/24.
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    private Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private MiaoshaUserService miaoshaUserService;
    @Autowired
    private RedisService       redisService;


    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
        log.info(loginVo.toString());

       String token =  miaoshaUserService.login(response,loginVo);

        return Result.success(token);
    }


}
