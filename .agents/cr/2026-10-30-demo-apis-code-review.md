# Code Review Report

> **Change** `demo-apis`（`demo-server`：三接口 + 三 Tab 前端 + 导出 + 埋点 + 报表）· **分支** `task/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-b42f3756-eac2-4dab-beff-9d82e4bcd9e2` / **Commit** `7771209` · **日期** `2026-10-30` · **审查者** AI
>
> 等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。已先运行 `scan-all-rules.sh` 并将要点并入 §5。
>
> **Checklist（唯一进度源）**：`.agents/cr/2026-10-30-demo-apis-cr-checklist.md`

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | `52`（main 45 + test 7） |
| 变更行数（Java） | `+3121 / -0` |
| 变更行数（含非 Java） | `+4445 / -0` |
| 非 Java 变更 | `pom.xml`、`application.yml`、`static/index.html`（1049 行）、`docs/*`、`.gitignore`、`.agents/2026-10-30-demo-apis/impl.md` |
| 涉及仓库 | `PakePlus-Android`（本次全部改动）；`PakePlus-iOS`（无改动） |

| 类/接口 | 路径（`demo-server/src/main/java/com/oaikes/pakeplus/demo/`） | 角色 |
|---------|------|--------------|
| `DemoController` | `controller/DemoController.java` | REQ-1 三接口入口 |
| `ExportController` | `controller/ExportController.java` | REQ-3 导出入口 |
| `ReportController` | `controller/ReportController.java` | REQ-5 报表入口 |
| `HelloWorldServiceImpl` / `HashServiceImpl` / `SortServiceImpl` | `service/impl/*.java` | REQ-1 业务实现 |
| `InvocationTrackInterceptor` / `CallerInfoResolver` / `TrackingConstants` | `tracking/*.java` | REQ-4 埋点 |
| `InvocationTrackServiceImpl` | `service/impl/InvocationTrackServiceImpl.java` | REQ-4 埋点存储 |
| `ReportServiceImpl` | `service/impl/ReportServiceImpl.java` | REQ-5 聚合与三图 |
| `ExportServiceImpl` / `CsvBuilder` | `service/impl/…` / `util/CsvBuilder.java` | REQ-3 导出与 CSV |
| `ApiResponse` / `GlobalExceptionHandler` / `ErrorCodeEnum` / `BizException` | `common/*.java` | 统一响应与错误码 |
| `WebMvcConfig` | `config/WebMvcConfig.java` | 拦截器注册 + CORS |
| 7 个测试类 | `src/test/java/…` | 单测 |

**核验方式（本次实际执行）**

| 检查 | 命令 / 手段 | 结果 |
|------|-------------|------|
| 自动化预扫 | `scan-all-rules.sh demo-server` | 12 findings（P0=5 / P1=7 / P2=0）；逐条人工复核，2 条 P0 成立降级为 P1，3 条为脚本误报 |
| 单元测试 | `mvn -s /tmp/mvn-settings.xml test`（`demo-server`） | `EXIT=0`；`target/surefire-reports` 7 个用例类 **tests=34 failures=0 errors=0 skipped=0**（13:14 生成） |
| 静态样式 | 脚本化扫描 52 个 `.java` | 行宽 >120 = 0 处；Tab = 0 处；行尾空白 = 0 处 |
| 运行时契约（真实 HTTP） | `java -jar target/demo-server-1.0.0.jar --server.port=18099` + `curl` | 见 §3 / §7.1 各条「实测」证据 |

> **降级说明**：首次 `mvn -o test` 因离线仓库缺少 `maven-surefire-plugin:3.2.5` 失败（环境依赖问题），第二次联网执行成功（`EXIT=0`），故未触发测试降级。验证期间启动的 18099 端口进程已停止，临时文件已清理。

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 1 | 7 | 11 |

**P0（1）**：REQ-3b 导出 hello world 页与展示不一致。

**P1（7）**：REQ-6/G10.2/S9.2 错误路径契约 + 异常外泄；S2.1 CSV 注入；S8.1 无鉴权；S10.2 CORS 任意源；G14.3/G14.4/M016 时区（7 处）；G16.2 解码静默无日志；G8.1 埋点异常无告警。

**P2（11）**：G11.4（`SortServiceImpl.java:68`）、G11.2（测试覆盖缺口）、G15.3（无功能开关）、G17.2（内存队列重启丢失）、G10.1（`apiCode` 双语义）、S8.3（自增可预测 ID）、S9.3（明文 HTTP）、A2（内联全限定名）、A4.3/A4.5（命名规范）、U1.1（入参校验不统一）、常量重复/未使用。

---

## 3. Step 2 — 功能（REQ）

### REQ-1: 三个接口（helloworld / 哈希算法 / 冒泡排序）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| `GET /api/hello-world?name=` 返回问候语与服务端时间 | ✅ | 需求「分别写三个接口helloworld、哈希算法以及冒泡排序」；`impl.md` §接口契约 | `DemoController.java:53-59`；`HelloWorldServiceImpl.java:26-30`；实测 `Hello, Alice!` + `2026-10-30 13:15:06` | 默认/自定义/空白三种输入均有单测 |
| `POST /api/hash {content, algorithm}` 支持 sha-256 / sha-512 / md5 | ✅ | 同上 | `DemoController.java:68-75`；`HashServiceImpl.java:27-70`；`HashAlgorithmEnum.java:13-19` | 非法算法返回 `A0001`；摘要与 JDK 标准值一致（单测 `ba7816bf…`） |
| `POST /api/sort {values}` 返回 `sortedValues/compareCount/swapCount` | ✅ | 同上 | `DemoController.java:84-92`；`SortServiceImpl.java:22-47` | 升序 + 提前退出；空数组 `A0001` |

### REQ-2: 前端三 Tab 页面

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 单页三 Tab 分别展示三类执行结果 | ✅ | 需求「前端新增一个页面，有三个tab分别展示不同的执行结果」 | `static/index.html:383/398/420`（三组输入）、`:676-677`（hello 请求）、`:751`（报表请求） | 前端为 `static/index.html`，由服务端直接提供；PakePlus-iOS 无改动（单副本策略） |

### REQ-3: 导出按钮 + 导出接口

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 导出接口返回 CSV 附件 | ✅ | 需求「新增导出按钮，后台提供导出接口」 | `ExportController.java:43-57`；实测 `Content-Type: text/csv;charset=UTF-8`、`attachment; filename="demo-result-all-…csv"` | 含 UTF-8 BOM，Excel 中文正常 |
| hash / sort / report 三页导出与页面展示一致 | ✅ | 需求「支持导出各个页面的展示结果」 | `ExportServiceImpl.java:117-186`；`index.html:967-976` 传 `content`/`values`/`dimension`/`days` | 三个 Tab 的入参均由前端透传 |
| **hello world 页导出与页面展示一致** | ❌ **P0** | 同上 | `ExportController.java:44-48`（无 `name` 入参）；`index.html:967-969`（仅传 `hashContent`）；`ExportServiceImpl.java:43,104,242-247`（回落 `"Hello PakePlus"`） | **实测**：页面 `?name=Alice` 显示 `Hello, Alice!`；导出 `page=hello-world` 得到 `"Hello, Hello PakePlus!"`。默认示例文本本身还叠加了前缀，产生 `Hello, Hello …` 双重问候 |

### REQ-4: 埋点（调用次数 + 调用人）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 每次业务调用记录调用人与人员属性 | ✅ | 需求「后端再做个埋点，获取调用次数和调用人」；`impl.md` §IMPL | `InvocationTrackInterceptor.java:41-82`；`CallerInfoResolver.java:35-46`（5 个 `X-User-*` 头）；`InvocationTrackServiceImpl.java:32-54` | 请求头缺省回落 `anonymous`/`UNKNOWN`，埋点不丢 |
| 报表/导出接口自身不计入埋点 | ✅ | `impl.md` §过程中修复的两个真实缺陷 2 | `DemoApiEnum.java:19-23`（`tracked=false`）+ `TrackingConstants.java:36-46`；单测 `should_notTrack_when_pathIsReportOrExport` | 双重约束，避免统计自我放大 |
| 失败调用可区分 | ✅ | `impl.md` §接口契约 | `InvocationTrackInterceptor.java:63`；单测 `should_markFailed_when_responseStatusIsErrorStatus` | `exception != null || status >= 400` 记为失败 |

### REQ-5: 报表（三维度 × 三图形）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 按人员类型 / 层级 / 部门聚合 | ✅ | 需求「根据不同的维度：人员类型、人员层级、人员部门等」 | `ReportDimensionEnum.java:13-19`；`ReportServiceImpl.java:112-176` | 非法维度 `A0001`；缺省维度 `userType`；缺省人员属性归入 `UNKNOWN` |
| 折线图 + 饼图 + 柱状图 | ✅ | 需求「折线图以及饼图和柱状图不同展示形式」 | `ReportServiceImpl.java:87-103`、`:187-209`（轴=日期，每维度一条折线）；`index.html:766-768` | `chartType` 明确 `pie`/`bar`，前端 Canvas 自绘 |
| 前端可视化调用情况 | ✅ | 需求「前端在当前页面上可视化出来一个报表查看调用情况」 | `index.html:760-786`（KPI + 三图 + 接口明细表） | KPI 含总次数/调用人数/成功/失败 |

### REQ-6: 统一响应契约

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 成功/业务异常返回 `success/errorCode/errorMessage/userTip/data` | ✅ | `impl.md` §接口契约「统一响应：success / errorCode / errorMessage / userTip / data」 | `ApiResponse.java:39-65`；`GlobalExceptionHandler.java:30-55` | 字段级校验错误拼接为 `field: message` |
| **参数类型错误 / 非法 JSON 走统一响应** | ⚠️ **P1** | 同上 | `GlobalExceptionHandler.java:63-68`（无 `MethodArgumentTypeMismatchException` / `HttpMessageNotReadableException` 分支） | **实测** `GET /api/exports?page=report&days=abc` → `HTTP 500` + `"Failed to convert value of type 'java.lang.String' to required type 'int'; For input string: \"abc\""`；`POST /api/hash` 传 `{bad json` → `HTTP 500` + Jackson 内部报错。应归为 `A0001` + `400` |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ⚠️ | **A1 ✅**：52 个 `.java` 均 UTF-8、无 Tab、无行尾空白（脚本核验全 0）。**A2 ⚠️ P2**：`ExportServiceImplTest.java:114` 内联全限定名 `org.mockito.ArgumentMatchers.any(...)`，未按导入风格引入。**A3 ✅**：4 空格缩进、K&R 大括号、行宽 ≤120（0 处超限）。**A4 ⚠️ P2**：7 个测试类方法名使用下划线式 `should_xxx_when_yyy`（`ReportServiceImplTest.java:50`、`InvocationTrackInterceptorTest.java:43` 等）违反 A4.3；`InvocationTrackServiceImpl.java:37` 局部变量 `recordDO` 带类型后缀（A4.5）。**A5 ✅**：重写方法均带 `@Override`，catch 均有返回或注释。**A6 ✅**：`switch` 枚举全覆盖无 fall-through，无 C 风格数组，无小写 `l`。**A7 ✅**：public 类与成员 Javadoc 完整，`@param/@return` 顺序正确。 |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ❌ | P1 / P2 | **命中**：`G8.1`（`InvocationTrackInterceptor.java:77-81` 埋点异常仅 WARN、无指标告警）、`G10.2`（新增接口无版本/开关，错误路径未统一，见 REQ-6）、`G14.3`+`G14.4`（时区未显式指定，5 处主代码）、`G16.2`（`CallerInfoResolver.java:78-81/:92-94` 静默兜底无日志）、`G11.4`（`SortServiceImpl.java:68`）、`G11.2`、`G15.3`、`G17.2`、`G10.1`。**已扫无命中**：G1/G2/G3/G5/G6/G7/G9/G12/G13/G16.4/G18（`G4` 无 SQL 结构性不适用；`G18` 清单仅定义至 G17） |
| 安全 | `security-checklist.md` S1–S10 | ❌ | P0–P1 | **命中**：`S2.1`（CSV 公式注入，实测成立）、`S8.1`（`/api/**` 无鉴权、调用人自证）、`S10.2`（CORS 回显任意 Origin，实测成立）、`S9.2`（`GlobalExceptionHandler.java:67` 内部异常信息外泄，实测成立）、`S8.3`、`S9.3`。**已扫无命中**：S4/S6.2/S7/S10.3（`S1`/`S3`/`S5` 无对应场景） |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120 条） | ❌ | P1 | **预扫命中并成立**：`M016` ×6（`ExportServiceImpl.java:92`、`HelloWorldServiceImpl.java:29`、`InvocationTrackServiceImpl.java:46`、`ReportServiceImpl.java:75`、`ReportServiceImplTest.java:69/166`）。**预扫 P0 复核后不成立**：`G16.2` ×3 为脚本误报（catch 内有重抛或 WARN 日志）。**其余 113 条 LLM 逐条核销**：B002/B004/B008/B011/B014/B022/B025/B031/B046/B049/B050/B052/B053/B055/B056/B057/B060/B071/B074/B075/B080 已扫无命中；B016/B019/B021/B027/B028/B032/B033/B034/B036/B041-B044/B054/B062-B064/B069/B070/B076 无对应场景 |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「未启用自定义规则」） |
|----|------|------|------|------------------------------------------|
| 自定义扩展 | `customized-checklist.md` U* | ⚠️ | P2 | `U1.1`（示例项：Controller 入参统一校验注解）部分符合——`DemoController.java:69/85` 用 `@Valid` ✅，`ExportController.java:44-48`、`ReportController.java:39-42` 仍手工校验；`U1.2/U1.3/U2.1-U2.3` 为 `N/A(未启用自定义规则)`（`customized-checklist.md` 未定义） |

---

## 7. 结论

- **合并建议**：**修复后合并**（P0 必须修复；P1 建议合并前处理）
- **P0**：
  1. **REQ-3b 功能性不符** — 导出「hello world 页」不复现页面展示结果，缺 `name` 入参且默认文本双重问候。实测证据见 §7.1-1。
- **P1 / P2**：
  1. P1 错误路径契约破损 + 内部异常信息外泄（`days=abc`、非法 JSON → 500）。
  2. P1 CSV 公式注入（Excel 打开即执行）。
  3. P1 全接口无鉴权、调用人由请求头自证。
  4. P1 CORS 回显任意 Origin。
  5. P1 7 处时间取值/格式化未显式指定时区（`M016`）。
  6. P1 请求头解码失败静默无日志；埋点异常仅 WARN 无告警。
  7. P2 11 项（`Integer.MIN_VALUE` 边界、测试覆盖缺口、无功能开关、内存队列重启丢失、`apiCode` 双语义、自增 ID、明文传输、导入风格、命名规范、入参校验不统一、常量重复/未使用）。
- **一句话**：主链路（三接口 / 三 Tab / 埋点 / 三图）功能完整、34 例单测全绿，但**导出与页面展示在 hello world 页不一致（P0）**，且错误路径契约、CSV 注入、鉴权与 CORS 存在明确的可用性/安全隐患。

---

## 7.1 问题片段（必填）

### 1. P0 `REQ-3b` — 导出 hello world 页与页面展示不一致

`demo-server/src/main/java/com/oaikes/pakeplus/demo/controller/ExportController.java:44-48`（导出链路无 `name` 入参）
`demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/ExportServiceImpl.java:103-109`（回落硬编码示例文本）

```java
// ExportController.java:44-48 —— 缺 name 参数
L44|    public ResponseEntity<byte[]> exportResults(@RequestParam(defaultValue = "all") String page,
L45|                                                @RequestParam(required = false) String content,
L46|                                                @RequestParam(required = false) String values,
L47|                                                @RequestParam(required = false) String dimension,
L48|                                                @RequestParam(defaultValue = "7") int days) {
```

```java
// ExportServiceImpl.java:103-109 —— hello 段用 resolveContent（默认 "Hello PakePlus"）而非页面 name
L103|    private void appendHelloSection(CsvBuilder csvBuilder, ExportQuery query) {
L104|        HelloWorldVO helloWorldVO = helloWorldService.getGreeting(resolveContent(query));
L105|        csvBuilder.appendRow("hello world 接口结果");
L106|        csvBuilder.appendRow("消息", "服务端时间");
L107|        csvBuilder.appendRow(helloWorldVO.message(), helloWorldVO.serverTime());
L108|        csvBuilder.appendEmptyRow();
L109|    }
```

```javascript
// index.html:965-976 —— 导出未拼 helloName
L965|        function exportResults(page) {
L966|            var params = ['page=' + encodeURIComponent(page)];
L967|            if (page === 'hash' || page === 'all') {
L968|                params.push('content=' + encodeURIComponent(byId('hashContent').value));
L969|            }
```

**实测证据（端口 18099，真实 HTTP）**

```text
GET /api/hello-world?name=Alice
  → {"success":true,...,"data":{"message":"Hello, Alice!","serverTime":"2026-10-30 13:15:06"}}

GET /api/exports?page=hello-world&days=7        （前端实际调用形态，不带 content）
  → ﻿hello world 接口结果
     消息,服务端时间
     "Hello, Hello PakePlus!",2026-10-30 13:15:06
```

页面展示 `Hello, Alice!`，导出为 `Hello, Hello PakePlus!` —— 既是「内容不一致」，也暴露 `DEFAULT_CONTENT = "Hello PakePlus"`（`ExportServiceImpl.java:43`）与问候模板 `"Hello, " + name + "!"` 叠加的双重前缀。

---

### 2. P1 `G10.2` + `REQ-6` + `S9.2` — 错误路径契约破损且回传内部异常信息

`demo-server/src/main/java/com/oaikes/pakeplus/demo/common/GlobalExceptionHandler.java:63-68`

```java
L63|    @ExceptionHandler(Exception.class)
L64|    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
L65|        LOGGER.error("系统异常, errorMessage: {}", exception.getMessage(), exception);
L66|        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
L67|                .body(ApiResponse.error(ErrorCodeEnum.SYSTEM_ERROR, exception.getMessage()));
L68|    }
```

**实测证据**

```text
GET /api/exports?page=report&days=abc   → HTTP 500
  {"success":false,"errorCode":"B0001","errorMessage":"Failed to convert value of type
   'java.lang.String' to required type 'int'; For input string: \"abc\"", ...}

POST /api/hash  body={bad json        → HTTP 500
  {"success":false,"errorCode":"B0001","errorMessage":"JSON parse error: Unexpected character
   ('b' (code 98)): was expecting double-quote to start field name", ...}
```

用户输入错误被归为 `B0001`（系统端错误），且 `errorMessage` 直接回传框架内部细节。修复方向：新增 `MethodArgumentTypeMismatchException` / `HttpMessageNotReadableException` 分支 → `A0001` + `400`，且 `errorMessage` 只输出面向排查的安全摘要。

---

### 3. P1 `S2.1` — CSV 公式注入未防护

`demo-server/src/main/java/com/oaikes/pakeplus/demo/util/CsvBuilder.java:70-81`（仅处理分隔符/引号/换行，未处理 `=` `+` `-` `@` 前缀）

```java
L70|    private String escape(Object cell) {
L71|        if (cell == null) {
L72|            return "";
L73|        }
L74|        String text = String.valueOf(cell);
L75|        for (int index = 0; index < NEED_ESCAPE_CHARS.length(); index++) {
L76|            if (text.indexOf(NEED_ESCAPE_CHARS.charAt(index)) >= 0) {
L77|                return '"' + text.replace("\"", "\"\"") + '"';
L78|            }
L79|        }
L80|        return text;
L81|    }
```

**实测证据**

```text
GET /api/exports?page=hash&content=%3D1%2B1
  → sha-256,=1+1,bac90eef5bf3aecd6123c6c185642260ca6b2ae97f32fac7e72871cee02cd212,64
```

用户可控 `content`（`ExportServiceImpl.java:123`）与 `values` 原样落 CSV，`=1+1` 在 Excel 中会作为公式求值。修复方向：以 `=`/`+`/`-`/`@`/`\t`/`\r` 开头的单元格前置单引号或前置空格。

---

### 4. P1 `S8.1` — 全部 `/api/**` 无鉴权，调用人由请求头自证

`demo-server/src/main/java/com/oaikes/pakeplus/demo/tracking/CallerInfoResolver.java:35-46`

```java
L35|    public static CallerInfo resolve(HttpServletRequest request) {
L36|        String callerId = getHeaderOrDefault(request, DemoConstants.HEADER_USER_ID,
L37|                DemoConstants.ANONYMOUS_USER_ID);
L38|        String callerName = getHeaderOrDefault(request, DemoConstants.HEADER_USER_NAME, callerId);
L39|        String userType = getHeaderOrDefault(request, DemoConstants.HEADER_USER_TYPE,
L40|                DemoConstants.DEFAULT_USER_TYPE);
L41|        String userLevel = getHeaderOrDefault(request, DemoConstants.HEADER_USER_LEVEL,
L42|                DemoConstants.DEFAULT_USER_LEVEL);
L43|        String userDept = getHeaderOrDefault(request, DemoConstants.HEADER_USER_DEPT,
L44|                DemoConstants.DEFAULT_USER_DEPT);
L45|        return new CallerInfo(callerId, callerName, userType, userLevel, userDept);
L46|    }
```

`WebMvcConfig.java:24-26` 仅注册拦截器、未做认证。任一调用方可任意伪造 `X-User-Dept: 财务部` 影响报表口径，并匿名拉取全部调用统计（`/api/reports`、`/api/exports`）。`impl.md` §已知限制 5 已披露，但对外演示环境仍属隐患。

---

### 5. P1 `S10.2` — CORS 回显任意 Origin

`demo-server/src/main/java/com/oaikes/pakeplus/demo/config/WebMvcConfig.java:29-35`

```java
L29|    public void addCorsMappings(CorsRegistry registry) {
L30|        registry.addMapping("/api/**")
L31|                .allowedOriginPatterns("*")
L32|                .allowedMethods("GET", "POST", "OPTIONS")
L33|                .allowedHeaders("*")
L34|                .maxAge(3600L);
L35|    }
```

**实测证据**

```text
OPTIONS /api/hash  Origin: https://evil.example
  → HTTP/1.1 200
    Access-Control-Allow-Origin: https://evil.example
    Access-Control-Allow-Methods: GET,POST,OPTIONS
```

任意站点可跨域读取报表与导出结果。修复方向：`allowedOriginPatterns` 改为显式白名单（配置项 + `@Value`/`@ConfigurationProperties`）。

---

### 6. P1 `G14.3` + `G14.4` + `M016` — 时间取值/格式化未显式指定时区（7 处）

`demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/HelloWorldServiceImpl.java:25-30`

```java
L25|    @Override
L26|    public HelloWorldVO getGreeting(String name) {
L27|        String effectiveName = (name == null || name.isBlank()) ? DEFAULT_NAME : name.trim();
L28|        String message = "Hello, " + effectiveName + "!";
L29|        return new HelloWorldVO(message, LocalDateTime.now().format(DATE_TIME_FORMATTER));
L30|    }
```

同类命中：`InvocationTrackServiceImpl.java:46`（埋点落库时间存本地时区，非 UTC/`Instant`）、`ReportServiceImpl.java:75`（统计窗口 `LocalDate.now()`）、`ExportServiceImpl.java:92`（文件名时间戳）、`ReportServiceImplTest.java:69/166`。若服务部署时区与调用方不一致，报表日期边界与导出文件名会漂移。

---

### 7. P1 `G16.2` — 请求头解码失败静默兜底，无任何日志

`demo-server/src/main/java/com/oaikes/pakeplus/demo/tracking/CallerInfoResolver.java:75-95`

```java
L75|        if (rawValue.indexOf('%') >= 0) {
L76|            try {
L77|                return URLDecoder.decode(rawValue, StandardCharsets.UTF_8);
L78|            } catch (IllegalArgumentException decodeException) {
L79|                // 非法百分号编码按原文返回，避免影响埋点主流程
L80|                return rawValue;
L81|            }
L82|        }
...
L92|        } catch (CharacterCodingException codingException) {
L93|            return rawValue;
L94|        }
```

`L78` 分支有注释（可接受），`L92-94` 无注释也无日志。两条分支都静默降级：上游字符集变更导致中文乱码时，报表里只会看到一堆 `æ...`，无任何可观测信号。建议 `LOGGER.debug/warn` 带 `headerName` 与异常。

---

### 8. P1 `G8.1` — 埋点异常仅 WARN，无指标/告警

`demo-server/src/main/java/com/oaikes/pakeplus/demo/tracking/InvocationTrackInterceptor.java:60-82`

```java
L77|        } catch (RuntimeException trackException) {
L78|            // 埋点失败不能影响主流程，仅记录告警日志
L79|            LOGGER.warn("调用埋点记录失败, uri: {}, errorMessage: {}",
L80|                    request.getRequestURI(), trackException.getMessage(), trackException);
L81|        }
L82|    }
```

「不影响主流程」是正确取舍，但埋点静默丢失没有任何可聚合指标，报表数字失真时无法自证。建议加计数器（埋点失败数）或至少 `error` 级别 + 采样。

---

### 9. P2 `G11.4` — `Math.abs(Integer.MIN_VALUE)` 绕过绝对值上限

`demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/SortServiceImpl.java:64-73`

```java
L64|        for (Integer value : values) {
L65|            if (value == null) {
L66|                throw new BizException(ErrorCodeEnum.PARAM_INVALID, "数组元素不能为 null");
L67|            }
L68|            if (Math.abs(value) > DemoConstants.MAX_SORT_VALUE) {
L69|                throw new BizException(ErrorCodeEnum.PARAM_INVALID,
L70|                        "数组元素绝对值不能超过 " + DemoConstants.MAX_SORT_VALUE);
L71|            }
L72|            copy.add(value);
L73|        }
```

`Math.abs(Integer.MIN_VALUE) == Integer.MIN_VALUE`（仍为负），校验被绕过；本场景无资损风险，故定 P2。

---

### 10. P2 `A2` — 测试内联全限定名

`demo-server/src/test/java/com/oaikes/pakeplus/demo/service/impl/ExportServiceImplTest.java:112-116`

```java
L112|        ChartVO barChart = new ChartVO("按人员部门调用次数", "bar", List.of(dimensionPoint));
L113|        LineChartVO lineChart = new LineChartVO(List.of("2026-10-30"), List.of(new LineSeriesVO("技术部", List.of(2L))));
L114|        when(reportService.getReport(org.mockito.ArgumentMatchers.any(ReportQuery.class)))
L115|                .thenReturn(new ReportVO(2L, 1L, 2L, 0L, "userDept", "人员部门",
L116|                        List.of(apiPoint), pieChart, barChart, lineChart));
```

`L114` 已 import `ReportQuery`，但 `ArgumentMatchers.any` 用全限定名；同文件其他位置（`ReportServiceImplTest.java:27`）采用静态导入，风格不统一。

---

### 11. P2 `A4.3` / `A4.5` — 命名规范

`demo-server/src/test/java/com/oaikes/pakeplus/demo/service/impl/ReportServiceImplTest.java:48-53`

```java
L48|    @Test
L49|    @DisplayName("按人员类型维度聚合调用次数并产出三类图表数据")
L50|    void should_aggregateByUserType_when_dimensionAbsent() {
L51|        when(invocationTrackService.listRecords(any(), any())).thenReturn(buildRecords());
L52|
L53|        ReportVO reportVO = reportService.getReport(new ReportQuery(null, 7, null));
```

`should_xxx_when_yyy` 下划线式在 7 个测试类中共 34 处（`InvocationTrackInterceptorTest.java:43/73/90/105/117` 等）；`InvocationTrackServiceImpl.java:37` `recordDO` 带类型后缀。

---

### 12. P2 `U1.1` — 导出/报表入参未统一校验注解

`demo-server/src/main/java/com/oaikes/pakeplus/demo/controller/ExportController.java:43-50`

```java
L43|    @GetMapping
L44|    public ResponseEntity<byte[]> exportResults(@RequestParam(defaultValue = "all") String page,
L45|                                                @RequestParam(required = false) String content,
L46|                                                @RequestParam(required = false) String values,
L47|                                                @RequestParam(required = false) String dimension,
L48|                                                @RequestParam(defaultValue = "7") int days) {
L49|        ExportFileVO exportFileVO = exportService.exportResults(
L50|                new ExportQuery(page, content, values, dimension, days));
```

对照 `DemoController.java:69/85` 的 `@Valid` 风格，导出与报表（`ReportController.java:39-42`）仍为手工/隐式校验，且 `days` 无范围约束注解（仅 Service 内收敛 `ReportServiceImpl.java:130-135`，导出侧 `appendReportSection` 未收敛）。

---

### 13. P2 常量重复与未使用

`demo-server/src/main/java/com/oaikes/pakeplus/demo/model/dto/SortRequest.java:15-18` 与 `constant/DemoConstants.java:53`

```java
L15|public record SortRequest(
L16|        @NotEmpty(message = "待排序数组不能为空")
L17|        @Size(max = 1000, message = "待排序数组元素个数不能超过 1000")
L18|        List<@NotNull(message = "数组元素不能为 null") Integer> values) {
```

`1000` 与 `DemoConstants.MAX_SORT_SIZE`（`DemoConstants.java:47`）重复，注解属性要求编译期常量本可引用该常量；`DemoConstants.EXPORT_CHARSET`（`DemoConstants.java:53`）定义后未被使用（`ExportServiceImpl.java:94` 直接用 `StandardCharsets.UTF_8`）。

---

### 14. P2 其余项汇总片段（G11.2 / G15.3 / G17.2 / G10.1 / S8.3 / S9.3）

`demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/InvocationTrackServiceImpl.java:28-54`（G17.2 重启丢数 + G15.3 无开关）

```java
L28|    /** 调用记录存储，按时间先后追加，队首为最早记录。 */
L29|    private final Deque<InvocationRecordDO> records = new ConcurrentLinkedDeque<>();
...
L50|        records.addLast(recordDO);
L51|        while (records.size() > DemoConstants.MAX_RECORD_SIZE) {
L52|            records.pollFirst();
L53|        }
L54|    }
```

`L29` 纯内存 + `L51-53` 有界淘汰：进程重启即全部丢失、超 10000 条静默丢最早记录，且无配置开关可在异常时关闭埋点（`impl.md` §已知限制 1 已披露）。

`demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/ReportServiceImpl.java:74-77`（G10.1 `apiCode` 空值双语义）

```java
L74|        String apiCode = query == null ? null : query.apiCode();
L75|        LocalDate endDate = LocalDate.now();
L76|        LocalDate startDate = endDate.minusDays(days - 1L);
L77|        List<InvocationRecordDO> records = invocationTrackService.listRecords(apiCode, startDate);
```

`apiCode == null` 表示「不过滤」，而当调用方请求一个不存在的接口编码时返回 0 条——两者在响应体中不可区分。

`demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/InvocationTrackServiceImpl.java:36-38`（S8.3 可预测 ID）

```java
L36|        long sequence = RECORD_SEQUENCE.incrementAndGet();
L37|        InvocationRecordDO recordDO = new InvocationRecordDO(
L38|                String.valueOf(sequence),
```

自增序列可预测；当前无按 ID 取数接口，风险未暴露，故 P2。

- **P2 G11.2**（测试覆盖缺口）：`N/A(非 Java 缺陷)` — 缺口表现为「缺少用例」，涉及 `SortServiceImplTest.java:65`（未覆盖 `Integer.MIN_VALUE`）、`ExportServiceImplTest.java`（未覆盖参数类型错误路径）、`InvocationTrackServiceImpl`（无并发写用例），无对应缺陷片段。
- **P2 S9.3**（明文传输）：`N/A(非 Java)` — 位于 `demo-server/src/main/resources/application.yml:1-2`（仅 `server.port`），未配置 TLS/反向代理，属部署层能力。

---

## 8. 修复任务列表

### P0

- [ ] **P0** `demo-server/src/main/java/com/oaikes/pakeplus/demo/controller/ExportController.java:44` — 新增 `@RequestParam(required = false) String name` 并在 `ExportQuery` 增加 `name` 字段后透传给 `ExportServiceImpl.appendHelloSection`
- [ ] **P0** `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/ExportServiceImpl.java:43` — 拆分「哈希示例文本」与「问候称呼」两个默认值，消除 `Hello, Hello PakePlus!` 双重前缀
- [ ] **P0** `demo-server/src/main/resources/static/index.html:967` — 导出 `hello-world`/`all` 时追加 `name=` + `helloName` 值，使导出内容与 Tab 展示一致

### P1

- [ ] **P1** `demo-server/src/main/java/com/oaikes/pakeplus/demo/common/GlobalExceptionHandler.java:63` — 增加 `MethodArgumentTypeMismatchException`、`HttpMessageNotReadableException` 分支，归为 `A0001` + `400`
- [ ] **P1** `demo-server/src/main/java/com/oaikes/pakeplus/demo/common/GlobalExceptionHandler.java:67` — `errorMessage` 改为固定安全摘要，不再回传 `exception.getMessage()`
- [ ] **P1** `demo-server/src/main/java/com/oaikes/pakeplus/demo/util/CsvBuilder.java:70` — 对以 `=` `+` `-` `@` `\t` `\r` 开头的单元格做防公式注入转义
- [ ] **P1** `demo-server/src/main/java/com/oaikes/pakeplus/demo/config/WebMvcConfig.java:31` — `allowedOriginPatterns` 改为配置化来源白名单
- [ ] **P1** `demo-server/src/main/java/com/oaikes/pakeplus/demo/tracking/CallerInfoResolver.java:35` — 调用人改由鉴权上下文注入（或对 `X-User-*` 加签名/白名单校验），禁用客户端自证
- [ ] **P1** `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/HelloWorldServiceImpl.java:29` — `LocalDateTime.now(...)` 显式指定 `ZoneId`
- [ ] **P1** `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/InvocationTrackServiceImpl.java:46` — 埋点时间改用 `Instant`/UTC 存库
- [ ] **P1** `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/ReportServiceImpl.java:75` — 统计窗口显式指定时区
- [ ] **P1** `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/ExportServiceImpl.java:92` — 导出文件名时间戳显式指定时区
- [ ] **P1** `demo-server/src/test/java/com/oaikes/pakeplus/demo/service/impl/ReportServiceImplTest.java:69` — 断言改用显式时区的 `LocalDate.now(zone)`，与实现对齐
- [ ] **P1** `demo-server/src/main/java/com/oaikes/pakeplus/demo/tracking/CallerInfoResolver.java:92` — 解码失败补 `LOGGER.warn`（含 `headerName`）
- [ ] **P1** `demo-server/src/main/java/com/oaikes/pakeplus/demo/tracking/InvocationTrackInterceptor.java:77` — 埋点异常增加失败计数指标或提升为 `error` 级别

### P2（可选）

- [ ] **P2** `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/SortServiceImpl.java:68` — 改用 `value < -MAX || value > MAX` 或 `Math.abs((long) value)` 规避 `Integer.MIN_VALUE`
- [ ] **P2** `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/ExportServiceImpl.java:150` — 导出报表的 `days` 复用 `ReportServiceImpl` 的 1~90 收敛逻辑
- [ ] **P2** `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/ReportServiceImpl.java:74` — `apiCode` 区分「未指定」与「指定但无数据」，避免 G10.1 双语义
- [ ] **P2** `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/InvocationTrackServiceImpl.java:36` — `recordId` 改为 UUID，去掉可预测自增
- [ ] **P2** `demo-server/src/main/java/com/oaikes/pakeplus/demo/constant/DemoConstants.java:53` — 删除未使用的 `EXPORT_CHARSET`，或让 `ExportServiceImpl.java:94` 引用它
- [ ] **P2** `demo-server/src/main/java/com/oaikes/pakeplus/demo/model/dto/SortRequest.java:17` — `@Size(max = DemoConstants.MAX_SORT_SIZE)` 复用常量
- [ ] **P2** `demo-server/src/main/java/com/oaikes/pakeplus/demo/controller/ExportController.java:44` — 为 `page`/`days`/`dimension` 增加校验注解，统一 `@Valid` 风格（U1.1）
- [ ] **P2** `demo-server/src/test/java/com/oaikes/pakeplus/demo/service/impl/ExportServiceImplTest.java:114` — 改静态导入 `ArgumentMatchers.any`（A2）
- [ ] **P2** `demo-server/src/test/java/com/oaikes/pakeplus/demo/service/impl/ReportServiceImplTest.java:50` — 34 处测试方法名改为 lowerCamelCase（A4.3）
- [ ] **P2** `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/InvocationTrackServiceImpl.java:37` — `recordDO` 改名为 `record`，去掉类型后缀（A4.5）
- [ ] **P2** `demo-server/src/test/java/com/oaikes/pakeplus/demo/service/impl/SortServiceImplTest.java:65` — 补 `Integer.MIN_VALUE`、参数类型错误、并发写队列用例（G11.2）
- [ ] **P2** `demo-server/src/main/java/com/oaikes/pakeplus/demo/tracking/InvocationTrackInterceptor.java:41` — 埋点与导出引入配置开关（`demo.tracking.enabled` / `demo.export.enabled`），支持在线关闭（G15.3 / G17.2）
- [ ] **P2** `demo-server/src/main/java/com/oaikes/pakeplus/demo/service/impl/InvocationTrackServiceImpl.java:29` — 内存队列增加兜底落盘或启动预热说明，明确重启丢数影响面（G17.2）
