package com.oaikes.pakeplus.demo.service;

import com.oaikes.pakeplus.demo.model.dto.ReportQuery;
import com.oaikes.pakeplus.demo.model.vo.ReportVO;

/**
 * 调用情况报表服务。
 *
 * @author pakeplus
 */
public interface ReportService {

    /**
     * 查询调用情况报表。
     *
     * @param query 查询条件，维度、天数与接口过滤
     * @return 报表数据，无调用记录时返回计数为 0 的空报表
     */
    ReportVO getReport(ReportQuery query);
}
