package com.leyang.miaosha.controller;

import com.leyang.miaosha.domain.MiaoshaUser;
import com.leyang.miaosha.redis.GoodsKey;
import com.leyang.miaosha.redis.RedisService;
import com.leyang.miaosha.result.Result;
import com.leyang.miaosha.service.GoodsService;
import com.leyang.miaosha.service.MiaoshaUserService;
import com.leyang.miaosha.vo.GoodsDetailVo;
import com.leyang.miaosha.vo.GoodsVo;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by qianpyn on 2018/5/24.
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    private Logger log = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    private MiaoshaUserService miaoshaUserService;
    @Autowired
    private RedisService       redisService;
    @Autowired
    GoodsService          goodsService;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;
    @Autowired
    ApplicationContext applicationContext;

    /**
     * QPS: 360（页面缓存后）
     * @param request
     * @param response
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value = "/to_list",produces = "text/html")
    @ResponseBody
    public String toLogin(HttpServletRequest request, HttpServletResponse response,
                          Model model , MiaoshaUser user) {
        model.addAttribute("user",user);
        //取缓存
        String html = redisService.get(GoodsKey.getGoodsList,"",String.class);
        if (!StringUtils.isEmpty(html)){
            return html;
        }
        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList",goodsList);


        //手动渲染
        WebContext context = new WebContext(request,response,request.getServletContext(),
                request.getLocale(),model.asMap());

        html = thymeleafViewResolver.getTemplateEngine().process("goods_list",context);
        if (!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsList,"",html);
        }
        return html;
    }


    @RequestMapping(value = "/to_detail2/{goodsId}",produces = "text/html")
    @ResponseBody
    public String toDetail2(HttpServletRequest request, HttpServletResponse response,
                           Model model , MiaoshaUser user,
                           @PathVariable("goodsId")long goodsId) {
        model.addAttribute("user",user);
        //取缓存
        String html = redisService.get(GoodsKey.getGoodsDetail,""+goodsId,String.class);
        if (!StringUtils.isEmpty(html)){
            return html;
        }
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goods);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if (now < startAt){//秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int)(startAt - now)/1000;
        }else if(now > endAt){ //秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else { //秒杀正在进行
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus",miaoshaStatus);
        model.addAttribute("remainSeconds",remainSeconds);

        //手动渲染
        WebContext context = new WebContext(request,response,request.getServletContext(),
                request.getLocale(),model.asMap());

        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail",context);
        if (!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsDetail,""+goodsId,html);
        }
        return html;
    }


    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response,
                                          Model model , MiaoshaUser user,
                                          @PathVariable("goodsId")long goodsId) {
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if (now < startAt){//秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int)(startAt - now)/1000;
        }else if(now > endAt){ //秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else { //秒杀正在进行
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setMiaoshaStatus(miaoshaStatus);
        vo.setRemainSeconds(remainSeconds);
        vo.setUser(user);
        return Result.success(vo);
    }
}
