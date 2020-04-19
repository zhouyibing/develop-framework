package com.yipeng.framework.common.web.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 在spring启动后移除不需要的接口
 * 使用者需要在配置文件里配置哪些接口path不需要
 * @author: yibingzhou
 */
@Component
public class UnregisterBaseRequestMapping implements ApplicationListener<ContextRefreshedEvent> {
    @Value("${dev-framework.api.ignorePaths:''}")
    private String ignorePaths;

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(StringUtils.isNotBlank(ignorePaths)) {
            Map<RequestMappingInfo, HandlerMethod> maps = requestMappingHandlerMapping.getHandlerMethods();
            final String[] paths = ignorePaths.split(",");
            PathMatcher pathMatcher = new AntPathMatcher();
            Set<RequestMappingInfo> needRemoved = new HashSet<>();
            for (RequestMappingInfo info : maps.keySet()) {
                Set<String> patterns = info.getPatternsCondition().getPatterns();

                point:
                for (String path : paths){
                    for(String p:patterns) {
                        if(pathMatcher.match(path,p)){
                            needRemoved.add(info);
                            break point;
                        }
                    }
                }
            }
            //移除mapping
            needRemoved.forEach(info -> requestMappingHandlerMapping.unregisterMapping(info));
        }
    }
}
