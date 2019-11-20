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
package com.chatopera.cc.persistence.repository;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.model.ColumnProperties;
import com.chatopera.cc.util.bi.CubeReportData;
import com.chatopera.cc.util.bi.model.FirstTitle;
import com.chatopera.cc.util.bi.model.Level;
import com.chatopera.cc.util.bi.model.ValueData;
import freemarker.template.TemplateException;
import mondrian.olap.*;
import mondrian.rolap.RolapCubeLevel;
import mondrian.rolap.RolapLevel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

public class CubeService {
	private DataSourceService dataSource ;
	
	private String SCHEMA_DATA_PATH = "WEB-INF/data/mdx/";
	private File schemaFile = null ;
	
	
	public CubeService(String xml , String path , DataSourceService dataSource , Map<String,Object> requestValues) throws IOException, TemplateException {
		this.dataSource = dataSource ;
		File mdxFileDir = new File(path , "mdx") ;
		if(!mdxFileDir.exists()){
			mdxFileDir.mkdirs() ;
		}
		schemaFile = new File(mdxFileDir , MainUtils.getUUID()+".xml") ;
		StringWriter writer = new StringWriter();
		IOUtils.copy(CubeService.class.getClassLoader().getResourceAsStream(SCHEMA_DATA_PATH+xml), writer, "UTF-8"); 
		FileUtils.write(schemaFile, MainUtils.getTemplet(writer.toString(), requestValues) , "UTF-8");	//使用系统默认编码
	}
	
	public CubeService(String xml , String path , DataSourceService dataSource , Map<String,Object> requestValues,boolean isContentStr) throws IOException, TemplateException {
		this.dataSource = dataSource ;
		File mdxFileDir = new File(path , "mdx") ;
		if(!mdxFileDir.exists()){
			mdxFileDir.mkdirs() ;
		}
		schemaFile = new File(mdxFileDir , MainUtils.getUUID()+".xml") ;
		if(isContentStr) {
			FileUtils.write(schemaFile, MainUtils.getTemplet(xml, requestValues) , "UTF-8");	//使用系统默认编码
		}else {
			StringWriter writer = new StringWriter();
			IOUtils.copy(CubeService.class.getClassLoader().getResourceAsStream(SCHEMA_DATA_PATH+xml), writer, "UTF-8"); 
			FileUtils.write(schemaFile, MainUtils.getTemplet(writer.toString(), requestValues) , "UTF-8");	//使用系统默认编码
		}
	}
	
	public CubeReportData execute(String mdx) throws Exception{
		return execute(mdx , null) ;
	}
	
	@SuppressWarnings("deprecation")
	public CubeReportData execute(String mdx,List<ColumnProperties> cols) throws Exception{
		Connection connection = null ;
		CubeReportData cubeReportData = new CubeReportData();
		try{
			connection = dataSource.service(schemaFile.getAbsolutePath()) ;
			Query query = connection.parseQuery(mdx);
			Result result = connection.execute(query) ;
			Axis[] axises = result.getAxes();
			cubeReportData.setData(new ArrayList<List<ValueData>>());
			for (int i = 0; i < axises.length; i++) {
				if (i == 0) {
					cubeReportData.setCol(createTitle(axises[i], i , cols));
				} else {
					cubeReportData.setRow(createTitle(axises[i], i , cols));
//					cubeReportData.setTotal(axises[i].getDataSize());
				}
			}
			if(cubeReportData.getRow()==null){
				cubeReportData.setRow(new Level("root","row", null , 0)) ;
				cubeReportData.getRow().setTitle(new ArrayList<List<Level>>());
				if(cubeReportData.getRow().getTitle().size()==0){
					List<Level> rowList = new ArrayList<Level>() ;
					rowList.add(new Level("合计","row", null , 0)) ;
					cubeReportData.getRow().getTitle().add(rowList) ;
				}
			}
			getRowData(result.getAxes(), result.getAxes().length - 1, new int[result.getAxes().length], result, cubeReportData.getData(), 0 , null , cubeReportData , cols);
			processSum(cubeReportData.getRow(), cubeReportData.getData() , cubeReportData.getRow()) ;
			processSum(cubeReportData.getCol(), cubeReportData.getData() , cubeReportData.getCol()) ;
			cubeReportData.getRow().setTitle(new ArrayList<List<Level>>()) ;
			processTitle(cubeReportData.getRow() , cubeReportData.getRow());
			
		}catch(Exception ex){ 
			throw ex;
		}finally{
			if(connection!=null){
				connection.close();
			}
			if(schemaFile.exists()){
				schemaFile.delete();
			}
		}
		return cubeReportData ;
	}
	
	@SuppressWarnings("rawtypes")
	public Level createTitle(Axis axis, int index,List<ColumnProperties> cols) {
		Level level = new Level("root", index == 0 ? "col" : "row" , null , 0);
		Map<String, Map> valueMap = new HashMap<String, Map>();
		List<Position> posList = axis.getPositions();
		List<String> valueStr = new ArrayList<String>();
		List<FirstTitle> firstTitle = new ArrayList<FirstTitle>();
		for (Position pos : posList) {
			StringBuffer strb = new StringBuffer();
			for (int i = 0; i < pos.size(); i++) {
				Member member = pos.get(i);
				RolapLevel cubeLevel = (RolapLevel) member.getLevel();
				int n = 0;
				if(member.getLevel() instanceof RolapCubeLevel && cubeLevel.getName().indexOf("All")<0){
					if(level.getFirstTitle()==null){
						level.setFirstTitle(firstTitle);
						FirstTitle first = new FirstTitle();
						first.setName(cubeLevel.getName());
						first.setLevel(cubeLevel.getUniqueName()) ;
						addFirstTitle(level.getFirstTitle(), -1 , first) ;
						while((cubeLevel = (RolapLevel) cubeLevel.getParentLevel())!=null && cubeLevel.getName().indexOf("All")<0){
							n++;
							FirstTitle first2 = new FirstTitle();
							first2.setName(cubeLevel.getName());
							first2.setLevel(cubeLevel.getUniqueName()) ;
							if(level.getFirstTitle().size()>firstTitle.size()-i){
								addFirstTitle(level.getFirstTitle(),  firstTitle.size()-n, first2) ;
							}else{
								addFirstTitle(level.getFirstTitle() , 0 , first2) ;
							}
						}
					}else{
						boolean isHave = false;
						for(FirstTitle fr : level.getFirstTitle()){
							if(fr.getLevel().equals(cubeLevel.getUniqueName())){
								isHave = true;
								break;
							}
						}
						if(!isHave){
							FirstTitle first = new FirstTitle();
							first.setName(cubeLevel.getName());
							first.setLevel(cubeLevel.getUniqueName()) ;
							addFirstTitle(level.getFirstTitle(), -1 , first ) ;
							while((cubeLevel = (RolapLevel) cubeLevel.getParentLevel())!=null && cubeLevel.getName().indexOf("All")<0){
								n++;
								FirstTitle first2 = new FirstTitle();
								first2.setName(cubeLevel.getName());
								first2.setLevel(cubeLevel.getUniqueName()) ;
								if(level.getFirstTitle().size()>firstTitle.size()-i){
									addFirstTitle(level.getFirstTitle() , firstTitle.size()-n, first2) ;
								}else{
									addFirstTitle(level.getFirstTitle() ,0,  first2) ;
								}
								
							}
						}
					}
				}else if(member.getLevel() instanceof RolapLevel && cubeLevel.getName().equals("MeasuresLevel")){	//指标列
					if(level.getFirstTitle()==null){
						level.setFirstTitle(firstTitle);
						FirstTitle first = new FirstTitle();
						first.setName(Constants.CUBE_TITLE_MEASURE);
						first.setLevel(cubeLevel.getUniqueName()) ;
						addFirstTitle(level.getFirstTitle(), -1 , first) ;
						while((cubeLevel = (RolapLevel) cubeLevel.getParentLevel())!=null && cubeLevel.getName().indexOf("All")<0){
							n++;
							FirstTitle first2 = new FirstTitle();
							first2.setName(cubeLevel.getName());
							first2.setLevel(cubeLevel.getUniqueName()) ;
							if(level.getFirstTitle().size()>firstTitle.size()-i){
								addFirstTitle(level.getFirstTitle(),  firstTitle.size()-n, first2) ;
							}else{
								addFirstTitle(level.getFirstTitle() , 0 , first2) ;
							}
						}
					}else{
						boolean isHave = false;
						for(FirstTitle fr : level.getFirstTitle()){
							if(fr.getLevel().equals(cubeLevel.getUniqueName())){
								isHave = true;
								break;
							}
						}
						if(!isHave){
							FirstTitle first = new FirstTitle();
							first.setName(Constants.CUBE_TITLE_MEASURE);
							first.setLevel(cubeLevel.getUniqueName()) ;
							addFirstTitle(level.getFirstTitle(), -1 , first ) ;
							while((cubeLevel = (RolapLevel) cubeLevel.getParentLevel())!=null && cubeLevel.getName().indexOf("All")<0){
								n++;
								FirstTitle first2 = new FirstTitle();
								first2.setName(cubeLevel.getName());
								first2.setLevel(cubeLevel.getUniqueName()) ;
								if(level.getFirstTitle().size()>firstTitle.size()-i){
									addFirstTitle(level.getFirstTitle() , firstTitle.size()-n, first2) ;
								}else{
									addFirstTitle(level.getFirstTitle() ,0,  first2) ;
								}
								
							}
						}
					}
				}
				
		
				if (strb.length() > 0) {
					strb.append("l__HHHH-A-HHHH__l");
				}
				strb.append(member.getUniqueName().substring(member.getUniqueName().indexOf(".")+1).replaceAll("\\.\\[\\]", "______R3_SPACE").replaceAll("\\]\\.\\[", "l__HHHH-A-HHHH__l").replaceAll("[\\]\\[]", ""));
			}
			if(cols!=null) {
				for(ColumnProperties col : cols) {
					if(strb.toString().equals(col.getDataname())) {
						strb = new StringBuffer() ;
						strb.append(col.getTitle()) ;
					}
				}
			}
			valueStr.add(strb.toString().replace("#null", " "));//替换掉所有的#null为空字符串
		}
		int depth = 0 ;
		for (int inx = 0 ; inx< valueStr.size() ; inx++) {
			String value = valueStr.get(inx) ;
			Level currentlevel = level;
			String[] levels = value.replaceAll("Measures______", "").split("l__HHHH-A-HHHH__l");
			if(levels.length>depth){
				depth = levels.length-1 ;
			}
			for (int i = 0 ; i < levels.length; i++) {
				boolean found = false;
				if (currentlevel.getChilderen() == null) {
					currentlevel.setChilderen(new ArrayList<Level>());
				}
				if(!levels[i].equals("R3_SPACE")){
					for (Level lv : currentlevel.getChilderen()) {
						if (levels[i].equals(lv.getName())) {
							currentlevel = lv;
							found = true;
							break;
						}
					}
					if (!found) {
						currentlevel.getChilderen().add(currentlevel = new Level(levels[i], level.getLeveltype() , currentlevel , levels.length - i , inx));
						if(i == levels.length-1){
							if(currentlevel.getChilderen()==null){
								currentlevel.setChilderen(new ArrayList<Level>()) ;
							}
							currentlevel.setIndex(inx) ;
							currentlevel.getChilderen().add(new Level("R3_TOTAL" , level.getLeveltype() , currentlevel , levels.length - i , inx));
						}
						if(level.getFirstTitle()!=null && level.getFirstTitle().size()>i){
							currentlevel.setDimname(level.getFirstTitle().get(i).getName()) ;
						}
					}else{
						if(i == levels.length-1){
							if(currentlevel.getChilderen()==null){
								currentlevel.setChilderen(new ArrayList<Level>()) ;
							}
							currentlevel.setIndex(inx) ;
							currentlevel.getChilderen().add(new Level("R3_TOTAL" , level.getLeveltype() , currentlevel , levels.length - i , inx));
						}
					}
				}else{
					currentlevel.getChilderen().add(currentlevel = new Level("", level.getLeveltype() , currentlevel , levels.length - i , inx));
					if(i == levels.length-1){
						if(currentlevel.getChilderen()==null){
							currentlevel.setChilderen(new ArrayList<Level>()) ;
						}
						currentlevel.setIndex(inx) ;
						currentlevel.getChilderen().add(new Level("R3_TOTAL" , level.getLeveltype() , currentlevel , levels.length - i , inx));
					}
					if(level.getFirstTitle()!=null && level.getFirstTitle().size()>i){
						currentlevel.setDimname(level.getFirstTitle().get(i).getName()) ;
					}
				}
			}
		}

		iterator(valueMap, level, level.getLeveltype());
		level.setDepth(depth) ;
		for(Level temp : level.getChilderen()){
			temp.setParent(level) ;
		}
		sumRowspanColspan(level);
		level.init() ;	//格式化
		return level;
	}
	
	/**
	 * 
	 * @param firstTitleList
	 * @param title
	 */
	private void addFirstTitle(List<FirstTitle> firstTitleList , int index , FirstTitle title){
		boolean found = false ;
		for(FirstTitle firstTitle : firstTitleList){
			if(firstTitle.getLevel().equals(title.getLevel())){
				found = true; 
				break ;
			}
		}
		if(!found){
			if(index<0){
				firstTitleList.add(title) ;
			}else{
				firstTitleList.add(index , title) ;
			}
		}
	}
	
	/**
	 * 
	 * @param axes
	 * @param axis
	 * @param pos
	 * @param result
	 * @param dataList
	 * @param rowno
	 */
	private void getRowData(Axis[] axes, int axis, int[] pos, Result result, List<List<ValueData>> dataList, int rowno , Position position , CubeReportData cubeData , List<ColumnProperties> cols) {
		if (axis < 0) {
			if (dataList.size() <= rowno || dataList.get(rowno) == null) {
				dataList.add(new ArrayList<ValueData>());
			}
			Cell cell = result.getCell(pos) ;
			ValueData valueData  = new ValueData(cell.getValue(), cell.getFormattedValue(), null  , cell.canDrillThrough(), cell.getDrillThroughSQL(true) , position!=null && position.size()>0 ? position.get(position.size()-1).getName():"" , cell.getCachedFormatString() , cols) ;
			int rows = 0 ;
			valueData.setRow(getParentLevel(cubeData.getRow() , rowno, rows)) ;
			dataList.get(rowno).add(valueData);
		} else {
			Axis _axis = axes[axis];
			List<Position> positions = _axis.getPositions();
			for (int i = 0; i < positions.size(); i++) {
				Position posit = positions.get(i) ;
				pos[axis] = i;
				if (axis == 0) {
					int row = axis + 1 < pos.length ? pos[axis + 1] : 0;
					rowno = row;
				}
				getRowData(axes, axis - 1, pos, result, dataList, rowno , posit , cubeData , cols);
			}
		}
	}
	
	private Level getParentLevel(Level level , int rowno , int rows){
		if(level!=null && level.getChilderen()!=null){
			for(Level lv : level.getChilderen()){
				rows = rows + lv.getRowspan() ;
				if(rows==rowno){
					while(level.getChilderen()!=null && level.getChilderen().size()>0){
						level = level.getChilderen().get(level.getChilderen().size()-1) ;
					}
				}
				if(rows>rowno){
					rows = rows - lv.getRowspan() ;
					return getParentLevel(lv , rowno , rows) ;
				}
			}
		}
		return level ;
	}
	/**
	 * 处理 合计字段
	 * @param level
	 */
	private void processSum(Level level , List<List<ValueData>> valueDataList , Level root){
		for(int i=0 ; level.getChilderen()!=null && i<level.getChilderen().size() ; i++){
			Level child = level.getChilderen().get(i) ;
//			child.setIndex(i) ;
			if(child.getName().equals("R3_TOTAL") && !child.isTotal() && level.getIndex() < valueDataList.size()){
				child.setName("合计");
				child.setTotal(true) ;
				child.setFirst(i==0) ;
				if(level.getChilderen().size()>1 && level.getParent()!=null){
					child.setValueData(valueDataList.get(level.getIndex())) ;
					if(level.getParent().getChilderen().size()>0){
//						for(int inx = 0 ; inx < level.getParent().getChilderen().size() ; inx++){
//							if(level.getParent().getChilderen().get(inx).getName().equals(child.getParent().getName())){
////								child.setParent(child.getParent().getParent()) ;
////								level.getParent().getChilderen().add(inx+1 , child);
//								break ;
//							}
//						}
						child.setDepth(getDepth(child)) ;
						child.setColspan(root.getFirstTitle().size() - child.getDepth()) ;
//						level.getChilderen().remove(i--) ;
					}
					
				}else{
					if(valueDataList.size()>child.getIndex()){
						level.setValueData(valueDataList.get(level.getIndex())) ;
					}
					level.setChilderen(null) ;
				}
			}else{
				processSum(child , valueDataList , root) ;
			}
		}
	}
	private int getDepth(Level level){
		int depth = 0 ;
		while((level = level.getParent())!=null){
			depth++ ;
		}
		return depth - 1 ;
	}
	
	/*
	 * 
	 */
	private void processTitle(Level level , Level root){
		for(int i=0 ; level.getChilderen()!=null && i<level.getChilderen().size() ; i++){
			Level child = level.getChilderen().get(i) ;
			int depth = getDepth(child) ;
			if(depth>=0){
				if(root.getTitle().size()<=depth){
					root.getTitle().add(new ArrayList<Level>()) ;
				}
				Level tempLevel = new Level(child.getName() , child.getNameValue() , child.getLeveltype() , child.getRowspan() , child.getColspan() , child.getValueData() , child.isTotal() , child.isFirst()) ;
				tempLevel.setParent(child) ;
				root.getTitle().get(depth).add(tempLevel)  ;
			}
			if((child.getNameValue().equals("R3_TOTAL") && root.getFirstTitle()!=null && (depth+1) < root.getFirstTitle().size()) || (child.getChilderen()==null && root.getFirstTitle()!=null && (depth+1) < root.getFirstTitle().size())){
				child.setChilderen(new ArrayList<Level>()) ;
				child.setColspan(root.getFirstTitle().size() - depth);
				if(root.getTitle()!=null && root.getTitle().size()>depth){
					for(Level title : root.getTitle().get(depth)){
						if(title.getName().equals(child.getName())){
							title.setColspan(child.getColspan()) ;
						}
					}
				}
				Level tempLevel = new Level("TOTAL_TEMP" , "R3_TOTAL" , child.getLeveltype() ,child.getRowspan() ,child.getColspan(), child.getValueData() , child.isTotal() , child.isFirst() , child.getDepth()+1) ;
				tempLevel.setParent(child) ;
				child.getChilderen().add(tempLevel) ;
				child.setValue(null) ;
			}
			processTitle(child , root);
		}
	}
	/**
	 * 处理行列合并单元格
	 * @param level
	 */
	private void sumRowspanColspan(Level level) {
		if(level.getChilderen()!=null){
			for(int i=0 ; level.getChilderen()!=null && i<level.getChilderen().size() ; i++){
				Level lv = level.getChilderen().get(i) ;
				if(lv.getColspan()==0 && lv.getRowspan()==0){
					sumRowspanColspan(lv);
				}
				if(level.getLeveltype().equals("col")){
					level.setColspan(level.getColspan()+lv.getColspan()) ;
				}else{
					level.setRowspan(level.getRowspan()+lv.getRowspan()) ;
				}
			}
		}else{
			if(level.getLeveltype().equals("col")){
				level.setColspan(1) ;
			}else{
				level.setRowspan(1) ;
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void iterator(Map<String, Map> value, Level level, String leveltype) {
		Iterator<String> iterator = value.keySet().iterator();
		if (level.getChilderen() == null) {
			level.setChilderen(new ArrayList<Level>());
		}
		while (iterator.hasNext()) {
			String name = iterator.next();
			Level sublevel = new Level(name, leveltype , level , level.getDepth()-1);
			level.getChilderen().add(sublevel);
			if (value.get(name) != null) {
				iterator(value.get(name), sublevel, leveltype);
			}
		}
	}
}
