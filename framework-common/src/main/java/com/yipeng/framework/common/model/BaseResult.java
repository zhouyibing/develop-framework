package com.yipeng.framework.common.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 基本返回结果
 * @author: yibingzhou
 */
public class BaseResult implements Serializable {
    private static final long serialVersionUID = 979932422002828104L;
    /**记录的id*/
    @ApiModelProperty(value = "id")
    @Getter
    @Setter
    private Long id;
}
