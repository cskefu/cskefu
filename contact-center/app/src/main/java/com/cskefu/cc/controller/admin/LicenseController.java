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

import com.chatopera.store.sdk.exceptions.InvalidRequestException;
import com.chatopera.store.sdk.exceptions.InvalidResponseException;
import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.controller.Handler;
import com.cskefu.cc.exception.LicenseNotFoundException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin/license")
public class LicenseController extends Handler {
    private final static Logger logger = LoggerFactory.getLogger(LicenseController.class);

    @Autowired
    private LicenseProxy licenseProxy;

    @RequestMapping("/index")
    @Menu(type = "admin", subtype = "licenseList")
    public ModelAndView index(ModelMap map, HttpServletRequest request) {
        User user = super.getUser(request);
        if (user.isSuperadmin()) {
            try {
                List<JSONObject> licenses = licenseProxy.getLicensesInStore();
                map.addAttribute(Constants.UPDATETIME, new Date());
                map.addAttribute(Constants.LICENSES, licenses);
                map.addAttribute(Constants.LICENSESTOREPROVIDER, licenseProxy.getLicenseStoreProvider());

            } catch (InvalidResponseException e) {
                throw new RuntimeException(e);
            }
            return request(super.createView("/admin/license/index"));
        } else {
            return request(super.createView("/public/error"));
        }
    }

    @RequestMapping("/add")
    @Menu(type = "admin", subtype = "licenseList")
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
    @Menu(type = "admin", subtype = "licenseList")
    public ModelAndView save(ModelMap map,
                             HttpServletRequest request,
                             @Valid String licenseShortId) throws MetaKvInvalidKeyException, InvalidRequestException {
        User user = super.getUser(request);
        logger.info("[save] licenseShortId {}", licenseShortId);
        String msg = "";

        if (user.isSuperadmin()) {
            try {
                /**
                 * 验证证书可以添加
                 */
                // 验证该证书不在当前证书列表中
                JSONArray currents = licenseProxy.getLicensesInMetakv();
                boolean isAddedBefore = false;

                for (int i = 0; i < currents.length(); i++) {
                    JSONObject item = (JSONObject) currents.get(i);
                    if (StringUtils.equals(item.getString(Constants.SHORTID), licenseShortId)) {
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
                licenseProxy.existLicenseInStore(licenseShortId);

                // 验证该证书的所属产品没有现在没有其它证书：同一个产品最多只有一个证书
                JSONObject licBasic = licenseProxy.getLicenseBasicsInStore(licenseShortId);
                final String productId = licBasic.getJSONObject(Constants.PRODUCT).getString(Constants.SHORTID);

                boolean isProductAdded = false;
                JSONArray addedLicenseBasicsFromStore = licenseProxy.getAddedLicenseBasicsInStore();

                for (int i = 0; i < addedLicenseBasicsFromStore.length(); i++) {
                    JSONObject item = (JSONObject) addedLicenseBasicsFromStore.get(i);
                    if (StringUtils.equals(item.getJSONObject(Constants.PRODUCT).getString(Constants.SHORTID), productId)) {
                        isProductAdded = true;
                        break;
                    }
                }

                if (isProductAdded) {
                    msg = "product_added_already";
                    return request(super.createView(
                            "redirect:/admin/license/index.html?msg=" + msg));
                }

                /**
                 * 添加该证书
                 */
                JSONObject licenseKvData = new JSONObject();
                licenseKvData.put(Constants.SHORTID, licenseShortId);
                licenseKvData.put(Constants.ADDDATE, new Date());
                licenseKvData.put(Constants.PRODUCT_ID, productId);
                currents.put(0, licenseKvData);
                licenseProxy.createOrUpdateMetaKv(Constants.LICENSEIDS, currents.toString(), Constants.METAKV_DATATYPE_STRING);

                // 跳转回到证书列表
                List<JSONObject> licenses = licenseProxy.getLicensesInStore();
                map.addAttribute(Constants.LICENSES, licenses);
                map.addAttribute(Constants.UPDATETIME, new Date());
                map.addAttribute(Constants.LICENSESTOREPROVIDER, licenseProxy.getLicenseStoreProvider());

                return request(super.createView("/admin/license/index"));
            } catch (InvalidResponseException e) {
                logger.warn("[save] error in getLicenseFromStore", e);
                msg = "invalid_id";
                return request(super.createView(
                        "redirect:/admin/license/index.html?msg=" + msg));
            } catch (LicenseNotFoundException e) {
                logger.warn("[save] error in getLicenseFromStore", e);
                msg = "notfound_id";
                return request(super.createView(
                        "redirect:/admin/license/index.html?msg=" + msg));
            }
        } else {
            return request(super.createView("/public/error"));
        }
    }

    @RequestMapping("/delete/{licenseShortId}")
    @Menu(type = "admin", subtype = "licenseList")
    public ModelAndView delete(ModelMap map,
                               HttpServletRequest request,
                               @PathVariable String licenseShortId) throws MetaKvInvalidKeyException {
        User user = super.getUser(request);
        logger.info("[delete] licenseShortId {}", licenseShortId);
        String msg = "";

        if (user.isSuperadmin()) {
            try {
                JSONArray currents = licenseProxy.getLicensesInMetakv();
                JSONArray post = new JSONArray();
                for (int i = 0; i < currents.length(); i++) {
                    JSONObject item = (JSONObject) currents.get(i);
                    if (!StringUtils.equals(item.getString(Constants.SHORTID), licenseShortId)) {
                        post.put(item);
                    }
                }

                /**
                 * 添加该证书
                 */
                licenseProxy.createOrUpdateMetaKv(Constants.LICENSEIDS, post.toString(), Constants.METAKV_DATATYPE_STRING);

                // 跳转回到证书列表
                List<JSONObject> licenses = licenseProxy.getLicensesInStore();
                map.addAttribute(Constants.LICENSES, licenses);
                map.addAttribute("updateTime", new Date());
                map.addAttribute(Constants.LICENSESTOREPROVIDER, licenseProxy.getLicenseStoreProvider());

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

    @RequestMapping("/instance")
    @Menu(type = "admin", subtype = "licenseInst")
    public ModelAndView getInstanceInfo(ModelMap map, HttpServletRequest request) {
        User user = super.getUser(request);
        if (user.isSuperadmin()) {
            map.addAttribute(Constants.LICENSE_SERVICE_NAME, licenseProxy.resolveServicename());
            map.addAttribute(Constants.LICENSE_SERVER_INST_ID, licenseProxy.resolveServerinstId());
            map.addAttribute(Constants.LICENSESTOREPROVIDER, licenseProxy.getLicenseStoreProvider());
            return request(super.createView("/admin/license/instance"));
        } else {
            return request(super.createView("/public/error"));
        }
    }
}
