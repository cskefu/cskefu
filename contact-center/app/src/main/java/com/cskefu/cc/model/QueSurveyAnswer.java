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
@Table(name = "uk_que_survey_answer")
@org.hibernate.annotations.Proxy(lazy = false)
public class QueSurveyAnswer implements java.io.Serializable{

	/**
	 * 问卷问题表
	 */
	private static final long serialVersionUID = 1115593425069549681L;
	
	private String id ;
	private String questionid;//问题ID
	private String questionname;//问题名称
	private String answer;//问题答案
	private String queid;//跳转问题ID
	private int answerscore;//答案评分
	private String orgi;//租户ID
	private String creater;//创建人
	private Date createtime;//创建时间
	private Date updatetime;//更新时间
	private String processid;//问卷ID
	private String correct;//是否是正确答案（0正确1不正确）
	
	private String hanguptype ;		//结束类型
	private String hangupmsg ;		//结束文字
	private String hangupvoice ;	//结束语音

	
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
	public String getQuestionid() {
		return questionid;
	}
	public void setQuestionid(String questionid) {
		this.questionid = questionid;
	}
	public String getQuestionname() {
		return questionname;
	}
	public void setQuestionname(String questionname) {
		this.questionname = questionname;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getQueid() {
		return queid;
	}
	public void setQueid(String queid) {
		this.queid = queid;
	}
	public int getAnswerscore() {
		return answerscore;
	}
	public void setAnswerscore(int answerscore) {
		this.answerscore = answerscore;
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
	public String getProcessid() {
		return processid;
	}
	public void setProcessid(String processid) {
		this.processid = processid;
	}
	public String getCorrect() {
		return correct;
	}
	public void setCorrect(String correct) {
		this.correct = correct;
	}
	public String getHanguptype() {
		return hanguptype;
	}
	public void setHanguptype(String hanguptype) {
		this.hanguptype = hanguptype;
	}
	public String getHangupmsg() {
		return hangupmsg;
	}
	public void setHangupmsg(String hangupmsg) {
		this.hangupmsg = hangupmsg;
	}
	public String getHangupvoice() {
		return hangupvoice;
	}
	public void setHangupvoice(String hangupvoice) {
		this.hangupvoice = hangupvoice;
	}
}
