var socketCallout;

$(document).ready(function () {
    var protocol = window.location.protocol.replace(/:/g,'');
    socketCallout = io(protocol + '://' + hostname + ':' + port + '/callout/exchange?orgi=' + orgi + "&userid=" + userid + "&session=" + session + "&admin=" + adminuser, {transports: ['websocket', 'polling']});
    socketCallout.on('connect', function () {
        console.log("Callout 连接初始化成功");
        //请求服务端记录 当前用户在线事件
    })
    .on('new', function (payload) {
        console.log("[callout wire] new: ", payload);
        var data = JSON.parse(payload);
        // if($('#multiMediaDialogWin').length > 0 && multiMediaDialogWin.$ &&multiMediaDialogWin.$('#agentusers').length > 0){
        //     multiMediaDialogWin.Proxy.newAgentUserService(data);
        // }else{
        //     //来电弹屏
        //     $('#agentdesktop').attr('data-href' , '/agent/index.html?userid='+data.userid).click();
        // }
        $('#agentdesktop').attr('data-href' , '/agent/index.html?userid='+data.userid).click();
        WebIM.audioplayer('audioplane', newuser, false); // 播放

    })
        .on('end', function (payload) {
            console.log("[callout wire] end: ", payload);
            var data = typeof payload === 'object' ? payload : JSON.parse(payload);
            if($('#multiMediaDialogWin').length > 0){
                if(multiMediaDialogWin.document.getElementById('agentusers') != null){
                    multiMediaDialogWin.Proxy.endAgentUserService(data);
                }
            }else{
                //来电弹屏
                $('#agentdesktop').attr('data-href', '/agent/index.html?userid='+data.userid).click();
            }
        })
        .on('disconnect', function () {
            console.log("Callout 连接已断开");
            //请求服务端记录，当前用户离线
    });
});