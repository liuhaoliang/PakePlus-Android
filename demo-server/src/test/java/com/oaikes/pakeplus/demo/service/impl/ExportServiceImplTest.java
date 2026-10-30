package com.oaikes.pakeplus.demo.service.impl;

import com.oaikes.pakeplus.demo.common.BizException;
import com.oaikes.pakeplus.demo.model.dto.ExportQuery;
import com.oaikes.pakeplus.demo.model.dto.ReportQuery;
import com.oaikes.pakeplus.demo.model.vo.ChartPointVO;
import com.oaikes.pakeplus.demo.model.vo.ChartVO;
import com.oaikes.pakeplus.demo.model.vo.ExportFileVO;
import com.oaikes.pakeplus.demo.model.vo.LineChartVO;
import com.oaikes.pakeplus.demo.model.vo.LineSeriesVO;
import com.oaikes.pakeplus.demo.model.vo.ReportVO;
import com.oaikes.pakeplus.demo.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * {@link ExportServiceImpl} 单元测试。
 *
 * @author pakeplus
 */
@ExtendWith(MockitoExtension.class)
class ExportServiceImplTest {

    @Mock
    private ReportService reportService;

    private ExportServiceImpl exportService;

    @BeforeEach
    void setUp() {
        exportService = new ExportServiceImpl(
                new HelloWorldServiceImpl(), new HashServiceImpl(), new SortServiceImpl(), reportService);
    }

    @Test
    @DisplayName("导出全部页面时包含四类结果分区")
    void should_exportAllSections_when_pageIsAll() {
        stubReport();

        ExportFileVO exportFileVO = exportService.exportResults(new ExportQuery("all", null, null, null, 7));

        String content = new String(exportFileVO.content(), StandardCharsets.UTF_8);
        assertThat(exportFileVO.fileName()).startsWith("demo-result-all-").endsWith(".csv");
        assertThat(exportFileVO.contentType()).isEqualTo("text/csv");
        assertThat(content).contains("hello world 接口结果");
        assertThat(content).contains("哈希算法接口结果");
        assertThat(content).contains("冒泡排序接口结果");
        assertThat(content).contains("调用情况报表");
    }

    @Test
    @DisplayName("仅导出哈希页面时包含全部算法且不含其他页面")
    void should_exportHashSectionOnly_when_pageIsHash() {
        ExportFileVO exportFileVO = exportService.exportResults(
                new ExportQuery("hash", "pakeplus", null, null, 7));

        String content = new String(exportFileVO.content(), StandardCharsets.UTF_8);
        assertThat(content).contains("sha-256").contains("sha-512").contains("md5");
        assertThat(content).contains("pakeplus");
        assertThat(content).doesNotContain("冒泡排序接口结果");
        assertThat(content).doesNotContain("调用情况报表");
    }

    @Test
    @DisplayName("排序页面导出使用入参数组并输出比较交换次数")
    void should_exportSortResult_when_valuesProvided() {
        ExportFileVO exportFileVO = exportService.exportResults(
                new ExportQuery("sort", null, "9,1,5", null, 7));

        String content = new String(exportFileVO.content(), StandardCharsets.UTF_8);
        assertThat(content).contains("冒泡排序接口结果");
        assertThat(content).contains("比较次数").contains("交换次数");
        assertThat(content).contains("[1, 5, 9]");
    }

    @Test
    @DisplayName("排序入参含非数字时抛出参数异常")
    void should_throwException_when_valuesNotNumeric() {
        assertThatThrownBy(() -> exportService.exportResults(
                new ExportQuery("sort", null, "9,abc", null, 7)))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("排序数组存在非数字元素");
    }

    @Test
    @DisplayName("不支持的导出页面抛出参数异常")
    void should_throwException_when_pageUnsupported() {
        assertThatThrownBy(() -> exportService.exportResults(
                new ExportQuery("unknown-page", null, null, null, 7)))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("不支持的导出页面");
    }

    /**
     * 构造报表服务的固定返回。
     */
    private void stubReport() {
        ChartPointVO apiPoint = new ChartPointVO("哈希算法", 2L);
        ChartPointVO dimensionPoint = new ChartPointVO("技术部", 2L);
        ChartVO pieChart = new ChartVO("按人员部门调用次数", "pie", List.of(dimensionPoint));
        ChartVO barChart = new ChartVO("按人员部门调用次数", "bar", List.of(dimensionPoint));
        LineChartVO lineChart = new LineChartVO(List.of("2026-10-30"), List.of(new LineSeriesVO("技术部", List.of(2L))));
        when(reportService.getReport(org.mockito.ArgumentMatchers.any(ReportQuery.class)))
                .thenReturn(new ReportVO(2L, 1L, 2L, 0L, "userDept", "人员部门",
                        List.of(apiPoint), pieChart, barChart, lineChart));
    }
}
