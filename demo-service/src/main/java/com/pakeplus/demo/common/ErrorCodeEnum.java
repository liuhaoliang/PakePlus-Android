package com.pakeplus.demo.common;

/**
 * 统一错误码。格式：来源标识 + 4 位编号（A 用户侧 / B 系统侧）。
 */
public enum ErrorCodeEnum {

    /** 请求参数不合法 */
    INVALID_PARAMETER("A0400", 400, "请求参数不合法"),

    /** 资源不存在 */
    RESOURCE_NOT_FOUND("A0404", 404, "请求的资源不存在"),

    /** 系统执行出错 */
    SYSTEM_ERROR("B0500", 500, "系统执行出错");

    private final String errorCode;
    private final int httpStatus;
    private final String message;

    ErrorCodeEnum(String errorCode, int httpStatus, String message) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
