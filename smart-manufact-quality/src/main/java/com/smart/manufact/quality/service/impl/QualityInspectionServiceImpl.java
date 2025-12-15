package com.smart.manufact.quality.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.manufact.common.exception.BusinessException;
import com.smart.manufact.quality.entity.QualityInspection;
import com.smart.manufact.quality.feign.OrderFeignClient;
import com.smart.manufact.quality.mapper.QualityInspectionMapper;
import com.smart.manufact.quality.service.QualityInspectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class QualityInspectionServiceImpl implements QualityInspectionService {
    
    private final QualityInspectionMapper qualityInspectionMapper;
    private final OrderFeignClient orderFeignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QualityInspection createInspection(QualityInspection inspection) {
        inspection.setInspectionNo(generateInspectionNo());
        inspection.setStatus(1);
        inspection.setInspectionTime(LocalDateTime.now());
        inspection.setCreateTime(LocalDateTime.now());
        
        qualityInspectionMapper.insert(inspection);
        
        log.info("质检记录创建成功，检验单号：{}", inspection.getInspectionNo());
        
        return inspection;
    }

    @Override
    public QualityInspection getInspectionById(Long id) {
        QualityInspection inspection = qualityInspectionMapper.selectById(id);
        if (inspection == null) {
            throw new BusinessException("质检记录不存在");
        }
        return inspection;
    }

    @Override
    public QualityInspection getInspectionByNo(String inspectionNo) {
        LambdaQueryWrapper<QualityInspection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QualityInspection::getInspectionNo, inspectionNo);
        
        QualityInspection inspection = qualityInspectionMapper.selectOne(wrapper);
        if (inspection == null) {
            throw new BusinessException("质检记录不存在");
        }
        return inspection;
    }

    @Override
    public List<QualityInspection> getInspectionList(Long orderId, String inspectionType) {
        LambdaQueryWrapper<QualityInspection> wrapper = new LambdaQueryWrapper<>();
        
        if (orderId != null) {
            wrapper.eq(QualityInspection::getOrderId, orderId);
        }
        
        if (inspectionType != null && !inspectionType.isEmpty()) {
            wrapper.eq(QualityInspection::getInspectionType, inspectionType);
        }
        
        wrapper.orderByDesc(QualityInspection::getInspectionTime);
        
        return qualityInspectionMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QualityInspection updateInspectionStatus(String inspectionNo, Integer status) {
        QualityInspection inspection = getInspectionByNo(inspectionNo);
        
        inspection.setStatus(status);
        inspection.setUpdateTime(LocalDateTime.now());
        
        qualityInspectionMapper.updateById(inspection);
        
        log.info("质检状态更新成功，检验单号：{}，状态：{}", inspectionNo, status);
        
        return inspection;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> reportQualityIssue(String inspectionNo, String defectDescription) {
        QualityInspection inspection = getInspectionByNo(inspectionNo);
        
        inspection.setDefectDescription(defectDescription);
        inspection.setDefectLevel("HIGH");
        inspection.setStatus(3);
        inspection.setUpdateTime(LocalDateTime.now());
        
        qualityInspectionMapper.updateById(inspection);
        
        log.info("质量问题上报，检验单号：{}，订单ID：{}", inspectionNo, inspection.getOrderId());
        
        try {
            Map<String, Object> orderResult = orderFeignClient.updateOrderStatus(
                    inspection.getOrderId(), 4);
            
            log.info("订单状态已更新为待处理，订单ID：{}", inspection.getOrderId());
            
        } catch (Exception e) {
            log.error("更新订单状态失败", e);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "质量问题已上报");
        result.put("inspectionNo", inspectionNo);
        result.put("orderId", inspection.getOrderId());
        
        return result;
    }

    @Override
    public Map<String, Object> getQualityStatistics(String startDate, String endDate) {
        LambdaQueryWrapper<QualityInspection> wrapper = new LambdaQueryWrapper<>();
        
        if (startDate != null && !startDate.isEmpty()) {
            LocalDateTime start = LocalDateTime.parse(startDate + " 00:00:00", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            wrapper.ge(QualityInspection::getInspectionTime, start);
        }
        
        if (endDate != null && !endDate.isEmpty()) {
            LocalDateTime end = LocalDateTime.parse(endDate + " 23:59:59", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            wrapper.le(QualityInspection::getInspectionTime, end);
        }
        
        List<QualityInspection> inspections = qualityInspectionMapper.selectList(wrapper);
        
        long totalCount = inspections.size();
        long passedCount = inspections.stream()
                .filter(i -> i.getStatus() == 2)
                .count();
        long failedCount = inspections.stream()
                .filter(i -> i.getStatus() == 3)
                .count();
        
        BigDecimal avgScore = inspections.stream()
                .filter(i -> i.getQualityScore() != null)
                .map(QualityInspection::getQualityScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(totalCount > 0 ? totalCount : 1), 2, BigDecimal.ROUND_HALF_UP);
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalCount", totalCount);
        statistics.put("passedCount", passedCount);
        statistics.put("failedCount", failedCount);
        statistics.put("passRate", totalCount > 0 ? passedCount * 100.0 / totalCount : 0);
        statistics.put("averageScore", avgScore);
        
        return statistics;
    }

    private String generateInspectionNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = IdUtil.fastSimpleUUID().substring(0, 8).toUpperCase();
        return "QI" + date + uuid;
    }
}
