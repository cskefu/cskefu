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
package com.chatopera.cc.util;

import com.chatopera.cc.basic.Constants;
import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.model.*;
import com.chatopera.cc.persistence.repository.*;
import com.chatopera.cc.proxy.UserProxy;
import com.chatopera.cc.util.es.SearchTools;
import com.chatopera.cc.util.es.UKDataBean;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.ui.ModelMap;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

public class CallCenterUtils {


    private static UserProxy userProxy;

    /**
     * @param extno
     * @param sipTrunkRes
     * @param extRes
     * @return
     */
    public static SipTrunk siptrunk(String extno, SipTrunkRepository sipTrunkRes, ExtentionRepository extRes) {
        SipTrunk sipTrunk = null;
        List<Extention> extList = extRes.findByExtention(extno);
        if (extList.size() > 0) {
            Extention ext = extList.get(0);
            if (StringUtils.isNotBlank(ext.getSiptrunk())) {
                sipTrunk = MainContext.getCache().findOneSystemByIdAndOrgi(ext.getSiptrunk(), ext.getOrgi());
                if (sipTrunk == null) {
                    sipTrunk = sipTrunkRes.findByIdAndOrgi(ext.getSiptrunk(), ext.getOrgi());
                    if (sipTrunk != null) {
                        MainContext.getCache().putSystemByIdAndOrgi(sipTrunk.getId(), ext.getOrgi(), sipTrunk);
                    }
                }
            } else {
                List<SipTrunk> sipTrunkList = sipTrunkRes.findByDefaultsipAndOrgi(true, ext.getOrgi());
                if (sipTrunkList.size() > 0) {
                    sipTrunk = sipTrunkList.get(0);
                }
            }
        }
        return sipTrunk;
    }


    /**
     * @param name
     * @param sipTrunkRes
     * @return
     */
    public static SipTrunk siptrunk(String name, SipTrunkRepository sipTrunkRes) {
        SipTrunk sipTrunk = null;
        List<SipTrunk> sipTrunkList = sipTrunkRes.findByName(name);
        if (sipTrunkList.size() > 0) {
            sipTrunk = sipTrunkList.get(0);
        } else {
            sipTrunkList = sipTrunkRes.findByDefaultsip(true);
            if (sipTrunkList.size() > 0) {
                sipTrunk = sipTrunkList.get(0);
            }
        }
        if (sipTrunk != null) {
            MainContext.getCache().putSystemByIdAndOrgi(sipTrunk.getId(), sipTrunk.getOrgi(), sipTrunk);
        }
        return sipTrunk;
    }

    /**
     * 我的部门以及授权给我的部门
     *
     * @param userRoleRes
     * @param callOutRoleRes
     * @param user
     * @return
     */
    public static List<String> getAuthOrgan(UserRoleRepository userRoleRes, UKefuCallOutRoleRepository callOutRoleRes, User user) {
        List<UserRole> userRole = userRoleRes.findByOrgiAndUser(user.getOrgi(), user);
        ArrayList<String> organList = new ArrayList<String>();
        if (userRole.size() > 0) {
            for (UserRole userTemp : userRole) {
                UKefuCallOutRole roleOrgan = callOutRoleRes.findByOrgiAndRoleid(
                        user.getOrgi(),
                        userTemp.getRole().getId());
                if (roleOrgan != null) {
                    if (StringUtils.isNotBlank(roleOrgan.getOrganid())) {
                        String[] organ = roleOrgan.getOrganid().split(",");
                        for (int i = 0; i < organ.length; i++) {
                            organList.add(organ[i]);
                        }

                    }
                }
            }
        }

        getUserProxy().attachOrgansPropertiesForUser(user);
        if (user.getAffiliates().size() > 0) {
            for (final String organ : user.getAffiliates()) {
                organList.add(organ);
            }
        }

        return organList;
    }

    /**
     * 我的部门以及授权给我的部门 - 批次
     *
     * @param batchRes
     * @param userRoleRes
     * @param callOutRoleRes
     * @param user
     * @return
     */
    public static List<JobDetail> getBatchList(JobDetailRepository batchRes, UserRoleRepository userRoleRes, UKefuCallOutRoleRepository callOutRoleRes, final User user) {

        //final List<String> organList = CallCenterUtils.getAuthOrgan(userRoleRes, callOutRoleRes, user);

        final List<String> organList = CallCenterUtils.getExistOrgan(user);

        List<JobDetail> batchList = batchRes.findAll(new Specification<JobDetail>() {
            @Override
            public Predicate toPredicate(
                    Root<JobDetail> root, CriteriaQuery<?> query,
                    CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                In<Object> in = cb.in(root.get("organ"));

                list.add(cb.equal(root.get("orgi").as(String.class), user.getOrgi()));
                list.add(cb.equal(root.get("tasktype").as(String.class), MainContext.TaskType.BATCH.toString()));

                if (organList.size() > 0) {

                    for (String id : organList) {
                        in.value(id);
                    }
                } else {
                    in.value(Constants.CSKEFU_SYSTEM_NO_DAT);
                }
                list.add(in);

                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        });

        return batchList;
    }

    /**
     * 我的部门以及授权给我的部门 - 筛选表单
     *
     * @param filterRes
     * @param userRoleRes
     * @param callOutRoleRes
     * @param user
     * @return
     */
    public static List<FormFilter> getFormFilterList(FormFilterRepository filterRes, UserRoleRepository userRoleRes, UKefuCallOutRoleRepository callOutRoleRes, final User user) {

        //final List<String> organList = CallCenterUtils.getAuthOrgan(userRoleRes, callOutRoleRes, user);

        final List<String> organList = CallCenterUtils.getExistOrgan(user);

        List<FormFilter> formFilterList = filterRes.findAll(new Specification<FormFilter>() {
            @Override
            public Predicate toPredicate(
                    Root<FormFilter> root, CriteriaQuery<?> query,
                    CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                In<Object> in = cb.in(root.get("organ"));

                list.add(cb.equal(root.get("orgi").as(String.class), user.getOrgi()));

                if (organList.size() > 0) {

                    for (String id : organList) {
                        in.value(id);
                    }
                } else {
                    in.value(Constants.CSKEFU_SYSTEM_NO_DAT);
                }
                list.add(in);

                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        });

        return formFilterList;
    }

    /**
     * 我的部门以及授权给我的部门 - 活动
     *
     * @param batchRes
     * @param userRoleRes
     * @param callOutRoleRes
     * @param user
     * @return
     */
    public static List<JobDetail> getActivityList(JobDetailRepository batchRes, UserRoleRepository userRoleRes, UKefuCallOutRoleRepository callOutRoleRes, final User user) {

        //final List<String> organList = CallCenterUtils.getAuthOrgan(userRoleRes, callOutRoleRes, user);

        final List<String> organList = CallCenterUtils.getExistOrgan(user);

        List<JobDetail> activityList = batchRes.findAll(new Specification<JobDetail>() {
            @Override
            public Predicate toPredicate(
                    Root<JobDetail> root, CriteriaQuery<?> query,
                    CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                In<Object> in = cb.in(root.get("organ"));

                list.add(cb.equal(root.get("orgi").as(String.class), user.getOrgi()));
                list.add(cb.equal(root.get("tasktype").as(String.class), MainContext.TaskType.ACTIVE.toString()));

                if (organList.size() > 0) {

                    for (String id : organList) {
                        in.value(id);
                    }
                } else {
                    in.value(Constants.CSKEFU_SYSTEM_NO_DAT);
                }
                list.add(in);

                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        });

        return activityList;
    }

    /**
     * 查询条件，下拉信息返回
     *
     * @param map
     * @param user
     * @param ownerdept
     * @param actid
     */
    public static void getAllCallOutList(ModelMap map, User user, String ownerdept, String actid) {
        JobDetailRepository batchRes = MainContext.getContext().getBean(JobDetailRepository.class);
        UserRoleRepository userRoleRes = MainContext.getContext().getBean(UserRoleRepository.class);
        UKefuCallOutRoleRepository callOutRoleRes = MainContext.getContext().getBean(UKefuCallOutRoleRepository.class);
        FormFilterRepository filterRes = MainContext.getContext().getBean(FormFilterRepository.class);
        OrganRepository organRes = MainContext.getContext().getBean(OrganRepository.class);

        List<JobDetail> activityList = CallCenterUtils.getActivityList(batchRes, userRoleRes, callOutRoleRes, user);
        List<SaleStatus> salestatusList = new ArrayList<>();
        for (JobDetail act : activityList) {
            List<SaleStatus> salestastus = MainContext.getContext().getBean(
                    SaleStatusRepository.class).findByOrgiAndActivityid(user.getOrgi(), act.getDicid());
            salestatusList.addAll(salestastus);

        }
        LinkedHashSet<SaleStatus> set = new LinkedHashSet<SaleStatus>(salestatusList.size());
        set.addAll(salestatusList);
        salestatusList.clear();
        salestatusList.addAll(set);
        map.put("salestatusList", salestatusList);
        map.put("batchList", CallCenterUtils.getBatchList(batchRes, userRoleRes, callOutRoleRes, user));
        map.put("activityList", CallCenterUtils.getActivityList(batchRes, userRoleRes, callOutRoleRes, user));
        map.put("formFilterList", CallCenterUtils.getFormFilterList(filterRes, userRoleRes, callOutRoleRes, user));
        if (StringUtils.isBlank(ownerdept)) {

            map.addAttribute(
                    "owneruserList",
                    getUserProxy().findByOrganAndOrgiAndDatastatus(
                            Constants.CSKEFU_SYSTEM_NO_DAT, user.getOrgi(), false));
        } else {
            map.addAttribute(
                    "owneruserList", getUserProxy().findByOrganAndOrgiAndDatastatus(ownerdept, user.getOrgi(), false));

        }
        map.addAttribute(
                "skillGroups", organRes.findAll(CallCenterUtils.getAuthOrgan(userRoleRes, callOutRoleRes, user)));
        map.put(
                "taskList", MainContext.getContext().getBean(UKefuCallOutTaskRepository.class).findByActidAndOrgi(
                        actid,
                        user.getOrgi()));
        map.put(
                "allUserList",
                MainContext.getContext().getBean(UserRepository.class).findByOrgiAndDatastatus(user.getOrgi(), false));
        //JobDetail act = batchRes.findByIdAndOrgi(actid, user.getOrgi());
        //if(act != null){
        //	map.put("salestatusList",MainContext.getContext().getBean(SaleStatusRepository.class).findByOrgiAndActivityid(user.getOrgi(), act.getDicid()));
        //}
        map.addAttribute("statusList", Dict.getInstance().getDic("com.dic.callout.activity"));
    }

    /**
     * 指定活动，已设置的分配数
     *
     * @param map
     * @param activityid
     * @param user
     */
    public static void getNamenum(ModelMap map, String activityid, User user) {

        CallAgentRepository callAgentRes = MainContext.getContext().getBean(CallAgentRepository.class);

        List<CallAgent> actList = callAgentRes.findByOrgiAndActid(user.getOrgi(), activityid);
        int namenum = 0;
        if (actList.size() > 0) {
            for (CallAgent callAgent : actList) {
                if (callAgent.getDisnum() > 0) {
                    namenum = namenum + callAgent.getDisnum();
                }
            }
        }
        map.put("namenum", namenum);
    }

    /**
     * 查询目前存在的部门
     * 已分配部门的坐席，如果部门被删之后，这个方法可以把这些用户过滤掉
     *
     * @param user
     * @return
     */
    public static List<String> getExistOrgan(User user) {

        UserRoleRepository userRoleRes = MainContext.getContext().getBean(UserRoleRepository.class);
        UKefuCallOutRoleRepository callOutRoleRes = MainContext.getContext().getBean(UKefuCallOutRoleRepository.class);
        OrganRepository organRes = MainContext.getContext().getBean(OrganRepository.class);

        final List<String> organList = CallCenterUtils.getAuthOrgan(userRoleRes, callOutRoleRes, user);

        List<Organ> organAllList = organRes.findByOrgi(user.getOrgi());

        final List<String> tempList = new ArrayList<String>();

        for (String organid : organList) {
            for (Organ organ : organAllList) {
                if (organid.equals(organ.getId())) {
                    tempList.add(organid);
                }
            }
        }
        return tempList;
    }

    /**
     * 分配给坐席的名单，单个名单，回收到池子
     *
     * @param task
     * @param batch
     * @param ukefuCallOutFilter
     */
    public static void getAgentRenum(UKefuCallOutTask task, JobDetail batch, UKefuCallOutFilter ukefuCallOutFilter) {

        UKefuCallOutTaskRepository callOutTaskRes = MainContext.getContext().getBean(UKefuCallOutTaskRepository.class);
        JobDetailRepository batchRes = MainContext.getContext().getBean(JobDetailRepository.class);
        UKefuCallOutFilterRepository callOutFilterRes = MainContext.getContext().getBean(
                UKefuCallOutFilterRepository.class);

        //修改，拨打任务
        if (task != null) {
            task.setAssigned(task.getAssigned() - 1);//分配到坐席数
            task.setNotassigned(task.getNotassigned() + 1);//未分配数
            task.setRenum(task.getRenum() + 1);//回收到池子数
            callOutTaskRes.save(task);
        }

        //修改，批次
        if (batch != null) {
            batch.setAssigned(batch.getAssigned() - 1);//已分配
            batch.setNotassigned(batch.getNotassigned() + 1);//未分配
            batchRes.save(batch);
        }

        //修改，筛选记录
        if (ukefuCallOutFilter != null) {
            ukefuCallOutFilter.setAssigned(ukefuCallOutFilter.getAssigned() - 1);//分配给坐席数
            ukefuCallOutFilter.setRenum(ukefuCallOutFilter.getRenum() + 1);//回收到池子数
            ukefuCallOutFilter.setNotassigned(ukefuCallOutFilter.getNotassigned() + 1);//未分配数
            callOutFilterRes.save(ukefuCallOutFilter);
        }
    }

    /**
     * 分配给坐席的名单，单个名单，回收到部门
     *
     * @param task
     * @param ukefuCallOutFilter
     */
    public static void getAgentReorgannum(UKefuCallOutTask task, UKefuCallOutFilter ukefuCallOutFilter) {

        UKefuCallOutTaskRepository callOutTaskRes = MainContext.getContext().getBean(UKefuCallOutTaskRepository.class);
        UKefuCallOutFilterRepository callOutFilterRes = MainContext.getContext().getBean(
                UKefuCallOutFilterRepository.class);

        //修改，拨打任务
        if (task != null) {
            task.setNotassigned(task.getNotassigned() + 1);//未分配数
            task.setAssignedorgan(task.getAssignedorgan() - 1);//分配到部门数
            task.setReorgannum(task.getReorgannum() + 1);//回收到部门数
            callOutTaskRes.save(task);
        }


        //修改，筛选记录
        if (ukefuCallOutFilter != null) {
            ukefuCallOutFilter.setAssigned(ukefuCallOutFilter.getAssigned() - 1);//分配给坐席数
            ukefuCallOutFilter.setReorgannum(ukefuCallOutFilter.getReorgannum() + 1);//回收到部门数
            callOutFilterRes.save(ukefuCallOutFilter);
        }
    }

    /**
     * 分配给部门的名单，单个名单，回收到池子
     *
     * @param task
     * @param batch
     * @param ukefuCallOutFilter
     */
    public static void getOrganRenum(UKefuCallOutTask task, JobDetail batch, UKefuCallOutFilter ukefuCallOutFilter) {

        UKefuCallOutTaskRepository callOutTaskRes = MainContext.getContext().getBean(UKefuCallOutTaskRepository.class);
        JobDetailRepository batchRes = MainContext.getContext().getBean(JobDetailRepository.class);
        UKefuCallOutFilterRepository callOutFilterRes = MainContext.getContext().getBean(
                UKefuCallOutFilterRepository.class);

        //修改，拨打任务
        if (task != null) {
            task.setAssignedorgan(task.getAssignedorgan() - 1);//分配到部门数
            task.setNotassigned(task.getNotassigned() + 1);//未分配数
            task.setRenum(task.getRenum() + 1);//回收到池子数
            callOutTaskRes.save(task);
        }

        //修改，批次
        if (batch != null) {
            batch.setAssigned(batch.getAssigned() - 1);//已分配
            batch.setNotassigned(batch.getNotassigned() + 1);//未分配
            batchRes.save(batch);
        }

        //修改，筛选记录
        if (ukefuCallOutFilter != null) {
            ukefuCallOutFilter.setAssignedorgan(ukefuCallOutFilter.getAssignedorgan() - 1);//分配到部门数
            ukefuCallOutFilter.setRenum(ukefuCallOutFilter.getRenum() + 1);//回收到池子数
            ukefuCallOutFilter.setNotassigned(ukefuCallOutFilter.getNotassigned() + 1);//未分配数
            callOutFilterRes.save(ukefuCallOutFilter);
        }
    }

    /**
     * 获取指定活动，已分配的名单数
     *
     * @param actid
     * @param user
     * @return
     */
    public static int getActDisnum(@Valid String actid, User user, @Valid int p, @Valid int ps) {
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        queryBuilder.must(termQuery("actid", actid));// 活动ID
        queryBuilder.mustNot(termQuery("status", MainContext.NamesDisStatusType.NOT.toString()));
        PageImpl<UKDataBean> dataList = SearchTools.search(queryBuilder, p, ps);
        return dataList.getContent().size();
    }

    public static UserProxy getUserProxy() {
        if (userProxy == null) {
            userProxy = MainContext.getContext().getBean(UserProxy.class);
        }
        return userProxy;
    }
}
