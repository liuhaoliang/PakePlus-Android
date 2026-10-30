package com.oaikes.pakeplus.demo.enums;

/**
 * 演示接口枚举：用于埋点、报表与导出时统一标识接口。
 *
 * @author pakeplus
 */
public enum DemoApiEnum {

    /** hello world 接口。 */
    HELLO_WORLD("hello-world", "hello world", true),

    /** 哈希算法接口。 */
    HASH("hash", "哈希算法", true),

    /** 冒泡排序接口。 */
    SORT("sort", "冒泡排序", true),

    /** 报表查询接口：自身查询不参与埋点，避免统计结果自我放大。 */
    REPORT("report", "调用报表", false),

    /** 导出接口：自身调用不参与埋点。 */
    EXPORT("export", "结果导出", false);

    private final String code;

    private final String description;

    private final boolean tracked;

    DemoApiEnum(String code, String description, boolean tracked) {
        this.code = code;
        this.description = description;
        this.tracked = tracked;
    }

    /**
     * 获取接口编码。
     *
     * @return 接口编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取接口描述。
     *
     * @return 接口描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 是否参与调用埋点统计。
     *
     * @return true 表示该接口的调用会被埋点记录
     */
    public boolean isTracked() {
        return tracked;
    }

    /**
     * 根据接口编码查找枚举，找不到返回 null。
     *
     * @param code 接口编码
     * @return 匹配的枚举，找不到返回 null
     */
    public static DemoApiEnum of(String code) {
        for (DemoApiEnum apiEnum : values()) {
            if (apiEnum.code.equalsIgnoreCase(code)) {
                return apiEnum;
            }
        }
        return null;
    }
}
