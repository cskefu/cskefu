/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// You must accept the terms of that agreement to use this software.
//
// Copyright (C) 2012-2012 Pentaho
// All Rights Reserved.
*/
package mondrian.spi.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import javax.sql.DataSource;

import com.chatopera.cc.app.basic.MainUtils;

import mondrian.rolap.RolapUtil;
import mondrian.rolap.SqlStatement;
import mondrian.server.Execution;
import mondrian.server.Locus;
import mondrian.spi.Dialect;
import mondrian.spi.StatisticsProvider;

/**
 * Implementation of {@link mondrian.spi.StatisticsProvider} that generates
 * SQL queries to count rows and distinct values.
 */
public class SqlStatisticsProvider implements StatisticsProvider {
    public int getTableCardinality(
        Dialect dialect,
        DataSource dataSource,
        String catalog,
        String schema,
        String table,
        Execution execution)
    {
        StringBuilder buf = new StringBuilder("select count(*) from ");
        dialect.quoteIdentifier(buf, catalog, schema, table);
        final String sql = buf.toString();
        SqlStatement stmt =
            RolapUtil.executeQuery(
                dataSource,
                sql,
                new Locus(
                    execution,
                    "SqlStatisticsProvider.getTableCardinality",
                    "Reading row count from table "
                    + Arrays.asList(catalog, schema, table)));
        try {
            ResultSet resultSet = stmt.getResultSet();
            if (resultSet.next()) {
                ++stmt.rowCount;
                return resultSet.getInt(1);
            }
            return -1; // huh?
        } catch (SQLException e) {
            throw stmt.handle(e);
        } finally {
            stmt.close();
        }
    }

    public int getQueryCardinality(
        Dialect dialect,
        DataSource dataSource,
        String sql,
        Execution execution)
    {
        final StringBuilder buf = new StringBuilder();
        buf.append(
            "select count(*) from (").append(sql).append(")");
        if (dialect.requiresAliasForFromQuery()) {
            if (dialect.allowsAs()) {
                buf.append(" as ");
            } else {
                buf.append(" ");
            }
            dialect.quoteIdentifier(buf, "init");
        }
        final String countSql = buf.toString();
        SqlStatement stmt =
            RolapUtil.executeQuery(
                dataSource,
                countSql,
                new Locus(
                    execution,
                    "SqlStatisticsProvider.getQueryCardinality",
                    "Reading row count from query [" + sql + "]"));
        try {
            ResultSet resultSet = stmt.getResultSet();
            if (resultSet.next()) {
                ++stmt.rowCount;
                return resultSet.getInt(1);
            }
            return -1; // huh?
        } catch (SQLException e) {
            throw stmt.handle(e);
        } finally {
            stmt.close();
        }
    }

    public int getColumnCardinality(
        Dialect dialect,
        DataSource dataSource,
        String catalog,
        String schema,
        String table,
        String column,
        Execution execution)
    {
        final String sql =
            generateColumnCardinalitySql(
                dialect, schema, table, column);
        if (sql == null) {
            return -1;
        }
        String sqlId = MainUtils.md5(sql);
    	SqlStatement stmt =
    			RolapUtil.executeQuery(
    					dataSource,
    					sql,
    					new Locus(
    							execution,
    							"SqlStatisticsProvider.getColumnCardinality",
    							"Reading cardinality for column "
    									+ Arrays.asList(catalog, schema, table, column)));
    	try {
    		ResultSet resultSet = stmt.getResultSet();
    		if (resultSet.next()) {
    			++stmt.rowCount;
    			int value = resultSet.getInt(1) ;
    			return value;
    		}
    		return -1; // huh?
    	} catch (SQLException e) {
    		throw stmt.handle(e);
    	} finally {
    		stmt.close();
    	}
    }

    private static String generateColumnCardinalitySql(
        Dialect dialect,
        String schema,
        String table,
        String column)
    {
        final StringBuilder buf = new StringBuilder();
        String exprString = dialect.quoteIdentifier(column);
        if (dialect.allowsCountDistinct()) {
            // e.g. "select count(distinct product_id) from product"
            buf.append("select count(distinct ")
                .append(exprString)
                .append(") from ");
            dialect.quoteIdentifier(buf, schema, table);
            return buf.toString();
        } else if (dialect.allowsFromQuery()) {
            // Some databases (e.g. Access) don't like 'count(distinct)',
            // so use, e.g., "select count(*) from (select distinct
            // product_id from product)"
            buf.append("select count(*) from (select distinct ")
                .append(exprString)
                .append(" from ");
            dialect.quoteIdentifier(buf, schema, table);
            buf.append(")");
            if (dialect.requiresAliasForFromQuery()) {
                if (dialect.allowsAs()) {
                    buf.append(" as ");
                } else {
                    buf.append(' ');
                }
                dialect.quoteIdentifier(buf, "init");
            }
            return buf.toString();
        } else {
            // Cannot compute cardinality: this database neither supports COUNT
            // DISTINCT nor SELECT in the FROM clause.
            return null;
        }
    }
}

// End SqlStatisticsProvider.java
