package com.oaikes.pakeplus.demo.model.vo;

import java.util.List;

/**
 * 折线图数据。
 *
 * @param axis   横轴刻度（日期，格式 yyyy-MM-dd）
 * @param series 折线系列
 * @author pakeplus
 */
public record LineChartVO(List<String> axis, List<LineSeriesVO> series) {
}
