package com.pakeplus.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.pakeplus.demo.common.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("HashAlgorithmService 单元测试")
class HashAlgorithmServiceTest {

    private final HashAlgorithmService hashAlgorithmService = new HashAlgorithmService();

    @Test
    @DisplayName("SHA-256 摘要与 JDK 标准一致")
    void should_returnSha256Digest_when_textProvided() {
        String digest = hashAlgorithmService.digest("abc", "SHA-256");

        assertThat(digest).isEqualTo("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
    }

    @Test
    @DisplayName("MD5 摘要与 JDK 标准一致")
    void should_returnMd5Digest_when_algorithmMd5() {
        String digest = hashAlgorithmService.digest("abc", "md5");

        assertThat(digest).isEqualTo("900150983cd24fb0d6963f7d28e17f72");
        assertThat(digest).hasSize(32);
    }

    @Test
    @DisplayName("算法缺省时使用 SHA-256")
    void should_useSha256_when_algorithmMissing() {
        assertThat(hashAlgorithmService.digest("abc", null))
                .isEqualTo(hashAlgorithmService.digest("abc", "SHA-256"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"SHA1", "sha-1", "SHA_1"})
    @DisplayName("算法别名归一化为 SHA-1")
    void should_normalizeAlias_when_algorithmVariant(String algorithm) {
        assertThat(hashAlgorithmService.normalizeAlgorithm(algorithm)).isEqualTo("SHA-1");
    }

    @Test
    @DisplayName("文本为空时抛出参数异常")
    void should_throwBusinessException_when_textEmpty() {
        assertThatThrownBy(() -> hashAlgorithmService.digest("", "SHA-256"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("待计算文本不能为空");
    }

    @Test
    @DisplayName("不支持的算法抛出参数异常")
    void should_throwBusinessException_when_algorithmUnsupported() {
        assertThatThrownBy(() -> hashAlgorithmService.digest("abc", "CRC32"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("不支持的哈希算法");
    }
}
