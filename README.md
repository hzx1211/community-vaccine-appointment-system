# 社区疫苗预约管理系统

面向社区接种场景的全栈毕业设计项目，包含疫苗展示、社区选择、预约审核、模拟余额支付、资讯公告、后台管理和统计功能。

## 技术栈

- 前端：Vue 3、Vite、Pinia、Element Plus、ECharts
- 后端：Spring Boot 3、MyBatis、MySQL、Redis、JWT、Knife4j
- 测试：JUnit 5、Mockito、Vitest、Postman、JMeter

## 项目技术亮点

这是一个围绕“预约、库存、时段名额和账户余额”设计的全栈业务项目。后端使用 Spring Boot 分层组织接口、业务服务和数据访问；MySQL 保存业务事实，Redis 用于分布式锁、热点数据缓存、登录 Token 状态和请求计数。

- **并发预约**：使用 Redis 锁协调同一用户的重复提交和同一疫苗的并发操作，并通过 MySQL 条件更新限制库存和时段容量不能扣成负数或超过上限。
- **乐观并发控制**：余额更新会带上读取到的旧余额作为更新条件；只有数据库中的余额仍与旧值相符时才写入新余额，并通过受影响行数识别冲突。
- **事务与数据一致性**：预约创建、余额变更、预约状态和记录写入通过 Spring 事务组织；Redis 锁和 MySQL 事务承担不同职责，详细时序及边界见技术说明。
- **Redis 的多种用法**：项目把 Redis 用于锁、缓存、登录状态和限流计数，并通过不同的 key 前缀区分业务用途。

实现细节、关键源码入口、并发流程和当前改进方向见[技术架构与并发控制说明](docs/TECHNICAL_OVERVIEW.md)。

## 效果图

| 用户端首页 | 疫苗列表 |
| --- | --- |
| ![用户端首页](screenshots/home-desktop.png) | ![疫苗列表](screenshots/vaccine-list.png) |

<img src="screenshots/home-mobile.png" alt="用户端首页移动端" width="260" />

完整截图说明见 [`screenshots/README.md`](screenshots/README.md)。

## 本地启动

### 1. 准备依赖

1. 启动 MySQL，并导入 `sql/vaccine_system2.sql`。
2. 启动 Redis（默认端口 `6379`）。
3. 确保 Java 17 和 Node.js 18+ 可用。

### 2. 启动后端

从本目录（`vaccine`）运行，保证上传图片目录稳定解析为 `vaccine/uploads`：

```powershell
java -jar backend/target/vaccine-system-1.0.0.jar
```

后端默认地址为 `http://127.0.0.1:8081`，接口文档为 `http://127.0.0.1:8081/doc.html`。

### 3. 启动前端

```powershell
cd frontend
npm install
npm run dev
```

前端默认地址为 `http://127.0.0.1:5173`。

## 可配置环境变量

不要把生产环境账号、密码或 JWT 密钥写进代码。可通过以下环境变量覆盖本地默认值：

| 变量 | 用途 |
| --- | --- |
| `DB_USERNAME` / `DB_PASSWORD` | MySQL 登录信息 |
| `JWT_SECRET` | JWT 签名密钥 |
| `VACCINE_UPLOAD_PATH` | 上传目录；生产环境建议使用绝对路径 |
| `CORS_ALLOWED_ORIGIN_PATTERNS` | 允许跨域的前端地址，逗号分隔 |

## 测试

```powershell
# 后端：在 backend 目录执行
mvn test

# 前端：在 frontend 目录执行
npm test
npm run build
```

Postman 集合位于 `test-tools/postman`，JMeter 计划位于 `test-tools/jmeter`。两者均使用后端默认端口 `8081`。

## 角色与边界

- `admin`：维护疫苗、社区、用户、资讯、公告、轮播图、缓存和全局统计。
- `community_admin`：审核和确认自己所属社区的预约，只能查看本社区概览。
- `user`：创建、支付、取消自己的预约，管理自己的收藏和充值记录。

支付与通知目前是**本地模拟实现**：支付操作改变站内余额，通知内容写入本地文件；接入真实支付或短信/邮件服务前，请补充回调验签、幂等处理和密钥管理。
