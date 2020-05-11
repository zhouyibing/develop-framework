package com.yipeng.framework.common.model.biz;

import lombok.Data;

import java.util.Map;

/**
 * 结果概述
 * @author: yibingzhou
 */
@Data
public class ResultOverview {

    /** 失败数*/
    private Integer fail;
    /** 成功数*/
    private Integer success;

    /** 失败描述*/
    private Map<String, String> failedDesc;
}
