package com.oaikes.pakeplus.demo.model.vo;

import java.util.List;

/**
 * 冒泡排序接口返回结果。
 *
 * @param originalValues 原始数组
 * @param sortedValues   排序后数组
 * @param compareCount   比较次数
 * @param swapCount      交换次数
 * @param costMillis     排序耗时（毫秒）
 * @author pakeplus
 */
public record SortVO(
        List<Integer> originalValues,
        List<Integer> sortedValues,
        long compareCount,
        long swapCount,
        long costMillis) {
}
