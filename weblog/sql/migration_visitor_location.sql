-- Visitor Location Statistics Table
-- Stores aggregated visitor counts by location

CREATE TABLE IF NOT EXISTS `t_visitor_location` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `province` varchar(60) NOT NULL DEFAULT '' COMMENT 'Province/State',
  `city` varchar(60) NOT NULL DEFAULT '' COMMENT 'City',
  `country` varchar(60) NOT NULL DEFAULT '' COMMENT 'Country',
  `visit_count` bigint unsigned NOT NULL DEFAULT '1' COMMENT 'Visit count from this location',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'First visit time',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_location` (`country`, `province`, `city`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Visitor location statistics';

-- Individual visit log table (optional, for detailed tracking)
CREATE TABLE IF NOT EXISTS `t_visitor_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `ip_address` varchar(45) NOT NULL DEFAULT '' COMMENT 'Visitor IP address',
  `country` varchar(60) NOT NULL DEFAULT '' COMMENT 'Country',
  `province` varchar(60) NOT NULL DEFAULT '' COMMENT 'Province/State',
  `city` varchar(60) NOT NULL DEFAULT '' COMMENT 'City',
  `article_id` bigint unsigned DEFAULT NULL COMMENT 'Article ID visited',
  `user_agent` varchar(500) DEFAULT '' COMMENT 'Browser user agent',
  `visit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Visit time',
  PRIMARY KEY (`id`),
  KEY `idx_visit_time` (`visit_time`),
  KEY `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Visitor log';
