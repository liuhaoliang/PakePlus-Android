package com.oaikes.pakeplus.demo.constant;

/**
 * 演示服务常量。
 *
 * @author pakeplus
 */
public final class DemoConstants {

    /** 调用人请求头。 */
    public static final String HEADER_USER_ID = "X-User-Id";

    /** 调用人姓名请求头。 */
    public static final String HEADER_USER_NAME = "X-User-Name";

    /** 人员类型请求头（如 INTERNAL / EXTERNAL）。 */
    public static final String HEADER_USER_TYPE = "X-User-Type";

    /** 人员层级请求头（如 P5 / P6 / M1）。 */
    public static final String HEADER_USER_LEVEL = "X-User-Level";

    /** 人员部门请求头。 */
    public static final String HEADER_USER_DEPT = "X-User-Dept";

    /** 匿名调用人标识。 */
    public static final String ANONYMOUS_USER_ID = "anonymous";

    /** 默认人员类型（未透传时使用）。 */
    public static final String DEFAULT_USER_TYPE = "UNKNOWN";

    /** 默认人员层级（未透传时使用）。 */
    public static final String DEFAULT_USER_LEVEL = "UNKNOWN";

    /** 默认部门（未透传时使用）。 */
    public static final String DEFAULT_USER_DEPT = "UNKNOWN";

    /** 时间格式：前后端统一 yyyy-MM-dd HH:mm:ss。 */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /** 趋势图日期格式。 */
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    /** 埋点记录最大保留条数，防止内存无限增长。 */
    public static final int MAX_RECORD_SIZE = 10000;

    /** 冒泡排序单次允许的最大元素个数。 */
    public static final int MAX_SORT_SIZE = 1000;

    /** 冒泡排序元素绝对值上限。 */
    public static final int MAX_SORT_VALUE = 1000000;

    /** 导出文件编码。 */
    public static final String EXPORT_CHARSET = "UTF-8";

    /** CSV 列分隔符。 */
    public static final String CSV_SEPARATOR = ",";

    /** CSV 换行符。 */
    public static final String CSV_LINE_END = "\r\n";

    /**
     * 私有构造方法，禁止实例化常量类。
     */
    private DemoConstants() {
    }
}
