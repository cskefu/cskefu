USE `cosinee`;
-- -----------------
-- prepare variables
-- -----------------

ALTER TABLE uk_callcenter_pbxhost MODIFY COLUMN `webrtcport` int(11) NOT NULL COMMENT 'WebRTC端口';
