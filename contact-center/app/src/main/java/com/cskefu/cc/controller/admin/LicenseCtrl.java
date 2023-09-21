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
package com.cskefu.cc.controller.admin;

import com.chatopera.store.sdk.exceptions.InvalidResponseException;
import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.exception.MetaKvInvalidKeyException;
import com.cskefu.cc.model.User;
import com.cskefu.cc.proxy.LicenseProxy;
import com.cskefu.cc.util.Menu;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin/license")
public class LicenseCtrl extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(LicenseCtrl.class);

    @Autowired
    private LicenseProxy licenseProxy;

    @RequestMapping("/index")
    @Menu(type = "admin", subtype = "license")
    public ModelAndView index(ModelMap map, HttpServletRequest request) {
        User user = super.getUser(request);
        if (user.isSuperadmin()) {

            try {
                List<JSONObject> licenses = licenseProxy.getLicensesFromStore();
                map.addAttribute(Constants.UPDATETIME, new Date());
                map.addAttribute(Constants.LICENSES, licenses);
            } catch (InvalidResponseException e) {
                throw new RuntimeException(e);
            }

            return request(super.createView("/admin/license/index"));
        } else {
            return request(super.createView("/public/error"));
        }
    }

    @RequestMapping("/add")
    @Menu(type = "admin", subtype = "license")
    public ModelAndView add(ModelMap map, HttpServletRequest request) {
        User user = super.getUser(request);
        if (user.isSuperadmin()) {
            return request(super.createView("/admin/license/add"));
        } else {
            return request(super.createView("/public/error"));
        }
    }

    /**
     * 保存新的证书
     *
     * @param map
     * @param request
     * @param licenseShortId
     * @return
     */
    @RequestMapping("/save")
    @Menu(type = "admin", subtype = "license")
    public ModelAndView save(ModelMap map,
                             HttpServletRequest request,
                             @Valid String licenseShortId) throws MetaKvInvalidKeyException {
        User user = super.getUser(request);
        logger.info("[save] licenseShortId {}", licenseShortId);

        if (user.isSuperadmin()) {
            // 验证该证书不在当前证书列表中
            JSONArray currents = licenseProxy.getLicensesInMetakv();
            String msg = "";
            boolean isAddedBefore = false;

            for (int i = 0; i < currents.length(); i++) {
                JSONObject item = (JSONObject) currents.get(i);
                if (StringUtils.equals(item.getString("shortId"), licenseShortId)) {
                    isAddedBefore = true;
                    break;
                }
            }

            if (isAddedBefore) {
                msg = "already_added";
                return request(super.createView(
                        "redirect:/admin/license/index.html?msg=" + msg));
            }

            // 验证该证书存在
            try {
                JSONObject licenseData = licenseProxy.getLicenseFromStore(licenseShortId);
                JSONObject licenseKvData = new JSONObject();
                licenseKvData.put(Constants.SHORTID, licenseData.getJSONObject(Constants.LICENSE).getString(Constants.SHORTID));
                licenseKvData.put(Constants.ADDDATE, new Date());

                // 添加该证书
                currents.put(0, licenseKvData);
                licenseProxy.createOrUpdateMetaKv(Constants.LICENSEIDS, currents.toString(), Constants.METAKV_DATATYPE_STRING);

                // 跳转回到证书列表
                List<JSONObject> licenses = licenseProxy.getLicensesFromStore();
                map.addAttribute(Constants.LICENSES, licenses);
                map.addAttribute("updateTime", new Date());

                return request(super.createView("/admin/license/index"));
            } catch (InvalidResponseException e) {
                logger.warn("[save] error in getLicenseFromStore", e);
                msg = "invalid_id";
                return request(super.createView(
                        "redirect:/admin/license/index.html?msg=" + msg));
            }


        } else {
            return request(super.createView("/public/error"));
        }
    }
}
