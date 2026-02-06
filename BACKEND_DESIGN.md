# MyBlog 后端设计文档

## 1. 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Java** | 17+ | 开发语言 |
| **Spring Boot** | 3.4.x | 后端框架 |
| **Spring Security** | 6.x | 安全框架 (JWT认证) |
| **MyBatis-Plus** | 3.5.x | ORM 框架 |
| **MySQL** | 8.0+ | 数据库 |
| **Lombok** | - | 简化代码 |
| **jjwt** | 0.12.x | JWT 令牌 |
| **Knife4j** | 4.x | API 文档 |

---

## 2. 项目结构

```
myblog-backend/
├── src/main/java/com/blog/
│   ├── BlogApplication.java          # 启动类
│   ├── config/                        # 配置类
│   │   ├── SecurityConfig.java       # Spring Security 配置
│   │   ├── MybatisPlusConfig.java    # MyBatis-Plus 配置
│   │   ├── CorsConfig.java           # 跨域配置
│   │   ├── SwaggerConfig.java        # API 文档配置
│   │   └── JacksonConfig.java        # JSON 配置
│   ├── common/                        # 公共模块
│   │   ├── Result.java               # 统一响应
│   │   ├── Constants.java           # 常量定义
│   │   ├── Exception.java            # 自定义异常
│   │   └── JwtTokenUtil.java        # JWT 工具
│   ├── entity/                        # 实体类
│   │   ├── User.java                 # 管理员用户
│   │   ├── Article.java              # 文章
│   │   ├── Category.java            # 分类
│   │   ├── Tag.java                  # 标签
│   │   ├── Comment.java              # 评论
│   │   ├── ArticleTag.java           # 文章标签关联
│   │   └── BlogInfo.java             # 博客信息配置
│   ├── mapper/                       # Mapper 接口
│   │   ├── UserMapper.java
│   │   ├── ArticleMapper.java
│   │   ├── CategoryMapper.java
│   │   ├── TagMapper.java
│   │   ├── CommentMapper.java
│   │   └── BlogInfoMapper.java
│   ├── service/                       # 服务层
│   │   ├── IService.java             # 业务接口
│   │   ├── impl/
│   │   │   ├── UserServiceImpl.java
│   │   │   ├── ArticleServiceImpl.java
│   │   │   ├── CategoryServiceImpl.java
│   │   │   ├── TagServiceImpl.java
│   │   │   ├── CommentServiceImpl.java
│   │   │   └── BlogInfoServiceImpl.java
│   ├── controller/                    # 控制器
│   │   ├── AuthController.java       # 认证接口
│   │   ├── BlogController.java       # 前台博客接口
│   │   ├── ArticleController.java    # 文章管理接口
│   │   ├── CategoryController.java   # 分类管理接口
│   │   ├── TagController.java        # 标签管理接口
│   │   ├── CommentController.java    # 评论管理接口
│   │   ├── SettingsController.java   # 设置接口
│   │   └── DashboardController.java  # 仪表盘接口
│   ├── dto/                           # 数据传输对象
│   │   ├── LoginDTO.java
│   │   ├── ArticleDTO.java
│   │   ├── CommentDTO.java
│   │   └── PageDTO.java
│   └── security/                      # 安全模块
│       ├── JwtAuthenticationFilter.java
│       └── UserDetailsServiceImpl.java
├── src/main/resources/
│   ├── application.yml               # 配置文件
│   ├── application-dev.yml           # 开发环境配置
│   ├── application-prod.yml          # 生产环境配置
│   └── mapper/                        # XML 映射文件
│       ├── ArticleMapper.xml
│       └── ...
├── uploads/                           # 本地图片存储目录
├── pom.xml
└── README.md
```

---

## 3. 数据库设计

### 3.1 ER 图

```
┌─────────────┐       ┌──────────────────┐       ┌─────────────┐
│    user     │       │    article       │       │   category  │
├─────────────┤       ├──────────────────┤       ├─────────────┤
│ id          │◄──────│ category_id     │       │ id          │
│ username    │       │                  │       │ name        │
│ password    │       └────────┬─────────┘       │ slug        │
│ email       │                │                 │ icon        │
│ avatar      │                │                 │ description │
│ bio         │                ▼                 │ color       │
│ create_time │       ┌──────────────────┐       │ sort        │
└─────────────┘       │  article_tag     │       └─────────────┘
                      ├──────────────────┤
                      │ article_id        │       ┌─────────────┐
                      │ tag_id            │       │    tag      │
                      └──────────────────┘       ├─────────────┤
                                                │ id          │
                      ┌──────────────────┐       │ name        │
                      │    comment       │       │ slug        │
                      ├──────────────────┤       │ color       │
                      │ id               │       │ create_time │
                      │ article_id       │       └─────────────┘
                      │ nickname         │
                      │ email            │       ┌─────────────┐
                      │ content          │       │  blog_info  │
                      │ avatar           │       ├─────────────┤
                      │ images           │       │ id          │
                      │ likes            │       │ key         │
                      │ approved         │       │ value       │
                      │ create_time      │       └─────────────┘
                      └──────────────────┘
```

### 3.2 数据库 SQL

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS myblog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE myblog;

-- 1. 管理员用户表
CREATE TABLE `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `email` VARCHAR(100) COMMENT '邮箱',
    `bio` TEXT COMMENT '个人简介',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员用户表';

-- 2. 分类表
CREATE TABLE `category` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `slug` VARCHAR(50) NOT NULL UNIQUE COMMENT 'URL别名',
    `icon` VARCHAR(10) COMMENT '图标Emoji',
    `description` VARCHAR(200) COMMENT '描述',
    `color` VARCHAR(20) COMMENT '颜色代码',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- 3. 标签表
CREATE TABLE `tag` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
    `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
    `slug` VARCHAR(50) NOT NULL UNIQUE COMMENT 'URL别名',
    `color` VARCHAR(20) COMMENT '颜色代码',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- 4. 文章表
CREATE TABLE `article` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文章ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `slug` VARCHAR(200) NOT NULL UNIQUE COMMENT 'URL别名',
    `excerpt` TEXT COMMENT '摘要',
    `content` LONGTEXT COMMENT 'Markdown内容',
    `cover` VARCHAR(500) COMMENT '封面图URL',
    `category_id` BIGINT COMMENT '分类ID',
    `status` VARCHAR(20) DEFAULT 'draft' COMMENT '状态: draft-草稿, published-已发布, archived-已归档',
    `views` INT DEFAULT 0 COMMENT '阅读量',
    `likes` INT DEFAULT 0 COMMENT '点赞数',
    `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶: 0-否, 1-是',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `publish_time` DATETIME COMMENT '发布时间',
    INDEX `idx_slug` (`slug`),
    INDEX `idx_status` (`status`),
    INDEX `idx_category` (`category_id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- 5. 文章标签关联表
CREATE TABLE `article_tag` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `tag_id` BIGINT NOT NULL COMMENT '标签ID',
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
    INDEX `idx_article_id` (`article_id`),
    INDEX `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- 6. 评论表
CREATE TABLE `comment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    `type` VARCHAR(20) DEFAULT 'article' COMMENT '类型: article-文章, about-关于页',
    `article_id` BIGINT COMMENT '文章ID(article类型)',
    `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
    `email` VARCHAR(100) COMMENT '邮箱',
    `website` VARCHAR(200) COMMENT '网站',
    `avatar` VARCHAR(500) COMMENT '头像URL',
    `content` TEXT NOT NULL COMMENT '内容',
    `images` TEXT COMMENT '评论图片(JSON数组)',
    `likes` INT DEFAULT 0 COMMENT '点赞数',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父评论ID',
    `approved` TINYINT DEFAULT 0 COMMENT '是否审核通过: 0-待审核, 1-通过, 2-拒绝',
    `ip` VARCHAR(50) COMMENT 'IP地址',
    `user_agent` TEXT COMMENT 'User Agent',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_article_id` (`article_id`),
    INDEX `idx_approved` (`approved`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 7. 博客信息配置表
CREATE TABLE `blog_info` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `key` VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    `value` TEXT COMMENT '配置值',
    `description` VARCHAR(200) COMMENT '描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客配置表';

-- 8. 初始化管理员用户 (密码: admin123)
INSERT INTO `user` (`username`, `password`, `nickname`, `email`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '管理员', 'admin@myblog.com');

-- 9. 初始化博客配置
INSERT INTO `blog_info` (`key`, `value`, `description`) VALUES
('blog_title', '我的博客', '博客标题'),
('blog_subtitle', '分享技术与生活', '博客副标题'),
('blog_author', '博主', '作者名称'),
('blog_avatar', '/uploads/avatar/default.png', '作者头像'),
('blog_description', '这是一个使用 Vue3 + Spring Boot 构建的个人博客', '博客描述'),
('blog_location', '中国', '所在地');

-- 10. 初始化分类
INSERT INTO `category` (`name`, `slug`, `icon`, `description`, `color`, `sort`) VALUES
('技术分享', 'tech', '💻', '技术相关的文章', '#1890ff', 1),
('生活随笔', 'life', '🌱', '生活随笔和感悟', '#52c41a', 2),
('学习笔记', 'study', '📚', '学习笔记和总结', '#faad14', 3);

-- 11. 初始化标签
INSERT INTO `tag` (`name`, `slug`, `color`) VALUES
('Vue', 'vue', '#42b883'),
('React', 'react', '#61dafb'),
('JavaScript', 'javascript', '#f7df1e'),
('Java', 'java', '#007396'),
('Spring Boot', 'spring-boot', '#6db33f'),
('Python', 'python', '#3776ab');
```

---

## 4. API 接口设计

### 4.1 认证模块

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/auth/login` | 登录 | 公开 |
| POST | `/api/auth/logout` | 退出登录 | 需要认证 |
| GET | `/api/auth/profile` | 获取个人信息 | 需要认证 |
| PUT | `/api/auth/profile` | 更新个人信息 | 需要认证 |
| PUT | `/api/auth/password` | 修改密码 | 需要认证 |

### 4.2 前台博客接口

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | `/api/blog/info` | 获取博客信息 | - |
| GET | `/api/blog/articles` | 获取文章列表 | page, limit, category, tag, keyword |
| GET | `/api/blog/articles/{slug}` | 获取文章详情 | - |
| GET | `/api/blog/articles/recent` | 获取近期文章 | limit |
| GET | `/api/blog/categories` | 获取所有分类 | - |
| GET | `/api/blog/tags` | 获取所有标签 | - |
| GET | `/api/blog/archives` | 获取文章归档 | - |
| GET | `/api/blog/comments` | 获取评论列表 | type, articleId, page, limit |
| POST | `/api/blog/comments` | 提交评论 | - |
| POST | `/api/blog/comments/{id}/like` | 点赞评论 | - |
| POST | `/api/blog/upload` | 图片上传 | file |

### 4.3 管理后台接口

#### 文章管理
| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | `/api/admin/articles` | 获取文章列表 | page, limit, status, keyword |
| GET | `/api/admin/articles/{id}` | 获取文章详情 | - |
| POST | `/api/admin/articles` | 创建文章 | - |
| PUT | `/api/admin/articles/{id}` | 更新文章 | - |
| DELETE | `/api/admin/articles/{id}` | 删除文章 | - |
| PUT | `/api/admin/articles/{id}/publish` | 发布文章 | - |
| PUT | `/api/admin/articles/{id}/unpublish` | 下架文章 | - |
| PUT | `/api/admin/articles/{id}/top` | 置顶/取消置顶 | - |
| GET | `/api/admin/articles/stats` | 获取文章统计 | - |

#### 分类管理
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/admin/categories` | 获取分类列表 |
| POST | `/api/admin/categories` | 创建分类 |
| PUT | `/api/admin/categories/{id}` | 更新分类 |
| DELETE | `/api/admin/categories/{id}` | 删除分类 |
| PUT | `/api/admin/categories/sort` | 批量排序 |

#### 标签管理
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/admin/tags` | 获取标签列表 |
| POST | `/api/admin/tags` | 创建标签 |
| PUT | `/api/admin/tags/{id}` | 更新标签 |
| DELETE | `/api/admin/tags/{id}` | 删除标签 |

#### 评论管理
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/admin/comments` | 获取评论列表 |
| GET | `/api/admin/comments/pending-count` | 获取待审核数量 |
| PUT | `/api/admin/comments/{id}/approve` | 审核通过 |
| PUT | `/api/admin/comments/{id}/reject` | 审核拒绝 |
| DELETE | `/api/admin/comments/{id}` | 删除评论 |

#### 系统设置
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/admin/settings` | 获取设置 |
| PUT | `/api/admin/settings` | 更新设置 |
| GET | `/api/admin/dashboard/stats` | 获取仪表盘统计 |

---

## 5. 响应格式

### 5.1 统一响应格式

```json
// 成功响应
{
    "code": 200,
    "message": "操作成功",
    "data": {
        // 实际数据
    }
}

// 分页响应
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "records": [],
        "total": 100,
        "page": 1,
        "limit": 10,
        "totalPages": 10
    }
}

// 错误响应
{
    "code": 400,
    "message": "错误信息",
    "data": null
}
```

### 5.2 状态码定义

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

---

## 6. 本地图片存储

### 6.1 存储目录结构

```
uploads/
├── avatar/              # 用户头像
│   └── default.png
├── cover/               # 文章封面
│   ├── 2024/
│   │   ├── 01/
│   │   └── 02/
│   └── ...
├── article/             # 文章内图片
│   ├── 2024/
│   └── ...
└── comment/             # 评论图片
    └── ...
```

### 6.2 上传配置

- 最大文件大小: 10MB
- 支持格式: jpg, jpeg, png, gif, webp
- 文件命名: 时间戳_随机数.扩展名

---

## 7. 安全设计

### 7.1 JWT Token
- 过期时间: 7天
- 密钥: 配置文件中定义
- Payload: userId, username, role

### 7.2 密码加密
- 使用 BCrypt 加密算法

### 7.3 接口安全
- 敏感接口需要认证
- 前台接口可匿名访问
- 后台接口需要 ADMIN 角色

---

## 8. 部署说明

### 8.1 环境要求
- JDK 17+
- MySQL 8.0+
- Maven 3.6+

### 8.2 配置修改
修改 `application-prod.yml` 中的数据库连接信息和文件上传路径。

### 8.3 启动命令
```bash
mvn clean package -DskipTests
java -jar myblog-backend.jar --spring.profiles.active=prod
```
