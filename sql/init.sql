-- MyBlog 数据库初始化脚本
-- 创建时间: 2024
-- 数据库版本: MySQL 8.0+

-- 创建数据库
CREATE DATABASE IF NOT EXISTS myblog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE myblog;

-- ============================================
-- 1. 管理员用户表
-- ============================================
DROP TABLE IF EXISTS `article_tag`;
DROP TABLE IF EXISTS `comment`;
DROP TABLE IF EXISTS `article`;
DROP TABLE IF EXISTS `tag`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `blog_info`;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `email` VARCHAR(100) COMMENT '邮箱',
    `bio` TEXT COMMENT '个人简介',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员用户表';

-- ============================================
-- 2. 分类表
-- ============================================
CREATE TABLE `category` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `slug` VARCHAR(50) NOT NULL UNIQUE COMMENT 'URL别名',
    `icon` VARCHAR(10) COMMENT '图标Emoji',
    `description` VARCHAR(200) COMMENT '描述',
    `color` VARCHAR(20) COMMENT '颜色代码',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- ============================================
-- 3. 标签表
-- ============================================
CREATE TABLE `tag` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
    `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
    `slug` VARCHAR(50) NOT NULL UNIQUE COMMENT 'URL别名',
    `color` VARCHAR(20) COMMENT '颜色代码',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- ============================================
-- 4. 文章表
-- ============================================
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

-- ============================================
-- 5. 文章标签关联表
-- ============================================
CREATE TABLE `article_tag` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `tag_id` BIGINT NOT NULL COMMENT '标签ID',
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
    INDEX `idx_article_id` (`article_id`),
    INDEX `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- ============================================
-- 6. 评论表
-- ============================================
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

-- ============================================
-- 7. 博客信息配置表
-- ============================================
CREATE TABLE `blog_info` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `config_key` VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    `config_value` TEXT COMMENT '配置值',
    `description` VARCHAR(200) COMMENT '描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客配置表';

-- ============================================
-- 8. 初始化管理员用户
-- 密码: admin123 (BCrypt加密)
-- ============================================
INSERT INTO `user` (`username`, `password`, `nickname`, `email`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '博主', 'admin@myblog.com');

-- ============================================
-- 9. 初始化博客配置
-- ============================================
INSERT INTO `blog_info` (`config_key`, `config_value`, `description`) VALUES
('blog_title', '我的博客', '博客标题'),
('blog_subtitle', '分享技术与生活', '博客副标题'),
('blog_author', '博主', '作者名称'),
('blog_avatar', '/uploads/avatar/default.png', '作者头像'),
('blog_description', '这是一个使用 Vue3 + Spring Boot 构建的个人博客', '博客描述'),
('blog_location', '中国', '所在地'),
('social_github', 'https://github.com/yourusername', 'GitHub链接'),
('social_weibo', 'https://weibo.com/yourusername', '微博链接'),
('social_twitter', '', 'Twitter链接'),
('contact_email', 'admin@myblog.com', '联系邮箱');

-- ============================================
-- 10. 初始化分类
-- ============================================
INSERT INTO `category` (`name`, `slug`, `icon`, `description`, `color`, `sort_order`) VALUES
('技术分享', 'tech', '💻', '技术相关的文章', '#1890ff', 1),
('生活随笔', 'life', '🌱', '生活随笔和感悟', '#52c41a', 2),
('学习笔记', 'study', '📚', '学习笔记和总结', '#faad14', 3),
('项目展示', 'projects', '🚀', '个人项目展示', '#722ed1', 4);

-- ============================================
-- 11. 初始化标签
-- ============================================
INSERT INTO `tag` (`name`, `slug`, `color`) VALUES
('Vue', 'vue', '#42b883'),
('React', 'react', '#61dafb'),
('JavaScript', 'javascript', '#f7df1e'),
('TypeScript', 'typescript', '#3178c6'),
('Java', 'java', '#007396'),
('Spring Boot', 'spring-boot', '#6db33f'),
('Python', 'python', '#3776ab'),
('Node.js', 'nodejs', '#339933'),
('CSS', 'css', '#1572b8'),
('Git', 'git', '#f05032');

-- ============================================
-- 12. 初始化示例文章
-- ============================================
INSERT INTO `article` (`title`, `slug`, `excerpt`, `content`, `cover`, `category_id`, `status`, `views`, `likes`, `is_top`, `publish_time`) VALUES
('欢迎来到我的博客', 'welcome-to-my-blog', '这是我的第一篇博客文章，欢迎大家来访！', '# 欢迎来到我的博客\n\n这是我的个人博客，使用 **Vue3** + **Spring Boot** 构建。\n\n## 技术栈\n\n- 前端: Vue3 + Vite + Pinia\n- 后端: Spring Boot 3 + MyBatis-Plus\n- 数据库: MySQL\n\n## 功能特性\n\n- Markdown 文章编辑\n- 评论系统\n- 分类和标签管理\n- 图片上传\n\n欢迎大家留言交流！', '/uploads/cover/default.png', 1, 'published', 100, 10, 1, NOW()),
('Vue3 组合式 API 入门指南', 'vue3-composition-api-guide', '本文介绍 Vue3 组合式 API 的基本用法和最佳实践。', '# Vue3 组合式 API 入门指南\n\n## 什么是组合式 API？\n\n组合式 API 是一组 API，允许我们使用函数来组织逻辑，而不是按照选项对象来组织逻辑。\n\n## 基本用法\n\n```javascript\nimport { ref, onMounted } from ''vue''\n\nexport default {\n  setup() {\n    const count = ref(0)\n    \n    const increment = () => {\n      count.value++\n    }\n    \n    onMounted(() => {\n      console.log(''组件已挂载'')\n    })\n    \n    return {\n      count,\n      increment\n    }\n  }\n}\n```\n\n## 优点\n\n1. 更好的逻辑复用\n2. 更灵活的代码组织\n3. 更好的类型推断', '/uploads/cover/vue.png', 1, 'published', 500, 25, 0, NOW()),
('Spring Boot 3 新特性介绍', 'spring-boot-3-new-features', '探索 Spring Boot 3 的新特性和改进。', '# Spring Boot 3 新特性介绍\n\n## Spring Boot 3 亮点\n\n### 1. 原生支持 GraalVM\nSpring Boot 3 原生支持 GraalVM，可以将应用编译为原生可执行文件。\n\n### 2. Jakarta EE 10\n升级到 Jakarta EE 10，使用新的命名空间。\n\n### 3. 改进的健康检查\n增强的健康检查机制。\n\n## 配置示例\n\n```yaml\nspring:\n  datasource:\n    url: jdbc:mysql://localhost:3306/blog\n```', '/uploads/cover/spring.png', 1, 'published', 300, 15, 0, NOW());

-- ============================================
-- 13. 初始化示例评论
-- ============================================
INSERT INTO `comment` (`type`, `article_id`, `nickname`, `email`, `content`, `approved`, `create_time`) VALUES
('article', 1, '访客1', 'visitor1@example.com', '博客很棒！', 1, NOW()),
('article', 1, '访客2', 'visitor2@example.com', '期待更多文章！', 1, NOW()),
('about', NULL, '新朋友', 'newfriend@example.com', '关于页面设计得很简洁，喜欢！', 1, NOW());

-- ============================================
-- 14. 初始化示例文章标签关联
-- ============================================
INSERT INTO `article_tag` (`article_id`, `tag_id`) VALUES
(1, 1), (1, 4), (2, 1), (2, 4), (3, 6);
