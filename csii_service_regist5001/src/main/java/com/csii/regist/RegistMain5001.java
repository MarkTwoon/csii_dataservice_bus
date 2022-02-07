package com.csii.regist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient//开启服务注册发现功能
public class RegistMain5001 {
    public static void main(String[] args) {
        SpringApplication.run(RegistMain5001.class,args);
    }
}
