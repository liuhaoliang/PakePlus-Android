package com.oaikes.pakeplus.demo.controller;

import com.oaikes.pakeplus.demo.common.ApiResponse;
import com.oaikes.pakeplus.demo.model.dto.HashRequest;
import com.oaikes.pakeplus.demo.model.dto.SortRequest;
import com.oaikes.pakeplus.demo.model.vo.HashVO;
import com.oaikes.pakeplus.demo.model.vo.HelloWorldVO;
import com.oaikes.pakeplus.demo.model.vo.SortVO;
import com.oaikes.pakeplus.demo.service.HashService;
import com.oaikes.pakeplus.demo.service.HelloWorldService;
import com.oaikes.pakeplus.demo.service.SortService;
import com.oaikes.pakeplus.demo.tracking.InvocationTrackInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 演示接口控制器：hello world、哈希算法、冒泡排序三类接口。
 *
 * @author pakeplus
 */
@RestController
@RequestMapping("/api")
public class DemoController {

    /** 结果摘要最大长度，避免埋点存储过长文本。 */
    private static final int SUMMARY_MAX_LENGTH = 120;

    private final HelloWorldService helloWorldService;

    private final HashService hashService;

    private final SortService sortService;

    public DemoController(HelloWorldService helloWorldService, HashService hashService, SortService sortService) {
        this.helloWorldService = helloWorldService;
        this.hashService = hashService;
        this.sortService = sortService;
    }

    /**
     * hello world 接口。
     *
     * @param name    称呼，可选
     * @param request HTTP 请求，用于写入埋点结果摘要
     * @return 问候结果
     */
    @GetMapping("/hello-world")
    public ApiResponse<HelloWorldVO> getHelloWorld(@RequestParam(required = false) String name,
                                                   HttpServletRequest request) {
        HelloWorldVO helloWorldVO = helloWorldService.getGreeting(name);
        request.setAttribute(InvocationTrackInterceptor.ATTR_RESULT_SUMMARY, helloWorldVO.message());
        return ApiResponse.success(helloWorldVO);
    }

    /**
     * 哈希算法接口。
     *
     * @param hashRequest 哈希请求
     * @param request     HTTP 请求，用于写入埋点结果摘要
     * @return 摘要结果
     */
    @PostMapping("/hash")
    public ApiResponse<HashVO> postHash(@Valid @RequestBody HashRequest hashRequest,
                                       HttpServletRequest request) {
        HashVO hashVO = hashService.getDigest(hashRequest.content(), hashRequest.algorithm());
        request.setAttribute(InvocationTrackInterceptor.ATTR_RESULT_SUMMARY,
                hashVO.algorithm() + ": " + abbreviate(hashVO.digest()));
        return ApiResponse.success(hashVO);
    }

    /**
     * 冒泡排序接口。
     *
     * @param sortRequest 排序请求
     * @param request     HTTP 请求，用于写入埋点结果摘要
     * @return 排序结果
     */
    @PostMapping("/sort")
    public ApiResponse<SortVO> postSort(@Valid @RequestBody SortRequest sortRequest,
                                        HttpServletRequest request) {
        SortVO sortVO = sortService.getSortedValues(sortRequest.values());
        request.setAttribute(InvocationTrackInterceptor.ATTR_RESULT_SUMMARY,
                "sorted: " + sortVO.sortedValues() + ", compare: " + sortVO.compareCount()
                        + ", swap: " + sortVO.swapCount());
        return ApiResponse.success(sortVO);
    }

    /**
     * 截断过长文本，用于埋点结果摘要。
     *
     * @param text 原文
     * @return 截断后的文本
     */
    private String abbreviate(String text) {
        if (text == null) {
            return "";
        }
        if (text.length() <= SUMMARY_MAX_LENGTH) {
            return text;
        }
        return text.substring(0, SUMMARY_MAX_LENGTH) + "...";
    }
}
