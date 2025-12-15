package com.smart.manufact.common.enums;

import lombok.Getter;

@Getter
public enum EquipmentStatus {
    
    RUNNING(1, "运行中"),
    IDLE(2, "空闲"),
    MAINTENANCE(3, "维护中"),
    FAULT(4, "故障"),
    OFFLINE(5, "离线");

    private final Integer code;
    private final String desc;

    EquipmentStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
