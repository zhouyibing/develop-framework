package com.yipeng.framework.core.model.biz;

import com.yipeng.framework.core.model.db.AccessObject;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty("记录的默认id主键")
    private T id;
    public String primaryKey() {
        return "id";
    }
}
