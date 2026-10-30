# demo-server 架构说明

## 模块定位

`demo-server` 是 PakePlus 演示服务（Spring Boot 3.3.4 / JDK 21），为移动端 WebView 页面提供：

- 三类演示接口：hello world、哈希算法、冒泡排序
- 调用埋点：记录调用人、人员类型、人员层级、人员部门、耗时与执行结果
- 调用情况报表：按人员维度聚合，同时产出折线图、饼图、柱状图数据结构
- 结果导出：按页面（Tab）导出展示结果，支持一次性导出全部页面
- 演示前端页面：`src/main/resources/static/index.html`，三个 Tab + 导出按钮 + 报表可视化

## 分层结构

| 层 | 包路径 | 职责 |
|----|--------|------|
| 控制器 | `com.oaikes.pakeplus.demo.controller` | 对外 HTTP 接口，参数校验、结果摘要回填 |
| 服务接口 | `com.oaikes.pakeplus.demo.service` | 业务能力契约 |
| 服务实现 | `com.oaikes.pakeplus.demo.service.impl` | 算法、埋点存储、报表聚合、导出实现 |
| 埋点 | `com.oaikes.pakeplus.demo.tracking` | 拦截器、调用人解析、路径到接口编码映射 |
| 模型 | `com.oaikes.pakeplus.demo.model` | `entity`（DO）/ `dto`（请求）/ `vo`（展示） |
| 通用 | `com.oaikes.pakeplus.demo.common` | 统一响应体、错误码、业务异常、全局异常处理 |
| 基础设施 | `com.oaikes.pakeplus.demo.constant` / `enums` / `util` / `config` | 常量、枚举、CSV 构造、Web 配置 |

## 关键约束

- 请求路径为名词复数、全小写连字符：`/api/hello-world`、`/api/hash`、`/api/sort`、`/api/reports`、`/api/exports`
- 统一响应体字段：`success` / `errorCode` / `errorMessage` / `userTip` / `data`；错误码为 5 位字符串（A 用户端 / B 系统端）
- 埋点数据当前存储于内存队列（上限 10000 条），生产环境应替换为持久化实现（见模块文档「后续演进」）
- 导出文件为 UTF-8 BOM 的 CSV，保证 Excel 正确识别中文
- 前后端时间格式统一 `yyyy-MM-dd HH:mm:ss`

## 跨仓关系

- `PakePlus-Android` / `PakePlus-iOS` 为原生壳工程，通过 WebView 加载本服务页面；两仓当前无独立 Web 前端工程，因此前端页面统一由本服务静态资源提供，避免多份副本不一致。
