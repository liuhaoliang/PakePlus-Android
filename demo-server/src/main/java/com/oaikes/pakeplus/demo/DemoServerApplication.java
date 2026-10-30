package com.oaikes.pakeplus.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 演示服务启动类：提供 hello world、哈希算法、冒泡排序三类接口，
 * 以及调用埋点、调用报表与结果导出能力。
 *
 * @author pakeplus
 */
@SpringBootApplication
public class DemoServerApplication {

    /**
     * 服务入口。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(DemoServerApplication.class, args);
    }
}
