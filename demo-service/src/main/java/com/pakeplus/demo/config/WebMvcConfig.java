package com.pakeplus.demo.config;

import com.pakeplus.demo.track.TrackInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置：跨域放行移动端 WebView 页面，并注册埋点拦截器。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final long CORS_MAX_AGE_SECONDS = 3600L;

    private final TrackInterceptor trackInterceptor;

    public WebMvcConfig(TrackInterceptor trackInterceptor) {
        this.trackInterceptor = trackInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(CORS_MAX_AGE_SECONDS);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(trackInterceptor)
                .addPathPatterns("/api/demo/hello", "/api/demo/hash", "/api/demo/sort");
    }
}
