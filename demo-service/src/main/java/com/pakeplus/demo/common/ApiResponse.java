package com.pakeplus.demo.common;

/**
 * 统一响应结构：code + message + errorCode + data。
 *
 * @param <T> 业务数据类型
 */
public class ApiResponse<T> {

    private static final int SUCCESS_CODE = 200;

    private final int code;
    private final String message;
    private final String errorCode;
    private final T data;

    private ApiResponse(int code, String message, String errorCode, T data) {
        this.code = code;
        this.message = message;
        this.errorCode = errorCode;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(SUCCESS_CODE, "success", null, data);
    }

    public static <T> ApiResponse<T> failure(ErrorCodeEnum errorCodeEnum, String message) {
        return new ApiResponse<>(errorCodeEnum.getHttpStatus(), message, errorCodeEnum.getErrorCode(), null);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public T getData() {
        return data;
    }
}
