package com.csii.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
//@EnableEurekaClient//Eureka
@EnableDiscoveryClient
public class GateWayMain4001 {

    public static void main(String[] args) {

        SpringApplication.run(GateWayMain4001.class, args);

    }
}