package com.pakeplus.demo.export;

import com.pakeplus.demo.common.BusinessException;
import com.pakeplus.demo.common.ErrorCodeEnum;
import com.pakeplus.demo.service.BubbleSortService;
import com.pakeplus.demo.service.HashAlgorithmService;
import com.pakeplus.demo.service.HelloWorldService;
import com.pakeplus.demo.track.CallRecord;
import com.pakeplus.demo.track.TrackDimensionEnum;
import com.pakeplus.demo.track.TrackItemVO;
import com.pakeplus.demo.track.TrackReportService;
import com.pakeplus.demo.track.TrackSummaryVO;
import com.pakeplus.demo.track.TrackTrendVO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

/**
 * 导出服务：把各页面展示结果渲染为 CSV。
 */
@Service
public class ExportService {

    private static final DateTimeFormatter FILE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final String MODULE_HELLO = "hello";
    private static final String MODULE_HASH = "hash";
    private static final String MODULE_SORT = "sort";
    private static final String MODULE_TRACK = "track";
    private static final String CRLF = "\r\n";
    private static final String COMMA = ",";

    private final HelloWorldService helloWorldService;
    private final HashAlgorithmService hashAlgorithmService;
    private final BubbleSortService bubbleSortService;
    private final TrackReportService trackReportService;

    public ExportService(HelloWorldService helloWorldService,
                         HashAlgorithmService hashAlgorithmService,
                         BubbleSortService bubbleSortService,
                         TrackReportService trackReportService) {
        this.helloWorldService = helloWorldService;
        this.hashAlgorithmService = hashAlgorithmService;
        this.bubbleSortService = bubbleSortService;
        this.trackReportService = trackReportService;
    }

    /**
     * 导出指定页面的展示结果。
     *
     * @param module   页面标识：hello / hash / sort / track
     * @param name     Hello 页面参数
     * @param text     哈希页面参数
     * @param algorithm 哈希算法
     * @param values   排序页面参数（逗号分隔）
     * @param order    排序方向
     * @param dimension 埋点报表维度
     * @return 导出结果
     */
    public ExportResult export(String module, String name, String text, String algorithm,
                               List<Integer> values, String order, String dimension) {
        if (module == null || module.isBlank()) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMETER, "导出模块不能为空");
        }
        String normalizedModule = module.trim().toLowerCase(Locale.ROOT);
        String fileNameSuffix = LocalDateTime.now().format(FILE_TIME_FORMATTER);
        return switch (normalizedModule) {
            case MODULE_HELLO -> new ExportResult("hello-" + fileNameSuffix + ".csv", exportHello(name));
            case MODULE_HASH -> new ExportResult("hash-" + fileNameSuffix + ".csv", exportHash(text, algorithm));
            case MODULE_SORT -> new ExportResult("sort-" + fileNameSuffix + ".csv", exportSort(values, order));
            case MODULE_TRACK -> new ExportResult("track-" + fileNameSuffix + ".csv",
                    exportTrack(TrackDimensionEnum.fromCode(dimension)));
            default -> throw new BusinessException(ErrorCodeEnum.INVALID_PARAMETER, "不支持的导出模块: " + module);
        };
    }

    private String exportHello(String name) {
        StringBuilder builder = new StringBuilder();
        builder.append("字段,值").append(CRLF);
        builder.append("问候语,").append(escape(helloWorldService.greet(name))).append(CRLF);
        builder.append("服务器时间,").append(escape(helloWorldService.currentTime())).append(CRLF);
        return builder.toString();
    }

    private String exportHash(String text, String algorithm) {
        String resolvedAlgorithm = hashAlgorithmService.normalizeAlgorithm(algorithm);
        String digest = hashAlgorithmService.digest(text == null ? "" : text, resolvedAlgorithm);
        StringBuilder builder = new StringBuilder();
        builder.append("字段,值").append(CRLF);
        builder.append("算法,").append(escape(resolvedAlgorithm)).append(CRLF);
        builder.append("原文,").append(escape(text)).append(CRLF);
        builder.append("摘要,").append(escape(digest)).append(CRLF);
        builder.append("摘要长度,").append(digest.length()).append(CRLF);
        return builder.toString();
    }

    private String exportSort(List<Integer> values, String order) {
        BubbleSortService.SortOutcome outcome = bubbleSortService.sort(values, order);
        StringBuilder builder = new StringBuilder();
        builder.append("排序方向,").append(escape(outcome == null ? "" : order == null ? "asc" : order)).append(CRLF);
        builder.append("比较次数,").append(outcome.comparisons()).append(CRLF);
        builder.append("交换次数,").append(outcome.swaps()).append(CRLF);
        builder.append("元素个数,").append(outcome.size()).append(CRLF);
        builder.append(CRLF).append("序号,排序结果").append(CRLF);
        List<Integer> sorted = outcome.sorted();
        for (int index = 0; index < sorted.size(); index++) {
            builder.append(index + 1).append(COMMA).append(sorted.get(index)).append(CRLF);
        }
        return builder.toString();
    }

    private String exportTrack(TrackDimensionEnum dimension) {
        TrackSummaryVO summary = trackReportService.summarize(dimension);
        StringBuilder builder = new StringBuilder();
        builder.append("统计维度,").append(escape(summary.dimensionLabel())).append(CRLF);
        builder.append("总调用次数,").append(summary.total()).append(CRLF);
        builder.append("成功次数,").append(summary.successCount()).append(CRLF);
        builder.append("失败次数,").append(summary.failCount()).append(CRLF);
        builder.append("平均耗时(ms),").append(summary.avgElapsedMs()).append(CRLF);
        builder.append("调用人数,").append(summary.callerCount()).append(CRLF);
        builder.append("生成时间,").append(escape(summary.generatedAt())).append(CRLF);
        builder.append(CRLF).append("分组,调用次数,占比").append(CRLF);
        List<TrackItemVO> items = summary.items();
        for (TrackItemVO item : items) {
            builder.append(escape(item.key())).append(COMMA).append(item.count()).append(COMMA)
                    .append(item.ratio()).append(CRLF);
        }
        builder.append(CRLF).append("时间桶,调用次数").append(CRLF);
        List<TrackTrendVO> trend = summary.trend();
        for (TrackTrendVO point : trend) {
            builder.append(escape(point.bucket())).append(COMMA).append(point.count()).append(CRLF);
        }
        return builder.toString();
    }

    /**
     * 调用明细导出（最近记录）。
     *
     * @param limit 记录数量
     * @return 导出结果
     */
    public ExportResult exportTrackRecords(Integer limit) {
        List<CallRecord> records = trackReportService.latestRecords(limit);
        StringBuilder builder = new StringBuilder();
        builder.append("调用时间,接口,人员ID,姓名,人员类型,人员层级,人员部门,是否成功,耗时(ms)").append(CRLF);
        for (CallRecord record : records) {
            List<String> columns = new ArrayList<>();
            columns.add(record.calledAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            columns.add(record.apiPath());
            columns.add(record.userId());
            columns.add(record.userName());
            columns.add(record.userType());
            columns.add(record.userLevel());
            columns.add(record.userDept());
            columns.add(record.success() ? "是" : "否");
            columns.add(String.valueOf(record.elapsedMs()));
            builder.append(String.join(COMMA, columns.stream().map(this::escape).toList())).append(CRLF);
        }
        return new ExportResult("track-records-" + LocalDateTime.now().format(FILE_TIME_FORMATTER) + ".csv",
                builder.toString());
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(COMMA) || escaped.contains("\"") || escaped.contains("\n") || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
