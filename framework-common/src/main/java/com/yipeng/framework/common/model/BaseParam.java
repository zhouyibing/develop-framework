package com.yipeng.framework.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 基本查询参数
 * @author: yibingzhou
 */
public class BaseParam extends AccessObject{
    /**记录的id*/
    @Getter
    @Setter
    @ApiModelProperty(value = "id")
    private Long id;
}
