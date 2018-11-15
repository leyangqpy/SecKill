package com.leyang.miaosha.vo;

import com.leyang.miaosha.domain.Goods;

import java.util.Date;

/**
 * Created by qianpyn on 2018/7/13.
 */
public class GoodsVo extends Goods {
    private Double miaoshaPrice;
    private Integer stockCount;
    private  Date    startDate;
    private  Date    endDate;

    public Double getMiaoshaPrice() {
        return miaoshaPrice;
    }

    public void setMiaoshaPrice(Double miaoshaPrice) {
        this.miaoshaPrice = miaoshaPrice;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
