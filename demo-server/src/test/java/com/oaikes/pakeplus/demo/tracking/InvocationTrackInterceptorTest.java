package com.oaikes.pakeplus.demo.tracking;

import com.oaikes.pakeplus.demo.model.dto.InvocationTrackCommand;
import com.oaikes.pakeplus.demo.service.InvocationTrackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * {@link InvocationTrackInterceptor} 单元测试。
 *
 * @author pakeplus
 */
@ExtendWith(MockitoExtension.class)
class InvocationTrackInterceptorTest {

    @Mock
    private InvocationTrackService invocationTrackService;

    private InvocationTrackInterceptor interceptor;

    private HandlerMethod handlerMethod;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        interceptor = new InvocationTrackInterceptor(invocationTrackService);
        handlerMethod = new HandlerMethod(new Object(), Object.class.getMethod("hashCode"));
    }

    @Test
    @DisplayName("按请求头记录调用人及人员属性并归类接口")
    void should_saveInvocation_when_requestMatched() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/hash");
        request.addHeader("X-User-Id", "10086");
        request.addHeader("X-User-Name", "张三");
        request.addHeader("X-User-Type", "INTERNAL");
        request.addHeader("X-User-Level", "P6");
        request.addHeader("X-User-Dept", "技术部");
        MockHttpServletResponse response = new MockHttpServletResponse();

        interceptor.preHandle(request, response, handlerMethod);
        request.setAttribute(InvocationTrackInterceptor.ATTR_RESULT_SUMMARY, "sha-256: abc");
        interceptor.afterCompletion(request, response, handlerMethod, null);

        ArgumentCaptor<InvocationTrackCommand> captor = ArgumentCaptor.forClass(InvocationTrackCommand.class);
        verify(invocationTrackService).saveInvocation(captor.capture());
        InvocationTrackCommand command = captor.getValue();
        assertThat(command.apiCode()).isEqualTo("hash");
        assertThat(command.apiName()).isEqualTo("哈希算法");
        assertThat(command.callerId()).isEqualTo("10086");
        assertThat(command.callerName()).isEqualTo("张三");
        assertThat(command.userType()).isEqualTo("INTERNAL");
        assertThat(command.userLevel()).isEqualTo("P6");
        assertThat(command.userDept()).isEqualTo("技术部");
        assertThat(command.success()).isTrue();
        assertThat(command.resultSummary()).isEqualTo("sha-256: abc");
        assertThat(command.costMillis()).isGreaterThanOrEqualTo(0L);
    }

    @Test
    @DisplayName("缺少调用人请求头时使用匿名兜底值")
    void should_useAnonymousCaller_when_headersAbsent() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/hello-world");
        MockHttpServletResponse response = new MockHttpServletResponse();

        interceptor.preHandle(request, response, handlerMethod);
        interceptor.afterCompletion(request, response, handlerMethod, null);

        ArgumentCaptor<InvocationTrackCommand> captor = ArgumentCaptor.forClass(InvocationTrackCommand.class);
        verify(invocationTrackService).saveInvocation(captor.capture());
        InvocationTrackCommand command = captor.getValue();
        assertThat(command.apiCode()).isEqualTo("hello-world");
        assertThat(command.callerId()).isEqualTo("anonymous");
        assertThat(command.userType()).isEqualTo("UNKNOWN");
    }

    @Test
    @DisplayName("响应状态为失败时记录 success=false")
    void should_markFailed_when_responseStatusIsErrorStatus() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/sort");
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(400);

        interceptor.preHandle(request, response, handlerMethod);
        interceptor.afterCompletion(request, response, handlerMethod, new IllegalStateException("bad request"));

        ArgumentCaptor<InvocationTrackCommand> captor = ArgumentCaptor.forClass(InvocationTrackCommand.class);
        verify(invocationTrackService).saveInvocation(captor.capture());
        assertThat(captor.getValue().success()).isFalse();
    }

    @Test
    @DisplayName("非业务路径不记录埋点")
    void should_notTrack_when_pathUnmatched() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/actuator/health");
        MockHttpServletResponse response = new MockHttpServletResponse();

        interceptor.preHandle(request, response, handlerMethod);
        interceptor.afterCompletion(request, response, handlerMethod, null);

        verifyNoInteractions(invocationTrackService);
    }

    @Test
    @DisplayName("报表与导出接口自身调用不记录埋点，避免统计自我放大")
    void should_notTrack_when_pathIsReportOrExport() {
        MockHttpServletRequest reportRequest = new MockHttpServletRequest("GET", "/api/reports");
        MockHttpServletRequest exportRequest = new MockHttpServletRequest("GET", "/api/exports");
        MockHttpServletResponse response = new MockHttpServletResponse();

        interceptor.preHandle(reportRequest, response, handlerMethod);
        interceptor.afterCompletion(reportRequest, response, handlerMethod, null);
        interceptor.preHandle(exportRequest, response, handlerMethod);
        interceptor.afterCompletion(exportRequest, response, handlerMethod, null);

        verifyNoInteractions(invocationTrackService);
    }
}
