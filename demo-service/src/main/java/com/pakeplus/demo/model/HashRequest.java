package com.pakeplus.demo.model;

import jakarta.validation.constraints.NotBlank;

/**
 * 哈希计算请求。
 *
 * @param text      待计算文本
 * @param algorithm 算法名称，可选 MD5 / SHA-1 / SHA-256 / SHA-512
 */
public record HashRequest(
        @NotBlank(message = "待计算文本不能为空") String text,
        String algorithm) {
}
