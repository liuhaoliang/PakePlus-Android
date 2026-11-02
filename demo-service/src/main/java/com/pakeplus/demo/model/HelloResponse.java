package com.pakeplus.demo.model;

/**
 * Hello World 接口响应。
 *
 * @param greeting  问候语
 * @param serverTime 服务器时间（yyyy-MM-dd HH:mm:ss）
 */
public record HelloResponse(String greeting, String serverTime) {
}
