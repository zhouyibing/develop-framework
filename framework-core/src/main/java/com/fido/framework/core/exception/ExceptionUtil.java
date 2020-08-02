package com.fido.framework.core.exception;

import lombok.Getter;

/**
 * @author: yibingzhou
 */
public class ExceptionUtil {

    public static RuntimeException doThrow(ErrorCode errorCode) {
        return isTechErroCode(errorCode) ? new TechException(errorCode) : new BizException(errorCode);
    }

    public static boolean isTechErroCode(ErrorCode errorCode) {
        String code = errorCode.getCode();
        char c = code.charAt(code.length()-1);
        return c % 2 == 0;
    }

    public static boolean isBizErrorCode(ErrorCode errorCode) {
        return !isTechErroCode(errorCode);
    }

    public static RuntimeException doThrow(ErrorCode errorCode, Object[] errorParams) {
        return isTechErroCode(errorCode) ? new TechException(errorCode, errorParams) : new BizException(errorCode, errorParams);
    }

    public static TechException doThrowTechException() {
        return new TechException(ErrorCode.SERVER_INTERNAL_ERROR);
    }

    public static BizException doThrowBizException() {
        return new BizException(ErrorCode.BIZ_ERROR);
    }

    public static class TechException extends RuntimeException{

        private static final long serialVersionUID = 415255227915360074L;
        @Getter
        private ErrorCode errorCode;

        private TechException(ErrorCode errorCode) {
            this(errorCode, null);
        }

        private TechException(ErrorCode errorCode, Object[] errorParams) {
            super(ErrorCode.formatMsg(errorCode.getMsg(), errorParams));
            if (!ExceptionUtil.isTechErroCode(errorCode)) {
                throw new RuntimeException("技术异常代码最后一位必须是偶数");
            }
            this.errorCode = errorCode.errorParams(errorParams);
        }
    }

    public static class BizException extends RuntimeException{

        private static final long serialVersionUID = 3773628804446554553L;
        @Getter
        private ErrorCode errorCode;

        private BizException(ErrorCode errorCode) {
            this(errorCode, null);
        }

        private BizException(ErrorCode errorCode, Object[] errorParams) {
            super(ErrorCode.formatMsg(errorCode.getMsg(), errorParams));
            if (!ExceptionUtil.isBizErrorCode(errorCode)) {
                throw new RuntimeException ("业务异常代码最后一位必须是奇数");
            }
            this.errorCode = errorCode.errorParams(errorParams);
        }
    }
}
