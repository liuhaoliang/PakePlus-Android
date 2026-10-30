package com.oaikes.pakeplus.demo.model.vo;

import java.util.List;

/**
 * 折线图数据系列。
 *
 * @param name   系列名称（维度取值，如 INTERNAL / 财务部）
 * @param values 与 X 轴刻度一一对应的调用次数
 * @author pakeplus
 */
public record LineSeriesVO(String name, List<Long> values) {
}
