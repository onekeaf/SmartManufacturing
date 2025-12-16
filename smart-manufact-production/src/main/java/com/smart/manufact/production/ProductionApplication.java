package com.smart.manufact.production;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.Properties;

@EnableFeignClients
@EnableDiscoveryClient
@MapperScan("com.smart.manufact.production.mapper")
@SpringBootApplication(scanBasePackages = {"com.smart.manufact.production", "com.smart.manufact.common"})
public class ProductionApplication {
    
    public static void main(String[] args) {
        // 设置 Nacos 命名空间
        Properties properties = new Properties();
        properties.setProperty("spring.cloud.nacos.discovery.namespace", "smart-manufact");
        
        new SpringApplicationBuilder(ProductionApplication.class)
                .properties(properties)
                .run(args);
    }
}
