package com.yipeng.framework.common.constants;

import lombok.Getter;

/**
 * @author: yibingzhou
 */
public enum ConvertRule implements CodeDesEnum<Integer, ConvertRule> {
    FULL_NAME(1, "全名称匹配"), IGNORE_CASE(2, "忽略大小写");

    @Getter
    private Integer code;
    @Getter
    private String description;

    ConvertRule(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public ConvertRule[] all() {
        return values();
    }
}
