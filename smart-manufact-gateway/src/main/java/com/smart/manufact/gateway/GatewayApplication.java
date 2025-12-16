package com.smart.manufact.gateway;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.Properties;

@EnableDiscoveryClient
@SpringBootApplication
public class GatewayApplication {
    
    public static void main(String[] args) {
        // 设置 Nacos 命名空间
        Properties properties = new Properties();
        properties.setProperty("spring.cloud.nacos.discovery.namespace", "smart-manufact");
        
        new SpringApplicationBuilder(GatewayApplication.class)
                .properties(properties)
                .run(args);
    }
}
