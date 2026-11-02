package com.pakeplus.demo.controller;

import com.pakeplus.demo.common.ApiResponse;
import com.pakeplus.demo.track.CallRecord;
import com.pakeplus.demo.track.TrackDimensionEnum;
import com.pakeplus.demo.track.TrackReportService;
import com.pakeplus.demo.track.TrackSampleDataService;
import com.pakeplus.demo.track.TrackSummaryVO;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 调用埋点查询接口：报表、调用明细、演示数据生成。
 */
@RestController
@RequestMapping("/api/demo/track")
public class TrackController {

    private final TrackReportService trackReportService;
    private final TrackSampleDataService trackSampleDataService;

    public TrackController(TrackReportService trackReportService, TrackSampleDataService trackSampleDataService) {
        this.trackReportService = trackReportService;
        this.trackSampleDataService = trackSampleDataService;
    }

    /**
     * 调用情况报表。
     *
     * @param dimension 统计维度：user_type / user_level / user_dept / api
     * @return 报表数据
     */
    @GetMapping("/summary")
    public ApiResponse<TrackSummaryVO> summary(@RequestParam(required = false, defaultValue = "user_type") String dimension) {
        return ApiResponse.success(trackReportService.summarize(TrackDimensionEnum.fromCode(dimension)));
    }

    /**
     * 最近调用明细。
     *
     * @param limit 数量，缺省 50
     * @return 调用记录
     */
    @GetMapping("/records")
    public ApiResponse<List<CallRecord>> records(@RequestParam(required = false, defaultValue = "50") Integer limit) {
        return ApiResponse.success(trackReportService.latestRecords(limit));
    }

    /**
     * 生成演示数据，便于离线查看图表。
     *
     * @param count 生成条数
     * @return 生成条数
     */
    @PostMapping("/sample-data")
    public ApiResponse<Map<String, Integer>> sampleData(@RequestParam(required = false) Integer count) {
        int generated = trackSampleDataService.generate(count);
        return ApiResponse.success(Map.of("generated", generated));
    }
}
