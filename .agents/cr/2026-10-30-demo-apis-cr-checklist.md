# Code Review Checklist

> **Change** `demo-apis`（demo-server：三接口 + 三 Tab 前端 + 导出 + 埋点 + 报表）· **分支** `task/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-b42f3756-eac2-4dab-beff-9d82e4bcd9e2` / **Commit** `7771209` · **日期** `2026-10-30`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：写入本清单并进入逐文件审查前，先在目标仓库对变更路径运行 `references/script/scan-all-rules.sh`，将输出贴入 Step 3 和 Step 4 备注；再用 LLM 完成 Step 2–5 中脚本未覆盖项及复核。

**预扫命令（已执行，在 `PakePlus-Android-main` 根目录）**

```text
bash /root/.agentix/skills/managed/dtazziboot-java-code-review/references/script/scan-all-rules.sh demo-server

=== Step 4 Rule Scan (B/M/I + A/S/G) ===
Targets: demo-server
Engine:  ripgrep

[P0] G16.2 — CatchWithoutLogging: demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/ExportServiceImpl.java:267
[P0] G16.2 — CatchWithoutLogging: demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/HashServiceImpl.java:66
[P0] G16.2 — CatchWithoutLogging: demo-server/src/main/java/com/oaikes/pakeplus/demo/tracking/CallerInfoResolver.java:78
[P0] G16.2 — CatchWithoutLogging: demo-server/src/main/java/com/oaikes/pakeplus/demo/tracking/CallerInfoResolver.java:92
[P0] G16.2 — CatchWithoutLogging: demo-server/src/main/java/com/oaikes/pakeplus/demo/tracking/InvocationTrackInterceptor.java:77
[P1] M016 — JavaTimeDefaultTimeZone: demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/ExportServiceImpl.java:92
[P1] M016 — JavaTimeDefaultTimeZone: demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/HelloWorldServiceImpl.java:29
[P1] M016 — JavaTimeDefaultTimeZone: demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/InvocationTrackServiceImpl.java:46
[P1] M016 — JavaTimeDefaultTimeZone: demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/ReportServiceImpl.java:75
[P1] M016 — JavaTimeDefaultTimeZone: demo-server/src/test/java/com/oaikes/pakeplus/demo/service/impl/ReportServiceImplTest.java:69
[P1] M016 — JavaTimeDefaultTimeZone: demo-server/src/test/java/com/oaikes/pakeplus/demo/service/impl/ReportServiceImplTest.java:166
[P1] S10.2 — CorsWildcard: demo-server/src/main/java/com/oaikes/pakeplus/demo/config/WebMvcConfig.java:31

=== Summary: 12 findings (P0=5, P1=7, P2=0) | 52/222 rules scanned ===
```

**脚本结论人工复核（误报/定级）**

- `ExportServiceImpl.java:267`、`HashServiceImpl.java:66`、`InvocationTrackInterceptor.java:77`：catch 内**重新抛出**或**写了 WARN 日志**，属脚本误报 → 不成立。
- `CallerInfoResolver.java:78`、`:92`：确认无日志（`:79` 有注释说明、`:92-94` 无注释）→ **成立**，按清单 `G16.2` 定级 **P1**（脚本标 P0；`G16.4` 空 catch 不成立，有返回兜底）。
- `M016`（6 处）+ `S10.2`（1 处）：确认成立。

---

## Step 1 — 执行队列（产物 A）

> **Step4 列语义**：每个 **Sn / Gn** 表示「**本文件**在 Step4 审查中，对 `reliability-checklist.md` 第 **G*n*** 节、`security-checklist.md` 第 **S*n*** 节的扫描结论」。与变更无关填 `N/A`；已扫无命中填 `✅`；命中风险填 `⚠️` 或 `❌`。

**N/A 语义（Legend）**

- `N/A(无相关场景)`：该文件不含该类逻辑（无 SQL / 无 MQ / 无缓存 / 无调度 / 无事务 / 无外部调用 / 无线程池 / 无 ThreadLocal 等）。
- `N/A(无 SQL)`：全仓未引入任何持久层，G4/G5/G6 结构性不适用。
- `N/A(演示工程无监控栈)`：无 metrics/告警体系可接入。
- `N/A(测试代码)`：测试代码不承担生产可靠性职责。
- 数据载体（DTO/VO/entity record）不承担并发、I/O、事务职责，仅核销契约（G10）与数据安全（S9）。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|--------|
| 1 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/DemoServerApplication.java` | 启动类 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ 已审 |
| 2 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/common/ApiResponse.java` | 统一响应体契约 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ 已审 |
| 3 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/common/BizException.java` | 业务异常契约 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ 已审 |
| 4 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/common/ErrorCodeEnum.java` | 错误码契约 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ 已审 |
| 5 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/common/GlobalExceptionHandler.java` | 全局异常→统一响应 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ⚠️ | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | ⚠️ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ⚠️ | N/A(无相关场景) | ⚠️ 已审有问题 |
| 6 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/config/WebMvcConfig.java` | 拦截器注册 + CORS | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ⚠️ | ⚠️ 已审有问题 |
| 7 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/constant/DemoConstants.java` | 常量定义 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ 已审 |
| 8 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/controller/DemoController.java` | REQ-1 三接口入口 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ 已审 |
| 9 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/controller/ExportController.java` | REQ-3 导出入口 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ⚠️ | ⚠️ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ⚠️ 已审有问题 |
| 10 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/controller/ReportController.java` | REQ-5 报表入口 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ 已审 |
| 11 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/enums/DemoApiEnum.java` | 埋点/导出接口编码 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ✅ 已审 |
| 12 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/enums/HashAlgorithmEnum.java` | 哈希算法白名单 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ✅ 已审 |
| 13 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/enums/ReportDimensionEnum.java` | 报表维度白名单 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ✅ 已审 |
| 14 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/model/dto/CallerInfo.java` | 调用人载体 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ✅ 已审 |
| 15 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/model/dto/ExportQuery.java` | 导出入参载体 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ✅ 已审 |
| 16 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/model/dto/HashRequest.java` | 哈希入参 + 校验注解 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ✅ 已审 |
| 17 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/model/dto/InvocationTrackCommand.java` | 埋点命令载体 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ✅ 已审 |
| 18 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/model/dto/ReportQuery.java` | 报表入参载体 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ✅ 已审 |
| 19 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/model/dto/SortRequest.java` | 排序入参 + 校验注解 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ✅ 已审 |
| 20 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/model/entity/InvocationRecordDO.java` | 埋点记录载体 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ✅ 已审 |
| 21 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/model/vo/ChartPointVO.java` | 图表点载体 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ✅ 已审 |
| 22 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/model/vo/ChartVO.java` | 饼/柱图载体 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ✅ 已审 |
| 23 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/model/vo/ExportFileVO.java` | 导出文件载体 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ✅ 已审 |
| 24 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/model/vo/HashVO.java` | 哈希结果载体 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ✅ 已审 |
| 25 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/model/vo/HelloWorldVO.java` | 问候结果载体 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ✅ 已审 |
| 26 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/model/vo/LineChartVO.java` | 折线图载体 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ✅ 已审 |
| 27 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/model/vo/LineSeriesVO.java` | 折线系列载体 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ✅ 已审 |
| 28 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/model/vo/ReportVO.java` | 报表契约载体 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ✅ 已审 |
| 29 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/model/vo/SortVO.java` | 排序结果载体 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ✅ 已审 |
| 30 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/ExportService.java` | 导出能力接口 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ 已审 |
| 31 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/HashService.java` | 哈希能力接口 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ 已审 |
| 32 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/HelloWorldService.java` | 问候能力接口 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ 已审 |
| 33 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/InvocationTrackService.java` | 埋点查询接口 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ 已审 |
| 34 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/ReportService.java` | 报表能力接口 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ 已审 |
| 35 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/SortService.java` | 排序能力接口 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ 已审 |
| 36 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/ExportServiceImpl.java` | REQ-3 导出实现 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | ⚠️ | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | ⚠️ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ⚠️ 已审有问题 |
| 37 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/HashServiceImpl.java` | REQ-1 哈希实现 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ 已审 |
| 38 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/HelloWorldServiceImpl.java` | REQ-1 问候实现 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | ⚠️ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ⚠️ 已审有问题 |
| 39 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/InvocationTrackServiceImpl.java` | REQ-4 埋点存储实现 | ✅ | ✅ | ⚠️ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | ⚠️ | N/A(无相关场景) | N/A(演示工程无监控栈) | ⚠️ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ⚠️ 已审有问题 |
| 40 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/ReportServiceImpl.java` | REQ-5 报表聚合实现 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无 SQL) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | ⚠️ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ⚠️ 已审有问题 |
| 41 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/SortServiceImpl.java` | REQ-1 冒泡排序实现 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | ⚠️ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ⚠️ 已审有问题 |
| 42 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/tracking/CallerInfoResolver.java` | REQ-4 调用人解析 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ⚠️ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | ⚠️ 已审有问题 |
| 43 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/tracking/InvocationTrackInterceptor.java` | REQ-4 埋点拦截 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ⚠️ | N/A(无相关场景) | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ⚠️ 已审有问题 |
| 44 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/tracking/TrackingConstants.java` | 埋点路径映射 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | ⚠️ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ⚠️ 已审有问题 |
| 45 | `demo-server/src/main/java/com/oaikes/pakeplus/demo/util/CsvBuilder.java` | REQ-3 CSV 构造 | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ⚠️ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ⚠️ 已审有问题 |
| 46 | `demo-server/src/test/java/com/oaikes/pakeplus/demo/service/impl/ExportServiceImplTest.java` | 导出用例（测试） | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(测试代码) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ 已审 |
| 47 | `demo-server/src/test/java/com/oaikes/pakeplus/demo/service/impl/HashServiceImplTest.java` | 哈希用例（测试） | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(测试代码) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ 已审 |
| 48 | `demo-server/src/test/java/com/oaikes/pakeplus/demo/service/impl/HelloWorldServiceImplTest.java` | 问候用例（测试） | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(测试代码) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ 已审 |
| 49 | `demo-server/src/test/java/com/oaikes/pakeplus/demo/service/impl/ReportServiceImplTest.java` | 报表用例（含 M016）（测试） | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | ⚠️ | N/A(无相关场景) | N/A(测试代码) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ⚠️ 已审有问题 |
| 50 | `demo-server/src/test/java/com/oaikes/pakeplus/demo/service/impl/SortServiceImplTest.java` | 排序用例（测试） | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(测试代码) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ 已审 |
| 51 | `demo-server/src/test/java/com/oaikes/pakeplus/demo/tracking/CallerInfoResolverTest.java` | 调用人解析用例（测试） | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(测试代码) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ 已审 |
| 52 | `demo-server/src/test/java/com/oaikes/pakeplus/demo/tracking/InvocationTrackInterceptorTest.java` | 埋点拦截用例（测试） | ✅ | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(测试代码) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | N/A(无相关场景) | ✅ 已审 |

**非 Java 变更（跳过，仅作 Step 2 功能证据）**

| 文件 | 说明 |
|------|------|
| `demo-server/pom.xml` | 跳过（构建描述符）；已核销依赖：spring-boot-starter-web / validation / test，JDK 21 |
| `demo-server/src/main/resources/application.yml` | 跳过（配置）；端口 8080、problemdetails 关闭 |
| `demo-server/src/main/resources/static/index.html` | 跳过（前端）；作为 REQ-2/REQ-3/REQ-5 的功能证据被引用（XSS 转义、导出参数拼装） |
| `demo-server/docs/ARCHITECTURE.md` | 跳过（文档） |
| `demo-server/docs/modules/demo-api/README.md` | 跳过（文档） |
| `demo-server/.gitignore` | 跳过（忽略规则） |
| `.agents/2026-10-30-demo-apis/impl.md` | 跳过（编码报告 / spec 输入） |

**收口**：Java 文件 52 个（main 45 + test 7），全部 `✅ 已审` / `⚠️ 已审有问题`，无 `⬜`。

---

## Step 2 — 功能（产物 B）

> 仅从需求原文与 `impl.md` 提 **REQ**，勿臆造。不符 spec 标 **P0**。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | 提供 helloworld / 哈希算法 / 冒泡排序三个接口 | 需求原文「分别写三个接口helloworld、哈希算法以及冒泡排序」；`impl.md` §接口契约 3 行 | `DemoController.java`、`HelloWorldServiceImpl.java`、`HashServiceImpl.java`、`SortServiceImpl.java` | ✅ | `DemoController.java:53/68/84`；`HashServiceImpl.java:27-35`；`SortServiceImpl.java:22-47`；单测 34 例通过（`target/surefire-reports` 7 文件 / 34 tests / 0 failures） |
| REQ-2 | 前端新增页面，三个 Tab 分别展示执行结果 | 需求原文「前端新增一个页面，有三个tab分别展示不同的执行结果」；`impl.md` §已实现文件清单 index.html 说明 | `static/index.html` | ✅ | `index.html:383-384`（hello 输入）、`:398`（hash 输入）、`:420`（sort 输入）、`:676`（调用 `/api/hello-world`）、`:751`（调用 `/api/reports`） |
| REQ-3a | 新增导出按钮，后台提供导出接口 | 需求原文「新增导出按钮，后台提供导出接口」 | `ExportController.java`、`index.html` | ✅ | `ExportController.java:43-57`；`index.html:965-993`（`exportResults(page)` + 双导出按钮 `btnExportCurrent`/`btnExportAll`） |
| REQ-3b | 导出必须支持导出**各个页面的展示结果** | 需求原文「支持导出各个页面的展示结果」；`impl.md` §接口契约 `/api/exports?...&content=&values=&dimension=&days=` | `ExportServiceImpl.java`、`ExportController.java`、`index.html` | ❌ **P0** | hello world 页展示值取决于 `helloName`（`index.html:676-677`），但导出链路既无 `name` 入参（`ExportController.java:44-48`）、前端也未传（`index.html:967-969` 仅传 `hashContent`），服务端回落硬编码 `DEFAULT_CONTENT`（`ExportServiceImpl.java:43/104/242-247`）→ 导出内容 ≠ 页面展示结果。hash / sort / report 三页 ✅（`ExportServiceImpl.java:117-186`） |
| REQ-4 | 后端埋点，获取调用次数与调用人 | 需求原文「后端再做个埋点，获取调用次数和调用人」；`impl.md` §IMPL 埋点段 | `InvocationTrackInterceptor.java`、`CallerInfoResolver.java`、`InvocationTrackServiceImpl.java` | ✅ | `InvocationTrackInterceptor.java:41-82`（统一记录）、`CallerInfoResolver.java:35-46`（5 个请求头）、`InvocationTrackServiceImpl.java:32-54`；`InvocationTrackInterceptorTest.java`、`CallerInfoResolverTest.java` 覆盖 |
| REQ-5a | 报表按人员类型 / 人员层级 / 人员部门维度查看调用情况 | 需求原文「根据不同的维度：人员类型、人员层级、人员部门等」；`impl.md` §接口契约 `/api/reports?dimension=` | `ReportServiceImpl.java`、`ReportDimensionEnum.java` | ✅ | `ReportDimensionEnum.java:13-19`（三维度白名单）、`ReportServiceImpl.java:156-176`；非法维度返回 `A0001`（`ReportServiceImpl.java:116-120`） |
| REQ-5b | 折线图 + 饼图 + 柱状图三种展示形式 | 需求原文「折线图以及饼图和柱状图不同展示形式」；`impl.md` §接口契约 `pieChart / barChart / lineChart` | `ReportServiceImpl.java`、`ReportVO.java`、`index.html` | ✅ | `ReportServiceImpl.java:87-103`、`:187-209`（轴=日期、每条维度一条折线）；`ReportVO.java:27-30`；`index.html:766-768` |
| REQ-6 | 统一响应契约（成功/错误码/用户提示） | `impl.md` §接口契约「统一响应：success / errorCode / errorMessage / userTip / data」 | `ApiResponse.java`、`GlobalExceptionHandler.java` | ⚠️ **P1** | 主链路一致（`ApiResponse.java:39-65`），但参数类型转换失败（如 `/api/exports?days=abc`）、请求体非法 JSON 未纳入统一响应体，落到 `GlobalExceptionHandler.java:63-68` 的兜底分支 → HTTP 500 + `errorMessage` 直接回传 `exception.getMessage()` |

---

## Step 3 — 可读性检查（产物 C）

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 52 个 `.java` 全部 UTF-8、无 Tab、无行尾空白（脚本化核验：LONG>120=0 / TABS=0 / TRAILING_WS=0） |
| A2 | 源文件结构/import 顺序 | ⚠️ | `ExportServiceImplTest.java:114` 内联全限定名 `org.mockito.ArgumentMatchers.any(...)`，未按 import 风格引入；其余 import 分组与字典序正常 |
| A3 | 代码样式 | ✅ | 4 空格缩进、K&R 大括号、行宽均 ≤120（脚本核验 0 处超限） |
| A4 | 命名规范 | ⚠️ | 7 个测试类的方法名使用下划线式 `should_xxx_when_yyy`（如 `ReportServiceImplTest.java:50`、`InvocationTrackInterceptorTest.java:43`），违反 lowerCamelCase（A4.3）；`InvocationTrackServiceImpl.java:37` 变量名 `recordDO` 带类型后缀（A4.5） |
| A5 | 编码实践 | ✅ | 重写方法均带 `@Override`；catch 块均有返回/注释（`CallerInfoResolver.java:79`）；无 `finalize()` |
| A6 | 特定元素样式 | ✅ | switch 表达式全覆盖枚举（`ReportServiceImpl.java:170-174`）无 fall-through；无 C 风格数组；无小写 `l` 字面量 |
| A7 | Javadoc 规范 | ✅ | public 类与成员均有 Javadoc；`@param/@return` 顺序正确；record 使用类级 `@param` 描述组件 |

---

## Step 4 — 可靠性检查（产物 D）

> **逐条核销（强制）**：G/S 每个 ID **独占一行**；Bug 模式**每条 B*/M*/I* 独占一行**（120 条）。

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

| ID | 状态 | 备注（命中写 `path:line`；预扫已并入） |
|----|------|--------------------------------------------------|
| ID | 状态 | 备注 |
|----|------|------|
| B001 | ✅ | 无字面量 parse/of（未见 `LocalDateTime.parse`/`UUID.fromString` 等） |
| B002 | ✅ | 无数组内容比较 |
| B003 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B004 | ✅ | 无数组 `toString()`；`SortVO` 用 `List` 承载 |
| B005 | ✅ | `Arrays.asList` 仅用于包装类型（`SortServiceImplTest.java:28`） |
| B006 | ✅ | 断言统一 AssertJ，无 `assertEquals` 参数颠倒 |
| B007 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B008 | ✅ | 未使用 `Executors` |
| B009 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B010 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B011 | ✅ | 枚举比较用 `==`（合法）；无包装类型 `==` 比较 |
| B012 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B013 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B014 | ✅ | 集合查询类型一致 |
| B015 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B016 | N/A(无相关场景) | N/A(无相关场景) |
| B017 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B018 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B019 | N/A(无相关场景) | N/A(无相关场景) |
| B020 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B021 | N/A(无相关场景) | N/A(无相关场景) |
| B022 | ✅ | 统一 `DateTimeFormatter`，无 `SimpleDateFormat` |
| B023 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B024 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B025 | ✅ | 无双括号初始化 |
| B026 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B027 | N/A(无相关场景) | N/A(无相关场景) |
| B028 | N/A(无相关场景) | N/A(无相关场景) |
| B029 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B030 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B031 | ✅ | `String.format` 未使用 |
| B032 | N/A(无相关场景) | N/A(无相关场景) |
| B033 | N/A(无相关场景) | N/A(无相关场景) |
| B034 | N/A(无相关场景) | N/A(无相关场景) |
| B035 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B036 | N/A(无相关场景) | N/A(无相关场景) |
| B037 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B038 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B039 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B040 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B041 | N/A(无相关场景) | N/A(无相关场景) |
| B042 | N/A(无相关场景) | N/A(无相关场景) |
| B043 | N/A(无相关场景) | N/A(无相关场景) |
| B044 | N/A(无相关场景) | N/A(无相关场景) |
| B045 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B046 | ✅ | 循环条件均在循环体内推进 |
| B047 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B048 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B049 | ✅ | 日期格式 `yyyy-MM-dd`（`DemoConstants.java:41`） |
| B050 | ✅ | 时间格式 `HH:mm:ss`（`DemoConstants.java:38`），无 12 小时制误配 |
| B051 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B052 | ✅ | 周年版 `YYYY` 未使用 |
| B053 | ✅ | 异常断言统一 `assertThatThrownBy` |
| B054 | N/A(无相关场景) | N/A(无相关场景) |
| B055 | ✅ | `when(...)` 均配 `thenReturn`；`verify` 无嵌套调用 |
| B056 | ✅ | `Arrays.asList` 结果未被结构修改 |
| B057 | ✅ | 无增强 for 内修改集合 |
| B058 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B059 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B060 | ✅ | 三目分支无 null 拆箱 |
| B061 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B062 | N/A(无相关场景) | N/A(无相关场景) |
| B063 | N/A(无相关场景) | N/A(无相关场景) |
| B064 | N/A(无相关场景) | N/A(无相关场景) |
| B065 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B066 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B067 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B068 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B069 | N/A(无相关场景) | N/A(无相关场景) |
| B070 | N/A(无相关场景) | N/A(无相关场景) |
| B071 | ✅ | 未见 `size() >= 0` |
| B072 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B073 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B074 | ✅ | 未见 `substring(0)` |
| B075 | ✅ | for 循环方向与退出条件一致 |
| B076 | N/A(无相关场景) | N/A(无相关场景) |
| B077 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B078 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B079 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| B080 | ✅ | 7 个测试类全部含断言 |
| B081 | ✅ | 预扫 + LLM 复核，已扫无命中 |
| M001 | ✅ | 无连续相同条件判断 |
| M002 | ✅ | `instanceof` 均为 `Object`/接口 → 具体类型（`InvocationTrackInterceptor.java:42/92/106`），不构成子类判父类 |
| M003 | ✅ | 无包装类构造器（未出现 `new Integer/Long/Boolean`） |
| M004 | ✅ | 无 `printStackTrace()`；异常统一走 `LOGGER`（`GlobalExceptionHandler.java:35/48/65`） |
| M005 | ✅ | 无内部类 |
| M006 | ✅ | 无编译期可判定的布尔表达式 |
| M007 | ✅ | 无空 catch（`CallerInfoResolver.java:78-81` 有 `return`，`:92-94` 有 `return`） |
| M008 | ✅ | 无类重写 `equals()`（record 由编译器生成配套实现） |
| M009 | ✅ | 无不兼容类型 `equals()` |
| M010 | ✅ | 无位运算表达式 |
| M011 | ✅ | 无 `switch` 语句 fall-through（`ReportServiceImpl.java:170-174` 为 switch 表达式） |
| M012 | ✅ | 无 `finally` 中 return/throw（全仓无 `finally` 块） |
| M013 | ✅ | 无浮点强转（`/ 1_000_000L` 为整型运算，`SortServiceImpl.java:45`、`InvocationTrackInterceptor.java:93`） |
| M014 | ✅ | 无枚举 `getClass()` |
| M015 | ✅ | 无继承字段隐藏（无类继承层次，仅 `extends RuntimeException`） |
| M016 | ❌ P1 | 7 处未显式指定时区：`ExportServiceImpl.java:92`、`HelloWorldServiceImpl.java:29`、`InvocationTrackServiceImpl.java:46`、`ReportServiceImpl.java:75`、`ReportServiceImplTest.java:69/166`（脚本命中，已复核成立） |
| M017 | ✅ | JUnit5 用例均带 `@Test`（34 例，`target/surefire-reports` 全部执行） |
| M018 | ✅ | 无显式加锁 |
| M019 | ✅ | `ReportDimensionEnum` switch 表达式覆盖全部枚举值（`ReportServiceImpl.java:170-174`），无 default 缺失风险 |
| M020 | ✅ | 重写方法均带 `@Override`（`InvocationTrackInterceptor.java:40/50` 等） |
| M021 | ✅ | 无 `equals(SpecificType)` 误写 |
| M022 | ✅ | 无 `Optional.of(null)`（全仓未使用 `Optional`） |
| M023 | ✅ | 打印/序列化对象均已重写 `toString()`（`ApiResponse.java:107`、`BizException.java:42`）；record 自动生成 |
| M024 | ✅ | 无 `Optional` 空值误用 |
| M025 | ✅ | 无 `final class` 含 `protected` 成员（`CallerInfoResolver`/`TrackingConstants`/`DemoConstants` 均为 `private` 构造 + `public static`） |
| M026 | ✅ | `@Mock` 字段均非 `static`（`InvocationTrackInterceptorTest.java:28`、`ReportServiceImplTest.java:42`、`ExportServiceImplTest.java:35`） |
| M027 | N/A(无相关场景) | 全仓未使用 `ThreadLocal` |
| I001 | ✅ | 异常断言含明细：`hasMessageContaining("不支持…")`、`getErrorCodeEnum()` 比对（`HashServiceImplTest.java:55`、`ReportServiceImplTest.java:114-116`） |
| I002 | N/A(无相关场景) | 无 `@DoNotMock` 标注类 |
| I003 | N/A(无相关场景) | 无 `@AutoValue` 类 |
| I004 | ✅ | 未使用 `java.util.Date`，统一 `java.time` |
| I005 | ✅ | 全部 JUnit 5（`org.junit.jupiter`），无 JUnit3 混用 |
| I006 | ✅ | 初始化用 `@BeforeEach`（`InvocationTrackInterceptorTest.java:35`、`ExportServiceImplTest.java:40`） |
| I007 | N/A(无相关场景) | 无用例级 `tearDown` |
| I008 | N/A(无相关场景) | 无 `dataProvider` |
| I009 | ✅ | 统计口径：7 个测试类 / 34 个用例（`mvn test` 实测 tests=34） |
| I010 | ✅ | 仅 `@ExtendWith(MockitoExtension.class)`，未启动 Spring 容器（`InvocationTrackInterceptorTest.java:25` 等） |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A(无相关场景) | 无事务/先读后写场景（无持久层） |
| G1.2 | N/A(无相关场景) | 无加锁后更新 |
| G1.3 | N/A(无相关场景) | 无乐观锁 |
| G1.4 | N/A(无相关场景) | 无多资源加锁 |
| G2.1 | N/A(无相关场景) | 埋点为逐次追加流水，业务上无需幂等键 |
| G2.2 | N/A(无相关场景) | 无重试 / 定时任务 / MQ 重投 |
| G2.3 | N/A(无相关场景) | 无幂等键约定 |
| G3.1 | N/A(无相关场景) | 无分布式事务 |
| G3.2 | N/A(无相关场景) | 无 `@Transactional` |
| G4.1 | N/A(无 SQL) | 全仓未引入持久层 |
| G4.2 | N/A(无 SQL) | 同上 |
| G4.3 | N/A(无 SQL) | 同上；内存全量扫描见 `InvocationTrackServiceImpl.java:57-66`（数据上限 10000，可接受） |
| G5.1 | N/A(无相关场景) | 未接入 MQ |
| G6.1 | N/A(无相关场景) | 未使用缓存中间件 |
| G6.2 | N/A(无相关场景) | 无缓存双写 |
| G7.1 | N/A(无相关场景) | 无调度任务 |
| G7.2 | N/A(无相关场景) | 无调度任务 |
| G8.1 | ⚠️ P1 | `InvocationTrackInterceptor.java:77-81` 埋点异常仅 WARN 记录、无指标/告警，埋点丢失不可观测（已按设计保证不影响主流程，缓解程度中等） |
| G8.2 | ✅ | 无强依赖外部系统的核心链路 |
| G8.3 | ✅ | `CsvBuilder` 纯内存拼接；无 I/O 流/连接/锁需释放；`downloadBlob` 有 `revokeObjectURL`（`index.html:1007-1009`） |
| G8.4 | N/A(无相关场景) | 未自建线程池 / 定时任务 |
| G8.5 | N/A(无相关场景) | 未使用 `ThreadLocal` |
| G8.6 | N/A(无相关场景) | 未创建线程池 |
| G9.1 | N/A(无相关场景) | 服务端无出站 HTTP/RPC/DB/Redis 调用 |
| G9.2 | N/A(无相关场景) | 同上 |
| G9.3 | N/A(无相关场景) | 同上 |
| G10.1 | ⚠️ P2 | `null` 与异常语义未混用（`ApiResponse.success/error` 区分明确）；但 `ReportServiceImpl.java:74` `apiCode` 为空表示「不过滤」与「无此接口」语义重叠，属可接受约定 |
| G10.2 | ⚠️ P1 | 新增接口无版本/开关，且参数类型错误（`days=abc`、非法 JSON）未走统一响应体（见 REQ-6） |
| G11.1 | ✅ | 7 个测试类 34 例，均含断言（`target/surefire-reports`：tests=34 failures=0 errors=0） |
| G11.2 | ⚠️ P2 | 未覆盖：`Integer.MIN_VALUE` 边界（`SortServiceImpl.java:68`）、并发写队列、`ExportController` 参数类型错误路径 |
| G11.3 | ✅ | `HashRequest`(`@NotBlank/@Size`)、`SortRequest`(`@NotEmpty/@Size/@NotNull`)、`HashServiceImpl.java:28`、`SortServiceImpl.java:56-71`、`ExportServiceImpl.java:242-275` 均有防御 |
| G11.4 | ⚠️ P2 | `SortServiceImpl.java:68` `Math.abs(Integer.MIN_VALUE)` 仍为负值 → 绝对值上限校验被绕过（无资金计算） |
| G12.1 | N/A(无相关场景) | 无资金/库存/积分场景 |
| G12.2 | N/A(无相关场景) | 同上 |
| G13.1 | ✅ | `GlobalExceptionHandler.java:35/48` 业务异常 WARN、`:65` 系统异常 ERROR，级别正确 |
| G14.1 | N/A(无相关场景) | 无金额计算 |
| G14.2 | N/A(无相关场景) | 无多租户表；人员属性来自请求头而非登录态（见 S8.1） |
| G14.3 | ⚠️ P1 | `InvocationTrackServiceImpl.java:46` `LocalDateTime.now()` 存本地时区时间（非 UTC/`Instant`） |
| G14.4 | ⚠️ P1 | 时区未显式指定：`HelloWorldServiceImpl.java:29`、`ReportServiceImpl.java:75`、`ExportServiceImpl.java:92`、`InvocationTrackServiceImpl.java:46`（脚本 M016 同步命中） |
| G15.1 | N/A(无相关场景) | 无库表结构变更 |
| G15.2 | ✅ | 全新增接口，未改动既有接口 |
| G15.3 | ⚠️ P2 | 埋点/导出无功能开关，异常时无法在线关闭（演示工程可接受） |
| G16.1 | N/A(演示工程无监控栈) | 埋点数据本身即调用量指标，未对接 metrics/告警 |
| G16.2 | ⚠️ P1 | `CallerInfoResolver.java:78-81`、`:92-94` 解码失败静默兜底且无日志（脚本命中，已复核成立）；日志均带 `uri` 上下文（`InvocationTrackInterceptor.java:79-80`） |
| G16.3 | ✅ | 业务异常 WARN / 系统异常 ERROR / 正常 INFO |
| G16.4 | ✅ | 无空 catch；无仅 `printStackTrace()`；埋点失败不影响主流程且有日志 |
| G17.1 | N/A(演示工程无监控栈) | 无配置中心/热开关 |
| G17.2 | ⚠️ P2 | 埋点存储为内存有界队列，重启清空、无兜底落盘（`InvocationTrackServiceImpl.java:29`，`impl.md` 已列已知限制） |
| G17.3 | N/A(无相关场景) | 无数据变更脚本 |
| G18.1 | N/A(清单未定义) | `reliability-checklist.md` 仅定义至 G17，G18 无规则文本可核销 |
| G18.2 | N/A(清单未定义) | 同上 |
| G18.3 | N/A(清单未定义) | 同上 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | N/A(无 SQL) | 无持久层 |
| S1.2 | N/A(无 SQL) | 同上（动态表名/字段名以枚举白名单实现于 `ReportDimensionEnum.java`） |
| S1.3 | N/A(无 SQL) | 同上 |
| S2.1 | ⚠️ P1 | 输出侧：JSON 由 Jackson 序列化 ✅；前端 `escapeHtml` 用于动态单元格（`index.html:783`）✅；但 **CSV 注入未防护**——用户可控 `content`/`values` 原样写入 CSV（`CsvBuilder.java:70-81`、`ExportServiceImpl.java:123/139`），以 `=`/`+`/`-`/`@` 开头时 Excel 打开会当公式执行 |
| S2.2 | N/A(无相关场景) | 无富文本 |
| S2.3 | N/A(无相关场景) | 服务端无模板引擎 |
| S3.1 | N/A(无相关场景) | 服务端无外部 URL 请求 |
| S3.2 | N/A(无相关场景) | 无跳转 |
| S3.3 | N/A(无相关场景) | 无出站调用 |
| S4.1 | ✅ | 无命令拼接（未调用 `Runtime`/`ProcessBuilder`） |
| S4.2 | N/A(无相关场景) | 无文件操作 |
| S5.1 | N/A(无相关场景) | 未解析 XML 输入 |
| S5.2 | N/A(无相关场景) | 未使用 XPath |
| S6.1 | N/A(无相关场景) | 无 Java 反序列化 |
| S6.2 | ✅ | Jackson 未开启 `@JsonTypeInfo`/默认类型，无多态反序列化 |
| S6.3 | N/A(无相关场景) | 无敏感字段序列化场景 |
| S7.1 | N/A(无相关场景) | 无文件上传 |
| S7.2 | ✅ | 导出为服务端生成内容，无路径拼接；`pageCode` 经 `DemoApiEnum` 白名单归一（`ExportServiceImpl.java:209-223`） |
| S7.3 | ✅ | 下载文件名由服务端拼接（`ExportServiceImpl.java:91-92`），非用户可控 |
| S8.1 | ⚠️ P1 | 所有 `/api/**` 未接入鉴权，报表/导出可匿名拉取调用统计；调用人身份完全由请求头自证（`CallerInfoResolver.java:35-46`） |
| S8.2 | ✅ | GET 仅查询（`/api/reports`、`/api/exports`），写操作走 POST |
| S8.3 | ⚠️ P2 | 埋点 `recordId` 为自增序列（`InvocationTrackServiceImpl.java:36-38`），可预测；当前无按 ID 取数接口，风险未暴露 |
| S8.4 | N/A(无相关场景) | 未使用 Cookie/会话 |
| S9.1 | ✅ | 无密钥/凭证硬编码（`application.yml` 无凭据） |
| S9.2 | ⚠️ P1 | `GlobalExceptionHandler.java:67` 把 `exception.getMessage()` 作为 `errorMessage` 回传前端，可能泄露内部实现细节；调用人姓名未写日志 ✅ |
| S9.3 | ⚠️ P2 | 演示服务默认 HTTP，未启用传输加密（部署层能力） |
| S9.4 | N/A(无相关场景) | 未使用随机数 |
| S10.1 | N/A(无相关场景) | 无会话/表单写操作 |
| S10.2 | ⚠️ P1 | `WebMvcConfig.java:30-34` `allowedOriginPatterns("*")` + `allowedHeaders("*")`，未做来源白名单（脚本命中） |
| S10.3 | N/A(无相关场景) | 无服务端跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | ⚠️ P2 | 示例项（Controller 入参统一校验注解）：`DemoController.java:69/85` 使用 `@Valid` ✅；`ExportController.java:44-48`、`ReportController.java:39-42` 以 `@RequestParam` 手工校验，未统一 |
| U1.2 | N/A(未启用自定义规则) | `customized-checklist.md` 无此定义 |
| U1.3 | N/A(未启用自定义规则) | 同上 |
| U2.1 | N/A(未启用自定义规则) | `customized-checklist.md` U2 业务红线为空 |
| U2.2 | N/A(未启用自定义规则) | 同上 |
| U2.3 | N/A(未启用自定义规则) | 同上 |

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（允许 `N/A`，但有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（允许 `N/A(未启用自定义规则)`）
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`
