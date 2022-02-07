package com.csii.regist.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.csii.commons.entities.CommonResult;
import com.csii.commons.entities.Payment;
import com.csii.regist.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RefreshScope//实现配置自动更新
public class RegistController {
    @Value("${server.port}")
    private String serverPort;

    @Resource
    private PaymentService paymentService;

    @GetMapping(value = "/getregist/nacos/{id}")
    public String getRegist(@PathVariable("id") Integer id) {
        return "nacos registry, serverPort: " + serverPort + "\t id" + id;
    }

    //只传给前端CommonResult，不需要前端了解其他的组件
    @PostMapping(value = "/getregist/create")
    public CommonResult create(@RequestBody  Payment payment) {

        int result = paymentService.create(payment);
        //log.info("*****插入结果："+result);
        if (result > 0) {
            return new CommonResult(200, "插入数据成功,serverPort：" + serverPort, result);
        } else {
            return new CommonResult(444, "插入数据失败", null);
        }
    }


    @GetMapping(value = "/getregist/get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id") Long id) {
        Payment payment = paymentService.getPaymentById(id);
        //log.info("*****插入结果："+payment);
        if (payment != null) {
            return new CommonResult(200, "查询成功,serverPort：" + serverPort, payment);
        } else {
            return new CommonResult(444, "没有对应记录,查询ID：" + id, null);
        }
    }

    @GetMapping("/testD")
    public String testD()
    {
        try { TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) { e.printStackTrace(); }
        log.info("testD 测试RT");

        return "------testD";
    }

    @GetMapping("/testB")
    public String testB()
    {

        log.info("testB 测试RT");
        int age = 10/0;
        return "------testB";
    }

    @GetMapping("/testHotKey")
    //@SentinelResource(value = "testHotKey",blockHandler = "deal_testHotKey")
   @SentinelResource(value = "testHotKey")
    public String testHotKey(@RequestParam(value = "p1",required = false) String p1,
                             @RequestParam(value = "p2",required = false) String p2) throws BlockException{
        //int age = 10/0;
        return "------testHotKey";
    }

    //兜底方法
   /* public String deal_testHotKey (String p1, String p2, BlockException exception){
        return "------deal_testHotKey,o(╥﹏╥)o";
    }*/





}
