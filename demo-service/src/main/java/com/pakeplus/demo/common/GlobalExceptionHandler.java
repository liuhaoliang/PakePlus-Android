package com.pakeplus.demo.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理，保证接口始终返回统一响应结构。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
        ErrorCodeEnum errorCode = exception.getErrorCode();
        LOGGER.warn("business exception, errorCode={}, message={}", errorCode.getErrorCode(), exception.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode, exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldError();
        String message = fieldError == null ? ErrorCodeEnum.INVALID_PARAMETER.getMessage() : fieldError.getDefaultMessage();
        LOGGER.warn("request validation failed, message={}", message);
        return ResponseEntity.status(ErrorCodeEnum.INVALID_PARAMETER.getHttpStatus())
                .body(ApiResponse.failure(ErrorCodeEnum.INVALID_PARAMETER, message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadableException(HttpMessageNotReadableException exception) {
        LOGGER.warn("request body unreadable, message={}", exception.getMessage());
        return ResponseEntity.status(ErrorCodeEnum.INVALID_PARAMETER.getHttpStatus())
                .body(ApiResponse.failure(ErrorCodeEnum.INVALID_PARAMETER, "请求体格式不正确"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception exception) {
        LOGGER.error("unexpected exception occurred", exception);
        return ResponseEntity.status(ErrorCodeEnum.SYSTEM_ERROR.getHttpStatus())
                .body(ApiResponse.failure(ErrorCodeEnum.SYSTEM_ERROR, ErrorCodeEnum.SYSTEM_ERROR.getMessage()));
    }
}
