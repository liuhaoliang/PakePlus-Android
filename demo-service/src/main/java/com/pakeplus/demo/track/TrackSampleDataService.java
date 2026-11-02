package com.pakeplus.demo.track;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;

/**
 * 演示数据生成服务：为报表页准备多维度样本，便于离线查看图表。
 */
@Service
public class TrackSampleDataService {

    private static final int DEFAULT_SAMPLE_COUNT = 60;
    private static final int MAX_SAMPLE_COUNT = 600;
    private static final String[] API_PATHS = {"/api/demo/hello", "/api/demo/hash", "/api/demo/sort"};
    private static final List<UserIdentity> SAMPLE_USERS = List.of(
            new UserIdentity("u1001", "张伟", "INTERNAL", "P7", "技术部"),
            new UserIdentity("u1002", "李娜", "INTERNAL", "P6", "产品部"),
            new UserIdentity("u1003", "王强", "INTERNAL", "P5", "运营部"),
            new UserIdentity("u2001", "赵敏", "EXTERNAL", "P6", "技术部"),
            new UserIdentity("u2002", "陈晨", "EXTERNAL", "P5", "运营部"),
            new UserIdentity("u3001", "刘洋", "PARTNER", "P4", "合作方"));

    private final TrackStore trackStore;

    public TrackSampleDataService(TrackStore trackStore) {
        this.trackStore = trackStore;
    }

    /**
     * 生成演示调用记录。
     *
     * @param count 生成条数，缺省 60，最大 600
     * @return 实际生成条数
     */
    public int generate(Integer count) {
        int resolvedCount = count == null ? DEFAULT_SAMPLE_COUNT : Math.min(Math.max(count, 1), MAX_SAMPLE_COUNT);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        LocalDateTime now = LocalDateTime.now();
        for (int index = 0; index < resolvedCount; index++) {
            UserIdentity identity = SAMPLE_USERS.get(random.nextInt(SAMPLE_USERS.size()));
            String apiPath = API_PATHS[random.nextInt(API_PATHS.length)];
            boolean success = random.nextInt(10) > 0;
            long elapsedMs = random.nextLong(3L, 120L);
            LocalDateTime calledAt = now.minusMinutes(random.nextLong(0L, 720L));
            trackStore.appendAt(apiPath, identity, success, elapsedMs, calledAt);
        }
        return resolvedCount;
    }
}
