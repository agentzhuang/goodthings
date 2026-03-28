# 须皓的好东西收藏网 - 后端服务

## 项目简介

**须皓的好东西收藏网** 是一个极简画廊风格的图片/视频收藏平台，支持瀑布流展示、深色沉浸式设计。

## 技术栈

| 层级 | 技术 |
|------|------|
| 基础框架 | Spring Boot 2.7.18 |
| ORM | MyBatis-Plus 3.5.5 |
| 数据库 | MySQL 8.0 |
| 安全 | JWT (jjwt 0.11.5) |
| 文件存储 | 阿里云OSS |
| Java版本 | JDK 17+ |

## 项目结构

```yaml
goodthings/
├── pom.xml                          # Maven配置
├── src/main/
│   ├── java/com/goodthings/
│   │   ├── GoodthingsApplication.java   # 启动类
│   │   ├── config/                      # 配置类
│   │   │   ├── CorsConfig.java          # 跨域配置
│   │   │   ├── MyMetaObjectHandler.java # 自动填充
│   │   │   └── OssProperties.java      # OSS配置
│   │   ├── common/                      # 通用类
│   │   │   ├── BizException.java        # 业务异常
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── Result.java              # 统一响应
│   │   ├── controller/                  # 控制器
│   │   │   ├── UserController.java      # 用户模块
│   │   │   ├── ItemController.java      # 收藏品模块
│   │   │   ├── CollectionController.java # 收藏夹模块
│   │   │   ├── TagController.java       # 标签模块
│   │   │   ├── CommentController.java   # 评论模块
│   │   │   └── OssController.java       # 文件上传
│   │   ├── dto/                         # 数据传输对象
│   │   ├── entity/                      # 实体类
│   │   ├── mapper/                      # MyBatis Mapper
│   │   ├── util/                        # 工具类
│   │   └── service/                     # 服务层
│   └── resources/
│       └── application.yml              # 应用配置
```

## 数据库初始化

```bash
# 1. 创建数据库
mysql -u root -p < goodthings-db-v1.0.sql

# 2. 修改 application.yml 中的数据库连接信息
```

## 快速启动

```bash
# 1. 克隆项目
git clone <repo-url>
cd goodthings

# 2. 修改 application.yml 配置
# - 数据库连接
# - Redis配置
# - 阿里云OSS配置
# - JWT密钥

# 3. 编译运行
mvn clean package -DskipTests
java -jar target/goodthings-server-1.0.0.jar

# 或开发模式
mvn spring-boot:run
```

## API 文档

启动服务后访问：`http://localhost:8080/v1`

详细API文档见：`goodthings-api.md`

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+ (可选)

## 开发说明

### 包说明

| 包 | 说明 |
|----|------|
| controller | REST API 控制器 |
| service | 业务逻辑 |
| mapper | MyBatis Mapper 接口 |
| entity | 数据库实体 |
| dto | 数据传输对象 |
| common | 通用类（异常、响应） |
| config | 配置类 |
| util | 工具类 |

### 响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 错误码

| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## License

Private Project - Aether AI
