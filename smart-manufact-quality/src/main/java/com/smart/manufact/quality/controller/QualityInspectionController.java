package com.smart.manufact.quality.controller;

import com.smart.manufact.common.result.Result;
import com.smart.manufact.quality.entity.QualityInspection;
import com.smart.manufact.quality.service.QualityInspectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/quality/inspection")
@RequiredArgsConstructor
public class QualityInspectionController {
    
    private final QualityInspectionService qualityInspectionService;

    @PostMapping("/create")
    public Result<QualityInspection> createInspection(@RequestBody QualityInspection inspection) {
        log.info("创建质检记录请求：{}", inspection);
        QualityInspection result = qualityInspectionService.createInspection(inspection);
        return Result.success("质检记录创建成功", result);
    }

    @GetMapping("/{id}")
    public Result<QualityInspection> getInspectionById(@PathVariable Long id) {
        QualityInspection inspection = qualityInspectionService.getInspectionById(id);
        return Result.success(inspection);
    }

    @GetMapping("/no/{inspectionNo}")
    public Result<QualityInspection> getInspectionByNo(@PathVariable String inspectionNo) {
        QualityInspection inspection = qualityInspectionService.getInspectionByNo(inspectionNo);
        return Result.success(inspection);
    }

    @GetMapping("/list")
    public Result<List<QualityInspection>> getInspectionList(
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String inspectionType) {
        List<QualityInspection> inspectionList = qualityInspectionService.getInspectionList(orderId, inspectionType);
        return Result.success(inspectionList);
    }

    @PutMapping("/{inspectionNo}/status")
    public Result<QualityInspection> updateInspectionStatus(
            @PathVariable String inspectionNo,
            @RequestParam Integer status) {
        QualityInspection inspection = qualityInspectionService.updateInspectionStatus(inspectionNo, status);
        return Result.success("质检状态更新成功", inspection);
    }

    @PostMapping("/{inspectionNo}/report-issue")
    public Result<Map<String, Object>> reportQualityIssue(
            @PathVariable String inspectionNo,
            @RequestParam String defectDescription) {
        log.info("上报质量问题，检验单号：{}，问题描述：{}", inspectionNo, defectDescription);
        Map<String, Object> result = qualityInspectionService.reportQualityIssue(inspectionNo, defectDescription);
        return Result.success("质量问题已上报", result);
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getQualityStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> statistics = qualityInspectionService.getQualityStatistics(startDate, endDate);
        return Result.success(statistics);
    }
}
