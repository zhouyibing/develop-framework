package com.yipeng.framework.projectbuilder.model;

import lombok.Data;

/**
 * @author: yibingzhou
 */
@Data
public class FieldMeta {
    private String fieldType;
    private String fieldName;
    private String originalFieldName;
    private String fieldComment;
    private boolean notInModel;
    private boolean notInParam;
    private boolean notInResult;
    private boolean isShort;
    private boolean isDouble;
    private boolean isFloat;
    private boolean isByte;
    private boolean isBigDecimal;
    private boolean isDate;
}
