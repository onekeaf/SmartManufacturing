package com.smart.manufact.production.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.manufact.common.exception.BusinessException;
import com.smart.manufact.production.entity.ProductionPlan;
import com.smart.manufact.production.feign.EquipmentFeignClient;
import com.smart.manufact.production.feign.InventoryFeignClient;
import com.smart.manufact.production.mapper.ProductionPlanMapper;
import com.smart.manufact.production.service.ProductionPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductionPlanServiceImpl implements ProductionPlanService {
    
    private final ProductionPlanMapper productionPlanMapper;
    private final InventoryFeignClient inventoryFeignClient;
    private final EquipmentFeignClient equipmentFeignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createProductionPlan(Map<String, Object> request) {
        String vehicleModel = (String) request.get("vehicleModel");
        Integer quantity = (Integer) request.get("quantity");
        Integer priority = (Integer) request.get("priority");
        
        log.info("开始创建生产计划，车型：{}，数量：{}", vehicleModel, quantity);
        
        Map<String, Object> inventoryCheck = inventoryFeignClient.checkMaterialAvailability(vehicleModel, quantity);
        Boolean inventoryAvailable = (Boolean) inventoryCheck.get("available");
        
        if (!inventoryAvailable) {
            throw new BusinessException("物料库存不足，无法创建生产计划");
        }
        
        String workshopCode = "WORKSHOP-01";
        Map<String, Object> equipmentCheck = equipmentFeignClient.checkEquipmentAvailability(workshopCode);
        Boolean equipmentAvailable = (Boolean) equipmentCheck.get("available");
        
        if (!equipmentAvailable) {
            throw new BusinessException("设备不可用，无法创建生产计划");
        }
        
        ProductionPlan plan = new ProductionPlan();
        plan.setPlanId(generatePlanId());
        plan.setOrderId(((Number) request.get("orderId")).longValue());
        plan.setOrderNo((String) request.get("orderNo"));
        plan.setVehicleModel(vehicleModel);
        plan.setQuantity(quantity);
        plan.setPriority(priority);
        plan.setStatus(1);
        plan.setWorkshopCode(workshopCode);
        plan.setPlanStartTime(LocalDateTime.now());
        plan.setPlanEndTime(LocalDateTime.now().plusDays(7));
        plan.setCompletedQuantity(0);
        plan.setCreateTime(LocalDateTime.now());
        
        productionPlanMapper.insert(plan);
        
        log.info("生产计划创建成功，计划ID：{}", plan.getPlanId());
        
        Map<String, Object> result = new HashMap<>();
        result.put("planId", plan.getPlanId());
        result.put("status", "success");
        result.put("message", "生产计划创建成功");
        
        return result;
    }

    @Override
    public Map<String, Object> getPlanById(String planId) {
        LambdaQueryWrapper<ProductionPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductionPlan::getPlanId, planId);
        
        ProductionPlan plan = productionPlanMapper.selectOne(wrapper);
        if (plan == null) {
            throw new BusinessException("生产计划不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("plan", plan);
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updatePlanStatus(String planId, Integer status) {
        LambdaQueryWrapper<ProductionPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductionPlan::getPlanId, planId);
        
        ProductionPlan plan = productionPlanMapper.selectOne(wrapper);
        if (plan == null) {
            throw new BusinessException("生产计划不存在");
        }
        
        plan.setStatus(status);
        plan.setUpdateTime(LocalDateTime.now());
        
        if (status == 2) {
            plan.setActualStartTime(LocalDateTime.now());
        } else if (status == 3) {
            plan.setActualEndTime(LocalDateTime.now());
        }
        
        productionPlanMapper.updateById(plan);
        
        log.info("生产计划状态更新成功，计划ID：{}，状态：{}", planId, status);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "生产计划状态更新成功");
        
        return result;
    }

    private String generatePlanId() {
        return "PLAN-" + IdUtil.fastSimpleUUID().substring(0, 12).toUpperCase();
    }
}
