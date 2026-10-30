# 编码报告：PakePlus 演示中心（demo-server）

- 任务节点：编码实现（stage: coding）
- 遵循规范：`dtazziboot-java-coding-standards` v1.1.0
- 需求：三个接口（hello world / 哈希算法 / 冒泡排序）+ 前端三 Tab 页面 + 导出接口与按钮 + 埋点（调用次数、调用人、人员类型/层级/部门）+ 折线图/饼图/柱状图报表
- 说明：用户未指定 `OUTPUT_FILE`，本报告按规范默认落盘路径 `.agents/2026-10-30-demo-apis/impl.md` 生成（仓库：`PakePlus-Android-main`）

## 模块进度追踪

| 序号 | 模块 | READ | TEST | IMPL | CHECK | DOCS | 状态 |
|----|------|------|------|------|-------|------|------|
| 1 | demo-api（三接口 + 埋点 + 报表 + 导出 + 前端页面） | ✅ | ✅ | ✅ | ✅ | ✅ | 已完成 |

## 阶段产出摘要

### READ
- 知识库检索：`mind content-search -b Z2qpItXsLMD_-XEOfFjgL`，命中 5 条，均为字面词碰撞、与需求无关（`related:false`），已留痕于 `kb_retrieval.jsonl`。
- 仓库勘察：`PakePlus-Android`（Kotlin/Gradle 壳工程）、`PakePlus-iOS`（Swift WKWebView 壳工程）均无 Java/Web 后端，因此新建 `PakePlus-Android-main/demo-server` 子模块承载服务端与演示页面，未改动 Android 构建脚本（`settings.gradle.kts` 未引入）。

### TEST
- 7 个测试类、34 个用例，覆盖哈希/排序/问候语/报表聚合/导出 CSV/埋点拦截器/调用人解析。
- 偏差（如实披露）：先写实现后补测试，与规范的 TEST→IMPL 顺序相反。

### IMPL
- Spring Boot 3.3.4 + JDK 21，分层：controller / service + impl / tracking / model(entity·dto·vo) / common / enums / constant / util / config。
- 埋点：`HandlerInterceptor` 拦截 `/api/**`，仅三个业务接口写入（`DemoApiEnum.isTracked()` + `TrackingConstants` 双重约束），内存有界队列（上限 10000 条）。

### CHECK
- `mvn test`：`Tests run: 34, Failures: 0, Errors: 0, Skipped: 0`，`BUILD SUCCESS`。
- 真机 HTTP 端到端（端口 18080，`java -jar` 实际启动）：见下节证据。
- 前端真实渲染：`chrome-headless-shell` 打开 `http://127.0.0.1:18080/index.html`，截图人工检视，中文正常、无 JS 报错、无错误提示条。

### DOCS
- `demo-server/docs/ARCHITECTURE.md`：模块定位、分层表、关键约束、跨仓关系。
- `demo-server/docs/modules/demo-api/README.md`：关键类、依赖关系、接口表、埋点请求头、埋点范围、后续演进。
- 本报告。

## 已实现文件清单（`PakePlus-Android-main/demo-server`，54 个源文件）

- 构建：`pom.xml`、`.gitignore`
- 启动：`DemoServerApplication.java`
- 通用：`common/{ApiResponse,BizException,ErrorCodeEnum,GlobalExceptionHandler}.java`、`constant/DemoConstants.java`
- 枚举：`enums/{DemoApiEnum,HashAlgorithmEnum,ReportDimensionEnum}.java`
- 模型：`model/entity/InvocationRecordDO.java`；`model/dto/{CallerInfo,ExportQuery,HashRequest,InvocationTrackCommand,ReportQuery,SortRequest}.java`；`model/vo/{ChartPointVO,ChartVO,ExportFileVO,HashVO,HelloWorldVO,LineChartVO,LineSeriesVO,ReportVO,SortVO}.java`
- 服务：`service/{Export,Hash,HelloWorld,InvocationTrack,Report,Sort}Service.java` 及 `service/impl/*Impl.java`
- 埋点：`tracking/{CallerInfoResolver,InvocationTrackInterceptor,TrackingConstants}.java`、`config/WebMvcConfig.java`
- 控制器：`controller/{DemoController,ReportController,ExportController}.java`
- 工具：`util/CsvBuilder.java`
- 配置与资源：`src/main/resources/application.yml`、`src/main/resources/static/index.html`（1049 行，三 Tab + 双导出按钮 + 三图表 + 报表 KPI/接口明细表）
- 测试：`service/impl/{Hash,Sort,HelloWorld,Report,Export}ServiceImplTest.java`、`tracking/{InvocationTrackInterceptorTest,CallerInfoResolverTest}.java`
- 文档：`docs/ARCHITECTURE.md`、`docs/modules/demo-api/README.md`

`PakePlus-iOS-dev`：无改动（该仓库为 Swift WKWebView 壳，页面由 demo-server 静态资源统一提供，避免多份副本不一致）。

## 接口契约

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/hello-world?name=` | 问候语 + 服务端时间 |
| POST | `/api/hash` | `{content, algorithm}`；支持 sha-256 / sha-512 / md5 |
| POST | `/api/sort` | `{values}`；升序冒泡，返回 `sortedValues`、`compareCount`、`swapCount` |
| GET | `/api/reports?dimension=&days=&apiCode=` | KPI + `pieChart` / `barChart`（ChartVO）+ `lineChart`（LineChartVO） |
| GET | `/api/exports?page=&content=&values=&dimension=&days=` | CSV 附件；`page` ∈ hello-world / hash / sort / report / all |
| GET | `/index.html` | 演示前端页面 |

统一响应：`success` / `errorCode` / `errorMessage` / `userTip` / `data`；错误码 5 位（`S0000`、`A0001`）。埋点请求头：`X-User-Id` / `X-User-Name` / `X-User-Type` / `X-User-Level` / `X-User-Dept`。

## CHECK 证据（最终版本，2026-10-30）

单元测试：

```
Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

端到端（干净实例，`java -jar target/demo-server-1.0.0.jar --server.port=18080`）：

- `GET /index.html` → 200，37628 字节，包含 `data-tab=hello|hash|sort`、`lineChart`、`pieChart`、`barChart`、`btnExportCurrent`、`btnExportAll`（8/8 PASS）
- `GET /api/hello-world?name=PakePlus` → `success=true, errorCode=S0000, message="Hello, PakePlus!", serverTime=2026-10-30 13:07:35`
- `POST /api/hash {"content":"abc","algorithm":"sha-256"}` → `ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad`，`digestLength=64`
- `POST /api/sort {"values":[5,3,8,1,9,2,7]}` → `[1,2,3,5,7,8,9]`，`compareCount=20`，`swapCount=10`
- `POST /api/sort {"values":[]}` → HTTP 400，`errorCode=A0001`，`errorMessage="values: 待排序数组不能为空"`
- `GET /api/reports?dimension=userSex` → HTTP 400，`不支持的统计维度: userSex，可选值: userType, userLevel, userDept`
- 报表幂等：连续 3 次 `GET /api/reports?dimension=userDept&days=7` → `totalCount` 恒为 7，饼图 `[技术部 4, 财务部 2, UNKNOWN 1]` 稳定；`pieChart.chartType=pie`、`barChart.chartType=bar`、折线轴 7 天、2 条序列
- 导出全量：`GET /api/exports?page=all&content=中文abc(URL 编码)&values=7,2,9,1&dimension=userLevel&days=7` → 200，`Content-Type: text/csv;charset=UTF-8`，`Content-Disposition: attachment; filename="demo-result-all-20261030130748.csv"`，`Cache-Control: no-store`，1056 字节，UTF-8 BOM 存在，四个分节（hello world / 哈希算法 / 冒泡排序 / 调用情况报表）齐全
- 导出单页：`page=sort&values=3,1,2` → `"[3, 1, 2]","[1, 2, 3]",3,2,0`，不含其它分节；`page=report` → 仅报表分节 25 行
- 导出不计入埋点：两次 `page=all` 导出后 `totalCount` 仍为 7

前端渲染验证：截图 `/tmp/shots2/real.png`（118835 字节，由 `chrome-headless-shell` 渲染真实页面），人工检视结果：三个 Tab 正常切换、冒泡排序结果区展示原始数组/排序结果/比较次数 20/交换次数 10、报表 KPI（调用总次数 10、调用人数 3、成功 10、失败 0）、折线/饼图/柱状图三图渲染、中文无豆腐块、无 JS 错误、无错误提示条。

## 过程中修复的两个真实缺陷

1. 中文请求头乱码：Tomcat 按 ISO-8859-1 解析请求头，`技术部` 变为 `ææ¯é¨`。`CallerInfoResolver` 增加 `decodeHeaderValue`：`%` 走 URLDecoder，否则按严格 UTF-8 重解码 ISO-8859-1 字节序列；ASCII 直通。
2. 报表/导出自身被埋点导致统计自增：刷新报表使 `totalCount` 从 29 增至 34。改为 `DemoApiEnum.tracked` + `TrackingConstants.isTracked()` 仅对三个业务接口埋点，新增单测 `should_notTrack_when_pathIsReportOrExport`。

## 已知限制与后续演进

1. 埋点存储为内存有界队列（上限 10000 条），重启即清空；生产应替换为 MySQL + Mapper（`demo_invocation_record`，含 `id/gmt_create/gmt_modified/creator/modifier`）。
2. 导出当前仅 CSV（UTF-8 BOM）；Excel 多图表导出待接入。
3. 请求行中的原始中文（未 URL 编码）会被 Tomcat 直接拒绝为 400，前端已统一 `encodeURIComponent`，外部调用方需自行编码；如需放宽可在连接器上配置。
4. 折线图 Y 轴刻度取整与饼图图例在极小比例下略有重叠，属展示层细节。
5. 未实现鉴权（`references/security.md` 未纳入本节点范围），调用人信息由请求头透传。

## 环境侧变更（非仓库产物）

- 通过 apt 安装 JDK 21 与 Maven（`APT::Sandbox::User=root`、`Dir::Cache::archives=/tmp/aptarch`），Maven 镜像配置为 `/tmp/mvn-settings.xml`（阿里云）。
- 安装 `fonts-noto-cjk` 用于中文字体渲染；Playwright chromium 缓存位于 `~/.cache/ms-playwright`。
- 验证期间启动的 18080/18081 端口进程已在收尾阶段停止。
