USE `cosinee`;
-- -----------------
-- set snsid as unique
-- -----------------

ALTER TABLE `uk_snsaccount` ADD UNIQUE (`snsid`);