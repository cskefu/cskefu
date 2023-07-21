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
function processUserAddOrUpdateResult(responsecode, cb){
    switch (responsecode) {
        case 'username_exist':
            layer.msg('用户名存在，请重新填写',{icon: 2, time: 3000});
            // 清空用户名
            $('input[name="username"]').val("");
            break;
        case 'email_exist':
            layer.msg('邮件存在，请重新填写',{icon: 2, time: 3000});
            // 清空邮件
            $('input[name="email"]').val("");
            break;
        case 'mobile_exist':
            layer.msg('手机存在，请重新填写',{icon: 2, time: 3000});
            // 清空手机号
            $('input[name="mobile"]').val("");
            break;
        case 'sip_account_exist':
            layer.msg('SIP地址已经存在，请重新填写',{icon: 2, time: 3000});
            // 清空SIP
            $('input[name="sipaccount"]').val("");
            break;
        case 'extension_binded':
            layer.msg('分机号已经被其他用户绑定',{icon: 2, time: 3000});
            $('input[name="extensionid"]').val("");
            break;
        case 'extension_not_exist':
            layer.msg('绑定分机不存在',{icon: 2, time: 3000});
            $('input[name="extensionid"]').val("");
            break;
        case 'pbxhost_not_exist':
            layer.msg('指定的呼叫中心语音平台不存在',{icon: 2, time: 3000});
            $('input[name="pbxhostid"]').val("");
            break;
        case 't1':
            layer.msg('当前用户坐席就绪或对话未结束，不能切换为非坐席',{icon: 2, time: 3000});
            break;
        case 'new_user_success':
            layer.msg('新用户创建成功',{icon: 1, time: 1000});
            cb();
            break;
        case 'edit_user_success':
            layer.msg('用户编辑成功',{icon: 1, time: 1000});
            cb();
            break;
    }
}