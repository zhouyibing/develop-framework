package com.yipeng.framework.common.constants;

import lombok.Getter;

/**
 * 优先级
 * @author: yibingzhou
 */
public enum Priority implements CodeDesEnum<Integer, Priority> {
    LOW(0, "低"), MEDIUM(5,"中"), HIGH(10, "高");

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
