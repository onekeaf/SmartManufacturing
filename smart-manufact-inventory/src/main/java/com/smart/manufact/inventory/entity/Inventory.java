package com.smart.manufact.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smart.manufact.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_inventory")
public class Inventory extends BaseEntity {
    
    private String materialCode;
    
    private String materialName;
    
    private String materialType;
    
    private String unit;
    
    private Integer quantity;
    
    private Integer safetyStock;
    
    private Integer reorderPoint;
    
    private String warehouseLocation;
    
    private String supplierCode;
}
