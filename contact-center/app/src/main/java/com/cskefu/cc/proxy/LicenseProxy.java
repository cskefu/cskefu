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

import com.chatopera.store.enums.LICSTATUS;
import com.chatopera.store.sdk.QuotaWdClient;
import com.chatopera.store.sdk.Response;
import com.chatopera.store.sdk.exceptions.InvalidRequestException;
import com.chatopera.store.sdk.exceptions.InvalidResponseException;
import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.exception.*;
import com.cskefu.cc.model.AgentUser;
import com.cskefu.cc.model.ExecuteResult;
import com.cskefu.cc.model.MetaKv;
import com.cskefu.cc.persistence.repository.MetaKvRepository;
import com.cskefu.cc.util.Base62;
import com.cskefu.cc.util.DateConverter;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

/**
 * 证书服务
 */
@Service
public class LicenseProxy {
    private final static Logger logger = LoggerFactory.getLogger(LicenseProxy.class);

    @Autowired
    private MetaKvRepository metaKvRes;

    @Autowired
    private QuotaWdClient quotaWdClient;

    private static final Map<MainContext.BillingResource, Integer> BILLING_RES_QUOTA_MAPPINGS = new HashMap<>();

    static {
        BILLING_RES_QUOTA_MAPPINGS.put(MainContext.BillingResource.USER, 100);
        BILLING_RES_QUOTA_MAPPINGS.put(MainContext.BillingResource.AGENGUSER, 1);
        BILLING_RES_QUOTA_MAPPINGS.put(MainContext.BillingResource.CHANNELWEBIM, 100);
        BILLING_RES_QUOTA_MAPPINGS.put(MainContext.BillingResource.CONTACT, 1);
        BILLING_RES_QUOTA_MAPPINGS.put(MainContext.BillingResource.ORGAN, 10);
    }

    /**
     * 初始化 serverinstId
     * serverinstId 作为服务唯一的实例ID
     */
    public void checkOnStartup() {
        /**
         * Check service connection
         */
        System.out.println("[license] license service URL " + quotaWdClient.getBaseUrl());
        try {
            Response resp = quotaWdClient.ping();
            System.out.println("[license] license service ping successfully.");

            if (resp.getRc() != 0) {
                throw new InvalidResponseException("Unexpected response from license service " + resp.toString());
            }
        } catch (InvalidResponseException e) {
            logger.error("[license] make sure this host machine could connect to " + quotaWdClient.getBaseUrl() + " during running.");
            logger.error("[license] checkOnStartup could not connect to license service, CSKeFu instance is terminated.", e);
            // Very serious event happens, just shutdown the instance
            SpringApplication.exit(MainContext.getContext(), () -> 1);
        }

        /**
         * Init local data for License
         */
        resolveServerinstId();
        resolveServicename();
        resolveLicenseIds();
    }

    /**
     * 读取或初始化 serverinstId
     *
     * @return
     */
    public String resolveServerinstId() {
        Optional<MetaKv> metaServerinstIdOpt = metaKvRes.findFirstByMetakey(Constants.LICENSE_SERVER_INST_ID);
        if (metaServerinstIdOpt.isEmpty()) {
            // 没有 serverinstId 信息，初始化
            final String serverinstId = Base62.generateShortId();
            createMetaKv(Constants.LICENSE_SERVER_INST_ID, serverinstId, Constants.METAKV_DATATYPE_STRING);
            return serverinstId;
        }
        return metaServerinstIdOpt.get().getMetavalue();
    }

    /**
     * 读取或初始化 licenseIds
     */
    private void resolveLicenseIds() {
        Optional<MetaKv> metaLicensesOpt = metaKvRes.findFirstByMetakey(Constants.LICENSEIDS);
        if (metaLicensesOpt.isEmpty()) {
            // 没有 license 信息，初始化
            createMetaKv(Constants.LICENSEIDS, (new JSONArray()).toString(), Constants.METAKV_DATATYPE_STRING);
        }
    }

    /**
     * 读取或初始化 serviceName
     *
     * @return
     */
    public String resolveServicename() {
        Optional<MetaKv> metaServicenameOpt = metaKvRes.findFirstByMetakey(Constants.LICENSE_SERVICE_NAME);
        if (metaServicenameOpt.isEmpty()) {
            // 没有 Service Name 信息，初始化
            final String serviceName = generateLicenseServiceName();
            createMetaKv(Constants.LICENSE_SERVICE_NAME, serviceName, Constants.METAKV_DATATYPE_STRING);
            return serviceName;
        }
        return metaServicenameOpt.get().getMetavalue();
    }

    /**
     * 从 MetaKv 表中取得数据 MetaKv
     *
     * @param key
     * @return
     * @throws MetaKvNotExistException
     */
    public MetaKv retrieveMetaKv(final String key) throws MetaKvNotExistException, MetaKvInvalidKeyException {

        if (StringUtils.isBlank(key)) {
            throw new MetaKvInvalidKeyException("Key must not be empy");
        }

        Optional<MetaKv> kvOpt = metaKvRes.findFirstByMetakey(key);
        if (kvOpt.isEmpty()) {
            throw new MetaKvNotExistException(key + " not exist");
        } else {
            return kvOpt.get();
        }
    }

    /**
     * 创建或更新 MetaKv
     * UpdateOnExist
     *
     * @param key
     * @param value
     * @param datatype
     */
    public MetaKv createOrUpdateMetaKv(final String key, final String value, final String datatype) throws MetaKvInvalidKeyException {
        try {
            MetaKv kv = retrieveMetaKv(key);
            kv.setMetavalue(value);
            kv.setUpdatetime(new Date());
            metaKvRes.save(kv);
            return kv;
        } catch (MetaKvNotExistException e) {
            return createMetaKv(key, value, datatype);
        }
    }


    /**
     * 建立 Metakv 数据
     *
     * @param key
     * @param value
     * @param datatype
     */
    public MetaKv createMetaKv(final String key, final String value, final String datatype) {
        Date now = new Date();
        MetaKv metakv = new MetaKv();
        metakv.setCreatetime(now);
        metakv.setUpdatetime(now);
        metakv.setMetakey(key);
        metakv.setMetavalue(value);
        metakv.setDatatype(datatype);

        metaKvRes.save(metakv);
        return metakv;
    }

    /**
     * 增加 MetaKv 中的 Key 的值，作为 Integer 做增量，不存在则初始化其值为 0，然后增量操作
     *
     * @param key
     * @param incrValue
     */
    public MetaKv increValueInMetaKv(final String key, final int incrValue) {
        try {
            MetaKv kv = retrieveMetaKv(key);
            int pre = Integer.parseInt(kv.getMetavalue());
            kv.setMetavalue(Integer.toString(pre + incrValue));
            kv.setUpdatetime(new Date());
            metaKvRes.save(kv);
            return kv;
        } catch (MetaKvNotExistException e) {
            return createMetaKv(key, Integer.toString(incrValue), Constants.METAKV_DATATYPE_INT);
        } catch (MetaKvInvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 生成随机字符串，作为服务名称
     *
     * @return
     */
    private String generateLicenseServiceName() {
        StringBuffer sb = new StringBuffer();

        sb.append(Constants.LICENSE_SERVICE_NAME_PREFIX);
        sb.append(Base62.generatingRandomAlphanumericString(5));

        return sb.toString();
    }

    /**
     * 从数据库及证书商店获得证书列表信息
     *
     * @return
     */
    public List<JSONObject> getLicensesInStore() throws InvalidResponseException {
        List<JSONObject> result = new ArrayList<>();

        try {
            JSONArray ja = new JSONArray((retrieveMetaKv(Constants.LICENSEIDS).getMetavalue()));
            HashMap<String, String> addDates = new HashMap<>();
            List<String> licenseIds = new ArrayList<>();
            for (int i = 0; i < ja.length(); i++) {
                JSONObject obj = ((JSONObject) ja.get(i));
                licenseIds.add(obj.getString(Constants.SHORTID));
                addDates.put(obj.getString(Constants.SHORTID), obj.getString(Constants.ADDDATE));
            }

            Response resp = null;
            try {
                resp = quotaWdClient.getLicenseBasics(licenseIds);
            } catch (InvalidRequestException e) {
                return result;
            }
            JSONArray data = (JSONArray) resp.getData();

            for (int i = 0; i < data.length(); i++) {
                JSONObject lic = (JSONObject) data.get(i);
                if(StringUtils.equals(lic.getJSONObject(Constants.LICENSE).getString(Constants.STATUS), "notfound")){
                    // fill in placeholders for notfound license
                    final JSONObject licenseJsonTmp = lic.getJSONObject(Constants.LICENSE);
                    licenseJsonTmp.put("effectivedateend", "N/A");
                    licenseJsonTmp.put("quotaeffectiveremaining", "N/A");

                    JSONObject productJsonTmp = new JSONObject();
                    productJsonTmp.put("shortId", "N/A");
                    productJsonTmp.put("name", "N/A");
                    lic.put("product", productJsonTmp);

                    JSONObject userJsonTmp = new JSONObject();
                    userJsonTmp.put("nickname", "N/A");
                    lic.put("user", userJsonTmp);

//                    lic.put(Constants.ADDDATE, null);

                    result.add(lic);
                    continue;
                }

                try {
                    Date addDate = DateConverter.parseCSTAsChinaTimezone(addDates.get(lic.getJSONObject(Constants.LICENSE).getString(Constants.SHORTID)));
                    lic.put(Constants.ADDDATE, addDate);
                } catch (ParseException e) {
                    logger.info("[getLicensesFromStore] can not resolve add date");
                }
                result.add(lic);
            }

        } catch (MetaKvNotExistException e) {
            logger.info("[getLicenses] no LICENSEIDS data in MySQL DB");
        } catch (MetaKvInvalidKeyException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * 获得在 MetaKV 表中的 license 信息
     *
     * @return JSONArray
     */
    public JSONArray getLicensesInMetakv() {
        try {
            String value = retrieveMetaKv(Constants.LICENSEIDS).getMetavalue();
            return new JSONArray(value);
        } catch (MetaKvNotExistException e) {
            return new JSONArray();
        } catch (MetaKvInvalidKeyException e) {
            return new JSONArray();
        }
    }

    /**
     * @param licenseShortId
     * @return
     * @throws InvalidResponseException
     */
    public JSONObject getLicenseBasicsInStore(final String licenseShortId) throws InvalidResponseException, InvalidRequestException {
        Response resp = quotaWdClient.getLicenseBasics(licenseShortId);
        if (resp.getRc() == 0) {
            JSONArray data = (JSONArray) resp.getData();
            if (data.length() != 1)
                throw new InvalidResponseException("Unexpected data in Response.");

            return (JSONObject) (data).get(0);
        } else {
            throw new InvalidResponseException("Unexpected Response.");
        }

    }

    /**
     * 获得已经添加的证书在 Store 中的基本信息
     *
     * @return
     * @throws InvalidResponseException
     */
    public JSONArray getAddedLicenseBasicsInStore() throws InvalidResponseException {
        JSONArray arr = getLicensesInMetakv();
        List<String> ids = new ArrayList<>();

        for (int i = 0; i < arr.length(); i++) {
            ids.add(((JSONObject) arr.get(i)).getString(Constants.SHORTID));
        }

        if (ids.size() > 0) {
            Response resp = null;
            try {
                resp = quotaWdClient.getLicenseBasics(StringUtils.join(ids, ","));
            } catch (InvalidRequestException e) {
                logger.error("[getAddedLicenseBasicsFromStore] InvalidRequestException", e);
            }
            if (resp.getRc() != 0) {
                throw new InvalidResponseException("Invalid response, rc " + Integer.toString(resp.getRc()));
            }

            return (JSONArray) resp.getData();
        } else {
            logger.error("[license] getAddedLicenseBasicsFromStore - No license ids in metaKv");
            return new JSONArray();
        }
    }


    /**
     * 验证证书存在
     *
     * @param licenseShortId
     * @return
     */
    public LICSTATUS existLicenseInStore(final String licenseShortId) throws InvalidResponseException, LicenseNotFoundException, InvalidRequestException {
        Map<String, LICSTATUS> statuses = quotaWdClient.getLicenseStatus(licenseShortId);

        if (statuses.size() == 1) {
            for (final Map.Entry<String, LICSTATUS> entry : statuses.entrySet()) {
                final LICSTATUS status = entry.getValue();

                if (status == LICSTATUS.NOTFOUND)
                    throw new LicenseNotFoundException("LicenseId not found [" + licenseShortId + "]");

                return status;
            }
            throw new InvalidResponseException("Unexpected response, internal error.");
        } else {
            throw new InvalidResponseException("Unexpected response, should contain one record.");
        }
    }

    public String getLicenseStoreProvider() {
        return quotaWdClient.getBaseUrl();
    }

    /**
     * 获得资源用量存储
     *
     * @param resourceKey
     * @return
     */
    public String getResourceUsageKey(final String resourceKey) {
        StringBuffer sb = new StringBuffer();
        sb.append(Constants.RESOURCES_USAGE_KEY_PREFIX);
        sb.append("_");
        sb.append(StringUtils.toRootUpperCase(resourceKey));
        return sb.toString();
    }


    /**
     * 增加计费资源用量
     *
     * @param billingResource
     * @param consume
     */
    public void increResourceUsageInMetaKv(final MainContext.BillingResource billingResource, int consume) throws BillingResourceException {
        switch (billingResource) {
            case USER:
            case CONTACT:
            case ORGAN:
            case AGENGUSER:
            case CHANNELWEBIM:
                increValueInMetaKv(getResourceUsageKey(billingResource.toString()), consume);
                break;
            default:
                throw new BillingResourceException("invalid_billing_resource_type");
        }
    }

    /**
     * 获取在 MetaKv 中资源的已经使用的计数
     *
     * @param billingResource
     * @return
     */
    private int getResourceUsageInMetaKv(final MainContext.BillingResource billingResource) {
        final String key = getResourceUsageKey(billingResource.toString());
        try {
            MetaKv kv = retrieveMetaKv(key);
            int pre = Integer.parseInt(kv.getMetavalue());
            return pre;
        } catch (MetaKvNotExistException e) {
            createMetaKv(key, Integer.toString(0), Constants.METAKV_DATATYPE_INT);
            return 0;
        } catch (MetaKvInvalidKeyException e) {
            return 0;
        }
    }

    /**
     * 获得春松客服 cskefu001 产品的证书标识 ID
     * 春松客服证书基本类型
     *
     * @return
     */
    private String getLicenseIdAsCskefu001InMetaKv() throws BillingQuotaException {
        JSONArray curr = getLicensesInMetakv();
        for (int i = 0; i < curr.length(); i++) {
            JSONObject jo = (JSONObject) curr.get(i);
            if (jo.has(Constants.PRODUCT_ID) &&
                    StringUtils.equals(jo.getString(Constants.PRODUCT_ID), Constants.PRODUCT_ID_CSKEFU001)) {
                return jo.getString(Constants.SHORTID);
            }
        }
        throw new BillingQuotaException(BillingQuotaException.NO_LICENSE_FOUND);
    }

    /**
     * 执行配额变更操作
     *
     * @param billingResource
     * @param unitNum
     * @return
     */
    public void writeDownResourceUsageInStore(final MainContext.BillingResource billingResource,
                                              int unitNum) throws BillingQuotaException, BillingResourceException {

        // 检查是否还在体验阶段
        if (billingResource == MainContext.BillingResource.CONTACT) {
            int alreadyUsed = getResourceUsageInMetaKv(billingResource);
            if (alreadyUsed <= 1) {
                // 可以免费创建 1 个联系人
                return;
            }
        }

        // 请求操作配额
        String licenseId = getLicenseIdAsCskefu001InMetaKv();
        String serverinstId = resolveServerinstId();
        String servicename = resolveServicename();

        try {
            Response resp = quotaWdClient.write(licenseId,
                    serverinstId, servicename, unitNum * BILLING_RES_QUOTA_MAPPINGS.get(billingResource));
            // 识别操作是否完成，并处理
            if (resp.getRc() == 0) {
                final JSONObject data = (JSONObject) resp.getData();

                // 配额操作成功，执行计数
                increResourceUsageInMetaKv(billingResource, unitNum);
            } else if (resp.getRc() == 1 || resp.getRc() == 2) {
                throw new BillingQuotaException(BillingQuotaException.INVALID_REQUEST_BODY);
            } else if (resp.getRc() == 3) {
                // 证书商店中不存在
                throw new BillingQuotaException(BillingQuotaException.LICENSE_INVALID);
            } else if (resp.getRc() == 4) {
                // 证书商店中不存在该产品或产品类型无效
                throw new BillingQuotaException(BillingQuotaException.PRODUCT_INVALID);
            } else if (resp.getRc() == 5) {
                // 该证书不支持资源回退
                throw new BillingQuotaException(BillingQuotaException.LICENSE_UNSUPPORT_REFUND);
            } else if (resp.getRc() == 6) {
                // 证书失效或耗尽，不支持继续扣除配额
                throw new BillingQuotaException(BillingQuotaException.LICENSE_EXPIRED_OR_EXHAUSTED);
            } else if (resp.getRc() == 7) {
                // 该证书禁用了该 serverinstId
                throw new BillingQuotaException(BillingQuotaException.LICENSE_DISABLED_SERVERINST);
            } else if (resp.getRc() == 8) {
                // 配额扣除额度超过该证书目前的剩余量
                throw new BillingQuotaException(BillingQuotaException.LICENSE_QUOTA_INADEQUATE);
            } else {
                // 未知情况
                logger.error("[writeDownResourceUsageInStore] resp data {}", resp.toString());
                throw new BillingQuotaException(BillingQuotaException.INTERNAL_ERROR);
            }
        } catch (InvalidResponseException e) {
            // TODO 处理异常信息
            logger.error("[writeDownResourceUsageInStore] error ", e);
            throw new BillingQuotaException(BillingQuotaException.RESPONSE_UNEXPECTED);
        }
    }

    /**
     * 访客会话执行计费
     *
     * @param agentUser
     * @return
     */
    public ExecuteResult writeDownAgentUserUsageInStore(final AgentUser agentUser) {
        // 检查是否还在体验阶段
        ExecuteResult er = new ExecuteResult();
        int alreadyUsed = getResourceUsageInMetaKv(MainContext.BillingResource.AGENGUSER);
        if (alreadyUsed <= 100) {
            // 可以免费创建 100 个访客会话
            er.setRc(ExecuteResult.RC_SUCC);
            return er;
        }

        try {
            writeDownResourceUsageInStore(MainContext.BillingResource.AGENGUSER, 1);
            er.setRc(ExecuteResult.RC_SUCC);
        } catch (BillingQuotaException e) {
            er.setRc(ExecuteResult.RC_ERR1);
            er.setMsg(e.getMessage());
        } catch (BillingResourceException e) {
            er.setRc(ExecuteResult.RC_ERR2);
            er.setMsg(e.getMessage());
        }

        return er;
    }
}
