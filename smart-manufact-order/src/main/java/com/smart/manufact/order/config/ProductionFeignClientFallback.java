package com.smart.manufact.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ProductionFeignClientFallback implements ProductionFeignClient {
    
    @Override
    public Map<String, Object> createProductionPlan(Map<String, Object> request) {
        log.error("调用生产计划服务失败，触发降级");
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "生产计划服务暂时不可用，请稍后重试");
        return result;
    }
}
