package com.oaikes.pakeplus.demo.service;

import com.oaikes.pakeplus.demo.model.vo.SortVO;

import java.util.List;

/**
 * 冒泡排序服务。
 *
 * @author pakeplus
 */
public interface SortService {

    /**
     * 对整数列表执行升序冒泡排序。
     *
     * @param values 待排序数组，不能为空，元素个数不超过 1000
     * @return 排序结果，包含比较与交换次数
     */
    SortVO getSortedValues(List<Integer> values);
}
