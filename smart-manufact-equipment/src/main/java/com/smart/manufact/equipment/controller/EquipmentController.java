package com.smart.manufact.equipment.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.smart.manufact.common.result.Result;
import com.smart.manufact.equipment.entity.Equipment;
import com.smart.manufact.equipment.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/equipment")
@RequiredArgsConstructor
public class EquipmentController {
    
    private final EquipmentService equipmentService;

    @GetMapping("/{id}")
    public Result<Equipment> getEquipmentById(@PathVariable Long id) {
        Equipment equipment = equipmentService.getEquipmentById(id);
        return Result.success(equipment);
    }

    @GetMapping("/code/{equipmentCode}")
    @SentinelResource(value = "getEquipmentByCode", blockHandler = "handleBlock")
    public Result<Equipment> getEquipmentByCode(@PathVariable String equipmentCode) {
        Equipment equipment = equipmentService.getEquipmentByCode(equipmentCode);
        return Result.success(equipment);
    }

    @GetMapping("/list")
    public Result<List<Equipment>> getEquipmentList(
            @RequestParam(required = false) String workshopCode,
            @RequestParam(required = false) Integer status) {
        List<Equipment> equipmentList = equipmentService.getEquipmentList(workshopCode, status);
        return Result.success(equipmentList);
    }

    @GetMapping("/check-availability")
    public Result<Map<String, Object>> checkEquipmentAvailability(
            @RequestParam String workshopCode) {
        Map<String, Object> result = equipmentService.checkEquipmentAvailability(workshopCode);
        return Result.success(result);
    }

    @PutMapping("/{equipmentCode}/status")
    public Result<Equipment> updateEquipmentStatus(
            @PathVariable String equipmentCode,
            @RequestParam Integer status) {
        Equipment equipment = equipmentService.updateEquipmentStatus(equipmentCode, status);
        return Result.success("设备状态更新成功", equipment);
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getEquipmentStatistics(
            @RequestParam(required = false) String workshopCode) {
        Map<String, Object> statistics = equipmentService.getEquipmentStatistics(workshopCode);
        return Result.success(statistics);
    }

    public Result<Equipment> handleBlock(String equipmentCode, BlockException ex) {
        log.warn("设备查询被限流，设备编号：{}", equipmentCode);
        Map<String, Object> fallbackData = new HashMap<>();
        fallbackData.put("message", "系统繁忙，请稍后重试");
        return Result.error(429, "请求过于频繁");
    }
}
