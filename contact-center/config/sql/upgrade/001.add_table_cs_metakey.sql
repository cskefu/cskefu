USE `cosinee`;
-- -----------------
-- prepare variables
-- -----------------

CREATE TABLE IF NOT EXISTS `cs_metakey` (
  `metakey` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '元数据字段名，唯一标识',
  `metavalue` text COLLATE utf8mb4_unicode_ci COMMENT '元数据值',
  `createtime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updatetime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `datatype` varchar(256) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'string' COMMENT '数据类型',
  `comment` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '字段备注描述',
  PRIMARY KEY (`metakey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统内置元数据';
