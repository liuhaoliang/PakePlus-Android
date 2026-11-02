package com.pakeplus.demo.track;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * 埋点报表聚合服务：按维度分组、生成时间趋势。
 */
@Service
public class TrackReportService {

    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int TREND_HOURS = 12;
    private static final int DEFAULT_RECORD_LIMIT = 50;
    private static final int MAX_RECORD_LIMIT = 500;
    private static final int RATIO_SCALE = 10000;

    private final TrackStore trackStore;

    public TrackReportService(TrackStore trackStore) {
        this.trackStore = trackStore;
    }

    /**
     * 生成调用情况报表。
     *
     * @param dimension 统计维度
     * @return 报表
     */
    public TrackSummaryVO summarize(TrackDimensionEnum dimension) {
        List<CallRecord> records = trackStore.snapshot();
        Map<String, Long> groupedCounts = new LinkedHashMap<>();
        for (CallRecord record : records) {
            groupedCounts.merge(record.dimensionValue(dimension), 1L, Long::sum);
        }
        long total = records.size();
        List<TrackItemVO> items = groupedCounts.entrySet().stream()
                .map(entry -> new TrackItemVO(entry.getKey(), entry.getValue(), ratio(entry.getValue(), total)))
                .sorted(Comparator.comparingLong(TrackItemVO::count).reversed())
                .toList();
        return new TrackSummaryVO(total, countSuccess(records), total - countSuccess(records),
                averageElapsed(records), countDistinctCallers(records), dimension.getCode(), dimension.getLabel(),
                items, buildTrend(records), LocalDateTime.now().format(TIME_FORMATTER));
    }

    /**
     * 最近调用记录。
     *
     * @param limit 数量，缺省 50，最大 500
     * @return 记录列表
     */
    public List<CallRecord> latestRecords(Integer limit) {
        int resolvedLimit = limit == null ? DEFAULT_RECORD_LIMIT : Math.min(Math.max(limit, 1), MAX_RECORD_LIMIT);
        return trackStore.latest(resolvedLimit);
    }

    private long countSuccess(List<CallRecord> records) {
        return records.stream().filter(CallRecord::success).count();
    }

    private long countDistinctCallers(List<CallRecord> records) {
        Set<String> callers = new LinkedHashSet<>();
        records.forEach(record -> callers.add(record.userId()));
        return callers.size();
    }

    private double averageElapsed(List<CallRecord> records) {
        if (records.isEmpty()) {
            return 0D;
        }
        long sum = records.stream().mapToLong(CallRecord::elapsedMs).sum();
        return Math.round(sum * 100D / records.size()) / 100D;
    }

    private double ratio(long count, long total) {
        if (total == 0L) {
            return 0D;
        }
        return (double) Math.round(count * RATIO_SCALE / (double) total) / RATIO_SCALE;
    }

    private List<TrackTrendVO> buildTrend(List<CallRecord> records) {
        LocalDateTime currentHour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        List<LocalDateTime> buckets = new ArrayList<>(TREND_HOURS);
        for (int offset = TREND_HOURS - 1; offset >= 0; offset--) {
            buckets.add(currentHour.minusHours(offset));
        }
        List<TrackTrendVO> trend = new ArrayList<>(TREND_HOURS);
        for (LocalDateTime bucket : buckets) {
            LocalDateTime bucketEnd = bucket.plusHours(1);
            long count = records.stream()
                    .filter(record -> !record.calledAt().isBefore(bucket) && record.calledAt().isBefore(bucketEnd))
                    .count();
            trend.add(new TrackTrendVO(bucket.format(HOUR_FORMATTER), count));
        }
        return trend;
    }
}
