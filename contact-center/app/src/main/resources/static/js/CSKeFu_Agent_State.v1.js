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
// 当坐席从就绪状态，切换到非就绪状态前，请求接口查看当前坐席服务到客户数。
function checkAgentStatusData() {
    var payload = {
        silent: true,
        path: 'agentuser',
        data: { ops: "inserv" },
    };
    return restApiRequest(payload);
}

// 处理坐席服务中访客的数据
function handleAgentStatusData(data){
    return new Promise(function(resolve, reject){
        if ( data.rc == 0) {
            resolve(data.data);
        } else if ( data.rc > 0 ){
            // 代表软件的BUG，比如参数不合法。这个是软件BUG，提示：“请求参数错误，请告知管理员。”
            handleRestApiFail(null, '请求参数错误!');
            reject();
        } else if (data["status"] && data["status"] == "AUTH_ERROR" ){
            //代表API的token过期，提示用“重新登录”
            handleRestApiFail(data.status);
            reject();
        } else {
            handleRestApiFail(null, '服务器处理异常，请稍后再试');
            reject();
        }
    })
}

// RestAPI: 重新分配当前坐席
function reAllotOnlineUsers(){
    var payload = {
        silent: true,
        path: 'agentuser',
        data: { ops: "withdraw" },
    };
    return restApiRequest(payload);
}