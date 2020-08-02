package com.fido.framework.core.web.listener;

import com.fido.framework.core.model.biz.ContextHolder;
import com.fido.framework.core.model.biz.ServerInfo;
import com.fido.framework.core.service.AppService;
import com.fido.framework.core.utils.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 启动listener
 * 启动后去应用服务里获取应用信息
 * @author: yibingzhou
 */
@Component
@Slf4j
public class StartupListener implements ApplicationListener<ApplicationEvent> {

    @Autowired
    private AppService appInfoService;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            //去获取应用信息
            log.info("app info:{}", appInfoService.getAppInfo());
        } else if(event instanceof WebServerInitializedEvent) {
            int port = ((WebServerInitializedEvent)event).getWebServer().getPort();
            String ip = IPUtil.getRealIp();
            ServerInfo serverInfo = new ServerInfo();
            serverInfo.setIp(ip);
            serverInfo.setPort(port);
            log.info("server info:{}", serverInfo);
            ContextHolder.setServerInfo(serverInfo);
        } else if(event instanceof ContextClosedEvent) {
            appInfoService.disconnect();
        }
    }
}
