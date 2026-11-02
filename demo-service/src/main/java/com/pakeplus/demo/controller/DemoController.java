package com.pakeplus.demo.controller;

import com.pakeplus.demo.common.ApiResponse;
import com.pakeplus.demo.model.HashRequest;
import com.pakeplus.demo.model.HashResponse;
import com.pakeplus.demo.model.HelloResponse;
import com.pakeplus.demo.model.SortRequest;
import com.pakeplus.demo.model.SortResponse;
import com.pakeplus.demo.service.BubbleSortService;
import com.pakeplus.demo.service.HashAlgorithmService;
import com.pakeplus.demo.service.HelloWorldService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 演示接口：Hello World、哈希计算、冒泡排序。
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final HelloWorldService helloWorldService;
    private final HashAlgorithmService hashAlgorithmService;
    private final BubbleSortService bubbleSortService;

    public DemoController(HelloWorldService helloWorldService,
                          HashAlgorithmService hashAlgorithmService,
                          BubbleSortService bubbleSortService) {
        this.helloWorldService = helloWorldService;
        this.hashAlgorithmService = hashAlgorithmService;
        this.bubbleSortService = bubbleSortService;
    }

    /**
     * Hello World 接口。
     *
     * @param name 调用方名称，可选
     * @return 问候语与服务器时间
     */
    @GetMapping("/hello")
    public ApiResponse<HelloResponse> hello(@RequestParam(required = false) String name) {
        return ApiResponse.success(new HelloResponse(helloWorldService.greet(name), helloWorldService.currentTime()));
    }

    /**
     * 哈希计算接口。
     *
     * @param request 文本与算法
     * @return 摘要结果
     */
    @PostMapping("/hash")
    public ApiResponse<HashResponse> hash(@Valid @RequestBody HashRequest request) {
        long startMillis = System.nanoTime();
        String algorithm = hashAlgorithmService.normalizeAlgorithm(request.algorithm());
        String digest = hashAlgorithmService.digest(request.text(), algorithm);
        double elapsedMs = (System.nanoTime() - startMillis) / 1_000_000D;
        HashResponse response = new HashResponse(algorithm, digest, digest.length(), request.text().length(),
                round(elapsedMs));
        return ApiResponse.success(response);
    }

    /**
     * 冒泡排序接口。
     *
     * @param request 数组与排序方向
     * @return 排序结果与统计
     */
    @PostMapping("/sort")
    public ApiResponse<SortResponse> sort(@Valid @RequestBody SortRequest request) {
        long startMillis = System.nanoTime();
        BubbleSortService.SortOutcome outcome = bubbleSortService.sort(request.values(), request.order());
        double elapsedMs = (System.nanoTime() - startMillis) / 1_000_000D;
        String order = (request.order() == null || request.order().isBlank()) ? "asc" : request.order().trim();
        SortResponse response = new SortResponse(List.copyOf(request.values()), outcome.sorted(), order,
                outcome.comparisons(), outcome.swaps(), round(elapsedMs));
        return ApiResponse.success(response);
    }

    private double round(double value) {
        return Math.round(value * 1000D) / 1000D;
    }
}
