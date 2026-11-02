package com.pakeplus.demo.model;

/**
 * 哈希计算响应。
 *
 * @param algorithm 实际使用的算法
 * @param digest    十六进制摘要
 * @param digestLength 摘要长度
 * @param inputLength  原文长度
 * @param elapsedMs    计算耗时（毫秒，保留 3 位小数）
 */
public record HashResponse(String algorithm, String digest, int digestLength, int inputLength, double elapsedMs) {
}
