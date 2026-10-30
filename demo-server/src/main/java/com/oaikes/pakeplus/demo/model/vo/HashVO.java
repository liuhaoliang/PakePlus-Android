package com.oaikes.pakeplus.demo.model.vo;

/**
 * 哈希算法接口返回结果。
 *
 * @param algorithm    实际使用的算法编码
 * @param content      原文
 * @param digest       十六进制摘要
 * @param digestLength 摘要长度（字符数）
 * @author pakeplus
 */
public record HashVO(String algorithm, String content, String digest, int digestLength) {
}
