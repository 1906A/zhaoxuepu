package com.leyou.controller;

import com.leyou.common.JwtUtils;
import com.leyou.common.UserInfo;
import com.leyou.config.JwtProperties;
import com.leyou.pojo.SkuVo;
import com.leyou.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class CartController {

    /**
     *  操作购物车数据放到redis  hash类型
     *
     *  1：加入购物车
     *  2：修改购物车
     *  3：删除购物车
     *  4：查询购物车
     *
     */
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    JwtProperties jwtProperties;

    public String prifix ="ly_carts_";
    public String prifixSelectedSku ="ly_carts_selectedSku";

    /**
     * 增加购物车
     *
     * @param token
     * @param skuVo
     */
    @RequestMapping("add")
    public void add(@CookieValue("token")String token, @RequestBody SkuVo skuVo){

        UserInfo userInfo = this.getUserInfoByToken(token);

        if(userInfo!=null){
            //添加购物车
            BoundHashOperations<String, Object, Object> hashOps = stringRedisTemplate
                    .boundHashOps(prifix + userInfo.getId());

            //判断redis中信息
            if(hashOps.hasKey(skuVo.getId()+"")){
                //从redis中获取已存在的购物车信息
                SkuVo redisSkuVo = JsonUtils.parse(hashOps.get(skuVo.getId()+"").toString(), SkuVo.class);
                //修改购物车信息中的数量
                redisSkuVo.setNum(redisSkuVo.getNum()+skuVo.getNum());

                //重新存放redis
                hashOps.put(skuVo.getId()+"",JsonUtils.serialize(redisSkuVo));

                //当前操作的sku单独存放redis
                stringRedisTemplate.boundValueOps(prifixSelectedSku+userInfo.getId()).set(JsonUtils.serialize(redisSkuVo));

            }else{
                //第一次往redis存放商品信息
                hashOps.put(skuVo.getId()+"",JsonUtils.serialize(skuVo));

                //当前操作的sku单独存放redis
                stringRedisTemplate.boundValueOps(prifixSelectedSku+userInfo.getId()).set(JsonUtils.serialize(skuVo));
            }
        }

    }

    /**
     * 去redis获取最新操作的sku信息
     * @param token
     * @return
     */
    @RequestMapping("selectedSku")
    public SkuVo selectedSku(@CookieValue("token")String token){
        UserInfo userInfo = this.getUserInfoByToken(token);
        //从redis获取最新的sku信息   json
        String s = stringRedisTemplate.boundValueOps(prifixSelectedSku + userInfo.getId()).get();

        SkuVo skuVo = JsonUtils.parse(s, SkuVo.class);
        return skuVo;
    }

    /**
     * 修改购物车信息
     *
     * @param token
     * @param skuVo
     */
    @RequestMapping("update")
    public void update(@CookieValue("token")String token,@RequestBody  SkuVo skuVo){

        UserInfo userInfo = this.getUserInfoByToken(token);

        if(userInfo!=null){
            //添加购物车
            BoundHashOperations<String, Object, Object> hashOps = stringRedisTemplate
                    .boundHashOps(prifix + userInfo.getId());

            //判断redis中信息
            if(hashOps.hasKey(skuVo.getId()+"")){
                //从redis中获取已存在的购物车信息
                SkuVo redisSkuVo = JsonUtils.parse(hashOps.get(skuVo.getId()+"").toString(), SkuVo.class);
                //修改购物车信息中的数量
                redisSkuVo.setNum(skuVo.getNum());

                //重新存放redis
                hashOps.put(skuVo.getId()+"",JsonUtils.serialize(redisSkuVo));

            }else{
                //第一次往redis存放商品信息
                hashOps.put(skuVo.getId()+"",JsonUtils.serialize(skuVo));
            }
        }
    }

    /**
     * 删除购物车
     *
     * @param token
     * @param id
     */
    @RequestMapping("delete")
    public void delete(@CookieValue("token")String token,@RequestParam("id") Long id){
        UserInfo userInfo = this.getUserInfoByToken(token);

        if(userInfo!=null){
            //添加购物车
            BoundHashOperations<String, Object, Object> hashOps = stringRedisTemplate
                    .boundHashOps(prifix + userInfo.getId());
            hashOps.delete(id+"");
        }
    }

    /**
     * 查询购物车
     *
     * @param token
     * @return
     */
    @RequestMapping("query")
    public List<SkuVo> query(@CookieValue("token")String token){

        UserInfo userInfo = this.getUserInfoByToken(token);
        List<SkuVo> list = new ArrayList<>();
        if(userInfo!=null){
            //添加购物车
            BoundHashOperations<String, Object, Object> hashOps = stringRedisTemplate
                    .boundHashOps(prifix + userInfo.getId());
            Map<Object, Object> map = hashOps.entries();

            map.keySet().forEach(key ->{
                SkuVo skuvo = JsonUtils.parse(hashOps.get(key).toString(),SkuVo.class);
                list.add(skuvo);
            });
        }

        return list;
    }


    /**
     * 登陆后根据token解析用户信息
     *
     * @param token
     * @return
     */
    public UserInfo getUserInfoByToken(String token){
        UserInfo userInfo =new UserInfo();
        try {
            userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userInfo;
    }


}
