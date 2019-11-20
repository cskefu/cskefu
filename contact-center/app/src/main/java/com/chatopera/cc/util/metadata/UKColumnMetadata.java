/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.util.metadata;

import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

/**
 * @author iceworld
 *
 */
public class UKColumnMetadata{
	private boolean pk = false;
	private String name;
	private String title ;
    private String typeName;
    private int columnSize;
    private int decimalDigits;
    private String isNullable;
    private int typeCode;

    UKColumnMetadata(ResultSet rs , boolean upcase) throws SQLException {
            name = rs.getString("COLUMN_NAME");
            if(upcase){
            	name = name!=null ? name.toUpperCase() : name  ;
            }
            columnSize = rs.getInt("COLUMN_SIZE");
            decimalDigits = rs.getInt("DECIMAL_DIGITS");
            isNullable = rs.getString("IS_NULLABLE");
            typeCode = rs.getInt("DATA_TYPE");
            StringTokenizer typeNameStr = new StringTokenizer( rs.getString("TYPE_NAME"), "() " ) ;
            if(typeNameStr.hasMoreTokens()){
            	typeName = typeNameStr.nextToken();
            }
            this.title = rs.getString("REMARKS") ;
            if(StringUtils.isBlank(title)){
            	this.title = this.name ;
            }
    }
    
    UKColumnMetadata(String name , String typeName , int typeCode , int colunmSize) throws SQLException {
        this.name = name ;
        this.typeCode = typeCode;
        this.typeName = typeName;
        this.columnSize = colunmSize ;
}

    public String getName() {
            return name;
    }

    public String getTypeName() {
            return typeName;
    }

    public int getColumnSize() {
            return columnSize;
    }

    public int getDecimalDigits() {
            return decimalDigits;
    }

    public String getNullable() {
            return isNullable;
    }

    public String toString() {
            return "ColumnMetadata(" + name + ')';
    }

    public int getTypeCode() {
            return typeCode;
    }

	public boolean isPk() {
		return pk;
	}

	public void setPk(boolean pk) {
		this.pk = pk;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
