package com.smart.manufact.inventory;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.Properties;

@EnableDiscoveryClient
@MapperScan("com.smart.manufact.inventory.mapper")
@SpringBootApplication(scanBasePackages = {"com.smart.manufact.inventory", "com.smart.manufact.common"})
public class InventoryApplication {
    
    public static void main(String[] args) {
        // 设置 Nacos 命名空间
        Properties properties = new Properties();
        properties.setProperty("spring.cloud.nacos.discovery.namespace", "smart-manufact");
        
        new SpringApplicationBuilder(InventoryApplication.class)
                .properties(properties)
                .run(args);
    }
}
