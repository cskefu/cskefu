package com.chatopera.cc.proxy;

import com.chatopera.cc.controller.admin.OrganController;
import com.chatopera.cc.model.Organ;
import com.chatopera.cc.model.User;
import com.chatopera.cc.persistence.repository.OrganRepository;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrganProxy {
    private final static Logger logger = LoggerFactory.getLogger(OrganController.class);

    @Autowired
    private OrganRepository organRes;

    /**
     * 检查组织机构树
     *
     * @param organ
     * @param organId
     * @param orgi
     * @return
     */
    private boolean checkParentOrgan(Organ organ, String organId, String orgi) {
        if (StringUtils.equals(organ.getParent(), "0")) {
            return true;
        }

        if (StringUtils.equals(organ.getId(), organ.getParent())) {
            return false;
        }

        Organ parent = organRes.findByIdAndOrgi(organ.getParent(), orgi);
        if (parent == null) {
            return false;
        } else {
            if (StringUtils.equals(parent.getParent(), organId)) {
                return false;
            } else {
                return checkParentOrgan(parent, organId, orgi);
            }
        }
    }


    /**
     * @param organ
     * @param orgi
     * @param user
     * @return msg
     */
    public String updateOrgan(final Organ organ, final String orgi, final User user) {
        final Organ oldOrgan = organRes.findByNameAndOrgi(organ.getName(), orgi);

        String msg = "admin_organ_update_success";

        if (oldOrgan != null && !StringUtils.equals(oldOrgan.getId(), (organ.getId()))) {
            return "admin_organ_update_name_not";
        }

        if (!checkParentOrgan(organ, organ.getId(), orgi)) {
            return "admin_organ_update_not_standard";
        }

        Organ tempOrgan = organRes.findByIdAndOrgi(organ.getId(), orgi);
        if (tempOrgan != null) {
            tempOrgan.setName(organ.getName());
            tempOrgan.setUpdatetime(new Date());
            tempOrgan.setOrgi(orgi);
            tempOrgan.setSkill(organ.isSkill());
            tempOrgan.setParent(organ.getParent());
            tempOrgan.setArea(organ.getArea());
            organRes.save(tempOrgan);
        } else {
            msg = "admin_organ_update_not_exist";
        }

        return msg;
    }

    public List<Organ> findOrganInIds(Collection<String> organIds) {
        return organRes.findAll(organIds);
    }

    private void processChild(Map<String, Organ> organs, String organId, String orgi) {
        Organ organ = organRes.findByIdAndOrgi(organId, orgi);
        if (organ != null) {
            organs.put(organId, organ);
            List<Organ> childOrgans = organRes.findByOrgiAndParent(orgi, organId);
            childOrgans.stream().forEach(o -> processChild(organs, o.getId(), orgi));
        }
    }

    public Map<String, Organ> findAllOrganByParentAndOrgi(Organ organ, String orgi) {
        Map<String, Organ> result = new HashMap<>();
        if (organ != null) {
            processChild(result, organ.getId(), orgi);
        }
        return result;
    }
}
