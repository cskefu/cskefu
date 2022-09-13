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

package com.cskefu.cc.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "uk_que_survey_question")
@org.hibernate.annotations.Proxy(lazy = false)
public class QueSurveyQuestion implements java.io.Serializable{

	/**
	 * 问卷问题表
	 */
	private static final long serialVersionUID = 1115593425069549681L;
	
	private String id ;
	private String name;//问题名称
	private String sortindex;//问题序号
	private int quetype;//问题类型（选择题/问答题）
	private String orgi;//租户ID
	private String creater;//创建人
	private Date createtime;//创建时间
	private Date updatetime;//更新时间
	
	private String description;//描述
	private String memo;//备注
	private int score;//问题分值
	private String processid;//问卷ID
	private String wvtype;//类型（文字/语音）
	private String quevoice;//问题语音ID
	
	private String confirmtype;//答案确认语类型
	private String confirmword;//答案确认语文字
	private String confirmvoice;//答案确认语语音
	
	private String overtimetype;//回答超时语
	private String overtimeword;//回答超时语文字
	private String overtimevoice;//回答超时语语音
	
	private String errortype;//回答错误语
	private String errorword;//回答错误语文字
	private String errorvoice;//回答错误语语音
	
	private String replykeyword ;	//遇到设置的关键词后重复播放语音
	private String replytype;//重复提示类型
	private String replyword;//重复提示语文字
	private String replyvoice;//重复提示语语音
	
	private String replyrepeat;//重复确认语-最大重复次数
	private String replyoperate;//重复确认语-到达最大次数的操作（转接trans/挂断/handup）
	private String replytrans;//重复确认语-转接号码
	private String replytypeup;//重复确认语-挂断提示语类型
	private String replywordup;//重复确认语-挂断提示语（文字）
	private String replyvoiceup;//重复确认语-挂断提示语（语音ID）
	
	private String overtimerepeat;//回答超时语-最大重复次数
	private String overtimeoperate;//回答超时语-到达最大次数的操作（转接trans/挂断/handup）
	private String overtimetrans;//回答超时语-转接号码
	private String overtimetypeup;//回答超时语-挂断提示语类型
	private String overtimewordup;//回答超时语-挂断提示语（文字）
	private String overtimevoiceup;//回答超时语-挂断提示语（语音ID）
	
	private String errorepeat;//回答错误语-最大重复次数
	private String erroroperate;//回答错误语-到达最大次数的操作（转接trans/挂断/handup）
	private String errortrans;//回答错误语-转接号码
	private String errortypeup;//回答错误语-挂断提示语类型
	private String errorwordup;//回答错误语-挂断提示语（文字）
	private String errorvoiceup;//回答错误语-挂断提示语（语音ID）
	
	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortindex() {
		return sortindex;
	}
	public void setSortindex(String sortindex) {
		this.sortindex = sortindex;
	}
	
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public String getCreater() {
		return creater;
	}
	public void setCreater(String creater) {
		this.creater = creater;
	}
	
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public int getQuetype() {
		return quetype;
	}
	public void setQuetype(int quetype) {
		this.quetype = quetype;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getProcessid() {
		return processid;
	}
	public void setProcessid(String processid) {
		this.processid = processid;
	}
	
	public String getWvtype() {
		return wvtype;
	}
	public void setWvtype(String wvtype) {
		this.wvtype = wvtype;
	}
	public String getQuevoice() {
		return quevoice;
	}
	public void setQuevoice(String quevoice) {
		this.quevoice = quevoice;
	}
	public String getConfirmtype() {
		return confirmtype;
	}
	public void setConfirmtype(String confirmtype) {
		this.confirmtype = confirmtype;
	}
	public String getConfirmword() {
		return confirmword;
	}
	public void setConfirmword(String confirmword) {
		this.confirmword = confirmword;
	}
	public String getConfirmvoice() {
		return confirmvoice;
	}
	public void setConfirmvoice(String confirmvoice) {
		this.confirmvoice = confirmvoice;
	}
	public String getOvertimetype() {
		return overtimetype;
	}
	public void setOvertimetype(String overtimetype) {
		this.overtimetype = overtimetype;
	}
	public String getOvertimeword() {
		return overtimeword;
	}
	public void setOvertimeword(String overtimeword) {
		this.overtimeword = overtimeword;
	}
	public String getOvertimevoice() {
		return overtimevoice;
	}
	public void setOvertimevoice(String overtimevoice) {
		this.overtimevoice = overtimevoice;
	}
	public String getErrortype() {
		return errortype;
	}
	public void setErrortype(String errortype) {
		this.errortype = errortype;
	}
	public String getErrorword() {
		return errorword;
	}
	public void setErrorword(String errorword) {
		this.errorword = errorword;
	}
	public String getErrorvoice() {
		return errorvoice;
	}
	public void setErrorvoice(String errorvoice) {
		this.errorvoice = errorvoice;
	}
	public String getReplykeyword() {
		return replykeyword;
	}
	public void setReplykeyword(String replykeyword) {
		this.replykeyword = replykeyword;
	}
	public String getReplytype() {
		return replytype;
	}
	public void setReplytype(String replytype) {
		this.replytype = replytype;
	}
	public String getReplyword() {
		return replyword;
	}
	public void setReplyword(String replyword) {
		this.replyword = replyword;
	}
	public String getReplyvoice() {
		return replyvoice;
	}
	public void setReplyvoice(String replyvoice) {
		this.replyvoice = replyvoice;
	}
	public String getReplyrepeat() {
		return replyrepeat;
	}
	public void setReplyrepeat(String replyrepeat) {
		this.replyrepeat = replyrepeat;
	}
	public String getReplyoperate() {
		return replyoperate;
	}
	public void setReplyoperate(String replyoperate) {
		this.replyoperate = replyoperate;
	}
	public String getReplytrans() {
		return replytrans;
	}
	public void setReplytrans(String replytrans) {
		this.replytrans = replytrans;
	}
	public String getReplytypeup() {
		return replytypeup;
	}
	public void setReplytypeup(String replytypeup) {
		this.replytypeup = replytypeup;
	}
	public String getReplywordup() {
		return replywordup;
	}
	public void setReplywordup(String replywordup) {
		this.replywordup = replywordup;
	}
	public String getReplyvoiceup() {
		return replyvoiceup;
	}
	public void setReplyvoiceup(String replyvoiceup) {
		this.replyvoiceup = replyvoiceup;
	}
	public String getOvertimerepeat() {
		return overtimerepeat;
	}
	public void setOvertimerepeat(String overtimerepeat) {
		this.overtimerepeat = overtimerepeat;
	}
	public String getOvertimeoperate() {
		return overtimeoperate;
	}
	public void setOvertimeoperate(String overtimeoperate) {
		this.overtimeoperate = overtimeoperate;
	}
	public String getOvertimetrans() {
		return overtimetrans;
	}
	public void setOvertimetrans(String overtimetrans) {
		this.overtimetrans = overtimetrans;
	}
	public String getOvertimetypeup() {
		return overtimetypeup;
	}
	public void setOvertimetypeup(String overtimetypeup) {
		this.overtimetypeup = overtimetypeup;
	}
	public String getOvertimewordup() {
		return overtimewordup;
	}
	public void setOvertimewordup(String overtimewordup) {
		this.overtimewordup = overtimewordup;
	}
	public String getOvertimevoiceup() {
		return overtimevoiceup;
	}
	public void setOvertimevoiceup(String overtimevoiceup) {
		this.overtimevoiceup = overtimevoiceup;
	}
	public String getErrorepeat() {
		return errorepeat;
	}
	public void setErrorepeat(String errorepeat) {
		this.errorepeat = errorepeat;
	}
	public String getErroroperate() {
		return erroroperate;
	}
	public void setErroroperate(String erroroperate) {
		this.erroroperate = erroroperate;
	}
	public String getErrortrans() {
		return errortrans;
	}
	public void setErrortrans(String errortrans) {
		this.errortrans = errortrans;
	}
	public String getErrortypeup() {
		return errortypeup;
	}
	public void setErrortypeup(String errortypeup) {
		this.errortypeup = errortypeup;
	}
	public String getErrorwordup() {
		return errorwordup;
	}
	public void setErrorwordup(String errorwordup) {
		this.errorwordup = errorwordup;
	}
	public String getErrorvoiceup() {
		return errorvoiceup;
	}
	public void setErrorvoiceup(String errorvoiceup) {
		this.errorvoiceup = errorvoiceup;
	}
	
}
