package com.pakeplus.demo.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 冒泡排序请求。
 *
 * @param values 待排序数组
 * @param order  排序方向 asc / desc
 */
public record SortRequest(
        @NotEmpty(message = "待排序数组不能为空")
        @Size(max = 2000, message = "待排序数组长度不能超过 2000") List<Integer> values,
        String order) {
}
