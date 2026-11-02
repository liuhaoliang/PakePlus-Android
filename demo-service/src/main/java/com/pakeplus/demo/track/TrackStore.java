package com.pakeplus.demo.track;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

/**
 * 埋点记录内存存储，容量达上限后淘汰最早记录。
 */
@Component
public class TrackStore {

    private static final int MAX_RECORDS = 5000;

    private final List<CallRecord> records = new ArrayList<>();
    private final AtomicLong sequenceGenerator = new AtomicLong(0L);

    /**
     * 追加一条调用记录。
     *
     * @param apiPath   接口路径
     * @param identity  调用人身份
     * @param success   是否成功
     * @param elapsedMs 耗时（毫秒）
     */
    public synchronized void append(String apiPath, UserIdentity identity, boolean success, long elapsedMs) {
        appendAt(apiPath, identity, success, elapsedMs, LocalDateTime.now());
    }

    /**
     * 追加一条指定调用时间的记录，用于演示数据生成。
     *
     * @param apiPath   接口路径
     * @param identity  调用人身份
     * @param success   是否成功
     * @param elapsedMs 耗时（毫秒）
     * @param calledAt  调用时间
     */
    public synchronized void appendAt(String apiPath, UserIdentity identity, boolean success, long elapsedMs,
                                      LocalDateTime calledAt) {
        records.add(new CallRecord(sequenceGenerator.incrementAndGet(), apiPath, identity.userId(), identity.userName(),
                identity.userType(), identity.userLevel(), identity.userDept(), success, elapsedMs, calledAt));
        while (records.size() > MAX_RECORDS) {
            records.remove(0);
        }
    }

    /**
     * 获取记录快照。
     *
     * @return 记录列表副本
     */
    public synchronized List<CallRecord> snapshot() {
        return List.copyOf(records);
    }

    /**
     * 获取最近 N 条记录（时间倒序）。
     *
     * @param limit 数量
     * @return 记录列表
     */
    public synchronized List<CallRecord> latest(int limit) {
        int size = records.size();
        int fromIndex = Math.max(0, size - limit);
        List<CallRecord> latestRecords = new ArrayList<>(records.subList(fromIndex, size));
        latestRecords.sort((left, right) -> Long.compare(right.sequence(), left.sequence()));
        return latestRecords;
    }

    /**
     * 清空记录，仅用于演示数据重置。
     */
    public synchronized void clear() {
        records.clear();
        sequenceGenerator.set(0L);
    }

    /**
     * 当前记录条数。
     *
     * @return 条数
     */
    public synchronized int size() {
        return records.size();
    }
}
