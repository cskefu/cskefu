/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd. 
 * <https://www.chatopera.com>, Licensed under the Chunsong Public 
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2018- Jun. 2023 Chatopera Inc, <https://www.chatopera.com>,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Copyright (C) 2017 优客服-多渠道客服系统,  Licensed under the Apache License, Version 2.0, 
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cskefu.cc.util;

import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.model.*;
import com.cskefu.cc.persistence.repository.*;
import com.cskefu.cc.proxy.UserProxy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.ui.ModelMap;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class CallCenterUtils {


    private static UserProxy userProxy;

    /**
     * @param extno
     * @param sipTrunkRes
     * @param extRes
     * @return
     */
    public static SipTrunk siptrunk(String extno, SipTrunkRepository sipTrunkRes, ExtensionRepository extRes) {
        SipTrunk sipTrunk = null;
        List<Extension> extList = extRes.findByExtension(extno);
        if (extList.size() > 0) {
            Extension ext = extList.get(0);
            if (StringUtils.isNotBlank(ext.getSiptrunk())) {
                sipTrunk = MainContext.getCache().findOneSystemById(ext.getSiptrunk());
                if (sipTrunk == null) {
                    sipTrunk = sipTrunkRes.getReferenceById(ext.getSiptrunk());
                    if (sipTrunk != null) {
                        MainContext.getCache().putSystemById(sipTrunk.getId(), sipTrunk);
                    }
                }
            } else {
                List<SipTrunk> sipTrunkList = sipTrunkRes.findByDefaultsip(true);
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
            MainContext.getCache().putSystemById(sipTrunk.getId(), sipTrunk);
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
        List<UserRole> userRole = userRoleRes.findByUser(user);
        ArrayList<String> organList = new ArrayList<>();
        if (userRole.size() > 0) {
            for (UserRole userTemp : userRole) {
                UKefuCallOutRole roleOrgan = callOutRoleRes.findByRoleid(
                        userTemp.getRole().getId());
                if (roleOrgan != null) {
                    if (StringUtils.isNotBlank(roleOrgan.getOrganid())) {
                        String[] organ = roleOrgan.getOrganid().split(",");
                        for (String s : organ) {
                            organList.add(s);
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

        List<JobDetail> batchList = batchRes.findAll((root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            In<Object> in = cb.in(root.get("organ"));

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

        List<FormFilter> formFilterList = filterRes.findAll((root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            In<Object> in = cb.in(root.get("organ"));

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

        List<JobDetail> activityList = batchRes.findAll((root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();
            In<Object> in = cb.in(root.get("organ"));

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
                    SaleStatusRepository.class).findByActivityid(act.getDicid());
            salestatusList.addAll(salestastus);

        }
        LinkedHashSet<SaleStatus> set = new LinkedHashSet<>(salestatusList.size());
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
                    getUserProxy().findByOrganAndDatastatus(
                            Constants.CSKEFU_SYSTEM_NO_DAT, false));
        } else {
            map.addAttribute(
                    "owneruserList", getUserProxy().findByOrganAndDatastatus(ownerdept, false));

        }
        map.addAttribute(
                "skillGroups", organRes.findAllById(CallCenterUtils.getAuthOrgan(userRoleRes, callOutRoleRes, user)));
        map.put(
                "taskList", MainContext.getContext().getBean(UKefuCallOutTaskRepository.class).findByActid(
                        actid));
        map.put(
                "allUserList",
                MainContext.getContext().getBean(UserRepository.class).findByDatastatus(false));
        //JobDetail act = batchRes.findByIdAndOrgi(actid);
        //if(act != null){
        //	map.put("salestatusList",MainContext.getContext().getBean(SaleStatusRepository.class).findByOrgiAndActivityid(act.getDicid()));
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

        List<CallAgent> actList = callAgentRes.findByActid(activityid);
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

        List<Organ> organAllList = organRes.findAll();

        final List<String> tempList = new ArrayList<>();

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

    public static UserProxy getUserProxy() {
        if (userProxy == null) {
            userProxy = MainContext.getContext().getBean(UserProxy.class);
        }
        return userProxy;
    }
}
