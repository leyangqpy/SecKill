package com.leyang.miaosha.controller;

import com.leyang.miaosha.access.AccessLimit;
import com.leyang.miaosha.domain.MiaoshaOrder;
import com.leyang.miaosha.domain.MiaoshaUser;
import com.leyang.miaosha.domain.OrderInfo;
import com.leyang.miaosha.rabbitmq.MQSender;
import com.leyang.miaosha.rabbitmq.MiaoshaMessage;
import com.leyang.miaosha.redis.AccessKey;
import com.leyang.miaosha.redis.GoodsKey;
import com.leyang.miaosha.redis.MiaoshaKey;
import com.leyang.miaosha.redis.RedisService;
import com.leyang.miaosha.result.CodeMsg;
import com.leyang.miaosha.result.Result;
import com.leyang.miaosha.service.GoodsService;
import com.leyang.miaosha.service.MiaoshaService;
import com.leyang.miaosha.service.MiaoshaUserService;
import com.leyang.miaosha.service.OrderService;
import com.leyang.miaosha.util.MD5Util;
import com.leyang.miaosha.util.UUIDUtil;
import com.leyang.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qianpyn on 2018/5/24.
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean{

    private Logger log = LoggerFactory.getLogger(MiaoshaController.class);

    @Autowired
    private MiaoshaUserService miaoshaUserService;
    @Autowired
    private RedisService       redisService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaService miaoshaService;
    @Autowired
    MQSender sender;

    private Map<Long,Boolean> localOverMap = new HashMap<>();

    /**
     * 系统初始化
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if (goodsList == null){
            return ;
        }
        for(GoodsVo goods : goodsList){
            redisService.set(GoodsKey.MiaoshaGoodsStock,""+goods.getId(),goods.getStockCount());
            localOverMap.put(goods.getId(),false);
        }
    }

    /**
     * QPS : 230
     * QPS : 297
     * QPS : 300
     * 5000 * 10
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/{path}/do_miaosha",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model , MiaoshaUser user,@PathVariable("path") String path,
                                   @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user",user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //验证path
        boolean check = miaoshaService.checkPath(user,goodsId,path);
        if (!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if(over) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //预减库存
        long stock = redisService.decr(GoodsKey.MiaoshaGoodsStock, ""+goodsId);//10
        if(stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //入队
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        sender.sendMiaoshaMessage(mm);
        return Result.success(0);//排队中

//    //判断库存
//        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
//        int stock = goods.getStockCount();
//        if (stock <= 0){
//            return Result.error(CodeMsg.MIAO_SHA_OVER);
//        }
//
//        //判断是否已经秒杀到了
//        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
//        if (order != null){
//            return Result.error(CodeMsg.REPEATE_MIAOSHA);
//        }
//
//        //减库存 下订单 写入秒杀订单
//        OrderInfo orderInfo = miaoshaService.miaosha(user,goods);

    }


    /**
     * orderId: 成功
     * 1：秒杀失败
     * 0：排队中
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model , MiaoshaUser user, @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result  =miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }

    @AccessLimit(seconds = 5,maxCount = 5,needLogin = true)
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(HttpServletRequest request , MiaoshaUser user,
                                         @RequestParam(value = "verifyCode")int verifyCode, @RequestParam("goodsId")long goodsId) {
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        boolean check = miaoshaService.checkVerifyCode(user,goodsId,verifyCode);
        if (!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        String path = miaoshaService.creatMiaoshaPath(user,goodsId);

        return Result.success(path);
    }

    @RequestMapping(value = "/verifyCode",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCode(HttpServletResponse response , MiaoshaUser user, @RequestParam("goodsId")long goodsId) {

        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        BufferedImage img = miaoshaService.createVerifyCode(user,goodsId);
        try {
            OutputStream out = response.getOutputStream();
            ImageIO.write(img,"JPEG",out);
            out.flush();
            out.close();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }
}
