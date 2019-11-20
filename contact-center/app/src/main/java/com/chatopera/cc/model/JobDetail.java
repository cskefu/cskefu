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

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 批次表，导入批次
 *
 * @author iceworld
 */
@Entity
@Table(name = "uk_jobdetail")
@org.hibernate.annotations.Proxy(lazy = false)
public class JobDetail implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2258870729818431384L;

    private String id;

    private String orgi;        //租户ID
    private String organ;        //创建部门
    private String creater;    //创建人
    private String batchtype;    //导入类型（计划批次/临时批次）
    private String imptype;    //导入方式：本地文件导入/远程文件导入/数据库导入/手动上传导入

    private String actype;        //活动类型
    private String filterid;    //筛选表单ID

    private String parentid;    //目录ID

    private int threads;        //并发限制

    private String impurl;
    private String filetype;
    private String dbtype;        //数据库类型
    private String jdbcurl;
    private String driverclazz;
    private String username;
    private String password;

    private String clazz;            //结算表的时候，危险操作提示
    private String taskid;     //任务目标id
    private String tasktype; //table 模型任务 、report报表计划任务
    private boolean plantask;    // true后台任务，false交互式任务
    private String source;   //File source eg:e:\document
    private String userid;        //schedule user
    private String email;
    private String nickname;
    private String crawltaskid;//用来标记是手动点击的还是到了执行时间执行的，手动：operation，自动：auto或null
    private long lastindex = 0;
    private String dataid;//为了在状态中检索，存放了报表的id集合
    private String dicid;//存放了报表所在目录

    private Date taskfiretime;
    private String crawltask;
    private String targettask;
    private boolean createtable;
    private String taskstatus = "0";
    private long startindex;    //数据更新位置
    private Date lastdate;        //数据更新时间
    private Date nextfiretime;        //数据更新时间
    private String cronexp;
    private boolean fetcher = true;
    private boolean pause = false;
    private boolean plantaskreadtorun = false;

    private boolean mapping;    //是否已经映射数据结构


    private String memo;        //任务类型，如果是 cubedata , 则在执行完毕后更新cube信息   , 导出任务时改变用处，用于导出是的 导出格式
    private long fetchSize;
    private String usearea;    //启用分区采集    ， 改变用处，在 导出文件的时候，用于记录当前导出的 数据集的 total
    private String areafield;    //分区字段
    private String areafieldtype;    //字段类型
    private String arearule;    //分区递增规则
    private String minareavalue;    //最小值
    private String maxareavalue;    //最大值
    private String formatstr;        //格式化字符串
    private String taskinfo;       //序列化以后的结果，JSON格式
    private int priority;           //优先级
    private String runserver;        //执行此任务的 服务器

    private String actid;        //元数据ID
    private String distype;    //名单分配方式
    private String distpolicy;    //分配策略
    private int policynum;    //策略数量
    private String busstype;    //业务类型

    private int disnum;        //默认分配数量

    private String siptrunk;    //线路资源
    private String province;    //线路省份
    private String city;        //线路地区
    private boolean prefix;    //异地号码加拨前缀 0

    private Date createtime = new Date();    //创建时间

    private Date updatetime = new Date();

    private String datastatus;    //数据状态（逻辑删除）
    private String status;        //状态		正常，已处理完，已过期

    private int namenum;        //名单总数
    private int validnum;        //有效名单
    private int invalidnum;    //无效名单

    private int execnum;        //执行次数

    private int assigned;        //已分配
    private int notassigned;    //未分配
    private String description;    //备注

    private String execmd;        //执行的指令：分配|回收
    private String exectype;    //回收的类型
    private String exectarget;    //回收的对象

    private String execto;        //回收到部门对象

    private String reportid;    //当前正在执行的 ReportID

    private String name;        //导入的批次名称 ， 自动生成， 规则为 yyyyMMdd--ORDER

    @Transient
    private String exceptionMsg = null;

    @Transient
    private Reporter report;

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

    public String getBatchtype() {
        return batchtype;
    }

    public void setBatchtype(String batchtype) {
        this.batchtype = batchtype;
    }

    public String getImptype() {
        return imptype;
    }

    public void setImptype(String imptype) {
        this.imptype = imptype;
    }

    public String getImpurl() {
        return impurl;
    }

    public void setImpurl(String impurl) {
        this.impurl = impurl;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getDbtype() {
        return dbtype;
    }

    public void setDbtype(String dbtype) {
        this.dbtype = dbtype;
    }

    public String getJdbcurl() {
        return jdbcurl;
    }

    public void setJdbcurl(String jdbcurl) {
        this.jdbcurl = jdbcurl;
    }

    public String getDriverclazz() {
        return driverclazz;
    }

    public void setDriverclazz(String driverclazz) {
        this.driverclazz = driverclazz;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNamenum() {
        return namenum;
    }

    public void setNamenum(int namenum) {
        this.namenum = namenum;
    }

    public int getValidnum() {
        return validnum;
    }

    public void setValidnum(int validnum) {
        this.validnum = validnum;
    }

    public int getInvalidnum() {
        return invalidnum;
    }

    public void setInvalidnum(int invalidnum) {
        this.invalidnum = invalidnum;
    }

    public int getAssigned() {
        return assigned;
    }

    public void setAssigned(int assigned) {
        this.assigned = assigned;
    }

    public int getNotassigned() {
        return notassigned;
    }

    public void setNotassigned(int notassigned) {
        this.notassigned = notassigned;
    }

    public String getActid() {
        return actid;
    }

    public void setActid(String actid) {
        this.actid = actid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getExecnum() {
        return execnum;
    }

    public void setExecnum(int execnum) {
        this.execnum = execnum;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getTasktype() {
        return tasktype;
    }

    public void setTasktype(String tasktype) {
        this.tasktype = tasktype;
    }

    public boolean isPlantask() {
        return plantask;
    }

    public void setPlantask(boolean plantask) {
        this.plantask = plantask;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCrawltaskid() {
        return crawltaskid;
    }

    public void setCrawltaskid(String crawltaskid) {
        this.crawltaskid = crawltaskid;
    }

    public long getLastindex() {
        return lastindex;
    }

    public void setLastindex(long lastindex) {
        this.lastindex = lastindex;
    }

    public String getDataid() {
        return dataid;
    }

    public void setDataid(String dataid) {
        this.dataid = dataid;
    }

    public String getDicid() {
        return dicid;
    }

    public void setDicid(String dicid) {
        this.dicid = dicid;
    }

    public Date getTaskfiretime() {
        return taskfiretime;
    }

    public void setTaskfiretime(Date taskfiretime) {
        this.taskfiretime = taskfiretime;
    }

    public String getCrawltask() {
        return crawltask;
    }

    public void setCrawltask(String crawltask) {
        this.crawltask = crawltask;
    }

    public String getTargettask() {
        return targettask;
    }

    public void setTargettask(String targettask) {
        this.targettask = targettask;
    }

    public boolean isCreatetable() {
        return createtable;
    }

    public void setCreatetable(boolean createtable) {
        this.createtable = createtable;
    }

    public String getTaskstatus() {
        return taskstatus;
    }

    public void setTaskstatus(String taskstatus) {
        this.taskstatus = taskstatus;
    }

    public long getStartindex() {
        return startindex;
    }

    public void setStartindex(long startindex) {
        this.startindex = startindex;
    }

    public Date getLastdate() {
        return lastdate;
    }

    public void setLastdate(Date lastdate) {
        this.lastdate = lastdate;
    }

    public Date getNextfiretime() {
        return nextfiretime;
    }

    public void setNextfiretime(Date nextfiretime) {
        this.nextfiretime = nextfiretime;
    }

    public String getCronexp() {
        return cronexp;
    }

    public void setCronexp(String cronexp) {
        this.cronexp = cronexp;
    }

    public boolean isFetcher() {
        return fetcher;
    }

    public void setFetcher(boolean fetcher) {
        this.fetcher = fetcher;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean isPlantaskreadtorun() {
        return plantaskreadtorun;
    }

    public void setPlantaskreadtorun(boolean plantaskreadtorun) {
        this.plantaskreadtorun = plantaskreadtorun;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public long getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(long fetchSize) {
        this.fetchSize = fetchSize;
    }

    public String getUsearea() {
        return usearea;
    }

    public void setUsearea(String usearea) {
        this.usearea = usearea;
    }

    public String getAreafield() {
        return areafield;
    }

    public void setAreafield(String areafield) {
        this.areafield = areafield;
    }

    public String getAreafieldtype() {
        return areafieldtype;
    }

    public void setAreafieldtype(String areafieldtype) {
        this.areafieldtype = areafieldtype;
    }

    public String getArearule() {
        return arearule;
    }

    public void setArearule(String arearule) {
        this.arearule = arearule;
    }

    public String getMinareavalue() {
        return minareavalue;
    }

    public void setMinareavalue(String minareavalue) {
        this.minareavalue = minareavalue;
    }

    public String getMaxareavalue() {
        return maxareavalue;
    }

    public void setMaxareavalue(String maxareavalue) {
        this.maxareavalue = maxareavalue;
    }

    public String getFormatstr() {
        return formatstr;
    }

    public void setFormatstr(String formatstr) {
        this.formatstr = formatstr;
    }

    public String getTaskinfo() {
        return taskinfo;
    }

    public void setTaskinfo(String taskinfo) {
        this.taskinfo = taskinfo;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getRunserver() {
        return runserver;
    }

    public void setRunserver(String runserver) {
        this.runserver = runserver;
    }

    public String getActype() {
        return actype;
    }

    public void setActype(String actype) {
        this.actype = actype;
    }

    public String getFilterid() {
        return filterid;
    }

    public void setFilterid(String filterid) {
        this.filterid = filterid;
    }

    public String getDistype() {
        return distype;
    }

    public void setDistype(String distype) {
        this.distype = distype;
    }

    public String getDistpolicy() {
        return distpolicy;
    }

    public void setDistpolicy(String distpolicy) {
        this.distpolicy = distpolicy;
    }

    public int getPolicynum() {
        return policynum;
    }

    public void setPolicynum(int policynum) {
        this.policynum = policynum;
    }

    public String getBusstype() {
        return busstype;
    }

    public void setBusstype(String busstype) {
        this.busstype = busstype;
    }

    @Transient
    public String getExceptionMsg() {
        return exceptionMsg;
    }

    public void setExceptionMsg(String exceptionMsg) {
        this.exceptionMsg = exceptionMsg;
    }

    @Transient
    public Reporter getReport() {
        return report;
    }

    public void setReport(Reporter report) {
        this.report = report;
    }

    public String getExecmd() {
        return execmd;
    }

    public void setExecmd(String execmd) {
        this.execmd = execmd;
    }

    public int getDisnum() {
        return disnum;
    }

    public void setDisnum(int disnum) {
        this.disnum = disnum;
    }

    public String getExectarget() {
        return exectarget;
    }

    public void setExectarget(String exectarget) {
        this.exectarget = exectarget;
    }

    public String getExectype() {
        return exectype;
    }

    public void setExectype(String exectype) {
        this.exectype = exectype;
    }

    public String getExecto() {
        return execto;
    }

    public void setExecto(String execto) {
        this.execto = execto;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public String getSiptrunk() {
        return siptrunk;
    }

    public void setSiptrunk(String siptrunk) {
        this.siptrunk = siptrunk;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isPrefix() {
        return prefix;
    }

    public void setPrefix(boolean prefix) {
        this.prefix = prefix;
    }

    public String getReportid() {
        return reportid;
    }

    public void setReportid(String reportid) {
        this.reportid = reportid;
    }

    public boolean isMapping() {
        return mapping;
    }

    public void setMapping(boolean mapping) {
        this.mapping = mapping;
    }
}
