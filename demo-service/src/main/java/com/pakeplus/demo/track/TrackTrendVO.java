package com.pakeplus.demo.track;

/**
 * 折线图时间点。
 *
 * @param bucket 时间桶（yyyy-MM-dd HH:00）
 * @param count  调用次数
 */
public record TrackTrendVO(String bucket, long count) {
}
