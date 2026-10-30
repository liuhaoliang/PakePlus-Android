package com.oaikes.pakeplus.demo.model.dto;

/**
 * 导出查询条件：指定导出的页面（Tab）与各页面的可选入参。
 *
 * @param pageCode      页面编码：hello-world / hash / sort / report / all
 * @param content       哈希页面入参原文，为空时使用默认示例文本
 * @param values        排序页面入参，逗号分隔的整数，为空时使用默认示例数组
 * @param dimensionCode 报表页面统计维度
 * @param days          报表页面统计天数
 * @author pakeplus
 */
public record ExportQuery(
        String pageCode,
        String content,
        String values,
        String dimensionCode,
        int days) {
}
