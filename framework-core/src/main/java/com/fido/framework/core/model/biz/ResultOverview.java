package com.fido.framework.core.model.biz;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * 结果概述
 * @author: yibingzhou
 */
@Data
@ApiModel("结果概述")
public class ResultOverview {

    @ApiModelProperty("失败数")
    private Integer fail;

    @ApiModelProperty("成功数")
    private Integer success;

    @ApiModelProperty("失败描述")
    private Map<String, String> failedDesc;
}
