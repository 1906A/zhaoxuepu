package com.leyou.controller;

import com.leyou.pojo.User;
import com.leyou.service.UserService;
import com.leyou.utils.CodeUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    AmqpTemplate amqpTemplate;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 实现用户数据的校验，主要包括对：手机号、用户名的唯一性校验
     * @param data
     * @param type
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public Boolean check(@PathVariable("data") String data, @PathVariable("type")Integer type){

          System.out.println("校验："+data+"=="+type);
          return userService.check(data,type);
    }

    /**
     *
     * 根据用户输入的手机号，生成随机验证码
     *
     * @param phone
     */
    @PostMapping("/code")
    public void code(@RequestParam("phone") String phone){
        System.out.println("code："+phone);

        //1：生成一个6位数字的随机码  code
        String code = CodeUtils.messageCode(6);

        //2:使用rabbitmq发送短信 phone、code
        Map<String,String> map =new HashMap<>();
        map.put("phone",phone);
        map.put("code",code);
        //amqpTemplate.convertAndSend("sms.changes","sms.send",map);

        //3：发送短信后存放redis 放验证码 code
        stringRedisTemplate.opsForValue().set("lysms_"+phone, code,55, TimeUnit.MINUTES); //放入redis中的code有效期为5分钟

    }


    /**
     *
     * 用户注册
     *
     * @param user
     * @param code
     */
    @PostMapping("/register")
    public void register(@Valid  User user, String code){
        System.out.println("用户注册："+user.getUsername()+"code="+code);

        if(user!=null){

            //从redis中获取code
            String redisCode = stringRedisTemplate.opsForValue().get("lysms_" + user.getPhone());
            //1:判断code验证码是否一致
            if(redisCode.equals(code)){
                userService.insertUser(user);
            }
        }
    }


    /**
     * 根据用户名和密码查询用户
     *
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/query")
    public User query(@RequestParam("username")String username,@RequestParam("password") String password){
        System.out.println("查询用户：username="+username+"password="+password);
        return new User();
    }


    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @return
     */
    @PostMapping("login")
    public Boolean login(@RequestParam("username")String username,@RequestParam("password") String password){
        Boolean result = false;
        //1：根据用户名查询用户信息
        User user  = userService.findUser(username);
        if(user!=null){
            //2：比对密码
            String newPassword = DigestUtils.md5Hex(password + user.getSalt());
            if(newPassword.equals(user.getPassword())){
                result = true;
            }
        }
        return result;
    }
}
