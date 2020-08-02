package com.fido.framework.core.exception;

import com.fido.framework.core.utils.ConfigUtils;
import com.fido.framework.core.utils.Precondition;
import lombok.Getter;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * 错误代码
 * 从左到右第1~3位标识应用/系统代码，第4~6位标识业务模块/服务代码，后三位标识具体的错误代码
 * 应用/系统代码从AppInfo里提取，在集成框架时，强制让应用设置AppInfo（便于管理和问题追踪）
 * 错误代码尽量保证全局唯一性。便于后面定位问题
 * 如：微商城-订单-下单失败 0010010001
 * 每个业务模块保证只有一个ErrorCode的子类，用于定义该模块内的所有错误代码
 * 业务异常代码，代码结尾为奇数
 * 奇数异常代码，代码结尾为偶数
 * @author: yibingzhou
 */
public class ErrorCode {

    public final static String FRAMEWORK_APP_ID = "000000";
    /**技术异常代码，代码结尾为偶数*/
    public final static ErrorCode SERVER_INTERNAL_ERROR = new ErrorCode(FRAMEWORK_APP_ID+"0000", "服务器内部错误:{0}");
    public final static ErrorCode UNSUPPORTTED_TYPE = new ErrorCode(FRAMEWORK_APP_ID+"0002", "不支持的类型");
    public final static ErrorCode ILLEGAL_ARGUMENT = new ErrorCode(FRAMEWORK_APP_ID+"0004", "参数错误");
    public final static ErrorCode CAN_NOT_NULL = new ErrorCode(FRAMEWORK_APP_ID+"0006", "参数不能为空");
    public final static ErrorCode PARAM_TYPE_NOT_MATCH = new ErrorCode(FRAMEWORK_APP_ID+"0008", "参数类型不匹配");
    public final static ErrorCode OBJECT_INSTANCE_FAILED = new ErrorCode(FRAMEWORK_APP_ID+"0010", "对象实例化失败");
    public final static ErrorCode OBJECT_CONVERT_FAILED = new ErrorCode(FRAMEWORK_APP_ID+"0012", "对象转换({0} to {1})失败:{2}");
    public final static ErrorCode DUPLICATE_KEY = new ErrorCode(FRAMEWORK_APP_ID+"0014", "主键冲突:{0}");
    public final static ErrorCode DATA_ACCESS_EXCEPTION = new ErrorCode(FRAMEWORK_APP_ID+"0016", "数据访问异常:{0}");

    /***业务异常代码，代码结尾为奇数*/
    public final static ErrorCode BIZ_ERROR = new ErrorCode(FRAMEWORK_APP_ID+"0001", "业务异常");
    public final static ErrorCode QUERY_PARAMS_IS_NULL = new ErrorCode(FRAMEWORK_APP_ID+"0003", "查询参数为空");
    public final static ErrorCode RECORD_EXISTED = new ErrorCode(FRAMEWORK_APP_ID+"0005", "记录已存在");
    public final static ErrorCode TOKEN_EMPTY = new ErrorCode(FRAMEWORK_APP_ID+"0007", "token为空");
    public final static ErrorCode TOKEN_EXPIRED = new ErrorCode(FRAMEWORK_APP_ID+"0009", "token已过期");
    public final static ErrorCode TOKEN_PARSE_ERROR = new ErrorCode(FRAMEWORK_APP_ID+"0011", "token解析异常");
    public final static ErrorCode SIGNATURE_FAILED = new ErrorCode(FRAMEWORK_APP_ID+"0013", "签名失败");
    public final static ErrorCode ACCESS_FORBIDDEN = new ErrorCode(FRAMEWORK_APP_ID+"0015", "禁止访问");

    /** 错误代码*/
    @Getter
    private String code;

    /** 错误消息体*/
    @Getter
    private String msg;

    /** 错误参数，可用于格式化输出*/
    @Getter
    private Object[] errorParams;

    public ErrorCode(String code) {
        checkCode(code);
        this.code = code;
    }

    public ErrorCode(String code, String msg) {
        this(code);
        this.msg = msg;
    }

    public ErrorCode(String code, String msg, Object[] errorParams) {
        this(code);
        this.msg = formatMsg(msg, errorParams);
        this.errorParams = errorParams;
    }

    public ErrorCode msg(String msg) {
        this.msg = msg;
        return this;
    }

    public ErrorCode errorParams(Object[] errorParams) {
        this.errorParams = errorParams;
        this.msg = formatMsg(msg, errorParams);
        return this;
    }

    public static String formatMsg(String msg, Object[] errorParams) {
        if (msg == null || errorParams == null || errorParams.length==0) {
            return msg;
        }
        msg = MessageFormat.format(msg, errorParams);
        return msg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ErrorCode errorCode = (ErrorCode) o;
        return code.equals(errorCode.code);
    }

    private String checkCode(String code) {
        if (code.trim().length() != 10) {
            throw new RuntimeException("错误代码必须为10位的数字");
        }
        Precondition.checkNumber(code, "错误代码必须为10位的数字");
        if(!code.startsWith(FRAMEWORK_APP_ID) && !code.startsWith(ConfigUtils.getProperty("dev-framework.appInfo.appId"))) {
            throw new RuntimeException("错误代码必须以应用id开始");
        }
        return code;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "ErrorCode{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
