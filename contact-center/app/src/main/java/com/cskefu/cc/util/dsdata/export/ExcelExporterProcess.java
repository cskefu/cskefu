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
package com.cskefu.cc.util.dsdata.export;

import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.model.Dict;
import com.cskefu.cc.model.MetadataTable;
import com.cskefu.cc.model.SysDic;
import com.cskefu.cc.model.TableProperties;
import com.cskefu.cc.persistence.interfaces.DataExchangeInterface;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class ExcelExporterProcess {
	private HSSFWorkbook  wb; 
	private Sheet sheet; 
	private CellStyle firstStyle = null ;
	
	private int rowNum ;
	
	private List<Map<String ,Object>> values ;
	private MetadataTable table ;
	private OutputStream output ;
	private Row titleRow ;
	
	public ExcelExporterProcess(List<Map<String ,Object>> values , MetadataTable table , OutputStream output) {
		this.values = values ;
		this.table = table ;
		this.output = output;
		wb = new HSSFWorkbook();
		sheet = wb.createSheet();
		firstStyle = createFirstCellStyle();
		createHead() ;
	}
	public void process() throws IOException{
		createContent();
		if(table!=null){
			for(TableProperties tp : table.getTableproperty()){
				sheet.autoSizeColumn(table.getTableproperty().indexOf(tp)) ;
			}
			wb.write(this.output);
		}
	}
	
	/**
	 * 构建头部
	 */
	private void createHead(){
		titleRow = sheet.createRow(rowNum);
		if(table!=null && table.getTableproperty()!=null){
			for(TableProperties tp : table.getTableproperty()){
				Cell cell2 = titleRow.createCell(table.getTableproperty().indexOf(tp)); 
				cell2.setCellStyle(firstStyle); 
				cell2.setCellValue(new HSSFRichTextString(tp.getName()));
			}
		}
		rowNum ++ ;
	}
	
	private CellStyle createContentStyle(){
		CellStyle cellStyle = wb.createCellStyle(); 
		
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 指定单元格居中对齐 
		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 指定单元格垂直居中对齐 
		cellStyle.setWrapText(false);// 指定单元格自动换行 

		// 设置单元格字体 
		Font font = wb.createFont(); 
		font.setFontName("微软雅黑"); 
		font.setFontHeight((short) 200); 
		cellStyle.setFont(font); 
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		return cellStyle ;
	}
	
	/**
	 * 首列样式
	 * @return
	 */
	private CellStyle createFirstCellStyle(){
		CellStyle cellStyle = baseCellStyle();
		Font font = wb.createFont();
		font.setFontName("微软雅黑"); 
		font.setFontHeight((short) 180);
		cellStyle.setFont(font);
		
		cellStyle.setWrapText(false);
		
		cellStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		
	
		return cellStyle;
	}
	
	
	private synchronized void createContent(){
		CellStyle cellStyle = createContentStyle() ;
		if(table!=null && table.getTableproperty()!=null){
			for(Map<String , Object> value:values){
				Row row2 = sheet.createRow(rowNum);
				List<ExportData> tempExportDatas = new ArrayList<ExportData>();
				int cols = 0 ;
				for(TableProperties tp : table.getTableproperty()){
					Cell cell2 = row2.createCell(cols++); 
					cell2.setCellStyle(cellStyle); 
					if(value.get(tp.getFieldname())!=null){
						if(tp.isModits()) {
							@SuppressWarnings("unchecked")
							List<String> list = (List<String>)value.get(tp.getFieldname());
							if(list.size()>0) {
								cell2.setCellValue(new HSSFRichTextString(list.remove(0)));
							}
							ExportData expData = new ExportData(tp , list) ;
							if(list.size()>0) {
								tempExportDatas.add(expData) ;
								if(list.size() > expData.getMaxcols()) {
									expData.setMaxcols(list.size());
								}
							}
						}else if(tp.isSeldata()){
							SysDic sysDic = Dict.getInstance().getDicItem(String.valueOf(value.get(tp.getFieldname()))) ;
							if(sysDic!=null) {
								cell2.setCellValue(new HSSFRichTextString(sysDic.getName()));
							}else {
								List<SysDic> dicItemList = Dict.getInstance().getSysDic(tp.getSeldatacode());
								if(dicItemList!=null && dicItemList.size() > 0) {
									for(SysDic dicItem : dicItemList) {
										String s = "";
										Object obj = value.get(tp.getFieldname());
										if(obj instanceof Boolean) {
											s = (Boolean)obj?"1":"0";
										}else {
											s= String.valueOf(value.get(tp.getFieldname()));
										}
										if(dicItem.getCode().equals(s)) {
											cell2.setCellValue(new HSSFRichTextString(dicItem.getName())); break ;
										}
									}
								}
							}
						}else if(tp.isReffk() && !StringUtils.isBlank(tp.getReftbid())){
							String key = (String) value.get(tp.getFieldname()) ;
							String orgi = (String) value.get("orgi") ;
							if(!StringUtils.isBlank(key) && !StringUtils.isBlank(orgi)) {
			            		DataExchangeInterface exchange = (DataExchangeInterface) MainContext.getContext().getBean(tp.getReftbid()) ;
			            		Object refvalue = exchange.getDataByIdAndOrgi(key, orgi) ;
			            		if(refvalue!=null) {
			            			cell2.setCellValue(new HSSFRichTextString(refvalue.toString()));
			            		}
							}
						}else{
							cell2.setCellValue(new HSSFRichTextString(String.valueOf(value.get(tp.getFieldname()))));
						}
					}
				}
				if(tempExportDatas.size() > 0) {
					for(ExportData expData : tempExportDatas) {
						for(int i=0 ; i<expData.getMaxcols() ; i++) {
							if(titleRow.getCell(cols + i) == null) {
								Cell title = titleRow.createCell(cols + i); 
								title.setCellStyle(firstStyle); 
								title.setCellValue(new HSSFRichTextString(expData.getTp().getName()));
							}
						}
						
						for(String itemValue : expData.getValues()) {
							Cell cell2 = row2.createCell(cols++);
							cell2.setCellValue(new HSSFRichTextString(itemValue));
						}
					}
				}
				rowNum ++ ;
			}
		}
	}
	
	
	private CellStyle baseCellStyle(){
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); 

		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); 
				
		cellStyle.setWrapText(true);
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		
		Font font = wb.createFont(); 
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
		font.setFontName("宋体"); 
		font.setFontHeight((short) 200); 
		cellStyle.setFont(font); 
		
		return cellStyle;
	}
}
