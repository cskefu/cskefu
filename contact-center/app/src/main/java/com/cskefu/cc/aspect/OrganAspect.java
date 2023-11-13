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
package com.cskefu.cc.aspect;

import com.cskefu.cc.basic.MainContext;
import com.cskefu.cc.exception.BillingQuotaException;
import com.cskefu.cc.exception.BillingResourceException;
import com.cskefu.cc.model.Channel;
import com.cskefu.cc.model.Organ;
import com.cskefu.cc.proxy.LicenseProxy;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class OrganAspect {
    private final static Logger logger = LoggerFactory.getLogger(OrganAspect.class);

    @Autowired
    private LicenseProxy licenseProxy;

    @Before("execution(* com.cskefu.cc.persistence.repository.OrganRepository.save(..))")
    public void beforeSave(final JoinPoint joinPoint) throws BillingResourceException, BillingQuotaException {
        final Organ organ = (Organ) joinPoint.getArgs()[0];
        logger.info("[beforeSave] before organ id {}", organ.getId());
        if (StringUtils.isBlank(organ.getId())) {
            // create new organ
            licenseProxy.writeDownResourceUsageInStore(MainContext.BillingResource.ORGAN, 1);
        } else {
            // update existed Channel
        }
    }
}
