package com.oaikes.pakeplus.demo.tracking;

import com.oaikes.pakeplus.demo.enums.DemoApiEnum;
import com.oaikes.pakeplus.demo.model.dto.CallerInfo;
import com.oaikes.pakeplus.demo.model.dto.InvocationTrackCommand;
import com.oaikes.pakeplus.demo.service.InvocationTrackService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 调用埋点拦截器：统一记录接口编码、调用人、人员属性、耗时与执行结果。
 *
 * @author pakeplus
 */
@Component
public class InvocationTrackInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvocationTrackInterceptor.class);

    /** 请求属性：埋点开始时间（纳秒）。 */
    private static final String ATTR_START_NANOS = "demo.track.startNanos";

    /** 请求属性：结果摘要，由 Controller 写入。 */
    public static final String ATTR_RESULT_SUMMARY = "demo.track.resultSummary";

    /** 请求属性：调用人信息。 */
    private static final String ATTR_CALLER_INFO = "demo.track.callerInfo";

    private final InvocationTrackService invocationTrackService;

    public InvocationTrackInterceptor(InvocationTrackService invocationTrackService) {
        this.invocationTrackService = invocationTrackService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        request.setAttribute(ATTR_START_NANOS, System.nanoTime());
        request.setAttribute(ATTR_CALLER_INFO, CallerInfoResolver.resolve(request));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) {
        if (!(handler instanceof HandlerMethod)) {
            return;
        }
        DemoApiEnum apiEnum = TrackingConstants.resolveApi(request.getRequestURI());
        if (apiEnum == null) {
            return;
        }
        try {
            long costMillis = resolveCostMillis(request);
            CallerInfo callerInfo = resolveCallerInfo(request);
            boolean success = exception == null && response.getStatus() < HttpServletResponse.SC_BAD_REQUEST;
            Object summaryAttribute = request.getAttribute(ATTR_RESULT_SUMMARY);
            String summary = summaryAttribute == null ? "" : String.valueOf(summaryAttribute);
            invocationTrackService.saveInvocation(new InvocationTrackCommand(
                    apiEnum.getCode(),
                    apiEnum.getDescription(),
                    callerInfo.callerId(),
                    callerInfo.callerName(),
                    callerInfo.userType(),
                    callerInfo.userLevel(),
                    callerInfo.userDept(),
                    costMillis,
                    success,
                    summary));
        } catch (RuntimeException trackException) {
            // 埋点失败不能影响主流程，仅记录告警日志
            LOGGER.warn("调用埋点记录失败, uri: {}, errorMessage: {}",
                    request.getRequestURI(), trackException.getMessage(), trackException);
        }
    }

    /**
     * 计算调用耗时。
     *
     * @param request HTTP 请求
     * @return 耗时毫秒数
     */
    private long resolveCostMillis(HttpServletRequest request) {
        Object startNanos = request.getAttribute(ATTR_START_NANOS);
        if (startNanos instanceof Long start) {
            return (System.nanoTime() - start) / 1_000_000L;
        }
        return 0L;
    }

    /**
     * 获取调用人信息，缺失时重新解析。
     *
     * @param request HTTP 请求
     * @return 调用人信息
     */
    private CallerInfo resolveCallerInfo(HttpServletRequest request) {
        Object callerInfo = request.getAttribute(ATTR_CALLER_INFO);
        if (callerInfo instanceof CallerInfo info) {
            return info;
        }
        return CallerInfoResolver.resolve(request);
    }
}
