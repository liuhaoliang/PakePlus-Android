# demo-api 模块

## 模块职责

提供三类演示接口（hello world、哈希算法、冒泡排序）、接口调用埋点、调用情况报表与结果导出能力，并托管演示前端页面。

## 关键类

| 类 | 说明 |
|----|------|
| `DemoController` | `/api/hello-world`、`/api/hash`、`/api/sort` 三个演示接口 |
| `ReportController` | `/api/reports` 调用情况报表 |
| `ExportController` | `/api/exports` 结果导出（CSV 附件） |
| `HelloWorldServiceImpl` | 问候语生成，默认称呼 World |
| `HashServiceImpl` | SHA-256 / SHA-512 / MD5 摘要，十六进制输出 |
| `SortServiceImpl` | 升序冒泡排序，提前退出优化，统计比较/交换次数 |
| `InvocationTrackInterceptor` | 埋点拦截器，按路径归类接口，记录调用人与耗时 |
| `CallerInfoResolver` | 解析 `X-User-*` 请求头，兼容 URL 编码与 ISO-8859-1 解码的中文 |
| `InvocationTrackServiceImpl` | 内存队列存储埋点，超出 10000 条淘汰最早记录 |
| `ReportServiceImpl` | 按人员类型/层级/部门聚合，产出饼图、柱状图、折线图数据 |
| `ExportServiceImpl` | 按页面编码导出 CSV，支持 `all` 全量导出 |
| `GlobalExceptionHandler` | 异常统一转换为标准错误响应 |
| `CsvBuilder` | CSV 构造与转义，带 UTF-8 BOM |

## 依赖关系

- `ReportServiceImpl` → `InvocationTrackService`（埋点数据源）
- `ExportServiceImpl` → `HelloWorldService`、`HashService`、`SortService`、`ReportService`
- `InvocationTrackInterceptor` → `InvocationTrackService`（由 `WebMvcConfig` 注册到 `/api/**`）
- 无外部数据库/中间件依赖，可独立启动

## 接口列表

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/hello-world?name=` | 返回问候语与服务端时间 |
| POST | `/api/hash` | 入参 `{content, algorithm}`，返回十六进制摘要 |
| POST | `/api/sort` | 入参 `{values}`，返回排序结果与比较/交换次数 |
| GET | `/api/reports?dimension=&days=&apiCode=` | 调用情况报表（KPI + 饼图 + 柱状图 + 折线图） |
| GET | `/api/exports?page=&content=&values=&dimension=&days=` | 导出 CSV，`page` 取值 hello-world / hash / sort / report / all |
| GET | `/index.html` | 演示前端页面（三个 Tab + 导出按钮 + 报表可视化） |

## 埋点请求头

| 请求头 | 说明 | 缺省值 |
|--------|------|--------|
| `X-User-Id` | 调用人 ID | `anonymous` |
| `X-User-Name` | 调用人姓名 | 同调用人 ID |
| `X-User-Type` | 人员类型 | `UNKNOWN` |
| `X-User-Level` | 人员层级 | `UNKNOWN` |
| `X-User-Dept` | 人员部门 | `UNKNOWN` |

非 ASCII 取值建议由前端 URL 编码后传输；服务端同时兼容原始 UTF-8 字节序列。

## 埋点范围

仅三个业务接口（`/api/hello-world`、`/api/hash`、`/api/sort`）写入埋点；`/api/reports` 与 `/api/exports`
属于统计与导出能力，不计入调用次数，避免刷新报表或导出结果时统计值自增。范围由 `DemoApiEnum.isTracked()`
与 `TrackingConstants` 共同约束。

## 后续演进

1. 埋点存储替换为 MySQL/JPA：新增 `demo_invocation_record` 表（具备 `id`、`gmt_create`、`gmt_modified`、`creator`、`modifier` 等必备字段）与对应 Mapper。
2. 报表接口支持分页与接口维度多选过滤。
3. 导出格式扩展为 Excel（`aiwork-excel` 能力）以支持图表版导出。
