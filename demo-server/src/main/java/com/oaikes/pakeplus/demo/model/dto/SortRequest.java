package com.oaikes.pakeplus.demo.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 冒泡排序请求。
 *
 * @param values 待排序的整数列表，元素个数 1~1000
 * @author pakeplus
 */
public record SortRequest(
        @NotEmpty(message = "待排序数组不能为空")
        @Size(max = 1000, message = "待排序数组元素个数不能超过 1000")
        List<@NotNull(message = "数组元素不能为 null") Integer> values) {
}
