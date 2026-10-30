package com.oaikes.pakeplus.demo.service.impl;

import com.oaikes.pakeplus.demo.model.vo.HelloWorldVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link HelloWorldServiceImpl} 单元测试。
 *
 * @author pakeplus
 */
class HelloWorldServiceImplTest {

    private final HelloWorldServiceImpl helloWorldService = new HelloWorldServiceImpl();

    @Test
    @DisplayName("未传称呼时使用默认 World")
    void should_returnDefaultGreeting_when_nameAbsent() {
        HelloWorldVO helloWorldVO = helloWorldService.getGreeting(null);

        assertThat(helloWorldVO.message()).isEqualTo("Hello, World!");
        assertThat(helloWorldVO.serverTime()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
    }

    @Test
    @DisplayName("传入称呼时拼接问候语并去除首尾空格")
    void should_returnCustomGreeting_when_nameProvided() {
        HelloWorldVO helloWorldVO = helloWorldService.getGreeting("  PakePlus  ");

        assertThat(helloWorldVO.message()).isEqualTo("Hello, PakePlus!");
    }

    @Test
    @DisplayName("称呼为空白字符时回退默认称呼")
    void should_returnDefaultGreeting_when_nameBlank() {
        HelloWorldVO helloWorldVO = helloWorldService.getGreeting("   ");

        assertThat(helloWorldVO.message()).isEqualTo("Hello, World!");
    }
}
