package com.oaikes.pakeplus.demo.model.vo;

import java.util.List;

/**
 * 调用情况报表：包含汇总指标、饼图、柱状图与折线图数据。
 *
 * @param totalCount          调用总次数
 * @param distinctCallerCount 调用人数
 * @param successCount        成功次数
 * @param failCount           失败次数
 * @param dimensionCode       统计维度编码
 * @param dimensionName       统计维度名称
 * @param apiPoints           各接口调用次数
 * @param pieChart            按统计维度分布的饼图数据
 * @param barChart            按统计维度分布的柱状图数据
 * @param lineChart           按统计维度分组的调用趋势折线图数据
 * @author pakeplus
 */
public record ReportVO(
        long totalCount,
        long distinctCallerCount,
        long successCount,
        long failCount,
        String dimensionCode,
        String dimensionName,
        List<ChartPointVO> apiPoints,
        ChartVO pieChart,
        ChartVO barChart,
        LineChartVO lineChart) {
}
