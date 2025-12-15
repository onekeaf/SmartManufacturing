package com.smart.manufact.production;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@MapperScan("com.smart.manufact.production.mapper")
@SpringBootApplication(scanBasePackages = {"com.smart.manufact.production", "com.smart.manufact.common"})
public class ProductionApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ProductionApplication.class, args);
    }
}
