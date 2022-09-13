/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cskefu.cc.util.metadata;

import org.hibernate.annotations.common.util.StringHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UKDatabaseMetadata{
	private Connection connection ;
	public UKDatabaseMetadata(Connection connection)
			throws SQLException {
		this.connection = connection ;
        meta = connection.getMetaData();
	}
	
	

	private final List<UKTableMetaData> tables = new ArrayList<UKTableMetaData>();
	private DatabaseMetaData meta;
	public Properties properties ;
	private static final String[] TYPES = { "TABLE", "VIEW" };
	/**
	 * 
	 * @return
	 */
	public List<UKTableMetaData> getTables() {
		return this.tables;
	}
	/**
	 * 
	 * @param name
	 * @param schema
	 * @param catalog
	 * @param isQuoted
	 * @return
	 * @throws Exception
	 */
	public List<UKTableMetaData> loadTables(String name, String schema, String catalog,
			boolean isQuoted) throws Exception {
		boolean upcase = false ;
		try {
			if(properties!=null && properties.get("schema")!=null && schema==null){
				schema = properties.get("upcase")!=null?((String)properties.get("schema")).toUpperCase():(String)properties.get("schema") ;
			}
			if(properties!=null && properties.get("upcase")!=null){
				upcase = properties.get("upcase")!=null &&  properties.get("upcase").toString().toLowerCase().equals("true");
			}
			UKTableMetaData table = null;
			Statement statement = null;
			ResultSet rs = null ;
			try {
				if ((isQuoted && meta.storesMixedCaseQuotedIdentifiers())) {
					rs = meta.getTables(catalog, schema, name, TYPES);
				} else if ((isQuoted && meta.storesUpperCaseIdentifiers() && meta.storesUpperCaseQuotedIdentifiers())
						|| (!isQuoted && meta.storesUpperCaseIdentifiers())) {
					rs = meta.getTables(StringHelper.toUpperCase(catalog),
							StringHelper.toUpperCase(schema), StringHelper
									.toUpperCase(name), TYPES);
				} else if ((isQuoted && meta.storesLowerCaseQuotedIdentifiers())
						|| (!isQuoted && meta.storesLowerCaseIdentifiers())) {
					rs = meta.getTables(StringHelper.toLowerCase(catalog),
							StringHelper.toLowerCase(schema), StringHelper
									.toLowerCase(name), TYPES);
				}else if(schema!=null && schema.equals("hive")){
					statement = this.connection.createStatement() ;
					if(properties.get("database")!=null){
						statement.execute("USE "+properties.get("database")) ;
					}
					rs = statement.executeQuery("SHOW TABLES") ;
				} else {
					rs = meta.getTables(catalog, schema, name, TYPES);
				}

				while (rs.next()) {
					String tableName = null ;
					if(schema!=null && schema.equals("hive")){
						tableName = rs.getString("tab_name") ;
					}else{
						tableName = rs.getString("TABLE_NAME");
					}
					
					if(tableName.matches("[\\da-zA-Z_-\u4e00-\u9fa5]+")){
						table = new UKTableMetaData(rs, meta, true , upcase , false , schema);
						tables.add(table);
					}
				}

			}catch(Exception ex){
				ex.printStackTrace();
			} finally {
				if (rs != null){
					rs.close();
				}
				if(statement!=null){
					statement.close();
				}
			}
		} catch (SQLException sqle) {
			throw sqle;
		}
		return tables ;
	}
	/**
	 * 
	 * @param name
	 * @param schema
	 * @param catalog
	 * @param isQuoted
	 * @return
	 * @throws Exception
	 */
	public UKTableMetaData loadTable(String name, String schema, String catalog,
			boolean isQuoted) throws Exception {
		UKTableMetaData table = null;
		boolean upcase = false ;
		try {
			if(properties!=null && properties.get("schema")!=null && schema==null){
				schema = (String)properties.get("schema") ;
			}
			if(properties!=null && properties.get("upcase")!=null){
				upcase = properties.get("upcase")!=null &&  properties.get("upcase").toString().toLowerCase().equals("true");
			}
			ResultSet rs = null;
			try {
				if ((isQuoted && meta.storesMixedCaseQuotedIdentifiers())) {
					rs = meta.getTables(catalog, schema, name, TYPES);
				} else if ((isQuoted && meta.storesUpperCaseQuotedIdentifiers())
						|| (!isQuoted && meta.storesUpperCaseIdentifiers())) {
					rs = meta.getTables(StringHelper.toUpperCase(catalog),
							StringHelper.toUpperCase(schema), StringHelper
									.toUpperCase(name), TYPES);
				} else if ((isQuoted && meta.storesLowerCaseQuotedIdentifiers())
						|| (!isQuoted && meta.storesLowerCaseIdentifiers())) {
					rs = meta.getTables(StringHelper.toLowerCase(catalog),
							StringHelper.toLowerCase(schema), StringHelper
									.toLowerCase(name), TYPES);
				} else {
					rs = meta.getTables(catalog, schema, name, TYPES);
				}

				while (rs.next()) {
					table = new UKTableMetaData(rs, meta, true , upcase , true , schema);
					break ;
				}

			} finally {
				if (rs != null)
					rs.close();
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace() ;
			throw sqle;
		}
		return table ;
	}
	
	/**
	 * 
	 * @param name
	 * @param schema
	 * @param catalog
	 * @param isQuoted
	 * @return
	 * @throws Exception
	 */
	public UKTableMetaData loadSQL(Statement statement ,String datasql, String tableName, String schema, String catalog,
			boolean isQuoted) throws Exception {
		UKTableMetaData table = null;
		if(properties!=null && properties.get("schema")!=null){
			schema = (String)properties.get("schema") ;
		}
		try {
			if(properties!=null && properties.get("schema")!=null && schema==null){
				schema = (String)properties.get("schema") ;
			}
			ResultSet rs = statement.executeQuery(datasql);
			try {
				table = new UKTableMetaData(tableName , schema , catalog , rs.getMetaData() , true);
			}catch(Exception ex){
				ex.printStackTrace() ;
			} finally {
				rs.close() ;
			}
		} catch (SQLException sqle) {
//			sqle.printStackTrace();
			throw sqle;
		}
		return table ;
	}

}
