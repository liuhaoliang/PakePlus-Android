package com.oaikes.pakeplus.demo.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器：把异常转换为包含错误码、错误描述、用户提示的标准响应体。
 *
 * @author pakeplus
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理参数校验失败。
     *
     * @param exception 参数校验异常
     * @return 400 响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String detail = exception.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        LOGGER.warn("参数校验失败, detail: {}", detail);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCodeEnum.PARAM_INVALID, detail));
    }

    /**
     * 处理业务异常。
     *
     * @param exception 业务异常
     * @return 对应状态码的响应
     */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<ApiResponse<Void>> handleBizException(BizException exception) {
        LOGGER.warn("业务异常, errorCode: {}, detail: {}",
                exception.getErrorCodeEnum().getCode(), exception.getMessage());
        HttpStatus status = ErrorCodeEnum.RESOURCE_NOT_FOUND == exception.getErrorCodeEnum()
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(ApiResponse.error(exception.getErrorCodeEnum(), exception.getMessage()));
    }

    /**
     * 处理未预期异常，避免堆栈外泄。
     *
     * @param exception 未预期异常
     * @return 500 响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        LOGGER.error("系统异常, errorMessage: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCodeEnum.SYSTEM_ERROR, exception.getMessage()));
    }

    /**
     * 拼接字段级校验错误信息。
     *
     * @param fieldError 字段校验错误
     * @return 格式化后的描述
     */
    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }
}
