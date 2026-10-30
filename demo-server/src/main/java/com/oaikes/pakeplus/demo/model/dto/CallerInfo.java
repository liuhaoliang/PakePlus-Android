package com.oaikes.pakeplus.demo.model.dto;

/**
 * 调用人信息：由请求头解析得到，用于埋点记录调用人与人员属性。
 *
 * @param callerId   调用人 ID
 * @param callerName 调用人姓名
 * @param userType   人员类型
 * @param userLevel  人员层级
 * @param userDept   人员部门
 * @author pakeplus
 */
public record CallerInfo(
        String callerId,
        String callerName,
        String userType,
        String userLevel,
        String userDept) {
}
