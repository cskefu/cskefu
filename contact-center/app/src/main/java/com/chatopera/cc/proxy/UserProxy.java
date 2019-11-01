/*
 * Copyright (C) 2019 Chatopera Inc, All rights reserved.
 * <https://www.chatopera.com>
 * This software and related documentation are provided under a license agreement containing
 * restrictions on use and disclosure and are protected by intellectual property laws.
 * Except as expressly permitted in your license agreement or allowed by law, you may not use,
 * copy, reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform,
 * publish, or display any part, in any form, or by any means. Reverse engineering, disassembly,
 * or decompilation of this software, unless required by law for interoperability, is prohibited.
 */

package com.chatopera.cc.proxy;

import com.chatopera.cc.basic.MainContext;
import com.chatopera.cc.model.Organ;
import com.chatopera.cc.model.OrganUser;
import com.chatopera.cc.model.User;
import com.chatopera.cc.persistence.repository.OrganRepository;
import com.chatopera.cc.persistence.repository.OrganUserRepository;
import com.chatopera.cc.persistence.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

/**
 * 用户/坐席 常用方法
 */
public class UserProxy {
    private final static Logger logger = LoggerFactory.getLogger(UserProxy.class);

    private static OrganUserRepository organUserRes;
    private static OrganRepository organRes;
    private static UserRepository userRes;


    public static User findOne(final String id) {
        return getUserRes().findOne(id);
    }


    public static List<String> findUserIdsInOrgan(final String organ) {
        List<OrganUser> x = getOrganUserRes().findByOrgan(organ);

        if (x.size() == 0) {
            return null;
        }

        List<String> z = new ArrayList<>();
        for (final OrganUser y : x) {
            z.add(y.getUserid());
        }
        return z;
    }

    public static List<String> findUserIdsInOrgans(final List<String> organs) {

        List<OrganUser> x = getOrganUserRes().findByOrganIn(organs);

        if (x.size() == 0) return null;

        Set<String> y = new HashSet<>();

        for (final OrganUser z : x) {
            y.add(z.getUserid());
        }

        return new ArrayList<>(y);

    }


    /**
     * 通过坐席ID查找其技能组Map
     *
     * @param agentno
     * @return
     */
    public static HashMap<String, String> getSkillsMapByAgentno(final String agentno) {

        final User user = getUserRes().findOne(agentno);
        if (user == null) return new HashMap<>();

        attachOrgansPropertiesForUser(user);
        return user.getSkills();
    }

    /**
     * 获得一个用户的直属组织机构
     *
     * @param userid
     * @return
     */
    public static List<String> findOrgansByUserid(final String userid) {
        List<OrganUser> x = getOrganUserRes().findByUserid(userid);

        if (x.size() == 0) return null;

        List<String> y = new ArrayList<>();

        for (final OrganUser z : x) {
            y.add(z.getOrgan());
        }

        return y;
    }


    public static Page<User> findByOrganInAndAgentAndDatastatus(
            final List<String> organs,
            boolean agent,
            boolean datastatus,
            Pageable pageRequest) {
        List<String> users = findUserIdsInOrgans(organs);

        if (users == null) return null;

        return getUserRes().findByAgentAndDatastatusAndIdIn(agent, datastatus, users, pageRequest);

    }

    public static List<User> findByOrganInAndAgentAndDatastatus(
            final List<String> organs,
            boolean agent,
            boolean datastatus) {
        List<String> users = findUserIdsInOrgans(organs);

        if (users == null) return null;

        return getUserRes().findByAgentAndDatastatusAndIdIn(agent, datastatus, users);
    }


    public static List<User> findByOrganInAndDatastatus(
            final List<String> organs,
            boolean datastatus) {
        List<String> users = findUserIdsInOrgans(organs);

        if (users == null) return null;

        return getUserRes().findByDatastatusAndIdIn(datastatus, users);
    }


    public static Page<User> findByOrganInAndDatastatusAndUsernameLike(
            final List<String> organs,
            final boolean datastatus,
            final String username,
            Pageable pageRequest) {
        List<String> users = findUserIdsInOrgans(organs);

        if (users == null) return null;

        return getUserRes().findByDatastatusAndUsernameLikeAndIdIn(datastatus, username, users, pageRequest);

    }

    public static List<User> findByOrganAndOrgiAndDatastatus(final String organ, final String orgi, final boolean datastatus) {
        List<String> users = findUserIdsInOrgan(organ);

        if (users == null) return null;

        return getUserRes().findByOrgiAndDatastatusAndIdIn(orgi, datastatus, users);

    }


    public static List<User> findAllByCallcenterIsTrueAndDatastatusIsFalseAndOrgan(final String organ) {

        final List<String> users = findUserIdsInOrgan(organ);

        if (users == null) return null;

        return getUserRes().findAllByCallcenterIsTrueAndDatastatusIsFalseAndIdIn(users);

    }

    /**
     * 或取Sips列表
     *
     * @param organ
     * @param datastatus
     * @param orgi
     * @return
     */
    public static List<String> findSipsByOrganAndDatastatusAndOrgi(final String organ, final boolean datastatus, final String orgi) {
        List<String> users = findUserIdsInOrgan(organ);

        if (users == null) return null;

        return getUserRes().findSipsByDatastatusAndOrgiAndIdIn(datastatus, orgi, users);
    }


    /**
     * 通过租户ID，是否为坐席，是否有效和组织机构查询坐席数
     *
     * @param orgi
     * @param agent
     * @param datastatus
     * @param organ
     * @return
     */
    public static long countByOrgiAndAgentAndDatastatusAndOrgan(
            final String orgi,
            final boolean agent,
            final boolean datastatus,
            final String organ) {

        final List<String> users = findUserIdsInOrgan(organ);

        if (users == null) return 0;

        return getUserRes().countByAgentAndDatastatusAndIdIn(agent, datastatus, users);

    }

    /**
     * 获得一个部门及其子部门并添加到User的myorgans中
     *
     * @param user
     */
    public static void processAffiliates(final User user, final Map<String, String> skills, final Organ organ) {
        if (organ == null) {
            return;
        }

        if (user.inAffiliates(organ.getId())) {
            return;
        }

        user.getAffiliates().add(organ.getId());

        if (organ.isSkill()) skills.put(organ.getId(), organ.getName());

        // 获得子部门
        List<Organ> y = getOrganRes().findByOrgiAndParent(user.getOrgi(), organ.getId());

        for (Organ x : y) {
            try {
                // 递归调用
                processAffiliates(user, skills, x);
            } catch (Exception e) {
                logger.error("processAffiliates", e);
            }
        }
    }

    /**
     * 获取用户部门以及下级部门
     *
     * @param user
     */
    public static void attachOrgansPropertiesForUser(final User user) {
        List<OrganUser> organs = getOrganUserRes().findByUserid(user.getId());
        user.setOrgans(new HashMap<>());
        final HashMap<String, String> skills = new HashMap<>();

        for (final OrganUser organ : organs) {
            // 添加直属部门到organs
            final Organ o = getOrganRes().findOne(organ.getOrgan());
            user.getOrgans().put(organ.getOrgan(), o);

            // 添加部门及附属部门
            processAffiliates(user, skills, o);
        }

        user.setSkills(skills);
    }


    private static OrganRepository getOrganRes() {
        if (organRes == null) {
            organRes = MainContext.getContext().getBean(OrganRepository.class);
        }
        return organRes;
    }

    private static OrganUserRepository getOrganUserRes() {
        if (organUserRes == null) {
            organUserRes = MainContext.getContext().getBean(OrganUserRepository.class);
        }
        return organUserRes;
    }

    private static UserRepository getUserRes() {
        if (userRes == null) {
            userRes = MainContext.getContext().getBean(UserRepository.class);
        }
        return userRes;
    }
}
