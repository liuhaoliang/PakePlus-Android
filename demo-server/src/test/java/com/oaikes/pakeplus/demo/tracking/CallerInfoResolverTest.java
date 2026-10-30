package com.oaikes.pakeplus.demo.tracking;

import com.oaikes.pakeplus.demo.model.dto.CallerInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link CallerInfoResolver} 单元测试：覆盖请求头字符集兼容场景。
 *
 * @author pakeplus
 */
class CallerInfoResolverTest {

    @Test
    @DisplayName("ASCII 请求头原样解析")
    void should_keepAscii_when_headerAscii() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/hash");
        request.addHeader("X-User-Id", "u100");
        request.addHeader("X-User-Name", "tom");
        request.addHeader("X-User-Type", "INTERNAL");
        request.addHeader("X-User-Level", "P6");
        request.addHeader("X-User-Dept", "finance");

        CallerInfo callerInfo = CallerInfoResolver.resolve(request);

        assertThat(callerInfo.callerId()).isEqualTo("u100");
        assertThat(callerInfo.callerName()).isEqualTo("tom");
        assertThat(callerInfo.userDept()).isEqualTo("finance");
    }

    @Test
    @DisplayName("URL 编码的中文请求头解码为可读文本")
    void should_decodePercentEncoded_when_headerUrlEncoded() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/hash");
        request.addHeader("X-User-Name", "%E5%BC%A0%E4%B8%89");
        request.addHeader("X-User-Dept", "%E6%8A%80%E6%9C%AF%E9%83%A8");

        CallerInfo callerInfo = CallerInfoResolver.resolve(request);

        assertThat(callerInfo.callerName()).isEqualTo("张三");
        assertThat(callerInfo.userDept()).isEqualTo("技术部");
    }

    @Test
    @DisplayName("按 ISO-8859-1 解码的 UTF-8 字节序列还原为中文")
    void should_restoreUtf8_when_headerDecodedAsIso8859() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/hash");
        request.addHeader("X-User-Dept", new String("财务部".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.ISO_8859_1));

        CallerInfo callerInfo = CallerInfoResolver.resolve(request);

        assertThat(callerInfo.userDept()).isEqualTo("财务部");
    }

    @Test
    @DisplayName("缺少请求头时返回默认值")
    void should_returnDefaults_when_headersAbsent() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/hash");

        CallerInfo callerInfo = CallerInfoResolver.resolve(request);

        assertThat(callerInfo.callerId()).isEqualTo("anonymous");
        assertThat(callerInfo.callerName()).isEqualTo("anonymous");
        assertThat(callerInfo.userType()).isEqualTo("UNKNOWN");
        assertThat(callerInfo.userLevel()).isEqualTo("UNKNOWN");
        assertThat(callerInfo.userDept()).isEqualTo("UNKNOWN");
    }
}
