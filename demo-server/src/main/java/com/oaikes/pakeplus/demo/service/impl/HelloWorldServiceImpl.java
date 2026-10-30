package com.oaikes.pakeplus.demo.service.impl;

import com.oaikes.pakeplus.demo.constant.DemoConstants;
import com.oaikes.pakeplus.demo.model.vo.HelloWorldVO;
import com.oaikes.pakeplus.demo.service.HelloWorldService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * hello world 服务实现。
 *
 * @author pakeplus
 */
@Service
public class HelloWorldServiceImpl implements HelloWorldService {

    /** 默认称呼。 */
    private static final String DEFAULT_NAME = "World";

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(DemoConstants.DATE_TIME_PATTERN);

    @Override
    public HelloWorldVO getGreeting(String name) {
        String effectiveName = (name == null || name.isBlank()) ? DEFAULT_NAME : name.trim();
        String message = "Hello, " + effectiveName + "!";
        return new HelloWorldVO(message, LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }
}
