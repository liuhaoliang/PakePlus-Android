package com.pakeplus.demo.track;

/**
 * 维度分组统计项。
 *
 * @param key   分组键
 * @param count 调用次数
 * @param ratio 占比（0 ~ 1，保留 4 位小数）
 */
public record TrackItemVO(String key, long count, double ratio) {
}
