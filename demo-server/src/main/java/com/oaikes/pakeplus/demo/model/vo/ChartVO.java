package com.oaikes.pakeplus.demo.model.vo;

import java.util.List;

/**
 * 饼图 / 柱状图数据。
 *
 * @param title     图表标题
 * @param chartType 图表类型：pie / bar
 * @param points    数据点
 * @author pakeplus
 */
public record ChartVO(String title, String chartType, List<ChartPointVO> points) {
}
