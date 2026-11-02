package com.pakeplus.demo.track;

import java.time.LocalDateTime;

/**
 * 一次接口调用埋点记录。
 *
 * @param sequence   自增序号
 * @param apiPath    接口路径
 * @param userId     调用人 ID
 * @param userName   调用人姓名
 * @param userType   人员类型
 * @param userLevel  人员层级
 * @param userDept   人员部门
 * @param success    是否调用成功
 * @param elapsedMs  接口耗时（毫秒）
 * @param calledAt   调用时间
 */
public record CallRecord(long sequence, String apiPath, String userId, String userName, String userType,
                         String userLevel, String userDept, boolean success, long elapsedMs, LocalDateTime calledAt) {

    /**
     * 按维度取分组键。
     *
     * @param dimension 统计维度
     * @return 分组键
     */
    public String dimensionValue(TrackDimensionEnum dimension) {
        return switch (dimension) {
            case USER_TYPE -> userType;
            case USER_LEVEL -> userLevel;
            case USER_DEPT -> userDept;
            case API -> apiPath;
        };
    }
}
