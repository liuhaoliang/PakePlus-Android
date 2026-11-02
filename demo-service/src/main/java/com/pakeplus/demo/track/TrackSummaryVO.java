package com.pakeplus.demo.track;

import java.util.List;

/**
 * 调用情况报表。
 *
 * @param total        总调用次数
 * @param successCount 成功次数
 * @param failCount    失败次数
 * @param avgElapsedMs 平均耗时（毫秒，保留 2 位小数）
 * @param callerCount  去重调用人数量
 * @param dimensionCode 统计维度编码
 * @param dimensionLabel 统计维度名称
 * @param items        维度分组统计（柱状图 / 饼图）
 * @param trend        时间趋势（折线图）
 * @param generatedAt  报表生成时间（yyyy-MM-dd HH:mm:ss）
 */
public record TrackSummaryVO(long total, long successCount, long failCount, double avgElapsedMs, long callerCount,
                             String dimensionCode, String dimensionLabel, List<TrackItemVO> items,
                             List<TrackTrendVO> trend, String generatedAt) {
}
