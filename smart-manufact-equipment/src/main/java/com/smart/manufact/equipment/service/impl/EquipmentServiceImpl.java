package com.smart.manufact.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.manufact.common.enums.EquipmentStatus;
import com.smart.manufact.common.exception.BusinessException;
import com.smart.manufact.equipment.entity.Equipment;
import com.smart.manufact.equipment.mapper.EquipmentMapper;
import com.smart.manufact.equipment.service.EquipmentService;
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
public class EquipmentServiceImpl implements EquipmentService {
    
    private final EquipmentMapper equipmentMapper;

    @Override
    public Equipment getEquipmentById(Long id) {
        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException("设备不存在");
        }
        return equipment;
    }

    @Override
    public Equipment getEquipmentByCode(String equipmentCode) {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Equipment::getEquipmentCode, equipmentCode);
        
        Equipment equipment = equipmentMapper.selectOne(wrapper);
        if (equipment == null) {
            throw new BusinessException("设备不存在");
        }
        return equipment;
    }

    @Override
    public List<Equipment> getEquipmentList(String workshopCode, Integer status) {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        
        if (workshopCode != null && !workshopCode.isEmpty()) {
            wrapper.eq(Equipment::getWorkshopCode, workshopCode);
        }
        
        if (status != null) {
            wrapper.eq(Equipment::getStatus, status);
        }
        
        wrapper.orderByAsc(Equipment::getEquipmentCode);
        
        return equipmentMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Object> checkEquipmentAvailability(String workshopCode) {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Equipment::getWorkshopCode, workshopCode);
        wrapper.in(Equipment::getStatus, 
                EquipmentStatus.RUNNING.getCode(), 
                EquipmentStatus.IDLE.getCode());
        
        List<Equipment> availableEquipment = equipmentMapper.selectList(wrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("available", !availableEquipment.isEmpty());
        result.put("count", availableEquipment.size());
        result.put("equipmentList", availableEquipment);
        
        log.info("车间 {} 可用设备数量：{}", workshopCode, availableEquipment.size());
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Equipment updateEquipmentStatus(String equipmentCode, Integer status) {
        Equipment equipment = getEquipmentByCode(equipmentCode);
        
        equipment.setStatus(status);
        equipment.setUpdateTime(LocalDateTime.now());
        
        equipmentMapper.updateById(equipment);
        
        log.info("设备状态更新成功，设备编号：{}，状态：{}", equipmentCode, status);
        
        return equipment;
    }

    @Override
    public Map<String, Object> getEquipmentStatistics(String workshopCode) {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        
        if (workshopCode != null && !workshopCode.isEmpty()) {
            wrapper.eq(Equipment::getWorkshopCode, workshopCode);
        }
        
        List<Equipment> allEquipment = equipmentMapper.selectList(wrapper);
        
        long runningCount = allEquipment.stream()
                .filter(e -> e.getStatus().equals(EquipmentStatus.RUNNING.getCode()))
                .count();
        
        long idleCount = allEquipment.stream()
                .filter(e -> e.getStatus().equals(EquipmentStatus.IDLE.getCode()))
                .count();
        
        long maintenanceCount = allEquipment.stream()
                .filter(e -> e.getStatus().equals(EquipmentStatus.MAINTENANCE.getCode()))
                .count();
        
        long faultCount = allEquipment.stream()
                .filter(e -> e.getStatus().equals(EquipmentStatus.FAULT.getCode()))
                .count();
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalCount", allEquipment.size());
        statistics.put("runningCount", runningCount);
        statistics.put("idleCount", idleCount);
        statistics.put("maintenanceCount", maintenanceCount);
        statistics.put("faultCount", faultCount);
        statistics.put("availabilityRate", 
                allEquipment.isEmpty() ? 0 : (runningCount + idleCount) * 100.0 / allEquipment.size());
        
        return statistics;
    }
}
