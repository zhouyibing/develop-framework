package com.yipeng.framework.core.web.handler;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author: yibingzhou
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UrlPrefixConfiguration implements WebMvcRegistrations {
    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new UrlPrefixRequestMappingHandlerMapping();
    }
}
