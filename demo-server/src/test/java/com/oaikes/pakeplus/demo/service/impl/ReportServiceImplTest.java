package com.oaikes.pakeplus.demo.service.impl;

import com.oaikes.pakeplus.demo.common.BizException;
import com.oaikes.pakeplus.demo.constant.DemoConstants;
import com.oaikes.pakeplus.demo.model.dto.InvocationTrackCommand;
import com.oaikes.pakeplus.demo.model.dto.ReportQuery;
import com.oaikes.pakeplus.demo.model.entity.InvocationRecordDO;
import com.oaikes.pakeplus.demo.model.vo.ChartPointVO;
import com.oaikes.pakeplus.demo.model.vo.LineSeriesVO;
import com.oaikes.pakeplus.demo.model.vo.ReportVO;
import com.oaikes.pakeplus.demo.service.InvocationTrackService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * {@link ReportServiceImpl} 单元测试。
 *
 * @author pakeplus
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(DemoConstants.DATE_PATTERN);

    @Mock
    private InvocationTrackService invocationTrackService;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Test
    @DisplayName("按人员类型维度聚合调用次数并产出三类图表数据")
    void should_aggregateByUserType_when_dimensionAbsent() {
        when(invocationTrackService.listRecords(any(), any())).thenReturn(buildRecords());

        ReportVO reportVO = reportService.getReport(new ReportQuery(null, 7, null));

        assertThat(reportVO.dimensionCode()).isEqualTo("userType");
        assertThat(reportVO.dimensionName()).isEqualTo("人员类型");
        assertThat(reportVO.totalCount()).isEqualTo(4L);
        assertThat(reportVO.successCount()).isEqualTo(3L);
        assertThat(reportVO.failCount()).isEqualTo(1L);
        assertThat(reportVO.distinctCallerCount()).isEqualTo(3L);
        assertThat(reportVO.pieChart().chartType()).isEqualTo("pie");
        assertThat(reportVO.barChart().chartType()).isEqualTo("bar");
        assertThat(reportVO.pieChart().points()).extracting(ChartPointVO::name)
                .containsExactly("INTERNAL", "EXTERNAL");
        assertThat(reportVO.pieChart().points()).extracting(ChartPointVO::value).containsExactly(3L, 1L);
        assertThat(reportVO.apiPoints()).extracting(ChartPointVO::name)
                .containsExactly("哈希算法", "hello world", "冒泡排序");
        assertThat(reportVO.lineChart().axis()).hasSize(7);
        assertThat(reportVO.lineChart().axis().get(6)).isEqualTo(LocalDate.now().format(DATE_FORMATTER));
        assertThat(reportVO.lineChart().series()).extracting(LineSeriesVO::name)
                .containsExactly("EXTERNAL", "INTERNAL");
        assertThat(sumOfSeries(reportVO, "INTERNAL")).isEqualTo(3L);
        assertThat(sumOfSeries(reportVO, "EXTERNAL")).isEqualTo(1L);
    }

    @Test
    @DisplayName("按人员部门维度聚合调用次数")
    void should_aggregateByUserDept_when_dimensionIsUserDept() {
        when(invocationTrackService.listRecords(any(), any())).thenReturn(buildRecords());

        ReportVO reportVO = reportService.getReport(new ReportQuery("userDept", 30, null));

        assertThat(reportVO.dimensionCode()).isEqualTo("userDept");
        assertThat(reportVO.pieChart().points()).extracting(ChartPointVO::name)
                .containsExactlyInAnyOrder("财务部", "技术部");
        assertThat(reportVO.lineChart().axis()).hasSize(30);
    }

    @Test
    @DisplayName("统计天数超出上限时收敛为 90 天")
    void should_clampDays_when_daysExceedMax() {
        when(invocationTrackService.listRecords(any(), any())).thenReturn(new ArrayList<>());

        ReportVO reportVO = reportService.getReport(new ReportQuery("userLevel", 999, null));

        assertThat(reportVO.lineChart().axis()).hasSize(90);
        assertThat(reportVO.totalCount()).isZero();
        assertThat(reportVO.pieChart().points()).isEmpty();
    }

    @Test
    @DisplayName("接口编码透传给埋点查询")
    void should_passApiCode_when_queryContainsApiCode() {
        when(invocationTrackService.listRecords(eq("hash"), any())).thenReturn(buildRecords());

        ReportVO reportVO = reportService.getReport(new ReportQuery("userType", 7, "hash"));

        assertThat(reportVO.totalCount()).isEqualTo(4L);
    }

    @Test
    @DisplayName("不支持的维度抛出参数异常")
    void should_throwException_when_dimensionUnsupported() {
        assertThatThrownBy(() -> reportService.getReport(new ReportQuery("userSex", 7, null)))
                .isInstanceOf(BizException.class)
                .hasMessageContaining("不支持的统计维度");
    }

    /**
     * 汇总某条折线在全部日期上的调用次数。
     *
     * @param reportVO  报表数据
     * @param seriesName 折线名称
     * @return 调用次数合计
     */
    private long sumOfSeries(ReportVO reportVO, String seriesName) {
        return reportVO.lineChart().series().stream()
                .filter(series -> seriesName.equals(series.name()))
                .flatMap(series -> series.values().stream())
                .mapToLong(Long::longValue)
                .sum();
    }

    /**
     * 构造测试用调用记录。
     *
     * @return 调用记录列表
     */
    private List<InvocationRecordDO> buildRecords() {
        List<InvocationRecordDO> records = new ArrayList<>();
        records.add(buildRecord("1", "hello-world", "hello world", "u1", "张三", "INTERNAL", "P6", "技术部", true));
        records.add(buildRecord("2", "hash", "哈希算法", "u1", "张三", "INTERNAL", "P6", "技术部", true));
        records.add(buildRecord("3", "hash", "哈希算法", "u2", "李四", "INTERNAL", "P7", "财务部", true));
        records.add(buildRecord("4", "sort", "冒泡排序", "u3", "王五", "EXTERNAL", "P5", "财务部", false));
        return records;
    }

    /**
     * 构造单条调用记录。
     *
     * @param recordId  记录 ID
     * @param apiCode   接口编码
     * @param apiName   接口名称
     * @param callerId  调用人 ID
     * @param callerName 调用人姓名
     * @param userType  人员类型
     * @param userLevel 人员层级
     * @param userDept  人员部门
     * @param success   是否成功
     * @return 调用记录
     */
    private InvocationRecordDO buildRecord(String recordId, String apiCode, String apiName, String callerId,
                                           String callerName, String userType, String userLevel,
                                           String userDept, boolean success) {
        return new InvocationRecordDO(recordId, apiCode, apiName, callerId, callerName, userType,
                userLevel, userDept, LocalDateTime.now(), 12L, success, "summary");
    }
}
