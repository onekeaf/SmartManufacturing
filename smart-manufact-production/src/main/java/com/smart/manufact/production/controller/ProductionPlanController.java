package com.smart.manufact.production.controller;

import com.smart.manufact.common.result.Result;
import com.smart.manufact.production.service.ProductionPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/production/plan")
@RequiredArgsConstructor
public class ProductionPlanController {
    
    private final ProductionPlanService productionPlanService;

    @PostMapping("/create")
    public Result<Map<String, Object>> createProductionPlan(@RequestBody Map<String, Object> request) {
        log.info("创建生产计划请求：{}", request);
        Map<String, Object> result = productionPlanService.createProductionPlan(request);
        return Result.success("生产计划创建成功", result);
    }

    @GetMapping("/{planId}")
    public Result<Map<String, Object>> getPlanById(@PathVariable String planId) {
        Map<String, Object> result = productionPlanService.getPlanById(planId);
        return Result.success(result);
    }

    @PutMapping("/{planId}/status")
    public Result<Map<String, Object>> updatePlanStatus(
            @PathVariable String planId,
            @RequestParam Integer status) {
        Map<String, Object> result = productionPlanService.updatePlanStatus(planId, status);
        return Result.success(result);
    }
}
