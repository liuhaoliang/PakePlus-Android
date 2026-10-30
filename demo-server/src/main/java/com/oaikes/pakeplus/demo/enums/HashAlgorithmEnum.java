package com.oaikes.pakeplus.demo.enums;

import java.util.Arrays;

/**
 * 哈希算法枚举：对外暴露小写算法名，内部映射 JDK MessageDigest 算法名。
 *
 * @author pakeplus
 */
public enum HashAlgorithmEnum {

    /** SHA-256 摘要算法。 */
    SHA_256("sha-256", "SHA-256"),

    /** SHA-512 摘要算法。 */
    SHA_512("sha-512", "SHA-512"),

    /** MD5 摘要算法。 */
    MD5("md5", "MD5");

    private final String code;

    private final String digestName;

    HashAlgorithmEnum(String code, String digestName) {
        this.code = code;
        this.digestName = digestName;
    }

    /**
     * 获取对外算法编码。
     *
     * @return 算法编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取 JDK 摘要算法名。
     *
     * @return JDK 摘要算法名
     */
    public String getDigestName() {
        return digestName;
    }

    /**
     * 根据算法编码查找枚举，找不到返回 null。
     *
     * @param code 算法编码
     * @return 匹配的枚举，找不到返回 null
     */
    public static HashAlgorithmEnum of(String code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(algorithm -> algorithm.code.equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }
}
