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
import com.cskefu.cc.model.Metakv;
import com.cskefu.cc.persistence.repository.MetakvRepository;
import com.cskefu.cc.util.Base62;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class LicenseProxy {
    private final static Logger logger = LoggerFactory.getLogger(LicenseProxy.class);

    @Autowired
    private MetakvRepository metkvRes;

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
        Optional<Metakv> metaServerinstIdOpt = metkvRes.findFirstByMetakey(Constants.LICENSE_SERVER_INST_ID);
        if (metaServerinstIdOpt.isEmpty()) {
            // 没有 serverinstId 信息，初始化
            final String serverinstId = MainUtils.getUUID();
            createMetakv(Constants.LICENSE_SERVER_INST_ID, serverinstId, Constants.METAKV_DATATYPE_STRING);
        }

        Optional<Metakv> metaServicenameOpt = metkvRes.findFirstByMetakey(Constants.LICENSE_SERVICE_NAME);
        if (metaServicenameOpt.isEmpty()) {
            // 没有 Service Name 信息，初始化
            final String serviceName = generateLicenseServiceName();
            createMetakv(Constants.LICENSE_SERVICE_NAME, serviceName, Constants.METAKV_DATATYPE_STRING);
        }

        Optional<Metakv> metaLicensesOpt = metkvRes.findFirstByMetakey(Constants.LICENSEIDS);
        if (metaLicensesOpt.isEmpty()) {
            // 没有 license 信息，初始化
            createMetakv(Constants.LICENSEIDS, (new JSONArray()).toString(), Constants.METAKV_DATATYPE_STRING);
        }
    }


    /**
     * 建立 Metakv 数据
     *
     * @param key
     * @param value
     * @param datatype
     */
    public void createMetakv(final String key, final String value, final String datatype) {
        Date now = new Date();
        Metakv metakv = new Metakv();
        metakv.setCreatetime(now);
        metakv.setUpdatetime(now);
        metakv.setMetakey(key);
        metakv.setMetavalue(value);
        metakv.setDatatype(datatype);

        metkvRes.save(metakv);
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
}
