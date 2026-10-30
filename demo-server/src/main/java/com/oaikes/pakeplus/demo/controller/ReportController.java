package com.oaikes.pakeplus.demo.controller;

import com.oaikes.pakeplus.demo.common.ApiResponse;
import com.oaikes.pakeplus.demo.model.dto.ReportQuery;
import com.oaikes.pakeplus.demo.model.vo.ReportVO;
import com.oaikes.pakeplus.demo.service.ReportService;
import com.oaikes.pakeplus.demo.tracking.InvocationTrackInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 调用报表控制器：返回按人员类型 / 层级 / 部门维度聚合的调用情况。
 *
 * @author pakeplus
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * 查询调用情况报表。
     *
     * @param dimension 统计维度：userType / userLevel / userDept
     * @param days      统计最近天数，1~90
     * @param apiCode   接口编码过滤，可选
     * @param request   HTTP 请求，用于写入埋点结果摘要
     * @return 报表数据
     */
    @GetMapping
    public ApiResponse<ReportVO> getReport(@RequestParam(required = false) String dimension,
                                           @RequestParam(defaultValue = "7") int days,
                                           @RequestParam(required = false) String apiCode,
                                           HttpServletRequest request) {
        ReportVO reportVO = reportService.getReport(new ReportQuery(dimension, days, apiCode));
        request.setAttribute(InvocationTrackInterceptor.ATTR_RESULT_SUMMARY,
                "dimension: " + reportVO.dimensionCode() + ", total: " + reportVO.totalCount());
        return ApiResponse.success(reportVO);
    }
}
