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


package com.chatopera.cc.model;

public class JobTask implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7709830389321099222L;
	private String[] reportids;
	private String emails;
	private String[] runDates;
	private String[] formats;
	private String[] runTypes;
	private String[] datetypes;
	private String excelType;
	private String emailType;
	private String emailTitle;
	private String emailContent;
	private int priority  = 3 ;
	private Integer runBeginDate;
	private String runCycle;
	private Integer runSpace;
	private Integer runBeginHour;
	private Integer runBeginMinute;
	private Integer runBeginSecond;
	private Boolean isRepeat;
	private Integer repeatSpace;
	private Integer repeatJustTime;
	private String dicids; 
	private String params;
	
	
	public enum ExcelType{
		SINGLE ,
		MULTIPLE  ;
		
		public String toString(){
			return super.toString().toLowerCase() ;
		}
	}
	
	public enum RunCycle{
		DAY ,
		WEEK,
		MONTH;
		
		public String toString(){
			return super.toString().toLowerCase() ;
		}
	}
	public enum EmailType{
		APPENDIX ,
		TEXT;
		
		public String toString(){
			return super.toString().toLowerCase() ;
		}
	}
	
	public String[] getReportids() {
		return reportids;
	}
	public void setReportids(String[] reportids) {
		this.reportids = reportids;
	}
	
	public String getEmails() {
		return emails;
	}
	public void setEmails(String emails) {
		this.emails = emails;
	}
	public String[] getRunDates() {
		return runDates;
	}
	public void setRunDates(String[] runDates) {
		this.runDates = runDates;
	}
	public String[] getFormats() {
		return formats;
	}
	public void setFormats(String[] formats) {
		this.formats = formats;
	}
	public String[] getRunTypes() {
		return runTypes;
	}
	public void setRunTypes(String[] runTypes) {
		this.runTypes = runTypes;
	}
	public String getExcelType() {
		return excelType;
	}
	public void setExcelType(String excelType) {
		this.excelType = excelType;
	}
	public String getEmailType() {
		return emailType;
	}
	public void setEmailType(String emailType) {
		this.emailType = emailType;
	}
	public Integer getRunBeginDate() {
		return runBeginDate;
	}
	public void setRunBeginDate(Integer runBeginDate) {
		this.runBeginDate = runBeginDate;
	}
	public String getRunCycle() {
		return runCycle;
	}
	public void setRunCycle(String runCycle) {
		this.runCycle = runCycle;
	}
	public Integer getRunSpace() {
		return runSpace;
	}
	public void setRunSpace(Integer runSpace) {
		this.runSpace = runSpace;
	}
	public Integer getRunBeginHour() {
		return runBeginHour;
	}
	public void setRunBeginHour(Integer runBeginHour) {
		this.runBeginHour = runBeginHour;
	}
	public Integer getRunBeginMinute() {
		return runBeginMinute;
	}
	public void setRunBeginMinute(Integer runBeginMinute) {
		this.runBeginMinute = runBeginMinute;
	}
	public Integer getRunBeginSecond() {
		return runBeginSecond;
	}
	public void setRunBeginSecond(Integer runBeginSecond) {
		this.runBeginSecond = runBeginSecond;
	}
	public Boolean getIsRepeat() {
		return isRepeat!=null ? isRepeat : false ;
	}
	public void setIsRepeat(Boolean isRepeat) {
		this.isRepeat = isRepeat;
	}
	public Integer getRepeatSpace() {
		return repeatSpace;
	}
	public void setRepeatSpace(Integer repeatSpace) {
		this.repeatSpace = repeatSpace;
	}
	public Integer getRepeatJustTime() {
		return repeatJustTime;
	}
	public void setRepeatJustTime(Integer repeatJustTime) {
		this.repeatJustTime = repeatJustTime;
	}
	public String getDicids() {
		return dicids;
	}
	public void setDicids(String dicids) {
		this.dicids = dicids;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getEmailTitle() {
		return emailTitle;
	}
	public void setEmailTitle(String emailTitle) {
		this.emailTitle = emailTitle;
	}
	public String getEmailContent() {
		return emailContent;
	}
	public void setEmailContent(String emailContent) {
		this.emailContent = emailContent;
	}
	public String[] getDatetypes() {
		return datetypes;
	}
	public void setDatetypes(String[] datetypes) {
		this.datetypes = datetypes;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	
	
}
