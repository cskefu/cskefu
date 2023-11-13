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
var title = "春松客服-开源客服系统";
var socket, newuser = [], newmessage = [], ring = [];
newuser['mp3'] = '/images/new.mp3';
newmessage['mp3'] = '/images/message.mp3';
ring['mp3'] = '/images/ring.mp3';
$(document).ready(function () {
    var protocol = window.location.protocol.replace(/:/g, '');
    socket = io(protocol + '://' + hostname + ':' + port + "/im/agent?userid=" + userid + "&session=" + session + "&admin=" + adminuser, {
        transports: ['websocket'],
        upgrade: false
    });
    socket.on('connect', function () {
        console.log("[IM] 连接初始化成功");
        //请求服务端记录 当前用户在线事件
    }).on('disconnect', function () {
        console.log("[IM] 连接已断开");
        //请求服务端记录，当前用户离线
    });

    socket.on('chatevent', function (data) {
        // console.log(data.messageType + " .....  message:"+data.message);
    }).on('task', function (data) {
    }).on('new', function (data) {
        console.log("new data ...", data);
        if ($('#customerChatAudit').length > 0) {
            if (customerChatAudit.$('#agentuser_' + data.userid).length > 0 && customerChatAudit.$("#chat_users li").length > 1) {
                customerChatAudit.$('#agentuser_' + data.userid).remove();
                customerChatAudit.$("#chat_users li:first-child a").click();
            } else {
                customerChatAudit.$('#ccaIndex').html("<div class=\"layui-layout layui-layout-content\"  style=\"height: 100%;\">\n" +
                    "    <div class=\"box default-box\" style=\"height: 100%;\">\n" +
                    "        <div class=\"box-body ukefu-im-theme\">\n" +
                    "            <div class=\"ukefu-empty\" style=\"background: none\">\n" +
                    "                <i class=\"layui-icon\">&#xe63a;</i>\n" +
                    "                <div style=\"\">还没有任何对话</div>\n" +
                    "            </div>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</div>");
            }
        }

        if ($('#multiMediaDialogWin').length > 0 &&
            multiMediaDialogWin != null &&
            multiMediaDialogWin.$ &&
            multiMediaDialogWin.$('#agentusers').length > 0) {
            multiMediaDialogWin.Proxy.newAgentUserService(data, "agent");
        } else {
            //来电弹屏
            $('#agentdesktop').attr('data-href', '/agent/index.html?userid=' + data.userid + '&licenseVerifiedPass=' + data.licenseVerifiedPass + '&licenseBillingMsg=' + data.licenseBillingMsg).click();
            WebIM.audioplayer('audioplane', newuser, false); // 播放
        }
    }).on('status', function (data) {
        $.post('/lazyAgentStatus').success(function (html) {
            $('#agents_status').html(html);
        });
        // $('#agents_status').html("服务中的人数："+data.users+"人，当前排队人数："+data.inquene+"人，在线坐席数："+data.agents+"人，坐席忙："+data.busy+"人");
    }).on('message', function (data) {
        if ($('#multiMediaDialogWin').length > 0 && multiMediaDialogWin != null && multiMediaDialogWin.$ && multiMediaDialogWin.$('#agentusers').length > 0) {
            multiMediaDialogWin.Proxy.newAgentUserMessage(data, "agent");
            if (data.type == 'message') {
                WebIM.audioplayer('audioplane', newmessage, false); // 播放
                if (multiMediaDialogWin.isAisuggest && multiMediaDialogWin.isAisuggest == "true") {
                    multiMediaDialogWin.Proxy.quickReply(data, "agent");
                }
            }
        } else {
            //来电弹屏
            $('#agentdesktop').attr('data-href', '/agent/index.html?userid=' + data.userid).click();
        }
    }).on('workorder', function (data) {

    }).on('transout', function (data) {
        // TODO 坐席会话被转接出去
        if ($('#multiMediaDialogWin').length > 0) {
            if (multiMediaDialogWin.document.getElementById('agentusers') != null) {
                multiMediaDialogWin.Proxy.transoutAgentUserService(data);
            }
        }
        layer.msg("您与" + data.username + "的会话已被转接给" + data.agentname, {time: 1500})

    }).on('audit_message', function (data) {
        // 会话监控：消息
        if ($('#customerChatAudit').length > 0 && customerChatAudit != null && customerChatAudit.$ && customerChatAudit.$('#agentuserscca').length > 0) {
            customerChatAudit.Proxy.newAgentUserMessage(data, "cca");
            if (data.type == 'message') {
                WebIM.audioplayer('audioplane', newmessage, false); // 播放
                if (customerChatAudit.isCcaAisuggest && customerChatAudit.isCcaAisuggest == "true") {
                    customerChatAudit.Proxy.quickReply(data, "cca");
                }
            }
        }
    }).on('audit_new', function (data) {
        // 会话监控：新建
        if (skills.indexOf(data.skill) > -1 && $('#customerChatAudit').length > 0 && customerChatAudit != null && customerChatAudit.$) {
            customerChatAudit.Proxy.newAgentUserService(data, "cca");
            if (data.type == 'message') {
                WebIM.audioplayer('audioplane', newmessage, false); // 播放
            }
        }
    }).on('audit_end', function (data) {
        // 会话监控：结束
        if ($('#customerChatAudit').length > 0) {
            if (customerChatAudit.document.getElementById('agentuserscca') != null) {
                customerChatAudit.Proxy.endAgentUserService(data);
            }
        }
    }).on('end', function (data) {
        console.warn(111111, data)
        if ($('#multiMediaDialogWin').length > 0) {
            if (multiMediaDialogWin.document.getElementById('agentusers') != null) {
                multiMediaDialogWin.Proxy.endAgentUserService(data);
            }
        } else {
            //来电弹屏
            $('#agentdesktop').attr('data-href', '/agent/index.html?userid=' + data.userid).click();
        }
    }).on('leave', function (data) {
        top.layer.msg('当前会话已经过期，稍后将自动登出！', {icon: 1, time: 2000});
        setTimeout(function () {
            // 执行登出
            window.location.href = "/logout.html?code=2";
        }, 2000);
    });
    /****每分钟执行一次，与服务器交互，保持会话****/
    setInterval(function () {
        WebIM.ping();
    }, 60000);
});

var WebIM = {
    sendMessage: function (message, userid, appid, session, touser, agentstatus, agentuserid) {
        WebIM.sendTypeMessage(message, userid, appid, session, touser, agentstatus, null, null, agentuserid);
    },
    sendTypeMessage: function (message, userid, appid, session, touser, agentstatus, msgtype, attachmentid, agentuserid) {
        socket.emit('message', {
            appid: appid,
            userid: userid,
            sign: session,
            touser: touser,
            session: session,
            username: agentstatus,
            nickname: agentstatus,
            message: message,
            msgtype: msgtype,
            attachmentid: attachmentid,
            agentuser: agentuserid
        });
    },
    // 发送会话监控干预消息
    sendIntervention: function (supervisorid, agentuserid, session, msgtype, content, extra) {
        socket.emit('intervention', {
            supervisorid: supervisorid,  // 坐席监控人员ID，必须
            agentuserid: agentuserid,    // 坐席访客会话ID，必须
            msgtype: msgtype,            // 干预消息类型，  必须
            content: content,            // 干预消息内容，  必须
            session: session,            // 登录会话ID
            extra: extra                 // 其它可选属性，  可选
        });
    },
    ping: function () {
        loadURL("/message/ping.html");
        console.log("[IM] heartbeat:" + new Date().getTime());
    },
    audioplayer: function (id, file, loop) {
        var audioplayer = document.getElementById(id);
        if (audioplayer != null) {
            document.body.removeChild(audioplayer);
        }

        if (typeof (file) != 'undefined') {
            if (navigator.userAgent.indexOf("MSIE") > 0) { // IE
                var player = document.createElement('bgsound');
                player.id = id;
                player.src = file['mp3'];
                player.setAttribute('autostart', 'true');
                if (loop) {
                    player.setAttribute('loop', 'infinite');
                }
                document.body.appendChild(player);

            } else { // Other FF Chome Safari Opera
                var player = document.createElement('audio');
                player.id = id;
                player.setAttribute('autoplay', 'autoplay');
                if (loop) {
                    player.setAttribute('loop', 'loop');
                }
                document.body.appendChild(player);

                var mp3 = document.createElement('source');
                mp3.src = file['mp3'];
                mp3.type = 'audio/mpeg';
                player.appendChild(mp3);
            }
        }
    }
}


