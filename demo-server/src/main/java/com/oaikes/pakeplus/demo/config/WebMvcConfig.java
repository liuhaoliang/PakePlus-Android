package com.oaikes.pakeplus.demo.config;

import com.oaikes.pakeplus.demo.tracking.InvocationTrackInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置：注册调用埋点拦截器，并放开演示页面的跨域访问。
 *
 * @author pakeplus
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final InvocationTrackInterceptor invocationTrackInterceptor;

    public WebMvcConfig(InvocationTrackInterceptor invocationTrackInterceptor) {
        this.invocationTrackInterceptor = invocationTrackInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(invocationTrackInterceptor).addPathPatterns("/api/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600L);
    }
}
