package com.leyang.miaosha.controller;

import com.leyang.miaosha.domain.MiaoshaUser;
import com.leyang.miaosha.domain.OrderInfo;
import com.leyang.miaosha.redis.RedisService;
import com.leyang.miaosha.result.CodeMsg;
import com.leyang.miaosha.result.Result;
import com.leyang.miaosha.service.GoodsService;
import com.leyang.miaosha.service.MiaoshaUserService;
import com.leyang.miaosha.service.OrderService;
import com.leyang.miaosha.vo.GoodsVo;
import com.leyang.miaosha.vo.OrderDetailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by qianpyn on 2018/5/24.
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    private Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private MiaoshaUserService miaoshaUserService;
    @Autowired
    private RedisService       redisService;
    @Autowired
    private OrderService       orderService;
    @Autowired
    private GoodsService       goodsService;


    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> toInfo(Model model, MiaoshaUser user,
                                        @RequestParam("orderId")long orderId) {
        if (user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo orderInfo = orderService.getOrderById(orderId);
        if(orderInfo == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = orderInfo.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setGoods(goods);
        vo.setOrder(orderInfo);
        return Result.success(vo);
    }


}
