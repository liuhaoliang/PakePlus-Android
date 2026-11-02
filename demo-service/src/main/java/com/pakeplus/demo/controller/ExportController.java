package com.pakeplus.demo.controller;

import com.pakeplus.demo.export.ExportResult;
import com.pakeplus.demo.export.ExportService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 导出接口：把各页面展示结果导出为 CSV 文件。
 */
@RestController
@RequestMapping("/api/demo/export")
public class ExportController {

    private static final byte[] UTF8_BOM = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
    private static final String TEXT_CSV = "text/csv;charset=UTF-8";

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    /**
     * 导出指定页面结果。
     *
     * @param module    页面标识：hello / hash / sort / track
     * @param name      Hello 页面参数
     * @param text      哈希页面参数
     * @param algorithm 哈希算法
     * @param values    排序数组（逗号分隔）
     * @param order     排序方向
     * @param dimension 埋点报表维度
     * @return CSV 文件
     */
    @GetMapping("/{module}")
    public ResponseEntity<byte[]> export(@PathVariable String module,
                                         @RequestParam(required = false) String name,
                                         @RequestParam(required = false) String text,
                                         @RequestParam(required = false) String algorithm,
                                         @RequestParam(required = false) List<Integer> values,
                                         @RequestParam(required = false) String order,
                                         @RequestParam(required = false) String dimension) {
        ExportResult result = exportService.export(module, name, text, algorithm, values, order, dimension);
        return buildResponse(result);
    }

    /**
     * 导出最近调用明细。
     *
     * @param limit 数量
     * @return CSV 文件
     */
    @GetMapping("/track/records")
    public ResponseEntity<byte[]> exportRecords(@RequestParam(required = false) Integer limit) {
        return buildResponse(exportService.exportTrackRecords(limit));
    }

    private ResponseEntity<byte[]> buildResponse(ExportResult result) {
        byte[] body = result.content().getBytes(StandardCharsets.UTF_8);
        byte[] payload = new byte[UTF8_BOM.length + body.length];
        System.arraycopy(UTF8_BOM, 0, payload, 0, UTF8_BOM.length);
        System.arraycopy(body, 0, payload, UTF8_BOM.length, body.length);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(TEXT_CSV));
        headers.setContentDisposition(ContentDisposition.attachment().filename(result.fileName()).build());
        headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        return ResponseEntity.ok().headers(headers).body(payload);
    }
}
