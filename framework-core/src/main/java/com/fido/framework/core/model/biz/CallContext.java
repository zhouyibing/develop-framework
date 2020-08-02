package com.fido.framework.core.model.biz;

import lombok.Data;

/**
 * 请求调用上下文
 * @author: yibingzhou
 */
@Data
public class CallContext {
    /** 请求id */
    private String traceId;

    /** 请求用户的token 可选*/
    private String token;

    /** 请求方应用id*/
    private String appId;

    /** 请求开始时间*/
    private long startTime = System.currentTimeMillis();

    public CallContext(String traceId, String token, String appId) {
        this.traceId = traceId;
        this.token = token;
        this.appId = appId;
    }
}
