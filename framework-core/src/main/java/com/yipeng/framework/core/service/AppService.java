package com.yipeng.framework.core.service;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yipeng.framework.core.exception.ErrorCode;
import com.yipeng.framework.core.exception.ExceptionUtil;
import com.yipeng.framework.core.model.biz.AppInfo;
import com.yipeng.framework.core.model.biz.ServerInfo;
import com.yipeng.framework.core.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * 应用信息交互
 * @author: yibingzhou
 */
@Service
@Slf4j
public class AppService {

    @Value("${dev-framework.appInfo.appId}")
    private String appId;
    @Value("${dev-framework.appInfo.infoUrl}")
    private String appInfoUrl;
    @Value("${dev-framework.appInfo.pingUrl}")
    private String pingUrl;
    @Value("${dev-framework.appInfo.disconnectUrl}")
    private String disconnectUrl;
    @Value("${dev-framework.appInfo.notConnenctToAppInfoServer:false}")
    private boolean notConnenctToAppInfoServer;

    private AppInfo appInfo;
    private ServerInfo serverInfo;

    public void setServerInfo(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    public AppInfo getAppInfo() {
        if(notConnenctToAppInfoServer) {
            return null;
        }
        if(appInfo == null) {
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("appId", appId);
            String result = HttpUtil.get(appInfoUrl, paramMap);
            JSONObject jsonObject = JSON.parseObject(result);
            Boolean success = jsonObject.getBoolean("success");
            if(success == null || !success) {
                String code = jsonObject.getString("code");
                String msg = jsonObject.getString("msg");
                throw ExceptionUtil.doThrow(new ErrorCode(code, msg));
            } else {
                JSONObject data = jsonObject.getJSONObject("data");
                AppInfo appInfo = new AppInfo();
                appInfo.setAppId(data.getString("appId"));
                appInfo.setSystemId(data.getString("systemId"));
                appInfo.setSystemName(data.getString("systemName"));
                appInfo.setServiceId(data.getString("serviceId"));
                appInfo.setServiceName(data.getString("serviceName"));
                log.info("get appInfo:{}", appInfo);
                this.appInfo = appInfo;
            }
        }
        return appInfo;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }
    /**
     * 启动5s后，每隔30秒ping一下应用管理服务
     */
    @Scheduled(initialDelayString = "${dev-framework.appInfo.pingInitial:5000}", fixedDelayString = "${dev-framework.appInfo.pingInterval:30000}")
    public void ping() {
        if(notConnenctToAppInfoServer) {
            return;
        }
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("appId", appId);
        paramMap.put("ip", serverInfo.getIp());
        paramMap.put("port", serverInfo.getPort());
        String result = HttpUtil.get(pingUrl, paramMap);
        if(log.isDebugEnabled()) {
            log.debug("ping [{}], result={}", pingUrl, result);
        }
    }

    public void disconnect() {
        if(notConnenctToAppInfoServer) {
            return;
        }
        if(serverInfo != null) {
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("appId", appId);
            paramMap.put("ip", serverInfo.getIp());
            paramMap.put("port", serverInfo.getPort());
            String result = HttpUtil.get(disconnectUrl, paramMap);
            if (log.isDebugEnabled()) {
                log.debug("disconnected [{}], result={}", disconnectUrl, result);
            }
        }
    }
}
