package com.yipeng.framework.core.web.valve;

import com.alibaba.fastjson.JSON;
import com.yipeng.framework.core.constants.Constants;
import com.yipeng.framework.core.constants.annotation.CheckAuth;
import com.yipeng.framework.core.constants.annotation.CheckToken;
import com.yipeng.framework.core.exception.ErrorCode;
import com.yipeng.framework.core.exception.ExceptionUtil;
import com.yipeng.framework.core.model.biz.CallContext;
import com.yipeng.framework.core.model.biz.ContextHolder;
import com.yipeng.framework.core.utils.PathMatcherUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * http请求日志打印，token验证,权限校验，链路追踪
 * @author: yibingzhou
 */
@Slf4j
@ControllerAdvice
@Component
public class HttpValve implements AbstractInterceptor, ResponseBodyAdvice {
    @Value("${dev-framework.api.timeout:100}")
    private long API_TIME_OUT;
    @Value("${dev-framework.api.debug:false}")
    private boolean DEBUG_ENABLE;
    @Value("${dev-framework.api.httpValue.pathPatterns:/**}")
    private String httpValuePathPatterns;
    private String[] httpValuePathPatternsArr;
    @Value("${dev-framework.api.httpValue.tokenPathPattern:}")
    private String tokenPathPatterns;
    private String[] tokenPathPatternsArr;
    @Value("${dev-framework.api.httpValue.authPathPattern:}")
    private String authPathPatterns;
    private String[] authPathPatternsArr;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        String traceId = request.getHeader(Constants.HEAD_TRACEID);
        String token = request.getHeader(Constants.HEAD_TOKEN);
        String appId = request.getHeader(Constants.HEAD_APPID);
        String uri = request.getRequestURI();
        if(StringUtils.isEmpty(traceId)) {
            traceId = ContextHolder.generateTraceId(appId);
        }
        MDC.put(Constants.HEAD_TRACEID, traceId);
        //token校验,并从token中解析出用户，存入callContext
        checkToken(uri, token, handler);
        //访问权限校验
        checkAuth(uri, token, handler);
        //从请求中获得traceId,没有则生成

        //设置traceId到当前请求上下文中CallContext
        CallContext callContext = new CallContext(traceId, token, appId);
        ContextHolder.setCallContext(callContext);
        //打印请求日志
        printLog(request);
        return true;
    }

    private void checkAuth(String uri, String token, Object handler) {
        if(authPathPatternsArr == null) {return;}
        //是否需要权限校验
        boolean pathMatch = PathMatcherUtil.matchAny(authPathPatternsArr, uri);
        CheckAuth checkAuth = ((HandlerMethod)handler).getMethodAnnotation(CheckAuth.class);
        //TODO
    }

    private void checkToken(String uri, String token, Object handler) {
        if(tokenPathPatternsArr == null) {return;}
        boolean tokenEmpty = StringUtils.isBlank(token);
        //是否需要校验token，根据路径配置判断和@CheckToken注解判断
        CheckToken checkToken = ((HandlerMethod)handler).getMethodAnnotation(CheckToken.class);
        if(checkToken != null && tokenEmpty) {
            throw ExceptionUtil.doThrow(ErrorCode.TOKEN_EMPTY);
        }
        boolean pathMatch = PathMatcherUtil.matchAny(tokenPathPatternsArr, uri);
        if(pathMatch && tokenEmpty) {
            throw ExceptionUtil.doThrow(ErrorCode.TOKEN_EMPTY);
        }
    }

    private void printLog(HttpServletRequest request) {
        if(DEBUG_ENABLE) {
            log.info("调用路径:{}, 请求参数:{}", request.getRequestURI(), request.getParameterMap() == null ? "" : JSON.toJSONString(request.getParameterMap()));
        }
    }

    private void printLog(ServerHttpRequest request, Object obj) {
        if(DEBUG_ENABLE && PathMatcherUtil.matchAny(httpValuePathPatternsArr, request.getURI().getPath())) {
            log.info("调用路径:{}, 返回结果:{}",request.getURI().getPath(), obj == null ? "" : JSON.toJSONString(obj));
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        CallContext callContext = ContextHolder.getCallContext();
        long elapsedTime = System.currentTimeMillis() - callContext.getStartTime();
        if(elapsedTime > API_TIME_OUT) {
            log.warn("'{}' 调用超时:{} ms", httpServletRequest.getRequestURI(), elapsedTime);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        //移除callContext
        ContextHolder.removeCallContext();
        MDC.remove(Constants.HEAD_TRACEID);
    }

    @Override
    public String[] pathPatterns() {
        httpValuePathPatternsArr = httpValuePathPatterns.split(",");
        int len = httpValuePathPatternsArr.length;
        if(StringUtils.isNotBlank(tokenPathPatterns)) {
            tokenPathPatternsArr = tokenPathPatterns.split(",");
            len += tokenPathPatternsArr.length;
        }
        if(StringUtils.isNotBlank(authPathPatterns)) {
            authPathPatternsArr = authPathPatterns.split(",");
            len += authPathPatternsArr.length;
        }
        String[] pathPatterns = new String[len];
        System.arraycopy(httpValuePathPatternsArr, 0, pathPatterns, 0, httpValuePathPatternsArr.length);
        if(tokenPathPatternsArr != null) {
            System.arraycopy(tokenPathPatternsArr, 0, pathPatterns, httpValuePathPatternsArr.length, tokenPathPatternsArr.length);
        }
        if(authPathPatternsArr != null) {
            System.arraycopy(authPathPatternsArr, 0, pathPatterns, tokenPathPatternsArr.length, authPathPatternsArr.length);
        }
        httpValuePathPatternsArr = pathPatterns;
        return pathPatterns;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        printLog(request, body);
        return body;
    }
}
