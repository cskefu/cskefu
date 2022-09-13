/**
 * Licensed to the Rivulet under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     webapps/LICENSE-Rivulet-1.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cskefu.cc.util.metadata;

import java.sql.Connection;
import java.util.List;


/**
 * @author jaddy0302 Rivulet DatabaseMetaDataHandler.java 2010-3-21
 *
 */
public class DatabaseMetaDataHandler{
	/**
	 *
	 * @param database
	 * @return
	 * @throws Exception
	 */
	public static List<UKTableMetaData> getTables(Connection conn) throws Exception{
		List<UKTableMetaData> tables = null ;
		{
			UKDatabaseMetadata rivuDatabase  = null ;
			rivuDatabase = new UKDatabaseMetadata(conn) ;
			tables = rivuDatabase.loadTables(null, null, null, true) ;
		}

		return tables;
	}
	/**
	 *
	 * @param database
	 * @return
	 * @throws Exception
	 */
	public static List<UKTableMetaData> getTables(Connection conn , String tabltableNamePattern) throws Exception{
		List<UKTableMetaData> tables = null ;
		{
			UKDatabaseMetadata rivuDatabase = new UKDatabaseMetadata(conn) ;
			tables = rivuDatabase.loadTables(tabltableNamePattern, null, null, true) ;
		}

		return tables;
	}
	/**
	 *
	 * @param database
	 * @return
	 * @throws Exception
	 */
	public static UKTableMetaData getTable(Connection conn , String tablename) throws Exception{
		UKTableMetaData rivuTableMetaData = null ;
		{
			UKDatabaseMetadata rivuDatabase = new UKDatabaseMetadata(conn) ;
			rivuTableMetaData = rivuDatabase.loadTable(tablename, null, null, true) ;
		}

		return rivuTableMetaData;
	}

}
