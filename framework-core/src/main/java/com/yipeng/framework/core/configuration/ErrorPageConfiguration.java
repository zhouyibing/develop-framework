/*
package com.yipeng.framework.core.configuration;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

*/
/**
 * @author: yibingzhou
 *//*

@Configuration
public class ErrorPageConfiguration {

    @Bean
    public ErrorPageRegistrar errorPageRegistrar(){
        return new MyErrorPageRegistrar();
    }

    class MyErrorPageRegistrar implements ErrorPageRegistrar {
        @Override
        public void registerErrorPages(ErrorPageRegistry errorPageRegistry) {
            ErrorPage page401 = new ErrorPage(HttpStatus.UNAUTHORIZED, "/401.html");
            ErrorPage page404 = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
            ErrorPage page500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html");

            errorPageRegistry.addErrorPages(page401,page404, page500);
        }
    }
}
*/
