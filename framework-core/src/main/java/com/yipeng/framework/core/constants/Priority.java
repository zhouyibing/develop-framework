package com.yipeng.framework.core.constants;

import lombok.Getter;

/**
 * 优先级
 * @author: yibingzhou
 */
public enum Priority implements CodeDesEnum<Integer, Priority> {
    //低
    LOW(0, "低"),
    //中
    MEDIUM(5,"中"),
    //高
    HIGH(10, "高");

    @Getter
    private Integer code;
    @Getter
    private String description;

    Priority(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public Priority[] all() {
        return values();
    }
}
