package com.smart.manufact.inventory.service;

import com.smart.manufact.inventory.entity.Inventory;
import java.util.List;
import java.util.Map;

public interface InventoryService {
    
    Inventory getInventoryById(Long id);
    
    Inventory getInventoryByMaterialCode(String materialCode);
    
    List<Inventory> getInventoryList(String materialType);
    
    Map<String, Object> checkMaterialAvailability(String vehicleModel, Integer quantity);
    
    Inventory updateInventoryQuantity(String materialCode, Integer quantity);
    
    List<Inventory> getLowStockInventory();
}
