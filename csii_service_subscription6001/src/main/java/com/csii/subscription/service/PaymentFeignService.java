package com.csii.subscription.service;


import com.csii.commons.entities.CommonResult;
import com.csii.commons.entities.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@Component
@FeignClient(value = "nocas-csii-provider",fallback = PaymentFallbackService.class)
public interface PaymentFeignService {

    @GetMapping(value = "/getregist/get/{id}")
    public String getRegist(@PathVariable("id") Integer id);
}