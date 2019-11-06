var title = "春松客服-全渠道智能客服" ;
var socket  , newuser = [] , newmessage = [] , ring = [];
newuser['mp3'] = '/images/new.mp3'; 
newmessage['mp3'] = '/images/message.mp3';
ring['mp3'] = '/images/ring.mp3';
$(document).ready(function(){
    var protocol = window.location.protocol.replace(/:/g,'');
    socket = io(protocol+'://'+hostname+':'+port+'/im/agent?orgi='+orgi+"&userid="+userid+"&session="+session+"&admin="+adminuser , {transports: ['websocket'], upgrade: false});
    socket.on('connect',function() {
		console.log("连接初始化成功");
		//请求服务端记录 当前用户在线事件
    }).on('disconnect',function() {
		console.log("连接已断开");       
		//请求服务端记录，当前用户离线
    });
	
    socket.on('chatevent', function(data) {
		console.log(data.messageType + " .....  message:"+data.message);          
    }).on('task', function(data) {
		
    }).on('new', function(data) {
    	if($('#multiMediaDialogWin').length > 0 && multiMediaDialogWin != null && multiMediaDialogWin.$ &&multiMediaDialogWin.$('#agentusers').length > 0){
    		multiMediaDialogWin.Proxy.newAgentUserService(data,"agent");
    	}else{
    		//来电弹屏
			$('#agentdesktop').attr('data-href' , '/agent/index.html?userid='+data.userid).click();
			WebIM.audioplayer('audioplane', newuser, false); // 播放
		}
	}).on('status', function(data) {
		if(orgi == data.orgi){
			$('#agents_status').html("服务中的人数："+data.users+"人，当前排队人数："+data.inquene+"人，在线坐席数："+data.agents+"人，坐席忙："+data.busy+"人");
		}
	}).on('message', function(data) {
		if($('#multiMediaDialogWin').length > 0 && multiMediaDialogWin != null && multiMediaDialogWin.$ && multiMediaDialogWin.$('#agentusers').length > 0){
			multiMediaDialogWin.Proxy.newAgentUserMessage(data,"agent");
			if(data.type == 'message'){
				WebIM.audioplayer('audioplane', newmessage, false); // 播放
			}
		}else{
			//来电弹屏
			$('#agentdesktop').attr('data-href' , '/agent/index.html?userid='+data.userid).click();
		}
	}).on('workorder', function(data) {

	}).on('transout', function(data){
		// TODO 坐席会话被转接出去
		if($('#multiMediaDialogWin').length > 0){
			if(multiMediaDialogWin.document.getElementById('agentusers') != null){
				multiMediaDialogWin.Proxy.transoutAgentUserService(data);
			}
		}
		layer.msg("您与"+data.username+"的会话已被转接给"+data.agentname,{time:1500})

	}).on('audit_message', function(data){
		// 会话监控：消息
		if($('#customerChatAudit').length > 0 && customerChatAudit != null && customerChatAudit.$ && customerChatAudit.$('#agentuserscca').length > 0){
			customerChatAudit.Proxy.newAgentUserMessage(data,"cca");
			if(data.type == 'message'){
				WebIM.audioplayer('audioplane', newmessage, false); // 播放
			}
		}
	}).on('audit_new', function(data){
		// 会话监控：新建
		if($('#customerChatAudit').length > 0 && customerChatAudit != null && customerChatAudit.$ && customerChatAudit.$('#agentuserscca').length > 0){
			customerChatAudit.Proxy.newAgentUserService(data,"cca");
			if(data.type == 'message'){
				WebIM.audioplayer('audioplane', newmessage, false); // 播放
			}
		}
	}).on('audit_end', function(data){
		// 会话监控：结束
		if($('#customerChatAudit').length > 0){
			if(customerChatAudit.document.getElementById('agentuserscca') != null){
				customerChatAudit.Proxy.endAgentUserService(data);
			}
		}
	}).on('end', function(data) {
    	if($('#multiMediaDialogWin').length > 0){
    		if(multiMediaDialogWin.document.getElementById('agentusers') != null){
    			multiMediaDialogWin.Proxy.endAgentUserService(data);
    		}
    	}else{
    		//来电弹屏
    		$('#agentdesktop').attr('data-href', '/agent/index.html?userid='+data.userid).click();
    	}
    }).on('leave', function(data){
		// 执行登出
		window.location.href = "/logout.html?code=2";
	});
	/****每分钟执行一次，与服务器交互，保持会话****/
	setInterval(function(){
		WebIM.ping();
	} , 60000);
});

var WebIM = {
	sendMessage:function(message , userid , appid , session , orgi , touser , agentstatus, agentuserid){
		WebIM.sendTypeMessage(message, userid, appid, session, orgi, touser, agentstatus, null , null,agentuserid) ;
	},
	sendTypeMessage:function(message , userid , appid , session , orgi , touser , agentstatus , msgtype , attachmentid,agentuserid){
		socket.emit('message', {
			appid : appid ,
			userid:userid,
			sign:session,
			touser:touser,
			session: session,
			orgi:orgi,
			username:agentstatus,
			nickname:agentstatus,
			message : message,
			msgtype:msgtype,
			attachmentid:attachmentid,
			agentuser:agentuserid
        });
	},
	// 发送会话监控干预消息
	sendIntervention: function(supervisorid, agentuserid, session, msgtype, content, extra){
		socket.emit('intervention', {
			supervisorid: supervisorid,  // 坐席监控人员ID，必须
			agentuserid: agentuserid,    // 坐席访客会话ID，必须
			msgtype: msgtype,            // 干预消息类型，  必须
			content: content,            // 干预消息内容，  必须
			session: session,            // 登录会话ID
			extra: extra                 // 其它可选属性，  可选
		});
	},
	ping : function(){
		loadURL("/message/ping.html") ;	
		console.log("ping:" + new Date().getTime());
	},
	audioplayer:function(id, file, loop) {
	    var audioplayer = document.getElementById(id);
	    if (audioplayer != null) {
	        document.body.removeChild(audioplayer);
	    }

	    if (typeof(file) != 'undefined') {
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


