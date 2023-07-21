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
// 发送邀请访客进入对话
function sendInvitationToOnlineUser(onlineUserId, isFirstInvite){
    return new Promise(function(resolved, reject){
        // 判断当前用户是否为就绪状态
        if ($("#agentstatus", parent.document).hasClass('layui-form-onswitch') && $("#agentstatus_busy", parent.document).hasClass('layui-form-onswitch-notbusy') && !$("#agentstatus_busy", parent.document).hasClass('layui-form-onswitch-busy') ){
            // 就绪状态
            restApiRequest({
                path: "apps",
                silent: true,
                data: {
                    ops: "invite",
                    userid: onlineUserId
                }
            }).then(function(data){
                if(data && data.rc == 0){
                    // 邀请成功
                    var btn = $('[name="'+onlineUserId+'"]');
                    if(isFirstInvite){
                        btn.removeClass("layui-btn-normal");
                    } else {
                        btn.removeClass("layui-btn-danger");
                    }

                    btn.addClass("layui-btn-warm");
                    btn.text("已邀请");
                    btn.removeAttr("onclick");

                    // 更新邀请次数
                    var invitetimes = $('#it-'+onlineUserId);
                    invitetimes.text(parseInt(invitetimes.text()) + 1);

                    var agentInviteTotalTimes =  $('#agentInviteTotalTimes');
                    agentInviteTotalTimes.text(parseInt(agentInviteTotalTimes.text()) + 1);

                } else {
                    // 邀请不成功
                    handleRestApiFail(data.status || data);
                }
            }, function(error){
                handleRestApiFail(error.status || error);
            })

        } else if (  $("#agentstatus_busy", parent.document).hasClass('layui-form-onswitch-busy') ) {
            // 置忙状态
            invitationInBusyStatus();
            reject("inbusy");
        } else {
            // 非就绪
            invitationInNotReady();
            reject("innotready")
        };
    })
};

// 坐席置忙弹窗提示
function invitationInBusyStatus(status) {
    layer.confirm('在置忙状态不可以邀请访客，是否设置当前状态为置闲？', {btn: ['设置', '再想想'], icon: 4, title:'提示',
        cancel: function (index, layero) {
            //点击关闭按钮
        }
    }, function (index, layero) {
        // 设置为就绪，置闲
        $("#agentstatus_busy", parent.document).removeClass("layui-form-onswitch-busy");
        $("#agentstatus_busy", parent.document).addClass("layui-form-onswitch-notbusy");
        loadURL("/agent/notbusy.html");
        layer.close(index);
    }, function (index) {
        layer.close(index);
    });
}

// 坐席非就绪弹窗提示
function invitationInNotReady(){
    layer.confirm('非就绪状态无法邀请访客，是否设置当前状态为就绪状态?', {btn: ['设置', '再想想'], icon: 4, title:'提示',
        cancel: function (index, layero) {
            //点击关闭按钮
        }
    }, function (index, layero) {
        // 设置为就绪，置闲
        $("#agentstatus", parent.document).addClass("layui-form-onswitch");
        sessionStorage.setItem('agentstatus',"ready")
        loadURL("/agent/ready.html",null, function(){
            $("#agentstatus_busy", parent.document).show();
        });

        layer.close(index);
    }, function (index) {
        layer.close(index);
    });
}