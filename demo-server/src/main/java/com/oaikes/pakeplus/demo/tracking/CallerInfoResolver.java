package com.oaikes.pakeplus.demo.tracking;

import com.oaikes.pakeplus.demo.constant.DemoConstants;
import com.oaikes.pakeplus.demo.model.dto.CallerInfo;
import jakarta.servlet.http.HttpServletRequest;

import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

/**
 * 调用人解析器：从请求头解析调用人信息，缺省时使用默认值保证埋点不丢数据。
 *
 * @author pakeplus
 */
public final class CallerInfoResolver {

    /** 请求头字符集上限：ISO-8859-1 可表示的字符最大值。 */
    private static final int ISO_8859_1_MAX_CHAR = 0xFF;

    /**
     * 私有构造方法，禁止实例化工具类。
     */
    private CallerInfoResolver() {
    }

    /**
     * 从请求头解析调用人信息。
     *
     * @param request HTTP 请求
     * @return 调用人信息，字段不会为 null
     */
    public static CallerInfo resolve(HttpServletRequest request) {
        String callerId = getHeaderOrDefault(request, DemoConstants.HEADER_USER_ID,
                DemoConstants.ANONYMOUS_USER_ID);
        String callerName = getHeaderOrDefault(request, DemoConstants.HEADER_USER_NAME, callerId);
        String userType = getHeaderOrDefault(request, DemoConstants.HEADER_USER_TYPE,
                DemoConstants.DEFAULT_USER_TYPE);
        String userLevel = getHeaderOrDefault(request, DemoConstants.HEADER_USER_LEVEL,
                DemoConstants.DEFAULT_USER_LEVEL);
        String userDept = getHeaderOrDefault(request, DemoConstants.HEADER_USER_DEPT,
                DemoConstants.DEFAULT_USER_DEPT);
        return new CallerInfo(callerId, callerName, userType, userLevel, userDept);
    }

    /**
     * 读取请求头，空值时返回默认值。
     *
     * @param request        HTTP 请求
     * @param headerName     请求头名称
     * @param defaultValue   默认值
     * @return 请求头值或默认值
     */
    private static String getHeaderOrDefault(HttpServletRequest request, String headerName, String defaultValue) {
        String value = request.getHeader(headerName);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        String trimmed = value.trim();
        return decodeHeaderValue(trimmed);
    }

    /**
     * 解码请求头取值：兼容前端 URL 编码传参与按 ISO-8859-1 解码出的 UTF-8 字节序列。
     *
     * @param rawValue 原始请求头取值
     * @return 解码后的可读文本
     */
    private static String decodeHeaderValue(String rawValue) {
        if (rawValue.isEmpty()) {
            return rawValue;
        }
        if (rawValue.indexOf('%') >= 0) {
            try {
                return URLDecoder.decode(rawValue, StandardCharsets.UTF_8);
            } catch (IllegalArgumentException decodeException) {
                // 非法百分号编码按原文返回，避免影响埋点主流程
                return rawValue;
            }
        }
        if (isPrintableAscii(rawValue) || !isIso8859Range(rawValue)) {
            return rawValue;
        }
        try {
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(rawValue.getBytes(StandardCharsets.ISO_8859_1)))
                    .toString();
        } catch (CharacterCodingException codingException) {
            return rawValue;
        }
    }

    /**
     * 判断文本是否全部为可打印 ASCII 字符。
     *
     * @param text 文本
     * @return 是否全部为 ASCII
     */
    private static boolean isPrintableAscii(String text) {
        for (int index = 0; index < text.length(); index++) {
            if (text.charAt(index) > ISO_8859_1_MAX_CHAR / 2) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断文本是否落在 ISO-8859-1 可表示范围内，用于识别被按单字节解码的 UTF-8 文本。
     *
     * @param text 文本
     * @return 是否全部字符不超过 0xFF
     */
    private static boolean isIso8859Range(String text) {
        for (int index = 0; index < text.length(); index++) {
            if (text.charAt(index) > ISO_8859_1_MAX_CHAR) {
                return false;
            }
        }
        return true;
    }
}
