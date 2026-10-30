package com.oaikes.pakeplus.demo.service;

import com.oaikes.pakeplus.demo.model.dto.InvocationTrackCommand;
import com.oaikes.pakeplus.demo.model.entity.InvocationRecordDO;

import java.time.LocalDate;
import java.util.List;

/**
 * 调用埋点服务：负责记录接口调用并支持后续统计查询。
 *
 * @author pakeplus
 */
public interface InvocationTrackService {

    /**
     * 保存一次调用埋点记录。
     *
     * @param command 埋点入库命令
     */
    void saveInvocation(InvocationTrackCommand command);

    /**
     * 查询调用记录。
     *
     * @param apiCode  接口编码，为空表示全部接口
     * @param fromDate 起始日期（含），为空表示不限
     * @return 调用记录列表，无数据时返回空集合
     */
    List<InvocationRecordDO> listRecords(String apiCode, LocalDate fromDate);

    /**
     * 查询全部调用记录。
     *
     * @return 调用记录列表，无数据时返回空集合
     */
    List<InvocationRecordDO> listAllRecords();
}
