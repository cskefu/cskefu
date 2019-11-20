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

import com.chatopera.cc.basic.MainUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "uk_act_callnames")
@org.hibernate.annotations.Proxy(lazy = false)
public class UKefuCallOutNames implements java.io.Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id  = MainUtils.getUUID();
	private String orgi ;		//租户ID
	private String organ ;		//创建部门
	private String creater ;	//创建人
	private String batid ;		//导入 批次ID
	
	private String calltype ;	//缓存里的临时字段， 人工外呼和机器人外呼，不存储进数据库
	
	private Date createtime = new Date();	//创建时间
	
	private Date updatetime = new Date();
	
	private String datastatus;	//数据状态（逻辑删除）
	private String status ;		//状态
	private int calls;			//拨打次数
	private int faildcalls ;	//失败拨打次数
	
	private boolean invalid ;	//多次未接通名单（6次以上）
	private boolean failed ;	//无效名单
	
	private String workstatus ;	//名单状态
	
	private boolean reservation ;	//是否预约
	private Date optime ;		//预约的下次拨打时间
	private String memo ;		//预约备注
	
	private String batname ;
	private String taskname ;
	
	private String servicetype ;	//服务类型标签
	
	private int leavenum ;		//剩余名单数量
	
	private String metaname ;	//表名
	
	
	private String owneruser ;	//分配 坐席
	private String ownerdept ;	//分配 部门
	
	private String dataid ;		//UKDataBean对象ID
	private String taskid ;		//任务ID
	private String filterid;	//筛选ID
	private String actid ;		//活动ID
	
	private String name ;		//名单名称	
	private String phonenumber;	//电话号码
	private String distype ;	//号码隐藏
	
	private int previewtimes ;	//预览次数
	private int previewtime ;	//预览时长
	
	private Date firstcalltime ;	//首次拨打时间
	private String firstcallstatus;	//首次拨打状态
	
	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "assigned")	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrgi() {
		return orgi;
	}

	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}

	public String getOrgan() {
		return organ;
	}

	public void setOrgan(String organ) {
		this.organ = organ;
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

	public String getDatastatus() {
		return datastatus;
	}

	public void setDatastatus(String datastatus) {
		this.datastatus = datastatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getCalls() {
		return calls;
	}

	public void setCalls(int calls) {
		this.calls = calls;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	public boolean isFailed() {
		return failed;
	}

	public void setFailed(boolean failed) {
		this.failed = failed;
	}

	public String getBatid() {
		return batid;
	}

	public void setBatid(String batid) {
		this.batid = batid;
	}

	public Date getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}

	public String getOwneruser() {
		return owneruser;
	}

	public void setOwneruser(String owneruser) {
		this.owneruser = owneruser;
	}

	public String getOwnerdept() {
		return ownerdept;
	}

	public void setOwnerdept(String ownerdept) {
		this.ownerdept = ownerdept;
	}

	public String getDataid() {
		return dataid;
	}

	public void setDataid(String dataid) {
		this.dataid = dataid;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getFilterid() {
		return filterid;
	}

	public void setFilterid(String filterid) {
		this.filterid = filterid;
	}

	public String getActid() {
		return actid;
	}

	public void setActid(String actid) {
		this.actid = actid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public String getWorkstatus() {
		return workstatus;
	}

	public void setWorkstatus(String workstatus) {
		this.workstatus = workstatus;
	}

	public Date getOptime() {
		return optime;
	}

	public void setOptime(Date optime) {
		this.optime = optime;
	}

	public String getBatname() {
		return batname;
	}

	public void setBatname(String batname) {
		this.batname = batname;
	}

	public String getTaskname() {
		return taskname;
	}

	public void setTaskname(String taskname) {
		this.taskname = taskname;
	}

	public int getLeavenum() {
		return leavenum;
	}

	public void setLeavenum(int leavenum) {
		this.leavenum = leavenum;
	}

	public int getFaildcalls() {
		return faildcalls;
	}

	public void setFaildcalls(int faildcalls) {
		this.faildcalls = faildcalls;
	}

	public String getMetaname() {
		return metaname;
	}

	public void setMetaname(String metaname) {
		this.metaname = metaname;
	}

	public String getDistype() {
		return distype;
	}

	public void setDistype(String distype) {
		this.distype = distype;
	}

	public int getPreviewtimes() {
		return previewtimes;
	}

	public void setPreviewtimes(int previewtimes) {
		this.previewtimes = previewtimes;
	}

	public int getPreviewtime() {
		return previewtime;
	}

	public void setPreviewtime(int previewtime) {
		this.previewtime = previewtime;
	}

	public String getServicetype() {
		return servicetype;
	}

	public void setServicetype(String servicetype) {
		this.servicetype = servicetype;
	}

	public boolean isReservation() {
		return reservation;
	}

	public void setReservation(boolean reservation) {
		this.reservation = reservation;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Date getFirstcalltime() {
		return firstcalltime;
	}

	public void setFirstcalltime(Date firstcalltime) {
		this.firstcalltime = firstcalltime;
	}

	public String getFirstcallstatus() {
		return firstcallstatus;
	}

	public void setFirstcallstatus(String firstcallstatus) {
		this.firstcallstatus = firstcallstatus;
	}
	/**
	 * 
	 * @return
	 */
	@Transient
	public String getCalltype() {
		return calltype;
	}

	public void setCalltype(String calltype) {
		this.calltype = calltype;
	}
}
