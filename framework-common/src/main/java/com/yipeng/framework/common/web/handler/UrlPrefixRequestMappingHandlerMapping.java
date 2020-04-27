package com.yipeng.framework.common.web.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UrlPrefixRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    @Value("${dev-framework.api.classPathUrlPrefix:''}")
    private String classPathUrlPrefix;

    private List<ClassPathUrlPrefixItem> classPathUrlPrefixItemList = new ArrayList<>();

    PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void afterPropertiesSet() {
        if(StringUtils.isNotBlank(classPathUrlPrefix)) {
            String[] classPathPrefix = classPathUrlPrefix.split(";");
            for(String pathAndPrefix : classPathPrefix) {
                String[] pp = pathAndPrefix.split("@");
                if(pp.length != 2){
                    throw new IllegalArgumentException("illegal configuration["+pathAndPrefix+"] defined in 'dev-framework.api.classPathUrlPrefix'");
                }
                ClassPathUrlPrefixItem classPathUrlPrefixItem = new ClassPathUrlPrefixItem(pp[0], pp[1]);
                classPathUrlPrefixItemList.add(classPathUrlPrefixItem);
            }
        }
        super.afterPropertiesSet();
    }

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
        String urlPrefix = "";
        if (info != null) {
            for(ClassPathUrlPrefixItem item : classPathUrlPrefixItemList) {
                if(isPattern(handlerType, item.classPath)) {
                    urlPrefix+=item.urlPrefix;
                    break;
                }
            }
            return combineRequestMappingInfo(info, urlPrefix);
        }
        return info;
    }

    public boolean isPattern(Class<?> clazz, String definedClassPath) {
        return clazz != null && pathMatcher.match(definedClassPath, clazz.getName()/*clazz.getName().replaceAll("\\.","/")*/);
    }

    public RequestMappingInfo combineRequestMappingInfo(RequestMappingInfo original, String prefix) {
        if (StringUtils.isBlank(prefix)) {
            return original;
        }

        RequestMappingInfo requestMappingInfo = RequestMappingInfo.paths(prefix).build().combine(original);
        if(log.isInfoEnabled()){
            log.info("will change {} onto {};",original,requestMappingInfo);
        }
        return requestMappingInfo;
    }

    class ClassPathUrlPrefixItem {
        private String classPath;
        private String urlPrefix;

        public ClassPathUrlPrefixItem(String classPath, String urlPrefix) {
            this.classPath = classPath;
            this.urlPrefix = urlPrefix;
        }

        public String getClassPath() {
            return classPath;
        }

        public String getUrlPrefix() {
            return urlPrefix;
        }
    }
}