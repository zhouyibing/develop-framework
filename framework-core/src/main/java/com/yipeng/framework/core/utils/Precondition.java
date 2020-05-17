package com.yipeng.framework.core.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.yipeng.framework.core.exception.ErrorCode;
import com.yipeng.framework.core.exception.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class Precondition {
    private Precondition() {
    }

    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw ExceptionUtil.doThrow(ErrorCode.ILLEGAL_ARGUMENT);
        }
    }

    public static void checkArgument(boolean expression, String errorMessage) {
        if (!expression) {
            throw ExceptionUtil.doThrow(new ErrorCode(ErrorCode.ILLEGAL_ARGUMENT.getCode(), errorMessage));
        }
    }

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw ExceptionUtil.doThrow(ErrorCode.CAN_NOT_NULL);
        } else {
            return reference;
        }
    }

    public static <T> T checkNotNull(T reference, String errorMessage) {
        if (reference == null) {
            throw ExceptionUtil.doThrow(ErrorCode.CAN_NOT_NULL.msg(errorMessage));
        } else {
            return reference;
        }
    }

    public static <T> Collection<T> checkNotEmpty(Collection<T> reference, String errorMessage) {
        if (CollectionUtil.isEmpty(reference)) {
            throw ExceptionUtil.doThrow(ErrorCode.CAN_NOT_NULL.msg(errorMessage));
        } else {
            return reference;
        }
    }

    public static String checkNotBlank(String reference, String errorMessage) {
        if (StringUtils.isBlank(reference)) {
            throw ExceptionUtil.doThrow(ErrorCode.ILLEGAL_ARGUMENT.msg(errorMessage));
        } else {
            return reference;
        }
    }

    public static <T> T setDefault(T reference, T def) {
        if (reference == null) {
            return def;
        }
        return reference;
    }

    public static String setDefault(String reference, String def) {
        if (StringUtils.isBlank(reference)) {
            return def;
        }
        return reference;
    }

    public static <T> T setDefault(T reference, Supplier<T> supplier) {
        if (reference == null) {
            return supplier.get();
        }
        return reference;
    }

    public static Long checkValidId(Long reference, String errorMessage) {
        if (reference != null && reference > 0L) {
            return reference;
        } else {
            throw ExceptionUtil.doThrow(ErrorCode.ILLEGAL_ARGUMENT.msg(errorMessage));
        }
    }

    public static Integer checkValidId(Integer reference, String errorMessage) {
        if (reference != null && (long)reference > 0L) {
            return reference;
        } else {
            throw ExceptionUtil.doThrow(ErrorCode.ILLEGAL_ARGUMENT.msg(errorMessage));
        }
    }

    public static <T> T checkPhone(T reference, String errorMessage) {
        String regMobileNo = "^[1][3,4,5,6,7,8,9][0-9]{9}$";
        if (!Pattern.matches(regMobileNo, reference.toString())) {
            throw ExceptionUtil.doThrow(ErrorCode.ILLEGAL_ARGUMENT.msg(errorMessage));
        } else {
            return reference;
        }
    }

    public static String checkNumber(String reference, String errorMessage) {
        boolean flag = checkNumber(reference);
        if (flag) {
            return reference;
        } else {
            throw new RuntimeException(errorMessage);
        }
    }

    public static boolean checkNumber(CharSequence reference) {
        Boolean flag = true;
        if (StringUtils.isNotBlank(reference)) {
            int i = reference.length();

            while(true) {
                --i;
                if (i < 0) {
                    break;
                }

                if (!Character.isDigit(reference.charAt(i))) {
                    flag = false;
                    break;
                }
            }
        } else {
            flag = false;
        }
        return flag;
    }
}
