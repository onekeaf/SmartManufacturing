package com.smart.manufact.production.feign;

import com.smart.manufact.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = ServiceNameConstants.INVENTORY_SERVICE)
public interface InventoryFeignClient {
    
    @GetMapping("/inventory/check-availability")
    Map<String, Object> checkMaterialAvailability(
            @RequestParam("vehicleModel") String vehicleModel,
            @RequestParam("quantity") Integer quantity);
}
