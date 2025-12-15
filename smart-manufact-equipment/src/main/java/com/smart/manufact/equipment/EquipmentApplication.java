package com.smart.manufact.equipment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@MapperScan("com.smart.manufact.equipment.mapper")
@SpringBootApplication(scanBasePackages = {"com.smart.manufact.equipment", "com.smart.manufact.common"})
public class EquipmentApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(EquipmentApplication.class, args);
    }
}
