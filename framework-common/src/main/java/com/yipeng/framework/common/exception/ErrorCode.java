package com.yipeng.framework.common.exception;

import com.yipeng.framework.common.configuration.AppInfoConfiguration;
import com.yipeng.framework.common.model.AppInfo;
import com.yipeng.framework.common.utils.ConfigUtils;
import com.yipeng.framework.common.utils.Precondition;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.util.Objects;

/**
 * 错误代码
 * 从左到右第1~3位标识应用/系统代码，第4~6位标识业务模块/服务代码，后三位标识具体的错误代码
 * 应用/系统代码从AppInfo里提取，在集成框架时，强制让应用设置AppInfo（便于管理和问题追踪）
 * 错误代码尽量保证全局唯一性。便于后面定位问题
 * 如：微商城-订单-下单失败 0101001
 * 每个业务模块保证只有一个ErrorCode的子类，用于定义该模块内的所有错误代码
 * 业务异常代码，代码结尾为奇数
 * 奇数异常代码，代码结尾为偶数
 * @author: yibingzhou
 */
public class ErrorCode {

    /**技术异常代码，代码结尾为偶数*/
    public final static ErrorCode SERVER_INTERNAL_ERROR = new ErrorCode("0000", "服务器内部错误:{0#60}");
    public final static ErrorCode UNSUPPORTTED_TYPE = new ErrorCode("0002", "不支持的类型");
    public final static ErrorCode ILLEGAL_ARGUMENT = new ErrorCode("0004", "参数错误");
    public final static ErrorCode CAN_NOT_NULL = new ErrorCode("0006", "参数不能为空");
    public final static ErrorCode PARAM_TYPE_NOT_MATCH = new ErrorCode("0008", "参数类型不匹配");

    /***业务异常代码，代码结尾为奇数*/
    public final static ErrorCode BIZ_ERROR = new ErrorCode("0001", "业务异常");
    public final static ErrorCode QUERY_PARAMS_IS_NULL = new ErrorCode("0003", "查询参数为空");
    public final static ErrorCode RECORD_EXISTED = new ErrorCode("0005", "记录已存在");

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
        code = assembleCode(code);
        if(code.trim().length() != 10) throw new RuntimeException("错误代码必须为10位的数字");
        Precondition.checkNumber(code, "错误代码必须为10位的数字");
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
        if(msg == null || errorParams == null || errorParams.length==0) return msg;

        for (int i = 0; i < errorParams.length; i++) {
            int leftIx = msg.indexOf("{");
            int rightIx = msg.indexOf("}");
            String placeHolder = null;//参数占位符
            int maxLen = 50;//默认长度50
            if(leftIx>0 && rightIx >0) {
                placeHolder = msg.substring(leftIx,rightIx+1);
                //提取限定长度
                int lenStartIx = placeHolder.indexOf("#");
                if(lenStartIx > 0){
                    String len = placeHolder.substring(lenStartIx+1, placeHolder.length()-1);
                    try {
                        maxLen = Integer.valueOf(len);
                    } catch (NumberFormatException e){}
                }
            }
            String paramStr = String.valueOf(errorParams[i]);
            msg = msg.replace(placeHolder, paramStr.substring(0,maxLen > paramStr.length() ? paramStr.length() : maxLen));
        }
        return msg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorCode errorCode = (ErrorCode) o;
        return code.equals(errorCode.code);
    }

    private String assembleCode(String code) {
        return ConfigUtils.getProperty("dev-framework.appInfo.systemId").concat(ConfigUtils.getProperty("dev-framework.appInfo.serviceId")).concat(code);
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
