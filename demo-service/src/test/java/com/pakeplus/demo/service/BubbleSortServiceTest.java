package com.pakeplus.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.pakeplus.demo.common.BusinessException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("BubbleSortService 单元测试")
class BubbleSortServiceTest {

    private final BubbleSortService bubbleSortService = new BubbleSortService();

    @Test
    @DisplayName("升序排序结果与比较/交换次数正确")
    void should_sortAscending_when_orderAsc() {
        BubbleSortService.SortOutcome outcome = bubbleSortService.sort(Arrays.asList(5, 1, 4, 2, 8), "asc");

        assertThat(outcome.sorted()).containsExactly(1, 2, 4, 5, 8);
        assertThat(outcome.size()).isEqualTo(5);
        assertThat(outcome.comparisons()).isPositive();
        assertThat(outcome.swaps()).isPositive();
    }

    @Test
    @DisplayName("降序排序结果正确")
    void should_sortDescending_when_orderDesc() {
        BubbleSortService.SortOutcome outcome = bubbleSortService.sort(Arrays.asList(3, 9, 1), "desc");

        assertThat(outcome.sorted()).containsExactly(9, 3, 1);
    }

    @Test
    @DisplayName("已排序数组只做一轮比较且无交换")
    void should_notSwap_when_arrayAlreadySorted() {
        BubbleSortService.SortOutcome outcome = bubbleSortService.sort(Arrays.asList(1, 2, 3, 4), "asc");

        assertThat(outcome.swaps()).isZero();
        assertThat(outcome.comparisons()).isEqualTo(3);
    }

    @Test
    @DisplayName("重复元素保持稳定结果")
    void should_sortDuplicateValues() {
        BubbleSortService.SortOutcome outcome = bubbleSortService.sort(Arrays.asList(2, 2, 1, 1), "asc");

        assertThat(outcome.sorted()).containsExactly(1, 1, 2, 2);
    }

    @Test
    @DisplayName("入参数组不被修改")
    void should_notModifyInput_when_sorting() {
        List<Integer> input = new ArrayList<>(Arrays.asList(3, 2, 1));
        bubbleSortService.sort(input, "asc");

        assertThat(input).containsExactly(3, 2, 1);
    }

    @Test
    @DisplayName("空数组抛出参数异常")
    void should_throwBusinessException_when_valuesEmpty() {
        assertThatThrownBy(() -> bubbleSortService.sort(List.of(), "asc"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("待排序数组不能为空");
    }

    @Test
    @DisplayName("数组包含空元素抛出参数异常")
    void should_throwBusinessException_when_valuesContainNull() {
        List<Integer> values = Arrays.asList(1, null, 3);
        assertThatThrownBy(() -> bubbleSortService.sort(values, "asc"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("不能包含空元素");
    }
}
