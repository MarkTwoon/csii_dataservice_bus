package com.csii.subscription.controller;

import com.csii.commons.entities.CommonResult;
import com.csii.commons.util.TokenUtil;
import com.csii.subscription.service.PaymentFeignService;
import com.csii.subscription.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class OrderFeignController {

    @Resource
    private PaymentFeignService paymentFeignService;


    @Value("${server.port}")
    private String serverPort;

    @PostMapping(value = "/login")
    public CommonResult login(@RequestBody Map<String,Object> map, HttpServletResponse response){
       String username=map.get("username")+"";
        Long currentTimeMillis = System.currentTimeMillis();
        String token= TokenUtil.sign(username,currentTimeMillis );
        RedisUtil.set(username,currentTimeMillis,TokenUtil.REFRESH_EXPIRE_TIME);
        response.setHeader("Authorization", token);
        response.setHeader("Access-Control-Expose-Headers", "Authorization");
        map.put("token",token);
        return new CommonResult(200,"登录成功！",map);
    }
    @GetMapping(value = "/sub/getregist/nacos/{id}")
    public String getRegist(@PathVariable("id") Integer id){
        return paymentFeignService.getRegist(id);
    }

}