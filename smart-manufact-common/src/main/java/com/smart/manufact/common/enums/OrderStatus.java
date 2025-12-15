package com.smart.manufact.common.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    
    PENDING(0, "待处理"),
    CONFIRMED(1, "已确认"),
    PRODUCING(2, "生产中"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消");

    private final Integer code;
    private final String desc;

    OrderStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
