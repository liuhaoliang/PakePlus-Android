package com.oaikes.pakeplus.demo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 哈希计算请求。
 *
 * @param content   待计算摘要的原文，不能为空
 * @param algorithm 算法编码，支持 sha-256 / sha-512 / md5，为空时默认 SHA-256
 * @author pakeplus
 */
public record HashRequest(
        @NotBlank(message = "待计算内容不能为空")
        @Size(max = 4096, message = "待计算内容长度不能超过 4096")
        String content,

        String algorithm) {
}
