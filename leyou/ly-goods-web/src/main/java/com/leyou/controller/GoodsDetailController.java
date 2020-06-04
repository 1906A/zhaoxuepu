package com.leyou.controller;


import com.leyou.client.SkuClient;
import com.leyou.client.SpecGroupClient;
import com.leyou.client.SpuClient;
import com.leyou.pojo.Sku;
import com.leyou.pojo.SpecGroup;
import com.leyou.pojo.Spu;
import com.leyou.pojo.SpuDetail;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class GoodsDetailController {


    @Autowired
    SpuClient spuClient;

    @Autowired
    SkuClient skuClient;

    @Autowired
    SpecGroupClient  specGroupClient;

    @RequestMapping("hello")
    public String hello(Model model){
        String name ="张三";

        model.addAttribute("name",name);
        return "hello";
    }

    /**
     * 请求商品详情的微服务
     * 1：spu
     * 2：spudetail
     * 3：sku
     * 4：规格参数组
     * 5：规格参数详情
     * 6：三级分类
     *
     * @param spuId
     * @param model
     * @return
     */
    @RequestMapping("item/{spuId}.html")
    public String item(@PathVariable("spuId") Long spuId,Model model){

        //1:spu
        Spu spu = spuClient.findSpuBuId(spuId);
        model.addAttribute("spu",spu);

        //2：spudetail
        SpuDetail spuDetail = spuClient.findSpuDetailBySpuId(spuId);
        model.addAttribute("spuDetail",spuDetail);

        //3:sku
        List<Sku> skuList = skuClient.findSkusBySpuId(spuId);
        model.addAttribute("skuList",skuList);

        //4:查询规格参数组
        List<SpecGroup> specGroupList = specGroupClient.findSpecGroupList(spu.getCid3());
        model.addAttribute("specGroupList",specGroupList);

        //5:规格参数组  cid  + generic =0    List

        //6:三级分类   category   id   name




        return "item";
    }
}
