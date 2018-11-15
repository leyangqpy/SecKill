package com.leyang.miaosha.controller;

import com.leyang.miaosha.domain.User;
import com.leyang.miaosha.rabbitmq.MQSender;
import com.leyang.miaosha.redis.RedisService;
import com.leyang.miaosha.redis.UserKey;
import com.leyang.miaosha.result.CodeMsg;
import com.leyang.miaosha.result.Result;
import com.leyang.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by qianpyn on 2018/5/24.
 */
@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    private UserService  userService;
    @Autowired
    private RedisService redisService;
    @Autowired
    MQSender mqSender;


    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }

//    @RequestMapping("/mq")
//    @ResponseBody
//    public Result<String> mq(){
//        mqSender.send("Hello,nihao");
//        return Result.success("hello,leyang");
//    }
//
//    @RequestMapping("/mq/topic")
//    @ResponseBody
//    public Result<String> mqTopic(){
//        mqSender.sendTopic("Hello,goudan");
//        return Result.success("hello,leyang");
//    }
//
//    @RequestMapping("/mq/fanout")
//    @ResponseBody
//    public Result<String> mqFanout(){
//        mqSender.sendFanout("Hello,goudan");
//        return Result.success("hello,leyang");
//    }
//
//    @RequestMapping("/mq/header")
//    @ResponseBody
//    public Result<String> mqHeader(){
//        mqSender.sendHeader("Hello,gouda");
//        return Result.success("hello,leyang");
//    }

    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> hello(){
        return Result.success("hello,leyang");
    }

    @RequestMapping("/helloError")
    @ResponseBody
    public Result<String> helloError(){
        return Result.error(CodeMsg.SERVER_ERROR);
    }


    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name","leyang");
        return "hello";
    }
    @RequestMapping("/db/get")
    @ResponseBody
    public  Result<User> dbGet(){
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public  Result<Boolean> dbTx(){
        boolean b = userService.tx();
        return Result.success(b);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public  Result<User> redisGet(){
        User user = redisService.get(UserKey.getById,""+1,User.class);
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public  Result<Boolean> redisSet(){
        User user = new User();
        user.setId(1);
        user.setName("1234");
        boolean b = redisService.set(UserKey.getById,""+1,user);
        return Result.success(true);
    }
}
