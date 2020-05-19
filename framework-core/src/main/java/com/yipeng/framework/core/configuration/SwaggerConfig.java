package com.yipeng.framework.core.configuration;

import com.google.common.collect.Lists;
import com.yipeng.framework.core.api.BaseApi;
import com.yipeng.framework.core.dao.BaseDao;
import com.yipeng.framework.core.mapper.BaseMapper;
import com.yipeng.framework.core.model.biz.AppInfo;
import com.yipeng.framework.core.model.db.AccessObject;
import com.yipeng.framework.core.model.db.BaseModel;
import com.yipeng.framework.core.service.AppService;
import com.yipeng.framework.core.service.BaseService;
import com.yipeng.framework.core.utils.PathMatcherUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Configuration
@EnableSwagger2
@ConditionalOnBean(name = "appService")
public class SwaggerConfig extends WebMvcConfigurerAdapter {
    @Value("${swagger.basePackage:com.yipeng}")
    private String basePackage;
    @Value("${swagger.title:api接口文档}")
    private String title;
    @Value("${swagger.description:api接口}")
    private String description;
    @Value("${swagger.version:1.0}")
    private String version;
    @Value("${swagger.contact.name:}")
    private String contactName;
    @Value("${swagger.contact.url:}")
    private String contactUrl;
    @Value("${swagger.contact.email:}")
    private String contactEmail;
    @Value("${swagger.license:}")
    private String license;
    @Value("${swagger.licenseUrl:}")
    private String licenseUrl;
    @Value("${swagger.needTokenPaths:}")
    private String tokenPaths;
    @Value("${swagger.headers:}")
    private String headers;
    @Value("${dev-framework.api.ignorePaths:}")
    private String ignorePaths;

    @Autowired
    private AppService appService;

    @Bean
    public Docket createRestApi() {
        AppInfo appInfo = appService.getAppInfo();
        if(appInfo != null) {
            contactName = appInfo.getManager() == null ? contactName : appInfo.getManager();
            contactUrl = appInfo.getManagerMobile() == null ? contactUrl : appInfo.getManagerMobile();
            contactEmail = appInfo.getManagerEmail() == null ? contactEmail : appInfo.getManagerEmail();
        }
        ApiInfoBuilder apiInfoBuilder = new ApiInfoBuilder()
                .title(title)
                .description(description)
                .version(version)
                .contact(new Contact(contactName,contactUrl,contactEmail));
                if(StringUtils.isNotBlank(license)){
                    apiInfoBuilder.license(license);
                }
                if(StringUtils.isNotBlank(licenseUrl)){
                    apiInfoBuilder.license(licenseUrl);
                }
        String[] headerParams = StringUtils.isNoneBlank(headers) ? headers.split(",") : null;
        return new Docket(DocumentationType.SWAGGER_2)
                .pathMapping("/")
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .apis((input) -> {
                    if(input.getPatternsCondition() != null && input.getPatternsCondition().getPatterns() != null && StringUtils.isNotBlank(ignorePaths)) {
                        //过滤掉需要忽略的接口
                        String[] patterns = ignorePaths.split(",");
                        Set<String> paths = input.getPatternsCondition().getPatterns();
                        if(PathMatcherUtil.matchAny(patterns, paths)) {
                            return false;
                        }
                    }
                    return true;
                })
                .paths(PathSelectors.any())
                .build()
                .ignoredParameterTypes(BaseApi.class, BaseService.class, BaseDao.class, BaseMapper.class, BaseModel.class, AccessObject.class)
                .directModelSubstitute(Date.class,String.class)
                .securityContexts(securityContexts(headerParams, StringUtils.isNotBlank(tokenPaths) ? tokenPaths.split(",") : null))
                .securitySchemes(securitySchemes(headerParams))
                .apiInfo(apiInfoBuilder.build());
    }

    private List<ApiKey> securitySchemes(String[] headerParams) {
        List<ApiKey> apiKeys = Lists.newArrayList();
        if (headerParams == null) {
            return apiKeys;
        }
        for(String header : headerParams) {
            apiKeys.add(new ApiKey(header, header, "header"));
        }
        return apiKeys;
    }

    private List<SecurityContext> securityContexts(String[] headerParams, String[] needTokenPaths) {
        return Lists.newArrayList(
                SecurityContext.builder()
                        .securityReferences(defaultAuth(headerParams))
                        .forPaths(path -> {
                            if (null == needTokenPaths) {
                                return true;
                            }
                            if(PathMatcherUtil.matchAny(needTokenPaths, path)) {
                                return false;
                            }
                            return true;
                        })
                        .build()
        );
    }

    List<SecurityReference> defaultAuth(String[] headerParams) {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> securityReferences = new ArrayList<>();
        if (headerParams == null) {
            return securityReferences;
        }
        for (String header : headerParams) {
            securityReferences.add(new SecurityReference(header, authorizationScopes));
        }
        return securityReferences;
    }


    /**
     * 注册拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SwaggerIntercept()).addPathPatterns("**/v2/api-docs");
    }

    public class SwaggerIntercept implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
            return true;
        }

        @Override
        public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

        }

        @Override
        public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        }
    }
}