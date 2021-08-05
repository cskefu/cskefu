USE `cosinee`;
-- -----------------
-- prepare variables
-- -----------------

ALTER TABLE `uk_agentuser` MODIFY COLUMN  `sessiontimes` BIGINT(64) NULL DEFAULT NULL COMMENT '会话时长';
