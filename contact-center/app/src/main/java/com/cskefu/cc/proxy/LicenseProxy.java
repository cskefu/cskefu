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

import com.chatopera.store.sdk.QuotaWdClient;
import com.chatopera.store.sdk.Response;
import com.chatopera.store.sdk.exceptions.InvalidResponseException;
import com.cskefu.cc.basic.Constants;
import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.basic.MainUtils;
import com.cskefu.cc.exception.MetaKvInvalidKeyException;
import com.cskefu.cc.exception.MetaKvNotExistException;
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
        Optional<MetaKv> metaServerinstIdOpt = metaKvRes.findFirstByMetakey(Constants.LICENSE_SERVER_INST_ID);
        if (metaServerinstIdOpt.isEmpty()) {
            // 没有 serverinstId 信息，初始化
            final String serverinstId = MainUtils.getUUID();
            createMetaKv(Constants.LICENSE_SERVER_INST_ID, serverinstId, Constants.METAKV_DATATYPE_STRING);
        }

        Optional<MetaKv> metaServicenameOpt = metaKvRes.findFirstByMetakey(Constants.LICENSE_SERVICE_NAME);
        if (metaServicenameOpt.isEmpty()) {
            // 没有 Service Name 信息，初始化
            final String serviceName = generateLicenseServiceName();
            createMetaKv(Constants.LICENSE_SERVICE_NAME, serviceName, Constants.METAKV_DATATYPE_STRING);
        }

        Optional<MetaKv> metaLicensesOpt = metaKvRes.findFirstByMetakey(Constants.LICENSEIDS);
        if (metaLicensesOpt.isEmpty()) {
            // 没有 license 信息，初始化
            createMetaKv(Constants.LICENSEIDS, (new JSONArray()).toString(), Constants.METAKV_DATATYPE_STRING);
        }
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
    public List<JSONObject> getLicensesFromStore() throws InvalidResponseException {
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

            Response resp = quotaWdClient.getLicensesInfo(licenseIds);
            JSONArray data = (JSONArray) resp.getData();

            for (int i = 0; i < data.length(); i++) {
                JSONObject lic = (JSONObject) data.get(i);
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
    public JSONObject getLicenseFromStore(final String licenseShortId) throws InvalidResponseException {
        Response resp = quotaWdClient.getLicenseInfo(licenseShortId);
        if (resp.getRc() == 0) {
            JSONArray data = (JSONArray) resp.getData();
            if (data.length() != 1)
                throw new InvalidResponseException("Unexpected data in Response.");

            return (JSONObject) (data).get(0);
        } else {
            throw new InvalidResponseException("Unexpected Response.");
        }

    }

}
