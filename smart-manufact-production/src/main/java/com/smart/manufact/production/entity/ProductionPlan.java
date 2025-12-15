package com.smart.manufact.production.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.manufact.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_production_plan")
public class ProductionPlan extends BaseEntity {
    
    private String planId;
    
    private Long orderId;
    
    private String orderNo;
    
    private String vehicleModel;
    
    private Integer quantity;
    
    private Integer priority;
    
    private Integer status;
    
    private String workshopCode;
    
    private LocalDateTime planStartTime;
    
    private LocalDateTime planEndTime;
    
    private LocalDateTime actualStartTime;
    
    private LocalDateTime actualEndTime;
    
    private Integer completedQuantity;
}
