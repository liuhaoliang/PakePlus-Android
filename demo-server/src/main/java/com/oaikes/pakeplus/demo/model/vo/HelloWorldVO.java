package com.oaikes.pakeplus.demo.model.vo;

/**
 * hello world 接口返回结果。
 *
 * @param message    问候语
 * @param serverTime 服务端时间，格式 yyyy-MM-dd HH:mm:ss
 * @author pakeplus
 */
public record HelloWorldVO(String message, String serverTime) {
}
