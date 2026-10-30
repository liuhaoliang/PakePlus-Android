package com.oaikes.pakeplus.demo.service.impl;

import com.oaikes.pakeplus.demo.common.BizException;
import com.oaikes.pakeplus.demo.common.ErrorCodeEnum;
import com.oaikes.pakeplus.demo.enums.HashAlgorithmEnum;
import com.oaikes.pakeplus.demo.model.vo.HashVO;
import com.oaikes.pakeplus.demo.service.HashService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * 哈希算法服务实现：基于 JDK MessageDigest 提供 SHA-256 / SHA-512 / MD5 摘要。
 *
 * @author pakeplus
 */
@Service
public class HashServiceImpl implements HashService {

    /** 默认算法。 */
    private static final HashAlgorithmEnum DEFAULT_ALGORITHM = HashAlgorithmEnum.SHA_256;

    @Override
    public HashVO getDigest(String content, String algorithm) {
        if (content == null || content.isEmpty()) {
            throw new BizException(ErrorCodeEnum.PARAM_INVALID, "待计算内容不能为空");
        }
        HashAlgorithmEnum algorithmEnum = resolveAlgorithm(algorithm);
        byte[] digestBytes = doDigest(content, algorithmEnum);
        String digest = HexFormat.of().formatHex(digestBytes);
        return new HashVO(algorithmEnum.getCode(), content, digest, digest.length());
    }

    /**
     * 解析算法编码，为空时使用默认算法。
     *
     * @param algorithm 算法编码
     * @return 算法枚举
     */
    private HashAlgorithmEnum resolveAlgorithm(String algorithm) {
        if (algorithm == null || algorithm.isBlank()) {
            return DEFAULT_ALGORITHM;
        }
        HashAlgorithmEnum algorithmEnum = HashAlgorithmEnum.of(algorithm);
        if (algorithmEnum == null) {
            throw new BizException(ErrorCodeEnum.PARAM_INVALID,
                    "不支持的哈希算法: " + algorithm + "，可选值: sha-256, sha-512, md5");
        }
        return algorithmEnum;
    }

    /**
     * 执行摘要计算。
     *
     * @param content       原文
     * @param algorithmEnum 算法枚举
     * @return 摘要字节数组
     */
    private byte[] doDigest(String content, HashAlgorithmEnum algorithmEnum) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithmEnum.getDigestName());
            return messageDigest.digest(content.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException exception) {
            throw new BizException(ErrorCodeEnum.SYSTEM_ERROR,
                    "JDK 不支持哈希算法: " + algorithmEnum.getDigestName(), exception);
        }
    }
}
