USE `cosinee`;
-- -----------------
-- prepare variables
-- -----------------

CREATE TABLE IF NOT EXISTS `cs_fb_otn` (
	`id` VARCHAR(32) NOT NULL,
	`name` VARCHAR(100) NOT NULL,
	`page_id` VARCHAR(100) NOT NULL,
	`pre_sub_message` VARCHAR(500) NULL DEFAULT NULL,
	`sub_message` VARCHAR(500) NOT NULL,
	`success_message` VARCHAR(500) NULL DEFAULT NULL,
	`otn_message` VARCHAR(1000) NOT NULL,
	`status` VARCHAR(50) NOT NULL,
	`createtime` DATETIME NOT NULL,
	`updatetime` DATETIME NOT NULL,
	`sendtime` DATETIME NULL DEFAULT NULL,
  `sub_num` INT(11) NOT NULL,
	PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='FB OTN';
