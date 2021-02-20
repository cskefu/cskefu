USE `cosinee`;
-- -----------------
-- prepare variables
-- -----------------

CREATE TABLE IF NOT EXISTS `cs_fb_messenger` (
  `id` varchar(32) NOT NULL,
  `page_id` varchar(100) NOT NULL,
  `token` varchar(300) NOT NULL,
  `verify_token` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `status` varchar(100) NOT NULL,
  `organ` varchar(32) NOT NULL,
  `aiid` varchar(32) DEFAULT NULL,
  `ai` tinyint(4) DEFAULT '0' COMMENT '启用AI',
  `aisuggest` tinyint(4) DEFAULT '0' COMMENT '启用智能建议',
  `config` VARCHAR(1000) NULL DEFAULT NULL COMMENT '文案配置',
  `createtime` datetime NOT NULL,
  `updatetime` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='FB渠道';
