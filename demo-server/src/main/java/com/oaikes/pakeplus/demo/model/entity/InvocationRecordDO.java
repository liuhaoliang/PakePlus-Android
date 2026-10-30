package com.oaikes.pakeplus.demo.model.entity;

import java.time.LocalDateTime;

/**
 * 调用埋点记录：记录一次接口调用的调用人、人员属性、耗时与结果摘要。
 *
 * @param recordId      记录流水号
 * @param apiCode       接口编码，取值见 {@link com.oaikes.pakeplus.demo.enums.DemoApiEnum}
 * @param apiName       接口名称
 * @param callerId      调用人 ID
 * @param callerName    调用人姓名
 * @param userType      人员类型
 * @param userLevel     人员层级
 * @param userDept      人员部门
 * @param invokeTime    调用时间
 * @param costMillis    调用耗时（毫秒）
 * @param success       是否调用成功
 * @param resultSummary 结果摘要，用于导出页面展示结果
 * @author pakeplus
 */
public record InvocationRecordDO(
        String recordId,
        String apiCode,
        String apiName,
        String callerId,
        String callerName,
        String userType,
        String userLevel,
        String userDept,
        LocalDateTime invokeTime,
        long costMillis,
        boolean success,
        String resultSummary) {
}
