USE `cosinee`;
-- -----------------
-- prepare variables
-- -----------------

SET @dbname = DATABASE ( );
SET @tablename = "uk_tenant";

SET @preparedStatement = (
	SELECT
	IF
		(
			(
			SELECT
				COUNT( * ) 
			FROM
				INFORMATION_SCHEMA.TABLES 
			WHERE
				( table_name = @tablename ) 
				AND ( table_schema = @dbname )
			) = 0,
			"SELECT 1",
			CONCAT( "DROP TABLE ", @tablename, ";" ) 
	) 
);

PREPARE dropTableIfExists 
FROM
	@preparedStatement;
EXECUTE dropTableIfExists;
DEALLOCATE PREPARE dropTableIfExists;