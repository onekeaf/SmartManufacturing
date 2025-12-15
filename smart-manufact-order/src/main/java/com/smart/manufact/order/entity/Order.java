package com.smart.manufact.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.manufact.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order")
public class Order extends BaseEntity {
    
    private String orderNo;
    
    private String customerName;
    
    private String customerType;
    
    private String vehicleModel;
    
    private Integer quantity;
    
    private BigDecimal totalAmount;
    
    private Integer priority;
    
    private Integer status;
    
    private LocalDateTime deliveryDate;
    
    private String productionPlanId;
    
    private String workshopCode;
}
