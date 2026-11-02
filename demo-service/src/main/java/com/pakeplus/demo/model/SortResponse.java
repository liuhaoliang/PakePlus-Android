package com.pakeplus.demo.model;

import java.util.List;

/**
 * 冒泡排序响应。
 *
 * @param original    原始数组
 * @param sorted      排序结果
 * @param order       排序方向
 * @param comparisons 比较次数
 * @param swaps       交换次数
 * @param elapsedMs   耗时（毫秒，保留 3 位小数）
 */
public record SortResponse(List<Integer> original, List<Integer> sorted, String order,
                           int comparisons, int swaps, double elapsedMs) {
}
