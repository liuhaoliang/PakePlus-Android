package com.oaikes.pakeplus.demo.service.impl;

import com.oaikes.pakeplus.demo.common.BizException;
import com.oaikes.pakeplus.demo.common.ErrorCodeEnum;
import com.oaikes.pakeplus.demo.constant.DemoConstants;
import com.oaikes.pakeplus.demo.enums.DemoApiEnum;
import com.oaikes.pakeplus.demo.enums.HashAlgorithmEnum;
import com.oaikes.pakeplus.demo.model.dto.ExportQuery;
import com.oaikes.pakeplus.demo.model.dto.ReportQuery;
import com.oaikes.pakeplus.demo.model.vo.ChartPointVO;
import com.oaikes.pakeplus.demo.model.vo.ExportFileVO;
import com.oaikes.pakeplus.demo.model.vo.HashVO;
import com.oaikes.pakeplus.demo.model.vo.HelloWorldVO;
import com.oaikes.pakeplus.demo.model.vo.LineSeriesVO;
import com.oaikes.pakeplus.demo.model.vo.ReportVO;
import com.oaikes.pakeplus.demo.model.vo.SortVO;
import com.oaikes.pakeplus.demo.service.ExportService;
import com.oaikes.pakeplus.demo.service.HashService;
import com.oaikes.pakeplus.demo.service.HelloWorldService;
import com.oaikes.pakeplus.demo.service.ReportService;
import com.oaikes.pakeplus.demo.service.SortService;
import com.oaikes.pakeplus.demo.util.CsvBuilder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 结果导出服务实现：按页面编码导出对应 Tab 的展示结果，支持一次性导出全部页面。
 *
 * @author pakeplus
 */
@Service
public class ExportServiceImpl implements ExportService {

    /** 全部页面导出编码。 */
    private static final String PAGE_ALL = "all";

    /** 默认哈希示例文本。 */
    private static final String DEFAULT_CONTENT = "Hello PakePlus";

    /** 默认排序示例数组。 */
    private static final String DEFAULT_VALUES = "5,3,8,1,9,2,7";

    /** 导出文件 MIME 类型。 */
    private static final String CONTENT_TYPE_CSV = "text/csv";

    /** 导出文件名前缀。 */
    private static final String FILE_NAME_PREFIX = "demo-result-";

    /** 导出文件名后缀。 */
    private static final String FILE_NAME_SUFFIX = ".csv";

    private static final DateTimeFormatter FILE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final HelloWorldService helloWorldService;

    private final HashService hashService;

    private final SortService sortService;

    private final ReportService reportService;

    public ExportServiceImpl(HelloWorldService helloWorldService, HashService hashService,
                             SortService sortService, ReportService reportService) {
        this.helloWorldService = helloWorldService;
        this.hashService = hashService;
        this.sortService = sortService;
        this.reportService = reportService;
    }

    @Override
    public ExportFileVO exportResults(ExportQuery query) {
        String pageCode = resolvePageCode(query);
        CsvBuilder csvBuilder = new CsvBuilder();
        if (isPage(pageCode, DemoApiEnum.HELLO_WORLD)) {
            appendHelloSection(csvBuilder, query);
        }
        if (isPage(pageCode, DemoApiEnum.HASH)) {
            appendHashSection(csvBuilder, query);
        }
        if (isPage(pageCode, DemoApiEnum.SORT)) {
            appendSortSection(csvBuilder, query);
        }
        if (isPage(pageCode, DemoApiEnum.REPORT)) {
            appendReportSection(csvBuilder, query);
        }
        String fileName = FILE_NAME_PREFIX + pageCode + "-"
                + LocalDateTime.now().format(FILE_TIME_FORMATTER) + FILE_NAME_SUFFIX;
        return new ExportFileVO(fileName, CONTENT_TYPE_CSV,
                csvBuilder.build().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 追加 hello world 页面结果。
     *
     * @param csvBuilder CSV 构造器
     * @param query      导出条件
     */
    private void appendHelloSection(CsvBuilder csvBuilder, ExportQuery query) {
        HelloWorldVO helloWorldVO = helloWorldService.getGreeting(resolveContent(query));
        csvBuilder.appendRow("hello world 接口结果");
        csvBuilder.appendRow("消息", "服务端时间");
        csvBuilder.appendRow(helloWorldVO.message(), helloWorldVO.serverTime());
        csvBuilder.appendEmptyRow();
    }

    /**
     * 追加哈希算法页面结果，覆盖全部支持的算法。
     *
     * @param csvBuilder CSV 构造器
     * @param query      导出条件
     */
    private void appendHashSection(CsvBuilder csvBuilder, ExportQuery query) {
        String content = resolveContent(query);
        csvBuilder.appendRow("哈希算法接口结果");
        csvBuilder.appendRow("算法", "原文", "摘要", "摘要长度");
        for (HashAlgorithmEnum algorithm : HashAlgorithmEnum.values()) {
            HashVO hashVO = hashService.getDigest(content, algorithm.getCode());
            csvBuilder.appendRow(hashVO.algorithm(), hashVO.content(), hashVO.digest(), hashVO.digestLength());
        }
        csvBuilder.appendEmptyRow();
    }

    /**
     * 追加冒泡排序页面结果。
     *
     * @param csvBuilder CSV 构造器
     * @param query      导出条件
     */
    private void appendSortSection(CsvBuilder csvBuilder, ExportQuery query) {
        List<Integer> values = parseValues(query);
        SortVO sortVO = sortService.getSortedValues(values);
        csvBuilder.appendRow("冒泡排序接口结果");
        csvBuilder.appendRow("原始数组", "排序结果", "比较次数", "交换次数", "耗时(ms)");
        csvBuilder.appendRow(sortVO.originalValues(), sortVO.sortedValues(),
                sortVO.compareCount(), sortVO.swapCount(), sortVO.costMillis());
        csvBuilder.appendEmptyRow();
    }

    /**
     * 追加调用情况报表页面结果。
     *
     * @param csvBuilder CSV 构造器
     * @param query      导出条件
     */
    private void appendReportSection(CsvBuilder csvBuilder, ExportQuery query) {
        ReportVO reportVO = reportService.getReport(new ReportQuery(
                query == null ? null : query.dimensionCode(),
                query == null ? 0 : query.days(),
                null));
        csvBuilder.appendRow("调用情况报表");
        csvBuilder.appendRow("统计维度", "调用总次数", "调用人数", "成功次数", "失败次数");
        csvBuilder.appendRow(reportVO.dimensionName(), reportVO.totalCount(),
                reportVO.distinctCallerCount(), reportVO.successCount(), reportVO.failCount());
        csvBuilder.appendEmptyRow();

        csvBuilder.appendRow("各接口调用次数");
        csvBuilder.appendRow("接口", "调用次数");
        for (ChartPointVO point : reportVO.apiPoints()) {
            csvBuilder.appendRow(point.name(), point.value());
        }
        csvBuilder.appendEmptyRow();

        csvBuilder.appendRow("按维度调用次数（" + reportVO.dimensionName() + "）");
        csvBuilder.appendRow("维度取值", "调用次数");
        for (ChartPointVO point : reportVO.barChart().points()) {
            csvBuilder.appendRow(point.name(), point.value());
        }
        csvBuilder.appendEmptyRow();

        csvBuilder.appendRow("调用趋势（按日期）");
        csvBuilder.appendRow(buildTrendHeader(reportVO));
        for (int index = 0; index < reportVO.lineChart().axis().size(); index++) {
            List<Object> row = new ArrayList<>();
            row.add(reportVO.lineChart().axis().get(index));
            for (LineSeriesVO series : reportVO.lineChart().series()) {
                row.add(series.values().get(index));
            }
            csvBuilder.appendRow(row.toArray());
        }
        csvBuilder.appendEmptyRow();
    }

    /**
     * 构造趋势表头：日期 + 各折线名称。
     *
     * @param reportVO 报表数据
     * @return 表头单元格数组
     */
    private Object[] buildTrendHeader(ReportVO reportVO) {
        List<Object> header = new ArrayList<>();
        header.add("日期");
        for (LineSeriesVO series : reportVO.lineChart().series()) {
            header.add(series.name());
        }
        return header.toArray();
    }

    /**
     * 解析并归一化页面编码。
     *
     * @param query 导出条件
     * @return 页面编码，全部页面导出返回 all
     */
    private String resolvePageCode(ExportQuery query) {
        if (query == null || query.pageCode() == null || query.pageCode().isBlank()) {
            return PAGE_ALL;
        }
        String pageCode = query.pageCode().trim();
        if (PAGE_ALL.equalsIgnoreCase(pageCode)) {
            return PAGE_ALL;
        }
        DemoApiEnum apiEnum = DemoApiEnum.of(pageCode);
        if (apiEnum == null || DemoApiEnum.EXPORT == apiEnum) {
            throw new BizException(ErrorCodeEnum.PARAM_INVALID,
                    "不支持的导出页面: " + pageCode + "，可选值: hello-world, hash, sort, report, all");
        }
        return apiEnum.getCode();
    }

    /**
     * 判断本次导出是否包含指定页面。
     *
     * @param pageCode 页面编码
     * @param apiEnum  接口枚举
     * @return 是否包含
     */
    private boolean isPage(String pageCode, DemoApiEnum apiEnum) {
        return PAGE_ALL.equalsIgnoreCase(pageCode) || apiEnum.getCode().equalsIgnoreCase(pageCode);
    }

    /**
     * 解析导出使用的原文。
     *
     * @param query 导出条件
     * @return 原文
     */
    private String resolveContent(ExportQuery query) {
        if (query == null || query.content() == null || query.content().isBlank()) {
            return DEFAULT_CONTENT;
        }
        return query.content().trim();
    }

    /**
     * 解析导出使用的排序数组。
     *
     * @param query 导出条件
     * @return 整数列表
     */
    private List<Integer> parseValues(ExportQuery query) {
        String rawValues = (query == null || query.values() == null || query.values().isBlank())
                ? DEFAULT_VALUES
                : query.values().trim();
        List<Integer> values = new ArrayList<>();
        for (String item : rawValues.split(",")) {
            String trimmed = item.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                values.add(Integer.valueOf(trimmed));
            } catch (NumberFormatException exception) {
                throw new BizException(ErrorCodeEnum.PARAM_INVALID,
                        "排序数组存在非数字元素: " + trimmed, exception);
            }
        }
        if (values.isEmpty()) {
            throw new BizException(ErrorCodeEnum.PARAM_INVALID, "待排序数组不能为空");
        }
        return values;
    }
}
