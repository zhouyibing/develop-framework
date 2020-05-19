/*
package com.yipeng.framework.core.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

*/
/**
 * 跨域访问设置
 *//*

@Configuration
@Slf4j
public class CorsConfig {

    @ConditionalOnProperty(name = "dev-framework.allowedCrossDomain", havingValue = "true")
    @Bean
    public CorsFilter corsFilter(){
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedHeader("*");
        config.addAllowedHeader("X-Forwarded-Prefix");
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.setMaxAge(10000L);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @ConditionalOnProperty(name = "dev-framework.allowedCrossDomain", havingValue = "true")
    @Bean
    public Filter createCrosFilter() {
       return new Filter(){
            @Override
            public void doFilter(ServletRequest req, ServletResponse res,
                                 FilterChain chain) throws IOException, ServletException {
                HttpServletResponse response = (HttpServletResponse) res;
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Methods",
                        "POST, GET, OPTIONS, DELETE, PUT");
                response.setHeader("Access-Control-Max-Age", "3600");
                response.setHeader("Access-Control-Allow-Headers", "x-requested-with,Origin,Content-Type, Accept, X-Forwarded-Prefix");
                chain.doFilter(req, res);
            }

            @Override
            public void init(FilterConfig filterConfig) {
            }

            @Override
            public void destroy() {
            }
        };
    }
}*/
