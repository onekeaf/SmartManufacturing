package com.smart.manufact.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.Properties;

@EnableFeignClients
@EnableDiscoveryClient
@MapperScan("com.smart.manufact.order.mapper")
@SpringBootApplication(scanBasePackages = {"com.smart.manufact.order", "com.smart.manufact.common"})
public class OrderApplication {
    
    public static void main(String[] args) {
        // 设置 Nacos 命名空间
        Properties properties = new Properties();
        properties.setProperty("spring.cloud.nacos.discovery.namespace", "smart-manufact");
        
        new SpringApplicationBuilder(OrderApplication.class)
                .properties(properties)
                .run(args);
    }
}
