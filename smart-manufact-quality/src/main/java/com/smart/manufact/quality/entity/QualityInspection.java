package com.smart.manufact.quality.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.manufact.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_quality_inspection")
public class QualityInspection extends BaseEntity {
    
    private String inspectionNo;
    
    private Long orderId;
    
    private String orderNo;
    
    private String productionPlanId;
    
    private String inspectionType;
    
    private String inspectionStage;
    
    private Integer status;
    
    private String inspectorName;
    
    private LocalDateTime inspectionTime;
    
    private String defectDescription;
    
    private String defectLevel;
    
    private BigDecimal qualityScore;
    
    private String correctionAction;
}
