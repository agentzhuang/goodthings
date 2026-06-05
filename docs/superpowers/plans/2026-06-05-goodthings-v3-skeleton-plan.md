# 须皓好东西收藏网 v3 — 阶段 1（骨架 + 鉴权）实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在 `rewrite/v3` 分支上重写一个"个人好东西收藏网"的项目骨架 + 用户鉴权，能注册、登录、获取当前用户；后端 Spring Boot 3 + 前端 Vue 3 + TS + Vite + 暗色 token。

**Architecture:** 单仓 monorepo 风格——保留两个独立 Git 仓库。后端 Spring Boot 3 标准分层（controller → service → mapper → entity），JWT 鉴权过滤器；前端 Vue 3 + Pinia + Vue Router 4 + 自写暗色 token；前后端用 `/api/v3` 前缀和 `ApiResponse<T>` 统一信封通信。

**Tech Stack:** Spring Boot 3.2, MyBatis-Plus 3.5.7, JJWT 0.12, MySQL 8, Java 17 / Vue 3.5, Vite 5, TypeScript 5, Pinia, Vue Router 4, Axios, @vueuse/core, dayjs, nprogress.

**Spec 文档：** `docs/superpowers/specs/2026-06-05-goodthings-v3-rewrite-design.md`

---

## 文件结构概览

### 后端（`goodthings` 仓库，路径相对仓库根）

| 路径 | 责任 |
|---|---|
| `pom.xml` | Maven 配置，Spring Boot 3 parent |
| `src/main/resources/application.yml` | 默认配置 |
| `src/main/resources/application-dev.yml` | 开发环境配置 |
| `src/main/resources/application-prod.yml` | 生产环境配置 |
| `src/main/resources/db/migration/V1__init.sql` | 初始化 sys_user 表 |
| `src/main/java/com/goodthings/v3/GoodthingsApplication.java` | 启动类 |
| `src/main/java/com/goodthings/v3/common/Result.java` | 统一响应信封 |
| `src/main/java/com/goodthings/v3/common/ErrorCode.java` | 错误码枚举 |
| `src/main/java/com/goodthings/v3/common/BizException.java` | 业务异常 |
| `src/main/java/com/goodthings/v3/common/GlobalExceptionHandler.java` | 全局异常处理 |
| `src/main/java/com/goodthings/v3/common/PageResult.java` | 分页响应（占位） |
| `src/main/java/com/goodthings/v3/config/CorsConfig.java` | 跨域配置 |
| `src/main/java/com/goodthings/v3/config/MybatisPlusConfig.java` | 分页 + 逻辑删除 |
| `src/main/java/com/goodthings/v3/config/MyMetaObjectHandler.java` | 自动填充时间 |
| `src/main/java/com/goodthings/v3/config/PasswordEncoderConfig.java` | BCrypt Bean |
| `src/main/java/com/goodthings/v3/security/JwtProperties.java` | JWT 配置绑定 |
| `src/main/java/com/goodthings/v3/security/JwtUtil.java` | 生成/解析 token |
| `src/main/java/com/goodthings/v3/security/JwtAuthFilter.java` | 鉴权过滤器 |
| `src/main/java/com/goodthings/v3/security/SecurityConfig.java` | Spring Security 配置 |
| `src/main/java/com/goodthings/v3/entity/SysUser.java` | 用户实体 |
| `src/main/java/com/goodthings/v3/mapper/SysUserMapper.java` | MyBatis-Plus Mapper |
| `src/main/java/com/goodthings/v3/dto/LoginDTO.java` | 登录入参 |
| `src/main/java/com/goodthings/v3/dto/RegisterDTO.java` | 注册入参 |
| `src/main/java/com/goodthings/v3/vo/UserVO.java` | 用户视图 |
| `src/main/java/com/goodthings/v3/vo/LoginVO.java` | 登录返回 |
| `src/main/java/com/goodthings/v3/service/AuthService.java` | 鉴权业务接口 |
| `src/main/java/com/goodthings/v3/service/impl/AuthServiceImpl.java` | 鉴权实现 |
| `src/main/java/com/goodthings/v3/service/UserService.java` | 用户业务接口 |
| `src/main/java/com/goodthings/v3/service/impl/UserServiceImpl.java` | 用户实现 |
| `src/main/java/com/goodthings/v3/controller/AuthController.java` | 鉴权 API |
| `src/main/java/com/goodthings/v3/controller/UserController.java` | 用户 API（仅 /me） |
| `src/main/java/com/goodthings/v3/util/RateLimiter.java` | 内存令牌桶 |

### 前端（`goodthings-web` 仓库，路径相对仓库根）

| 路径 | 责任 |
|---|---|
| `package.json` | 依赖与脚本 |
| `tsconfig.json` | TypeScript 配置（strict） |
| `vite.config.ts` | Vite 配置 |
| `index.html` | 入口 HTML |
| `.env.development` | 开发环境变量 |
| `.env.production` | 生产环境变量 |
| `.env.example` | 变量名示例 |
| `.gitignore` | 忽略文件 |
| `src/main.ts` | 入口 |
| `src/App.vue` | 根组件 |
| `src/style/tokens.css` | 暗色 token |
| `src/style/reset.css` | CSS reset |
| `src/style/global.css` | 全局样式 |
| `src/api/types.ts` | ApiResponse<T> 等共享类型 |
| `src/api/http.ts` | Axios 实例与拦截器 |
| `src/api/auth.ts` | register / login / me / logout |
| `src/api/user.ts` | 用户相关 |
| `src/stores/auth.ts` | Pinia auth store |
| `src/router/index.ts` | 路由表 |
| `src/router/guards.ts` | 鉴权守卫 |
| `src/views/Discover.vue` | 首页占位 |
| `src/views/Login.vue` | 登录页 |
| `src/views/Register.vue` | 注册页 |
| `src/views/About.vue` | 关于页 |
| `src/components/AppHeader.vue` | 顶栏 |
| `src/components/AppFooter.vue` | 页脚 |
| `src/lib/nprogress.ts` | 路由进度条 |

### 文档
| 路径 | 责任 |
|---|---|
| `docs/superpowers/specs/2026-06-05-goodthings-v3-rewrite-design.md` | 设计 spec（已存在） |
| `docs/superpowers/plans/2026-06-05-goodthings-v3-skeleton-plan.md` | 本 plan |
| `docs/deploy/skeleton.md` | 阶段 1 部署文档（开发跑起来 + 生产 systemd 草稿） |

---

## 任务总览

| 任务 | 内容 | 后端/前端 | 依赖 |
|---|---|---|---|
| 0 | 创建分支与初始化仓库 | 双方 | - |
| 1 | 后端：pom.xml + application*.yml | 后端 | 0 |
| 2 | 后端：DB 初始化脚本 + 实体 + Mapper | 后端 | 1 |
| 3 | 后端：common（Result/ErrorCode/BizException/Handler/PageResult） | 后端 | 1 |
| 4 | 后端：security（JwtProperties/Util/Filter/SecurityConfig） | 后端 | 1 |
| 5 | 后端：config（Cors/MybatisPlus/MetaObjectHandler/PasswordEncoder） | 后端 | 1 |
| 6 | 后端：DTO/VO | 后端 | 1 |
| 7 | 后端：service（Auth/User）+ impl | 后端 | 2,3,4,5,6 |
| 8 | 后端：controller（Auth/User）+ util/RateLimiter | 后端 | 7 |
| 9 | 后端：GoodthingsApplication + 本地启动验证 | 后端 | 1-8 |
| 10 | 前端：package.json + vite/ts/环境 | 前端 | 0 |
| 11 | 前端：style tokens + reset + global | 前端 | 10 |
| 12 | 前端：api/types + http + auth + user | 前端 | 10 |
| 13 | 前端：stores/auth | 前端 | 12 |
| 14 | 前端：router/index + guards | 前端 | 13 |
| 15 | 前端：AppHeader / AppFooter / main.ts / App.vue | 前端 | 11,13,14 |
| 16 | 前端：Discover / Login / Register / About | 前端 | 12,15 |
| 17 | 前端：本地 dev 跑通联调 | 前端 | 9,16 |
| 18 | 部署文档 | 双方 | 17 |

---

## Task 0：创建分支与初始化仓库

**Files:**
- 后端：`goodthings` 仓库
- 前端：`goodthings-web` 仓库

- [ ] **Step 1：后端仓库开 `rewrite/v3` 分支并推送到 origin**

```bash
cd /mnt/f/zxh/goodthings
git checkout dev
git pull
git checkout -b rewrite/v3
git push -u origin rewrite/v3
```

预期：`Branch 'rewrite/v3' set up to track remote 'rewrite/v3' from 'origin'.`

- [ ] **Step 2：前端仓库开 `rewrite/v3` 分支并推送到 origin**

```bash
cd /mnt/f/zxh/goodthings-web
git checkout -b rewrite/v3   # 当前在 fix/code-cleanup 上，未提交修改会带到这个分支
git push -u origin rewrite/v3
```

预期：分支被推上去。**注意**：之前 `fix/code-cleanup` 分支上有未提交的修改（package.json 等），这些会被带进 `rewrite/v3`。我们会在 Task 10 写新的 `package.json`，旧的未提交修改会被覆盖，无碍。

- [ ] **Step 3：清理后端仓库旧 src 与 target**

```bash
cd /mnt/f/zxh/goodthings
git rm -rf src/main/java target
mkdir -p src/main/java/com/goodthings/v3
git commit -m "chore: 移除 v2 源码，准备 v3 重写"
```

预期：commit 成功。

- [ ] **Step 4：清理前端仓库旧 src / node_modules**

```bash
cd /mnt/f/zxh/goodthings-web
rm -rf src node_modules
mkdir -p src
git add -A
git commit -m "chore: 移除 v2 前端源码与依赖锁，准备 v3 重写" --allow-empty
```

预期：commit 成功。

- [ ] **Step 5：在后端创建 plan 目录**

```bash
mkdir -p /mnt/f/zxh/goodthings/docs/superpowers/plans
# 把本 plan 复制到 plans/
cp /mnt/f/zxh/goodthings/docs/superpowers/plans/2026-06-05-goodthings-v3-skeleton-plan.md \
   /tmp/plan.md 2>/dev/null || true
```

实际：plan 文件已经写到 `/mnt/f/zxh/goodthings/docs/superpowers/plans/2026-06-05-goodthings-v3-skeleton-plan.md`，由 writing-plans 流程保证。

---

## Task 1：后端 pom.xml + application*.yml

**Files:**
- Create: `pom.xml`
- Create: `src/main/resources/application.yml`
- Create: `src/main/resources/application-dev.yml`
- Create: `src/main/resources/application-prod.yml`
- Create: `.env.example`
- Modify: `.gitignore`（追加 `.env`）

- [ ] **Step 1：写 `pom.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>

    <groupId>com.goodthings</groupId>
    <artifactId>goodthings-server</artifactId>
    <version>3.0.0</version>
    <packaging>jar</packaging>
    <name>goodthings-server</name>
    <description>须皓好东西收藏网 v3 后端</description>

    <properties>
        <java.version>17</java.version>
        <mybatis-plus.version>3.5.7</mybatis-plus.version>
        <jjwt.version>0.12.5</jjwt.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-3-starter</artifactId>
            <version>1.2.20</version>
        </dependency>

        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2：写 `application.yml`**

```yaml
spring:
  profiles:
    active: dev
  application:
    name: goodthings-server
  jackson:
    time-zone: Asia/Shanghai
    date-format: yyyy-MM-dd HH:mm:ss

server:
  port: 8080
  servlet:
    context-path: /api/v3

goodthings:
  jwt:
    secret: ${JWT_SECRET:please-change-me-this-is-a-default-32byte-secret-12345678}
    expire-days: 7
    issuer: goodthings-v3
```

- [ ] **Step 3：写 `application-dev.yml`**

```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/goodthings_v3?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: ${DB_USER:root}
    password: ${DB_PASSWORD:root}
  web:
    cors:
      allowed-origins: http://localhost:5173
      allowed-methods: "*"
      allowed-headers: "*"
      allow-credentials: true

goodthings:
  rate-limit:
    register: 5
    login: 10
```

- [ ] **Step 4：写 `application-prod.yml`**

```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    url: ${DB_URL}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  web:
    cors:
      allowed-origins: ${CORS_ORIGINS}
      allowed-methods: "*"
      allowed-headers: "*"
      allow-credentials: true

goodthings:
  rate-limit:
    register: 5
    login: 10
```

- [ ] **Step 5：写 `.env.example`**

```
DB_URL=jdbc:mysql://localhost:3306/goodthings_v3?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
DB_USER=root
DB_PASSWORD=
JWT_SECRET=please-generate-a-random-32-byte-base64-secret
CORS_ORIGINS=https://goodthings.example.com
```

- [ ] **Step 6：更新 `.gitignore`**

```bash
cd /mnt/f/zxh/goodthings
# 确保 .env 不进仓库
grep -q '^\.env$' .gitignore || echo '.env' >> .gitignore
# target 已有则保留
```

- [ ] **Step 7：验证 pom 解析**

```bash
cd /mnt/f/zxh/goodthings
mvn -q -DskipTests dependency:resolve
```

预期：BUILD SUCCESS。

- [ ] **Step 8：提交**

```bash
git add pom.xml src/main/resources .gitignore .env.example
git commit -m "feat(v3): pom.xml + application*.yml + .env.example"
```

---

## Task 2：后端 DB 初始化脚本 + 实体 + Mapper

**Files:**
- Create: `src/main/resources/db/migration/V1__init.sql`
- Create: `src/main/java/com/goodthings/v3/entity/SysUser.java`
- Create: `src/main/java/com/goodthings/v3/mapper/SysUserMapper.java`

- [ ] **Step 1：写 SQL 脚本**

`src/main/resources/db/migration/V1__init.sql`：

```sql
CREATE DATABASE IF NOT EXISTS goodthings_v3
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE goodthings_v3;

CREATE TABLE IF NOT EXISTS sys_user (
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

- [ ] **Step 2：本地执行 SQL 验证可建表**

```bash
mysql -u root -p < /mnt/f/zxh/goodthings/src/main/resources/db/migration/V1__init.sql
mysql -u root -p -e "USE goodthings_v3; SHOW TABLES; DESC sys_user;"
```

预期：表 `sys_user` 存在，列名与类型匹配。

- [ ] **Step 3：写 `SysUser.java`**

`src/main/java/com/goodthings/v3/entity/SysUser.java`：

```java
package com.goodthings.v3.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    @TableField("password_hash")
    private String passwordHash;

    private String nickname;

    @TableField("avatar_url")
    private String avatarUrl;

    private String email;

    private Integer status;

    @TableField(value = "last_login_at", fill = FieldFill.UPDATE)
    private LocalDateTime lastLoginAt;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}
```

- [ ] **Step 4：写 `SysUserMapper.java`**

`src/main/java/com/goodthings/v3/mapper/SysUserMapper.java`：

```java
package com.goodthings.v3.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goodthings.v3.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
```

- [ ] **Step 5：编译验证**

```bash
cd /mnt/f/zxh/goodthings
mvn -q -DskipTests compile
```

预期：BUILD SUCCESS。

- [ ] **Step 6：提交**

```bash
git add src/main/resources/db src/main/java/com/goodthings/v3/entity src/main/java/com/goodthings/v3/mapper
git commit -m "feat(v3): sys_user 实体 + Mapper + V1 初始化脚本"
```

---

## Task 3：后端 common（Result / ErrorCode / BizException / Handler / PageResult）

**Files:**
- Create: `src/main/java/com/goodthings/v3/common/Result.java`
- Create: `src/main/java/com/goodthings/v3/common/ErrorCode.java`
- Create: `src/main/java/com/goodthings/v3/common/BizException.java`
- Create: `src/main/java/com/goodthings/v3/common/GlobalExceptionHandler.java`
- Create: `src/main/java/com/goodthings/v3/common/PageResult.java`

- [ ] **Step 1：写 `ErrorCode.java`**

```java
package com.goodthings.v3.common;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SUCCESS(200, "success"),
    PARAM_INVALID(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源冲突"),
    SERVER_ERROR(500, "服务器内部错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
```

- [ ] **Step 2：写 `Result.java`**

```java
package com.goodthings.v3.common;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = ErrorCode.SUCCESS.getCode();
        r.message = ErrorCode.SUCCESS.getMessage();
        r.data = data;
        return r;
    }

    public static <T> Result<T> error(ErrorCode ec) {
        return error(ec, ec.getMessage());
    }

    public static <T> Result<T> error(ErrorCode ec, String message) {
        Result<T> r = new Result<>();
        r.code = ec.getCode();
        r.message = message;
        return r;
    }
}
```

- [ ] **Step 3：写 `BizException.java`**

```java
package com.goodthings.v3.common;

import lombok.Getter;

@Getter
public class BizException extends RuntimeException {
    private final ErrorCode errorCode;

    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
```

- [ ] **Step 4：写 `PageResult.java`（阶段 1 占位）**

```java
package com.goodthings.v3.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private long total;
    private long page;
    private long size;
    private List<T> records;
}
```

- [ ] **Step 5：写 `GlobalExceptionHandler.java`**

```java
package com.goodthings.v3.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public Result<Void> handleBiz(BizException ex) {
        log.warn("BizException: {}", ex.getMessage());
        return Result.error(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValid(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return Result.error(ErrorCode.PARAM_INVALID, msg);
    }

    @ExceptionHandler(BindException.class)
    public Result<Void> handleBind(BindException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return Result.error(ErrorCode.PARAM_INVALID, msg);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleNotReadable(HttpMessageNotReadableException ex) {
        return Result.error(ErrorCode.PARAM_INVALID, "请求体不可读");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleAny(Exception ex) {
        log.error("Unhandled exception", ex);
        return Result.error(ErrorCode.SERVER_ERROR);
    }
}
```

- [ ] **Step 6：编译**

```bash
cd /mnt/f/zxh/goodthings
mvn -q -DskipTests compile
```

预期：BUILD SUCCESS。

- [ ] **Step 7：提交**

```bash
git add src/main/java/com/goodthings/v3/common
git commit -m "feat(v3): common 层 - Result/ErrorCode/BizException/Handler/PageResult"
```

---

## Task 4：后端 security（JwtProperties / JwtUtil / JwtAuthFilter / SecurityConfig）

**Files:**
- Create: `src/main/java/com/goodthings/v3/security/JwtProperties.java`
- Create: `src/main/java/com/goodthings/v3/security/JwtUtil.java`
- Create: `src/main/java/com/goodthings/v3/security/JwtAuthFilter.java`
- Create: `src/main/java/com/goodthings/v3/security/SecurityConfig.java`

- [ ] **Step 1：写 `JwtProperties.java`**

```java
package com.goodthings.v3.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "goodthings.jwt")
public class JwtProperties {
    private String secret;
    private int expireDays = 7;
    private String issuer = "goodthings-v3";
}
```

- [ ] **Step 2：写 `JwtUtil.java`**

```java
package com.goodthings.v3.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties props;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generate(Long userId, String username) {
        Instant now = Instant.now();
        Instant exp = now.plus(props.getExpireDays(), ChronoUnit.DAYS);
        return Jwts.builder()
                .issuer(props.getIssuer())
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key())
                .compact();
    }

    public Claims parse(String token) {
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token);
        return jws.getPayload();
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public Long extractUserId(String token) {
        return Long.parseLong(parse(token).getSubject());
    }

    public String extractUsername(String token) {
        return parse(token).get("username", String.class);
    }
}
```

- [ ] **Step 3：写 `JwtAuthFilter.java`**

```java
package com.goodthings.v3.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.isValid(token)) {
                Long userId = jwtUtil.extractUserId(token);
                String username = jwtUtil.extractUsername(token);
                var auth = new UsernamePasswordAuthenticationToken(
                        userId, username,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                log.debug("Invalid JWT for path {}", request.getRequestURI());
            }
        }
        chain.doFilter(request, response);
    }
}
```

- [ ] **Step 4：写 `SecurityConfig.java`**

```java
package com.goodthings.v3.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Value("${spring.web.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(c -> c.configurationSource(corsSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(reg -> reg
                .requestMatchers(HttpMethod.POST,
                        "/auth/register", "/auth/login", "/auth/refresh").permitAll()
                .requestMatchers("/actuator/health", "/error").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private CorsConfigurationSource corsSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        cfg.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }
}
```

> **注意**：`SecurityConfig` 里写了 `PasswordEncoder` Bean 之外的 CORS。如需单独 CORS `Bean`，参考老项目 `config/CorsConfig.java`，但本计划把 CORS 放在 `SecurityConfig` 里更紧凑。

- [ ] **Step 5：编译**

```bash
cd /mnt/f/zxh/goodthings
mvn -q -DskipTests compile
```

预期：BUILD SUCCESS。如报 `JwtAuthFilter`/`SecurityConfig` 找不到符号，停下检查 import 路径。

- [ ] **Step 6：提交**

```bash
git add src/main/java/com/goodthings/v3/security
git commit -m "feat(v3): security 层 - JwtProperties/Util/Filter/SecurityConfig"
```

---

## Task 5：后端 config（Cors / MybatisPlus / MetaObjectHandler / PasswordEncoder）

**Files:**
- Create: `src/main/java/com/goodthings/v3/config/CorsConfig.java`
- Create: `src/main/java/com/goodthings/v3/config/MybatisPlusConfig.java`
- Create: `src/main/java/com/goodthings/v3/config/MyMetaObjectHandler.java`
- Create: `src/main/java/com/goodthings/v3/config/PasswordEncoderConfig.java`

> **注意**：步骤 4 已把 CORS 写在 `SecurityConfig` 里；本 Task 不再单独写 CorsConfig Bean（避免重复）。文件清单中可保留 `CorsConfig.java` 作为后续扩展，但本 Task 只创建以下三个文件。

- [ ] **Step 1：写 `MybatisPlusConfig.java`**

```java
package com.goodthings.v3.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

- [ ] **Step 2：写 `MyMetaObjectHandler.java`**

```java
package com.goodthings.v3.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
```

- [ ] **Step 3：写 `PasswordEncoderConfig.java`**

```java
package com.goodthings.v3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
```

- [ ] **Step 4：编译**

```bash
cd /mnt/f/zxh/goodthings
mvn -q -DskipTests compile
```

预期：BUILD SUCCESS。

- [ ] **Step 5：提交**

```bash
git add src/main/java/com/goodthings/v3/config
git commit -m "feat(v3): config 层 - MybatisPlus/MetaObjectHandler/PasswordEncoder"
```

---

## Task 6：后端 DTO / VO

**Files:**
- Create: `src/main/java/com/goodthings/v3/dto/LoginDTO.java`
- Create: `src/main/java/com/goodthings/v3/dto/RegisterDTO.java`
- Create: `src/main/java/com/goodthings/v3/vo/UserVO.java`
- Create: `src/main/java/com/goodthings/v3/vo/LoginVO.java`

- [ ] **Step 1：写 `LoginDTO.java`**

```java
package com.goodthings.v3.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
```

- [ ] **Step 2：写 `RegisterDTO.java`**

```java
package com.goodthings.v3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,32}$", message = "用户名必须为 3-32 位字母/数字/下划线")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 32, message = "密码长度 8-32")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "密码需含字母与数字")
    private String password;

    @Size(max = 32, message = "昵称长度 0-32")
    private String nickname;
}
```

- [ ] **Step 3：写 `UserVO.java`**

```java
package com.goodthings.v3.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatarUrl;
    private String email;
    private LocalDateTime createTime;
}
```

- [ ] **Step 4：写 `LoginVO.java`**

```java
package com.goodthings.v3.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginVO {
    private String token;
    private UserVO user;
}
```

- [ ] **Step 5：编译**

```bash
cd /mnt/f/zxh/goodthings
mvn -q -DskipTests compile
```

预期：BUILD SUCCESS。

- [ ] **Step 6：提交**

```bash
git add src/main/java/com/goodthings/v3/dto src/main/java/com/goodthings/v3/vo
git commit -m "feat(v3): DTO/VO - LoginDTO/RegisterDTO/UserVO/LoginVO"
```

---

## Task 7：后端 service（Auth / User）+ impl

**Files:**
- Create: `src/main/java/com/goodthings/v3/service/AuthService.java`
- Create: `src/main/java/com/goodthings/v3/service/impl/AuthServiceImpl.java`
- Create: `src/main/java/com/goodthings/v3/service/UserService.java`
- Create: `src/main/java/com/goodthings/v3/service/impl/UserServiceImpl.java`

- [ ] **Step 1：写 `UserService.java`**

```java
package com.goodthings.v3.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.goodthings.v3.entity.SysUser;

public interface UserService extends IService<SysUser> {

    UserVO toVO(SysUser user);
}
```

> `IService` 提供 `getById / save / lambdaQuery` 等基础方法，无需手写。

- [ ] **Step 2：写 `UserServiceImpl.java`**

```java
package com.goodthings.v3.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodthings.v3.entity.SysUser;
import com.goodthings.v3.mapper.SysUserMapper;
import com.goodthings.v3.service.UserService;
import com.goodthings.v3.vo.UserVO;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {

    @Override
    public UserVO toVO(SysUser u) {
        if (u == null) return null;
        return UserVO.builder()
                .id(u.getId())
                .username(u.getUsername())
                .nickname(u.getNickname())
                .avatarUrl(u.getAvatarUrl())
                .email(u.getEmail())
                .createTime(u.getCreateTime())
                .build();
    }
}
```

- [ ] **Step 3：写 `AuthService.java`**

```java
package com.goodthings.v3.service;

import com.goodthings.v3.dto.LoginDTO;
import com.goodthings.v3.dto.RegisterDTO;
import com.goodthings.v3.entity.SysUser;
import com.goodthings.v3.vo.LoginVO;
import com.goodthings.v3.vo.UserVO;

public interface AuthService {

    Long register(RegisterDTO dto);

    LoginVO login(LoginDTO dto);

    UserVO me(Long userId);
}
```

- [ ] **Step 4：写 `AuthServiceImpl.java`**

```java
package com.goodthings.v3.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.goodthings.v3.common.BizException;
import com.goodthings.v3.common.ErrorCode;
import com.goodthings.v3.dto.LoginDTO;
import com.goodthings.v3.dto.RegisterDTO;
import com.goodthings.v3.entity.SysUser;
import com.goodthings.v3.security.JwtUtil;
import com.goodthings.v3.service.AuthService;
import com.goodthings.v3.service.UserService;
import com.goodthings.v3.vo.LoginVO;
import com.goodthings.v3.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public Long register(RegisterDTO dto) {
        LambdaQueryWrapper<SysUser> q = new LambdaQueryWrapper<>();
        q.eq(SysUser::getUsername, dto.getUsername());
        if (userService.getOne(q) != null) {
            throw new BizException(ErrorCode.CONFLICT, "用户名已存在");
        }
        SysUser u = new SysUser();
        u.setUsername(dto.getUsername());
        u.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        u.setNickname(dto.getNickname() == null || dto.getNickname().isBlank()
                ? dto.getUsername() : dto.getNickname());
        u.setStatus(1);
        userService.save(u);
        return u.getId();
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        LambdaQueryWrapper<SysUser> q = new LambdaQueryWrapper<>();
        q.eq(SysUser::getUsername, dto.getUsername());
        SysUser u = userService.getOne(q);
        if (u == null || !passwordEncoder.matches(dto.getPassword(), u.getPasswordHash())) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "用户名或密码错误");
        }
        if (u.getStatus() != null && u.getStatus() == 0) {
            throw new BizException(ErrorCode.FORBIDDEN, "账号已被封禁");
        }
        u.setLastLoginAt(LocalDateTime.now());
        userService.updateById(u);

        String token = jwtUtil.generate(u.getId(), u.getUsername());
        return new LoginVO(token, userService.toVO(u));
    }

    @Override
    public UserVO me(Long userId) {
        SysUser u = userService.getById(userId);
        if (u == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "用户不存在");
        }
        return userService.toVO(u);
    }
}
```

- [ ] **Step 5：编译**

```bash
cd /mnt/f/zxh/goodthings
mvn -q -DskipTests compile
```

预期：BUILD SUCCESS。

- [ ] **Step 6：提交**

```bash
git add src/main/java/com/goodthings/v3/service
git commit -m "feat(v3): service 层 - Auth/User + 实现"
```

---

## Task 8：后端 controller（Auth / User）+ util/RateLimiter

**Files:**
- Create: `src/main/java/com/goodthings/v3/controller/AuthController.java`
- Create: `src/main/java/com/goodthings/v3/controller/UserController.java`
- Create: `src/main/java/com/goodthings/v3/util/RateLimiter.java`

- [ ] **Step 1：写 `RateLimiter.java`**

```java
package com.goodthings.v3.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimiter {

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public boolean tryAcquire(String key, int permitsPerMinute) {
        Bucket b = buckets.computeIfAbsent(key, k -> new Bucket(permitsPerMinute));
        return b.tryConsume();
    }

    private static class Bucket {
        private final int maxPermits;
        private final AtomicInteger remaining;
        private volatile long windowStartMs;

        Bucket(int maxPermits) {
            this.maxPermits = maxPermits;
            this.remaining = new AtomicInteger(maxPermits);
            this.windowStartMs = System.currentTimeMillis();
        }

        synchronized boolean tryConsume() {
            long now = System.currentTimeMillis();
            if (now - windowStartMs >= 60_000) {
                windowStartMs = now;
                remaining.set(maxPermits);
            }
            int r = remaining.get();
            if (r <= 0) return false;
            return remaining.compareAndSet(r, r - 1);
        }
    }
}
```

- [ ] **Step 2：写 `AuthController.java`**

```java
package com.goodthings.v3.controller;

import com.goodthings.v3.common.BizException;
import com.goodthings.v3.common.ErrorCode;
import com.goodthings.v3.common.Result;
import com.goodthings.v3.dto.LoginDTO;
import com.goodthings.v3.dto.RegisterDTO;
import com.goodthings.v3.service.AuthService;
import com.goodthings.v3.util.RateLimiter;
import com.goodthings.v3.vo.LoginVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RateLimiter rateLimiter;

    @Value("${goodthings.rate-limit.register:5}")
    private int registerLimit;

    @Value("${goodthings.rate-limit.login:10}")
    private int loginLimit;

    @PostMapping("/register")
    public Result<Map<String, Long>> register(@Valid @RequestBody RegisterDTO dto,
                                              HttpServletRequest req) {
        String ip = clientIp(req);
        if (!rateLimiter.tryAcquire("register:" + ip, registerLimit)) {
            throw new BizException(ErrorCode.FORBIDDEN, "注册过于频繁，请稍后再试");
        }
        Long userId = authService.register(dto);
        return Result.success(Map.of("userId", userId));
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto,
                                 HttpServletRequest req) {
        String ip = clientIp(req);
        if (!rateLimiter.tryAcquire("login:" + ip, loginLimit)) {
            throw new BizException(ErrorCode.FORBIDDEN, "登录过于频繁，请稍后再试");
        }
        return Result.success(authService.login(dto));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        // 阶段 1：客户端清 token；服务端可后续接黑名单
        return Result.success(null);
    }

    private String clientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }
}
```

- [ ] **Step 3：写 `UserController.java`**

```java
package com.goodthings.v3.controller;

import com.goodthings.v3.common.Result;
import com.goodthings.v3.service.AuthService;
import com.goodthings.v3.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @GetMapping("/me")
    public Result<UserVO> me(@AuthenticationPrincipal Long userId) {
        return Result.success(authService.me(userId));
    }
}
```

> **注意**：`@AuthenticationPrincipal Long userId` 直接拿到 `JwtAuthFilter` 放进 `principal` 的 `userId`。`SecurityContextHolder` 由 `JwtAuthFilter` 写入。

- [ ] **Step 4：编译**

```bash
cd /mnt/f/zxh/goodthings
mvn -q -DskipTests compile
```

预期：BUILD SUCCESS。

- [ ] **Step 5：提交**

```bash
git add src/main/java/com/goodthings/v3/controller src/main/java/com/goodthings/v3/util
git commit -m "feat(v3): controller - Auth/User + RateLimiter"
```

---

## Task 9：后端 GoodthingsApplication + 本地启动验证

**Files:**
- Create: `src/main/java/com/goodthings/v3/GoodthingsApplication.java`

- [ ] **Step 1：写启动类**

```java
package com.goodthings.v3;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.goodthings.v3.mapper")
public class GoodthingsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodthingsApplication.class, args);
    }
}
```

- [ ] **Step 2：编译打 jar**

```bash
cd /mnt/f/zxh/goodthings
mvn -q -DskipTests package
ls target/*.jar
```

预期：`target/goodthings-server-3.0.0.jar` 存在。

- [ ] **Step 3：本地启动（后台）**

```bash
cd /mnt/f/zxh/goodthings
# 准备环境变量
export DB_USER=root
export DB_PASSWORD=你的密码
export JWT_SECRET="goodthings-v3-dev-secret-please-change-32bytes-long"
nohup java -jar target/goodthings-server-3.0.0.jar > /tmp/goodthings.log 2>&1 &
echo $! > /tmp/goodthings.pid
sleep 15
tail -50 /tmp/goodthings.log
```

预期：日志出现 `Started GoodthingsApplication in X.XXX seconds (process running for X.XXX)`，无 ERROR。

- [ ] **Step 4：cURL 烟测 register → login → me**

```bash
# 注册
curl -sS -X POST http://localhost:8080/api/v3/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"alice12345","nickname":"Alice"}'
# 预期：{"code":200,"message":"success","data":{"userId":1}}

# 登录
LOGIN=$(curl -sS -X POST http://localhost:8080/api/v3/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"alice12345"}')
echo $LOGIN
# 预期：{"code":200,"message":"success","data":{"token":"...","user":{...}}}

TOKEN=$(echo $LOGIN | sed -E 's/.*"token":"([^"]+)".*/\1/')

# 当前用户
curl -sS http://localhost:8080/api/v3/user/me -H "Authorization: Bearer $TOKEN"
# 预期：{"code":200,"message":"success","data":{"id":1,"username":"alice",...}}

# 未登录访问 /me
curl -sS http://localhost:8080/api/v3/user/me
# 预期：401 或被 Spring Security 重定向到 /login 之外的 401 JSON
```

- [ ] **Step 5：停服**

```bash
kill $(cat /tmp/goodthings.pid)
rm -f /tmp/goodthings.pid
```

- [ ] **Step 6：提交启动类**

```bash
git add src/main/java/com/goodthings/v3/GoodthingsApplication.java
git commit -m "feat(v3): 启动类 GoodthingsApplication"
```

---

## Task 10：前端 package.json + vite/ts/环境

**Files:**
- Create: `package.json`
- Create: `tsconfig.json`
- Create: `tsconfig.node.json`
- Create: `vite.config.ts`
- Create: `index.html`
- Create: `.env.development`
- Create: `.env.production`
- Create: `.env.example`
- Create: `.gitignore`

- [ ] **Step 1：写 `package.json`**

```json
{
  "name": "goodthings-web",
  "private": true,
  "version": "3.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vue-tsc --noEmit && vite build",
    "preview": "vite preview",
    "typecheck": "vue-tsc --noEmit"
  },
  "dependencies": {
    "axios": "^1.7.7",
    "dayjs": "^1.11.13",
    "nprogress": "^0.2.0",
    "pinia": "^2.2.6",
    "vue": "^3.5.13",
    "vue-router": "^4.4.5",
    "@vueuse/core": "^11.2.0"
  },
  "devDependencies": {
    "@types/node": "^22.9.0",
    "@types/nprogress": "^0.2.3",
    "@vitejs/plugin-vue": "^5.2.1",
    "typescript": "~5.6.3",
    "vite": "^5.4.11",
    "vue-tsc": "^2.1.10"
  }
}
```

- [ ] **Step 2：写 `tsconfig.json`**

```json
{
  "compilerOptions": {
    "target": "ES2022",
    "useDefineForClassFields": true,
    "module": "ESNext",
    "lib": ["ES2022", "DOM", "DOM.Iterable"],
    "skipLibCheck": true,
    "moduleResolution": "Bundler",
    "allowImportingTsExtensions": false,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "preserve",
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true,
    "esModuleInterop": true,
    "forceConsistentCasingInFileNames": true,
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    },
    "types": ["node"]
  },
  "include": ["src/**/*.ts", "src/**/*.tsx", "src/**/*.vue", "vite.config.ts"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

- [ ] **Step 3：写 `tsconfig.node.json`**

```json
{
  "compilerOptions": {
    "composite": true,
    "skipLibCheck": true,
    "module": "ESNext",
    "moduleResolution": "Bundler",
    "allowSyntheticDefaultImports": true,
    "strict": true
  },
  "include": ["vite.config.ts"]
}
```

- [ ] **Step 4：写 `vite.config.ts`**

```ts
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **Step 5：写 `index.html`**

```html
<!doctype html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <link rel="icon" type="image/svg+xml" href="/favicon.svg" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>须皓的好东西收藏网</title>
  </head>
  <body>
    <div id="app"></div>
    <script type="module" src="/src/main.ts"></script>
  </body>
</html>
```

- [ ] **Step 6：写 `.env.development`**

```
VITE_API_BASE_URL=/api/v3
```

- [ ] **Step 7：写 `.env.production`**

```
VITE_API_BASE_URL=/api/v3
```

- [ ] **Step 8：写 `.env.example`**

```
VITE_API_BASE_URL=/api/v3
```

- [ ] **Step 9：写 `.gitignore`**

```
node_modules
dist
.env
.env.local
.DS_Store
*.log
.vscode
.idea
```

- [ ] **Step 10：安装依赖**

```bash
cd /mnt/f/zxh/goodthings-web
npm install
```

预期：依赖安装成功，无 ERR。

- [ ] **Step 11：提交**

```bash
git add package.json package-lock.json tsconfig.json tsconfig.node.json vite.config.ts index.html .env.development .env.production .env.example .gitignore
git commit -m "feat(v3): package.json + vite/ts/环境配置"
```

---

## Task 11：前端 style tokens + reset + global

**Files:**
- Create: `src/style/tokens.css`
- Create: `src/style/reset.css`
- Create: `src/style/global.css`

- [ ] **Step 1：写 `tokens.css`**

```css
:root {
  /* Color */
  --color-bg: oklch(14% 0.012 260);
  --color-surface: oklch(20% 0.015 260);
  --color-surface-elev: oklch(24% 0.018 260);
  --color-border: oklch(28% 0.018 260);
  --color-text: oklch(96% 0.01 260);
  --color-text-muted: oklch(70% 0.012 260);
  --color-accent: oklch(72% 0.18 35);
  --color-accent-hover: oklch(76% 0.19 35);
  --color-danger: oklch(65% 0.22 25);
  --color-success: oklch(72% 0.17 145);

  /* Typography */
  --font-sans: "Inter", "Plus Jakarta Sans", system-ui, -apple-system,
    "PingFang SC", "Microsoft YaHei", sans-serif;
  --font-mono: ui-monospace, SFMono-Regular, Menlo, monospace;
  --text-xs: 12px;
  --text-sm: 14px;
  --text-base: 16px;
  --text-lg: 18px;
  --text-xl: 24px;
  --text-2xl: 32px;
  --text-hero: clamp(2.5rem, 1.5rem + 4vw, 5rem);

  /* Spacing */
  --space-1: 4px;
  --space-2: 8px;
  --space-3: 12px;
  --space-4: 16px;
  --space-6: 24px;
  --space-8: 32px;
  --space-12: 48px;
  --space-16: 64px;

  /* Radius */
  --radius-sm: 6px;
  --radius-md: 10px;
  --radius-lg: 16px;
  --radius-pill: 999px;

  /* Elevation */
  --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.4);
  --shadow-md: 0 6px 18px rgba(0, 0, 0, 0.45);
  --shadow-lg: 0 18px 40px rgba(0, 0, 0, 0.55);

  /* Motion */
  --duration-fast: 150ms;
  --duration-normal: 250ms;
  --duration-slow: 400ms;
  --ease-out: cubic-bezier(0.16, 1, 0.3, 1);
}
```

- [ ] **Step 2：写 `reset.css`**

```css
*, *::before, *::after { box-sizing: border-box; }
* { margin: 0; }
html, body, #app { height: 100%; }
body {
  line-height: 1.5;
  -webkit-font-smoothing: antialiased;
  text-rendering: optimizeLegibility;
}
img, picture, video, canvas, svg { display: block; max-width: 100%; }
input, button, textarea, select { font: inherit; color: inherit; }
button { background: transparent; border: 0; cursor: pointer; }
p, h1, h2, h3, h4, h5, h6 { overflow-wrap: break-word; }
a { color: inherit; text-decoration: none; }
```

- [ ] **Step 3：写 `global.css`**

```css
body {
  background: var(--color-bg);
  color: var(--color-text);
  font-family: var(--font-sans);
  font-size: var(--text-base);
}

a:focus-visible, button:focus-visible, input:focus-visible {
  outline: 2px solid var(--color-accent);
  outline-offset: 2px;
  border-radius: var(--radius-sm);
}

::selection {
  background: var(--color-accent);
  color: var(--color-bg);
}

/* NProgress 暗色适配 */
#nprogress .bar { background: var(--color-accent) !important; }
#nprogress .peg { box-shadow: 0 0 10px var(--color-accent), 0 0 5px var(--color-accent) !important; }
#nprogress .spinner-icon { border-top-color: var(--color-accent) !important; border-left-color: var(--color-accent) !important; }
```

- [ ] **Step 4：提交**

```bash
git add src/style
git commit -m "feat(v3): 暗色 token + reset + global"
```

---

## Task 12：前端 api/types + http + auth + user

**Files:**
- Create: `src/api/types.ts`
- Create: `src/api/http.ts`
- Create: `src/api/auth.ts`
- Create: `src/api/user.ts`

- [ ] **Step 1：写 `types.ts`**

```ts
export interface ApiResponse<T> {
  code: number
  message: string
  data: T | null
}

export interface LoginDTO {
  username: string
  password: string
}

export interface RegisterDTO {
  username: string
  password: string
  nickname?: string
}

export interface UserVO {
  id: number
  username: string
  nickname: string
  avatarUrl: string
  email: string
  createTime: string
}

export interface LoginVO {
  token: string
  user: UserVO
}
```

- [ ] **Step 2：写 `http.ts`**

```ts
import axios, { AxiosError, type AxiosInstance } from 'axios'
import type { ApiResponse } from './types'

const baseURL = import.meta.env.VITE_API_BASE_URL || '/api/v3'

export const http: AxiosInstance = axios.create({
  baseURL,
  timeout: 15000
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (resp) => resp.data as ApiResponse<unknown>,
  (error: AxiosError<ApiResponse<unknown>>) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      // 路由跳转由守卫统一处理
      if (location.pathname !== '/login' && location.pathname !== '/register') {
        location.href = '/login'
      }
    }
    const data = error.response?.data
    const message = (data && data.message) || error.message || '网络错误'
    return Promise.reject(new Error(message))
  }
)
```

- [ ] **Step 3：写 `auth.ts`**

```ts
import { http } from './http'
import type { ApiResponse, LoginDTO, RegisterDTO, LoginVO, UserVO } from './types'

export function register(dto: RegisterDTO) {
  return http.post<ApiResponse<{ userId: number }>, ApiResponse<{ userId: number }>>(
    '/auth/register',
    dto
  ) as unknown as Promise<ApiResponse<{ userId: number }>>
}

export function login(dto: LoginDTO) {
  return http.post<ApiResponse<LoginVO>, ApiResponse<LoginVO>>(
    '/auth/login',
    dto
  ) as unknown as Promise<ApiResponse<LoginVO>>
}

export function logout() {
  return http.post<ApiResponse<null>, ApiResponse<null>>(
    '/auth/logout'
  ) as unknown as Promise<ApiResponse<null>>
}

export function me() {
  return http.get<ApiResponse<UserVO>, ApiResponse<UserVO>>(
    '/user/me'
  ) as unknown as Promise<ApiResponse<UserVO>>
}
```

> 说明：上面 `as unknown as Promise<...>` 是一种最小成本的"剥离 axios 包装"写法，让 http.ts 的 `response.use` 已经解过一次后再断言。生产中可换 `ApiResponse<T>` 泛型简化。

- [ ] **Step 4：写 `user.ts`**

```ts
import { http } from './http'
import type { ApiResponse, UserVO } from './types'

export function getMe() {
  return http.get<ApiResponse<UserVO>, ApiResponse<UserVO>>(
    '/user/me'
  ) as unknown as Promise<ApiResponse<UserVO>>
}
```

> 阶段 1 与 `auth.ts#me` 重复，预留后续用户修改等接口。

- [ ] **Step 5：提交**

```bash
git add src/api
git commit -m "feat(v3): api 层 - types/http/auth/user"
```

---

## Task 13：前端 stores/auth

**Files:**
- Create: `src/stores/auth.ts`

- [ ] **Step 1：写 `auth.ts`**

```ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserVO } from '@/api/types'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<UserVO | null>(loadUser())

  const isLoggedIn = computed(() => !!token.value)

  function setToken(t: string | null) {
    token.value = t
    if (t) localStorage.setItem('token', t)
    else localStorage.removeItem('token')
  }

  function setUser(u: UserVO | null) {
    user.value = u
    if (u) localStorage.setItem('user', JSON.stringify(u))
    else localStorage.removeItem('user')
  }

  function clear() {
    setToken(null)
    setUser(null)
  }

  return { token, user, isLoggedIn, setToken, setUser, clear }
})

function loadUser(): UserVO | null {
  const raw = localStorage.getItem('user')
  if (!raw) return null
  try {
    return JSON.parse(raw) as UserVO
  } catch {
    return null
  }
}
```

- [ ] **Step 2：提交**

```bash
git add src/stores
git commit -m "feat(v3): Pinia auth store"
```

---

## Task 14：前端 router/index + guards

**Files:**
- Create: `src/router/index.ts`
- Create: `src/router/guards.ts`
- Create: `src/lib/nprogress.ts`

- [ ] **Step 1：写 `lib/nprogress.ts`**

```ts
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

NProgress.configure({ showSpinner: false, trickleSpeed: 200 })

export function startProgress() { NProgress.start() }
export function doneProgress() { NProgress.done() }
```

- [ ] **Step 2：写 `router/guards.ts`**

```ts
import type { Router } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { startProgress, doneProgress } from '@/lib/nprogress'

export function setupGuards(router: Router) {
  router.beforeEach((to) => {
    startProgress()
    const auth = useAuthStore()
    if (to.meta.requiresAuth && !auth.isLoggedIn) {
      return { path: '/login', query: { redirect: to.fullPath } }
    }
    if (auth.isLoggedIn && (to.path === '/login' || to.path === '/register')) {
      return { path: '/' }
    }
    return true
  })

  router.afterEach(() => doneProgress())
  router.onError(() => doneProgress())
}
```

- [ ] **Step 3：写 `router/index.ts`**

```ts
import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { setupGuards } from './guards'

const routes: RouteRecordRaw[] = [
  { path: '/', name: 'discover', component: () => import('@/views/Discover.vue') },
  { path: '/login', name: 'login', component: () => import('@/views/Login.vue') },
  { path: '/register', name: 'register', component: () => import('@/views/Register.vue') },
  { path: '/about', name: 'about', component: () => import('@/views/About.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

setupGuards(router)

export default router
```

- [ ] **Step 4：提交**

```bash
git add src/router src/lib
git commit -m "feat(v3): router + guards + nprogress"
```

---

## Task 15：前端 AppHeader / AppFooter / main.ts / App.vue

**Files:**
- Create: `src/components/AppHeader.vue`
- Create: `src/components/AppFooter.vue`
- Create: `src/main.ts`
- Create: `src/App.vue`

- [ ] **Step 1：写 `AppHeader.vue`**

```vue
<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()

function onLogout() {
  auth.clear()
  router.push('/login')
}
</script>

<template>
  <header class="app-header">
    <div class="inner">
      <RouterLink to="/" class="brand">须皓的好东西</RouterLink>
      <nav>
        <RouterLink to="/">发现</RouterLink>
        <RouterLink to="/about">关于</RouterLink>
      </nav>
      <div class="auth">
        <template v-if="auth.isLoggedIn">
          <span class="user">{{ auth.user?.nickname || auth.user?.username }}</span>
          <button class="btn ghost" @click="onLogout">登出</button>
        </template>
        <template v-else>
          <RouterLink to="/login" class="btn ghost">登录</RouterLink>
          <RouterLink to="/register" class="btn primary">注册</RouterLink>
        </template>
      </div>
    </div>
  </header>
</template>

<style scoped>
.app-header {
  position: sticky; top: 0; z-index: 10;
  background: color-mix(in oklch, var(--color-bg) 80%, transparent);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--color-border);
}
.inner {
  max-width: 1200px; margin: 0 auto;
  padding: var(--space-3) var(--space-4);
  display: flex; align-items: center; gap: var(--space-6);
}
.brand {
  font-weight: 700; font-size: var(--text-lg);
  color: var(--color-text);
}
nav { display: flex; gap: var(--space-4); flex: 1; }
nav a { color: var(--color-text-muted); }
nav a:hover, nav a.router-link-active { color: var(--color-text); }
.auth { display: flex; align-items: center; gap: var(--space-2); }
.user { color: var(--color-text-muted); margin-right: var(--space-2); }
.btn {
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-pill);
  font-size: var(--text-sm);
  transition: background var(--duration-fast) var(--ease-out);
}
.btn.ghost { color: var(--color-text-muted); }
.btn.ghost:hover { color: var(--color-text); }
.btn.primary {
  background: var(--color-accent); color: var(--color-bg); font-weight: 600;
}
.btn.primary:hover { background: var(--color-accent-hover); }
</style>
```

- [ ] **Step 2：写 `AppFooter.vue`**

```vue
<template>
  <footer class="app-footer">
    <p>© {{ year }} 须皓 · 个人收藏</p>
  </footer>
</template>

<script setup lang="ts">
const year = new Date().getFullYear()
</script>

<style scoped>
.app-footer {
  padding: var(--space-8) var(--space-4);
  text-align: center;
  color: var(--color-text-muted);
  font-size: var(--text-sm);
  border-top: 1px solid var(--color-border);
  margin-top: var(--space-16);
}
</style>
```

- [ ] **Step 3：写 `App.vue`**

```vue
<script setup lang="ts">
import AppHeader from '@/components/AppHeader.vue'
import AppFooter from '@/components/AppFooter.vue'
</script>

<template>
  <AppHeader />
  <main>
    <RouterView />
  </main>
  <AppFooter />
</template>

<style scoped>
main {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--space-8) var(--space-4);
  min-height: calc(100vh - 200px);
}
</style>
```

- [ ] **Step 4：写 `main.ts`**

```ts
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

import './style/reset.css'
import './style/tokens.css'
import './style/global.css'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.mount('#app')
```

- [ ] **Step 5：提交**

```bash
git add src/components src/App.vue src/main.ts
git commit -m "feat(v3): App 骨架 - AppHeader/AppFooter/main/App"
```

---

## Task 16：前端 Discover / Login / Register / About

**Files:**
- Create: `src/views/Discover.vue`
- Create: `src/views/Login.vue`
- Create: `src/views/Register.vue`
- Create: `src/views/About.vue`

- [ ] **Step 1：写 `Discover.vue`（占位）**

```vue
<template>
  <section class="discover">
    <h1>须皓的好东西</h1>
    <p class="lead">收藏让我心动的图、视频、文字与代码片段。</p>
    <p class="hint">阶段 1：骨架已就绪。瀑布流敬请期待。</p>
  </section>
</template>

<style scoped>
.discover { text-align: center; padding: var(--space-12) 0; }
h1 {
  font-size: var(--text-hero); font-weight: 800; letter-spacing: -0.02em;
  background: linear-gradient(120deg, var(--color-text), var(--color-accent));
  -webkit-background-clip: text; background-clip: text; color: transparent;
}
.lead { font-size: var(--text-lg); color: var(--color-text-muted); margin-top: var(--space-3); }
.hint {
  display: inline-block; margin-top: var(--space-6);
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-pill);
  border: 1px solid var(--color-border); color: var(--color-text-muted);
  font-size: var(--text-sm);
}
</style>
```

- [ ] **Step 2：写 `Login.vue`**

```vue
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { login } from '@/api/auth'
import type { ApiResponse, LoginVO } from '@/api/types'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const form = reactive({ username: '', password: '' })
const submitting = ref(false)
const errorMsg = ref('')

async function onSubmit() {
  errorMsg.value = ''
  if (!form.username || !form.password) {
    errorMsg.value = '请输入用户名和密码'
    return
  }
  submitting.value = true
  try {
    const resp = await login({ username: form.username, password: form.password })
    if (resp.code !== 200 || !resp.data) {
      errorMsg.value = resp.message || '登录失败'
      return
    }
    const vo: LoginVO = resp.data
    auth.setToken(vo.token)
    auth.setUser(vo.user)
    const redirect = (route.query.redirect as string) || '/'
    router.replace(redirect)
  } catch (e) {
    errorMsg.value = (e as Error).message
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <section class="auth">
    <h2>登录</h2>
    <form @submit.prevent="onSubmit">
      <label>
        <span>用户名</span>
        <input v-model="form.username" type="text" autocomplete="username" />
      </label>
      <label>
        <span>密码</span>
        <input v-model="form.password" type="password" autocomplete="current-password" />
      </label>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <button class="primary" :disabled="submitting" type="submit">
        {{ submitting ? '登录中…' : '登录' }}
      </button>
    </form>
    <p class="alt">还没有账号？<RouterLink to="/register">去注册</RouterLink></p>
  </section>
</template>

<style scoped>
.auth { max-width: 380px; margin: 0 auto; }
h2 { font-size: var(--text-2xl); margin-bottom: var(--space-6); }
form { display: flex; flex-direction: column; gap: var(--space-4); }
label { display: flex; flex-direction: column; gap: var(--space-2); }
label span { color: var(--color-text-muted); font-size: var(--text-sm); }
input {
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-md);
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  color: var(--color-text);
}
input:focus { border-color: var(--color-accent); outline: none; }
button.primary {
  padding: var(--space-3) var(--space-4);
  background: var(--color-accent); color: var(--color-bg);
  border-radius: var(--radius-md); font-weight: 600;
  transition: background var(--duration-fast) var(--ease-out);
}
button.primary:hover { background: var(--color-accent-hover); }
button:disabled { opacity: 0.6; cursor: not-allowed; }
.error { color: var(--color-danger); font-size: var(--text-sm); }
.alt { margin-top: var(--space-6); color: var(--color-text-muted); font-size: var(--text-sm); }
.alt a { color: var(--color-accent); }
</style>
```

- [ ] **Step 3：写 `Register.vue`**

```vue
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'
import { login as apiLogin } from '@/api/auth'

const router = useRouter()
const auth = useAuthStore()

const form = reactive({ username: '', password: '', nickname: '' })
const submitting = ref(false)
const errorMsg = ref('')

async function onSubmit() {
  errorMsg.value = ''
  if (!/^[a-zA-Z0-9_]{3,32}$/.test(form.username)) {
    errorMsg.value = '用户名必须为 3-32 位字母/数字/下划线'
    return
  }
  if (form.password.length < 8 || form.password.length > 32) {
    errorMsg.value = '密码长度需 8-32'
    return
  }
  if (!/[A-Za-z]/.test(form.password) || !/\d/.test(form.password)) {
    errorMsg.value = '密码需同时包含字母和数字'
    return
  }
  submitting.value = true
  try {
    const resp = await register({
      username: form.username,
      password: form.password,
      nickname: form.nickname || undefined
    })
    if (resp.code !== 200) {
      errorMsg.value = resp.message || '注册失败'
      return
    }
    // 注册成功后自动登录
    const lr = await apiLogin({ username: form.username, password: form.password })
    if (lr.code !== 200 || !lr.data) {
      router.replace({ path: '/login' })
      return
    }
    auth.setToken(lr.data.token)
    auth.setUser(lr.data.user)
    router.replace('/')
  } catch (e) {
    errorMsg.value = (e as Error).message
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <section class="auth">
    <h2>注册</h2>
    <form @submit.prevent="onSubmit">
      <label>
        <span>用户名 *</span>
        <input v-model="form.username" type="text" autocomplete="username" />
      </label>
      <label>
        <span>密码 *（8-32，含字母与数字）</span>
        <input v-model="form.password" type="password" autocomplete="new-password" />
      </label>
      <label>
        <span>昵称（可选）</span>
        <input v-model="form.nickname" type="text" />
      </label>
      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <button class="primary" :disabled="submitting" type="submit">
        {{ submitting ? '提交中…' : '注册' }}
      </button>
    </form>
    <p class="alt">已有账号？<RouterLink to="/login">去登录</RouterLink></p>
  </section>
</template>

<style scoped>
.auth { max-width: 380px; margin: 0 auto; }
h2 { font-size: var(--text-2xl); margin-bottom: var(--space-6); }
form { display: flex; flex-direction: column; gap: var(--space-4); }
label { display: flex; flex-direction: column; gap: var(--space-2); }
label span { color: var(--color-text-muted); font-size: var(--text-sm); }
input {
  padding: var(--space-3) var(--space-4);
  border-radius: var(--radius-md);
  background: var(--color-surface);
  border: 1px solid var(--color-border);
  color: var(--color-text);
}
input:focus { border-color: var(--color-accent); outline: none; }
button.primary {
  padding: var(--space-3) var(--space-4);
  background: var(--color-accent); color: var(--color-bg);
  border-radius: var(--radius-md); font-weight: 600;
}
button.primary:hover { background: var(--color-accent-hover); }
button:disabled { opacity: 0.6; cursor: not-allowed; }
.error { color: var(--color-danger); font-size: var(--text-sm); }
.alt { margin-top: var(--space-6); color: var(--color-text-muted); font-size: var(--text-sm); }
.alt a { color: var(--color-accent); }
</style>
```

- [ ] **Step 4：写 `About.vue`**

```vue
<template>
  <section class="about">
    <h1>关于</h1>
    <p>这是须皓的个人好东西收藏网。收录让我心动的图片、视频、文字与代码片段。</p>
    <p class="muted">v3 · 重写中</p>
  </section>
</template>

<style scoped>
.about { max-width: 640px; margin: 0 auto; }
h1 { font-size: var(--text-2xl); margin-bottom: var(--space-4); }
.muted { color: var(--color-text-muted); margin-top: var(--space-4); }
</style>
```

- [ ] **Step 5：类型检查 + 构建**

```bash
cd /mnt/f/zxh/goodthings-web
npx vue-tsc --noEmit
npm run build
```

预期：typecheck 无错；build 成功，`dist/` 存在。

- [ ] **Step 6：提交**

```bash
git add src/views
git commit -m "feat(v3): views - Discover/Login/Register/About"
```

---

## Task 17：前端本地 dev 跑通联调

- [ ] **Step 1：后端启动（如果还没起）**

```bash
cd /mnt/f/zxh/goodthings
export DB_USER=root DB_PASSWORD='你的密码' JWT_SECRET='goodthings-v3-dev-secret-please-change-32bytes-long'
nohup java -jar target/goodthings-server-3.0.0.jar > /tmp/goodthings.log 2>&1 &
echo $! > /tmp/goodthings.pid
sleep 12
curl -sS http://localhost:8080/api/v3/auth/login -X POST -H 'Content-Type: application/json' -d '{}' || true
tail -20 /tmp/goodthings.log
```

预期：服务起来（健康探测 200/400 都行），日志无 ERROR。

- [ ] **Step 2：启动前端 dev server**

```bash
cd /mnt/f/zxh/goodthings-web
nohup npm run dev > /tmp/goodthings-web.log 2>&1 &
echo $! > /tmp/goodthings-web.pid
sleep 8
tail -30 /tmp/goodthings-web.log
```

预期：日志包含 `Local:   http://localhost:5173/`，无 ERR。

- [ ] **Step 3：手动验证（浏览器）**

打开 `http://localhost:5173/`，走一遍：
1. 跳到 `/register`，填 `bob / bob12345 / Bob`，点注册 → 跳到 `/`（Discover），顶栏显示 Bob
2. 退出登录，访问 `/` → 重定向到 `/login`
3. 用 `bob / bob12345` 登录 → 回到 `/`
4. 顶栏点登出 → 回到 `/login`
5. 错误密码登录 → 看到错误信息

- [ ] **Step 4：停服**

```bash
kill $(cat /tmp/goodthings-web.pid) 2>/dev/null
kill $(cat /tmp/goodthings.pid) 2>/dev/null
rm -f /tmp/goodthings-web.pid /tmp/goodthings.pid
```

- [ ] **Step 5：提交（无变更则空提交）**

```bash
cd /mnt/f/zxh/goodthings-web
git status
# 如有 .env.local / dist 之类的无变更，可跳过此步
```

---

## Task 18：部署文档

**Files:**
- Create: `goodthings/docs/deploy/skeleton.md`
- Create: `goodthings-web/docs/deploy/skeleton.md`

- [ ] **Step 1：写 `goodthings/docs/deploy/skeleton.md`**

```markdown
# 阶段 1 后端部署（开发 + 生产草稿）

## 本地开发
1. 准备 MySQL 8，执行 `src/main/resources/db/migration/V1__init.sql`
2. 复制 `.env.example` 为 `.env`，填入 `DB_USER` / `DB_PASSWORD` / `JWT_SECRET`
3. `mvn -DskipTests package`
4. `java -jar target/goodthings-server-3.0.0.jar`

服务监听 `http://localhost:8080`，API 前缀 `/api/v3`。

## 生产 systemd（草稿）
```ini
# /etc/systemd/system/goodthings.service
[Unit]
Description=Goodthings v3 Backend
After=network.target

[Service]
User=goodthings
EnvironmentFile=/etc/goodthings/goodthings.env
ExecStart=/usr/bin/java -jar /opt/goodthings/goodthings-server-3.0.0.jar
Restart=always
RestartSec=3

[Install]
WantedBy=multi-user.target
```

`/etc/goodthings/goodthings.env`：
```
DB_URL=jdbc:mysql://127.0.0.1:3306/goodthings_v3?...
DB_USER=goodthings
DB_PASSWORD=...
JWT_SECRET=请使用 openssl rand -base64 48 生成
CORS_ORIGINS=https://goodthings.example.com
SPRING_PROFILES_ACTIVE=prod
```

## Nginx 反代
```nginx
location /api/ {
  proxy_pass http://127.0.0.1:8080;
  proxy_set_header Host $host;
  proxy_set_header X-Real-IP $remote_addr;
  proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
}
```
```

- [ ] **Step 2：写 `goodthings-web/docs/deploy/skeleton.md`**

```markdown
# 阶段 1 前端部署

## 本地开发
1. `npm install`
2. `npm run dev` → `http://localhost:5173/`
3. Vite dev server 把 `/api/*` 反代到 `http://localhost:8080`（见 `vite.config.ts`）

## 生产构建
1. `npm run build` → 产物在 `dist/`
2. Nginx 静态托管：
```nginx
server {
  listen 443 ssl http2;
  server_name goodthings.example.com;
  root /var/www/goodthings-web/dist;
  index index.html;
  location / { try_files $uri $uri/ /index.html; }
  location /api/ {
    proxy_pass http://127.0.0.1:8080;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
  }
}
```
```

- [ ] **Step 3：提交**

```bash
cd /mnt/f/zxh/goodthings
git add docs/deploy
git commit -m "docs(v3): 阶段 1 部署文档（开发 + 生产草稿）"
cd /mnt/f/zxh/goodthings-web
git add docs/deploy
git commit -m "docs(v3): 阶段 1 部署文档（开发 + 生产草稿）"
```

---

## 收尾

- [ ] **Step 1：把 `rewrite/v3` 分支推到 origin**

```bash
cd /mnt/f/zxh/goodthings
git push origin rewrite/v3
cd /mnt/f/zxh/goodthings-web
git push origin rewrite/v3
```

- [ ] **Step 2：在 GitHub 上发起 PR：`rewrite/v3` → `dev`（可选）**

两个仓库分别走标准 PR 流程，标题：`v3: 阶段 1 骨架 + 鉴权`

---

## 自审（Self-Review）

**1. Spec 覆盖**
- 章节 2 后端架构 → Task 1（pom + 配置）、Task 4（security）、Task 5（config）
- 章节 3 前端架构 → Task 10-15
- 章节 4 数据模型 → Task 2（SQL + entity + mapper）、Task 7（service）
- 章节 4 API → Task 7-8（service + controller）
- 章节 5 错误处理 → Task 3（common）
- 章节 5 安全 → Task 4（JWT filter）、Task 8（RateLimiter）
- 章节 5 部署 → Task 18
- ✅ 无遗漏。

**2. 占位符扫描**
- 无 TBD/TODO/“适当”/“类似 Task N”。所有文件路径、代码、命令均给出。

**3. 类型一致性**
- 后端 `SysUser.id` 为 `Long` → `JwtAuthFilter` 放 `Long userId` → `UserController.me(@AuthenticationPrincipal Long userId)` → `AuthService.me(Long userId)` → `JwtUtil.extractUserId` 返回 `Long`。一致。
- 前端 `UserVO.id` 为 `number` → auth store / LoginVO / Pinia 全部 `UserVO` 类型。一致。
- API 路径：`/auth/register` `/auth/login` `/auth/logout` `/user/me` 与 spec 一致。

**4. 已知遗留 / 后续阶段**
- 阶段 1 `logout` 不做服务端黑名单（客户端清 token 即可）。
- 阶段 1 RateLimiter 是 In-Memory，阶段 2 切 Redis。
- 阶段 1 不含测试；spec 章节 1 已确认。

**5. 风险**
- JJWT 0.12 API 与 0.11 差异较大；Task 4 已用 0.12 新 API（`Jwts.builder()...signWith(key)`、`Jwts.parser().verifyWith(key)`）。
- 移除 Element Plus 后样式全部自写；阶段 1 视图简单可控。
