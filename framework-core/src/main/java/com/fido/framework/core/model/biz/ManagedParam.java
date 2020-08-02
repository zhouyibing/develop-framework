package com.fido.framework.core.model.biz;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 管理型参数
 * 包含创建人，更新人字段
 * @author: yibingzhou
 */
@Data
public class ManagedParam<T extends Comparable> extends BaseParam <T>{

    @ApiModelProperty("创建人id")
    private String creatorId;

    @ApiModelProperty("更新人id")
    private String updaterId;
}
