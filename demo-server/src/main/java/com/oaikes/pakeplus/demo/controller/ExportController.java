package com.oaikes.pakeplus.demo.controller;

import com.oaikes.pakeplus.demo.model.dto.ExportQuery;
import com.oaikes.pakeplus.demo.model.vo.ExportFileVO;
import com.oaikes.pakeplus.demo.service.ExportService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 结果导出控制器：导出各页面（Tab）的展示结果，以附件形式下载。
 *
 * @author pakeplus
 */
@RestController
@RequestMapping("/api/exports")
public class ExportController {

    /** 导出内容字符集。 */
    private static final String CSV_CONTENT_TYPE = "text/csv;charset=UTF-8";

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    /**
     * 导出页面展示结果。
     *
     * @param page      页面编码：hello-world / hash / sort / report / all
     * @param content   哈希页面入参原文，可选
     * @param values    排序页面入参，逗号分隔的整数，可选
     * @param dimension 报表页面统计维度，可选
     * @param days      报表页面统计天数，可选
     * @return CSV 附件
     */
    @GetMapping
    public ResponseEntity<byte[]> exportResults(@RequestParam(defaultValue = "all") String page,
                                                @RequestParam(required = false) String content,
                                                @RequestParam(required = false) String values,
                                                @RequestParam(required = false) String dimension,
                                                @RequestParam(defaultValue = "7") int days) {
        ExportFileVO exportFileVO = exportService.exportResults(
                new ExportQuery(page, content, values, dimension, days));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(CSV_CONTENT_TYPE))
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + exportFileVO.fileName() + "\"")
                .body(exportFileVO.content());
    }
}
