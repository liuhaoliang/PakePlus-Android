# Code Review Checklist

> **Change** `demo-service（三接口 + 导出 + 埋点报表，含 Android/iOS 前端页）` · **分支/Commit** `task/DEV-966dcd0a…` / `3815a0f`（Android）、`cf284e8`（iOS） · **日期** `2026-11-02`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：先在目标仓库对变更路径运行 `references/script/scan-all-rules.sh`，输出贴入 Step 3 和 Step 4 备注；再用 LLM 完成 Step 2–5 中脚本未覆盖项及复核。
>
> **预扫命令与结果**（PakePlus-Android-main 仓库根目录）：
> `bash …/scan-all-rules.sh demo-service/src/main/java demo-service/src/test/java demo-service/pom.xml`
> → `Summary: 17 findings (P0=3, P1=11, P2=3) | 52/222 rules scanned`。逐条复核结论见 §4.1–§4.3（其中 `S1.1 pom.xml:48`、`G16.2 HashAlgorithmService.java:40` 判定为误报）。

---

## Step 1 — 执行队列（产物 A）

> **Step4 列语义**：每个 **Sn / Gn** 表示「**本文件**在 Step4 审查中，对 `reliability-checklist.md` 第 **G*n*** 节、`security-checklist.md` 第 **S*n*** 节的扫描结论」。与变更无关填 `N/A`；已扫无命中填 `✅`；命中风险填 `⚠️` 或 `❌`。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|--------|
| 1 | `demo-service/src/main/java/com/pakeplus/demo/DemoApplication.java` | REQ-1 服务启动 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 2 | `demo-service/src/main/java/com/pakeplus/demo/common/ApiResponse.java` | REQ-1/3/4 统一响应结构 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 3 | `demo-service/src/main/java/com/pakeplus/demo/common/BusinessException.java` | REQ-1 统一业务异常 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 4 | `demo-service/src/main/java/com/pakeplus/demo/common/ErrorCodeEnum.java` | REQ-1 统一错误码 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 5 | `demo-service/src/main/java/com/pakeplus/demo/common/GlobalExceptionHandler.java` | REQ-1 异常统一出口 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 6 | `demo-service/src/main/java/com/pakeplus/demo/config/WebMvcConfig.java` | REQ-2/4 跨域与埋点注册 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 7 | `demo-service/src/main/java/com/pakeplus/demo/controller/DemoController.java` | REQ-1 三个接口 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 8 | `demo-service/src/main/java/com/pakeplus/demo/controller/ExportController.java` | REQ-3 导出接口 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 9 | `demo-service/src/main/java/com/pakeplus/demo/controller/TrackController.java` | REQ-4/5 报表与明细接口 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 10 | `demo-service/src/main/java/com/pakeplus/demo/export/ExportResult.java` | REQ-3 导出结果载体 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 11 | `demo-service/src/main/java/com/pakeplus/demo/export/ExportService.java` | REQ-3 各页面 CSV 渲染 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 12 | `demo-service/src/main/java/com/pakeplus/demo/model/HashRequest.java` | REQ-1 哈希入参 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 13 | `demo-service/src/main/java/com/pakeplus/demo/model/HashResponse.java` | REQ-1 哈希出参 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 14 | `demo-service/src/main/java/com/pakeplus/demo/model/HelloResponse.java` | REQ-1 Hello 出参 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 15 | `demo-service/src/main/java/com/pakeplus/demo/model/SortRequest.java` | REQ-1 排序入参 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 16 | `demo-service/src/main/java/com/pakeplus/demo/model/SortResponse.java` | REQ-1 排序出参 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 17 | `demo-service/src/main/java/com/pakeplus/demo/service/BubbleSortService.java` | REQ-1 冒泡排序 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 18 | `demo-service/src/main/java/com/pakeplus/demo/service/HashAlgorithmService.java` | REQ-1 哈希算法 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 19 | `demo-service/src/main/java/com/pakeplus/demo/service/HelloWorldService.java` | REQ-1 Hello 逻辑 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 20 | `demo-service/src/main/java/com/pakeplus/demo/track/CallRecord.java` | REQ-4 埋点记录模型 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 21 | `demo-service/src/main/java/com/pakeplus/demo/track/TrackDimensionEnum.java` | REQ-5 维度枚举 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 22 | `demo-service/src/main/java/com/pakeplus/demo/track/TrackInterceptor.java` | REQ-4 埋点采集 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 23 | `demo-service/src/main/java/com/pakeplus/demo/track/TrackItemVO.java` | REQ-5 分组统计项 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 24 | `demo-service/src/main/java/com/pakeplus/demo/track/TrackReportService.java` | REQ-5 维度聚合与趋势 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 25 | `demo-service/src/main/java/com/pakeplus/demo/track/TrackSampleDataService.java` | REQ-5 演示数据 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 26 | `demo-service/src/main/java/com/pakeplus/demo/track/TrackStore.java` | REQ-4 埋点存储 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 27 | `demo-service/src/main/java/com/pakeplus/demo/track/TrackSummaryVO.java` | REQ-5 报表结构 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 28 | `demo-service/src/main/java/com/pakeplus/demo/track/TrackTrendVO.java` | REQ-5 折线图结构 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 29 | `demo-service/src/main/java/com/pakeplus/demo/track/UserIdentity.java` | REQ-4 调用人身份 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 30 | `demo-service/src/test/java/com/pakeplus/demo/DemoApiIntegrationTest.java` | REQ-1~5 接口集成验证 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 31 | `demo-service/src/test/java/com/pakeplus/demo/service/BubbleSortServiceTest.java` | REQ-1 排序单测 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 32 | `demo-service/src/test/java/com/pakeplus/demo/service/HashAlgorithmServiceTest.java` | REQ-1 哈希单测 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 33 | `demo-service/src/test/java/com/pakeplus/demo/service/HelloWorldServiceTest.java` | REQ-1 Hello 单测 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 34 | `demo-service/src/test/java/com/pakeplus/demo/track/TrackReportServiceTest.java` | REQ-5 聚合单测 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 35 | `demo-service/src/test/java/com/pakeplus/demo/track/UserIdentityTest.java` | REQ-4 身份解析单测 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |

非 Java 变更（跳过，不计入 Java 队列）：

| # | 文件 | 处理 |
|---|------|------|
| S1 | `.gitignore` | 跳过（构建产物忽略（demo-service/target）） |
| S2 | `app/src/main/assets/app.json` | 跳过（Android 壳配置 webUrl 指向本地演示页） |
| S3 | `app/src/main/assets/demo/index.html` | 跳过（前端页面（非 Java，跨仓契约核对）） |
| S4 | `demo-service/pom.xml` | 跳过（构建配置（依赖/资源复制）） |
| S5 | `demo-service/src/main/resources/application.yml` | 跳过（服务配置（SNAKE_CASE 序列化契约）） |
| S6 | `docs/ARCHITECTURE.md` | 跳过（架构说明） |
| S7 | `docs/modules/demo-service/README.md` | 跳过（模块文档） |
| S8 | `[PakePlus-iOS] PakePlus/WebView.swift` | 跳过（Swift，非 Java） |
| S9 | `[PakePlus-iOS] PakePlus/demo.html` | 跳过（H5 镜像页，非 Java） |

- 由 `git diff --name-status $(git merge-base origin/main HEAD) HEAD` 展开；**禁止 glob**；非 Java 标 `跳过`。
- **守卫**：Java 文件 35 个（main 29 + test 6），守卫通过。
- **收口**：每文件各 **Sn/Gn** 列均非 `⬜`；与下方 Step 4 逐条 ID 表已交叉核对。

---

## Step 2 — 功能（产物 B）

> 仅从需求原文 / `docs/` 设计文档提取 REQ，勿臆造。不符标 **P0**。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | Given 演示页，When 调用三个接口，Then 分别返回 Hello/哈希/排序结果 | 需求原文「分别写三个接口helloworld、哈希算法以及冒泡排序」 | `DemoController.java`、`HelloWorldService.java`、`HashAlgorithmService.java`、`BubbleSortService.java` | ✅ | `DemoController.java:46-83`；`DemoApiIntegrationTest.java:36-74` 通过 |
| REQ-2 | Given App，When 打开演示页，Then 三个 tab 分别展示执行结果 | 「前端新增一个页面，有三个tab分别展示不同的执行结果」 | `app/src/main/assets/demo/index.html` | ✅ | `index.html:143-207`（实际 4 个 tab，含报表页，属超集） |
| REQ-3 | Given 任一页面，When 点击导出，Then 后端返回该页面结果文件 | 「新增导出按钮，后台提供导出接口，支持导出各个页面的展示结果」 | `ExportController.java`、`ExportService.java`、`index.html` | ✅ | `ExportController.java:45-66`；`ExportService.java:62-136`；前端 `index.html:393-468`；`DemoApiIntegrationTest.java:101-131` |
| REQ-4 | Given 调用接口，When 请求完成，Then 记录调用次数与调用人 | 「后端再做个埋点，获取调用次数和调用人」 | `TrackInterceptor.java`、`TrackStore.java`、`UserIdentity.java`、`WebMvcConfig.java` | ✅ | `WebMvcConfig.java:33-36` 注册三个路径；`TrackInterceptor.java:26-47`；`DemoApiIntegrationTest.java:76-99` |
| REQ-5 | Given 报表页，When 切换维度，Then 用折线/饼/柱展示调用情况 | 「根据不同的维度：人员类型、人员层级、人员部门等，折线图以及饼图和柱状图不同展示形式」 | `TrackController.java`、`TrackReportService.java`、`TrackDimensionEnum.java`、`index.html` | ✅ | `TrackDimensionEnum.java:10-22`（4 维度）；`TrackReportService.java:39-53`（items+trend）；`index.html:209-253`、`541-724`（三图） |

---

## Step 3 — 可读性检查（产物 C）

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | UTF-8、无 Tab、文件名与类名一致 |
| A2 | 源文件结构/import 顺序 | ✅ | 无 `import *`；静态导入与非静态分组（测试类）；单顶层类型 |
| A3 | 代码样式 | ❌ | A3.4 行宽超 120：`GlobalExceptionHandler.java:31`(124)、`TrackController.java:39`(126)、`ExportService.java:75`(123)/`:102`(122)/`:147`(138)、`BubbleSortService.java:30`(122)、`HashAlgorithmService.java:41`(142)/`:61`(126)、`TrackReportServiceTest.java:35/37/53/54/67`(121) |
| A4 | 命名规范 | ✅ | 包名全小写；常量 UPPER_SNAKE；测试类 `XxxTest` |
| A5 | 编码实践 | ✅ | 重写方法均有 `@Override`；无空 catch（`UserIdentity:61` 有返回值） |
| A6 | 特定元素样式 | ✅ | switch 用箭头语法且枚举全覆盖/带 default；long 字面量 `0L/100D` 规范 |
| A7 | Javadoc 规范 | ✅ | public 类/方法均有 Javadoc；record 使用 `@param` 说明 |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

| ID | 状态 | 备注（命中写 `path:line`；预扫可粘贴脚本摘要） |
|----|------|--------------------------------------------------|
| B001 | N/A | 无对应代码形态（详见报告 §5） |
| B002 | N/A | 无对应代码形态（详见报告 §5） |
| B003 | N/A | 无对应代码形态（详见报告 §5） |
| B004 | N/A | 无对应代码形态（详见报告 §5） |
| B005 | N/A | 无对应代码形态（详见报告 §5） |
| B006 | N/A | 全部断言使用 AssertJ，无 JUnit assertEquals |
| B007 | N/A | 无 catch Throwable/Error |
| B008 | N/A | 未创建线程池 |
| B009 | N/A | 无对应代码形态（详见报告 §5） |
| B010 | N/A | 无对应代码形态（详见报告 §5） |
| B011 | N/A | BubbleSortService 对 Integer 使用 > / < 数值比较，无 == 比较 |
| B012 | N/A | 无对应代码形态（详见报告 §5） |
| B013 | N/A | 无对应代码形态（详见报告 §5） |
| B014 | N/A | 无对应代码形态（详见报告 §5） |
| B015 | N/A | 无对应代码形态（详见报告 §5） |
| B016 | N/A | 无对应代码形态（详见报告 §5） |
| B017 | N/A | 无 this == null |
| B018 | N/A | 三目分支均为 String/同类型 |
| B019 | N/A | 无对应代码形态（详见报告 §5） |
| B020 | N/A | 无对应代码形态（详见报告 §5） |
| B021 | N/A | 无对应代码形态（详见报告 §5） |
| B022 | N/A | 无对应代码形态（详见报告 §5） |
| B023 | N/A | 无对应代码形态（详见报告 §5） |
| B024 | N/A | 无对应代码形态（详见报告 §5） |
| B025 | N/A | 无对应代码形态（详见报告 §5） |
| B026 | N/A | 无对应代码形态（详见报告 §5） |
| B027 | N/A | 无对应代码形态（详见报告 §5） |
| B028 | N/A | 无对应代码形态（详见报告 §5） |
| B029 | N/A | 无对应代码形态（详见报告 §5） |
| B030 | N/A | 无对应代码形态（详见报告 §5） |
| B031 | N/A | 无对应代码形态（详见报告 §5） |
| B032 | N/A | 无对应代码形态（详见报告 §5） |
| B033 | N/A | 无对应代码形态（详见报告 §5） |
| B034 | N/A | 无对应代码形态（详见报告 §5） |
| B035 | N/A | 无对应代码形态（详见报告 §5） |
| B036 | N/A | 无对应代码形态（详见报告 §5） |
| B037 | N/A | 无对应代码形态（详见报告 §5） |
| B038 | N/A | 无对应代码形态（详见报告 §5） |
| B039 | N/A | 无对应代码形态（详见报告 §5） |
| B040 | N/A | 无对应代码形态（详见报告 §5） |
| B041 | N/A | 无 JDBC |
| B042 | N/A | JUnit5，无 JUnit3 用例 |
| B043 | N/A | 无对应代码形态（详见报告 §5） |
| B044 | N/A | 无对应代码形态（详见报告 §5） |
| B045 | N/A | 无对应代码形态（详见报告 §5） |
| B046 | N/A | 无对应代码形态（详见报告 §5） |
| B047 | N/A | 无对应代码形态（详见报告 §5） |
| B048 | N/A | 无对应代码形态（详见报告 §5） |
| B049 | N/A | 无对应代码形态（详见报告 §5） |
| B050 | N/A | 无对应代码形态（详见报告 §5） |
| B051 | N/A | 无对应代码形态（详见报告 §5） |
| B052 | N/A | 无对应代码形态（详见报告 §5） |
| B053 | N/A | 异常断言使用 assertThatThrownBy（自带 fail 语义） |
| B054 | N/A | 无对应代码形态（详见报告 §5） |
| B055 | N/A | 未使用 Mockito |
| B056 | N/A | 无对应代码形态（详见报告 §5） |
| B057 | N/A | 无对应代码形态（详见报告 §5） |
| B058 | N/A | 无对应代码形态（详见报告 §5） |
| B059 | N/A | 无对应代码形态（详见报告 §5） |
| B060 | N/A | 无对应代码形态（详见报告 §5） |
| B061 | N/A | 无对应代码形态（详见报告 §5） |
| B062 | N/A | 无对应代码形态（详见报告 §5） |
| B063 | N/A | 无对应代码形态（详见报告 §5） |
| B064 | N/A | 无对应代码形态（详见报告 §5） |
| B065 | N/A | 无对应代码形态（详见报告 §5） |
| B066 | N/A | 无对应代码形态（详见报告 §5） |
| B067 | N/A | 使用 nextInt(bound)/nextLong(origin,bound) |
| B068 | N/A | 无对应代码形态（详见报告 §5） |
| B069 | N/A | 无对应代码形态（详见报告 §5） |
| B070 | N/A | 无对应代码形态（详见报告 §5） |
| B071 | N/A | 无对应代码形态（详见报告 §5） |
| B072 | N/A | 无对应代码形态（详见报告 §5） |
| B073 | N/A | 无对应代码形态（详见报告 §5） |
| B074 | N/A | 无对应代码形态（详见报告 §5） |
| B075 | N/A | 无对应代码形态（详见报告 §5） |
| B076 | N/A | 无对应代码形态（详见报告 §5） |
| B077 | N/A | 无对应代码形态（详见报告 §5） |
| B078 | N/A | 无对应代码形态（详见报告 §5） |
| B079 | N/A | 无对应代码形态（详见报告 §5） |
| B080 | ✅ | 34 个用例均含断言（surefire 34/0/0/0） |
| B081 | N/A | 无对应代码形态（详见报告 §5） |
| M001 | N/A | 无重复条件分支 |
| M002 | N/A | TrackInterceptor 用 instanceof 具体类型模式匹配，非父类型恒真判断 |
| M003 | N/A | 无包装类构造器 |
| M004 | N/A | 无 printStackTrace |
| M005 | N/A | SortOutcome 为嵌套 record（隐式 static） |
| M006 | N/A | 无编译期恒真布尔 |
| M007 | N/A | 无空 catch；UserIdentity:61 catch 有 return 分支 |
| M008 | N/A | record 的 equals/hashCode 由编译器生成 |
| M009 | N/A | 无跨类型 equals 调用 |
| M010 | N/A | 无位运算 |
| M011 | N/A | switch 均以 -> 表达式或带 default，无 fall-through |
| M012 | N/A | 无 finally return/throw |
| M013 | N/A | 无强转与算术混合 |
| M014 | N/A | 无枚举 getClass |
| M015 | N/A | 无继承字段隐藏 |
| M016 | ❌ | 依赖 JVM 默认时区：HelloWorldService.java:34、ExportService.java:68/161、TrackReportService.java:52/92、TrackSampleDataService.java:40、TrackStore.java:29、TrackReportServiceTest.java:73/81/83（P1） |
| M017 | ✅ | 测试方法均带 @Test/@ParameterizedTest |
| M018 | N/A | 无显式 Lock |
| M019 | ✅ | CallRecord.dimensionValue switch 覆盖全部枚举值；ExportService switch 有 default |
| M020 | ✅ | TrackInterceptor 两个重写方法均有 @Override；WebMvcConfig 实现方法均有 @Override |
| M021 | N/A | 无 equals 重载 |
| M022 | N/A | 未使用 Optional |
| M023 | N/A | 无未重写 toString 对象打印 |
| M024 | N/A | 无 Optional.get |
| M025 | N/A | 无 final 类 protected 成员 |
| M026 | N/A | 无 Mockito |
| M027 | N/A | 无 ThreadLocal |
| I001 | N/A | 无 @Test(expected=)，均断言异常类型+消息 |
| I002 | N/A | 无 @DoNotMock |
| I003 | N/A | 无 AutoValue |
| I004 | N/A | 未使用 java.util.Date |
| I005 | N/A | 非 JUnit3 |
| I006 | N/A | 使用 @BeforeEach |
| I007 | N/A | 无 tearDown |
| I008 | N/A | 无 dataProvider |
| I009 | N/A | 统计类规则，不适用于评审结论 |
| I010 | N/A | DemoApiIntegrationTest 为 @SpringBootTest 集成测试，符合 I010 建议 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 无 DB；TrackStore 读改写在同一个 synchronized 方法内完成 |
| G1.2 | N/A | 无锁后二次校验场景 |
| G1.3 | N/A | 无乐观锁重试 |
| G1.4 | N/A | 无多资源加锁 |
| G2.1 | N/A | 无业务写接口；sample-data 为演示数据生成，重复调用仅追加 |
| G2.2 | N/A | 无重试/MQ 重投 |
| G2.3 | N/A | 无幂等键约定 |
| G3.1 | N/A | 无分布式事务 |
| G3.2 | N/A | 无 @Transactional |
| G4.1 | N/A | 无 SQL |
| G4.2 | N/A | 无 SQL |
| G4.3 | N/A | 无 SQL；内存快照全量 5000 条见 G11.2/性能建议 |
| G5.1 | N/A | 无 MQ |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存双写 |
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度任务 |
| G8.1 | ⚠️ | UserIdentity.java:61 catch IllegalArgumentException 后静默返回原值，无日志（P2） |
| G8.2 | N/A | 无外部依赖调用 |
| G8.3 | N/A | 无 I/O 流或连接 |
| G8.4 | N/A | 无线程池 |
| G8.5 | N/A | 无线程池 + ThreadLocal |
| G8.6 | N/A | 无线程池 |
| G9.1 | N/A | 无 RPC/HTTP 外呼 |
| G9.2 | N/A | 无外呼 |
| G9.3 | N/A | 无重试 |
| G10.1 | ⚠️ | avgElapsedMs=0、items=[] 无法区分「无数据」与「真实 0 值」，需配合 total 判定（P2） |
| G10.2 | ⚠️ | 埋点字段/接口无版本标识，前端硬编码 /api/demo 路径与 snake_case 字段（P2） |
| G11.1 | ⚠️ | ExportService/TrackInterceptor/TrackController/TrackSampleDataService 无独立单测，仅集成覆盖（P2） |
| G11.2 | ⚠️ | 缺并发追加、5000 条淘汰、>2000 排序超限的边界用例（P2） |
| G11.3 | ⚠️ | 主链路入参防御完整（BubbleSortService:26-34、HashAlgorithmService:32-34、TrackReportService:62 clamp）；但 `TrackStore.latest` 直接受 `limit<=0` 影响会抛 IndexOutOfBounds（TrackStore.java:65-71，当前仅经 Service clamp 保护）（P2） |
| G11.4 | ✅ | 无金额运算；ratio/average 仅展示用 double |
| G12.1 | N/A | 无资金链路 |
| G12.2 | N/A | 无资损场景 |
| G13.1 | ✅ | 业务异常 WARN、系统异常 ERROR、正常 INFO（GlobalExceptionHandler/TrackInterceptor） |
| G14.1 | N/A | 无金额 |
| G14.2 | N/A | 无多租户 |
| G14.3 | ❌ | 依赖 JVM 默认时区生成并存储时间字符串（M016 同源，P1） |
| G14.4 | ⚠️ | DateTimeFormatter 未显式绑定 ZoneId（P2，随 G14.3 一并修） |
| G15.1 | N/A | 无表结构变更 |
| G15.2 | ✅ | 全部为新增接口，未破坏既有能力 |
| G15.3 | N/A | 无灰度切换需求（新功能） |
| G16.1 | ✅ | 埋点覆盖接口、调用人、耗时、成功与否 |
| G16.2 | ⚠️ | UserIdentity.java:61 无日志；预扫 HashAlgorithmService.java:40 为误报（异常带 cause 重抛） |
| G16.3 | ✅ | 级别使用正确 |
| G16.4 | ✅ | 无空 catch / printStackTrace |
| G17.1 | ⚠️ | 埋点采集与 sample-data 无开关，异常时无法单点关闭（P2） |
| G17.2 | N/A | 无核心链路降级需求 |
| G17.3 | N/A | 无数据变更 |
| G18.1 | N/A | 安全补强项已在 S8.1/S9.2 覆盖 |
| G18.2 | N/A | 同上 |
| G18.3 | N/A | 同上 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | ✅ | 无 SQL/Mapper；预扫 pom.xml:48 命中为误报（资源目录占位符） |
| S1.2 | N/A | 无动态 SQL |
| S1.3 | N/A | 无 SQL |
| S2.1 | ✅ | 响应为 JSON，前端以 textContent 渲染动态值（index.html:337-341/509-519 的 innerHTML 仅拼固定标签） |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1 | N/A | 服务端不外呼 URL |
| S3.2 | N/A | 无跳转 |
| S3.3 | N/A | 无外呼 |
| S4.1 | N/A | 无命令执行 |
| S4.2 | N/A | 无外部命令 |
| S5.1 | N/A | 无 XML 解析 |
| S5.2 | N/A | 无 XPath |
| S6.1 | N/A | 无 Java 原生反序列化 |
| S6.2 | ✅ | 仅 Jackson 默认绑定，无 @JsonTypeInfo 多态 |
| S6.3 | N/A | 无凭证字段 |
| S7.1 | ✅ | 导出内容为服务端生成的 CSV，无上传入口 |
| S7.2 | ✅ | 文件名由服务端拼接（ExportService:70-73/161），无用户可控路径 |
| S7.3 | ✅ | 不落盘，直接以响应流下载 |
| S8.1 | ❌ | 全部 /api/demo/** 无鉴权；埋点身份取自客户端可伪造的 X-User-* 请求头（TrackInterceptor:29、UserIdentity:36-43） |
| S8.2 | ✅ | sample-data 用 POST；导出为幂等只读 GET |
| S8.3 | N/A | 无资源 ID 暴露 |
| S8.4 | N/A | 无 Cookie |
| S9.1 | ✅ | 无硬编码密钥 |
| S9.2 | ⚠️ | TrackInterceptor:44-46 INFO 日志输出 userId/userType/userDept，建议脱敏或降级为 DEBUG（P2） |
| S9.3 | ⚠️ | 支持 MD5 且演示链路走明文 HTTP（Android usesCleartextTraffic=true、iOS NSAllowsArbitraryLoads=true）（P2，演示取舍） |
| S9.4 | N/A | 随机数仅用于演示数据 |
| S10.1 | N/A | 无会话态，无 CSRF 面 |
| S10.2 | ❌ | allowedOriginPatterns("*") + allowedHeaders("*")（WebMvcConfig.java:26-28，P1） |
| S10.3 | N/A | 无 URL 跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | ⚠️ | Controller body 入参有 `@Valid`（`DemoController.java:58/75`），但 query 参数（`limit`/`count`/`dimension`）无 `@Validated` 约束，靠手写 clamp；建议补 `@Min/@Max` 或统一在 Service 校验（P2） |
| U1.2 | N/A | 示例项未启用 |
| U1.3 | N/A | 示例项未启用 |
| U2.1 | N/A | 未启用自定义规则 |
| U2.2 | N/A | 未启用自定义规则 |
| U2.3 | N/A | 未启用自定义规则 |

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（N/A 均附原因）
- [x] Step 5 全部 U* ID 均非 `⬜`
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`
