-- 创建数据库
CREATE DATABASE IF NOT EXISTS blog_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE blog_system;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(32) NOT NULL COMMENT '密码(MD5加密)',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `role` tinyint NOT NULL DEFAULT '0' COMMENT '角色: 0-普通用户, 1-管理员',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态: 0-正常, 1-冻结',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 空间表
CREATE TABLE IF NOT EXISTS `space` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '空间ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_size` bigint NOT NULL DEFAULT '104857600' COMMENT '总空间大小(字节), 默认100MB',
  `used_size` bigint NOT NULL DEFAULT '0' COMMENT '已用空间大小(字节)',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态: 0-正常, 1-冻结',
  `total_downloads` bigint NOT NULL DEFAULT '0' COMMENT '总下载次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_space_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户空间表';

-- 文件信息表
CREATE TABLE IF NOT EXISTS `file_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `space_id` bigint NOT NULL COMMENT '空间ID',
  `file_name` varchar(255) NOT NULL COMMENT '存储文件名',
  `original_name` varchar(255) NOT NULL COMMENT '原始文件名',
  `file_path` varchar(500) NOT NULL COMMENT '文件存储路径',
  `content_type` varchar(100) DEFAULT NULL COMMENT '文件MIME类型',
  `file_size` bigint NOT NULL COMMENT '文件大小(字节)',
  `category` varchar(50) DEFAULT 'other' COMMENT '一级分类',
  `sub_category` varchar(50) DEFAULT NULL COMMENT '二级分类',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态: 0-正常, 1-冻结',
  `download_count` bigint NOT NULL DEFAULT '0' COMMENT '下载次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_space_id` (`space_id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_file_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_file_space` FOREIGN KEY (`space_id`) REFERENCES `space` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件信息表';

-- 扩容申请表
CREATE TABLE IF NOT EXISTS `space_apply` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `space_id` bigint NOT NULL COMMENT '空间ID',
  `requested_size` bigint NOT NULL COMMENT '申请扩容大小(字节)',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态: 0-待审核, 1-通过, 2-驳回',
  `reason` text COMMENT '申请理由',
  `review_comment` text COMMENT '审核意见',
  `review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `reviewer_id` bigint DEFAULT NULL COMMENT '审核人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_space_id` (`space_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_apply_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_apply_space` FOREIGN KEY (`space_id`) REFERENCES `space` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_apply_reviewer` FOREIGN KEY (`reviewer_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='空间扩容申请表';

-- 关注表
CREATE TABLE IF NOT EXISTS `follow` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关注ID',
  `follower_id` bigint NOT NULL COMMENT '关注者ID',
  `following_id` bigint NOT NULL COMMENT '被关注者ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follow` (`follower_id`, `following_id`),
  KEY `idx_follower` (`follower_id`),
  KEY `idx_following` (`following_id`),
  CONSTRAINT `fk_follow_follower` FOREIGN KEY (`follower_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_follow_following` FOREIGN KEY (`following_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注关系表';

-- 通知表
CREATE TABLE IF NOT EXISTS `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '接收通知的用户ID',
  `from_user_id` bigint DEFAULT NULL COMMENT '触发通知的用户ID',
  `title` varchar(100) NOT NULL COMMENT '通知标题',
  `content` text COMMENT '通知内容',
  `type` varchar(50) DEFAULT NULL COMMENT '通知类型: follow, file_upload等',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态: 0-未读, 1-已读',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_from_user_id` (`from_user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_type` (`type`),
  CONSTRAINT `fk_notification_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_notification_from_user` FOREIGN KEY (`from_user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- 插入默认管理员用户 (密码: admin123)
INSERT INTO `user` (`username`, `password`, `email`, `nickname`, `role`, `status`) 
VALUES ('admin', '0192023a7bbd73250516f069df18b500', 'admin@example.com', '系统管理员', 1, 0)
ON DUPLICATE KEY UPDATE `username` = `username`;

-- 为管理员创建空间
INSERT INTO `space` (`user_id`, `total_size`, `used_size`, `status`, `total_downloads`)
SELECT `id`, 1073741824, 0, 0, 0 FROM `user` WHERE `username` = 'admin'
ON DUPLICATE KEY UPDATE `user_id` = `user_id`;