package com.pakeplus.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Demo 服务启动类：提供 Hello World、哈希、冒泡排序三个接口，
 * 以及导出与调用埋点统计能力。
 */
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
