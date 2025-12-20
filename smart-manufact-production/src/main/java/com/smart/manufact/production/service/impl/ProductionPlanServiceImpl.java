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
        
        try {
            // 调用库存服务检查物料可用性
            Map<String, Object> inventoryCheckResult = inventoryFeignClient.checkMaterialAvailability(vehicleModel, quantity);
            
            // 检查Feign调用是否成功
            if (inventoryCheckResult == null) {
                throw new BusinessException("调用库存服务失败：服务无响应");
            }
            
            // 从Result对象中提取实际的数据
            Map<String, Object> inventoryCheck = (Map<String, Object>) inventoryCheckResult.get("data");
            if (inventoryCheck == null) {
                throw new BusinessException("调用库存服务失败：返回数据为空");
            }
            
            // 获取可用性检查结果
            Boolean inventoryAvailable = (Boolean) inventoryCheck.get("available");
            
            // 检查物料是否充足
            if (inventoryAvailable == null || !inventoryAvailable) {
                String errorMessage = "物料库存不足，无法创建生产计划";
                if (inventoryCheck.containsKey("message")) {
                    errorMessage = (String) inventoryCheck.get("message");
                }
                throw new BusinessException(errorMessage);
            }
        } catch (Exception e) {
            log.error("调用库存服务检查物料可用性失败", e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("调用库存服务失败：" + e.getMessage());
        }
        
        try {
            String workshopCode = "WORKSHOP-01";
            // 调用设备服务检查设备可用性
            Map<String, Object> equipmentCheckResult = equipmentFeignClient.checkEquipmentAvailability(workshopCode);
            
            // 检查Feign调用是否成功
            if (equipmentCheckResult == null) {
                throw new BusinessException("调用设备服务失败：服务无响应");
            }
            
            // 从Result对象中提取实际的数据
            Map<String, Object> equipmentCheck = (Map<String, Object>) equipmentCheckResult.get("data");
            if (equipmentCheck == null) {
                throw new BusinessException("调用设备服务失败：返回数据为空");
            }
            
            // 获取可用性检查结果
            Boolean equipmentAvailable = (Boolean) equipmentCheck.get("available");
            
            // 检查设备是否可用
            if (equipmentAvailable == null || !equipmentAvailable) {
                String errorMessage = "设备不可用，无法创建生产计划";
                if (equipmentCheck.containsKey("message")) {
                    errorMessage = (String) equipmentCheck.get("message");
                }
                throw new BusinessException(errorMessage);
            }
        } catch (Exception e) {
            log.error("调用设备服务检查设备可用性失败", e);
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("调用设备服务失败：" + e.getMessage());
        }
        
        ProductionPlan plan = new ProductionPlan();
        plan.setPlanId(generatePlanId());
        plan.setOrderId(((Number) request.get("orderId")).longValue());
        plan.setOrderNo((String) request.get("orderNo"));
        plan.setVehicleModel(vehicleModel);
        plan.setQuantity(quantity);
        plan.setPriority(priority);
        plan.setStatus(1);
        plan.setWorkshopCode("WORKSHOP-01");
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
