# demo-service 模块

## 模块职责

提供演示所需的三个业务接口、结果导出能力与接口调用埋点报表，供移动端页面展示。

## 关键类

| 类 | 职责 |
|----|------|
| `DemoApplication` | 启动类 |
| `DemoController` | Hello World / 哈希 / 冒泡排序接口 |
| `HelloWorldService` | 问候语与服务器时间 |
| `HashAlgorithmService` | MD5 / SHA-1 / SHA-256 / SHA-512 摘要计算 |
| `BubbleSortService` | 冒泡排序与比较/交换统计 |
| `ExportService` / `ExportController` | 各页面结果与报表明细导出为 CSV（含 UTF-8 BOM） |
| `TrackInterceptor` / `TrackStore` | 请求级埋点采集（调用人、耗时、成功与否） |
| `TrackReportService` | 按人员类型 / 层级 / 部门 / 接口聚合，输出报表与 12 小时趋势 |
| `TrackDimensionEnum` | 统计维度枚举 |
| `GlobalExceptionHandler` | 统一错误码响应（A0400 / A0404 / B0500） |

## API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/demo/hello?name=` | 问候语 + 服务器时间 |
| POST | `/api/demo/hash` | 入参 `{text, algorithm}`，返回摘要、长度、耗时 |
| POST | `/api/demo/sort` | 入参 `{values[], order}`，返回排序结果、比较次数、交换次数 |
| GET | `/api/demo/export/hello?name=` | 导出 Hello 页面结果 CSV |
| GET | `/api/demo/export/hash?text=&algorithm=` | 导出哈希页面结果 CSV |
| GET | `/api/demo/export/sort?values=1,2,3&order=asc` | 导出排序页面结果 CSV |
| GET | `/api/demo/export/track?dimension=user_type` | 导出报表页结果 CSV |
| GET | `/api/demo/export/track/records?limit=` | 导出调用明细 CSV |
| GET | `/api/demo/track/summary?dimension=` | 报表数据（分组统计 + 12 小时趋势） |
| GET | `/api/demo/track/records?limit=` | 最近调用明细 |
| POST | `/api/demo/track/sample-data?count=` | 生成演示埋点数据 |
| GET | `/` | 演示页面（`assets/demo/index.html` 构建时复制到静态资源） |

## 调用人身份契约

请求头：`X-User-Id`、`X-User-Name`、`X-User-Type`、`X-User-Level`、`X-User-Dept`；缺失时按 `anonymous / 匿名访客 / UNKNOWN` 归集；中文字段需 URL 编码。

## 运行

```bash
cd PakePlus-Android-main/demo-service
mvn spring-boot:run          # 默认 8080
mvn test                     # 单元测试 + 接口集成测试
```

Android 模拟器访问宿主机服务用 `http://10.0.2.2:8080`，iOS 模拟器用 `http://127.0.0.1:8080`（页面内置默认值，可在 `localStorage['pakeplus.demo.apiBase']` 覆盖）。
