package com.smart.manufact.inventory.controller;

import com.smart.manufact.common.result.Result;
import com.smart.manufact.inventory.entity.Inventory;
import com.smart.manufact.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {
    
    private final InventoryService inventoryService;

    @GetMapping("/{id}")
    public Result<Inventory> getInventoryById(@PathVariable Long id) {
        Inventory inventory = inventoryService.getInventoryById(id);
        return Result.success(inventory);
    }

    @GetMapping("/material/{materialCode}")
    public Result<Inventory> getInventoryByMaterialCode(@PathVariable String materialCode) {
        Inventory inventory = inventoryService.getInventoryByMaterialCode(materialCode);
        return Result.success(inventory);
    }

    @GetMapping("/list")
    public Result<List<Inventory>> getInventoryList(
            @RequestParam(required = false) String materialType) {
        List<Inventory> inventoryList = inventoryService.getInventoryList(materialType);
        return Result.success(inventoryList);
    }

    @GetMapping("/check-availability")
    public Result<Map<String, Object>> checkMaterialAvailability(
            @RequestParam String vehicleModel,
            @RequestParam Integer quantity) {
        Map<String, Object> result = inventoryService.checkMaterialAvailability(vehicleModel, quantity);
        return Result.success(result);
    }

    @PutMapping("/{materialCode}/quantity")
    public Result<Inventory> updateInventoryQuantity(
            @PathVariable String materialCode,
            @RequestParam Integer quantity) {
        Inventory inventory = inventoryService.updateInventoryQuantity(materialCode, quantity);
        return Result.success("库存更新成功", inventory);
    }

    @GetMapping("/low-stock")
    public Result<List<Inventory>> getLowStockInventory() {
        List<Inventory> lowStockList = inventoryService.getLowStockInventory();
        return Result.success(lowStockList);
    }
}
