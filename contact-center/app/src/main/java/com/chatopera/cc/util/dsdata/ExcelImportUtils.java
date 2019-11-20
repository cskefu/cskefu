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
package com.chatopera.cc.util.dsdata;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.model.MetadataTable;
import com.chatopera.cc.model.TableProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ExcelImportUtils{
	private DecimalFormat format = new DecimalFormat("###");
	protected DSDataEvent event ;
	
	public ExcelImportUtils(DSDataEvent event){
		this.event = event ;
	}
	
	public MetadataTable processExcel(final DSDataEvent event , String tableTitle){
		MetadataTable metaDataTable = new MetadataTable(); 
		InputStream is = null;  
		boolean findId = false ;
    	try {
    		metaDataTable.setTablename(event.getTablename());
    		metaDataTable.setOrgi(this.event.getOrgi());
    		metaDataTable.setId(MainUtils.md5(event.getTablename()));
    		metaDataTable.setTabledirid("0");
    		metaDataTable.setCreater(event.getDSData().getUser().getId());
    		metaDataTable.setCreatername(event.getDSData().getUser().getUsername());
    		metaDataTable.setName(tableTitle);
    		metaDataTable.setUpdatetime(new Date());
    		metaDataTable.setCreatetime(new Date());
    		metaDataTable.setTableproperty(new ArrayList<TableProperties>());
            try {  
                is = new FileInputStream(event.getDSData().getFile());  
            } catch (FileNotFoundException ex) {  
                ex.printStackTrace();
            }  
            boolean isExcel2003 = true;  
            if (isExcel2007(event.getDSData().getFile().getName())) {  
                isExcel2003 = false;  
            }
            
            Workbook wb = null;  
            try {  
                wb = isExcel2003 ? new HSSFWorkbook(is) : new XSSFWorkbook(is);  
            } catch (IOException ex) {  
                ex.printStackTrace();
            }  
            Sheet sheet = wb.getSheetAt(0);  
            Row titleRow = sheet.getRow(0);
            int totalRows = sheet.getPhysicalNumberOfRows(); 
            int colNum = titleRow.getPhysicalNumberOfCells();
           
            /**
             * 需要检查Mapping 是否存在
             */
            if(totalRows > 1) {
	            Row row = sheet.getRow(0) ;
	        	if(row!=null){
					for(int col=0 ; col<colNum ; col++){
						Cell title = titleRow.getCell(col) ;
						String titleValue = getValue(title) ;
						if(!StringUtils.isBlank(titleValue)) {
							if(titleValue.equalsIgnoreCase("id")) {
								findId = true ;
							}
							TableProperties tp  = initProperties("f"+ MainUtils.genIDByKey(titleValue+"String") , titleValue, "String", event.getOrgi() , event.getTablename() , false) ;
							tp.setViewtype("list,add,edit,detail");
							metaDataTable.getTableproperty().add(tp) ;
						}
					}
	        	}
            }
            if(findId == false) {
				metaDataTable.getTableproperty().add(initProperties("id", "主键", "String", event.getOrgi() , event.getTablename() , true)) ;
			}
			metaDataTable.getTableproperty().add(initProperties("orgi", "租户ID", "String", event.getOrgi() , event.getTablename() , true)) ;
			metaDataTable.getTableproperty().add(initProperties("creater", "创建人", "String", event.getOrgi() , event.getTablename() , true)) ;
			metaDataTable.getTableproperty().add(initProperties("createtime", "创建时间", "Datetime", event.getOrgi() , event.getTablename() , true)) ;
			
			metaDataTable.getTableproperty().add(initProperties("validresult", "数据状态", "String", event.getOrgi() , event.getTablename() , true)) ;
			metaDataTable.getTableproperty().add(initProperties("validmessage", "数据状态", "String", event.getOrgi() , event.getTablename() , true)) ;
			
			metaDataTable.getTableproperty().add(initProperties("assuser", "分配执行人", "String", event.getOrgi() , event.getTablename() , true)) ;
			
			
			metaDataTable.getTableproperty().add(initProperties(Constants.CSKEFU_SYSTEM_DIS_AI, "分配AI", "String", event.getOrgi() , event.getTablename() , true)) ;
			metaDataTable.getTableproperty().add(initProperties(Constants.CSKEFU_SYSTEM_DIS_AGENT, "分配用户", "String", event.getOrgi() , event.getTablename() , true)) ;
			metaDataTable.getTableproperty().add(initProperties(Constants.CSKEFU_SYSTEM_DIS_ORGAN, "分配部门", "String", event.getOrgi() , event.getTablename() , true)) ;
			metaDataTable.getTableproperty().add(initProperties(Constants.CSKEFU_SYSTEM_DIS_TIME, "分配时间", "Datetime", event.getOrgi() , event.getTablename() , true)) ;
			
			metaDataTable.getTableproperty().add(initProperties("status", "状态", "String", event.getOrgi() , event.getTablename() , true)) ;
			
			/**
			 * 机器人/人工
			 */
			metaDataTable.getTableproperty().add(initProperties("process", "处理状态", "String", event.getOrgi() , event.getTablename() , true)) ;
			metaDataTable.getTableproperty().add(initProperties("processtime", "处理时间", "Datetime", event.getOrgi() , event.getTablename() , true)) ;
			metaDataTable.getTableproperty().add(initProperties("processmemo", "处理备注", "String", event.getOrgi() , event.getTablename() , true)) ;
			
			metaDataTable.getTableproperty().add(initProperties("metaid", "元数据", "String", event.getOrgi() , event.getTablename() , true)) ;
			metaDataTable.getTableproperty().add(initProperties("actid", "活动ID", "String", event.getOrgi() , event.getTablename() , true)) ;
			metaDataTable.getTableproperty().add(initProperties("batid", "批次ID", "String", event.getOrgi() , event.getTablename() , true)) ;
			metaDataTable.getTableproperty().add(initProperties("taskid", "任务ID", "String", event.getOrgi() , event.getTablename() , true)) ;
			metaDataTable.getTableproperty().add(initProperties("filterid", "任务ID", "String", event.getOrgi() , event.getTablename() , true)) ;
			metaDataTable.getTableproperty().add(initProperties("cusid", "客户ID", "String", event.getOrgi() , event.getTablename() , true)) ;
			metaDataTable.getTableproperty().add(initProperties("calloutfilid", "筛选记录ID", "String", event.getOrgi() , event.getTablename() , true)) ;
			
			metaDataTable.getTableproperty().add(initProperties("execid", "导入记录ID", "String", event.getOrgi() , event.getTablename() , true)) ;
			
			metaDataTable.getTableproperty().add(initProperties("callstatus", "拨打状态", "String", event.getOrgi() , event.getTablename() , true)) ;
			metaDataTable.getTableproperty().add(initProperties("workstatus", "业务状态", "String", event.getOrgi() , event.getTablename() , true)) ;
			
			metaDataTable.getTableproperty().add(initProperties("apstatus", "是否预约", "String", event.getOrgi() , event.getTablename() , true)) ;
			metaDataTable.getTableproperty().add(initProperties("aptime", "预约时间", "Date", event.getOrgi() , event.getTablename() , true)) ;
			metaDataTable.getTableproperty().add(initProperties("apmemo", "预约备注", "String", event.getOrgi() , event.getTablename() , true)) ;
			
			metaDataTable.getTableproperty().add(initProperties("calltime", "拨打时间", "Date", event.getOrgi() , event.getTablename() , true)) ;
			metaDataTable.getTableproperty().add(initProperties("firstcalltimes", "首次拨打次数", "Date", event.getOrgi() , event.getTablename() , true)) ;
			metaDataTable.getTableproperty().add(initProperties("firstcallstatus", "首次拨打结果", "String", event.getOrgi() , event.getTablename() , true)) ;
			
			metaDataTable.getTableproperty().add(initProperties("calltimes", "拨打次数", "Long", event.getOrgi() , event.getTablename() , true)) ;
			
			
			
			metaDataTable.getTableproperty().add(initProperties("succcall", "拨打成功次数", "Long", event.getOrgi() , event.getTablename() , true)) ;
			metaDataTable.getTableproperty().add(initProperties("faildcall", "拨打失败次数", "Long", event.getOrgi() , event.getTablename() , true)) ;
			
            /**
			 * 映射 Mapping , 已修正，增加了一个手动映射的步骤，上传数据结构以后，允许手动映射
			 */
			//ESTools.mapping(metaDataTable, MainContext.SYSTEM_INDEX);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(event.getDSData().getFile()!=null && event.getDSData().getFile().exists()) {
				event.getDSData().getFile().delete() ;
			}
		}
    	return metaDataTable;
	}
	
	private TableProperties initProperties(String name ,String title, String type ,String orgi ,String tableName , boolean sysfield) {
		TableProperties tablePorperties = new TableProperties(name, type, 255 , tableName) ;
		tablePorperties.setOrgi(orgi) ;
		
		tablePorperties.setDatatypecode(0);
		tablePorperties.setLength(255);
		tablePorperties.setDatatypename(type);
		tablePorperties.setName(title);
		
		tablePorperties.setSysfield(sysfield);
		return tablePorperties;
	}
	
	private boolean isExcel2007(String fileName) {  
        return fileName.matches("^.+\\.(?i)(xlsx)$");  
    } 
	@SuppressWarnings("deprecation")
	private String getValue(Cell cell){
		String strCell = "";
		if(cell!=null){
			short dt = cell.getCellStyle().getDataFormat() ;
	        switch (cell.getCellType()) {
		        case HSSFCell.CELL_TYPE_STRING:
		            strCell = cell.getStringCellValue();
		            break;
		        case HSSFCell.CELL_TYPE_BOOLEAN:
		            strCell = String.valueOf(cell.getBooleanCellValue());
		            break;
		        case HSSFCell.CELL_TYPE_BLANK:
		            strCell = "";
		            break;
		        case HSSFCell.CELL_TYPE_NUMERIC:
		        	if (HSSFDateUtil.isCellDateFormatted(cell)) {
		        		SimpleDateFormat sdf = null;  
		                if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {  
		                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		                } else {// 日期  
		                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		                }  
		                strCell = sdf.format(cell.getDateCellValue());  
		        	} else if (cell.getCellStyle().getDataFormat() == 58) {  
		                // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)  
		                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		                double value = cell.getNumericCellValue();  
		                strCell = sdf.format(org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value));  
		            }else{
		            	
		            	if (HSSFDateUtil.isCellDateFormatted(cell)) {
		            		SimpleDateFormat sdf = null;  
			                if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {  
			                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			                } else {// 日期  
			                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			                }  
			                strCell = sdf.format(cell.getDateCellValue());  
			        	}else{
			            	boolean isNumber = isNumberFormat(dt) ;
			        		if(isNumber){
			        			DecimalFormat numberFormat = getNumberFormat(cell.getCellStyle().getDataFormatString()) ;
			        			if(numberFormat!=null){
			        				strCell = String.valueOf(numberFormat.format(cell.getNumericCellValue()));
			        			}else{
			        				strCell = String.valueOf(cell.getNumericCellValue());
			        			}
			        		}else{
			        			strCell = String.valueOf(format.format(cell.getNumericCellValue())) ;
			        		}
			        	}
	                }
		            break;
		        case HSSFCell.CELL_TYPE_FORMULA: {
	                // 判断当前的cell是否为Date
		        	boolean isNumber = isNumberFormat(dt) ;
		        	try{
		        		if(isNumber){
		        			strCell = String.valueOf(cell.getNumericCellValue());
			        	}else{
			        		strCell = "";
			        	}
		        	}catch(Exception ex){
		        		ex.printStackTrace();
		        		strCell = cell.getRichStringCellValue().getString();  
		        	}
	                break;
	            }
		        default:
		            strCell = "";
		            break;
	        }
	        if (strCell.equals("") || strCell == null) {
	            return "";
	        }
		}
        return strCell;
	}
	
	@SuppressWarnings({ "deprecation", "unused" })
	private String getDataType(Cell cell){
		String dataType = "string";
		if(cell!=null){
			short dt = cell.getCellStyle().getDataFormat() ;
	        switch (cell.getCellType()) {
		        case HSSFCell.CELL_TYPE_STRING:
		        	dataType = "string";
		            break;
		        case HSSFCell.CELL_TYPE_BOOLEAN:
		        	dataType = "number";
		            break;
		        case HSSFCell.CELL_TYPE_BLANK:
		        	if (HSSFDateUtil.isCellDateFormatted(cell)) {
		        		if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {  
		        			dataType = "datetime" ;  
		                } else {// 日期  
		                	dataType = "datetime" ;  
		                }  
		        		
		        	} else if (cell.getCellStyle().getDataFormat() == 58){
		        		dataType = "datetime" ;
		        	}else{
		        		boolean isNumber = isNumberFormat(dt) ;
		        		if(isNumber){
		        			dataType = "number";
		        		}else{
		        			dataType = "string";
		        		}
		        	}
		            break;
		        case HSSFCell.CELL_TYPE_NUMERIC:
		        	if (HSSFDateUtil.isCellDateFormatted(cell)) {
		        		if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {  
		        			dataType = "datetime" ;  
		                } else {// 日期  
		                	dataType = "datetime" ;  
		                }  
		        		
		        	} else if (cell.getCellStyle().getDataFormat() == 58){
		        		dataType = "datetime" ;
		        	}else{
		        		if (HSSFDateUtil.isCellDateFormatted(cell)) {
			        		if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {  
			        			dataType = "datetime" ;  
			                } else {// 日期  
			                	dataType = "datetime" ;  
			                }  
			        	}else{
			        		boolean isNumber = isNumberFormat(dt) ;
			        		if(isNumber){
			        			dataType = "number";
			        		}else{
			        			dataType = "string";
			        		}
			        	}
	                }
		            break;
		        case HSSFCell.CELL_TYPE_FORMULA: {
	                // 判断当前的cell是否为Date
		        	boolean isNumber = isNumberFormat(dt) ;
	        		if(isNumber){
	        			dataType = "number";
	        		}else{
	        			dataType = "string";
	        		}
	                break;
	            }
		        default:
		        	dataType = "string";
		            break;
	        }
	       
		}
        return dataType;
	}
	
	private DecimalFormat getNumberFormat(String dataformat){
		DecimalFormat numberFormat = null ;
		int index = dataformat.indexOf("_") > 0 ?  dataformat.indexOf("_") : dataformat.indexOf(";") ;
		if(index > 0){
			String format = dataformat.substring( 0 , index) ;
			if(format.matches("[\\d.]{1,}")){
				numberFormat = new DecimalFormat(format);
			}
		}
		
		return numberFormat ;
	}
	
	private boolean isNumberFormat(short dataType){
		boolean number = false ;
		switch(dataType){
			case 180 : number = true  ; break; 
			case 181 : number = true  ; break; 
			case 182 : number = true  ; break;
			case 178 : number = true  ; break;
			case 177 : number = true  ; break;
			case 176 : number = true  ; break;
			case 183 : number = true  ; break; 
			case 185 : number = true  ; break; 
			case 186 : number = true  ; break;
			case 179 : number = true  ; break;
			case 187 : number = true  ; break; 
			case 7 : number = true  ; break; 
			case 8 : number = true  ; break; 
			case 44 : number = true  ; break; 
			case 10 : number = true  ; break; 
			case 12 : number = true  ; break; 
			case 13 : number = true  ; break; 
			case 188 : number = true  ; break; 
			case 189 : number = true  ; break; 
			case 190 : number = true  ; break; 
			case 191 : number = true  ; break; 
			case 192 : number = true  ; break; 
			case 193 : number = true  ; break; 
			case 194 : number = true  ; break; 
			case 11 : number = true  ; break; 

		}
		return number ;
	}


}
