/*
package com.yipeng.framework.core.web.valve;
import com.yipeng.framework.core.constants.Constants;
import com.yipeng.framework.core.model.biz.CallContext;
import com.yipeng.framework.core.model.biz.ContextHolder;
import com.yipeng.framework.core.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

*/
/**
 * @author: yibingzhou
 *//*

@Slf4j
public class RequestTraceInteceptor implements HandlerInterceptor {

    private long API_TIME_OUT = ConfigUtils.getLong("dev-framework.api.timeout", 100L);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        String traceId = request.getHeader(Constants.HEAD_TRACEID);
        String token = request.getHeader(Constants.HEAD_TOKEN);
        String appId = request.getHeader(Constants.HEAD_APPID);
        //从请求中获得traceId,没有则生成
        if(StringUtils.isEmpty(traceId)) {
            traceId = ContextHolder.generateTraceId(appId);
        }
        //设置traceId到当前请求上下文中CallContext
        CallContext callContext = new CallContext(traceId, token, appId);
        ContextHolder.setCallContext(callContext);
        return true;
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
    }
}
*/
