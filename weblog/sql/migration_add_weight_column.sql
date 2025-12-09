ALTER TABLE t_article ADD COLUMN `weight` int(6) unsigned NOT NULL DEFAULT '0' COMMENT 'Article weight, used for pinning (0: not pinned; >0: pinned, higher weight means higher ranking)';
