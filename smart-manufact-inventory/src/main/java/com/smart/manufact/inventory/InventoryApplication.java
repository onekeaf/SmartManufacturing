package com.smart.manufact.inventory;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@MapperScan("com.smart.manufact.inventory.mapper")
@SpringBootApplication(scanBasePackages = {"com.smart.manufact.inventory", "com.smart.manufact.common"})
public class InventoryApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
    }
}
