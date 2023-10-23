/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd.
 * <https://www.chatopera.com>, Licensed under the Chunsong Public
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2019-Jun. 2023 Chatopera Inc, <https://www.chatopera.com>,
 * Licensed under the Apache License, Version 2.0,
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cskefu.cc.exception;

public class BillingQuotaException extends Exception {

    public final static String SUFFIX = "billingquotaexception.";

    // metakv 中没有找到可以支持配额的证书类型
    public final static String NO_LICENSE_FOUND = SUFFIX + "no_license_found";
    // 返回值异常，可能是网络连接不上，稍后再试
    public static final String RESPONSE_UNEXPECTED = SUFFIX + "response_unexpected";
    // 请求参数不合法
    public static final String INVALID_REQUEST_BODY = SUFFIX + "invalid_request_body";
    // 证书在证书商店不存在
    public static final String LICENSE_INVALID = SUFFIX + "license_invalid";
    // 证书关联的产品信息不合法
    public static final String PRODUCT_INVALID = SUFFIX + "product_invalid";
    // 证书失效或耗尽
    public static final String LICENSE_EXPIRED_OR_EXHAUSTED = SUFFIX + "license_expired_or_exhausted";
    // 证书关闭了对该 serverinst 的支持
    public static final String LICENSE_DISABLED_SERVERINST = SUFFIX + "license_disabled_serverinst";
    // 证书不支持配额回退
    public static final String LICENSE_UNSUPPORT_REFUND = SUFFIX + "license_unsupport_refund";
    // 证书配额余量不足，不能完成本次请求
    public static final String LICENSE_QUOTA_INADEQUATE = SUFFIX + "license_quota_inadequate";
    // 内部错误，不应该发生
    public static final String INTERNAL_ERROR = SUFFIX + "internal_error";

    public BillingQuotaException(final String s) {
        super(s);
    }
}
