USE `cosinee`;
-- -----------------
-- prepare variables
-- -----------------

SET @dbname = DATABASE ( );
SET @tablename = "uk_xiaoe_config";
SET @targettablename = "cs_chatbot_config";

SELECT Count(*)
INTO @exists
FROM information_schema.tables 
WHERE table_schema = @dbname
    AND table_name = @tablename;

SET @query = If(@exists>0,
    CONCAT("RENAME TABLE ", @tablename, " TO ", @targettablename),
    'SELECT \'nothing to rename\' status');

PREPARE stmt FROM @query;

EXECUTE stmt;