package com.yipeng.framework.common.model;

import com.yipeng.framework.common.utils.Precondition;
import lombok.Getter;

/**
 * @author: yibingzhou
 */
public final class AppInfo {
    /** 应用/系统代码 3位数字*/
    @Getter
    private String systemId;

    /** 业务模块/服务代码 3位数字**/
    @Getter
    private String serviceId;

    private AppInfo(String systemId, String serviceId) {
        this.systemId = systemId;
        this.serviceId = serviceId;
    }

    public String appId(){
        return this.systemId.concat(this.serviceId);
    }

    public static AppInfoBuilder builder(){
        return new AppInfoBuilder();
    }

    public static class AppInfoBuilder{
        private String systemId;
        private String serviceId;

        private AppInfoBuilder() {
        }
        public AppInfoBuilder serviceId(String serviceId) {
            if(serviceId.trim().length() != 3) throw new RuntimeException("业务模块/服务代码必须为3位的数字");
            Precondition.checkNumber(serviceId, "业务模块/服务代码必须为3位的数字");
            this.serviceId = serviceId;
            return this;
        }
        public AppInfoBuilder systemId(String systemId) {
            if(systemId.trim().length() != 3) throw new RuntimeException("应用/系统代码必须为3位的数字");
            Precondition.checkNumber(systemId, "应用/系统代码必须为3位的数字");
            this.systemId = systemId;
            return this;
        }
        public AppInfo build(){
            return new AppInfo(systemId, serviceId);
        }
    }
}
