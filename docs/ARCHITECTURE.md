# PakePlus-Android 架构说明

## 模块边界

| 模块 | 目录 | 职责 |
|------|------|------|
| Android 壳工程 | `app/` | WebView 容器、原生能力桥接（下载、上传、相机、定位、手势） |
| 演示页面（前端） | `app/src/main/assets/demo/` | 三个 Tab（Hello World / 哈希算法 / 冒泡排序）+ 调用报表页，导出按钮 |
| 演示服务（后端） | `demo-service/` | 三个演示接口、导出接口、调用埋点与报表聚合 |
| 构建脚本 | `scripts/` | 打包产物、注入配置（`app.json`）、图标处理 |

## 分层与约束

- `demo-service` 为独立 Maven 工程（Spring Boot 3.3.x / JDK 21），**不参与** Gradle 构建，需单独 `mvn` 构建与运行。
- 前端页面**单一来源**：以 `app/src/main/assets/demo/index.html` 为准，`demo-service` 打包时复制为静态资源；iOS 仓库 `PakePlus/demo.html` 为同一页面的镜像副本。
- 页面既可由 WebView 以 `file:///android_asset/demo/index.html` 加载（离线），也可由后端 `http://<host>:8080/` 直接访问。
- 跨端身份契约：调用方通过 `X-User-Id / X-User-Name / X-User-Type / X-User-Level / X-User-Dept` 请求头传递调用人，非 ASCII 值需 URL 编码。
- 埋点数据当前为进程内内存存储（容量 5000 条，先进先出淘汰），重启即清空；接入真实存储时替换 `TrackStore` 即可。

## 依赖方向

`Android WebView` → `demo/index.html` → `demo-service REST API`；后端不依赖 Android 工程，二者仅通过 HTTP 契约耦合。
