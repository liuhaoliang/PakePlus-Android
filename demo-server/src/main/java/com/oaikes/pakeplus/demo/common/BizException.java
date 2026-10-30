package com.oaikes.pakeplus.demo.common;

/**
 * 业务异常：统一携带错误码，由全局异常处理器转换为标准错误响应。
 *
 * @author pakeplus
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 错误码枚举。 */
    private final ErrorCodeEnum errorCodeEnum;

    /**
     * 构造业务异常。
     *
     * @param errorCodeEnum 错误码枚举
     * @param detail        详细错误描述
     */
    public BizException(ErrorCodeEnum errorCodeEnum, String detail) {
        super(detail);
        this.errorCodeEnum = errorCodeEnum;
    }

    /**
     * 构造业务异常并保留原始异常。
     *
     * @param errorCodeEnum 错误码枚举
     * @param detail        详细错误描述
     * @param cause         原始异常
     */
    public BizException(ErrorCodeEnum errorCodeEnum, String detail, Throwable cause) {
        super(detail, cause);
        this.errorCodeEnum = errorCodeEnum;
    }

    public ErrorCodeEnum getErrorCodeEnum() {
        return errorCodeEnum;
    }

    @Override
    public String toString() {
        return "BizException{errorCode='" + errorCodeEnum.getCode()
                + "', detail='" + getMessage() + "'}";
    }
}
