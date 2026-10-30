package com.oaikes.pakeplus.demo.model.dto;

/**
 * 埋点入库命令：由拦截器组装后交给埋点服务落库。
 *
 * @param apiCode       接口编码
 * @param apiName       接口名称
 * @param callerId      调用人 ID
 * @param callerName    调用人姓名
 * @param userType      人员类型
 * @param userLevel     人员层级
 * @param userDept      人员部门
 * @param costMillis    调用耗时（毫秒）
 * @param success       是否调用成功
 * @param resultSummary 结果摘要
 * @author pakeplus
 */
public record InvocationTrackCommand(
        String apiCode,
        String apiName,
        String callerId,
        String callerName,
        String userType,
        String userLevel,
        String userDept,
        long costMillis,
        boolean success,
        String resultSummary) {
}
