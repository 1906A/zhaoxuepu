package com.leyou;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void test(){

        //hash
        BoundHashOperations<String, Object, Object> hashOps = stringRedisTemplate.boundHashOps("ly_carts");

        hashOps.put("skuid_121","{\"title\"：\"小米手机1\"}");
        hashOps.put("skuid_122","{\"title\"：\"小米手机2\"}");
        hashOps.put("skuid_123","{\"title\"：\"小米手机3\"}");


        Map<Object, Object> map = hashOps.entries();
        map.keySet().forEach(key ->{
            System.out.println(key+"==="+map.get(key));
        });


        System.out.println(hashOps.get("skuid_123"));





    }
}
