package com.smart.manufact.quality.feign;

import com.smart.manufact.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = ServiceNameConstants.ORDER_SERVICE)
public interface OrderFeignClient {
    
    @PutMapping("/order/{id}/status")
    Map<String, Object> updateOrderStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") Integer status);
}
