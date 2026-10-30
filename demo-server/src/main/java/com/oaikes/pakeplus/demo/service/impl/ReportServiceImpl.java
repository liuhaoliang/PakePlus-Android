package com.oaikes.pakeplus.demo.service.impl;

import com.oaikes.pakeplus.demo.common.BizException;
import com.oaikes.pakeplus.demo.common.ErrorCodeEnum;
import com.oaikes.pakeplus.demo.constant.DemoConstants;
import com.oaikes.pakeplus.demo.enums.ReportDimensionEnum;
import com.oaikes.pakeplus.demo.model.dto.ReportQuery;
import com.oaikes.pakeplus.demo.model.entity.InvocationRecordDO;
import com.oaikes.pakeplus.demo.model.vo.ChartPointVO;
import com.oaikes.pakeplus.demo.model.vo.ChartVO;
import com.oaikes.pakeplus.demo.model.vo.LineChartVO;
import com.oaikes.pakeplus.demo.model.vo.LineSeriesVO;
import com.oaikes.pakeplus.demo.model.vo.ReportVO;
import com.oaikes.pakeplus.demo.service.InvocationTrackService;
import com.oaikes.pakeplus.demo.service.ReportService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 调用情况报表服务实现：按人员类型 / 人员层级 / 人员部门维度聚合调用次数，
 * 同时产出饼图、柱状图与折线图所需数据结构。
 *
 * @author pakeplus
 */
@Service
public class ReportServiceImpl implements ReportService {

    /** 默认统计维度。 */
    private static final ReportDimensionEnum DEFAULT_DIMENSION = ReportDimensionEnum.USER_TYPE;

    /** 默认统计天数。 */
    private static final int DEFAULT_DAYS = 7;

    /** 统计天数下限。 */
    private static final int MIN_DAYS = 1;

    /** 统计天数上限。 */
    private static final int MAX_DAYS = 90;

    /** 饼图 / 柱状图标题前缀。 */
    private static final String CHART_TITLE_PREFIX = "按";

    /** 饼图类型。 */
    private static final String CHART_TYPE_PIE = "pie";

    /** 柱状图类型。 */
    private static final String CHART_TYPE_BAR = "bar";

    /** 未知维度取值。 */
    private static final String UNKNOWN_DIMENSION_VALUE = "UNKNOWN";

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(DemoConstants.DATE_PATTERN);

    private final InvocationTrackService invocationTrackService;

    public ReportServiceImpl(InvocationTrackService invocationTrackService) {
        this.invocationTrackService = invocationTrackService;
    }

    @Override
    public ReportVO getReport(ReportQuery query) {
        ReportDimensionEnum dimension = resolveDimension(query);
        int days = resolveDays(query);
        String apiCode = query == null ? null : query.apiCode();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1L);
        List<InvocationRecordDO> records = invocationTrackService.listRecords(apiCode, startDate);

        long totalCount = records.size();
        long successCount = records.stream().filter(InvocationRecordDO::success).count();
        long failCount = totalCount - successCount;
        long distinctCallerCount = records.stream()
                .map(InvocationRecordDO::callerId)
                .distinct()
                .count();

        Map<String, Long> dimensionCounter = countByDimension(records, dimension);
        List<ChartPointVO> dimensionPoints = toChartPoints(dimensionCounter);
        String chartTitle = CHART_TITLE_PREFIX + dimension.getDescription() + "调用次数";
        ChartVO pieChart = new ChartVO(chartTitle, CHART_TYPE_PIE, dimensionPoints);
        ChartVO barChart = new ChartVO(chartTitle, CHART_TYPE_BAR, dimensionPoints);

        return new ReportVO(
                totalCount,
                distinctCallerCount,
                successCount,
                failCount,
                dimension.getCode(),
                dimension.getDescription(),
                toChartPoints(countByApi(records)),
                pieChart,
                barChart,
                buildLineChart(records, dimension, startDate, endDate));
    }

    /**
     * 解析统计维度，非法取值抛出参数异常。
     *
     * @param query 查询条件
     * @return 统计维度枚举
     */
    private ReportDimensionEnum resolveDimension(ReportQuery query) {
        if (query == null || query.dimensionCode() == null || query.dimensionCode().isBlank()) {
            return DEFAULT_DIMENSION;
        }
        ReportDimensionEnum dimension = ReportDimensionEnum.of(query.dimensionCode());
        if (dimension == null) {
            throw new BizException(ErrorCodeEnum.PARAM_INVALID,
                    "不支持的统计维度: " + query.dimensionCode() + "，可选值: userType, userLevel, userDept");
        }
        return dimension;
    }

    /**
     * 解析统计天数，超出范围时收敛到边界值。
     *
     * @param query 查询条件
     * @return 统计天数
     */
    private int resolveDays(ReportQuery query) {
        if (query == null || query.days() <= 0) {
            return DEFAULT_DAYS;
        }
        return Math.min(MAX_DAYS, Math.max(MIN_DAYS, query.days()));
    }

    /**
     * 按接口分组统计调用次数。
     *
     * @param records 调用记录
     * @return 接口编码到调用次数的映射，按次数降序
     */
    private Map<String, Long> countByApi(List<InvocationRecordDO> records) {
        Map<String, Long> counter = records.stream()
                .collect(Collectors.groupingBy(InvocationRecordDO::apiName, Collectors.counting()));
        return sortByValueDesc(counter);
    }

    /**
     * 按指定维度分组统计调用次数。
     *
     * @param records   调用记录
     * @param dimension 统计维度
     * @return 维度取值到调用次数的映射，按次数降序
     */
    private Map<String, Long> countByDimension(List<InvocationRecordDO> records, ReportDimensionEnum dimension) {
        Map<String, Long> counter = records.stream()
                .collect(Collectors.groupingBy(record -> dimensionValue(record, dimension), Collectors.counting()));
        return sortByValueDesc(counter);
    }

    /**
     * 提取记录在指定维度上的取值。
     *
     * @param record    调用记录
     * @param dimension 统计维度
     * @return 维度取值，缺失时返回 UNKNOWN
     */
    private String dimensionValue(InvocationRecordDO record, ReportDimensionEnum dimension) {
        String value = switch (dimension) {
            case USER_TYPE -> record.userType();
            case USER_LEVEL -> record.userLevel();
            case USER_DEPT -> record.userDept();
        };
        return (value == null || value.isBlank()) ? UNKNOWN_DIMENSION_VALUE : value;
    }

    /**
     * 构造折线图：横轴为日期，每个维度取值一条折线。
     *
     * @param records   调用记录
     * @param dimension 统计维度
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @return 折线图数据
     */
    private LineChartVO buildLineChart(List<InvocationRecordDO> records, ReportDimensionEnum dimension,
                                       LocalDate startDate, LocalDate endDate) {
        List<String> axis = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1L)) {
            axis.add(date.format(DATE_FORMATTER));
        }
        Map<String, Map<String, Long>> seriesCounter = new TreeMap<>();
        for (InvocationRecordDO record : records) {
            String seriesName = dimensionValue(record, dimension);
            String dateKey = record.invokeTime().toLocalDate().format(DATE_FORMATTER);
            seriesCounter.computeIfAbsent(seriesName, key -> new LinkedHashMap<>())
                    .merge(dateKey, 1L, Long::sum);
        }
        List<LineSeriesVO> series = new ArrayList<>();
        for (Map.Entry<String, Map<String, Long>> entry : seriesCounter.entrySet()) {
            List<Long> values = new ArrayList<>(axis.size());
            for (String dateKey : axis) {
                values.add(entry.getValue().getOrDefault(dateKey, 0L));
            }
            series.add(new LineSeriesVO(entry.getKey(), values));
        }
        return new LineChartVO(axis, series);
    }

    /**
     * 把计数字典转换为图表数据点。
     *
     * @param counter 计数字典
     * @return 图表数据点列表，无数据时返回空列表
     */
    private List<ChartPointVO> toChartPoints(Map<String, Long> counter) {
        return counter.entrySet().stream()
                .map(entry -> new ChartPointVO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * 按次数降序排序计数字典。
     *
     * @param counter 计数字典
     * @return 排序后的计数字典
     */
    private Map<String, Long> sortByValueDesc(Map<String, Long> counter) {
        return counter.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (left, right) -> left,
                        LinkedHashMap::new));
    }
}
