package com.oaikes.pakeplus.demo.util;

import com.oaikes.pakeplus.demo.constant.DemoConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * CSV 构造器：统一处理分隔符、转义与换行。
 *
 * @author pakeplus
 */
public class CsvBuilder {

    /** BOM 头，保证 Excel 以 UTF-8 正确打开中文。 */
    private static final String UTF8_BOM = "\uFEFF";

    /** 需要转义的字符集合。 */
    private static final String NEED_ESCAPE_CHARS = ",\"\r\n";

    private final List<String> lines = new ArrayList<>();

    /**
     * 追加一行数据。
     *
     * @param cells 单元格内容
     * @return 当前构造器，支持链式调用
     */
    public CsvBuilder appendRow(Object... cells) {
        StringBuilder lineBuilder = new StringBuilder();
        for (int index = 0; index < cells.length; index++) {
            if (index > 0) {
                lineBuilder.append(DemoConstants.CSV_SEPARATOR);
            }
            lineBuilder.append(escape(cells[index]));
        }
        lines.add(lineBuilder.toString());
        return this;
    }

    /**
     * 追加一个空行。
     *
     * @return 当前构造器，支持链式调用
     */
    public CsvBuilder appendEmptyRow() {
        lines.add("");
        return this;
    }

    /**
     * 输出 CSV 文本，带 UTF-8 BOM 头。
     *
     * @return CSV 文本
     */
    public String build() {
        StringBuilder builder = new StringBuilder(UTF8_BOM);
        for (String line : lines) {
            builder.append(line).append(DemoConstants.CSV_LINE_END);
        }
        return builder.toString();
    }

    /**
     * 转义单元格：包含分隔符、引号或换行时用双引号包裹并转义内部引号。
     *
     * @param cell 单元格内容
     * @return 转义后的内容
     */
    private String escape(Object cell) {
        if (cell == null) {
            return "";
        }
        String text = String.valueOf(cell);
        for (int index = 0; index < NEED_ESCAPE_CHARS.length(); index++) {
            if (text.indexOf(NEED_ESCAPE_CHARS.charAt(index)) >= 0) {
                return '"' + text.replace("\"", "\"\"") + '"';
            }
        }
        return text;
    }
}
