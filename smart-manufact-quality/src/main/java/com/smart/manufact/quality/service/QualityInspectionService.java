package com.smart.manufact.quality.service;

import com.smart.manufact.quality.entity.QualityInspection;
import java.util.List;
import java.util.Map;

public interface QualityInspectionService {
    
    QualityInspection createInspection(QualityInspection inspection);
    
    QualityInspection getInspectionById(Long id);
    
    QualityInspection getInspectionByNo(String inspectionNo);
    
    List<QualityInspection> getInspectionList(Long orderId, String inspectionType);
    
    QualityInspection updateInspectionStatus(String inspectionNo, Integer status);
    
    Map<String, Object> reportQualityIssue(String inspectionNo, String defectDescription);
    
    Map<String, Object> getQualityStatistics(String startDate, String endDate);
}
