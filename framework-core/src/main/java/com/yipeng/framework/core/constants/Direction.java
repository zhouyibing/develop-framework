package com.yipeng.framework.core.constants;

import lombok.Getter;

/**
 * @author: yibingzhou
 */
public enum Direction implements CodeDesEnum<Integer, Direction> {
    //输入
    IN(0,"输入"),
    //输出
    OUT(1,"输出"),
    //双向
    BOTH(2,"双向");

    @Getter
    private Integer code;
    @Getter
    private String description;

    Direction(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public Direction[] all() {
        return values();
    }
}
