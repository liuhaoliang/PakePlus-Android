package com.oaikes.pakeplus.demo.enums;

import java.util.Arrays;

/**
 * 报表统计维度枚举：人员类型、人员层级、人员部门。
 *
 * @author pakeplus
 */
public enum ReportDimensionEnum {

    /** 按人员类型统计。 */
    USER_TYPE("userType", "人员类型"),

    /** 按人员层级统计。 */
    USER_LEVEL("userLevel", "人员层级"),

    /** 按人员部门统计。 */
    USER_DEPT("userDept", "人员部门");

    private final String code;

    private final String description;

    ReportDimensionEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 获取维度编码。
     *
     * @return 维度编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取维度描述。
     *
     * @return 维度描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据维度编码查找枚举，找不到返回 null。
     *
     * @param code 维度编码
     * @return 匹配的枚举，找不到返回 null
     */
    public static ReportDimensionEnum of(String code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(dimension -> dimension.code.equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }
}
