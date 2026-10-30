package com.oaikes.pakeplus.demo.service;

import com.oaikes.pakeplus.demo.model.vo.HelloWorldVO;

/**
 * hello world 服务。
 *
 * @author pakeplus
 */
public interface HelloWorldService {

    /**
     * 生成问候语。
     *
     * @param name 称呼，为空时使用默认称呼
     * @return 问候结果
     */
    HelloWorldVO getGreeting(String name);
}
