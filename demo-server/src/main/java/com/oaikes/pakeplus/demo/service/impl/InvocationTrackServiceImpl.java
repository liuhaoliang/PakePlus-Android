package com.oaikes.pakeplus.demo.service.impl;

import com.oaikes.pakeplus.demo.constant.DemoConstants;
import com.oaikes.pakeplus.demo.model.dto.InvocationTrackCommand;
import com.oaikes.pakeplus.demo.model.entity.InvocationRecordDO;
import com.oaikes.pakeplus.demo.service.InvocationTrackService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 调用埋点服务实现：内存环形队列存储，超出上限后淘汰最早记录。
 *
 * @author pakeplus
 */
@Service
public class InvocationTrackServiceImpl implements InvocationTrackService {

    /** 记录流水号生成器。 */
    private static final AtomicLong RECORD_SEQUENCE = new AtomicLong(0L);

    /** 调用记录存储，按时间先后追加，队首为最早记录。 */
    private final Deque<InvocationRecordDO> records = new ConcurrentLinkedDeque<>();

    @Override
    public void saveInvocation(InvocationTrackCommand command) {
        if (command == null) {
            return;
        }
        long sequence = RECORD_SEQUENCE.incrementAndGet();
        InvocationRecordDO recordDO = new InvocationRecordDO(
                String.valueOf(sequence),
                command.apiCode(),
                command.apiName(),
                command.callerId(),
                command.callerName(),
                command.userType(),
                command.userLevel(),
                command.userDept(),
                LocalDateTime.now(),
                command.costMillis(),
                command.success(),
                command.resultSummary());
        records.addLast(recordDO);
        while (records.size() > DemoConstants.MAX_RECORD_SIZE) {
            records.pollFirst();
        }
    }

    @Override
    public List<InvocationRecordDO> listRecords(String apiCode, LocalDate fromDate) {
        List<InvocationRecordDO> result = new ArrayList<>();
        for (InvocationRecordDO recordDO : records) {
            if (!matchesApi(recordDO, apiCode) || !matchesDate(recordDO, fromDate)) {
                continue;
            }
            result.add(recordDO);
        }
        return result;
    }

    @Override
    public List<InvocationRecordDO> listAllRecords() {
        return new ArrayList<>(records);
    }

    /**
     * 判断记录是否匹配接口条件。
     *
     * @param recordDO 调用记录
     * @param apiCode  接口编码，为空表示不过滤
     * @return 是否匹配
     */
    private boolean matchesApi(InvocationRecordDO recordDO, String apiCode) {
        return apiCode == null || apiCode.isBlank() || apiCode.equalsIgnoreCase(recordDO.apiCode());
    }

    /**
     * 判断记录是否匹配日期条件。
     *
     * @param recordDO 调用记录
     * @param fromDate 起始日期，为空表示不过滤
     * @return 是否匹配
     */
    private boolean matchesDate(InvocationRecordDO recordDO, LocalDate fromDate) {
        if (fromDate == null) {
            return true;
        }
        return !recordDO.invokeTime().toLocalDate().isBefore(fromDate);
    }
}
