/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2018- Jun. 2023 Chatopera Inc, <https://www.chatopera.com>,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (C) 2017 优客服-多渠道客服系统,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cskefu.cc.util.metadata;

import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.internal.util.StringHelper;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC table metadata
 * 
 * @author Christoph Sturm
 */
public class UKTableMetaData {

	private final String catalog;
	private final String schema;
	private String name;
	private final List<UKColumnMetadata> columnMetaData = new ArrayList<>();
	private final Map<String, Object> columName = new HashMap<>() ;
	/**
	 * 
	 * @param rs
	 * @param meta
	 * @param extras
	 * @throws SQLException
	 */
	public UKTableMetaData(ResultSet rs, DatabaseMetaData meta, boolean extras, boolean upcase , boolean loadColumns , String dbtype)
			throws SQLException {
		if(dbtype!=null && dbtype.equals("hive")){
			catalog = null;
			schema = null;
			if(upcase){
				name =  rs.getObject("tab_name").toString() ;
			}else{
				name = rs.getObject("tab_name").toString();
			}
		}else{
			catalog = rs.getString("TABLE_CAT");
			schema = rs.getString("TABLE_SCHEM");
			if(upcase){
				name =  rs.getString("TABLE_NAME").toUpperCase() ;
			}else{
				name = rs.getString("TABLE_NAME");
			}
		}
		
		if(loadColumns){
			initColumns(meta , upcase);
		}
	}
	/**
	 * 
	 * @param tablename
	 * @param meta
	 * @param extras
	 * @throws SQLException
	 */
	public UKTableMetaData(String tableName , String tableCatalog , String tableSchema, ResultSetMetaData meta, boolean extras)
			throws SQLException {
		catalog = tableCatalog;
		schema = tableSchema;
		name = tableName;
		initColumns(meta);
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name ;
	}

	public String toString() {
		return "TableMetadata(" + name + ')';
	}

	public List<UKColumnMetadata> getColumnMetadatas() {
		return columnMetaData;
	}

	/**
	 * 
	 * @param rs
	 * @throws SQLException
	 */
	public void addColumn(ResultSet rs , boolean upcase ) throws SQLException {
		String column = rs.getString("COLUMN_NAME");
		if(upcase){
			column = column!=null ? column.toUpperCase() : column ;
		}
		if (column == null)
			return;

		if (columName.get(column) == null) {
			UKColumnMetadata info = new UKColumnMetadata(rs , upcase);
			columnMetaData.add(info) ;
			if(upcase){
				columName.put(info.getName().toUpperCase(),"");
			}else{
				columName.put(info.getName().toLowerCase(),"");
			}
		}
	}
	/**
	 * 
	 * @param rs
	 * @throws SQLException
	 */
	public void addSqlColumn(String name , String typeName , int typeCode , int columSize) throws SQLException {

		if (name == null)
			return;

		if (columName.get(name) == null) {
			UKColumnMetadata info = new UKColumnMetadata(name , typeName , typeCode , columSize);
			columnMetaData.add(info) ;
			columName.put(info.getName().toLowerCase(),"");
		}
	}
	/**
	 * 
	 * @param meta
	 * @throws SQLException
	 */
	private void initColumns(DatabaseMetaData meta , boolean upcase) throws SQLException {
		ResultSet rs = null;

		try {
			if (meta.storesUpperCaseIdentifiers()) {
				rs = meta.getColumns(StringUtils.upperCase(catalog),
						StringUtils.upperCase(schema), StringUtils.upperCase(name), "%");
			} else if (meta.storesLowerCaseIdentifiers()) {
				rs = meta.getColumns(StringUtils.lowerCase(catalog),
						StringUtils.lowerCase(schema), StringUtils.lowerCase(name), "%");
			} else {
				rs = meta.getColumns(catalog, schema, name, "%");
			}
			while (rs.next())
				addColumn(rs , upcase);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally {
			if (rs != null)
				rs.close();
		}
	}
	/**
	 * 
	 * @param meta
	 * @throws SQLException
	 */
	private void initColumns(ResultSetMetaData meta) throws SQLException {
		for(int i=1 ; i<=meta.getColumnCount(); i++){
			Object tbName = meta.getColumnName(i) ;
			if(tbName!=null && !String.valueOf(tbName).toLowerCase().contains("rownum")){
				addSqlColumn(meta.getColumnName(i) , meta.getColumnTypeName(i) , meta.getColumnType(i) , meta.getColumnDisplaySize(i));
			}
		}
	}
	
}
