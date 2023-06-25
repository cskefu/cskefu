/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd.
 * <https://www.chatopera.com>, Licensed under the Chunsong Public
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cskefu.cc.proxy;

import com.cskefu.cc.controller.admin.OrganController;
import com.cskefu.cc.model.Organ;
import com.cskefu.cc.model.User;
import com.cskefu.cc.persistence.repository.OrganRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
     * @return
     */
    private boolean checkParentOrgan(Organ organ, String organId) {
        if (StringUtils.equals(organ.getParent(), "0")) {
            return true;
        }

        if (StringUtils.equals(organ.getId(), organ.getParent())) {
            return false;
        }

        Organ parent = organRes.getReferenceById(organ.getParent());
        if (parent == null) {
            return false;
        } else {
            if (StringUtils.equals(parent.getParent(), organId)) {
                return false;
            } else {
                return checkParentOrgan(parent, organId);
            }
        }
    }

    /**
     * @param organ
     * @param user
     * @return msg
     */
    public String updateOrgan(final Organ organ, final User user) {
        final Organ oldOrgan = organRes.findByName(organ.getName());

        String msg = "admin_organ_update_success";

        if (oldOrgan != null && !StringUtils.equals(oldOrgan.getId(), (organ.getId()))) {
            return "admin_organ_update_name_not";
        }

        if (!checkParentOrgan(organ, organ.getId())) {
            return "admin_organ_update_not_standard";
        }

        Organ tempOrgan = organRes.getReferenceById(organ.getId());
        if (tempOrgan != null) {
            tempOrgan.setName(organ.getName());
            tempOrgan.setUpdatetime(new Date());
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
        return organRes.findAllById(organIds);
    }

    private void processChild(Map<String, Organ> organs, String organId) {
        Organ organ = organRes.getReferenceById(organId);
        if (organ != null) {
            organs.put(organId, organ);
            List<Organ> childOrgans = organRes.findByParent(organId);
            childOrgans.stream().forEach(o -> processChild(organs, o.getId()));
        }
    }

    public Map<String, Organ> findAllOrganByParentId(String organId) {
        Map<String, Organ> result = new HashMap<>();
        if (StringUtils.isNotBlank(organId)) {
            processChild(result, organId);
        }
        return result;
    }

    public Map<String, Organ> findAllOrganByParent(Organ organ) {
        if (organ != null) {
            return findAllOrganByParentId(organ.getId());
        } else {
            throw new NullPointerException("Invalid organ info");
        }
    }

    public Organ getDefault(Collection<Organ> organs) {
        Organ organ = null;

        if (organs.size() > 0) {
            ArrayList<String> organTree = new ArrayList<>();
            organs.stream().forEach(o -> {
                if (organTree.stream().filter(p -> StringUtils.equals(o.getParent(), p)).findFirst()
                        .isPresent()) {
                    int index = organTree.indexOf(o.getParent());
                    organTree.add(index + 1, o.getId());
                } else {
                    organTree.add(0, o.getId());
                }
            });

            organ = organs.stream().filter(o -> StringUtils.equals(o.getId(), organTree.get(0))).findFirst()
                    .orElse(organs.stream().findFirst().get());
        }

        return organ;
    }
}
