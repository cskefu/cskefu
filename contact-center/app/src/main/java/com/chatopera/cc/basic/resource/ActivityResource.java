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
package com.chatopera.cc.basic.resource;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.model.*;
import com.chatopera.cc.persistence.impl.BatchDataProcess;
import com.chatopera.cc.persistence.impl.ESDataExchangeImpl;
import com.chatopera.cc.persistence.repository.*;
import com.chatopera.cc.util.es.SearchTools;
import com.chatopera.cc.util.es.UKDataBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ActivityResource extends Resource {

    private final JobDetail jobDetail;
    private final FormFilterRepository formFilterRes;
    private final FormFilterItemRepository formFilterItemRes;
    private final UKefuCallOutTaskRepository callOutTaskRes;
    private final UKefuCallOutFilterRepository callOutFilterRes;
    private final JobDetailRepository batchRes;
    private final MetadataRepository metadataRes;
    /***分配到坐席***/
    private final AtomicInteger assignOrganInt = new AtomicInteger();
    /***分配到部门***/
    private final AtomicInteger assignInt = new AtomicInteger();
    /***分配到AI***/
    private final AtomicInteger assignAiInt = new AtomicInteger();
    private final AtomicInteger atomInt = new AtomicInteger();
    private final BatchDataProcess batchDataProcess;
    private PageImpl<UKDataBean> dataList;
    private MetadataTable metadataTable;
    private FormFilter formFilter = null;
    private List<CallAgent> callAgentList;
    private CallAgent current;
    private UKefuCallOutTask task;
    private UKefuCallOutFilter filter;
    private JobDetail batch;

    public ActivityResource(JobDetail jobDetail) {
        this.jobDetail = jobDetail;
        this.formFilterRes = MainContext.getContext().getBean(FormFilterRepository.class);
        this.formFilterItemRes = MainContext.getContext().getBean(FormFilterItemRepository.class);
        this.callOutTaskRes = MainContext.getContext().getBean(UKefuCallOutTaskRepository.class);
        this.callOutFilterRes = MainContext.getContext().getBean(UKefuCallOutFilterRepository.class);
        this.batchRes = MainContext.getContext().getBean(JobDetailRepository.class);
        this.metadataRes = MainContext.getContext().getBean(MetadataRepository.class);
        this.batchDataProcess = new BatchDataProcess(null, MainContext.getContext().getBean(ESDataExchangeImpl.class));
    }

    @Override
    public void begin() throws IOException {
        if (!StringUtils.isBlank(jobDetail.getFilterid())) {
            formFilter = formFilterRes.findByIdAndOrgi(jobDetail.getFilterid(), this.jobDetail.getOrgi());
            List<FormFilterItem> formFilterList = formFilterItemRes.findByOrgiAndFormfilterid(this.jobDetail.getOrgi(), jobDetail.getFilterid());
            if (formFilter != null && !StringUtils.isBlank(formFilter.getFiltertype())) {
                if (formFilter.getFiltertype().equals(MainContext.FormFilterType.BATCH.toString())) {
                    batch = batchRes.findByIdAndOrgi(formFilter.getBatid(), this.jobDetail.getOrgi());
                    if (batch != null && !StringUtils.isBlank(batch.getActid())) {
                        metadataTable = metadataRes.findByTablename(batch.getActid());
                    }
                } else {    //业务表
                    String tableid = formFilter.getTableid();
                    if (!StringUtils.isBlank(tableid)) {
                        metadataTable = metadataRes.findById(tableid)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Metadata %s not found", tableid)));
                    }
                }
            }
            if (metadataTable != null) {
                /*
                 * 只加载 未分配的有效名单数据
                 */
                if (isRecovery()) {
                    //回收数据 , 需要传入回收的目标  ： 包括 批次ID，任务ID，筛选ID，活动ID
                    dataList = SearchTools.recoversearch(this.jobDetail.getOrgi(), this.jobDetail.getExectype(), this.jobDetail.getExectarget(), metadataTable, (int) Math.ceil(this.jobDetail.getStartindex() / 50000.0), 50000);
                } else {
                    dataList = SearchTools.dissearch(this.jobDetail.getOrgi(), formFilterList, metadataTable, (int) Math.ceil(this.jobDetail.getStartindex() / 50000.0), 50000);
                }
            }
            this.callAgentList = MainContext.getContext().getBean(CallAgentRepository.class).findByActidAndOrgi(this.jobDetail.getId(), this.jobDetail.getOrgi());
            /*
             * 生成 活动任务， 然后完成分配 , 同时还需要生成 筛选表单的筛选记录 ， 在后台管理界面上可以看到
             */
            if (this.callAgentList != null && this.callAgentList.size() > 0) {
                this.current = this.callAgentList.remove(0);
            }

            this.jobDetail.setExecnum(this.jobDetail.getExecnum() + 1);

            if (this.isRecovery() && !StringUtils.isBlank(this.jobDetail.getExectype()) && (this.jobDetail.getExectype().equals("filterid") || this.jobDetail.getExectype().equals("filterskill") || this.jobDetail.getExectype().equals("taskskill") || this.jobDetail.getExectype().equals("taskid"))) {
                if (this.jobDetail.getExectype().equals("filterid") || this.jobDetail.getExectype().equals("filterskill")) {
                    this.filter = this.callOutFilterRes.findByIdAndOrgi(this.jobDetail.getExectarget(), this.jobDetail.getOrgi());
                } else if (this.jobDetail.getExectype().equals("taskid") || this.jobDetail.getExectype().equals("taskskill")) {
                    this.task = this.callOutTaskRes.findByIdAndOrgi(this.jobDetail.getExectarget(), this.jobDetail.getOrgi());
                }
            } else {
                task = new UKefuCallOutTask();
                task.setName(this.jobDetail.getName() + "_" + MainUtils.dateFormate.format(new Date()));
                task.setBatid(formFilter.getBatid());

                task.setOrgi(this.jobDetail.getOrgi());

                if (this.isRecovery()) {
                    task.setExectype(MainContext.ActivityExecType.RECOVERY.toString());
                } else {
                    task.setExectype(MainContext.ActivityExecType.DEFAULT.toString());
                }

                task.setFilterid(formFilter.getId());
                task.setActid(this.jobDetail.getId());

                task.setExecnum(this.jobDetail.getExecnum());

                task.setOrgan(this.jobDetail.getOrgan());

                task.setCreatetime(new Date());
                if (this.dataList != null) {
                    task.setNamenum((int) this.dataList.getTotalElements());
                    task.setNotassigned((int) this.dataList.getTotalElements());
                }

                this.callOutTaskRes.save(task);

                filter = new UKefuCallOutFilter();

                formFilter.setExecnum(formFilter.getExecnum() + 1);

                MainUtils.copyProperties(task, filter);
                filter.setName(this.formFilter.getName() + "_" + MainUtils.dateFormate.format(new Date()));
                filter.setExecnum(formFilter.getExecnum());
                this.callOutFilterRes.save(filter);
            }
        }
    }

    @Override
    public void end(boolean clear) throws IOException {
        if (this.atomInt.intValue() > 0) {
            this.batchDataProcess.end();
        }
        //doNothing
        /*
         * FormFilter的执行信息更新，执行次数
         */
        if (formFilterRes != null && this.formFilter != null) {
            this.formFilter.setFilternum(this.formFilter.getFilternum() + 1);
            formFilterRes.save(this.formFilter);
        }
        /*
         * 批次的信息更新，批次剩余未分配的名单总数 ， 已分配的名单总数
         */
        if (this.batchRes != null && this.batch != null) {
            if (this.isRecovery()) {
                batch.setAssigned(batch.getAssigned() - this.atomInt.intValue());
            } else {
                batch.setAssigned(batch.getAssigned() + this.atomInt.intValue());
            }
            batch.setNotassigned(batch.getNamenum() - batch.getAssigned());
            this.batchRes.save(batch);
        }
        if (this.task != null) {
            if (this.isRecovery()) {
                if (!StringUtils.isBlank(this.jobDetail.getExecto())) {
                    this.task.setReorgannum(this.atomInt.intValue());
                } else {
                    this.task.setRenum(this.atomInt.intValue());
                }
            } else {
                this.task.setAssigned(this.assignInt.intValue());
                this.task.setAssignedorgan(this.assignOrganInt.intValue());
                this.task.setAssignedai(this.assignAiInt.intValue());
                this.task.setNotassigned(this.task.getNamenum() - this.assignInt.intValue() - this.assignOrganInt.intValue() - this.assignAiInt.intValue());
            }
            this.callOutTaskRes.save(this.task);
        }
        if (this.filter != null) {
            if (this.isRecovery()) {
                if (!StringUtils.isBlank(this.jobDetail.getExecto())) {
                    this.filter.setReorgannum(this.atomInt.intValue());
                } else {
                    this.filter.setRenum(this.atomInt.intValue());
                }
            } else {
                this.filter.setAssigned(this.assignInt.intValue());
                this.filter.setAssignedorgan(this.assignOrganInt.intValue());
                this.filter.setAssignedai(this.assignAiInt.intValue());
                this.filter.setNotassigned(this.task.getNamenum() - this.assignInt.intValue() - this.assignOrganInt.intValue() - this.assignAiInt.intValue());
            }
            this.callOutFilterRes.save(this.filter);
        }

        /*
         * 更新任务状态，记录生成的任务信息
         */
        this.jobDetail.setExecmd(null);
        this.jobDetail.setExectype(null);
        this.jobDetail.setExectarget(null);
        this.jobDetail.setExecto(null);
    }

    @Override
    public JobDetail getJob() {
        return this.jobDetail;
    }

    @Override
    public void process(@NonNull OutputTextFormat meta, JobDetail job) {
        /*
         * 执行分配
         */
        Map<String, Object> values = meta.getDataBean().getValues();
        if (this.isRecovery()) {
            if (!StringUtils.isBlank(this.jobDetail.getExecto())) {
                values.put(Constants.CSKEFU_SYSTEM_DIS_AGENT, null);
                values.put(Constants.CSKEFU_SYSTEM_DIS_AI, null);
//				meta.getDataBean().getValues().put(Constants.CSKEFU_SYSTEM_DIS_ORGAN, this.jobDetail.getExecto()) ;
                values.put(Constants.CSKEFU_SYSTEM_DIS_TIME, System.currentTimeMillis());
                values.put("status", MainContext.NamesDisStatusType.DISORGAN.toString());
            } else {
                values.put(Constants.CSKEFU_SYSTEM_DIS_AI, null);
                values.put(Constants.CSKEFU_SYSTEM_DIS_AGENT, null);
                values.put(Constants.CSKEFU_SYSTEM_DIS_ORGAN, null);
                values.put(Constants.CSKEFU_SYSTEM_DIS_TIME, null);
                values.put("status", MainContext.NamesDisStatusType.NOT.toString());
            }
        } else {
            if (this.current != null && meta.getDataBean() != null) {
                this.current.getDisnames().incrementAndGet();
                /*
                 *
                 */
                values.put(Constants.CSKEFU_SYSTEM_DIS_TIME, System.currentTimeMillis());

                values.put("actid", this.jobDetail.getId());
                values.put("metaid", this.metadataTable.getTablename());
                values.put("batid", this.formFilter.getBatid());

                values.put("filterid", this.formFilter.getId());
                values.put("calloutfilid", this.filter.getId());

                values.put("taskid", this.task.getId());


                if (!StringUtils.isBlank(this.jobDetail.getUserid())) {
                    values.put("assuser", this.jobDetail.getUserid());
                } else {
                    values.put("assuser", this.jobDetail.getCreater());
                }
                /*
                 * 任务ID
                 */

                if ("agent".equals(this.current.getDistype())) {
                    values.put("status", MainContext.NamesDisStatusType.DISAGENT.toString());
                    values.put(Constants.CSKEFU_SYSTEM_DIS_AGENT, this.current.getDistarget());
                    values.put(Constants.CSKEFU_SYSTEM_DIS_ORGAN, this.current.getOrgan());
                    this.assignInt.incrementAndGet();
                } else if ("skill".equals(this.current.getDistype())) {
                    values.put("status", MainContext.NamesDisStatusType.DISORGAN.toString());
                    values.put(Constants.CSKEFU_SYSTEM_DIS_ORGAN, this.current.getDistarget());
                    this.assignOrganInt.incrementAndGet();
                } else if ("ai".equals(this.current.getDistype())) {
                    values.put("status", MainContext.NamesDisStatusType.DISAI.toString());
                    values.put(Constants.CSKEFU_SYSTEM_DIS_AI, this.current.getDistarget());
                    values.put(Constants.CSKEFU_SYSTEM_DIS_ORGAN, this.current.getOrgan());
                    this.assignAiInt.incrementAndGet();
                }
            }
        }
        values.put("updatetime", System.currentTimeMillis());

        /*
         * 更新记录（是否同时保存分配信息，以便于查看分配历史？）
         */
        batchDataProcess.process(meta.getDataBean());
    }

    @Override
    public OutputTextFormat next() {
        OutputTextFormat outputTextFormat = null;
        if (this.dataList != null && this.current != null) {
            synchronized (ActivityResource.class) {
                if (atomInt.intValue() < this.dataList.getContent().size()) {
                    if (this.isRecovery()) {
                        UKDataBean dataBean = this.dataList.getContent().get(atomInt.intValue());
                        outputTextFormat = new OutputTextFormat(this.jobDetail);
                        if (this.formFilter != null) {
                            outputTextFormat.setTitle(this.formFilter.getName());
                        }
                        outputTextFormat.setDataBean(dataBean);
                        atomInt.incrementAndGet();
                    } else if (this.dataList != null) {
                        if (this.current.getDisnames().intValue() >= this.current.getDisnum()) {
                            if (this.callAgentList.size() > 0) {
                                this.current = this.callAgentList.remove(0);
                            } else {
                                this.current = null;
                            }
                        }
                        if (this.current != null) {
                            UKDataBean dataBean = this.dataList.getContent().get(atomInt.intValue());
                            outputTextFormat = new OutputTextFormat(this.jobDetail);
                            if (this.formFilter != null) {
                                outputTextFormat.setTitle(this.formFilter.getName());
                            }
                            outputTextFormat.setDataBean(dataBean);

                            atomInt.incrementAndGet();

                            /*
                             * 修改为平均分配的方式 ， 每个坐席或者部门评价分配
                             */
                            this.callAgentList.add(this.current);
                            if (this.callAgentList.size() > 0) {
                                this.current = this.callAgentList.remove(0);
                            }
                        }
                    }
                }
            }
        }
        return outputTextFormat;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public OutputTextFormat getText(OutputTextFormat object) {
        return object;
    }

    @Override
    public void rmResource() {
        /*
         * 啥也不做
         */
    }

    @Override
    public void updateTask() {
        /*
         * 更新任务状态，记录生成的任务信息
         */
        this.jobDetail.setExecmd(null);
        this.jobDetail.setExectype(null);
        this.jobDetail.setExectarget(null);
        this.jobDetail.setExecto(null);
    }

    private boolean isRecovery() {
        return StringUtils.isNotBlank(this.jobDetail.getExecmd()) && this.jobDetail.getExecmd().equals("recovery");
    }
}
