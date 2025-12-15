package com.smart.manufact.production.feign;

import com.smart.manufact.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = ServiceNameConstants.EQUIPMENT_SERVICE)
public interface EquipmentFeignClient {
    
    @GetMapping("/equipment/check-availability")
    Map<String, Object> checkEquipmentAvailability(@RequestParam String workshopCode);
}
