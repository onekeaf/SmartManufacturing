package com.smart.manufact.order.config;

import com.smart.manufact.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = ServiceNameConstants.PRODUCTION_SERVICE, fallback = ProductionFeignClientFallback.class)
public interface ProductionFeignClient {
    
    @PostMapping("/production/plan/create")
    Map<String, Object> createProductionPlan(@RequestBody Map<String, Object> request);
}
