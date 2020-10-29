USE `cosinee`;
-- -----------------
-- prepare variables
-- -----------------

SET @dbname = DATABASE ( );
SET @tablename = "uk_callcenter_pbxhost";
SET @columnname = "apiport";

SET @preparedStatement = (
	SELECT
	IF
		(
			(
			SELECT
				COUNT( * ) 
			FROM
				INFORMATION_SCHEMA.COLUMNS 
			WHERE
				( table_name = @tablename ) 
				AND ( table_schema = @dbname ) 
				AND ( column_name = @columnname ) 
			) > 0,
			"SELECT 1",
			CONCAT( "ALTER TABLE ", @tablename, " ADD ", @columnname, " int(11) NOT NULL COMMENT 'Chatopera Switch API端口';" ) 
	) 
);

PREPARE alterIfNotExists 
FROM
	@preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;
