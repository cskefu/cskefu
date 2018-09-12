/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
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
package com.chatopera.cc.app.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.chatopera.cc.app.basic.MainUtils;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.elasticsearch.annotations.Document;


/**
 * 
 */
@Document(indexName = "cskefu", type = "workorders" , createIndex = false )
@Entity
@Table(name = "uk_workorders")
@org.hibernate.annotations.Proxy(lazy = false)
public class WorkOrders extends ESBean implements UKAgg{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	
	private String id  = MainUtils.getUUID();
	
	private String orderno ;	//工单编号
	private String sessionid ;
	
	private String title ;		//标题
	private String content ;	//内容
	private float price ;		//问题价格
	private String keyword ;	//关键词
	private String summary ;	//摘要
	private boolean anonymous ;		//修改功能 未 是否有上传附件
	
	private boolean top ;		//是否置顶
	private boolean essence ;	//是否精华
	private boolean accept ;	//是否已采纳最佳答案
	private boolean finish	;	//结贴
	
	private int answers ;		//回答数量
	
	
	private int views ;			//阅读数量
	private int followers ;		//关注数量
	private int collections;	//收藏数量
	private int comments ;		//评论数量
	private boolean frommobile ;	//是否移动端提问
	private String status ;	//	状态	
	private String wotype;	//工单类型
	private boolean datastatus ;	//数据状态，是否删除 , 逻辑删除
	
	private String taskid ;
	private String orderid ;
	
	private String dataid ;
	private String eventid ;
	
	private String ani ;
	
	private String cate ;	//工单分类
	
	private String priority ;	//优先级
	
	private Contacts contacts ;
	private String cusid ;
	
	private String initiator ;	//发起人 ， 可以是多人发起的工单
	
	private String bpmid ;
	
	private String tags ;
	
	private String accdept ;//	受理部门
	private String accuser ;	//受理人
	
	private boolean assigned ;	//已分配
		
	private String username ;
	private String orgi ;
	private String creater;
	
	private Date createtime = new Date();
	
	private Date updatetime = new Date();
	private String memo;
	
	
	private String organ;		//
	private String agent ;		//
	
	private String shares ;
	
	private String skill ;
	private int rowcount ;
	private String key ;		//变更用处，修改为 OrderID
	
	
	private User user ;
	
	private User current ;	//当前处理人
	
	private Organ currentorgan ;	//处理部门
	
	private Favorites fav; 
	
	
	/**
	 * @return the id
	 */
	@Id
	@Column(length = 32)
	@GeneratedValue(generator= "paymentableGenerator")
	@GenericGenerator(name= "paymentableGenerator",strategy = "assigned")
	public String getId() {
		return id;
	}

	@Transient
	public String getSessionid() {
		return sessionid;
	}


	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public boolean isAnonymous() {
		return anonymous;
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}
	public String getWotype() {
		return wotype;
	}

	public void setWotype(String wotype) {
		this.wotype = wotype;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}
	@Transient
	public Contacts getContacts() {
		return contacts;
	}

	public void setContacts(Contacts contacts) {
		this.contacts = contacts;
	}

	public String getCusid() {
		return cusid;
	}

	public void setCusid(String cusid) {
		this.cusid = cusid;
	}

	public String getAccdept() {
		return accdept;
	}

	public void setAccdept(String accdept) {
		this.accdept = accdept;
	}

	public String getAccuser() {
		return accuser;
	}

	public void setAccuser(String accuser) {
		this.accuser = accuser;
	}

	public boolean isAssigned() {
		return assigned;
	}

	public void setAssigned(boolean assigned) {
		this.assigned = assigned;
	}

	public int getAnswers() {
		return answers;
	}

	public void setAnswers(int answers) {
		this.answers = answers;
	}

	@Column(name="sviews")
	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public int getFollowers() {
		return followers;
	}

	public void setFollowers(int followers) {
		this.followers = followers;
	}

	public int getCollections() {
		return collections;
	}

	public void setCollections(int collections) {
		this.collections = collections;
	}

	public int getComments() {
		return comments;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}
	
	public boolean isFrommobile() {
		return frommobile;
	}

	public void setFrommobile(boolean frommobile) {
		this.frommobile = frommobile;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getOrgan() {
		return organ;
	}

	public void setOrgan(String organ) {
		this.organ = organ;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCate() {
		return cate;
	}

	public void setCate(String cate) {
		this.cate = cate;
	}

	public boolean isTop() {
		return top;
	}

	public void setTop(boolean top) {
		this.top = top;
	}

	public boolean isEssence() {
		return essence;
	}

	public void setEssence(boolean essence) {
		this.essence = essence;
	}

	public boolean isAccept() {
		return accept;
	}

	public void setAccept(boolean accept) {
		this.accept = accept;
	}

	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}
	@Transient
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	@Transient
	public int getRowcount() {
		return rowcount;
	}

	public void setRowcount(int rowcount) {
		this.rowcount = rowcount;
	}
	
	@Transient
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	public String getOrderno() {
		return orderno;
	}

	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}

	public String getShares() {
		return shares;
	}

	public void setShares(String shares) {
		this.shares = shares;
	}

	public String getInitiator() {
		return initiator;
	}

	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}

	public boolean isDatastatus() {
		return datastatus;
	}

	public void setDatastatus(boolean datastatus) {
		this.datastatus = datastatus;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getBpmid() {
		return bpmid;
	}

	public void setBpmid(String bpmid) {
		this.bpmid = bpmid;
	}

	@Transient
	public Favorites getFav() {
		return fav;
	}

	public void setFav(Favorites fav) {
		this.fav = fav;
	}
	@Transient
	public User getCurrent() {
		return current;
	}

	public void setCurrent(User current) {
		this.current = current;
	}
	@Transient
	public Organ getCurrentorgan() {
		return currentorgan;
	}

	public void setCurrentorgan(Organ currentorgan) {
		this.currentorgan = currentorgan;
	}
	@Transient
	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
	@Transient
	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getDataid() {
		return dataid;
	}

	public void setDataid(String dataid) {
		this.dataid = dataid;
	}

	public String getEventid() {
		return eventid;
	}

	public void setEventid(String eventid) {
		this.eventid = eventid;
	}

	public String getAni() {
		return ani;
	}

	public void setAni(String ani) {
		this.ani = ani;
	}
}
