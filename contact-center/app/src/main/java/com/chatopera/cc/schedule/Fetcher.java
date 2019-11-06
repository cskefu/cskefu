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
package com.chatopera.cc.schedule;

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.basic.MainUtils;
import com.chatopera.cc.basic.resource.OutputTextFormat;
import com.chatopera.cc.basic.resource.Resource;
import com.chatopera.cc.cache.Cache;
import com.chatopera.cc.model.JobDetail;
import com.chatopera.cc.persistence.repository.ReporterRepository;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class Fetcher implements Runnable {

    private static ReporterRepository reporterRes;
    private static Cache cache;
    private JobDetail job = null;
    private AtomicInteger activeThreads = new AtomicInteger(0);
    private AtomicInteger pages = new AtomicInteger(0); // total pages fetched
    private AtomicInteger errors = new AtomicInteger(0); // total pages fetched
    private Resource resource = null;
    private int processpages = 0;

    /**
     * 构建任务信息
     *
     * @param job
     */
    public Fetcher(JobDetail job) throws Exception {
        this.job = job;
        try {
            if (job != null && job.getTasktype() != null) {
                resource = Resource.getResource(job);
            }
            /**
             * 初始化资源
             */

            if (resource != null) {
                resource.begin();
            }
            this.job.setLastindex(job.getStartindex());
            this.pages = new AtomicInteger((int) job.getReport().getPages()); // total pages fetched
            processpages = this.pages.intValue();
            job.getReport().setDataid(this.job.getId());
        } catch (Exception e1) {
            String msg = "TaskID:" + job.getId() + " TaskName:" + job.getName() + " TaskType:" + job.getTasktype() + " Date:" + new Date() + " Exception:" + e1.getMessage();
            job.setExceptionMsg(ExceptionUtils.getMessage(e1));

            /**
             * 设置错误代码
             */
            e1.printStackTrace();
            throw new Exception(msg, e1);
        }
    }

    public void run() {
        job.getReport().setThreads(1);
        job.getReport().setStarttime(new java.util.Date());
        try {
            synchronized (activeThreads) {
                activeThreads.incrementAndGet(); // count threads
            }
            reportStatus();
            OutputTextFormat obj;
            while (job.isFetcher() && resource != null && (obj = resource.next()) != null) {
                try {
                    while (job.isPause() && job.isFetcher()) {
                        Thread.sleep(1000);
                    }
                    if (obj != null) {
                        output(obj);
                    }
                } catch (Throwable t) { // unexpected exception
                    // unblock
                    job.setExceptionMsg(t.getMessage());
                    t.printStackTrace();
                    errors.incrementAndGet();
                    break;
                }
            }
            /**
             *
             */
        } catch (Throwable e) {
            e.printStackTrace();
            job.setExceptionMsg(e.getMessage());
        } finally {
            this.job.getReport().setErrors(this.job.getReport().getErrors() + errors.intValue());
            if (resource != null) {
                /**
                 * end中包含了 Close 方法
                 */
                try {
                    reportStatus();
                    this.resource.end(this.pages.intValue() == processpages);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.job.getReport().setOrgi(this.job.getOrgi());
            this.job.getReport().setDataid(this.job.getId());
            this.job.getReport().setTitle(this.job.getName() + "_" + MainUtils.dateFormate.format(new Date()));

            this.job.getReport().setUserid(this.job.getCreater());
            this.job.getReport().setUsername(this.job.getUsername());
            this.job.getReport().setEndtime(new Date());
            this.job.getReport().setTotal(this.pages.intValue());
            this.job.getReport().setAmount(String.valueOf((this.job.getReport().getEndtime().getTime() - this.job.getReport().getStart()) / 1000f));

            Fetcher.getReporterRes().save(this.job.getReport());
            synchronized (activeThreads) {
                activeThreads.decrementAndGet(); // count threads
            }
        }
    }

    private void output(OutputTextFormat object) throws Exception {
        try {
            this.reportStatus();
            OutputTextFormat outputTextFormat = resource.getText(object);
            if (outputTextFormat == null) {
                return;
            } else {
                pages.incrementAndGet();
                resource.process(outputTextFormat, job);
                job.setStartindex(job.getStartindex() + 1);
            }
            reportStatus();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @throws IOException
     */
    private void reportStatus() throws IOException {
        if (this.job != null && this.job.getReport() != null) {
            this.job.getReport().setPages(this.pages.intValue());
            this.job.getReport().setThreads(activeThreads.intValue());
            this.job.getReport().setStatus(new StringBuffer().append("已处理：").append(this.job.getReport().getPages()).append(", 错误：").append(this.job.getReport().getErrors()).append("，处理速度：").append(job.getReport().getSpeed()).append("条/秒，线程数：").append(this.job.getReport().getThreads()).append(this.job.getReport().getDetailmsg() != null ? "，详细信息：" + this.job.getReport().getDetailmsg() : "").toString());
            Fetcher.getCache().putJobByIdAndOrgi(job.getId(), job.getOrgi(), job);
        }
    }

    private static ReporterRepository getReporterRes() {
        if (reporterRes == null)
            reporterRes = MainContext.getContext().getBean(ReporterRepository.class);
        return reporterRes;
    }


    private static Cache getCache(){
        if(cache == null)
            cache = MainContext.getContext().getBean(Cache.class);
        return cache;
    }
}