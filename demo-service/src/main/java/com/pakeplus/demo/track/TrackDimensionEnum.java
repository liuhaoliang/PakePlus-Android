package com.pakeplus.demo.track;

import com.pakeplus.demo.common.BusinessException;
import com.pakeplus.demo.common.ErrorCodeEnum;
import java.util.Locale;

/**
 * 埋点统计维度。
 */
public enum TrackDimensionEnum {

    /** 人员类型 */
    USER_TYPE("user_type", "人员类型"),

    /** 人员层级 */
    USER_LEVEL("user_level", "人员层级"),

    /** 人员部门 */
    USER_DEPT("user_dept", "人员部门"),

    /** 接口 */
    API("api", "接口");

    private final String code;
    private final String label;

    TrackDimensionEnum(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    /**
     * 按编码解析维度，缺省为人员类型。
     *
     * @param code 维度编码
     * @return 维度枚举
     */
    public static TrackDimensionEnum fromCode(String code) {
        if (code == null || code.isBlank()) {
            return USER_TYPE;
        }
        String candidate = code.trim().toLowerCase(Locale.ROOT);
        for (TrackDimensionEnum dimension : values()) {
            if (dimension.code.equals(candidate)) {
                return dimension;
            }
        }
        throw new BusinessException(ErrorCodeEnum.INVALID_PARAMETER, "不支持的统计维度: " + code);
    }
}
