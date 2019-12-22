var layer , iframe , layerwin , cursession  ;
$(document).ready(function(){
	var hide ;
	$('.dropdown-menu').on("click" , function(){
		var distance = getDistance(this);
		if(hide = true){
			$(this).closest(".ukefu-btn-group").addClass("open");
		}else{
			$(this).closest(".ukefu-btn-group").removeClass("open");
		}
		if(distance.right < 200){
			$(this).next().css("right" , "0px").css("left" , "auto");
		}
	}).hover(function(){
		hide = true ;
	} , function(){
		hide = false ;
		var btn = $(this); 
		setTimeout(function(){
			if(hide){
				$(btn).removeClass("open");
			}
		} , 500);
	});
	$('.ukefu-btn-group').hover(function(){
		$(this).addClass("open");
		$(this).find('.ukefu-dropdown-menu').css("right" , "0px").css("left" , "auto");
		hide = false ;
	} , function(){
		hide = true ;
		setTimeout(function(){
			if(hide){
				$(".ukefu-btn-group.open").removeClass("open");
			}
		} , 500);
	});
	layui.use(['layer'], function(){
		layer = layui.layer;	 	 
	});
	//password验证
	layui.use(['form'], function(){
		var form = layui.form();
		form.verify({
			pass: function(value, item){ //value：表单的值、item：表单的DOM对象
			    if(value && !(/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{6,18}$/.test(value))){
			      return '密码由6到18位数字和字母组成';
			    }
			  }
			}); 	 
	});
	$(document).on('click','[data-toggle="tab"]', function ( e ) {
		var type = $(this).data('type');
		if(type == "tabAdd"){
			top.active.tabAdd($(this).data('href') , $(this).data('title'), $(this).data('id'));
		}else if(type == "tabChange"){
			top.active.tabChange($(this).data('href') , $(this).data('title'), $(this).data('id'));
		}
		
		return false;
	});
	$(document).on('click','[data-toggle="ajax"]', function ( e ) {
		var url = $(this).attr("href");
		if(url && url != "javascript:void(0)"){
			var title = $(this).attr("title") ? $(this).attr("title") : $(this).attr("data-title");
			var artwidth = $(this).attr("data-width") ? $(this).attr("data-width") : 800 ;
			var artheight = $(this).attr("data-height") ? $(this).attr("data-height") : 400 ;
			var target = $(this) ;
			top.iframe = window.frameElement && window.frameElement.id || '';
			$.ajax({
				url:url,
				cache:false,
				success: function(data){
					var multiTitle = $(target).data("multi") ;
					if(multiTitle){
						var img = $(target).data("icon") ? $(target).data("icon") : "/images/workorders.png" ;
						var name = title ;
						var text = $(target).data("text") ? $(target).data("text") : title ;
						top.layerwin = top.layer.open({
							title: ["<div style='position: relative;height: 42px;padding: 5px 15px 5px 0px;line-height: 20px;cursor: pointer;display: inline-block;vertical-align: top;'><img src='"+img+"' style='max-height:50px;'><div style='padding:0px 5px;line-height: 23px;display: inline-block;vertical-align: top;'><span style='vertical-align: top;font-size:18px;'>"+name+"</span><p style='vertical-align: top;font-size: 12px;color: #999;'>"+text+"</p></div></div>" , "height:55px"], 
							type: 1, 
							maxmin: true, 
							anim: 2,
							id: 'mainajaxwin', 
							area:[artwidth+"px" , artheight+"px"] ,
							content: data}
						);
					}else{
						top.layerwin = top.layer.open({title:title, type: 1, id: 'mainajaxwin', area:[artwidth+"px" , artheight+"px"] , maxmin: true, anim: 2,content: data});
					}
				}
			});
		}
		
		return false;
	});
	
	$(document).on('click','[data-toggle="load"]', function ( e ) {
		var url = $(this).attr("href");
		var target = $(this).data("target");
		var callback = $(this).data("callback");
		var index = top.layer.load(0, {shade: false});
		$.ajax({
			url:url,
			cache:false,
			success: function(data){
				if(target){
					$(target).empty().html(data);
				}
				top.layer.close(index);
				if(callback){
					eval(callback);
				}
			}
		});
		
		return false;
	});
	
	$(document).on('click','[data-toggle="tip"]', function ( e ) {
		var title = $(this).attr("title") ? $(this).attr("title") : $(this).attr("data-title");
		var href = 	$(this).attr('href')  ;
		var confirm = $(this).data('confirm')  ;
		var target = $(this).data('target')  ;
		if(href == null){
			href = $(this).data('href') ;
		}
		var callback = $(this).data('callback') ;
		top.layer.confirm(title, {icon: 3, title:'提示'}, function(index){
			top.layer.close(index);
			if(confirm){
				top.layer.prompt({title: confirm, formType:1}, function(text, cindex){
					top.layer.close(cindex);
					if(href){
						if(href.indexOf("?") > 0){
							href = href + "&confirm="+text ;
						}else{
							href = href + "?confirm="+text ;
						}
						if(callback!=null){
							eval(callback+"('"+href+"' , '"+target+"')");
						}else{
							location.href = href ;
						}
					}
					
				});
			}else{
				if(href){
					if(callback!=null){
						eval(callback+"('"+href+"' , '"+target+"')");
					}else{
						location.href = href ;
					}
				}
			}
		});
		return false;
	});
	$(document).on('submit.form.data-api','form', function ( e ) {
		var formValue = $(e.target) ;
		var disabled =  $(e.target).data("disabled") ;
		if(disabled !=null && disabled == "true"){
			return false ;
		}else{
			 $(e.target).data("disabled","true");
			var close = $(this).data("close");
			if(iframe){
				$(e.target).attr('target' , iframe);
			}
			if(layerwin && close == null){
				layer.close(layerwin);
			}
		}
	});
	
	/**
	 *表单验证
	 *
	 *
	 */
	$(document).on('submit.form.data-api','[data-toggle="ajax-form"]', function ( e ) {
		var formValue = $(e.target) ;
		var target = $(this).data("target");
		var inner = $(this).data("inner");
		var callback = $(this).data("callback");
		var close = $(this).data("close");
		var message = $(this).data("message");
		var index ;
		if(close == null){
			index = top.layer.load(0, {shade: false});
		}
		$(this).ajaxSubmit({	  
			url:formValue.attr("action"),
			success: function(data){
				if(target){
					$(target).empty().append(data) ;
				}else if(callback){
					var targetIFrame = eval(iframe);
					targetIFrame.Proxy.callback(callback, data) ;
				}else if(inner){
					var targetIFrame = eval(iframe);
					targetIFrame.Proxy.updateData(inner , data) ;
				}
				if(close == null || close == true){
					if(close == null){
						top.layer.close(index);
					}else{
						layer.close(layer.index);
					}
					if(message == false){
						
					}else if(message){
                        top.layer.msg(message,{icon: 1, offset: 'b', time: 1500})
					}else{
                        top.layer.msg('保存成功！',{icon: 1, offset: 'b', time: 1500})
					}
				}
			},
			error:function(xhr, type, s){  				
				//notification("",false);	//结束
			}
		}); 
		return false;
	});
	
	function getDistance(obj) {  
		 var distance = {};  
		 distance.top = ($(obj).offset().top - $(document).scrollTop());  
		 distance.bottom = ($(window).height() - distance.top - $(obj).outerHeight());  
		 distance.left = ($(obj).offset().left - $(document).scrollLeft());  
		 distance.right = ($(window).width() - distance.left - $(obj).outerWidth());  
		 return distance;  
	}
});

function loadURL(url , panel , callback  , append){
	loadURLWithTip(url  , panel , callback , append , false) ;
}

function loadURLWithTip(url , panel , callback , append  , tip){
	var inx ;
	if(tip){
		index = top.layer.load(0, {shade: false});
	}
	$.ajax({
		url:url,
		cache:false,
		success: function(data){
			if(tip){
				top.layer.close(index);
			}
			if(panel){
				if(append){
					$(panel).append(data);
				}else{
					$(panel).empty().html(data);
				}
			}
			if(callback){
				callback(data);			
			}
		},
		error:  function(xhr, type, s){	
			if(xhr.getResponseHeader("emsg")){
				art.alert(xhr.getResponseHeader("emsg"));
			}
		}
	}).done(function(){
		
	});
}

function formatDate(value) {
	var gtoMs = new Date().getTimezoneOffset() * 60000
	if (parent.timeDifference) {
		var date = new Date(value + gtoMs + parent.timeDifference)
	} else {
		var date = new Date(value)
	}
	var y = date.getFullYear();
	var m = date.getMonth() + 1;
	m = m < 10 ? ('0' + m) : m;
	var d = date.getDate();
	d = d < 10 ? ('0' + d) : d;
	var h = date.getHours();
	h = h < 10 ? ('0' + h) : h;
	var minute = date.getMinutes();
	minute = minute < 10 ? ('0' + minute) : minute;
	var second = date.getSeconds();
	second = second < 10 ? ('0' + second) : second;
	return y + '-' + m + '-' + d + ' ' + h + ':' + minute + ':' + second;
}

var Proxy = {
	newAgentUserService:function(data,type){
		if($('#tip_message_'+data.userid).length >0){
            if(data.channel){
                var channel = data.channel;
                switch (channel) {
                    case 'weixin':
                        $('#tip_icon_wenxin_' + data.userid).removeClass('ukefu-channel-icon-end').addClass("ukefu-channel-icon");
                        break;
                    case 'webim':
                        $('#tip_icon_webim_' + data.userid).removeClass('ukefu-channel-icon-end').addClass("ukefu-channel-icon");
                        break;
                    case 'phone':
                        $('#tip_icon_phone_' + data.userid).attr("src","/images/phone-ico.png");
                        break;
                }
            }
		    $('#tip_message_' + data.userid).removeClass('bg-gray').addClass("bg-green").text('在线');
		}else{
			if($('.chat-list-item.active').length > 0){
				var id = $('.chat-list-item.active').data('id') ;
				type ==  "agent" ? loadURL('/agent/agentusers.html?newuser=true&userid='+id , '#agentusers') : loadURL('/apps/cca/agentusers.html?newuser=true&userid='+id , '#agentuserscca');
			}else{
				type ==  "agent" ? location.href = "/agent/index.html?newuser=true" : location.href = "/apps/cca/index.html?newuser=true";
			}
		}
		if(data.userid == cursession){
			$('#agentuser-curstatus').remove();
			type ==  "agent" ? $("#chat_msg_list").append(template($('#begin_tpl').html(), {data: data})) : $("#chat_msg_list_cca").append(template($('#begin_tpl').html(), {data: data}));
		}
	},
	newAgentUserMessage:function(data,type){
		if(data.usession == cursession){
			if(data.type == 'writing' && $('#writing').length > 0){
				$('#writing').remove();
			}
			var id = $('.chat-list-item.active').data('id') ;
			if(data.message!=""){
				data.createtime = formatDate(data.createtime);
				var newlist = template($('#message_tpl').html(), {data: data})
				var nodeMeassage = $(newlist);
				nodeMeassage.find(".iconclick").click(function () {
					if($(this).attr('name') == 'nolabe'){
						$(this).html('&#xe616;')
						$(this).css('color','#46cad4')
						$(this).attr('name','yeslabe')
					}else{
						$(this).html('&#x1005;')
						$(this).css('color','#aaaaaa')
						$(this).attr('name','nolabe')
					}
					$.ajax({
						url: '/agent/agentuserLabel.html',
						data: {'iconid': $(this).attr('id')},
						type: "get",
						success: function () {
						}
					});
				});
				type == "agent" ? $("#chat_msg_list").append(nodeMeassage) : $("#chat_msg_list_cca").append(nodeMeassage);
				type == "agent" ?
					document.getElementById('chat_msg_list').scrollTop = document.getElementById('chat_msg_list').scrollHeight
					: document.getElementById('chat_msg_list_cca').scrollTop = document.getElementById('chat_msg_list_cca').scrollHeight;
			}
			loadURL("/agent/readmsg.html?userid="+data.agentuser);	//更新数据状态，将当前对话的新消息数量清空
		}else{
			if(data.type == 'message'){
				$('#last_msg_'+data.userid).text(data.tokenum).show();
				if(type == "agent"){
					Proxy.addTopMsgTip(1) ;
				}
			}
		}
	},
	quickReply:function(data,type){
		if(data.usession == cursession){
				if(data.message!=""){
					restApiRequest({
						silent: true,
						path: 'chatbot',
						data: {
							ops: 'faq',
							snsaccountid: data.appid ,
							userId:data.userid,
							textMessage:data.message
						}
					}).then(function(result){
						if(result.rc === 0){
							if(result.data.length>0){
								type == "agent" ? $("#quickReplyBox").html("") : $("#ccaQuickReplyBox").html("") ;
								$.each(sortByKey(result.data,'score'),function(i,n){
									var li = ' <li class="ukefu-agentservice-list" onclick="chooseAnswer(\''+result.data[i].reply_plain_text+'\')">\n' +
										'                  <div class="nowrap" title="'+result.data[i].post+'">问题：'+result.data[i].post+'</div>\n' +
										'                    <div style="color: #333">\n' +
										'                       <p class="nowrap" title="'+result.data[i].reply_plain_text+'"  style="float: left ">答案：'+result.data[i].reply_plain_text+'</p>\n' +
										'                       <button style="float: right" class="layui-btn layui-btn-mini" onclick="chooseAnswer(\''+result.data[i].reply_plain_text+'\')">选择</button>\n' +
										'                   </div>\n' +
										'      </li>'
									type == "agent" ? $("#quickReplyBox").append(li) : $("#ccaQuickReplyBox").append(li) ;
									if(i>4){
										return false;
									}
								});
								if(!$("#robot").hasClass('layui-this')){
									$("#dot").css("display","inline-block")
								}
							}else{
								type == "agent" ? $("#quickReplyBox").html("") : $("#ccaQuickReplyBox").html("") ;
								$("#dot").css("display","none")
								var liNone = ' <li style="list-style: none;background-image: url();padding: 50px 0 50px;">\n' +
									'                    <div class="ukefu-empty"  style="background: none">\n' +
									'                        <i class="layui-icon"></i>\n' +
									'                        <div style="">在知识库中未得到相关问题</div>\n' +
									'                    </div>\n' +
									'                </li>'
								type == "agent" ? $("#quickReplyBox").html(liNone) : $("#ccaQuickReplyBox").html(liNone) ;
							}
						}else{
							type == "agent" ? $("#quickReplyBox").html("") : $("#ccaQuickReplyBox").html("") ;
							$("#dot").css("display","none")
						}
					}, function(error){
						console.log("error", error);
						// 服务器异常
						top.layer.msg('服务器抽风，请稍后再试！',{icon: 2, time: 3000})
					})

				}
			}
	},
	endAgentUserService:function(data){
		if($('#tip_message_'+data.userid).length >0){
            if(data.channel){
                var channel = data.channel;
                switch (channel) {
                    case 'weixin':
                        $('#tip_icon_wenxin_' + data.userid).removeClass("ukefu-channel-icon").addClass('ukefu-channel-icon-end');
                        break;
                    case 'webim':
                        $('#tip_icon_webim_' + data.userid).removeClass("ukefu-channel-icon").addClass('ukefu-channel-icon-end');
                        break;
                    case 'phone':
                        $('#tip_icon_phone_' + data.userid).attr("src","/images/cde-ico-gray.png");
                        break;
                }
            }
            $('#tip_message_' + data.userid).removeClass("bg-green").addClass('bg-gray').text('离开');
		}
		if(data.userid == cursession){
			$('#agentuser-curstatus').remove();
			$("#chat_msg_list").append(template($('#end_tpl').html(), {data: data}));
		}
	},
	transoutAgentUserService:function(data){
		if($("#chat_users li").length>1){
			$('#agentuser_' + data.userid).remove();
			$("#chat_users li:first-child a").click();
		}else{
			parent.$('#agentdesktop').click();
		}
	},
	tipMsgForm : function(href){
		top.layer.prompt({formType: 2,title: '请输入拉黑原因',area: ['300px', '50px']} , function(value, index, elem){
			location.href = href+"&description="+encodeURIComponent(value);
			top.layer.close(index);
		});
	},
	// 坐席对话关联联系人
	execLinkContactsFunction: function(data){
		if(data!=null && data!= ""){
			if(typeof ani != "undefined"){
				loadURL("/apps/softphone/search.html?display=false&ani="+ani+"&q="+data, "#ukefu-chat-agent") ;
			}else if(userid && userid != '' && agentserviceid && agentserviceid != '' && agentuserid && agentuserid != ''){
				loadURL("/agent/contacts.html?userid="+userid+"&agentserviceid="+agentserviceid+"&agentuserid="+agentuserid+"&contactsid="+data , "#ukefu_contacts_info") ;
			}
		}
	},
	// 坐席对话取消关联联系人
	execCancelContactsFunction: function(data){
		if (data != null){
			loadURL("/agent/clean/associated.html?currentAgentUserContactsId="+data,"#ukefu_contacts_info");
		}
	},
	updateData : function(inner , data){
		$(inner).empty().append(data) ;
	},
	callback:function(callback , data){
		eval(callback);
	},
	updateFormData : function(inner , data){
		$(inner).val(data).click() ;
	},
	addTopMsgTip : function(num){
		var msgNum = top.$('#ukefu-last-msg').data("num");
		msgNum = msgNum + num ;
		if(msgNum > 0){
			top.$('#ukefu-last-msg').data("num" , msgNum).show();
		}
		top.$('#msgnum').text(msgNum);
	},
	cleanTopMsgTip : function(num){
		var msgNum = top.$('#ukefu-last-msg').data("num");
		msgNum = msgNum - num ;
		if(msgNum > 0){
			top.$('#ukefu-last-msg').data("num" , msgNum).show();
			top.$('#msgnum').text(msgNum);
		}else{
			top.$('#ukefu-last-msg').data("num" , 0).hide();
			top.$('#msgnum').text(0);
		}
	}
}
var active = {
	tabAdd : function(href, title, id , reload) {
		//新增一个Tab项
		var layelement = layui.element();
		if ($('#' + id).length == 0) {
			layelement.tabAdd('ukefutab', {
				title : title //用于演示
				,
				content : '<iframe frameborder="0" src="' + href + '" id="'
						+ id + '" name="' + id
						+ '" width="100%" height="100%"></iframe>',
				id : id
			});
		}
		layelement.tabChange('ukefutab', id);
		$(".layui-this").each(function() {
			if (!$(this).parent().hasClass("layui-tab-title")) {
				$(this).removeClass("layui-this");
			}
		});
		if(reload == null){
			$('#' + id).attr("src", href);
		}
	},
	tabChange : function(href, title, id) {
		var layelement = layui.element();
		var inx = $('#' + id).parent().index();
		if ($('#' + id).length > 0) {
			$('#' + id).attr('src', href);
			layelement.tabChange('ukefutab', id);
		}
		$(".layui-this").each(function() {
			if (!$(this).parent().hasClass("layui-tab-title")) {
				$(this).removeClass("layui-this");
			}

		});
	}
};
