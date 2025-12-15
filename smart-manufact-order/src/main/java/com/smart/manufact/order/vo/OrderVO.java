package com.smart.manufact.order.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderVO {
    
    private Long id;
    
    private String orderNo;
    
    private String customerName;
    
    private String customerType;
    
    private String vehicleModel;
    
    private Integer quantity;
    
    private BigDecimal totalAmount;
    
    private Integer priority;
    
    private String priorityDesc;
    
    private Integer status;
    
    private String statusDesc;
    
    private LocalDateTime deliveryDate;
    
    private String productionPlanId;
    
    private String workshopCode;
    
    private LocalDateTime createTime;
    
    private String remark;
}
