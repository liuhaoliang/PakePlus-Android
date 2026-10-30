package com.oaikes.pakeplus.demo.service;

import com.oaikes.pakeplus.demo.model.dto.ExportQuery;
import com.oaikes.pakeplus.demo.model.vo.ExportFileVO;

/**
 * 结果导出服务：导出各页面（hello world / 哈希 / 排序 / 报表）的展示结果。
 *
 * @author pakeplus
 */
public interface ExportService {

    /**
     * 导出页面展示结果。
     *
     * @param query 导出条件
     * @return 导出文件内容（CSV）
     */
    ExportFileVO exportResults(ExportQuery query);
}
