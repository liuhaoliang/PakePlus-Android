package com.pakeplus.demo.service;

import com.pakeplus.demo.common.BusinessException;
import com.pakeplus.demo.common.ErrorCodeEnum;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;
import org.springframework.stereotype.Service;

/**
 * 哈希算法示例服务，支持 MD5 / SHA-1 / SHA-256 / SHA-512。
 */
@Service
public class HashAlgorithmService {

    private static final String ALGORITHM_MD5 = "MD5";
    private static final String ALGORITHM_SHA1 = "SHA-1";
    private static final String ALGORITHM_SHA256 = "SHA-256";
    private static final String ALGORITHM_SHA512 = "SHA-512";
    private static final String DEFAULT_ALGORITHM = ALGORITHM_SHA256;

    /**
     * 对文本做摘要计算。
     *
     * @param text      待计算文本
     * @param algorithm 算法名称，支持 MD5 / SHA-1 / SHA-256 / SHA-512，缺省 SHA-256
     * @return 小写十六进制摘要
     */
    public String digest(String text, String algorithm) {
        if (text == null || text.isEmpty()) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMETER, "待计算文本不能为空");
        }
        String normalizedAlgorithm = normalizeAlgorithm(algorithm);
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(normalizedAlgorithm);
            byte[] digestBytes = messageDigest.digest(text.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digestBytes);
        } catch (NoSuchAlgorithmException exception) {
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "当前 JDK 不支持该哈希算法: " + normalizedAlgorithm, exception);
        }
    }

    /**
     * 归一化算法名称，非法名称统一返回参数错误。
     *
     * @param algorithm 原始算法名称
     * @return 标准算法名称
     */
    public String normalizeAlgorithm(String algorithm) {
        if (algorithm == null || algorithm.isBlank()) {
            return DEFAULT_ALGORITHM;
        }
        String candidate = algorithm.trim().toUpperCase(Locale.ROOT).replace('_', '-');
        return switch (candidate) {
            case ALGORITHM_MD5 -> ALGORITHM_MD5;
            case ALGORITHM_SHA1, "SHA1" -> ALGORITHM_SHA1;
            case ALGORITHM_SHA256, "SHA256" -> ALGORITHM_SHA256;
            case ALGORITHM_SHA512, "SHA512" -> ALGORITHM_SHA512;
            default -> throw new BusinessException(ErrorCodeEnum.INVALID_PARAMETER, "不支持的哈希算法: " + algorithm);
        };
    }
}
