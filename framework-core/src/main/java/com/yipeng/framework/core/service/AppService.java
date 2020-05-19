package com.yipeng.framework.core.service;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yipeng.framework.core.constants.Constants;
import com.yipeng.framework.core.exception.ErrorCode;
import com.yipeng.framework.core.exception.ExceptionUtil;
import com.yipeng.framework.core.model.biz.AppInfo;
import com.yipeng.framework.core.model.biz.ContextHolder;
import com.yipeng.framework.core.model.biz.ServerInfo;
import com.yipeng.framework.core.utils.AsyncHelper;
import com.yipeng.framework.core.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
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

    @Autowired
    private AsyncHelper asyncHelper;

    @Nullable
    public AppInfo getAppInfo() {
        if(notConnenctToAppInfoServer) {
            return null;
        }
        if(ContextHolder.getAppInfo() == null) {
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("appId", appId);
            String result = null;
            try {
                result = HttpUtil.createGet(appInfoUrl).form(paramMap).execute().body();
            }catch (Exception e) {
                log.error("", e);
                //连接异常，后面5s异步重试
                asyncHelper.asyncDelay("getAppInfo", () -> getAppInfo(), 30000);
            }
            if(result == null) {
                return null;
            }
            JSONObject jsonObject = JSON.parseObject(result);
            Boolean success = jsonObject.getBoolean("success");
            if (success == null || !success) {
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
                appInfo.setManager(data.getString("manager"));
                appInfo.setManagerEmail(data.getString("managerEmail"));
                appInfo.setManagerMobile(data.getString("managerMobile"));
                appInfo.setCandidate(data.getString("candidate"));
                appInfo.setCandidateEmail(data.getString("candidateEmail"));
                appInfo.setCandidateMobile(data.getString("candidateMobile"));
                log.info("get appInfo:{}", appInfo);
                ContextHolder.setAppInfo(appInfo);
            }
        }
        return ContextHolder.getAppInfo();
    }

    /**
     * 启动5s后，每隔30秒ping一下应用管理服务
     */
    @Scheduled(initialDelayString = "${dev-framework.appInfo.pingInitial:5000}", fixedDelayString = "${dev-framework.appInfo.pingInterval:30000}")
    public void ping() {
        handshake(pingUrl);
    }

    public void disconnect() {
        handshake(disconnectUrl);
    }

    private void handshake(String url) {
        if(notConnenctToAppInfoServer) {
            return;
        }
        ServerInfo serverInfo = ContextHolder.getServerInfo();
        if(serverInfo != null) {
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("appId", appId);
            paramMap.put("ip", serverInfo.getIp());
            paramMap.put("port", serverInfo.getPort());
            String result = HttpUtil.createGet(url).form(paramMap).execute().body();
            if (log.isDebugEnabled()) {
                log.debug("handshake [{}], result={}", url, result);
            }
        }
    }
}
