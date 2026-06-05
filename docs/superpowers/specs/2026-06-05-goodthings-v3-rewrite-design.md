# 须皓的好东西收藏网 v3 — 重写设计

**作者**：须皓（与 Claude 协作）
**日期**：2026-06-05
**目标仓库**：
- 后端 `git@github.com:agentzhuang/goodthings.git` 分支 `rewrite/v3`
- 前端 `git@github.com:agentzhuang/goodthings-web.git` 分支 `rewrite/v3`

---

## 1. 目标与范围

### 一句话目标
在 `goodthings` 和 `goodthings-web` 仓库的 `rewrite/v3` 分支上，分别重写一个"个人好东西收藏网"，阶段 1 跑通"项目骨架 + 用户鉴权"。

### 阶段 1（本轮要写的）
- 后端 Spring Boot 3 工程骨架
- 前端 Vue 3 + TS + Vite 工程骨架
- 最小数据库初始化脚本（仅 `sys_user` 表）
- 鉴权 API：注册 / 登录 / 获取当前用户 / 登出
- JWT 鉴权过滤器
- 前端登录/注册页 + 路由守卫
- 暗色沉浸式 UI token（不依赖具体功能，只先把调色板/字体/间距立起来）

### 阶段 1 不做（后续阶段）
- 收藏品 / 标签 / 收藏夹 / 评论 / 点赞
- 单元/集成/E2E 测试
- 管理后台
- CI/CD 接入（仅写部署文档）
- HTTPS 证书、Nginx 实际配置（最后阶段一并出）

### 阶段化路线（远景）
| 阶段 | 内容 |
|---|---|
| 1（当前）| 骨架 + 鉴权 + 暗色 token |
| 2 | 收藏品 + 文件上传（Nginx 静态） + 瀑布流展示 |
| 3 | 标签 + 收藏夹 |
| 4 | 评论（含回复）+ 点赞 |
| 5 | 部署：Nginx、HTTPS、systemd 守护、部署文档 |

---

## 2. 后端架构

### 2.1 技术栈
| 组件 | 版本 |
|---|---|
| Spring Boot | 3.2.x |
| Java | 17+ |
| MyBatis-Plus | 3.5.7+ |
| MySQL | 8.0 |
| 连接池 | 阿里 Druid |
| JJWT | 0.12.x |
| Lombok | 默认 |
| Validation | spring-boot-starter-validation |
| Spring Security Crypto | 用于 BCrypt |

### 2.2 包结构
```
com.goodthings.v3
├── GoodthingsApplication.java
├── config/                  # CorsConfig, MybatisPlusConfig, JwtProperties, MyMetaObjectHandler
├── common/
│   ├── Result.java
│   ├── BizException.java
│   ├── GlobalExceptionHandler.java
│   ├── ErrorCode.java
│   └── PageResult.java
├── controller/              # AuthController, UserController
├── dto/                     # LoginDTO, RegisterDTO
├── vo/                      # UserVO, LoginVO
├── entity/                  # SysUser
├── mapper/                  # SysUserMapper
├── security/                # JwtAuthFilter, JwtUtil, PasswordEncoderConfig
├── service/
│   ├── AuthService
│   ├── UserService
│   └── impl/
└── util/
```

### 2.3 关键约定
- 控制器不写业务逻辑，所有逻辑进 `service`
- DTO / VO / Entity 严格分离；Controller 永远不返回 Entity
- 错误用 `BizException(ErrorCode.X, msg)`，由 `GlobalExceptionHandler` 转 `Result`
- BCrypt 强度 12
- JWT：HS256 + 配置化密钥，payload 放 `userId` + `username`
- 时间统一存 MySQL `DATETIME(3)`，Java 端用 `LocalDateTime`
- 主键 `BIGINT UNSIGNED AUTO_INCREMENT`
- 所有表带 `create_time` / `update_time` / `is_deleted`（逻辑删除，MyBatis-Plus `@TableLogic`）

### 2.4 配置
- `application.yml` 三套：`application-dev.yml` / `application-prod.yml`
- 密钥/连接串全部走环境变量 `${...}`，仓库里不存真实值
- `.env.example` 列出所有变量名
- `JwtProperties` 通过 `@ConfigurationProperties` 绑定 `goodthings.jwt.*`

---

## 3. 前端架构

### 3.1 技术栈
| 组件 | 版本 |
|---|---|
| Vue | 3.5+ |
| Vite | 5.x |
| TypeScript | 5.x（strict） |
| 路由 | Vue Router 4（懒加载） |
| 状态 | Pinia |
| HTTP | Axios（封装 `request`） |
| 工具 | `@vueuse/core`、`dayjs`、`nprogress` |
| UI 库 | **不引入** Element Plus（自写极小组件） |

### 3.2 目录结构
```
goodthings-web/
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
├── .env.development      # VITE_API_BASE_URL=http://localhost:8080/api/v3
├── .env.production
└── src/
    ├── main.ts
    ├── App.vue
    ├── style/
    │   ├── tokens.css
    │   ├── reset.css
    │   └── global.css
    ├── api/
    │   ├── http.ts
    │   ├── types.ts
    │   ├── auth.ts
    │   └── user.ts
    ├── stores/
    │   └── auth.ts
    ├── router/
    │   ├── index.ts
    │   └── guards.ts
    ├── views/
    │   ├── Discover.vue
    │   ├── Login.vue
    │   ├── Register.vue
    │   └── About.vue
    ├── components/
    │   ├── AppHeader.vue
    │   └── AppFooter.vue
    └── lib/
        └── nprogress.ts
```

### 3.3 关键约定
- `request` 拦截器：401 → 清 token + 跳 `/login`
- `ApiResponse<T>` 在 `api/types.ts` 定义，所有 API 方法返回 `Promise<T>`
- 路由守卫集中到 `router/guards.ts`，从 Pinia 读 token
- token 存 `localStorage`；PII 不进 Pinia
- 暗色调色板（oklch）：
  - 背景 `oklch(14% 0.012 260)`
  - 前景 `oklch(96% 0.01 260)`
  - 强调 `oklch(72% 0.18 35)`（暖橙）
  - 卡片 `oklch(20% 0.015 260)`
  - 边框 `oklch(28% 0.018 260)`
- 字体：标题用 `Inter` 或 `Plus Jakarta Sans`，正文用系统栈

---

## 4. 数据模型与 API

### 4.1 数据库
```sql
CREATE DATABASE IF NOT EXISTS goodthings_v3
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE goodthings_v3;

CREATE TABLE sys_user (
  id              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  username        VARCHAR(32)  NOT NULL,
  password_hash   VARCHAR(100) NOT NULL,
  nickname        VARCHAR(32)  NOT NULL DEFAULT '',
  avatar_url      VARCHAR(255) NOT NULL DEFAULT '',
  email           VARCHAR(64)  NOT NULL DEFAULT '',
  status          TINYINT      NOT NULL DEFAULT 1,
  last_login_at   DATETIME(3)  NULL,
  create_time     DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_time     DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  is_deleted      TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 4.2 REST API（前缀 `/api/v3`）
| Method | Path | Body / Query | 鉴权 | Response |
|---|---|---|---|---|
| POST | `/auth/register` | `{username, password, nickname?}` | 否 | `{code, data: {userId}}` |
| POST | `/auth/login` | `{username, password}` | 否 | `{code, data: {token, user}}` |
| GET  | `/auth/me` | — | 是 | `{code, data: UserVO}` |
| POST | `/auth/logout` | — | 是 | `{code}` |

### 4.3 统一响应
```ts
interface ApiResponse<T> {
  code: number
  message: string
  data: T | null
}
```

### 4.4 错误码
```ts
enum ErrorCode {
  SUCCESS = 200,
  PARAM_INVALID = 400,
  UNAUTHORIZED = 401,
  FORBIDDEN = 403,
  NOT_FOUND = 404,
  CONFLICT = 409,
  SERVER_ERROR = 500
}
```

### 4.5 DTO / VO
```ts
interface LoginDTO { username: string; password: string }
interface RegisterDTO { username: string; password: string; nickname?: string }
interface LoginVO { token: string; user: UserVO }
interface UserVO {
  id: number
  username: string
  nickname: string
  avatarUrl: string
  email: string
  createTime: string
}
```

### 4.6 字段校验
- `username`：`^[a-zA-Z0-9_]{3,32}$`
- `password`：长度 8–32，至少 1 字母 + 1 数字
- `nickname`：可选，长度 0–32

---

## 5. 错误处理、安全、配置、部署

### 5.1 错误处理
- Controller 抛 `BizException(ErrorCode.X, msg)`，由 `GlobalExceptionHandler` 统一捕获
- `MethodArgumentNotValidException` → 400 + 字段错误
- 兜底 `Exception` → 500 + 隐藏细节
- 日志：服务端 `log.error("...", ex)`，响应只给 message

### 5.2 安全
- BCrypt 强度 12
- JWT 密钥从 `application.yml` 读（`goodthings.jwt.secret`），长度 ≥ 256 bit
- 密码字段 DTO 用 `String`，永远不打日志
- 注册/登录接口加简单 In-Memory RateLimiter（阶段 1），阶段 2 切 Redis
- 跨域 `CorsConfig`：开发 `http://localhost:5173`，生产读配置
- `JwtAuthFilter` 白名单：`/api/v3/auth/login`、`/api/v3/auth/register`、`/api/v3/auth/refresh`（阶段 1 无 refresh）

### 5.3 部署（阶段 1 仅文档）
- 后端：`mvn -DskipTests package` 出 jar，scp 上服务器，`systemd` 守护
- 前端：`pnpm build` 出 `dist/`，Nginx 静态托管 + 反代 `/api/` 到后端
- 静态资源目录：`/var/www/goodthings/uploads/`（阶段 2 引入）

### 5.4 后续阶段预留
- 阶段 2：`cms_item`、`cms_tag`、`cms_item_tag`、`cms_collection`、`cms_item_collection`、`cms_oss_file`
- 阶段 3：评论与回复表
- 阶段 4：点赞表
