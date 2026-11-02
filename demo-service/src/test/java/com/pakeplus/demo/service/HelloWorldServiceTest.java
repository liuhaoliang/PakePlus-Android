package com.pakeplus.demo.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HelloWorldService 单元测试")
class HelloWorldServiceTest {

    private final HelloWorldService helloWorldService = new HelloWorldService();

    @Test
    @DisplayName("传入名称时返回个性化问候")
    void should_returnGreeting_when_nameProvided() {
        String greeting = helloWorldService.greet("PakePlus");

        assertThat(greeting).isEqualTo("Hello, PakePlus!");
    }

    @Test
    @DisplayName("名称为空时使用默认名称")
    void should_returnDefaultGreeting_when_nameBlank() {
        assertThat(helloWorldService.greet(null)).isEqualTo("Hello, World!");
        assertThat(helloWorldService.greet("   ")).isEqualTo("Hello, World!");
    }

    @Test
    @DisplayName("服务器时间格式为 yyyy-MM-dd HH:mm:ss")
    void should_returnFormattedTime_when_currentTimeCalled() {
        String currentTime = helloWorldService.currentTime();

        assertThat(currentTime).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
    }
}
