package com.oaikes.pakeplus.demo.service.impl;

import com.oaikes.pakeplus.demo.common.BizException;
import com.oaikes.pakeplus.demo.model.vo.SortVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link SortServiceImpl} 单元测试。
 *
 * @author pakeplus
 */
class SortServiceImplTest {

    private final SortServiceImpl sortService = new SortServiceImpl();

    @Test
    @DisplayName("乱序数组升序排序并统计比较交换次数")
    void should_returnSortedValues_when_valuesUnordered() {
        SortVO sortVO = sortService.getSortedValues(Arrays.asList(5, 3, 8, 1));

        assertThat(sortVO.sortedValues()).containsExactly(1, 3, 5, 8);
        assertThat(sortVO.originalValues()).containsExactly(5, 3, 8, 1);
        assertThat(sortVO.compareCount()).isGreaterThan(0L);
        assertThat(sortVO.swapCount()).isGreaterThan(0L);
    }

    @Test
    @DisplayName("已排序数组在首轮后提前退出，无交换")
    void should_notSwap_when_valuesAlreadySorted() {
        SortVO sortVO = sortService.getSortedValues(Arrays.asList(1, 2, 3, 4));

        assertThat(sortVO.sortedValues()).containsExactly(1, 2, 3, 4);
        assertThat(sortVO.swapCount()).isZero();
        assertThat(sortVO.compareCount()).isEqualTo(3L);
    }

    @Test
    @DisplayName("单元素数组原样返回")
    void should_returnSameList_when_singleElement() {
        SortVO sortVO = sortService.getSortedValues(Collections.singletonList(7));

        assertThat(sortVO.sortedValues()).containsExactly(7);
        assertThat(sortVO.compareCount()).isZero();
    }

    @Test
    @DisplayName("不修改调用方传入的数组")
    void should_notModifyInput_when_sorted() {
        List<Integer> input = new ArrayList<>(Arrays.asList(9, 2, 6));

        sortService.getSortedValues(input);

        assertThat(input).containsExactly(9, 2, 6);
    }

    @Test
    @DisplayName("空数组抛出参数异常")
    void should_throwException_when_valuesEmpty() {
        assertThatThrownBy(() -> sortService.getSortedValues(Collections.emptyList()))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("不能为空");
    }

    @Test
    @DisplayName("含 null 元素抛出参数异常")
    void should_throwException_when_valueNull() {
        assertThatThrownBy(() -> sortService.getSortedValues(Arrays.asList(1, null)))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("不能为 null");
    }

    @Test
    @DisplayName("元素个数超过上限抛出参数异常")
    void should_throwException_when_sizeExceeded() {
        List<Integer> values = new ArrayList<>();
        for (int index = 0; index < 1001; index++) {
            values.add(index % 10);
        }

        assertThatThrownBy(() -> sortService.getSortedValues(values))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("不能超过");
    }
}
