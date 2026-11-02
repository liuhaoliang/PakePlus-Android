# Code Review Report

> **Change** `demo-service（三接口 + 导出 + 埋点报表，含 Android/iOS 前端页）` · **分支/Commit** `task/DEV-966dcd0a…` / Android `3815a0f`、iOS `cf284e8` · **日期** `2026-11-02` · **审查者** AI
>
> 等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。执行顺序：先跑 `scan-all-rules.sh`（17 findings，52/222 规则），再 LLM 逐文件补扫与复核。
>
> **验证证据**：`cd demo-service && mvn -o -B -q test` → `EXIT=0`；surefire 汇总 `Tests run: 34, Failures: 0, Errors: 0, Skipped: 0`（6 个测试类）。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | `35`（main 29 + test 6） |
| 变更行数 | `+1823 / -0`（全部文件 `+2720 / -1`） |
| 变更来源 | `git diff --name-status $(git merge-base origin/main HEAD) HEAD`（Android）、`… origin/dev …`（iOS） |

### 1.1 队列清单（Java）

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| `DemoApplication` | `demo-service/src/main/java/com/pakeplus/demo/DemoApplication.java` | 启动类 |
| `ApiResponse` / `BusinessException` / `ErrorCodeEnum` / `GlobalExceptionHandler` | `demo-service/src/main/java/com/pakeplus/demo/common/*.java` | 统一响应与异常 |
| `WebMvcConfig` | `demo-service/src/main/java/com/pakeplus/demo/config/WebMvcConfig.java` | CORS + 埋点注册 |
| `DemoController` / `ExportController` / `TrackController` | `demo-service/src/main/java/com/pakeplus/demo/controller/*.java` | 业务/导出/报表入口 |
| `ExportResult` / `ExportService` | `demo-service/src/main/java/com/pakeplus/demo/export/*.java` | CSV 导出 |
| `HelloWorldService` / `HashAlgorithmService` / `BubbleSortService` | `demo-service/src/main/java/com/pakeplus/demo/service/*.java` | 三个接口实现 |
| `CallRecord` / `TrackDimensionEnum` / `TrackInterceptor` / `TrackItemVO` / `TrackReportService` / `TrackSampleDataService` / `TrackStore` / `TrackSummaryVO` / `TrackTrendVO` / `UserIdentity` | `demo-service/src/main/java/com/pakeplus/demo/track/*.java` | 埋点采集与聚合 |
| `HashRequest` / `HashResponse` / `HelloResponse` / `SortRequest` / `SortResponse` | `demo-service/src/main/java/com/pakeplus/demo/model/*.java` | DTO（record） |
| `DemoApiIntegrationTest` + 5 个单测 | `demo-service/src/test/java/com/pakeplus/demo/**` | 测试 |

### 1.2 非 Java 变更（跳过 + 跨仓契约核对）

| 仓库 | 文件 | 处理 |
|------|------|------|
| Android | `.gitignore`、`app/src/main/assets/app.json`、`app/src/main/assets/demo/index.html`、`demo-service/pom.xml`、`src/main/resources/application.yml`、`docs/*` | 非 Java，跳过逐文件 Step2–5；契约与构建结果以 §1.3 核对 |
| iOS | `PakePlus/WebView.swift`、`PakePlus/demo.html` | 非 Java，跳过；跨仓对齐见 §7 |

### 1.3 跨仓对齐点核对（本节点重点）

| 对齐点 | 结论 | 证据 |
|--------|------|------|
| 响应字段命名（后端 SNAKE_CASE ↔ 前端读取） | ✅ 一致 | `application.yml:8`；`index.html:388/406-408/442-446/478-482/509-513` 全部读 `server_time`/`digest_length`/`elapsed_ms`/`caller_count`/`called_at` 等 |
| 身份头契约（`X-User-*`，非 ASCII 需 URL 编码） | ✅ 一致 | 前端 `index.html:288-296` `encodeURIComponent(name/dept)`；后端 `UserIdentity.java:36-64` `URLDecoder.decode`；`UserIdentityTest` 覆盖 |
| API Base 平台差异 | ✅ 正确分离 | Android `index.html:262` `10.0.2.2:8080`；iOS `demo.html` 仅该行不同（`127.0.0.1:8080`），`diff` 其余 494 行完全一致 |
| 导出参数绑定（`values=1,2,3` → `List<Integer>`） | ✅ 可用 | `ExportController.java:50`；前端 `index.html:460-463` |
| 统一响应 `code=200` 判定 | ✅ 一致 | `ApiResponse.java:10/24-26`；`index.html:314` |
| 导出下载能力（WKWebView vs WebView） | ❌ 不一致（见 §7 跨仓 X-1） | Android `MainActivity.kt:274` 注册 `setDownloadListener`+`DownloadManager`；iOS `PakePlus/*.swift` 内 **0** 处 download / WKDownload / navigationDelegate |
| iOS 本地页优先加载 vs `app.json webUrl` | ⚠️ 行为变更（见 §7 跨仓 X-2） | `PakePlus/WebView.swift:51-55`、`:73-77` |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 1 | 3 | 15 |

> 跨仓非 Java 风险另计：P1 ×1、P2 ×1（见 §7.1，不计入上表）。P0/P1/P2 与 §8 任务项逐项对齐。

---

## 3. Step 2 — 功能（REQ）

REQ 来源：需求原文（本次任务 `requirement_section`）+ 设计文档 `docs/ARCHITECTURE.md`、`docs/modules/demo-service/README.md`。

### REQ-1: 三个接口（Hello World / 哈希算法 / 冒泡排序）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 演示页，When GET `/api/demo/hello?name=`，Then 返回问候语+服务器时间 | ✅ | 原文「分别写三个接口helloworld、哈希算法以及冒泡排序」 | `DemoController.java:46-49`；`DemoApiIntegrationTest.java:36-44` | 名称缺省回落 `World` |
| When POST `/api/demo/hash`，Then 返回摘要/长度/耗时，非法算法 400 | ✅ | 同上 + README「入参 `{text, algorithm}`」 | `DemoController.java:57-66`；`HashAlgorithmService.java:51-63`；`DemoApiIntegrationTest.java:46-61` | 算法别名归一化 `SHA1/SHA_1 → SHA-1` |
| When POST `/api/demo/sort`，Then 返回升/降序结果与比较/交换次数 | ✅ | 同上 | `DemoController.java:74-83`；`BubbleSortService.java:25-60`；`BubbleSortServiceTest` 7 例 | 入参不改写、上限 2000 |

### REQ-2: 前端页面三个 Tab 展示执行结果

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given App 打开演示页，Then 三个业务 Tab 分别展示结果 | ✅ | 原文「前端新增一个页面，有三个tab分别展示不同的执行结果」 | `index.html:143-207`（hello/hash/sort）+ `:209-253`（报表，超集） | 需求允许多出报表页；四个 Tab 均可用 |

### REQ-3: 导出按钮 + 后端导出接口（各页面结果）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 每个页面有导出按钮，点击下载对应 CSV | ✅ | 原文「新增导出按钮，后台提供导出接口，支持导出各个页面的展示结果」 | 前端 `index.html:157/181/203/225` + `393-468`；后端 `ExportController.java:45-66` | CSV 带 UTF-8 BOM，Android 中文 Excel 不乱码 |
| 报表页与调用明细均可导出 | ✅ | 同上 | `ExportService.java:114-163`；`DemoApiIntegrationTest.java:115-123` | 明细导出 `track-records-*.csv` |
| 非法模块返回 400/A0400 | ✅ | 同上 | `ExportService.java:64-76`；`DemoApiIntegrationTest.java:125-131` | — |

### REQ-4: 埋点采集调用次数与调用人

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 每次调用三个接口后记录接口、调用人、耗时、成功与否 | ✅ | 原文「后端再做个埋点，获取调用次数和调用人」 | `WebMvcConfig.java:33-36`；`TrackInterceptor.java:26-47`；`TrackStore.java:28-48` | 仅注册三个业务路径 |
| 按调用人身份五要素归集，缺失回落匿名 | ✅ | 同上 + README「调用人身份契约」 | `UserIdentity.java:36-64`；`UserIdentityTest` 3 例 | 中文头 URL 编码还原 |
| 调用次数按维度可统计 | ✅ | 同上 | `TrackReportService.java:39-53` | total/success/fail/callerCount |

### REQ-5: 报表可视化（维度 + 折线/饼/柱）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 切换人员类型/层级/部门（+接口）维度刷新报表 | ✅ | 原文「根据不同的维度：人员类型、人员层级、人员部门等」 | `TrackDimensionEnum.java:10-22`；`TrackController.java:38-41`；`index.html:215-220` | `fromCode` 非法值 400 |
| 折线图展示近 12 小时趋势 | ✅ | 原文「折线图」 | `TrackReportService.java:91-106`；`index.html:541-613` | 12 小时桶 |
| 柱状图 + 饼图展示分组次数/占比 | ✅ | 原文「饼图和柱状图」 | `index.html:615-710`；`TrackItemVO` 含 `ratio` | ratio 保留 4 位 |

**功能结论**：REQ-1~REQ-5 全部 ✅；无 P0 功能不符项。

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ⚠️ | A3.4 行宽 > 120 共 13 处：`GlobalExceptionHandler.java:31`(124)、`TrackController.java:39`(126)、`ExportService.java:75`(123)/`:102`(122)/`:147`(138)、`BubbleSortService.java:30`(122)、`HashAlgorithmService.java:41`(142)/`:61`(126)、`TrackReportServiceTest.java:35/37/53/54/67`(121)；A1/A2/A4/A5/A6/A7 ✅。另 `ExportService.java:102` 三层嵌套三目且 `outcome == null` 分支不可达（`BubbleSortService.sort` 非空返回或抛业务异常），建议拆分。 |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ⚠️ | P1/P2 | `G14.3`（P1，默认时区）、`G8.1/G16.2`（P2，静默兜底无日志）、`G11.1/G11.2`（P2，测试缺口）、`G11.3`（P2，`TrackStore.latest` 越界隐患）、`G10.1/G10.2/G17.1`（P2）；G1–G7/G9/G12/G15 无命中 |
| 安全 | `security-checklist.md` S1–S10 | ❌ | P0/P1/P2 | `S8.1`（P0，全接口无鉴权 + 身份头可伪造）、`S10.2`（P1，CORS 通配）、`S9.2/S9.3`（P2）；S1.1 预扫 `pom.xml:48` 为误报（资源目录占位符，无 SQL） |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ⚠️ | P1/P2 | 预扫：`M016`（8 处默认时区，P1）、`A3.4`（3 处，P2）；`G16.2 HashAlgorithmService.java:40` 复核为**误报**（异常带 cause 重抛为 `BusinessException`）；B001–B081、M001–M027、I001–I010 其余逐条 N/A，理由见 checklist §4.1 |

**补充（列表外，按 `path:line` 定位）**

- **P1** `GlobalExceptionHandler.java:44-49` — 未覆盖 `MethodArgumentTypeMismatchException` / `ConstraintViolationException`。`GET /api/demo/export/sort?values=abc`、`GET /api/demo/track/records?limit=xyz` 会落到 `Exception` 分支，返回 **500 + B0500**，而同类“客户端参数错误”应为 **400 + A0400**；前端按 `payload.message` 展示，用户会把参数错误看成系统故障。
- **P2** `TrackStore.java:45-47` — 容量淘汰用 `records.remove(0)`，5000 条满后每次追加 O(n) 搬移，且与 `append` 的 `synchronized` 叠加放大请求线程阻塞；建议 `ArrayDeque`/环形缓冲。
- **P2** `TrackReportService.java:50` — `countSuccess(records)` 在一次 `summarize` 内被调用两次（每次全量流过滤）；`summarize` 每次也全量 `snapshot()`+排序，5000 条上限内可接受，接入真实存储前需改为聚合查询。
- **P2** `ExportService.java:150` — `DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")` 在循环内创建；应提为常量。
- **P2** `ExportService.java:87-97` — 导出哈希页时若 `text` 缺失/为空，`digest` 直接抛 `A0400`，接口返回 JSON 而非 CSV；前端 `<a download>` 触发下载时用户无任何提示（建议导出空表头或前端预校验）。

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「未启用自定义规则」） |
|----|------|------|------|------------------------------------------|
| 自定义扩展 | `customized-checklist.md` U* | ⚠️ | P2 | `U1.1`：body 入参有 `@Valid`（`DemoController.java:58/75`），但 query 参数 `limit`/`count` 无 `@Min/@Max`，靠 `TrackReportService.java:62`、`TrackSampleDataService.java:38` 手写 clamp；U1.2/U1.3/U2.* 未启用 |

---

## 7. 结论

- **合并建议**：**修复后合并**（P0 若产品确认「演示服务、无真实数据」需书面豁免；3 项 P1 必修）
- **P0**：
  1. `S8.1` 全部 `/api/demo/**` 无任何鉴权，且埋点“调用人”完全取自客户端可伪造的 `X-User-*` 头 —— 报表的调用人/部门/层级统计可被任意伪造，不能作为真实调用结论。
- **P1**：
  1. `S10.2` CORS `allowedOriginPatterns("*")` + `allowedHeaders("*")`。
  2. `M016/G14.3` 服务端时间与报表时间桶依赖 JVM 默认时区（8 处），跨端/跨区展示与统计会偏移。
  3. 参数类型错误被映射为 500/B0500（`GlobalExceptionHandler.java:44-49`），错误码契约失真。
- **P2**：18 项（静默兜底无日志、测试缺口、`limit<=0` 越界、日志含人员属性、长行、query 无约束注解、`remove(0)` 淘汰、重复计算、循环内建 formatter、导出空文本语义、契约无版本、无开关等），详见 §8。
- **一句话**：功能与跨仓字段契约对齐到位、34 个测试全绿；主要风险集中在“身份来源不可信 + CORS 全放开”的安全面，以及时间时区与错误码语义的可靠性细节。

### 7.1 跨仓非 Java 风险（不计入上表计数）

- **P1（X-1）** iOS 导出按钮无下载实现：`[PakePlus-iOS] PakePlus/WebView.swift`（全文件无 `WKDownload`/`navigationDelegate`/download 相关代码），而 Android `[PakePlus-Android] MainActivity.kt:274` 已注册 `setDownloadListener` + `DownloadManager`。后端 `ExportController.java:73-77` 返回 `Content-Disposition: attachment`，在 iOS WKWebView 中会以“导航打开 CSV”方式处理，页面被替换/白屏，导出功能在 iOS 上等同失效。
- **P2（X-2）** `[PakePlus-iOS] PakePlus/WebView.swift:51-55/73-77`：内置 `demo.html` 存在时**始终**优先加载并在 `updateUIView` 直接 return，使 `app.json` 的 `webUrl` 配置与后续 URL 变更失效；建议增加开关或仅在 `webUrl` 为空/为内置地址时回落到本地页。

### 7.2 问题片段（必填）

#### P0 · S8.1 — 埋点身份取自客户端可伪造请求头

片段范围：`demo-service/src/main/java/com/pakeplus/demo/track/UserIdentity.java:36-43`

```java
L36|    public static UserIdentity from(HttpServletRequest request) {
L37|        return new UserIdentity(
L38|                resolve(request.getHeader(HEADER_USER_ID), ANONYMOUS_ID),
L39|                resolve(request.getHeader(HEADER_USER_NAME), ANONYMOUS_NAME),
L40|                resolve(request.getHeader(HEADER_USER_TYPE), ANONYMOUS_TYPE),
L41|                resolve(request.getHeader(HEADER_USER_LEVEL), ANONYMOUS_LEVEL),
L42|                resolve(request.getHeader(HEADER_USER_DEPT), ANONYMOUS_DEPT));
L43|    }
```

配合 `TrackInterceptor.java:29`（`request.setAttribute(IDENTITY_ATTRIBUTE, UserIdentity.from(request));`）与 `WebMvcConfig.java:25-29`（`/api/**` 无鉴权链路）——任何客户端设置 `X-User-Id: u1001` 即可把调用记到别人名下。

#### P1 · S10.2 — CORS 全通配

片段范围：`demo-service/src/main/java/com/pakeplus/demo/config/WebMvcConfig.java:24-30`

```java
L24|    public void addCorsMappings(CorsRegistry registry) {
L25|        registry.addMapping("/api/**")
L26|                .allowedOriginPatterns("*")   // 问题：任意来源，含 file:// 的 Origin: null
L27|                .allowedMethods("GET", "POST", "OPTIONS")
L28|                .allowedHeaders("*")
L29|                .maxAge(CORS_MAX_AGE_SECONDS);
L30|    }
```

#### P1 · M016/G14.3 — 依赖 JVM 默认时区

片段范围：`demo-service/src/main/java/com/pakeplus/demo/track/TrackReportService.java:91-104`

```java
L91|    private List<TrackTrendVO> buildTrend(List<CallRecord> records) {
L92|        LocalDateTime currentHour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0); // 问题：默认时区
L93|        List<LocalDateTime> buckets = new ArrayList<>(TREND_HOURS);
...
L103|            trend.add(new TrackTrendVO(bucket.format(HOUR_FORMATTER), count));
```

同类：`HelloWorldService.java:34`、`ExportService.java:68/161`、`TrackReportService.java:52`、`TrackSampleDataService.java:40`、`TrackStore.java:29`。

#### P1 — 参数类型错误映射为 500

片段范围：`demo-service/src/main/java/com/pakeplus/demo/common/GlobalExceptionHandler.java:44-49`

```java
L44|    @ExceptionHandler(Exception.class)
L45|    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception exception) {
L46|        LOGGER.error("unexpected exception occurred", exception);
L47|        return ResponseEntity.status(ErrorCodeEnum.SYSTEM_ERROR.getHttpStatus())
L48|                .body(ApiResponse.failure(ErrorCodeEnum.SYSTEM_ERROR, ErrorCodeEnum.SYSTEM_ERROR.getMessage()));
L49|    }
```

#### P2 · G8.1/G16.2 — 静默兜底无日志

片段范围：`demo-service/src/main/java/com/pakeplus/demo/track/UserIdentity.java:55-64`

```java
L55|    private static String decodeIfEncoded(String value) {
L56|        if (!value.contains("%")) {
L57|            return value;
L58|        }
L59|        try {
L60|            return URLDecoder.decode(value, StandardCharsets.UTF_8);
L61|        } catch (IllegalArgumentException exception) {   // 问题：吞异常且无任何日志
L62|            return value;
L63|        }
L64|    }
```

#### P2 · G11.3 — `TrackStore.latest` 对 `limit<=0` 越界

片段范围：`demo-service/src/main/java/com/pakeplus/demo/track/TrackStore.java:65-71`

```java
L65|    public synchronized List<CallRecord> latest(int limit) {
L66|        int size = records.size();
L67|        int fromIndex = Math.max(0, size - limit);   // limit<=0 时 fromIndex>size
L68|        List<CallRecord> latestRecords = new ArrayList<>(records.subList(fromIndex, size)); // IndexOutOfBounds
L69|        latestRecords.sort((left, right) -> Long.compare(right.sequence(), left.sequence()));
L70|        return latestRecords;
L71|    }
```

#### P2 · 可读性 A3.4 — 超长行（示例）

片段范围：`demo-service/src/main/java/com/pakeplus/demo/service/HashAlgorithmService.java:40-42`

```java
L40|        } catch (NoSuchAlgorithmException exception) {
L41|            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, "当前 JDK 不支持该哈希算法: " + normalizedAlgorithm, exception);  // 142 chars
L42|        }
```

#### P2 · `ExportService` 嵌套三目 / 不可达分支

片段范围：`demo-service/src/main/java/com/pakeplus/demo/export/ExportService.java:99-105`

```java
L99|    private String exportSort(List<Integer> values, String order) {
L100|        BubbleSortService.SortOutcome outcome = bubbleSortService.sort(values, order); // 非空或抛异常
L101|        StringBuilder builder = new StringBuilder();
L102|        builder.append("排序方向,").append(escape(outcome == null ? "" : order == null ? "asc" : order)).append(CRLF);
L103|        builder.append("比较次数,").append(outcome.comparisons()).append(CRLF);
```

#### P2 · `ExportService` 循环内创建 formatter

片段范围：`demo-service/src/main/java/com/pakeplus/demo/export/ExportService.java:147-152`

```java
L147|        builder.append("调用时间,接口,人员ID,...").append(CRLF);
L148|        for (CallRecord record : records) {
L149|            List<String> columns = new ArrayList<>();
L150|            columns.add(record.calledAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))); // 每次循环 new
L151|            columns.add(record.apiPath());
L152|            columns.add(record.userId());
```

#### P2 · `TrackReportService` 重复计算

片段范围：`demo-service/src/main/java/com/pakeplus/demo/track/TrackReportService.java:50-52`

```java
L50|        return new TrackSummaryVO(total, countSuccess(records), total - countSuccess(records),  // 两次全量过滤
L51|                averageElapsed(records), countDistinctCallers(records), dimension.getCode(), dimension.getLabel(),
L52|                items, buildTrend(records), LocalDateTime.now().format(TIME_FORMATTER));
```

#### P2 · U1.1 — query 参数无约束注解

片段范围：`demo-service/src/main/java/com/pakeplus/demo/controller/TrackController.java:49-52`

```java
L49|    @GetMapping("/records")
L50|    public ApiResponse<List<CallRecord>> records(@RequestParam(required = false, defaultValue = "50") Integer limit) {
L51|        return ApiResponse.success(trackReportService.latestRecords(limit));   // 无 @Min/@Max，依赖服务内 clamp
L52|    }
```

#### P2 · 缺失类问题（无代码片段）

- `G11.1`：`ExportService`、`TrackInterceptor`、`TrackController`、`TrackSampleDataService` 无独立单测（仅集成覆盖）——缺失项，无片段。
- `G11.2`：缺 5000 条淘汰、2000 上限、并发追加、`limit<=0` 的边界用例——缺失项，无片段。
- `G10.1/G10.2/G17.1/S9.2/S9.3` 见 §8 任务项，代码位置已在 checklist 备注中标注。

---

## 8. 修复任务列表

### P0

- [ ] **P0** `track/UserIdentity.java:36-43`（`S8.1`）— 埋点身份改为网关注入/签名校验（仅信任受信来源的 `X-User-*`），或至少在部署层为 `/api/**` 接入鉴权；对外丢弃未签名身份的写入口径并在报表标注「未授权来源」，避免统计被伪造。

### P1

- [ ] **P1** `config/WebMvcConfig.java:25-29`（`S10.2`）— 将 `allowedOriginPatterns("*")` 改为配置化白名单（`app.cors.allowed-origins`），`allowedHeaders` 收敛为 `Content-Type, X-User-Id, X-User-Name, X-User-Type, X-User-Level, X-User-Dept`。
- [ ] **P1** `track/TrackReportService.java:52`、`:92`、`service/HelloWorldService.java:34`、`export/ExportService.java:68`、`:161`、`track/TrackStore.java:29`、`track/TrackSampleDataService.java:40`（`M016/G14.3`）— 统一引入 `Clock`/`ZoneId`（建议 `spring.jackson` 之外显式配置 `app.timezone`，默认 `Asia/Shanghai`），时间字符串格式显式 `withZone`，趋势桶按同一时区计算。
- [ ] **P1** `common/GlobalExceptionHandler.java:44-49` — 增加 `MethodArgumentTypeMismatchException`、`ConstraintViolationException`、`MissingServletRequestParameterException` 处理，统一返回 400 + `A0400`，避免参数错误落到 500。
- [ ] **P1** 跨仓 `[PakePlus-iOS] PakePlus/WebView.swift`（X-1）— 为 WKWebView 补下载能力（`WKNavigationDelegate` + `WKDownloadDelegate` 或 JS 桥接 base64 保存），与 Android `MainActivity.kt:274` 行为对齐。

### P2（可选）

- [ ] **P2** `track/UserIdentity.java:61`（`G8.1/G16.2`）— catch 分支补 `LOGGER.debug("identity header decode failed, value={}", value, exception)`（注意脱敏）。
- [ ] **P2** `track/TrackStore.java:65-71`（`G11.3`）— `latest` 入参自校验（`limit <= 0` 直接返回空列表或 `IllegalArgumentException`）。
- [ ] **P2** `track/TrackStore.java:45-47` — 淘汰逻辑改用 `ArrayDeque`/环形缓冲，避免 `remove(0)` O(n)。
- [ ] **P2** `track/TrackReportService.java:50` — `successCount` 只算一次并复用；`summarize` 抽私有方法返回聚合结果，减少重复遍历。
- [ ] **P2** `track/TrackInterceptor.java:44-46`（`S9.2`）— 日志中的 `userId/userDept` 脱敏或降为 DEBUG。
- [ ] **P2** `service/HashAlgorithmService.java:18-22`（`S9.3`）— MD5 仅在演示开关下暴露，并在响应/Javadoc 标注「非安全用途」。
- [ ] **P2** `export/ExportService.java:102` — 拆分三层三目并删除不可达的 `outcome == null` 分支。
- [ ] **P2** `export/ExportService.java:150` — `DateTimeFormatter` 提为 `private static final` 常量。
- [ ] **P2** `export/ExportService.java:87-97` — 导出哈希页对空 `text` 返回带表头的空 CSV（或与前端约定预校验），避免下载请求得到 JSON 错误。
- [ ] **P2** `common/GlobalExceptionHandler.java:31`、`controller/TrackController.java:39`、`export/ExportService.java:75`、`:102`、`:147`、`service/BubbleSortService.java:30`、`service/HashAlgorithmService.java:41`、`:61`、`test/track/TrackReportServiceTest.java:35`、`:37`、`:53`、`:54`、`:67`（`A3.4`）— 按 120 列换行。
- [ ] **P2** `controller/TrackController.java:49-52`、`:60-64`（`U1.1`）— query 参数补 `@Validated` + `@Min/@Max`，或在 Controller 层显式 clamp 并保留服务层兜底。
- [ ] **P2** `track/TrackSummaryVO.java:19-21`（`G10.1`）— 补充 `hasData`/`dataRange` 字段，区分「无数据」与「真实 0 值」。
- [ ] **P2** `controller/DemoController.java:24-26`（`G10.2`）— 接口路径与字段增加版本前缀（`/api/v1/demo`）或契约文档标注，避免前端硬编码漂移。
- [ ] **P2** `config/WebMvcConfig.java:33-36`（`G17.1`）— 埋点与 `sample-data` 增加配置开关（`app.track.enabled`）便于故障止血。
- [ ] **P2** 补测：`ExportService`（空模块/空 text/超长数组）、`TrackInterceptor`（成功与 4xx 判定）、`TrackStore`（5000 淘汰、并发追加）、`TrackReportService`（`limit<=0`）（`G11.1/G11.2`）。
- [ ] **P2** 跨仓 `[PakePlus-iOS] PakePlus/WebView.swift:51-55/73-77`（X-2）— 内置页优先加载改为可配置（仅当 `webUrl` 为空或为内置地址时回落），恢复 `app.json` 配置语义。
