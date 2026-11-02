package com.pakeplus.demo.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

/**
 * Hello World 示例服务。
 */
@Service
public class HelloWorldService {

    private static final String DEFAULT_NAME = "World";
    private static final String GREETING_TEMPLATE = "Hello, %s!";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 生成问候语。
     *
     * @param name 调用方名称，为空时使用默认名称
     * @return 问候语
     */
    public String greet(String name) {
        String targetName = (name == null || name.isBlank()) ? DEFAULT_NAME : name.trim();
        return String.format(GREETING_TEMPLATE, targetName);
    }

    /**
     * 当前服务器时间，格式 yyyy-MM-dd HH:mm:ss。
     *
     * @return 当前时间字符串
     */
    public String currentTime() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }
}
