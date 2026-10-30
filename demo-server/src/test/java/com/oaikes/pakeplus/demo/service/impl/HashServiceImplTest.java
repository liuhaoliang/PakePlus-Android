package com.oaikes.pakeplus.demo.service.impl;

import com.oaikes.pakeplus.demo.common.BizException;
import com.oaikes.pakeplus.demo.common.ErrorCodeEnum;
import com.oaikes.pakeplus.demo.model.vo.HashVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link HashServiceImpl} 单元测试。
 *
 * @author pakeplus
 */
class HashServiceImplTest {

    private final HashServiceImpl hashService = new HashServiceImpl();

    @Test
    @DisplayName("默认使用 SHA-256 计算摘要")
    void should_returnSha256Digest_when_algorithmAbsent() {
        HashVO hashVO = hashService.getDigest("abc", null);

        assertThat(hashVO.algorithm()).isEqualTo("sha-256");
        assertThat(hashVO.digest()).isEqualTo("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
        assertThat(hashVO.digestLength()).isEqualTo(64);
    }

    @Test
    @DisplayName("显式指定 MD5 时输出 32 位摘要")
    void should_returnMd5Digest_when_algorithmIsMd5() {
        HashVO hashVO = hashService.getDigest("abc", "md5");

        assertThat(hashVO.algorithm()).isEqualTo("md5");
        assertThat(hashVO.digest()).isEqualTo("900150983cd24fb0d6963f7d28e17f72");
        assertThat(hashVO.digestLength()).isEqualTo(32);
    }

    @Test
    @DisplayName("SHA-512 输出 128 位摘要")
    void should_returnSha512Digest_when_algorithmIsSha512() {
        HashVO hashVO = hashService.getDigest("abc", "SHA-512");

        assertThat(hashVO.algorithm()).isEqualTo("sha-512");
        assertThat(hashVO.digestLength()).isEqualTo(128);
    }

    @Test
    @DisplayName("内容为空时抛出参数异常")
    void should_throwException_when_contentEmpty() {
        assertThatThrownBy(() -> hashService.getDigest("", "sha-256"))
                .isInstanceOf(BizException.class)
                .satisfies(exception -> assertThat(((BizException) exception).getErrorCodeEnum())
                        .isEqualTo(ErrorCodeEnum.PARAM_INVALID));
    }

    @Test
    @DisplayName("算法不支持时抛出参数异常")
    void should_throwException_when_algorithmUnsupported() {
        assertThatThrownBy(() -> hashService.getDigest("abc", "sha-1"))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("不支持的哈希算法");
    }
}
