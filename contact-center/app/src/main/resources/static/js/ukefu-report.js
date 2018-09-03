var layer;
$(document).ready(function(){
	layui.use(['layer'], function(){
		layer = layui.layer;	 	 
	});
	/**
	 *表单验证
	 *
	 *
	 */
	$('body').on('submit.form.data-api', '[data-toggle="ajax-form"]', function ( e ) {
		closeDialog = false ;
		var close = $(e.target).attr("data-close");
		var width = $(e.target).attr("data-width") ;
		if(close){
			closeDialog = true ;
		}
		var formValue = $(e.target) ;
			$(this).ajaxSubmit({	  
				url:formValue.attr("action"),
				success: function(data){
					if(formValue.attr("data-callback")){
						eval(formValue.attr("data-callback")+'($(formValue.attr("data-target")) , data)');
					}else{
						var target = formValue.attr("data-target");
						if(formValue.attr("data-target")){
							$(formValue.attr("data-target")).empty().html(data);
						}else{
							//d-content
							$(target = '.d-content').empty().html(data);							
						}
						if(!closeDialog){							
							if(width){
								$(target).width(width) ;
							}
						}
					}					
						
				},
				error:function(xhr, type, s){  				
					
				}
			});
			DialogHelper.close();
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
				$(target).empty().html(data);
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
	
});
function loadURL(url , panel , callback  , append){
	loadURLWithTip(url  , panel , callback , append , false) ;
}

function loadURLWithTip(url , panel , callback , append  , tip){
	let inx ;
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
