package com.pakeplus.demo.service;

import com.pakeplus.demo.common.BusinessException;
import com.pakeplus.demo.common.ErrorCodeEnum;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 冒泡排序示例服务，输出排序结果与比较/交换次数。
 */
@Service
public class BubbleSortService {

    private static final int MAX_SIZE = 2000;
    private static final String ORDER_DESC = "desc";

    /**
     * 执行冒泡排序。
     *
     * @param values 待排序数组
     * @param order  排序方向 asc / desc，缺省 asc
     * @return 排序结果及统计信息
     */
    public SortOutcome sort(List<Integer> values, String order) {
        if (values == null || values.isEmpty()) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMETER, "待排序数组不能为空");
        }
        if (values.size() > MAX_SIZE) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMETER, "待排序数组长度不能超过 " + MAX_SIZE);
        }
        if (values.contains(null)) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMETER, "待排序数组不能包含空元素");
        }
        boolean descending = ORDER_DESC.equalsIgnoreCase(order == null ? "" : order.trim());
        List<Integer> data = new ArrayList<>(values);
        int comparisons = 0;
        int swaps = 0;
        int length = data.size();
        for (int end = length - 1; end > 0; end--) {
            boolean swappedInRound = false;
            for (int index = 0; index < end; index++) {
                comparisons++;
                boolean shouldSwap = descending
                        ? data.get(index) < data.get(index + 1)
                        : data.get(index) > data.get(index + 1);
                if (shouldSwap) {
                    Integer current = data.get(index);
                    data.set(index, data.get(index + 1));
                    data.set(index + 1, current);
                    swaps++;
                    swappedInRound = true;
                }
            }
            if (!swappedInRound) {
                break;
            }
        }
        return new SortOutcome(List.copyOf(data), comparisons, swaps, data.size());
    }

    /**
     * 排序结果。
     *
     * @param sorted      排序后的数组
     * @param comparisons 比较次数
     * @param swaps       交换次数
     * @param size        元素个数
     */
    public record SortOutcome(List<Integer> sorted, int comparisons, int swaps, int size) {
    }
}
