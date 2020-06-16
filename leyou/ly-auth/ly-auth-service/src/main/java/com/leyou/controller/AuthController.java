package com.leyou.controller;

import com.leyou.client.UserClient;
import com.leyou.common.JwtUtils;
import com.leyou.common.UserInfo;
import com.leyou.config.JwtProperties;
import com.leyou.pojo.User;
import com.leyou.utils.CookieUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthController {

    @Autowired
    UserClient userClient;
    @Autowired
    JwtProperties jwtProperties;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @return
     */
    @PostMapping("login")
    public String login(@RequestParam("username")String username, @RequestParam("password") String password,
                        HttpServletRequest request, HttpServletResponse response){
        String result = "1";

        /**
         * 1：获取用户信息
         * 2：把用户信息  根据公钥和私钥 去生成我们的token
         * 3：生成的token返回用户，写入cookie
         *
         */

        User queryUser = userClient.query(username, password);

        try {
            if(queryUser!=null){

                //给当前用户生成token
                String token = JwtUtils.generateToken(new UserInfo(queryUser.getId(), queryUser.getUsername()),
                          jwtProperties.getPrivateKey(),
                        jwtProperties.getExpire() * 60);
                logger.info("授权中心获取到的用户token{}",token);

                //给登录的用户写入cookie
                CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getExpire()*60);

                result ="0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 当cookie失效之后重新设置cookie
     */
    @GetMapping("verify")
    public Object verify(@CookieValue(value = "token",required = false) String token, HttpServletRequest request, HttpServletResponse response){

        System.out.println("verify====="+token);
        UserInfo userInfo =new UserInfo();
        try {
            //从token信息中解析获取用户信息
            userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());

            //防止过期，重新设置token
            token = JwtUtils.generateToken(new UserInfo(userInfo.getId(), userInfo.getUsername()),
                    jwtProperties.getPrivateKey(), jwtProperties.getExpire());

            //返回token
            CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getExpire()*60);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userInfo;
    }
}
