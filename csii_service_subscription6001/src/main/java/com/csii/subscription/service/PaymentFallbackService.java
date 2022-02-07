package com.csii.subscription.service;

import com.alibaba.fastjson.JSON;
import com.csii.commons.entities.CommonResult;
import org.springframework.stereotype.Component;

@Component
public class PaymentFallbackService implements PaymentFeignService{
    @Override
    public String getRegist(Integer id) {

        return JSON.toJSONString(new CommonResult (44444,"服务降级返回,---PaymentFallbackService"));
    }
}
