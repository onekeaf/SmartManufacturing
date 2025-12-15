package com.smart.manufact.order.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDTO {
    
    private String customerName;
    
    private String customerType;
    
    private String vehicleModel;
    
    private Integer quantity;
    
    private BigDecimal totalAmount;
    
    private Integer priority;
    
    private LocalDateTime deliveryDate;
    
    private String remark;
}
