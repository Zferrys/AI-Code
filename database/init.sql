-- ========================================
-- AI-Code 智能编程学习平台 数据库建库建表脚本
-- 数据库：aicode_db
-- 字符集：utf8mb4
-- 说明：分阶段执行，每阶段注释标注新增表
-- ========================================

CREATE DATABASE IF NOT EXISTS aicode_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE aicode_db;

-- ========================================
-- 阶段1：用户表 + 系统配置表
-- ========================================

-- 用户账户表
CREATE TABLE `user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `phone` VARCHAR(20) DEFAULT NULL,
  `avatar` VARCHAR(255) DEFAULT '/default.png',
  `role` ENUM('USER','ADMIN') DEFAULT 'USER',
  `status` TINYINT(1) DEFAULT 1,
  `last_login` DATETIME DEFAULT NULL,
  `create_time` DATETIME NOT NULL,
  `update_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 系统配置表
CREATE TABLE `sys_config` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `config_key` VARCHAR(100) NOT NULL,
  `config_value` TEXT NOT NULL,
  `description` VARCHAR(255) DEFAULT NULL,
  `create_time` DATETIME NOT NULL,
  `update_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================
-- 种子数据
-- ========================================

-- 默认管理员（密码：1978738217）
-- ⚠️ 安全警告：首次部署后必须修改默认管理员密码！
--    执行 SQL: UPDATE `user` SET `password` = '{新密码的BCrypt哈希}' WHERE `username` = 'zferry';
INSERT INTO `user` (`username`, `password`, `email`, `phone`, `role`, `status`, `create_time`, `update_time`)
VALUES ('zferry', '$2b$10$hKDTK/0ncXWKdl0nHTm4A.eN0p5cc2HiAs3KIZntFDIO0w3bDUAOy', 'admin@aicode.com', '13800000000', 'ADMIN', 1, NOW(), NOW());

-- 默认系统配置
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`, `create_time`, `update_time`) VALUES
('ai.api.endpoint', 'https://apihub.agnes-ai.com/v1/chat/completions', 'AI API 端点（兼容 OpenAI 协议，默认 Agnes）', NOW(), NOW()),
('ai.api.key', '', 'AI API Key（优先使用此配置，留空则读取环境变量）', NOW(), NOW()),
('ai.api.model', 'deepseek-chat', 'AI 模型名称（如 deepseek-chat、gpt-4o、claude-sonnet 等）', NOW(), NOW()),
('ai.api.timeout', '30000', 'API 超时时间（毫秒）', NOW(), NOW()),
('ai.api.max_retries', '2', 'API 最大重试次数', NOW(), NOW()),
('ai.rate_limit.per_minute', '30', 'AI API 每分钟调用限制', NOW(), NOW()),
('ai.review.prompt', '', '代码审查自定义 Prompt 后缀', NOW(), NOW()),
('ai.qa.prompt', '', 'AI 问答自定义 Prompt 后缀', NOW(), NOW()),
('redis.cache.ttl', '1800', 'Redis 缓存默认过期时间（秒）', NOW(), NOW()),
('site.title', 'AI-Code 智能编程学习平台', '站点标题', NOW(), NOW());

-- ========================================
-- 阶段2：用户扩展信息、标签、用户技术标签
-- ========================================

-- 用户扩展信息表
CREATE TABLE `user_profile` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `real_name` VARCHAR(50) DEFAULT NULL,
  `school` VARCHAR(100) DEFAULT NULL,
  `major` VARCHAR(100) DEFAULT NULL,
  `grade` VARCHAR(20) DEFAULT NULL,
  `bio` VARCHAR(500) DEFAULT NULL,
  `github_url` VARCHAR(255) DEFAULT NULL,
  `experience_level` ENUM('BEGINNER','INTERMEDIATE','ADVANCED') DEFAULT 'BEGINNER',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 标签表
CREATE TABLE `tag` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `category` ENUM('LANGUAGE','FRAMEWORK','DATABASE','TOOL','TOPIC') NOT NULL,
  `color` VARCHAR(7) DEFAULT '#1a56db',
  `icon` VARCHAR(50) DEFAULT NULL,
  `sort_order` INT(3) DEFAULT 0,
  `create_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户技术标签关联表
CREATE TABLE `user_tech_tag` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `tag_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 默认标签数据
INSERT INTO `tag` (`name`, `category`, `color`, `sort_order`, `create_time`) VALUES
('Java', 'LANGUAGE', '#f8981d', 1, NOW()),
('Python', 'LANGUAGE', '#3572a5', 2, NOW()),
('JavaScript', 'LANGUAGE', '#f7df1e', 3, NOW()),
('TypeScript', 'LANGUAGE', '#3178c6', 4, NOW()),
('C++', 'LANGUAGE', '#00599c', 5, NOW()),
('Go', 'LANGUAGE', '#00add8', 6, NOW()),
('Spring Boot', 'FRAMEWORK', '#6db33f', 1, NOW()),
('Spring MVC', 'FRAMEWORK', '#6db33f', 2, NOW()),
('MyBatis', 'FRAMEWORK', '#1a56db', 3, NOW()),
('Vue.js', 'FRAMEWORK', '#4fc08d', 4, NOW()),
('React', 'FRAMEWORK', '#61dafb', 5, NOW()),
('MySQL', 'DATABASE', '#4479a1', 1, NOW()),
('Redis', 'DATABASE', '#dc382d', 2, NOW()),
('PostgreSQL', 'DATABASE', '#336791', 3, NOW()),
('MongoDB', 'DATABASE', '#47a248', 4, NOW()),
('Docker', 'TOOL', '#2496ed', 1, NOW()),
('Git', 'TOOL', '#f05032', 2, NOW()),
('Maven', 'TOOL', '#c71a36', 3, NOW()),
('Linux', 'TOPIC', '#1793d1', 1, NOW()),
('数据结构', 'TOPIC', '#1a56db', 2, NOW()),
('算法', 'TOPIC', '#059669', 3, NOW()),
('设计模式', 'TOPIC', '#7c3aed', 4, NOW()),
('网络编程', 'TOPIC', '#dc2626', 5, NOW()),
('多线程', 'TOPIC', '#d97706', 6, NOW());

-- ========================================
-- 阶段3：代码审查表
-- ========================================

-- 代码审查记录表
CREATE TABLE `code_review` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `language` VARCHAR(30) NOT NULL,
  `code_content` TEXT NOT NULL,
  `review_result` TEXT NOT NULL,
  `quality_score` TINYINT(3) DEFAULT NULL,
  `bug_count` INT(5) DEFAULT 0,
  `suggestion_count` INT(5) DEFAULT 0,
  `status` ENUM('PENDING','PROCESSING','COMPLETED','FAILED') DEFAULT 'PENDING',
  `ai_response_time` INT(5) DEFAULT NULL,
  `is_helpful` TINYINT(1) DEFAULT NULL,
  `create_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 代码审查历史版本表
CREATE TABLE `code_review_history` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `review_id` BIGINT(20) NOT NULL,
  `version` INT(5) DEFAULT 1,
  `code_content` TEXT NOT NULL,
  `review_result` TEXT NOT NULL,
  `ai_response_time` INT(5) DEFAULT NULL,
  `create_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_review_id` (`review_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================
-- 阶段4：问答表
-- ========================================

-- 问答问题表
CREATE TABLE `qa_question` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `content` TEXT NOT NULL,
  `category` VARCHAR(50) NOT NULL,
  `tags` VARCHAR(200) DEFAULT NULL,
  `view_count` INT(10) DEFAULT 0,
  `favorite_count` INT(10) DEFAULT 0,
  `answer_count` INT(5) DEFAULT 0,
  `is_resolved` TINYINT(1) DEFAULT 0,
  `status` ENUM('ACTIVE','CLOSED','DELETED') DEFAULT 'ACTIVE',
  `create_time` DATETIME NOT NULL,
  `update_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 问答回答表
CREATE TABLE `qa_answer` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `question_id` BIGINT(20) NOT NULL,
  `type` ENUM('AI','USER') DEFAULT 'AI',
  `content` TEXT NOT NULL,
  `rating` TINYINT(1) DEFAULT NULL,
  `is_accepted` TINYINT(1) DEFAULT 0,
  `ai_response_time` INT(5) DEFAULT NULL,
  `create_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================
-- 阶段5：学习路径表
-- ========================================

-- 学习路径表
CREATE TABLE `learning_path` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL,
  `description` TEXT NOT NULL,
  `difficulty` ENUM('BEGINNER','INTERMEDIATE','ADVANCED') DEFAULT 'BEGINNER',
  `estimated_days` INT(5) DEFAULT NULL,
  `cover_image` VARCHAR(255) DEFAULT NULL,
  `course_count` INT(5) DEFAULT 0,
  `status` ENUM('DRAFT','PUBLISHED','ARCHIVED') DEFAULT 'DRAFT',
  `create_time` DATETIME NOT NULL,
  `update_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 学习路径课程表
CREATE TABLE `learning_path_course` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `path_id` BIGINT(20) NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `description` TEXT,
  `order_index` INT(3) NOT NULL,
  `content_type` ENUM('ARTICLE','VIDEO','CODING','QUIZ') DEFAULT 'ARTICLE',
  `content_url` VARCHAR(500) DEFAULT NULL,
  `content_markdown` LONGTEXT,
  `estimated_minutes` INT(5) DEFAULT 30,
  `create_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_path_id` (`path_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户学习进度表
CREATE TABLE `user_learning_progress` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `course_id` BIGINT(20) NOT NULL,
  `path_id` BIGINT(20) NOT NULL,
  `status` ENUM('NOT_STARTED','IN_PROGRESS','COMPLETED') DEFAULT 'NOT_STARTED',
  `score` TINYINT(3) DEFAULT NULL,
  `completed_time` DATETIME DEFAULT NULL,
  `create_time` DATETIME NOT NULL,
  `update_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_course_id` (`course_id`),
  INDEX `idx_path_id` (`path_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 内容标签关联表（多态）
CREATE TABLE `content_tag` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `tag_id` BIGINT(20) NOT NULL,
  `content_type` ENUM('COURSE','PATH','QUESTION','REVIEW') NOT NULL,
  `content_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_tag_id` (`tag_id`),
  INDEX `idx_content` (`content_type`, `content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================
-- 阶段6：反馈、收藏、系统日志表
-- ========================================

-- AI 反馈评价表
CREATE TABLE `ai_feedback` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `feedback_type` ENUM('REVIEW','ANSWER','PATH') NOT NULL,
  `target_id` BIGINT(20) NOT NULL,
  `rating` TINYINT(1) NOT NULL,
  `comment` VARCHAR(500) DEFAULT NULL,
  `is_resolved` TINYINT(1) DEFAULT 0,
  `create_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_target` (`feedback_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 收藏表
CREATE TABLE `favorite` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `content_type` ENUM('QUESTION','COURSE','PATH') NOT NULL,
  `content_id` BIGINT(20) NOT NULL,
  `create_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  UNIQUE KEY `uk_user_content` (`user_id`, `content_type`, `content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 系统日志表
CREATE TABLE `sys_log` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) DEFAULT NULL,
  `action_type` VARCHAR(50) NOT NULL,
  `target_type` VARCHAR(50) DEFAULT NULL,
  `target_id` BIGINT(20) DEFAULT NULL,
  `description` VARCHAR(500) DEFAULT NULL,
  `ip_address` VARCHAR(45) DEFAULT NULL,
  `create_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_action_type` (`action_type`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
