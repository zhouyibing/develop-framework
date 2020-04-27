package com.yipeng.framework.common.constants;

import com.yipeng.framework.common.exception.ErrorCode;
import com.yipeng.framework.common.exception.ExceptionUtil;

/**
 * code-description类型枚举 接口
 * @author: yibingzhou
 */
public interface CodeDesEnum<C,E extends Enum> {
    C getCode();

    String getDescription();

    E[] all();

    default E codeOf(C code) {
        if (code == null) {
            throw ExceptionUtil.doThrow(ErrorCode.UNSUPPORTTED_TYPE);
        }
        for(E type : all()) {
            if( getCode().equals(code)) {
                return type;
            }
        }
        throw ExceptionUtil.doThrow(ErrorCode.UNSUPPORTTED_TYPE);
    }
}
