package com.pakeplus.demo.track;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

@DisplayName("UserIdentity 单元测试")
class UserIdentityTest {

    @Test
    @DisplayName("请求头缺少身份信息时按匿名访客归集")
    void should_returnAnonymousIdentity_when_headersMissing() {
        UserIdentity identity = UserIdentity.from(new MockHttpServletRequest());

        assertThat(identity.userId()).isEqualTo("anonymous");
        assertThat(identity.userName()).isEqualTo("匿名访客");
        assertThat(identity.userType()).isEqualTo("UNKNOWN");
        assertThat(identity.userLevel()).isEqualTo("UNKNOWN");
        assertThat(identity.userDept()).isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("URL 编码的中文请求头被正确解码")
    void should_decodeChineseHeader_when_valueUrlEncoded() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(UserIdentity.HEADER_USER_ID, "u1001");
        request.addHeader(UserIdentity.HEADER_USER_NAME, "%E5%BC%A0%E4%BC%9F");
        request.addHeader(UserIdentity.HEADER_USER_TYPE, "INTERNAL");
        request.addHeader(UserIdentity.HEADER_USER_LEVEL, "P7");
        request.addHeader(UserIdentity.HEADER_USER_DEPT, "%E6%8A%80%E6%9C%AF%E9%83%A8");

        UserIdentity identity = UserIdentity.from(request);

        assertThat(identity.userName()).isEqualTo("张伟");
        assertThat(identity.userDept()).isEqualTo("技术部");
    }

    @Test
    @DisplayName("非法编码内容原样保留，不抛异常")
    void should_keepRawValue_when_decodeFails() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(UserIdentity.HEADER_USER_NAME, "100%");

        assertThat(UserIdentity.from(request).userName()).isEqualTo("100%");
    }
}
