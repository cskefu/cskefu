USE `cosinee`;
-- -----------------
-- prepare variables
-- -----------------

SET @dbname = DATABASE ( );
SET @tablename = "uk_consult_invite";
SET @columnname = "whitelist_mode";

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
			CONCAT( "ALTER TABLE ", @tablename, " ADD ", @columnname, " TINYINT ( 4 ) DEFAULT '0' COMMENT '启用白名单';" ) 
	) 
);
PREPARE alterIfNotExists 
FROM
	@preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;