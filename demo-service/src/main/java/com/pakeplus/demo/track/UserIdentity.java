package com.pakeplus.demo.track;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 调用人身份，来源为网关注入的请求头；缺失时按匿名访客归集。
 *
 * @param userId   人员 ID
 * @param userName 人员姓名
 * @param userType 人员类型
 * @param userLevel 人员层级
 * @param userDept 人员部门
 */
public record UserIdentity(String userId, String userName, String userType, String userLevel, String userDept) {

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_NAME = "X-User-Name";
    public static final String HEADER_USER_TYPE = "X-User-Type";
    public static final String HEADER_USER_LEVEL = "X-User-Level";
    public static final String HEADER_USER_DEPT = "X-User-Dept";

    private static final String ANONYMOUS_ID = "anonymous";
    private static final String ANONYMOUS_NAME = "匿名访客";
    private static final String ANONYMOUS_TYPE = "UNKNOWN";
    private static final String ANONYMOUS_LEVEL = "UNKNOWN";
    private static final String ANONYMOUS_DEPT = "UNKNOWN";

    /**
     * 从请求头解析调用人身份。
     *
     * @param request HTTP 请求
     * @return 调用人身份，字段缺失时使用匿名默认值
     */
    public static UserIdentity from(HttpServletRequest request) {
        return new UserIdentity(
                resolve(request.getHeader(HEADER_USER_ID), ANONYMOUS_ID),
                resolve(request.getHeader(HEADER_USER_NAME), ANONYMOUS_NAME),
                resolve(request.getHeader(HEADER_USER_TYPE), ANONYMOUS_TYPE),
                resolve(request.getHeader(HEADER_USER_LEVEL), ANONYMOUS_LEVEL),
                resolve(request.getHeader(HEADER_USER_DEPT), ANONYMOUS_DEPT));
    }

    private static String resolve(String value, String defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return decodeIfEncoded(value.trim());
    }

    /**
     * HTTP 头只保证 ASCII，约定中文字段由调用方 URL 编码后传递，这里做解码还原。
     */
    private static String decodeIfEncoded(String value) {
        if (!value.contains("%")) {
            return value;
        }
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            return value;
        }
    }
}
