/*!
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd.
 * <https://www.chatopera.com>, Licensed under the Chunsong Public
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (C) 2018-Jun. 2023 Chatopera Inc, <https://www.chatopera.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 * 处理系统用户的创建的返回值
 * @param responsecode
 * @param cb
 */
function processUserAddOrUpdateResult(responsecode, cb) {
    switch (responsecode) {
        case 'username_exist':
            layer.msg('用户名存在，请重新填写', {icon: 2, time: 3000});
            // 清空用户名
            $('input[name="username"]').val("");
            break;
        case 'email_exist':
            layer.msg('邮件存在，请重新填写', {icon: 2, time: 3000});
            // 清空邮件
            $('input[name="email"]').val("");
            break;
        case 'mobile_exist':
            layer.msg('手机存在，请重新填写', {icon: 2, time: 3000});
            // 清空手机号
            $('input[name="mobile"]').val("");
            break;
        case 'sip_account_exist':
            layer.msg('SIP地址已经存在，请重新填写', {icon: 2, time: 3000});
            // 清空SIP
            $('input[name="sipaccount"]').val("");
            break;
        case 'extension_binded':
            layer.msg('分机号已经被其他用户绑定', {icon: 2, time: 3000});
            $('input[name="extensionid"]').val("");
            break;
        case 'extension_not_exist':
            layer.msg('绑定分机不存在', {icon: 2, time: 3000});
            $('input[name="extensionid"]').val("");
            break;
        case 'pbxhost_not_exist':
            layer.msg('指定的呼叫中心语音平台不存在', {icon: 2, time: 3000});
            $('input[name="pbxhostid"]').val("");
            break;
        case 't1':
            layer.msg('当前用户坐席就绪或对话未结束，不能切换为非坐席', {icon: 2, time: 3000});
            break;
        case 'new_user_success':
            layer.msg('新用户创建成功', {icon: 1, time: 1000});
            cb();
            break;
        case 'edit_user_success':
            layer.msg('用户编辑成功', {icon: 1, time: 1000});
            cb();
            break;
        default:
            handleGeneralCodeInQueryPathOrApiResp(responsecode, cb);
    }
}

/**
 * 处理在 RedirectURL, API 中返回的 code 信息： status, msg, etc.
 * code 为约定的返回值，通过下面的函数进行展示
 * @param code
 * @param cb
 */
function handleGeneralCodeInQueryPathOrApiResp(code, cb) {
    switch (code) {
        case 'billingquotaexception.no_license_found':
            layer.msg('【使用授权证书】证书不存在，联系系统超级管理员导入。', {icon: 2, time: 5000});
            if (cb && (typeof (x) === 'function')) {
                cb()
            }
            break;
        case 'billingquotaexception.response_unexpected':
            layer.msg('【使用授权证书】证书商店返回异常，稍后再试。', {icon: 2, time: 5000});
            if (cb && (typeof (x) === 'function')) {
                cb()
            }
            break;
        case 'billingquotaexception.invalid_request_body':
            layer.msg('【使用授权证书】请求证书商店参数不合法，请获取最新软件代码。', {icon: 2, time: 5000});
            if (cb && (typeof (x) === 'function')) {
                cb()
            }
            break;
        case 'billingquotaexception.license_invalid':
            layer.msg('【使用授权证书】证书商店中不存在该证书，请联系系统超级管理员导入。', {icon: 2, time: 5000});
            if (cb && (typeof (x) === 'function')) {
                cb()
            }
            break;
        case 'billingquotaexception.product_invalid':
            layer.msg('【使用授权证书】产品或产品款式不存在，请联系系统超级管理员导入新证书。', {icon: 2, time: 5000});
            if (cb && (typeof (x) === 'function')) {
                cb()
            }
            break;
        case 'billingquotaexception.license_expired_or_exhausted':
            layer.msg('【使用授权证书】证书过期或耗尽，请升级证书或绑定新证书。', {icon: 2, time: 5000});
            if (cb && (typeof (x) === 'function')) {
                cb()
            }
            break;
        case 'billingquotaexception.license_disabled_serverinst':
            layer.msg('【使用授权证书】证书商店禁用了本服务实例。', {icon: 2, time: 5000});
            if (cb && (typeof (x) === 'function')) {
                cb()
            }
            break;
        case 'billingquotaexception.license_unsupport_refund':
            layer.msg('【使用授权证书】目前使用的证书不支持回退配额。', {icon: 2, time: 5000});
            if (cb && (typeof (x) === 'function')) {
                cb()
            }
            break;
        case 'billingquotaexception.license_quota_inadequate':
            layer.msg('【使用授权证书】本次操作需要使用的配额资源超过证书中剩余配额，请联系系统超级管理员升级证书。', {
                icon: 2,
                time: 5000
            });
            if (cb && (typeof (x) === 'function')) {
                cb()
            }
            break;
        case 'billingquotaexception.internal_error':
            layer.msg('【使用授权证书】系统错误，稍后再试。', {icon: 2, time: 5000});
            if (cb && (typeof (x) === 'function')) {
                cb()
            }
            break;
        default:
            console.log("[handleGeneralCodeInQueryPathOrApiResp] none code matched", code);
            if (cb && (typeof (x) === 'function')) {
                cb("no_code_matched");
            }
    }
}