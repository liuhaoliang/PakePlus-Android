package com.pakeplus.demo.export;

/**
 * 导出结果。
 *
 * @param fileName 导出文件名
 * @param content  CSV 文本内容（UTF-8，含 BOM 由控制器追加）
 */
public record ExportResult(String fileName, String content) {
}
