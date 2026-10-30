package com.oaikes.pakeplus.demo.service;

import com.oaikes.pakeplus.demo.model.vo.HashVO;

/**
 * 哈希算法服务。
 *
 * @author pakeplus
 */
public interface HashService {

    /**
     * 计算文本摘要。
     *
     * @param content   原文，不能为空
     * @param algorithm 算法编码，支持 sha-256 / sha-512 / md5，为空时默认 sha-256
     * @return 摘要结果
     */
    HashVO getDigest(String content, String algorithm);
}
