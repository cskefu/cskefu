package com.chatopera.cc.proxy;

import com.chatopera.cc.controller.admin.OrganController;
import com.chatopera.cc.model.Organ;
import com.chatopera.cc.model.User;
import com.chatopera.cc.persistence.repository.OrganRepository;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrganProxy {
    private final static Logger logger = LoggerFactory.getLogger(OrganController.class);

    @Autowired
    private OrganRepository organRes;

    /**
     * 检查组织机构树
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
            OnlineUserProxy.clean(orgi);
        } else {
            msg = "admin_organ_update_not_exist";
        }

        return msg;
    }
}
