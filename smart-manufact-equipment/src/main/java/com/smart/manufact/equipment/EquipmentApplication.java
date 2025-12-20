package com.smart.manufact.equipment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.Properties;

@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.smart.manufact.equipment.mapper")
@SpringBootApplication(scanBasePackages = {"com.smart.manufact.equipment", "com.smart.manufact.common"})
public class EquipmentApplication {
    
    public static void main(String[] args) {
        // 设置 Nacos 命名空间
        Properties properties = new Properties();
        properties.setProperty("spring.cloud.nacos.discovery.namespace", "smart-manufact");
        
        new SpringApplicationBuilder(EquipmentApplication.class)
                .properties(properties)
                .run(args);
    }
}
