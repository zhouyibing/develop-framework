package com.yipeng.framework.common.constants;

import lombok.Getter;

/**
 * @author: yibingzhou
 */
public enum CaseRule implements CodeDesEnum<Integer, CaseRule> {
    //默认
    DEFAULT(0, "默认"),
    //全名称匹配
    FULL_NAME(1, "全名称匹配"),
    //忽略大小写
    IGNORE_CASE(2, "忽略大小写");

    @Getter
    private Integer code;
    @Getter
    private String description;

    CaseRule(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public CaseRule[] all() {
        return values();
    }
}
