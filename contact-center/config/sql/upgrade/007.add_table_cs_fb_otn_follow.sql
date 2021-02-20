USE `cosinee`;
-- -----------------
-- prepare variables
-- -----------------

CREATE TABLE IF NOT EXISTS `cs_fb_otn_follow` (
	`id` VARCHAR(32) NOT NULL,
	`page_id` VARCHAR(32) NOT NULL,
	`otn_id` VARCHAR(32) NOT NULL,
	`user_id` VARCHAR(300) NOT NULL,
	`otn_token` VARCHAR(300) NOT NULL,
	`createtime` DATETIME NOT NULL,
	`updatetime` DATETIME NOT NULL,
	`sendtime` DATETIME NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='FB OTN 订阅';
