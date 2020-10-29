USE `cosinee`;
-- -----------------
-- prepare variables
-- -----------------

SET @dbname = DATABASE ( );
SET @tablename = "cs_user";
SET @columnname = "sipaccount";

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
			) = 0,
			"SELECT 1",
			CONCAT( "ALTER TABLE ", @tablename, " DROP COLUMN ", @columnname, ";" ) 
	) 
);

PREPARE removeIfExists 
FROM
	@preparedStatement;
EXECUTE removeIfExists;
DEALLOCATE PREPARE removeIfExists;