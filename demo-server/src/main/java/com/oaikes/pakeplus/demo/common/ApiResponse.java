package com.oaikes.pakeplus.demo.common;

/**
 * 统一响应体：错误场景返回 errorCode、errorMessage、userTip、data 四部分。
 *
 * @param <T> 业务数据类型
 * @author pakeplus
 */
public class ApiResponse<T> {

    /** 是否成功。 */
    private boolean success;

    /** 错误码，成功时为 S0000。 */
    private String errorCode;

    /** 错误描述，供前端排查使用。 */
    private String errorMessage;

    /** 用户提示信息。 */
    private String userTip;

    /** 业务数据。 */
    private T data;

    /**
     * 构造空响应体。
     */
    public ApiResponse() {
    }

    /**
     * 构造成功响应体。
     *
     * @param data 业务数据
     * @param <T>  业务数据类型
     * @return 成功响应体
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setErrorCode(ErrorCodeEnum.SUCCESS.getCode());
        response.setErrorMessage(ErrorCodeEnum.SUCCESS.getMessage());
        response.setUserTip(ErrorCodeEnum.SUCCESS.getUserTip());
        response.setData(data);
        return response;
    }

    /**
     * 构造失败响应体。
     *
     * @param errorCodeEnum 错误码枚举
     * @param detail        详细错误描述（用于排查，不直接展示给用户）
     * @param <T>           业务数据类型
     * @return 失败响应体
     */
    public static <T> ApiResponse<T> error(ErrorCodeEnum errorCodeEnum, String detail) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setErrorCode(errorCodeEnum.getCode());
        response.setErrorMessage(detail);
        response.setUserTip(errorCodeEnum.getUserTip());
        response.setData(null);
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getUserTip() {
        return userTip;
    }

    public void setUserTip(String userTip) {
        this.userTip = userTip;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiResponse{success=" + success
                + ", errorCode='" + errorCode + '\''
                + ", errorMessage='" + errorMessage + '\''
                + ", userTip='" + userTip + '\''
                + ", data=" + data + '}';
    }
}
