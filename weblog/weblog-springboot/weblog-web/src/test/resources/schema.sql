-- Weblog Database Schema for H2 Testing
-- Adapted from MySQL init.sql

-- Drop tables if they exist to ensure clean state for tests
DROP TABLE IF EXISTS t_article;
DROP TABLE IF EXISTS t_article_category_rel;
DROP TABLE IF EXISTS t_article_content;
DROP TABLE IF EXISTS t_article_tag_rel;
DROP TABLE IF EXISTS t_blog_settings;
DROP TABLE IF EXISTS t_category;
DROP TABLE IF EXISTS t_statistics_article_pv;
DROP TABLE IF EXISTS t_tag;
DROP TABLE IF EXISTS t_user;
DROP TABLE IF EXISTS t_user_role;

-- Article Table
CREATE TABLE `t_article` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'Article ID',
  `title` varchar(120) NOT NULL DEFAULT '' COMMENT 'Article Title',
  `cover` varchar(120) NOT NULL DEFAULT '' COMMENT 'Article Cover Image URL',
  `summary` varchar(160) DEFAULT '' COMMENT 'Article Summary',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Last Update Time',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT 'Deletion Flag: 0 for not deleted, 1 for deleted',
  `read_num` int unsigned NOT NULL DEFAULT '1' COMMENT 'Read Count',
  `weight` int unsigned NOT NULL DEFAULT '0' COMMENT 'Article Weight',
  `type` tinyint NOT NULL DEFAULT '1' COMMENT 'Article Type',
  PRIMARY KEY (`id`)
);

-- Article-Category Relation Table
CREATE TABLE `t_article_category_rel` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `article_id` bigint unsigned NOT NULL COMMENT 'Article ID',
  `category_id` bigint unsigned NOT NULL COMMENT 'Category ID',
  PRIMARY KEY (`id`)
);

-- Article Content Table
CREATE TABLE `t_article_content` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'Content ID',
  `article_id` bigint NOT NULL COMMENT 'Article ID',
  `content` text COMMENT 'Article Body Content',
  PRIMARY KEY (`id`)
);

-- Article-Tag Relation Table
CREATE TABLE `t_article_tag_rel` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `article_id` bigint unsigned NOT NULL COMMENT 'Article ID',
  `tag_id` bigint unsigned NOT NULL COMMENT 'Tag ID',
  PRIMARY KEY (`id`)
);

-- Blog Settings Table
CREATE TABLE `t_blog_settings` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `logo` varchar(120) NOT NULL DEFAULT '' COMMENT 'Blog Logo URL',
  `name` varchar(60) NOT NULL DEFAULT '' COMMENT 'Blog Name',
  `author` varchar(20) NOT NULL DEFAULT '' COMMENT 'Author Name',
  `introduction` varchar(120) NOT NULL DEFAULT '' COMMENT 'Introduction Text',
  `avatar` varchar(120) NOT NULL DEFAULT '' COMMENT 'Author Avatar URL',
  `github_homepage` varchar(60) NOT NULL DEFAULT '' COMMENT 'GitHub Homepage URL',
  `csdn_homepage` varchar(60) NOT NULL DEFAULT '' COMMENT 'CSDN Homepage URL',
  `gitee_homepage` varchar(60) NOT NULL DEFAULT '' COMMENT 'Gitee Homepage URL',
  `zhihu_homepage` varchar(60) NOT NULL DEFAULT '' COMMENT 'Zhihu Homepage URL',
  PRIMARY KEY (`id`)
);

-- Article Category Table
CREATE TABLE `t_category` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'Category ID',
  `name` varchar(60) NOT NULL DEFAULT '' COMMENT 'Category Name',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Last Update Time',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT 'Logical Deletion Flag: 0 for not deleted, 1 for deleted',
  `articles_total` int NOT NULL DEFAULT '0' COMMENT 'Total articles under this category',
  PRIMARY KEY (`id`)
);

-- Statistics Table - Article PV (Page View)
CREATE TABLE `t_statistics_article_pv` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `pv_date` date NOT NULL COMMENT 'Date of Statistics',
  `pv_count` bigint unsigned NOT NULL COMMENT 'PV (Page View) Count',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Last Update Time',
  PRIMARY KEY (`id`)
);

-- Article Tag Table
CREATE TABLE `t_tag` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'Tag ID',
  `name` varchar(60) NOT NULL DEFAULT '' COMMENT 'Tag Name',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Last Update Time',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT 'Logical Deletion Flag: 0 for not deleted, 1 for deleted',
  `articles_total` int NOT NULL DEFAULT '0' COMMENT 'Total articles count under this tag',
  PRIMARY KEY (`id`)
);

-- User Table
CREATE TABLE `t_user` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(60) NOT NULL COMMENT 'Username',
  `password` varchar(60) NOT NULL COMMENT 'Password',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Last Update Time',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT 'Deletion Flag: 0 for not deleted, 1 for deleted',
  PRIMARY KEY (`id`)
);

-- User Role Table
CREATE TABLE `t_user_role` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(60) NOT NULL COMMENT 'Username',
  `role` varchar(60) NOT NULL COMMENT 'Role',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
  PRIMARY KEY (`id`)
);

-- Visitor Location Statistics Table
DROP TABLE IF EXISTS t_visitor_location;
CREATE TABLE `t_visitor_location` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `province` varchar(60) NOT NULL DEFAULT '' COMMENT 'Province/State',
  `city` varchar(60) NOT NULL DEFAULT '' COMMENT 'City',
  `country` varchar(60) NOT NULL DEFAULT '' COMMENT 'Country',
  `visit_count` bigint unsigned NOT NULL DEFAULT '1' COMMENT 'Visit count from this location',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'First visit time',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Last update time',
  PRIMARY KEY (`id`),
  UNIQUE (`country`, `province`, `city`)
);

-- Visitor Log Table
DROP TABLE IF EXISTS t_visitor_log;
CREATE TABLE `t_visitor_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `ip_address` varchar(45) NOT NULL DEFAULT '' COMMENT 'Visitor IP address',
  `country` varchar(60) NOT NULL DEFAULT '' COMMENT 'Country',
  `province` varchar(60) NOT NULL DEFAULT '' COMMENT 'Province/State',
  `city` varchar(60) NOT NULL DEFAULT '' COMMENT 'City',
  `article_id` bigint unsigned DEFAULT NULL COMMENT 'Article ID visited',
  `user_agent` varchar(500) DEFAULT '' COMMENT 'Browser user agent',
  `visit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Visit time',
  PRIMARY KEY (`id`)
);

-- Insert default data
INSERT INTO t_user (username, password) VALUES ('admin', '$2a$10$ps.oAyozZ.MZjGw9Nk3vJOEf8xSqM7pM5HOshUTfzRRQk9RxOw3ka');
INSERT INTO t_user_role (username, role) VALUES ('admin', 'ADMIN');
INSERT INTO t_blog_settings (logo, name, author, introduction, avatar, github_homepage, csdn_homepage, gitee_homepage, zhihu_homepage) VALUES ('', 'My Blog', 'Admin', 'Welcome to my blog!', '', '', '', '', '');
