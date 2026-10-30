package com.oaikes.pakeplus.demo.model.vo;

/**
 * 导出文件视图对象。
 *
 * @param fileName    下载文件名
 * @param contentType 文件 MIME 类型
 * @param content     文件内容
 * @author pakeplus
 */
public record ExportFileVO(String fileName, String contentType, byte[] content) {
}
