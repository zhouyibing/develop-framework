package com.yipeng.framework.common.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 基本查询参数
 * @author: yibingzhou
 */
public class BaseParam <T> extends AccessObject {
    /**记录的默认id主键*/
    @Getter
    @Setter
    private T id;
    public String primaryKey() {
        return "id";
    }
}
