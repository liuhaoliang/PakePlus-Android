package com.oaikes.pakeplus.demo.service.impl;

import com.oaikes.pakeplus.demo.common.BizException;
import com.oaikes.pakeplus.demo.common.ErrorCodeEnum;
import com.oaikes.pakeplus.demo.constant.DemoConstants;
import com.oaikes.pakeplus.demo.model.vo.SortVO;
import com.oaikes.pakeplus.demo.service.SortService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 冒泡排序服务实现：升序排序，提前退出优化，并统计比较与交换次数。
 *
 * @author pakeplus
 */
@Service
public class SortServiceImpl implements SortService {

    @Override
    public SortVO getSortedValues(List<Integer> values) {
        List<Integer> originalValues = validateAndCopy(values);
        List<Integer> sortedValues = new ArrayList<>(originalValues);
        long compareCount = 0L;
        long swapCount = 0L;
        long startNanos = System.nanoTime();
        int length = sortedValues.size();
        for (int end = length - 1; end > 0; end--) {
            boolean swapped = false;
            for (int index = 0; index < end; index++) {
                compareCount++;
                if (sortedValues.get(index) > sortedValues.get(index + 1)) {
                    int temp = sortedValues.get(index);
                    sortedValues.set(index, sortedValues.get(index + 1));
                    sortedValues.set(index + 1, temp);
                    swapCount++;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
        long costMillis = (System.nanoTime() - startNanos) / 1_000_000L;
        return new SortVO(originalValues, sortedValues, compareCount, swapCount, costMillis);
    }

    /**
     * 校验入参并复制为可变列表，避免污染调用方数据。
     *
     * @param values 待排序数组
     * @return 校验通过后的副本
     */
    private List<Integer> validateAndCopy(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            throw new BizException(ErrorCodeEnum.PARAM_INVALID, "待排序数组不能为空");
        }
        if (values.size() > DemoConstants.MAX_SORT_SIZE) {
            throw new BizException(ErrorCodeEnum.PARAM_INVALID,
                    "待排序数组元素个数不能超过 " + DemoConstants.MAX_SORT_SIZE);
        }
        List<Integer> copy = new ArrayList<>(values.size());
        for (Integer value : values) {
            if (value == null) {
                throw new BizException(ErrorCodeEnum.PARAM_INVALID, "数组元素不能为 null");
            }
            if (Math.abs(value) > DemoConstants.MAX_SORT_VALUE) {
                throw new BizException(ErrorCodeEnum.PARAM_INVALID,
                        "数组元素绝对值不能超过 " + DemoConstants.MAX_SORT_VALUE);
            }
            copy.add(value);
        }
        return copy;
    }
}
