package com.smart.manufact.common.enums;

import lombok.Getter;

@Getter
public enum OrderPriority {
    
    LOW(1, "低"),
    NORMAL(2, "普通"),
    HIGH(3, "高"),
    URGENT(4, "紧急");

    private final Integer code;
    private final String desc;

    OrderPriority(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
