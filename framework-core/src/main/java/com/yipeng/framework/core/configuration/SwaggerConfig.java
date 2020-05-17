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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.*;

@Configuration
@EnableSwagger2
@ConditionalOnBean(name = "appService")
public class SwaggerConfig{
    @Value("${swagger.basePackage:com.yipeng}")
    private String basePackage;
    @Value("${swagger.title:api接口文档}")
    private String title;
    @Value("${swagger.description:api接口}")
    private String description;
    @Value("${swagger.version:1.0}")
    private String version;
    @Value("${swagger.contact.name:''}")
    private String contactName;
    @Value("${swagger.contact.url:''}")
    private String contactUrl;
    @Value("${swagger.contact.email:''}")
    private String contactEmail;
    @Value("${swagger.license:''}")
    private String license;
    @Value("${swagger.licenseUrl:''}")
    private String licenseUrl;
    @Value("${swagger.needTokenPaths:''}")
    private String tokenPaths;
    @Value("${swagger.headers:''}")
    private String headers;
    @Value("${dev-framework.api.ignorePaths:''}")
    private String ignorePaths;
    PathMatcher pathMatcher = new AntPathMatcher();

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
                        String[] paths = ignorePaths.split(",");
                        Set<String> patterns = input.getPatternsCondition().getPatterns();
                        for (String path : paths){
                            for(String p:patterns) {
                                if(pathMatcher.match(path,p)){
                                    return false;
                                }
                            }
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
                            for(String p : needTokenPaths) {
                                if (pathMatcher.match(p,path)) {
                                    return false;
                                }
                            }
                            return true;
                        })
                        .build()
        );
    }

    @Bean
    public UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .deepLinking(true)
                .displayOperationId(false)
                // 隐藏UI上的Models模块
                .defaultModelsExpandDepth(-1)
                .defaultModelExpandDepth(0)
                .defaultModelRendering(ModelRendering.EXAMPLE)
                .displayRequestDuration(false)
                .docExpansion(DocExpansion.NONE)
                .filter(false)
                .maxDisplayedTags(null)
                .operationsSorter(OperationsSorter.ALPHA)
                .showExtensions(false)
                .tagsSorter(TagsSorter.ALPHA)
                .validatorUrl(null)
                .build();
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
}