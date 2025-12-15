package com.smart.manufact.quality;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@EnableDiscoveryClient
@MapperScan("com.smart.manufact.quality.mapper")
@SpringBootApplication(scanBasePackages = {"com.smart.manufact.quality", "com.smart.manufact.common"})
public class QualityApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(QualityApplication.class, args);
    }
}
