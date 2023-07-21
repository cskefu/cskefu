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

import org.apache.commons.lang3.StringUtils;

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
