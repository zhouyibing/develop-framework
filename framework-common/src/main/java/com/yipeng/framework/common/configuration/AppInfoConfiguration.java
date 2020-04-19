package com.yipeng.framework.common.configuration;

import com.yipeng.framework.common.model.AppInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 应用基本信息
 * @author: yibingzhou
 */
@Configuration
public class AppInfoConfiguration {

    /** 应用/系统代码 3位数字*/
    @Value("${dev-framework.appInfo.systemId}")
    private String systemId;

    /** 业务模块/服务代码 3位数字**/
    @Value("${dev-framework.appInfo.serviceId}")
    private String serviceId;

    @Bean
    public AppInfo createAppInfo() {
        return AppInfo.builder().serviceId(serviceId).systemId(systemId).build();
    }
}
