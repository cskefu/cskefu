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

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.model.*;
import com.chatopera.cc.persistence.interfaces.DataExchangeInterface;
import com.chatopera.cc.persistence.repository.JobDetailRepository;
import com.chatopera.cc.persistence.repository.ReporterRepository;
import com.google.common.collect.ArrayListMultimap;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelImportProecess extends DataProcess {
    private final DecimalFormat format = new DecimalFormat("###");
    private final AtomicInteger pages = new AtomicInteger();
    private final AtomicInteger errors = new AtomicInteger();

    public ExcelImportProecess(DSDataEvent event) {
        super(event);
    }

    @Override
    public void process() {
        processExcel(event);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void processExcel(final DSDataEvent event) {
        InputStream is = null;
        try {
            event.getDSData().getReport().setTableid(event.getDSData().getTask().getId());
            if (event.getDSData().getUser() != null) {
                event.getDSData().getReport().setUserid(event.getDSData().getUser().getId());
                event.getDSData().getReport().setUsername(event.getDSData().getUser().getUsername());
            }

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
                Objects.requireNonNull(is);
                wb = isExcel2003 ? new HSSFWorkbook(is) : new XSSFWorkbook(is);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Objects.requireNonNull(wb);
            Sheet sheet = wb.getSheetAt(0);
            Row titleRow = sheet.getRow(0);
            Row valueRow = sheet.getRow(1);
            int totalRows = sheet.getPhysicalNumberOfRows();
            int colNum = titleRow.getPhysicalNumberOfCells();
            for (int i = 2; i < totalRows && valueRow == null; i++) {
                valueRow = sheet.getRow(i);
                if (valueRow != null) {
                    break;
                }
            }
            /*
             * 需要检查Mapping 是否存在
             */
            long start = System.currentTimeMillis();
            Map<Object, List> refValues = new HashMap<>();
            MetadataTable table = event.getDSData().getTask();
            for (TableProperties tp : table.getTableproperty()) {
                if (tp.isReffk() && !StringUtils.isBlank(tp.getReftbid())) {
                    DataExchangeInterface exchange = (DataExchangeInterface) MainContext.getContext().getBean(tp.getReftbid());
                    refValues.put(tp.getFieldname(), exchange.getListDataByIdAndOrgi(null, null, event.getOrgi()));
                }
            }

            for (int i = 1; i < totalRows; i++) {
                Row row = sheet.getRow(i);
                Object data = null;
                if (row != null) {
                    if (event.getDSData().getClazz() != null) {
                        data = event.getDSData().getClazz().newInstance();
                    }
                    Map<String, Object> values = new HashMap<>();
                    ArrayListMultimap<String, Object> multiValues = ArrayListMultimap.create();
                    boolean skipDataVal = false; //跳过数据校验
                    StringBuilder pkStr = new StringBuilder(), allStr = new StringBuilder();
                    for (int col = 0; col < colNum; col++) {
                        Cell value = row.getCell(col);
                        Cell title = titleRow.getCell(col);
                        String titleValue = getValue(title);
                        TableProperties tableProperties = getTableProperties(event, titleValue);
                        if (tableProperties != null && value != null) {
                            String valuestr = getValue(value);
                            if (!StringUtils.isBlank(valuestr)) {
                                if (tableProperties.isModits()) {
                                    if (!StringUtils.isBlank(valuestr)) {
                                        multiValues.put(tableProperties.getFieldname(), valuestr);
                                    }
                                } else {
                                    if (tableProperties.isSeldata()) {
                                        SysDic sysDic = Dict.getInstance().getDicItem(valuestr);
                                        if (sysDic != null) {
                                            values.put(tableProperties.getFieldname(), sysDic.getName());
                                        } else {
                                            List<SysDic> dicItemList = Dict.getInstance().getSysDic(tableProperties.getSeldatacode());
                                            if (dicItemList != null && dicItemList.size() > 0) {
                                                for (SysDic dicItem : dicItemList) {
                                                    if (dicItem.getName().equals(valuestr)) {
                                                        values.put(tableProperties.getFieldname(), dicItem.isDiscode() ? dicItem.getCode() : dicItem.getId());
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    } else if (tableProperties.isReffk() && refValues.get(tableProperties.getFieldname()) != null) {
                                        List keys = refValues.get(tableProperties.getFieldname());
                                        if (keys != null) {
                                            values.put(tableProperties.getFieldname(), getRefid(refValues.get(tableProperties.getFieldname()), valuestr));
                                        }
                                    } else {
                                        values.put(tableProperties.getFieldname(), valuestr);
                                    }
                                    if (tableProperties.isPk() && !tableProperties.getFieldname().equalsIgnoreCase("id")) {
                                        pkStr.append(valuestr);
                                    }
                                }
                                allStr.append(valuestr);
                            }
                            event.getDSData().getReport().setBytes(event.getDSData().getReport().getBytes() + valuestr.length());
                            event.getDSData().getReport().getAtompages().incrementAndGet();
                        }
                    }
                    values.put("orgi", event.getOrgi());
                    if (values.get("id") == null) {
                        if (pkStr.length() > 0) {
                            values.put("id", MainUtils.md5(pkStr.append(event.getDSData().getTask().getTablename()).toString()));
                        } else {
                            values.put("id", MainUtils.md5(allStr.append(event.getDSData().getTask().getTablename()).toString()));
                        }
                    }
                    if (event.getValues() != null && event.getValues().size() > 0) {
                        values.putAll(event.getValues());
                    }
                    values.putAll(multiValues.asMap());
                    String validFaildMessage = null;
                    for (TableProperties tp : table.getTableproperty()) {
                        if (!StringUtils.isBlank(tp.getDefaultvaluetitle())) {
                            String valuestr = (String) values.get(tp.getFieldname());
                            if (tp.getDefaultvaluetitle().contains("required") && StringUtils.isBlank(valuestr)) {
                                skipDataVal = true;
                                validFaildMessage = "required";
                                break;
                            } else if (valuestr != null && (tp.getDefaultvaluetitle().contains("numstr") && !valuestr.matches("[\\d]+"))) {
                                skipDataVal = true;
                                validFaildMessage = "numstr";
                                break;
                            } else if (valuestr != null && (tp.getDefaultvaluetitle().contains("datenum") || tp.getDefaultvaluetitle().contains("datetime"))) {
                                if (!valuestr.matches("[\\d]{4}-[\\d]{2}-[\\d]{2}") && !valuestr.matches("[\\d]{4}-[\\d]{2}-[\\d]{2} [\\d]{2}:[\\d]{2}:[\\d]{2}")) {
                                    skipDataVal = true;
                                    validFaildMessage = "datenum";
                                    break;
                                } else {
                                    if (valuestr.matches("[\\d]{4}-[\\d]{2}-")) {
                                        if ("date".equals(tp.getDefaultfieldvalue())) {
                                            values.put(tp.getFieldname(), MainUtils.simpleDateFormat.parse(valuestr));
                                        } else {
                                            values.put(tp.getFieldname(), MainUtils.simpleDateFormat.format(MainUtils.simpleDateFormat.parse(valuestr)));
                                        }
                                    } else if (valuestr.matches("[\\d]{4}-[\\d]{2}-[\\d]{2} [\\d]{2}:[\\d]{2}:[\\d]{2}")) {
                                        if ("date".equals(tp.getDefaultfieldvalue())) {
                                            values.put(tp.getFieldname(), MainUtils.dateFormate.parse(valuestr));
                                        } else {
                                            values.put(tp.getFieldname(), MainUtils.simpleDateFormat.format(MainUtils.dateFormate.parse(valuestr)));
                                        }

                                    }
                                }
                            }
                        }
                        if (tp.isReffk() && !StringUtils.isBlank(tp.getReftbid()) && refValues.get(tp.getFieldname()) == null) {
                            DataExchangeInterface exchange = (DataExchangeInterface) MainContext.getContext().getBean(tp.getReftbid());
                            exchange.process(data, event.getOrgi());
                        }
                    }

                    if (!values.containsKey("orgi")) {
                        skipDataVal = true;
                    }
                    event.getDSData().getReport().setTotal(pages.intValue());
                    values.put("creater", event.getValues().get("creater"));
                    if (data != null && !skipDataVal) {
                        MainUtils.populate(data, values);
                        pages.incrementAndGet();
                        event.getDSData().getProcess().process(data);
                    } else if (data == null) {
                        /*
                         * 导入的数据，只写入ES
                         */
                        if (skipDataVal) {    //跳过
                            values.put("status", "invalid");
                            values.put("validresult", "invalid");
                            values.put("validmessage", validFaildMessage != null ? validFaildMessage : "");
                        } else {
                            values.put("validresult", "valid");
                        }
                        values.put("status", MainContext.NamesDisStatusType.NOT.toString());
                        values.put("batid", event.getBatid());

                        values.put("createtime", System.currentTimeMillis());
                        values.put("callstatus", MainContext.NameStatusType.NOTCALL.toString());
                        values.put("execid", event.getDSData().getReport().getId());

                        if (i % 500 == 0) {
                            MainContext.getContext().getBean(ReporterRepository.class).save(event.getDSData().getReport());
                        }

                        if (values.get("cusid") == null) {
                            values.put("cusid", values.get("id"));
                        }
                        pages.incrementAndGet();
                        event.getDSData().getProcess().process(values);

                        /*
                         * 访客信息表
                         */
                    }
                    if (skipDataVal) {    //跳过
                        errors.incrementAndGet();
                    }
                }
            }

            event.setTimes(System.currentTimeMillis() - start);
            event.getDSData().getReport().setEndtime(new Date());
            event.getDSData().getReport().setAmount(String.valueOf((float) event.getTimes() / 1000f));
            event.getDSData().getReport().setStatus(MainContext.TaskStatusType.END.getType());
            event.getDSData().getReport().setTotal(pages.intValue());
            event.getDSData().getReport().setPages(pages.intValue());
            event.getDSData().getReport().setErrors(errors.intValue());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (event.getDSData().getFile().exists()) {
                //noinspection ResultOfMethodCallIgnored
                event.getDSData().getFile().delete();
            }
            /*
             * 更新数据
             */
            MainContext.getContext().getBean(ReporterRepository.class).save(event.getDSData().getReport());
            if (event.getDSData().getClazz() == null && !StringUtils.isBlank(event.getBatid())) {
                JobDetailRepository batchRes = MainContext.getContext().getBean(JobDetailRepository.class);
                JobDetail batch = this.event.getDSData().getJobDetail();
                if (batch == null) {
                    batch = batchRes.findByIdAndOrgi(event.getBatid(), event.getOrgi());
                }
                if (batch != null) {
                    batch.setNamenum(batch.getNamenum() + pages.intValue());
                    batch.setValidnum(batch.getValidnum() + (pages.intValue() - errors.intValue()));
                    batch.setInvalidnum(batch.getInvalidnum() + errors.intValue());
                    batch.setExecnum(batch.getExecnum() + 1);
                    batch.setNotassigned(batch.getNotassigned() + (pages.intValue() - errors.intValue()));
                    batchRes.save(batch);
                }
            }
            event.getDSData().getProcess().end();
        }
    }

    private String getRefid(List<Object> dataList, String value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String id = "";
        for (Object data : dataList) {
            Object target;
            if (PropertyUtils.isReadable(data, "name")) {
                target = BeanUtils.getProperty(data, "name");
                if (target != null && target.equals(value)) {
                    id = BeanUtils.getProperty(data, "id");
                }
            }
            if (PropertyUtils.isReadable(data, "tag")) {
                target = BeanUtils.getProperty(data, "tag");
                if (target != null && target.equals(value)) {
                    id = BeanUtils.getProperty(data, "id");
                }
            }
            if (StringUtils.isBlank(id) && PropertyUtils.isReadable(data, "title")) {
                target = BeanUtils.getProperty(data, "title");
                if (target != null && target.equals(value)) {
                    id = BeanUtils.getProperty(data, "id");
                }
            }
            if (StringUtils.isBlank(id)) {
                target = BeanUtils.getProperty(data, "id");
                if (target != null && target.equals(value)) {
                    id = target.toString();
                }
            }
        }
        return id;
    }

    private TableProperties getTableProperties(DSDataEvent event, String title) {
        TableProperties tableProperties = null;
        for (TableProperties tp : event.getDSData().getTask().getTableproperty()) {
            if (tp.getName().equals(title) || tp.getFieldname().equals(title)) {
                tableProperties = tp;
                break;
            }
        }
        return tableProperties;
    }

    private boolean isExcel2007(String fileName) {
        return fileName.matches("^.+\\.(?i)(xlsx)$");
    }

    private String getValue(Cell cell) {
        String strCell = "";
        if (cell != null) {
            short dt = cell.getCellStyle().getDataFormat();
            switch (cell.getCellType()) {
                case STRING:
                    strCell = cell.getStringCellValue();
                    break;
                case BOOLEAN:
                    strCell = String.valueOf(cell.getBooleanCellValue());
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        SimpleDateFormat sdf;
                        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        strCell = sdf.format(cell.getDateCellValue());
                    } else if (cell.getCellStyle().getDataFormat() == 58) {
                        // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        double value = cell.getNumericCellValue();
                        strCell = sdf.format(org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value));
                    } else {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            SimpleDateFormat sdf;
                            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            strCell = sdf.format(cell.getDateCellValue());
                        } else {
                            boolean isNumber = isNumberFormat(dt);
                            if (isNumber) {
                                DecimalFormat numberFormat = getNumberFormat(cell.getCellStyle().getDataFormatString());
                                if (numberFormat != null) {
                                    strCell = String.valueOf(numberFormat.format(cell.getNumericCellValue()));
                                } else {
                                    strCell = String.valueOf(cell.getNumericCellValue());
                                }
                            } else {
                                strCell = String.valueOf(format.format(cell.getNumericCellValue()));
                            }
                        }
                    }
                    break;
                case FORMULA: {
                    // 判断当前的cell是否为Date
                    boolean isNumber = isNumberFormat(dt);
                    try {
                        if (isNumber) {
                            strCell = String.valueOf(cell.getNumericCellValue());
                        } else {
                            strCell = "";
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        strCell = cell.getRichStringCellValue().getString();
                    }
                    break;
                }
                default:
                    strCell = "";
                    break;
            }
            if (strCell.equals("")) {
                return "";
            }
        }
        return strCell;
    }

    @SuppressWarnings({"deprecation", "unused"})
    private String getDataType(Cell cell) {
        String dataType = "string";
        if (cell != null) {
            short dt = cell.getCellStyle().getDataFormat();
            switch (cell.getCellType()) {
                case BOOLEAN:
                    dataType = "number";
                    break;
                case BLANK:
                    dataType = determineDataType(dt, HSSFDateUtil.isCellDateFormatted(cell), cell.getCellStyle().getDataFormat() == 58);
                    break;
                case NUMERIC:
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        dataType = "datetime";
                    } else {
                        dataType = determineDataType(dt, cell.getCellStyle().getDataFormat() == 58, HSSFDateUtil.isCellDateFormatted(cell));
                    }
                    break;
                case FORMULA: {
                    // 判断当前的cell是否为Date
                    boolean isNumber = isNumberFormat(dt);
                    if (isNumber) {
                        dataType = "number";
                    } else {
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

    private String determineDataType(short dt, boolean b, boolean cellDateFormatted) {
        String dataType;
        if (b) {
            dataType = "datetime";
        } else {
            if (cellDateFormatted) {
                dataType = "datetime";
            } else {
                boolean isNumber = isNumberFormat(dt);
                if (isNumber) {
                    dataType = "number";
                } else {
                    dataType = "string";
                }
            }
        }
        return dataType;
    }

    private DecimalFormat getNumberFormat(String dataformat) {
        DecimalFormat numberFormat = null;
        int index = dataformat.indexOf("_") > 0 ? dataformat.indexOf("_") : dataformat.indexOf(";");
        if (index > 0) {
            String format = dataformat.substring(0, index);
            if (format.matches("[\\d.]+")) {
                numberFormat = new DecimalFormat(format);
            }
        }

        return numberFormat;
    }

    private boolean isNumberFormat(short dataType) {
        boolean number = false;
        switch (dataType) {
            case 180:
            case 181:
            case 182:
            case 178:
            case 177:
            case 176:
            case 183:
            case 185:
            case 186:
            case 179:
            case 187:
            case 7:
            case 8:
            case 44:
            case 10:
            case 12:
            case 13:
            case 188:
            case 189:
            case 190:
            case 191:
            case 192:
            case 193:
            case 194:
            case 11:
                number = true;
                break;
        }
        return number;
    }


}
