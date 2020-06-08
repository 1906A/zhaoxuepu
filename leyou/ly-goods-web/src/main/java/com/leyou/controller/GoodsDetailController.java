package com.leyou.controller;


import com.leyou.client.*;
import com.leyou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class GoodsDetailController {


    @Autowired
    SpuClient spuClient;

    @Autowired
    SkuClient skuClient;

    @Autowired
    SpecGroupClient  specGroupClient;

    @Autowired
    CategoryClient categoryClient;

    @Autowired
    SpecParamClient specParamClient;

    @Autowired
    BrandClient brandClient;

    @Autowired
    TemplateEngine templateEngine;

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

        //2：spudetail
        SpuDetail spuDetail = spuClient.findSpuDetailBySpuId(spuId);

        //7:查询品牌
        Brand brand = brandClient.findBrandById(spu.getBrandId());

        //3:sku
        List<Sku> skuList = skuClient.findSkusBySpuId(spuId);

        //4:查询规格参数组 及组内信息
        List<SpecGroup> groups = specGroupClient.findSpecGroupList(spu.getCid3());

        //5:三级分类   category   id   name
        List<Category> categoryList = categoryClient.findCategoryByCids(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        //6:参数中的规格参数
        List<SpecParam> specParamList = specParamClient.fingParamByCidAndGeneric(spu.getCid3(),false);

        //7: 规格参数的特殊属性
        Map<Long,String> paramMap = new HashMap<>();
        specParamList.forEach(param->{
            paramMap.put(param.getId(),param.getName());
        });

        model.addAttribute("spu",spu);
        model.addAttribute("spuDetail",spuDetail);
        model.addAttribute("categoryList",categoryList);
        model.addAttribute("brand",brand);
        model.addAttribute("skuList",skuList);
        model.addAttribute("groups",groups);
        model.addAttribute("paramMap",paramMap);


        //2：写入静态文件

        creatHtml(spu,spuDetail,categoryList,brand,skuList,groups,paramMap);

        return "item";
    }

    /**
     * 通过thyemleaf实现页面的静态化
     *
     * @param spu
     * @param spuDetail
     * @param categoryList
     * @param brand
     * @param skuList
     * @param groups
     * @param paramMap
     */
    private void creatHtml(Spu spu, SpuDetail spuDetail, List<Category> categoryList, Brand brand, List<Sku> skuList, List<SpecGroup> groups, Map<Long, String> paramMap) {

        PrintWriter writer = null;
        try {
            //1:创建上下文 thyemleaf
            Context context =new Context();

            //2:把数据放入到上下文中
            context.setVariable("spu",spu);
            context.setVariable("spuDetail",spuDetail);
            context.setVariable("categoryList",categoryList);
            context.setVariable("brand",brand);
            context.setVariable("skuList",skuList);
            context.setVariable("groups",groups);
            context.setVariable("paramMap",paramMap);

            //3:写入文件，写入流
            File file =new File("D:\\system\\nginx-1.16.1\\html\\"+spu.getId()+".html");
            writer = new PrintWriter(file);

            //4：执行静态化
            templateEngine.process("item",context,writer);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(writer!=null){
                //5:关闭写入流
                writer.close();
            }
        }
    }
}
