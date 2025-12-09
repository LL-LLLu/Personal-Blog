-- Create Wiki Table
CREATE TABLE `t_wiki` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(120) NOT NULL DEFAULT '' COMMENT 'Title',
  `cover` varchar(120) NOT NULL DEFAULT '' COMMENT 'Cover Image',
  `summary` varchar(160) DEFAULT '' COMMENT 'Summary',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Update Time',
  `is_deleted` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Deleted Flag: 0: No 1: Yes',
  `weight` int(6) unsigned NOT NULL DEFAULT '0' COMMENT 'Weight for Pinning (0: Not Pinned; >0: Pinned, higher is top)',
  `is_publish` tinyint(2) NOT NULL DEFAULT '1' COMMENT 'Publish Status: 0: Unpublished 1: Published',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='Wiki Knowledge Base Table';

-- Create Wiki Catalog Table
CREATE TABLE `t_wiki_catalog` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `wiki_id` bigint(20) unsigned NOT NULL COMMENT 'Wiki ID',
  `article_id` bigint(20) unsigned DEFAULT NULL COMMENT 'Article ID',
  `title` text NOT NULL COMMENT 'Title',
  `level` tinyint(2) NOT NULL DEFAULT '1' COMMENT 'Catalog Level',
  `parent_id` bigint(20) unsigned DEFAULT NULL COMMENT 'Parent Catalog ID',
  `sort` tinyint(2) unsigned NOT NULL DEFAULT '1' COMMENT 'Sort Order',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create Time',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Update Time',
  `is_deleted` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'Deleted Flag: 0: No 1: Yes',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_article_id` (`article_id`) USING BTREE,
  KEY `idx_sort` (`sort`) USING BTREE,
  KEY `idx_wiki_id` (`wiki_id`) USING BTREE,
  KEY `idx_parent_id` (`parent_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='Wiki Catalog Table';

-- Add 'type' column to Article Table
ALTER TABLE `t_article` ADD COLUMN `type` tinyint(2) NOT NULL DEFAULT '1' COMMENT 'Article Type - 1: Normal Article, 2: Wiki Article';
