package com.yipeng.framework.core.model.biz;

import cn.hutool.core.lang.UUID;

/**
 * 应用上下文保管者
 * @author: yibingzhou
 */
public class ContextHolder {
    /** 调用上下文 */
    private static ThreadLocal<CallContext> callContextHolder = new ThreadLocal<>();
    /** 应用信息 */
    private static AppInfo appInfo;
    /** 服务器信息*/
    private static ServerInfo serverInfo;


    public static void setCallContext(CallContext callContext) {
        if (callContext == null) {
            callContextHolder.remove();
        } else {
            callContextHolder.set(callContext);
        }
    }

    public static CallContext getCallContext() {
        return callContextHolder.get();
    }

    public static void removeCallContext() {
        callContextHolder.remove();
    }

    public synchronized static void setAppInfo(AppInfo info) {
        appInfo = info;
    }

    public static AppInfo getAppInfo() {
        return appInfo;
    }

    public synchronized static void setServerInfo(ServerInfo info) {
        serverInfo = info;
    }

    public static ServerInfo getServerInfo() {
        return serverInfo;
    }

    /**
     * 生成以appId开头的traceId
     * @param appId
     * @return
     */
    public static String generateTraceId(String appId) {
        if(appId == null && appInfo != null && appInfo.getAppId() != null) {
            appId = appInfo.getAppId();
        }
        String uuid = UUID.fastUUID().toString().replaceAll("-", "");
        return null == appId ? uuid : appId+uuid.substring(appId.length());
    }
}
