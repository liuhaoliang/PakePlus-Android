package com.oaikes.pakeplus.demo.model.dto;

/**
 * 调用报表查询条件。
 *
 * @param dimensionCode 统计维度编码：userType / userLevel / userDept，默认 userType
 * @param days          统计最近天数，取值范围 1~90，默认 7
 * @param apiCode       接口编码过滤条件，为空表示全部接口
 * @author pakeplus
 */
public record ReportQuery(String dimensionCode, int days, String apiCode) {
}
