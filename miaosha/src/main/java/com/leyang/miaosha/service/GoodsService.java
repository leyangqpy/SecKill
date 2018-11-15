package com.leyang.miaosha.service;

import com.leyang.miaosha.dao.GoodsDao;
import com.leyang.miaosha.domain.Goods;
import com.leyang.miaosha.domain.MiaoshaGoods;
import com.leyang.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by qianpyn on 2018/7/13.
 */
@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId (long goodsId){
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public boolean reduceStock(GoodsVo goods) {
        MiaoshaGoods good = new MiaoshaGoods();
        good.setGoodsId(goods.getId());
        int ret = goodsDao.reduceStock(good);
        return ret > 0;
    }
}
