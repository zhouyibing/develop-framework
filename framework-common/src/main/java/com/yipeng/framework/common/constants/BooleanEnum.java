package com.yipeng.framework.common.constants;

import lombok.Getter;

/**
 * @author: yibingzhou
 */
public enum BooleanEnum implements CodeDesEnum<Integer, BooleanEnum>{
    //否
    FALSE(0, "否"),
    //是
    TRUE(1, "是");

    @Getter
    private Integer code;
    @Getter
    private String description;

    BooleanEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }


    @Override
    public BooleanEnum[] all() {
        return values();
    }
}
