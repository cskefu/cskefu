-- Enable whitelist for online users
USE `cosinee`;
ALTER TABLE uk_consult_invite add whitelist_mode tinyint(4) DEFAULT '0' COMMENT '启用白名单';