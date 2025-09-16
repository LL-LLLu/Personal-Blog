-- Weblog Database Schema
-- Generated from existing MySQL database

CREATE DATABASE IF NOT EXISTS weblog CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE weblog;

SET FOREIGN_KEY_CHECKS=0;

-- Article Table
CREATE TABLE `t_article` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'Article ID',
  `title` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'Article Title',
  `cover` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'Article Cover Image URL',
  `summary` varchar(160) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'Article Summary',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Last Update Time',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT 'Deletion Flag: 0 for not deleted, 1 for deleted',
  `read_num` int unsigned NOT NULL DEFAULT '1' COMMENT 'Read Count',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Article Table';

-- Article-Category Relation Table
CREATE TABLE `t_article_category_rel` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `article_id` bigint unsigned NOT NULL COMMENT 'Article ID',
  `category_id` bigint unsigned NOT NULL COMMENT 'Category ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uni_article_id` (`article_id`) USING BTREE,
  KEY `idx_category_id` (`category_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Article-Category Relation Table';

-- Article Content Table
CREATE TABLE `t_article_content` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'Content ID',
  `article_id` bigint NOT NULL COMMENT 'Article ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'Article Body Content',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_article_id` (`article_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Article Content Table';

-- Article-Tag Relation Table
CREATE TABLE `t_article_tag_rel` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `article_id` bigint unsigned NOT NULL COMMENT 'Article ID',
  `tag_id` bigint unsigned NOT NULL COMMENT 'Tag ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_article_id` (`article_id`) USING BTREE,
  KEY `idx_tag_id` (`tag_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Article-Tag Relation Table';

-- Blog Settings Table
CREATE TABLE `t_blog_settings` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `logo` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'Blog Logo URL',
  `name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'Blog Name',
  `author` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'Author Name',
  `introduction` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'Introduction Text',
  `avatar` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'Author Avatar URL',
  `github_homepage` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'GitHub Homepage URL',
  `csdn_homepage` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'CSDN Homepage URL',
  `gitee_homepage` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'Gitee Homepage URL',
  `zhihu_homepage` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'Zhihu Homepage URL',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Blog Settings Table';

-- Article Category Table
CREATE TABLE `t_category` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'Category ID',
  `name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'Category Name',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Last Update Time',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT 'Logical Deletion Flag: 0 for not deleted, 1 for deleted',
  `articles_total` int NOT NULL DEFAULT '0' COMMENT 'Total articles under this category',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_name` (`name`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Article Category Table';

-- Statistics Table - Article PV (Page View)
CREATE TABLE `t_statistics_article_pv` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `pv_date` date NOT NULL COMMENT 'Date of Statistics',
  `pv_count` bigint unsigned NOT NULL COMMENT 'PV (Page View) Count',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Last Update Time',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_pv_date` (`pv_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Statistics Table - Article PV (Page View)';

-- Article Tag Table
CREATE TABLE `t_tag` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'Tag ID',
  `name` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT 'Tag Name',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Last Update Time',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT 'Logical Deletion Flag: 0 for not deleted, 1 for deleted',
  `articles_total` int NOT NULL DEFAULT '0' COMMENT 'Total articles count under this tag',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_name` (`name`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='Article Tag Table';

-- User Table
CREATE TABLE `t_user` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Username',
  `password` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Password',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Last Update Time',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT 'Deletion Flag: 0 for not deleted, 1 for deleted',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  KEY `idx_username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='User Table';

-- User Role Table
CREATE TABLE `t_user_role` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Username',
  `role` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Role',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='User Role Table';

SET FOREIGN_KEY_CHECKS=1;

-- Insert default admin user (password is hashed for 123456)
INSERT INTO t_user (username, password) VALUES ('admin', '$2a$10$ps.oAyozZ.MZjGw9Nk3vJOEf8xSqM7pM5HOshUTfzRRQk9RxOw3ka');
INSERT INTO t_user_role (username, role) VALUES ('admin', 'ADMIN');

-- Insert default blog settings
INSERT INTO t_blog_settings (logo, name, author, introduction, avatar, github_homepage, csdn_homepage, gitee_homepage, zhihu_homepage) 
VALUES ('', 'My Blog', 'Admin', 'Welcome to my blog!', '', '', '', '', '');