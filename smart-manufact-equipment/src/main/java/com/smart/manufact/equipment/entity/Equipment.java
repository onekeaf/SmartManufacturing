package com.smart.manufact.equipment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.manufact.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_equipment")
public class Equipment extends BaseEntity {
    
    private String equipmentCode;
    
    private String equipmentName;
    
    private String equipmentType;
    
    private String workshopCode;
    
    private Integer status;
    
    private BigDecimal oee;
    
    private Integer runningHours;
    
    private LocalDateTime lastMaintenanceTime;
    
    private LocalDateTime nextMaintenanceTime;
    
    private String currentTask;
}
