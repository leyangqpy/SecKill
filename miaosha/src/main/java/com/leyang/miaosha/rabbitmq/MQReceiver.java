package com.leyang.miaosha.rabbitmq;

import com.leyang.miaosha.domain.MiaoshaOrder;
import com.leyang.miaosha.domain.MiaoshaUser;
import com.leyang.miaosha.domain.OrderInfo;
import com.leyang.miaosha.redis.RedisService;
import com.leyang.miaosha.result.CodeMsg;
import com.leyang.miaosha.result.Result;
import com.leyang.miaosha.service.GoodsService;
import com.leyang.miaosha.service.MiaoshaService;
import com.leyang.miaosha.service.OrderService;
import com.leyang.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by qianpyn on 2018/8/28.
 */
@Service
public class MQReceiver {

    private static Logger logger = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    private RedisService       redisService;
    @Autowired
    GoodsService   goodsService;
    @Autowired
    OrderService   orderService;
    @Autowired
    MiaoshaService miaoshaService;

    @RabbitListener(queues=MQConfig.MIAOSHA_QUEUE)
    public void receive(String message) {
        logger.info("receive message:"+message);
        MiaoshaMessage mm  = RedisService.stringToBean(message, MiaoshaMessage.class);
        MiaoshaUser user = mm.getUser();
        long goodsId = mm.getGoodsId();

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if(stock <= 0) {
            return;
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return;
        }
        //减库存 下订单 写入秒杀订单
        miaoshaService.miaosha(user, goods);
    }

//    @RabbitListener(queues = MQConfig.QUEUE)
//    public void receive(String message){
//        logger.info("receive message: "+message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
//    public void receiveTopic1(String message){
//        logger.info("topic queue1 message: "+message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
//    public void receiveTopic2(String message){
//        logger.info("topic queue2 message: "+message);
//    }
//
//    @RabbitListener(queues = MQConfig.HEADER_QUEUE)
//    public void receiveHeaderQueue(byte[] message){
//        logger.info("header queue message: "+new String(message));
//    }
}
