package com.oaikes.pakeplus.demo.tracking;

import com.oaikes.pakeplus.demo.enums.DemoApiEnum;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 埋点相关常量与接口路径映射。
 *
 * @author pakeplus
 */
public final class TrackingConstants {

    /** 接口路径与接口编码映射，用于按请求路径归类埋点。 */
    private static final Map<String, DemoApiEnum> PATH_APIS = new LinkedHashMap<>();

    static {
        PATH_APIS.put("/api/hello-world", DemoApiEnum.HELLO_WORLD);
        PATH_APIS.put("/api/hash", DemoApiEnum.HASH);
        PATH_APIS.put("/api/sort", DemoApiEnum.SORT);
    }

    /**
     * 私有构造方法，禁止实例化常量类。
     */
    private TrackingConstants() {
    }

    /**
     * 根据实际请求路径解析接口编码。
     *
     * @param requestUri 请求 URI（可能包含 context path）
     * @return 匹配的接口枚举，未匹配返回 null
     */
    public static DemoApiEnum resolveApi(String requestUri) {
        if (requestUri == null) {
            return null;
        }
        for (Map.Entry<String, DemoApiEnum> entry : PATH_APIS.entrySet()) {
            if (requestUri.endsWith(entry.getKey()) && entry.getValue().isTracked()) {
                return entry.getValue();
            }
        }
        return null;
    }
}
