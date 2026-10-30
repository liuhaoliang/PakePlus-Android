package com.oaikes.pakeplus.demo.model.vo;

/**
 * 图表数据点：饼图、柱状图与明细列表通用。
 *
 * @param name  维度名称，如人员类型、部门名称
 * @param value 调用次数
 * @author pakeplus
 */
public record ChartPointVO(String name, long value) {
}
