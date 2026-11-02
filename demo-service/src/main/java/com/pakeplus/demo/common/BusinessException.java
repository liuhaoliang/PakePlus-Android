package com.pakeplus.demo.common;

/**
 * 业务异常，携带统一错误码。
 */
public class BusinessException extends RuntimeException {

    private final ErrorCodeEnum errorCode;

    public BusinessException(ErrorCodeEnum errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCodeEnum errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCodeEnum getErrorCode() {
        return errorCode;
    }
}
