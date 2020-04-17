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
package com.chatopera.cc.util.bi;

import com.chatopera.cc.util.bi.model.FirstTitle;
import com.chatopera.cc.util.bi.model.Level;
import com.chatopera.cc.util.bi.model.ValueData;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 导出Excel
 *
 * @author Jason Chen
 */
public class UKExcelUtil {
    private final static int ROW_LIMIT = 100;
    int page = 1;
    private ReportData reportData;
    private SXSSFWorkbook wb;
    private Sheet sheet;
    private int rowNum = 0;
    private int rowTitleNum = 0;
    private int cellNumber = 0;        //总列数
    private String headTitle = "报表";
    private String startTime = "";
    private String endTime = "";
    private CellStyle firstStyle = null;
    private CellStyle style = null;
    private CellStyle datastyle = null;
    private OutputStream out;

    public UKExcelUtil() {
        super();
        wb = new SXSSFWorkbook(ROW_LIMIT);
        firstStyle = createFirstCellStyle();
        style = createContentCellStyle();
        datastyle = createContentCellStyle();
    }

    public UKExcelUtil(ReportData reportData, OutputStream out, String title) {
        this.reportData = reportData;
        this.out = out;
        this.headTitle = title;
        init();
    }

    /**
     * 初始化部分信息
     */
    private void init() {
        wb = new SXSSFWorkbook(ROW_LIMIT);
        sheet = wb.createSheet();

        firstStyle = createFirstCellStyle();
        style = createContentCellStyle();
        datastyle = createContentCellStyle();
    }

    public void createFile() throws IOException {
        this.createCellNumber();
        this.createHead();
        this.createSubHead();
        this.createTitle();
        this.createContent();
        this.outputExcel();
    }


    /**
     * 计算总列数
     */
    private void createCellNumber() {
        if (reportData.getRow() != null) {
            if (reportData.getRow().getFirstTitle() != null) {
                rowTitleNum = reportData.getRow().getTitle().size();
            }
        }
        cellNumber = reportData.getCol().getColspan() + rowTitleNum;    //总列数
        getnewCol();
        sheet.setColumnWidth(0, 8000);
        for (int i = 1; i < cellNumber; i++) {
            sheet.setColumnWidth(i, 5000);
        }
    }

    private void getnewCol() {
        List<List<Level>> title = reportData.getCol().getTitle();
        if (title != null) {
            for (List<Level> levelList : title) {
                for (Level level : levelList) {
                    if ("newcol".equals(level.getLeveltype())) {
                        cellNumber++;
                    }
                }
            }
        }
    }

    /**
     * 构建头部
     */
    @SuppressWarnings("deprecation")
    private void createHead() {
        Row row = sheet.createRow(rowNum);

        // 设置第一行
        Cell cell = row.createCell(0);
        row.setHeight((short) 1100);

        // 定义单元格为字符串类型
        cell.setCellType(CellType.STRING);
        cell.setCellValue(new XSSFRichTextString(this.headTitle));

        // 指定合并区域
        if (rowNum > 0) {
            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, (cellNumber - 1)));
        }

        CellStyle cellStyle = wb.createCellStyle();

        cellStyle.setAlignment(HorizontalAlignment.CENTER); // 指定单元格居中对齐
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 指定单元格垂直居中对齐
        cellStyle.setWrapText(true);// 指定单元格自动换行

        // 设置单元格字体
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontName("宋体");
        font.setFontHeight((short) 400);
        cellStyle.setFont(font);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cell.setCellStyle(cellStyle);
        for (int i = 1; i < this.cellNumber; i++) {
            Cell cell3 = row.createCell(i);
            cell3.setCellStyle(cellStyle);
        }
        rowNum++;
    }

    /**
     * 构建副标题
     */
    @SuppressWarnings("deprecation")
    private void createSubHead() {

        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER); // 指定单元格居中对齐
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 指定单元格垂直居中对齐
        cellStyle.setWrapText(true);// 指定单元格自动换行

        // 设置单元格字体
        Font font = wb.createFont();
        //font.setBold(true);
        font.setFontName("宋体");
        font.setFontHeight((short) 180);
        cellStyle.setFont(font);

        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);


        CellStyle leftStyle = wb.createCellStyle();
        leftStyle.setAlignment(HorizontalAlignment.LEFT); // 指定单元格居中对齐
        leftStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 指定单元格垂直居中对齐
        leftStyle.setWrapText(true);// 指定单元格自动换行
        leftStyle.setFont(font);

        leftStyle.setBorderTop(BorderStyle.THIN);
        leftStyle.setBorderLeft(BorderStyle.THIN);
        leftStyle.setBorderRight(BorderStyle.THIN);
        leftStyle.setBorderBottom(BorderStyle.THIN);

        Row row1 = sheet.createRow(rowNum);
        row1.setHeight((short) 440);


        //		if(false){	//增加 过滤器
//
//		}else{
//
//		}

        Cell cell2 = row1.createCell(0);
        cell2.setCellType(CellType.STRING);
        cell2.setCellValue(new XSSFRichTextString("报表生成日期:" + getNowDate()));
        cell2.setCellStyle(leftStyle);
        // 指定合并区域
        if (rowNum > 1) {
            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, (cellNumber - 1)));
        }

        for (int i = 1; i < this.cellNumber; i++) {
            Cell cell3 = row1.createCell(i);
            cell3.setCellStyle(cellStyle);
        }
        rowNum++;
    }

    /**
     * 构建标题
     */
    private void createTitle() {
        List<List<Level>> title = reportData.getCol().getTitle();
        if (title != null) {
//			HSSFRow row = sheet.createRow(rowNum);
//			row.setHeight((short)480);
//
//			rowNum ++;
            CellStyle titleStyle = createTitleCellStyle();
            {
                int rowinx = 0;
                int rowTitleSize = reportData.getRow() != null ? reportData.getRow().getTitle().size() : 0;
                for (List<Level> levelList : title) {
                    Row titleRow = sheet.createRow(rowNum);
                    //第一个空表
                    int i = 0;
                    if (title.size() > 1 && rowTitleSize > 0 && rowinx == 0) {
                        Cell cell = titleRow.createCell(0);
                        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + reportData.getCol().getTitle().size() - 2, 0, (rowTitleSize - 1)));
                        cell.setCellValue(new HSSFRichTextString(""));
                        cell.setCellStyle(titleStyle);
                    }
                    if (rowinx + 1 == reportData.getCol().getTitle().size()) {
                        int firstTitleNo = 0;
                        if (reportData.getRow() != null && reportData.getRow().getFirstTitle() != null) {
                            for (FirstTitle firstTitle : reportData.getRow().getFirstTitle()) {
                                Cell blankcell = titleRow.createCell(firstTitleNo++);
                                blankcell.setCellStyle(titleStyle);
                                blankcell.setCellValue(new XSSFRichTextString(firstTitle.getName()));
                            }
                        }
                    }
                    for (Level level : levelList) {
                        Cell cell = titleRow.createCell(rowTitleSize + i);
                        if ("newcol".equals(level.getLeveltype())) {

                            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum + reportData.getCol().getTitle().size() - 1, (rowTitleSize + i), (rowTitleSize + i)));
                        }
                        if (level.getColspan() > 1) {
                            for (int rowinxno = 0; rowinxno < level.getColspan(); rowinxno++) {
                                Cell blankcell = titleRow.createCell(rowTitleSize + i + rowinxno);
                                blankcell.setCellStyle(titleStyle);
                                blankcell.setCellValue(new HSSFRichTextString(level.getName()));
                            }
                            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, (rowTitleSize + i), (rowTitleSize + i + level.getColspan() - 1)));
                        }
                        cell.setCellValue(new XSSFRichTextString(level.getName()));
                        cell.setCellStyle(titleStyle);
                        i = i + level.getColspan();
                    }
                    rowinx++;
                    rowNum++;
                }
            }
        }
    }

    /**
     * 构建内容
     */
    private synchronized void createContent() {

        StringBuilder sbRol = new StringBuilder();
        StringBuilder sbCol = new StringBuilder();
        StringBuilder sbDataCol = new StringBuilder();
        StringBuilder sbDataRol = new StringBuilder();
        if (reportData.getRow() != null) {
            List<List<Level>> rowlst = reportData.getRow().getTitle();
            List<List<ValueData>> dataList = reportData.getData();

            for (int r = 0; dataList != null && r < dataList.size(); r++) {

                if (rowNum > 1048575) {//超过excel上限新建sheet
                    sheet = wb.createSheet();
                    rowNum = 0;
                    this.createCellNumber();
                    this.createHead();
                    this.createSubHead();
                    this.createTitle();

                }
                int cellNum = 0;
                Row row2 = sheet.createRow(rowNum);
                row2.setHeight((short) 420);
                Cell cell2 = row2.createCell(0);

                cell2.setCellStyle(firstStyle);
                //weidu
                for (List<Level> levels : rowlst) {
                    int rowspan = 0;
                    Cell cell3 = row2.createCell(cellNum);
                    Level currentLevel = null;
                    for (Level level : levels) {
                        if (rowspan == r) {
                            currentLevel = level;
                            break;
                        }
                        rowspan = rowspan + (level.getRowspan() > 0 ? level.getRowspan() : 1);

                    }
                    if (currentLevel != null) {
                        String value = String.valueOf(currentLevel.getName());
                        if (!"".equals(value)) {
                            if (value.contains("%")) {
                                value = value.replace("%", "");
                                if (checkIsNumber(value)) {
                                    cell3.setCellValue(Double.parseDouble(value) / 100);
                                    datastyle.setDataFormat(wb.createDataFormat().getFormat("0.00%"));
                                    cell3.setCellStyle(datastyle);
                                } else {
//										style.setDataFormat(wb.createDataFormat().getFormat(""));
                                    cell3.setCellValue("");
                                    cell3.setCellStyle(style);
                                }

                            } else {
                                if (checkIsNumber(value)) {
                                    cell3.setCellValue(Double.parseDouble(value));
                                    cell3.setCellStyle(style);
                                } else {
                                    cell3.setCellValue(value);
                                    cell3.setCellStyle(style);
                                }
                            }

                        } else {
                            cell3.setCellValue("");
                            cell3.setCellStyle(style);
                        }
                        if (currentLevel.getRowspan() > 1) {
                            sbRol.append(rowNum).append(",").append(cellNum).append(",").append(currentLevel.getRowspan()).append(";");//开始行；开始列；合并行数
                        } else if (currentLevel.getColspan() > 1) {
                            sbCol.append(rowNum).append(",").append(cellNum).append(",").append(currentLevel.getColspan()).append(";");//开始行；开始列；合并列数
                        }
                    } else {
                        cell3.setCellValue("");
                        cell3.setCellStyle(style);
                    }
                    cellNum++;
                }
                //zhibiao
                for (int j = 0; j < dataList.get(r).size(); j++) {
                    Cell cell3 = row2.createCell(cellNum);
                    String value = String.valueOf(dataList.get(r).get(j));
                    if (dataList.get(r).get(j) != null && dataList.get(r).get(j).getRowspan() > 1) {
                        sbDataRol.append(rowNum).append(",").append(cellNum).append(",").append(dataList.get(r).get(j).getRowspan()).append(";");//开始行；开始列；合并行数
                    } else if (dataList.get(r).get(j) != null && dataList.get(r).get(j).getColspan() > 1) {
                        sbDataCol.append(rowNum).append(",").append(cellNum).append(",").append(dataList.get(r).get(j).getColspan()).append(";");//开始行；开始列；合并列数
                    }
                    if (!"".equals(value)) {
                        if (value.contains("%")) {
                            value = value.replace("%", "");
                            if (checkIsNumber(value)) {
                                cell3.setCellValue(Double.parseDouble(value) / 100);
                                datastyle.setDataFormat(wb.createDataFormat().getFormat("0.00%"));
                                cell3.setCellStyle(datastyle);
                            } else {
                                cell3.setCellValue("0.00%");
                                cell3.setCellStyle(style);
                            }

                        } else {
//								if(checkIsNumber(String.valueOf(value)) ){
//									cell3.setCellValue(Double.parseDouble(String.valueOf(value)));
//									cell3.setCellStyle(style);
//								}else{
                            cell3.setCellValue(value);
                            cell3.setCellStyle(style);
//								}
                        }

                    } else {
                        cell3.setCellValue("0");
                        cell3.setCellStyle(style);
                    }
                    cellNum++;
                }


                rowNum++;
            }
            // 单元格合并
            // 四个参数分别是：起始行，结束行，起始列，结束列
            for (String clospan : sbRol.toString().split(";")) {
                if (clospan.length() > 0) {
                    String[] valueStr = clospan.split(",");
                    sheet.addMergedRegion(new CellRangeAddress(Integer.parseInt(valueStr[0]), Integer.parseInt(valueStr[0]) + Integer.parseInt(valueStr[2]) - 1, (short) Integer.parseInt(valueStr[1]), (short) Integer.parseInt(valueStr[1])));
                }
            }
            for (String clospan : sbCol.toString().split(";")) {
                if (clospan.length() > 0) {
                    String[] valueStr = clospan.split(",");
                    sheet.addMergedRegion(new CellRangeAddress(Integer.parseInt(valueStr[0]), Integer.parseInt(valueStr[0]), (short) Integer.parseInt(valueStr[1]), (short) (Integer.parseInt(valueStr[1]) + Integer.parseInt(valueStr[2]) - 1)));
                }
            }
            for (String clospan : sbDataRol.toString().split(";")) {
                if (clospan.length() > 0) {
                    String[] valueStr = clospan.split(",");
                    sheet.addMergedRegion(new CellRangeAddress(Integer.parseInt(valueStr[0]), Integer.parseInt(valueStr[0]) + Integer.parseInt(valueStr[2]) - 1, (short) Integer.parseInt(valueStr[1]), (short) Integer.parseInt(valueStr[1])));
                }
            }
            for (String clospan : sbDataCol.toString().split(";")) {
                if (clospan.length() > 0) {
                    String[] valueStr = clospan.split(",");
                    sheet.addMergedRegion(new CellRangeAddress(Integer.parseInt(valueStr[0]), Integer.parseInt(valueStr[0]), (short) Integer.parseInt(valueStr[1]), (short) (Integer.parseInt(valueStr[1]) + Integer.parseInt(valueStr[2]) - 1)));
                }
            }
        }

    }

    /**
     * 首列样式
     */
    private CellStyle createFirstCellStyle() {
        CellStyle cellStyle = baseCellStyle();
        Font font = wb.createFont();
        font.setFontHeight((short) 180);
        cellStyle.setFont(font);

        cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_GREEN.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        return cellStyle;
    }

    /**
     * 标题样式
     */
    private CellStyle createTitleCellStyle() {
        CellStyle cellStyle = baseCellStyle();
        Font font = wb.createFont();
        font.setFontHeight((short) 180);
        cellStyle.setFont(font);

        cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_GREEN.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        return cellStyle;
    }


    /**
     * 内容样式
     */
    private CellStyle createContentCellStyle() {
        CellStyle cellStyle = baseCellStyle();
        Font font = wb.createFont();
        font.setFontHeight((short) 200);
        font.setBold(false);
        cellStyle.setFont(font);
        return cellStyle;
    }

    private CellStyle baseCellStyle() {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        cellStyle.setWrapText(true);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);

        Font font = wb.createFont();
        font.setBold(true);
        font.setFontName("宋体");
        font.setFontHeight((short) 200);
        cellStyle.setFont(font);

        return cellStyle;
    }

    private String getNowDate() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(new Date());
    }

    /**
     * 输入EXCEL文件
     */
    private void outputExcel() throws IOException {
        if (this.out != null) {
            wb.write(this.out);
        }
    }

    private boolean checkIsNumber(String str) {
        Pattern p = Pattern.compile("^(-?\\d+)(\\.\\d+)?$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public String getHeadTitle() {
        // TODO Auto-generated method stub
        return this.headTitle;
    }

    public void setHeadTitle(String title) {
        this.headTitle = title;
    }

    public String getStartTime() {
        // TODO Auto-generated method stub
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        // TODO Auto-generated method stub
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void createSheet(String sheetName) throws Exception {
        // TODO Auto-generated method stub
        this.sheet = wb.createSheet(sheetName);
    }

    public int getRowNum() throws Exception {
        // TODO Auto-generated method stub
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public void close() {
        try {
            wb.write(out);
            wb.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOut(OutputStream out) throws Exception {
        // TODO Auto-generated method stub
        this.out = out;
    }

    public int getPage() {
        // TODO Auto-generated method stub
        return this.page;
    }

    public void setPage(int page) {
        // TODO Auto-generated method stub
        this.page = page;
    }

    public void writeHead(ReportData reportData) {
        if (this.reportData == null) {
            this.reportData = reportData;
        }
        this.createCellNumber();
        this.createHead();
        this.createSubHead();
        this.createTitle();
    }

    public synchronized void writeRow(ReportData reportData) {
        if (reportData != null) {
            this.reportData = reportData;
            this.createContent();
        }
    }

    public ReportData getReportData() {
        // TODO Auto-generated method stub
        return this.reportData;
    }

    public void setReportData(ReportData reportData) {
        // TODO Auto-generated method stub
        this.reportData = reportData;
    }

}
