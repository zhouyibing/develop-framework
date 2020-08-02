package com.fido.framework.core.configuration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.google.common.collect.Lists;
import com.fido.framework.core.api.BaseApi;
import com.fido.framework.core.dao.BaseDao;
import com.fido.framework.core.mapper.BaseMapper;
import com.fido.framework.core.model.biz.AppInfo;
import com.fido.framework.core.model.biz.ModelPropertyFilter;
import com.fido.framework.core.model.db.AccessObject;
import com.fido.framework.core.model.db.BaseModel;
import com.fido.framework.core.service.AppService;
import com.fido.framework.core.service.BaseService;
import com.fido.framework.core.utils.PathMatcherUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import java.util.*;

@Configuration
@EnableSwagger2
@ConditionalOnBean(AppService.class)
public class SwaggerConfig {
    @Value("${swagger.basePackage:com.fido}")
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

    @ControllerAdvice
    @ConditionalOnBean(ModelPropertyFilter.class)
    public class SwaggerIntercept implements ResponseBodyAdvice {

        @Autowired
        private ModelPropertyFilter modelPropertyFilter;

        private String apiDocsPath = "/**/v2/api-docs";

        @Override
        public boolean supports(MethodParameter returnType, Class converterType) {
            return true;
        }

        @Override
        public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
            if(PathMatcherUtil.match(apiDocsPath, request.getURI().getPath()) && body instanceof Json) {
                JSONObject docsObj = JSON.parseObject(((Json) body).value(), Feature.DisableCircularReferenceDetect);
                JSONObject paths = docsObj.getJSONObject("paths");
                JSONObject definitions = docsObj.getJSONObject("definitions");
                paths.forEach( (path,v) -> {
                    //循环paths
                    ((JSONObject)v).forEach((kk,vv) -> {
                        JSONArray parameters = ((JSONObject)vv).getJSONArray("parameters");
                        //根据参数位置，找到对应的过滤配置
                        for(int i = 0; i < parameters.size(); i++) {
                            ModelPropertyFilter.FilterParam filterParam = modelPropertyFilter.getFilterParam(path, i);
                            if(filterParam == null) {continue;}
                            filterModel(filterParam.getFields(), definitions, parameters.getJSONObject(i));
                        }
                    });
                });

                //移除没有properties的model
                if(definitions != null) {
                    List<String> removeKeys = Lists.newArrayList();
                    for(Map.Entry<String, Object> entry : definitions.entrySet()) {
                        Object value = entry.getValue();
                        JSONObject properties = ((JSONObject)value).getJSONObject("properties");
                        if(properties == null || properties.isEmpty()) {
                            removeKeys.add(entry.getKey());
                        }
                    }
                    removeKeys.forEach(k -> definitions.remove(k));
                }
                return docsObj;
            }
            return body;
        }

        private void filterModel(Set<String> filterFileds, JSONObject definitions, JSONObject param) {
            JSONObject schema = param.getJSONObject("schema");
            String ref = schema.getString("$ref");
            if(StringUtils.isNotBlank(ref)) {
                String[] modelDef = ref.split("definitions/");
                if(modelDef.length < 2) {return;}
                JSONObject model = definitions.getJSONObject(modelDef[1]);
                JSONObject copyModel = JSONObject.parseObject(model.toJSONString(), Feature.DisableCircularReferenceDetect);
                JSONObject properties = copyModel.getJSONObject("properties");
                filterFileds.forEach(filterFiled -> properties.remove(filterFiled));
                param.put("schema", copyModel);
            }
        }
    }
}