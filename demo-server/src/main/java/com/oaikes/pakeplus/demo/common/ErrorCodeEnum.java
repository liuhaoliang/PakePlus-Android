package com.oaikes.pakeplus.demo.common;

/**
 * 错误码枚举：错误码为 5 位字符串，首字母标识错误来源（A 用户端 / B 系统端），后四位为编号。
 *
 * @author pakeplus
 */
public enum ErrorCodeEnum {

    /** 成功，非错误码，仅用于统一响应体的成功标识。 */
    SUCCESS("S0000", "success", "操作成功"),

    /** 用户端参数错误。 */
    PARAM_INVALID("A0001", "用户参数错误", "请检查输入内容后重试"),

    /** 用户端请求的资源不存在。 */
    RESOURCE_NOT_FOUND("A0002", "请求的资源不存在", "请求的内容不存在"),

    /** 系统端执行异常。 */
    SYSTEM_ERROR("B0001", "系统执行出错", "系统繁忙，请稍后重试");

    private final String code;

    private final String message;

    private final String userTip;

    ErrorCodeEnum(String code, String message, String userTip) {
        this.code = code;
        this.message = message;
        this.userTip = userTip;
    }

    /**
     * 获取错误码。
     *
     * @return 5 位错误码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取错误描述。
     *
     * @return 错误描述
     */
    public String getMessage() {
        return message;
    }

    /**
     * 获取用户提示。
     *
     * @return 用户提示
     */
    public String getUserTip() {
        return userTip;
    }
}
