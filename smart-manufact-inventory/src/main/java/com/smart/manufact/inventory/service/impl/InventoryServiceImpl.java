package com.smart.manufact.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.manufact.common.exception.BusinessException;
import com.smart.manufact.inventory.entity.Inventory;
import com.smart.manufact.inventory.mapper.InventoryMapper;
import com.smart.manufact.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    
    private final InventoryMapper inventoryMapper;

    @Override
    public Inventory getInventoryById(Long id) {
        Inventory inventory = inventoryMapper.selectById(id);
        if (inventory == null) {
            throw new BusinessException("库存记录不存在");
        }
        return inventory;
    }

    @Override
    public Inventory getInventoryByMaterialCode(String materialCode) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getMaterialCode, materialCode);
        
        Inventory inventory = inventoryMapper.selectOne(wrapper);
        if (inventory == null) {
            throw new BusinessException("物料不存在");
        }
        return inventory;
    }

    @Override
    public List<Inventory> getInventoryList(String materialType) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        
        if (materialType != null && !materialType.isEmpty()) {
            wrapper.eq(Inventory::getMaterialType, materialType);
        }
        
        wrapper.orderByAsc(Inventory::getMaterialCode);
        
        return inventoryMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Object> checkMaterialAvailability(String vehicleModel, Integer quantity) {
        log.info("检查物料可用性，车型：{}，数量：{}", vehicleModel, quantity);
        
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Inventory::getMaterialName, vehicleModel)
               .or()
               .eq(Inventory::getMaterialType, "RAW_MATERIAL");
        
        List<Inventory> materials = inventoryMapper.selectList(wrapper);
        
        boolean allAvailable = true;
        StringBuilder insufficientMaterials = new StringBuilder();
        
        for (Inventory material : materials) {
            int requiredQuantity = quantity * 10;
            
            if (material.getQuantity() < requiredQuantity) {
                allAvailable = false;
                insufficientMaterials.append(material.getMaterialName())
                        .append("(需要:").append(requiredQuantity)
                        .append(",库存:").append(material.getQuantity())
                        .append("), ");
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("available", allAvailable);
        result.put("checkedMaterials", materials.size());
        
        if (!allAvailable) {
            result.put("message", "物料不足: " + insufficientMaterials.toString());
        }
        
        log.info("物料检查结果：{}", allAvailable ? "充足" : "不足");
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Inventory updateInventoryQuantity(String materialCode, Integer quantity) {
        Inventory inventory = getInventoryByMaterialCode(materialCode);
        
        int newQuantity = inventory.getQuantity() + quantity;
        if (newQuantity < 0) {
            throw new BusinessException("库存不足，无法扣减");
        }
        
        inventory.setQuantity(newQuantity);
        inventory.setUpdateTime(LocalDateTime.now());
        
        inventoryMapper.updateById(inventory);
        
        log.info("库存更新成功，物料编号：{}，变更数量：{}，当前库存：{}", 
                materialCode, quantity, newQuantity);
        
        return inventory;
    }

    @Override
    public List<Inventory> getLowStockInventory() {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.apply("quantity <= safety_stock");
        wrapper.orderByAsc(Inventory::getQuantity);
        
        return inventoryMapper.selectList(wrapper);
    }
}
