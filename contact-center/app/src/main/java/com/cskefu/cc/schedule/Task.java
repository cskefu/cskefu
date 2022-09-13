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
package com.cskefu.cc.schedule;

import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.model.JobDetail;
import com.cskefu.cc.model.Reporter;
import com.cskefu.cc.persistence.repository.JobDetailRepository;
import com.cskefu.cc.persistence.repository.ReporterRepository;
import com.cskefu.cc.util.TaskTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class Task implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Task.class);

    private JobDetail jobDetail;
    private JobDetailRepository jobDetailRes;

    public Task(JobDetail jobDetail, JobDetailRepository jobDetailRes) {
        this.jobDetail = jobDetail;
        this.jobDetailRes = jobDetailRes;
    }

    @Override
    public void run() {
        try {
            /**
             * 首先从  等待执行的队列中找到优先级最高的任务，然后将任务放入到  执行队列
             */
            if (jobDetail != null) {
                /**
                 * 开始启动执行线程
                 */
                jobDetail.setTaskfiretime(new Date());
                jobDetail.setTaskstatus(MainContext.TaskStatusType.RUNNING.getType());
                jobDetailRes.save(jobDetail);
                /**
                 * 任务开始执行
                 */
                if (true) {
                    if (jobDetail.getReport() == null) {
                        jobDetail.setReport(new Reporter());
                        MainContext.getContext().getBean(ReporterRepository.class).save(jobDetail.getReport());
                    }
                    if (jobDetail.isFetcher()) {//while (jobDetail.isFetcher()) {
                        new Fetcher(jobDetail).run();
                    }
                }
            }

        } catch (Exception e) {
            logger.error("error during execution", e);
        } finally {
            /**
             * 任务开始执行，执行完毕后 ，任务状态回执为  NORMAL
             */
            if (jobDetail.getCronexp() != null && jobDetail.getCronexp().length() > 0 && jobDetail.isPlantask() && !"operation".equals(jobDetail.getCrawltaskid())) {
                jobDetail.setNextfiretime(TaskTools.updateTaskNextFireTime(jobDetail));
            }
            jobDetail.setStartindex(0);    //将分页位置设置为从头开始，对数据采集有效，对RivuES增量采集无效
            jobDetail.setFetcher(true);
            jobDetail.setPause(false);
            jobDetail.setTaskstatus(MainContext.TaskStatusType.NORMAL.getType());
            jobDetail.setCrawltaskid(null);
			jobDetail.setLastdate(new Date()) ;
            jobDetailRes.save(jobDetail);

            /**
             * 存储历史信息
             */
            MainContext.getCache().deleteJobByJobIdAndOrgi(this.jobDetail.getId(), this.jobDetail.getOrgi());
        }
    }
}
