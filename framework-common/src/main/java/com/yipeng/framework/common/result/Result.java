package com.yipeng.framework.common.result;

import com.yipeng.framework.common.exception.ErrorCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;

/**
 * 返回结果包装
 * @author: yibingzhou
 */
public class Result<T> {
    public final static String OK = "ok";
    public final static String CODE_OK = "200";
    @ApiModelProperty("响应代码")
    @Getter
    private String code = CODE_OK;
    @ApiModelProperty("响应消息")
    @Getter
    private String msg = OK;
    @ApiModelProperty("是否成功")
    @Getter
    /** 该次请求是否成功，没有异常*/
    private boolean success = true;
    @ApiModelProperty("响应数据")
    @Getter
    /** 该次请求返回结果，data的值不影响success*/
    private T data;

    private Result(String code, String msg) {
        this.code = code;
        this.msg = msg;
        this.success = false;
    }

    private Result(T data) {
        this.data = data;
    }

    public static Result fail(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMsg());
    }

    public static Result fail() {
        return new Result(ErrorCode.SERVER_INTERNAL_ERROR);
    }

    public static  Result success() {
        Result r = new Result(null);
        return r;
    }

    public static <D> Result<D> success(D data) {
        Result r = new Result<D>(data);
        return r;
    }
}
