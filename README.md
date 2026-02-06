# MyBlog 后端项目

## 快速启动

### 1. 环境要求
- JDK 17+
- MySQL 8.0+
- Maven 3.6+

### 2. 数据库配置

创建数据库:
```sql
CREATE DATABASE myblog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

导入初始化脚本: `sql/init.sql`

### 3. 修改配置文件

编辑 `src/main/resources/application-dev.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/myblog?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 4. 启动项目

```bash
# 开发环境启动
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 或打包后启动
mvn clean package -DskipTests
java -jar target/myblog-backend.jar --spring.profiles.active=dev
```

### 5. 访问地址

- API文档: http://localhost:8080/api/doc.html
- 默认账号: admin / admin123

## 项目结构

```
myblog-backend/
├── src/main/java/com/blog/
│   ├── BlogApplication.java       # 启动类
│   ├── config/                    # 配置类
│   ├── controller/               # 控制器
│   ├── service/                  # 服务层
│   ├── mapper/                   # 数据访问层
│   ├── entity/                   # 实体类
│   ├── dto/                      # 数据传输对象
│   ├── common/                    # 公共模块
│   └── security/                  # 安全模块
├── src/main/resources/
│   ├── application.yml           # 主配置
│   ├── application-dev.yml       # 开发环境
│   └── application-prod.yml      # 生产环境
├── uploads/                       # 文件上传目录
└── pom.xml
```

## API 接口

### 认证模块
- `POST /api/auth/login` - 登录
- `POST /api/auth/logout` - 退出登录
- `GET /api/auth/profile` - 获取个人信息
- `PUT /api/auth/profile` - 更新个人信息
- `PUT /api/auth/password` - 修改密码

### 前台博客接口
- `GET /api/blog/info` - 获取博客信息
- `GET /api/blog/articles` - 获取文章列表
- `GET /api/blog/articles/{slug}` - 获取文章详情
- `GET /api/blog/articles/recent` - 获取近期文章
- `GET /api/blog/categories` - 获取所有分类
- `GET /api/blog/tags` - 获取所有标签
- `GET /api/blog/archives` - 获取文章归档
- `GET /api/blog/comments` - 获取评论列表
- `POST /api/blog/comments` - 提交评论
- `POST /api/blog/upload` - 图片上传

### 管理后台接口
- `GET /api/admin/articles` - 获取文章列表
- `POST /api/admin/articles` - 创建文章
- `PUT /api/admin/articles/{id}` - 更新文章
- `DELETE /api/admin/articles/{id}` - 删除文章
- `GET /api/admin/categories` - 获取分类列表
- `POST /api/admin/categories` - 创建分类
- `GET /api/admin/tags` - 获取标签列表
- `POST /api/admin/tags` - 创建标签
- `GET /api/admin/comments` - 获取评论列表
- `PUT /api/admin/comments/{id}/approve` - 审核通过
- `PUT /api/admin/comments/{id}/reject` - 审核拒绝
- `GET /api/admin/dashboard/stats` - 获取仪表盘统计
- `GET /api/admin/settings` - 获取系统设置
- `PUT /api/admin/settings` - 更新系统设置
