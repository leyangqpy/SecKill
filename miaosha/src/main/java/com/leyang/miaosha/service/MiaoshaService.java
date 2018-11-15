package com.leyang.miaosha.service;

import com.leyang.miaosha.domain.MiaoshaOrder;
import com.leyang.miaosha.domain.MiaoshaUser;
import com.leyang.miaosha.domain.OrderInfo;
import com.leyang.miaosha.redis.MiaoshaKey;
import com.leyang.miaosha.redis.RedisService;
import com.leyang.miaosha.util.MD5Util;
import com.leyang.miaosha.util.UUIDUtil;
import com.leyang.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Created by qianpyn on 2018/7/16.
 */
@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    RedisService redisService;

    private static char[] ops = new char[]{'+','-','*'};

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {

        //减库存 下订单 写入秒杀订单
        boolean success = goodsService.reduceStock(goods);
        if(success) {
            //order_info maiosha_order
            return orderService.createOrder(user, goods);
        }else {
            setGoodsOver(goods.getId());
            return null;
        }
    }

    public long getMiaoshaResult(Long userId, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        if(order != null) {//秒杀成功
            return order.getOrderId();
        }else {
            boolean isOver = getGoodsOver(goodsId);
            if(isOver) {
                return -1;
            }else {
                return 0;
            }
        }
    }

    private boolean getGoodsOver(long goodsId) {
        return  redisService.exists(MiaoshaKey.isGoodsOver,""+goodsId);
    }

    private void setGoodsOver(long goodsId){
        redisService.set(MiaoshaKey.isGoodsOver,""+goodsId,true);
    }

    public String creatMiaoshaPath(MiaoshaUser user, long goodsId) {
        if (user == null || goodsId <= 0){
            return null;
        }
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(MiaoshaKey.getMiaoshaPath,user.getId()+"_" +goodsId,str);
        return str;
    }

    public boolean checkPath(MiaoshaUser user, long goodsId, String path) {
        if (user == null || path== null){
            return false;
        }
        String oldPath = redisService.get(MiaoshaKey.getMiaoshaPath, user.getId() + "_" + goodsId, String.class);
        return path.equals(oldPath);
    }

    public BufferedImage createVerifyCode(MiaoshaUser user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, rnd);
        //输出图片
        return image;
    }

    private int calc(String verifyCode) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return  (Integer)engine.eval(verifyCode);
        }catch (Exception e){
            e.printStackTrace();
            return  0;
        }
    }

    /**
     *   加减乘
     * @param rdm
     * @return
     */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+num1 + op1 + num2 +op2 +num3;
        return exp;
    }

    public boolean checkVerifyCode(MiaoshaUser user, long goodsId, int verifyCode) {
        if(user == null || goodsId <=0) {
            return false;
        }
        Integer oldCode = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId, Integer.class);
        if (oldCode ==null || oldCode - verifyCode !=0){
            return false;
        }
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId);
        return true;
    }
}

