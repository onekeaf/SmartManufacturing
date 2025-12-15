package com.smart.manufact.equipment.service;

import com.smart.manufact.equipment.entity.Equipment;
import java.util.List;
import java.util.Map;

public interface EquipmentService {
    
    Equipment getEquipmentById(Long id);
    
    Equipment getEquipmentByCode(String equipmentCode);
    
    List<Equipment> getEquipmentList(String workshopCode, Integer status);
    
    Map<String, Object> checkEquipmentAvailability(String workshopCode);
    
    Equipment updateEquipmentStatus(String equipmentCode, Integer status);
    
    Map<String, Object> getEquipmentStatistics(String workshopCode);
}
