package com.pakeplus.demo.track;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 埋点拦截器：对 /api/demo/** 的接口调用记录调用人、耗时与结果。
 */
@Component
public class TrackInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackInterceptor.class);
    private static final String START_TIME_ATTRIBUTE = "demo.track.startTime";
    private static final String IDENTITY_ATTRIBUTE = "demo.track.identity";

    private final TrackStore trackStore;

    public TrackInterceptor(TrackStore trackStore) {
        this.trackStore = trackStore;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        request.setAttribute(IDENTITY_ATTRIBUTE, UserIdentity.from(request));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception exception) {
        Object startTime = request.getAttribute(START_TIME_ATTRIBUTE);
        Object identity = request.getAttribute(IDENTITY_ATTRIBUTE);
        if (!(startTime instanceof Long startMillis) || !(identity instanceof UserIdentity userIdentity)) {
            return;
        }
        long elapsedMs = System.currentTimeMillis() - startMillis;
        boolean success = exception == null && response.getStatus() < HttpServletResponse.SC_BAD_REQUEST;
        trackStore.append(request.getRequestURI(), userIdentity, success, elapsedMs);
        LOGGER.info("api call tracked, api={}, userId={}, userType={}, userDept={}, success={}, elapsedMs={}",
                request.getRequestURI(), userIdentity.userId(), userIdentity.userType(), userIdentity.userDept(),
                success, elapsedMs);
    }
}
