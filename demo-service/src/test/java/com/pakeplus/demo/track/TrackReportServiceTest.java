package com.pakeplus.demo.track;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TrackReportService 单元测试")
class TrackReportServiceTest {

    private TrackStore trackStore;
    private TrackReportService trackReportService;

    @BeforeEach
    void setUp() {
        trackStore = new TrackStore();
        trackReportService = new TrackReportService(trackStore);
    }

    @Test
    @DisplayName("无数据时返回零值报表")
    void should_returnEmptySummary_when_noRecords() {
        TrackSummaryVO summary = trackReportService.summarize(TrackDimensionEnum.USER_TYPE);

        assertThat(summary.total()).isZero();
        assertThat(summary.items()).isEmpty();
        assertThat(summary.trend()).hasSize(12);
    }

    @Test
    @DisplayName("按人员类型分组统计次数与占比")
    void should_groupByUserType_when_dimensionUserType() {
        trackStore.append("/api/demo/hello", new UserIdentity("u1", "张三", "INTERNAL", "P6", "技术部"), true, 10L);
        trackStore.append("/api/demo/hash", new UserIdentity("u2", "李四", "INTERNAL", "P7", "产品部"), true, 20L);
        trackStore.append("/api/demo/sort", new UserIdentity("u3", "王五", "EXTERNAL", "P5", "运营部"), false, 30L);

        TrackSummaryVO summary = trackReportService.summarize(TrackDimensionEnum.USER_TYPE);

        assertThat(summary.total()).isEqualTo(3);
        assertThat(summary.successCount()).isEqualTo(2);
        assertThat(summary.failCount()).isEqualTo(1);
        assertThat(summary.callerCount()).isEqualTo(3);
        assertThat(summary.items()).extracting(TrackItemVO::key).containsExactly("INTERNAL", "EXTERNAL");
        assertThat(summary.items().get(0).count()).isEqualTo(2);
        assertThat(summary.items().get(0).ratio()).isEqualTo(0.6667);
    }

    @Test
    @DisplayName("按部门分组统计")
    void should_groupByDept_when_dimensionUserDept() {
        trackStore.append("/api/demo/hello", new UserIdentity("u1", "张三", "INTERNAL", "P6", "技术部"), true, 10L);
        trackStore.append("/api/demo/hello", new UserIdentity("u2", "李四", "INTERNAL", "P6", "技术部"), true, 10L);
        trackStore.append("/api/demo/hash", new UserIdentity("u3", "王五", "EXTERNAL", "P5", "运营部"), true, 10L);

        TrackSummaryVO summary = trackReportService.summarize(TrackDimensionEnum.USER_DEPT);

        assertThat(summary.dimensionCode()).isEqualTo("user_dept");
        assertThat(summary.items()).extracting(TrackItemVO::key).containsExactly("技术部", "运营部");
        assertThat(summary.items().get(0).count()).isEqualTo(2);
    }

    @Test
    @DisplayName("时间趋势包含当前小时调用")
    void should_countTrendBucket_when_recordInCurrentHour() {
        trackStore.append("/api/demo/hello", new UserIdentity("u1", "张三", "INTERNAL", "P6", "技术部"), true, 10L);

        TrackSummaryVO summary = trackReportService.summarize(TrackDimensionEnum.API);

        TrackTrendVO lastBucket = summary.trend().get(summary.trend().size() - 1);
        assertThat(lastBucket.count()).isEqualTo(1);
        assertThat(lastBucket.bucket()).isEqualTo(LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00")));
    }

    @Test
    @DisplayName("明细按时间倒序返回并限制数量")
    void should_returnLatestRecords_when_limitGiven() {
        trackStore.appendAt("/api/demo/hello", new UserIdentity("u1", "张三", "INTERNAL", "P6", "技术部"),
                true, 10L, LocalDateTime.now().minusHours(2));
        trackStore.appendAt("/api/demo/hash", new UserIdentity("u2", "李四", "INTERNAL", "P6", "技术部"),
                true, 10L, LocalDateTime.now().minusHours(1));

        assertThat(trackReportService.latestRecords(1)).hasSize(1)
                .first().extracting(CallRecord::apiPath).isEqualTo("/api/demo/hash");
        assertThat(trackReportService.latestRecords(0)).hasSize(1);
        assertThat(trackReportService.latestRecords(null)).hasSize(2);
    }
}
