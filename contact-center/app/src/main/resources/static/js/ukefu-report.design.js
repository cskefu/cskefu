var hasData = false,layer;
$(document).ready(function(){
	$(".tpcom").niceScroll({
		cursorcolor:"#d7d7d7"
	}); 
	SpaceTools.init();
	/*$(window).on('beforeunload',function() {   	
		//这里用于防止用户频繁刷新或者不小心关闭。
	//	return "你确定关闭?";
		
		if(hasData == true){
			return "本次修改的内容还未保存，离开页面会导致设计的页面内容丢失，请确认是否退出？";
		}
	});*/
	layui.use(['layer'], function(){
		layer = layui.layer;	 	 
	});
	$(document).on("click" , '[data-toggle="dropdown"]' , function(e){
		if($(this).next().hasClass("ukefu-dropdown-menu")){
			$(this).next().css("top" , ($(this).offset().top - 2)+"px");		
		}
	});
	$(document).on("click" , '.ukefu-report-design' , function(e){
		var dsid = $(this).closest(".ukefu-title").attr("data-dsid") ;
		var colspan = $(this).closest(".ukefu-col").attr("data-colspan") ;
		var templet  = $(this).closest(".ukefu-title").attr("data-templet") ;
		var id = $(this).attr("data-id") ;

		if(localStorage && localStorage.getItem(id)){
			var localData = localStorage.getItem(id);
			var localDsid = localStorage.getItem(id+"_dsid");
			if(localData && localData != "undefined"){
				$('#struct_'+id).val(localData);				
			}
			if(localDsid && localDsid != "undefined"){
				$('#t_'+id).attr("data-dsid" , localDsid) ;
				dsid = localDsid ;
			}
			localStorage.removeItem(id);
			localStorage.removeItem(id+"_dsid");

			hasData = true ;
			/*****本地有临时存储的数据，需要更新图表****/
			DefaultHelper.refresh(dsid , templet , id , localData) ;
		}
		if(!dsid || dsid == "undefined"){
			dsid = "" ;
		}
		$("#form_"+id).attr("action" , $(this).attr("data-href")+"&dsid="+dsid+"&colspan="+colspan).submit();
	});
	
});
var deletemodel = function(id){
	let thisid = id?id:$("#rmLayout").attr("data-val");
	$.ajax({
		url:'/apps/report/design/modeldelete.html?id='+thisid,
		cache:false,
		success: function(data){
			$('.'+thisid).remove();	
		}
	});
	
}
var deletefilter = function(id){
	let thisid = id?id:"";
	$.ajax({
		url:'/apps/report/design/rfilterdel.html?id='+thisid,
		cache:false,
		success: function(data){
			$('#'+thisid).remove();	
		}
	});
	
}
var ElementHelper = {
	init : function(clazz){
		$(clazz).each(function(){
			$(this).prepend(ElementHelper.getRowHandle(defaultTplType));
		});
	},
	process:function(tpl , handle){
		return $(tpl).prepend(handle)[0].outerHTML;
	},
	getRowHandle:function(type){
		return 	"<div class='handle' data-type='"+type+"'>拖拽</div>";
	}
}

var btnUndo,btnRedo,btnClear , $templateCache = new Map() , defaultTplType = 'layout', 
	tpltypes = {
		layout : new TemplateType("layout" , "布局组件" , "/template/layoutprop.html"),
		element : new TemplateType("element" , "布局组件" , "/design/default/chart.html"),
		filter : new TemplateType("filter" , "过滤器组件" , "/design/default/filter.html")	
	},tplMap = new Map();

var TempletHelper = {
	getTyepletType:function(type){
		var tpltype ;
		if(type == "layout"){
			tpltype = tpltypes.layout;		
		}else if(type == "element"){
			tpltype = tpltypes.element;		
		}else if(type == "filter"){
			tpltype = tpltypes.filter;		
		}
		return tpltype ;
	},
	getTemplet : function(type , ui){
		var tpltype = tpltypes.layout ;
		if($(ui.helper).attr("data-type") == "element"){
			tpltype =  tpltypes.element ;
		}else if($(ui.helper).attr("data-type") == "filter"){
			tpltype =  tpltypes.filter ;
		}
		var tpl = new Template($(ui.helper).attr("data-id") , $(ui.helper).attr("data-title") , tpltype , $(ui.helper).attr("data-templet"));
		tpl.colspan = $(ui.helper).data("colspan") ;
		return tpl ;
	},
	doRender:function(tpl, content , div ,callback , helper , model){
		if(tpl){
			if(helper){
				callback(template(ElementHelper.process(content , tpl.handle()) , {m:model}) , model) ;	//渲染模板
			}else{
				callback(template(content , {m:model}) , model) ;	//渲染模板
			}
		}	
	},
	renderTemplet:function(tplid, ui ,div ,callback , helper){
		tpl = TempletHelper.getTemplet(tplid , ui) ;				//获取模板 
		var colspan = $(div).data("colspan");
		var model = {id:Math.uuid(6)} ;
		var parentid = $(div).data("mid");
		var colindex = $(div).data("index");
		
		
		UKHelper.loadURLWith(tpl.url+"&colindex="+colindex+"&parentid="+parentid+"&mid="+model.id+"&colspan="+tpl.colspan, null , function(data){ 		
			TempletHelper.doRender(tpl , data , div , callback , helper , model);
		});
		/****
		if(!$templateCache.containsKey(tplid)){
			UKHelper.loadURLWith(tpl.url+"&mid="+model.id , null , function(data){ 		
				TempletHelper.doRender(tpl , data , div , callback , helper , model);
			});
		}else{
			TempletHelper.doRender(tpl , $templateCache.get(tplid) , div , callback , helper , model);
		}
		**/
		
	}	
}
var SpaceTools = {
	init : function(){	 //初始化方式，如果是静态布局，则可以拖拽，可以改变尺寸，如果是layout，则不可拖拽，不可改变尺寸
		KeyEvent.init();				//初始化热键
		ElementHelper.init('.content >.ukefu-row');				//初始化拖拽辅助条

		LayoutHelper.init();

	    TeamHelper.init();

		$(".comp-item").draggable({		//初始化 模板列表 的拖拽动作
			connectToSortable:".content",
			helper: "clone",			
			stop:function(event , ui){
				$(".content .comp-item").remove();
			}
		});

		$(".model-item").draggable({		//初始化 模板列表 的拖拽动作
			connectToSortable:".ukefu-col",
			helper: "clone",
			start:function(event , ui){
				console.log(ui);
			},
			stop:function(event , ui){
				$(".content .model-item").remove();	
			}
		});
		$(".filter-item").draggable({		//初始化 模板列表 的拖拽动作
			connectToSortable:"#ukefu-filter-list",
			helper: "clone",			
			start:function(event , ui){
			},
			stop:function(event , ui){
				$("#ukefu-filter-list .filter-item").remove();	
				$(".ukefu-filter-submit").show();
			}
		});

		$("#ukefu-filter-list").sortable({ 
			handle:".form-group",
			placeholder: "ui-filter-highlight",
			stack: { group: ".filter-list-item"},	
			stop:function(event , ui){				
				
			},
			sort:function(event , ui){
				
			},
			receive: function(event, ui) {
				if(ui.helper){
					$(".ukefu-filters-content").removeClass("dropin") ;
					SpaceTools.processModel(event , ui , $(this) , null , true);	
					$('#filter-btn').show();
				}
			}
		});

		

		SpaceTools.dynamic(".design" , true);

		SpaceTools.bind(".design .ukefu-col" , ".ukefu-col" , ".ukefu-col"); //绑定排序事件 

		SpaceTools.save();

		$(document).on("dblclick" , '.ukefu-row', function(event){ 
			if(!$(event.target).hasClass("handle")){
				if(!event.ctrlKey){
					$(".cur_row").removeClass("cur_row");
				}
				$(this).addClass("cur_row");
				
				$("#rmLayout").removeClass("layui-btn-disabled");
				$("#rmLayout").removeAttr("disabled");
				$("#rmLayout").attr("data-val",$(this).attr("data-val"));
				event.stopPropagation();	
			}
		});

		$(document).on("dblclick" , '.filter-list-item', function(event){ 
			if(!$(this).parent().hasClass("cur_row")){
				$(".cur_filter").removeClass("cur_filter");
				$(this).addClass("cur_filter");
			}else{
				$(this).removeClass("cur_filter");
			}
		});

		$(document).on("click" , function(event){
			if(!$(event.target).hasClass("handle") && $(event.target).attr("id") != "rmLayout"){
				$(".cur_row").removeClass("cur_row");
				$("#rmLayout").addClass("layui-btn-disabled");
				$("#rmLayout").attr("disabled",true);
			}
		});
		/*$(document).on("click" , "#rmLayout" , function(){
			if($(".ukefu-filters-content > .cur_row").length > 0){
				art.confirm('此操作将删除选中的过滤器，并且无法恢复，您确定要删除此过滤器吗？' , function(){
					$(".ukefu-filters-content .cur_filter").each(function(){
						$(this).remove();
					});
					if($(".ukefu-filters-content .filter-list-item").length == 0){
						$(".ukefu-filters-content").addClass("dropin");
						$('#filter-btn').hide();
					}
				} , function(){})	;
			}else{
				art.confirm('此操作将删除全部选中的模块内容，并且无法恢复，您确定要删除此模块吗？' , function(){
					$(".design > .cur_row , .ukefu-model-content > .cur_row").each(function(){
						$(this).remove();
					});
				} , function(){})	;
			}
		});*/
		/***
		$(document).on("dblclick" , '.handle', function(event){ 
			$(this).parent().click();								//将当前对象的父级对象触发为选中状态
			if($(this).parent().parent().hasClass("design")){
				var type = $(this).attr("data-type");
				var tpl = TempletHelper.getTyepletType(type) ;
				var rid = $(this).parent().attr("data-rid");
				var id = $(this).parent().attr("id");
				var rowsid = $(this).parent().attr("data-rows");
				var rows = "" ;
				if(rowsid){
					rows = $('#'+rowsid).val();
				}
				if(tpl){
					UKHelper.tipArtPage("设置“"+tpl.name+"”属性" , tpl.prop+"?id="+id+"&rid="+rid+"&mtype="+$(this).parent().attr("data-mtype")+"&rows="+rows , 750 , true);			
				}
			}
		});
		
		$(document).on("dblclick" , '.ukefu-title', function(event){ 
			$(this).parent().click();								//将当前对象的父级对象触发为选中状态
			if($(this).parent().parent().parent().parent().hasClass("design")){
				var type = $(this).attr("data-type");
				var mid = $(this).attr("data-mid");
				var rid = $(this).attr("data-rid");
				var dsid = $(this).attr("data-dsid");
				var templet = $(this).attr("data-templet");
				if(!mid){mid = "" ;}if(!rid){rid = "" ;}if(!dsid){dsid = "" ;}

				var tpl = TempletHelper.getTyepletType(type) ;
				if(tpl){
					UKHelper.tipArtPage("设置“"+tpl.name+"”属性" , tpl.prop+"?templet="+templet+"&rid="+rid+"&mid="+mid+"&dsid="+dsid , 550 , true);			
				}
			}
		});
		**/
	},
	initmodel:function(){

		LayoutHelper.init(".ukefu-prop");

		$(".ukefu-tab-content .comp-item").draggable({		//初始化 模板列表 的拖拽动作
			connectToSortable:".ukefu-model-content",
			helper: "clone",			
			stop:function(event , ui){
				ui.helper.remove();	
			}
		});
		$(".ukefu-tab-content .model-item").draggable({		//初始化 模板列表 的拖拽动作
			connectToSortable:".ukefu-prop-col",
			helper: "clone",			
			stop:function(event , ui){
				ui.helper.remove();	
			}
		});
		ElementHelper.init('.ukefu-model-content >.ukefu-row');				//初始化拖拽辅助条

		SpaceTools.dynamic(".ukefu-model-content" , true);

		SpaceTools.bind(".ukefu-model-content  .ukefu-prop-col" , ".ukefu-prop-col" , ".ukefu-prop-col"); //绑定排序事件
	},
	dynamic:function(clazz , helper , filter){ 	
		
		$(clazz).sortable({ 
			handle:".handle",
			placeholder: "ui-th-highlight",
			stack: { group: ".comp-item"},	
			stop:function(event , ui){				
				
			},
			sort:function(event , ui){
				
			},
			receive: function(event, ui) {
				if(ui.helper){
					SpaceTools.processModel(event , ui , $(this) , helper , true);	
				}
			}
		});	
		

		$(clazz).selectable({
			filter:".ukefu-col,.ukefu-prop-col" ,
			cancel: ".handle",
			start:function(){
				$(".ui-selected").removeClass("ui-selected");
			},
			selected:function(){
				if($(".ui-selected").length > 0){
					$("#coltools").removeClass("disabled");
					if($(".ui-selected").length == 1){
						$(".multicol").addClass("disabled");
						$(".singlecol").removeClass("disabled");
						$(".allcol").removeClass("disabled");
					}else{
						$(".multicol").removeClass("disabled");
						$(".singlecol").addClass("disabled");
						$(".allcol").removeClass("disabled");
					}
				}else{
					$("#coltools").addClass("disabled");
				}
			},
			unselected:function(){
				LayoutHelper.disabled();
			}
		});
		
	},
	bind:function(clazz , coltype , group){
		$(clazz).sortable({ 
			handle:".ukefu-title",
			connectWith:coltype,
			placeholder: "ui-th-highlight",
			stack: { group: group},	
			stop:function(event , ui){
				
			},
			sort:function(event , ui){
				
			},
			receive: function(event, ui) {
				if(ui.helper){
					SpaceTools.processModel(event , ui , $(this) , false , false);	
				}else if(ui.item){
					UKHelper.loadURLWith("/apps/report/design/element.html?id="+ui.item.attr("id")+"&parentid="+ui.item.closest(".ukefu-col").data("mid")+"&colindex="+ui.item.closest(".ukefu-col").data("index"));
				}
			}
		});
	},
	processModel:function(event , ui , target , helper , bind){  
		var title = ui.helper.attr("data-title");
		var id = ui.helper.attr("data-id");
		var index = $(target).data("ui-sortable").currentItem.index() ;

		UKHelper.render(id , ui , target, function(data , model){
			if($(target).children().length > 0 ){
				if(index > 0){
					$(target).children().eq(index-1).after(data) ;
				}else{
					$(target).children().eq(index).before(data) ;
				}	
			}else{
				$(target).append(data);	
			}
			
			if(bind){
				var coltype = ".ukefu-col"  , group = ".ukefu-col";
				if($(target).closest(".ukefu-model-content").length >0){
					coltype = ".ukefu-prop-col" ;
					group = ".ukefu-prop-col" ;
				}
				SpaceTools.bind("#" + model.id + " "+coltype , coltype , group); //绑定排序事件
			}
			hasData = true ;

		} , helper);		

	},
	save:function(){
		$("#save").on("click" , function(){
			IoHelper.save();	
		});	
		$("#teamSave").on("click" , function(){
			IoHelper.teamsave();	
		});
	}	   	
}
var ChartHelper = {
	init:function(container){
		return echarts.init(document.getElementById(container));	
	}
}
var IoHelper = {
	save:function(){
		var json = [];
		$(".ukefu-row").each(function(){
			var row = {id:$(this).attr("id"),reportid:$(this).attr("data-rid"),labeltext:$(this).attr('data-mtype'),chartemplet:$(this).attr("data-templet")} ;
			row.modelPackageList = [] ; 
			$(this).find(".ukefu-col").each(function(){
				var mpk = {cols:$(this).attr("data-colspan"),layoutcontent:[[{models:[]}]]};
				$(this).children(".ukefu").each(function(){
					/****序列化****/
					mpk.layoutcontent[0][0].models.push(IoHelper.serial($(this)));
				});	 
				
				row.modelPackageList.push(mpk);	
			});
			json.push(row);
		});
		art.dialog({
			lock: false,
			id:'dialog',
			fixed: true,
			width: 230 ,
			padding:'1px 1px',
			title: "操作提示", 
			content: defaultSaveHtml,
			initialize: function () {
				var currentDialog = this;
				var filters = [] ;
				if($(".filters").length > 0){
					$(".filters").each(function(){
						filters.push(Base64.decode($(this).val()));
					});
				}
				
				$.post(report.save,{json: JSON.stringify(json) , filters : "["+filters.join(",")+"]"},function(data){
					currentDialog.close();
					if(data == "1"){
						alertMsg("页面保存成功！");	
						hasData = false ;
					}else{
						alertMsg("页面保存失败！");				
					}
				});	
			}
		});
		
	},
	teamsave:function(target){
		TeamHelper.save(target);
	},
	setModelValue:function(mid , data , dsid){
		var msg = 0 ;
		if(mid!="" && data!=""){
			$("#t_"+mid).attr("data-dsid" , dsid);
			$("#struct_"+mid).val(data) ;
			var dsid = $("#t_"+mid).attr("data-dsid") ;
			var templet = $("#t_"+mid).attr("data-templet") ;

			//保存完毕，需要刷新当前模块
			DefaultHelper.refresh(dsid , templet , mid , data) ;
			msg = 1;
			hasData = true ;
		}		
		return msg ;
	},
	serial:function(box){
		var model = $(box).find(".ukefu-title")[0] ;
		var id = $(model).attr("data-mid");
		var box = null ;

		if(id){
			var json =Base64.decode($('#struct_'+id).val()) ;			
			box = JSON.parse(json) ; 	
		}else{
			box = {id:'',tableid:'',chartemplet:'',struct:''} ;			
			box.id = $(model).attr("data-mid");
			box.tableid = $(model).attr("data-dsid");
			box.chartemplet = $(model).attr("data-templet");
		}
		
		return box ;
	}
}
var LayoutHelper = {
	init:function(selector){
		if(selector){
			$(selector + " .mergecol").on("click" , LayoutHelper.merge);
			$(selector + " .clearcol").on("click" , LayoutHelper.clear);
			$(selector + " .splitcol").on("click" , LayoutHelper.split);
		}else{
			$("#mergecol").on("click" , LayoutHelper.merge);
			$("#clearcol").on("click" , LayoutHelper.clear);
			$("#splitcol").on("click" , LayoutHelper.split);
		}
	},
	changetype:function(id , type){
		$('#'+id).removeClass("am-topbar").removeClass("footer");	
		if(type == "header"){
			$('#'+id).addClass("am-topbar");
		}else if(type == "footer"){
			$('#'+id).addClass("footer");
		}
		$('#'+id).attr("data-mtype" , type) ;
		DialogHelper.close();
	},
	merge : function(){
		var colspan = 0 , merge , newcolspan;
			
		for(i=0 ; i< $(".ui-selected").length ; ){
			var selected = $(".ui-selected")[i] ; 
			var curcolspan = $(selected).attr("data-colspan") ;
			newcolspan = colspan + parseInt(curcolspan) ;
			if(merge == null){
				merge = $(selected) ;
				i++;
			}else{ 				
				merge.removeClass("col-md-"+colspan).addClass("col-md-"+newcolspan).removeClass("am-u-lg-"+colspan).addClass("am-u-lg-"+newcolspan).attr("data-colspan",newcolspan).append($(selected).html());
				$(selected).remove();
			}
			colspan = newcolspan ;
			
		};
		LayoutHelper.disabled();	
	},
	clear:function(){
		$(".ui-selected").empty();
		LayoutHelper.disabled();	
	},
	split:function(){
		$(".ui-selected").each(function(){
			var colspan = $(this).attr("data-colspan") , colspannum ;
			if(colspan){
				colspannum = parseInt(colspan);
				if(colspannum > 1){
					var newcolspan = Math.ceil(colspannum/2) , newcolwidth = Math.floor(colspannum/2);
					var id = Math.uuid(6);
					
					/**************需要为新增的单元格绑定事件********************/
					var coltype = ".ukefu-col"  , group = ".ukefu-col" , css = "ukefu-col";
					if($(this).closest(".ukefu-model-content").length >0){
						coltype = ".ukefu-prop-col" ;
						group = ".ukefu-prop-col" ;
						css = "ukefu-prop-col" ;
					}
					$(this).removeClass("col-md-"+colspan).addClass("col-md-"+newcolspan).removeClass("am-u-lg-"+colspan).addClass("am-u-lg-"+newcolspan).attr("data-colspan",newcolspan).after('<div class="col-md-'+newcolwidth+' '+css+'" id="'+id+'" data-colspan="'+newcolwidth+'">') ;

					SpaceTools.bind("#"+id , coltype , group); //绑定排序事件
				}
			}
			
		});
		LayoutHelper.disabled();
	},
	disabled:function(){
		$("#coltools").addClass("disabled");
		$(".multicol").addClass("disabled");
		$(".singlecol").addClass("disabled");
		$(".allcol").addClass("disabled");
	}
}
var UKHelper = {
	sendAuthCode:function(mobile , mobileelement , btn){
		if( $(btn).attr("disabled") == null){
			if(mobile!="" && (mobile.length == 11 && /^(((13[0-9]{1})|(15[0-9]{1})|(17[0-9]{1})|(14[0-9]{1})|(18[0-9]{1}))+\d{8})$/.test(mobile))){
				UKHelper.loadURLWith("/user/sendauthcode.html?mobile="+mobile , null , function(data){
					if(data!=""){
						alertMsg(data) ;
					}
				});
				var time = 60 ;
				$(btn).text("等待 "+(time--)+" 秒");
				UKHelper.resetTimer(time , btn);
				$(btn).attr("disabled" , "disabled") ;
			}else{
				alertMsg("请输入正确的手机号码");
			}
		}
	},
	resetTimer:function(time , btn){
		setTimeout(function(){
			if(time>0){
				$(btn).text("等待 "+(time--)+" 秒");
				UKHelper.resetTimer(time , btn);
			}else{
				$(btn).text("发送验证码").attr("disabled",false);	
			}
								
		} , 1000);
			
		return time ;
	},
	render:function(tplid , ui , div , callback , helper){
		TempletHelper.renderTemplet(tplid , ui , div , callback , helper);		
	},
	loadURLWith:function(url , panel , callback , append){
		$.ajax({
			url:url,
			cache:false,
			async: false,
			success: function(data){
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
			
			//var dialogNicescroll = $(".setScroll").niceScroll({cursorcolor:"#d7d7d7"});
		});
	},
	tipArtPage : function (title , url , artwidth , lock  , callback){
		var list = art.dialog.list;
		for (var i in list) {
			list[i].close();
		};
		art.dialog({
			lock: lock,
			id:'curDialog',
			fixed: true,
			width: artwidth ,
			padding:'1px 1px',
			title: title, 
			content: defaultLoadHtml,
			initialize: function () {
				var currentDialog = this;
				$.ajax({
					url:url,
					cache:false,
					title:title,
					success: function(data){
						$('.d-content').empty().html(data);
						$('.d-main').width(artwidth);
						if(callback){
							eval(callback) ;
						}
						currentDialog._reset();
					},
					error:  function(xhr, type, s){	
						if(xhr.getResponseHeader("emsg")){
							art.alert(xhr.getResponseHeader("emsg"));
						}
					}
				}).done(function(){
					//var dialogNicescroll = $(".setScroll").niceScroll({cursorcolor:"#d7d7d7"});
				});
			}
		});
		
	},
	
	submit:function(dom , url , panel , callback , append){
		$(panel).empty().html("<div style='padding-top:100px;width:100%;height:270px;    background: #000;filter: alpha(opacity=70);opacity: 0.2;text-align:center;'><span style='margin-top:100px;color:#fff;'>正在加载...<span></div>");
		$(dom).ajaxSubmit({	  
			url:url,
			success: function(data){
				
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
			}
		});
	},
	getLayDateType:function(formatStr){
		let type = "date";
		if(!formatStr){
			return type;
		}
		if(/^.*(s|m|H).$/.test(formatStr)){
			return "datetime";
		}
		if(/^.*d.$/.test(formatStr)){
			return "date";
		}
		if(/^.*M.$/.test(formatStr)){
			return "month";
		}
		if(/^.*y.$/.test(formatStr)){
			return "year";
		}
		return type;
	}
	
}
var KeyEvent = { 	
	init:function(){
		KeyEvent.delete();					//按键删除选中的对象
		KeyEvent.esc();						//按下ESC键取消当前选择的 工具栏菜单
		KeyEvent.ctra();					//按下CTRL+A键全选
	},
	delete:function(){
		$(document).bind('keydown', 'del',function (evt){
			art.confirm('此操作将删除全部选中的模块内容，并且无法恢复，您确定要删除此模块吗？' , function(){
				$(".design > .cur_row , .ukefu-model-content > .cur_row").each(function(){
					$(this).remove();
				});
			} , function(){})	;			
			evt.stopPropagation();	
		});
	},
	esc:function(){
		$(document).bind('keydown', 'esc',function (evt){
			$(".cur_row").removeClass("cur_row");
			$("#rmLayout").addClass("layui-btn-disabled");
			$("#rmLayout").attr("disabled",true);
			$("#coltools").addClass("disabled");
			evt.stopPropagation();	
		});
	},
	ctra:function(){
		$(document).bind('keydown', 'ctrl+a',function (evt){
			$(".design > .ukefu-row").addClass("cur_row");

			evt.stopPropagation();	
			return false;
		});
	}
}
var DefaultHelper = {
	refresh : function(dsid , templet , mid ,  data){
		//保存完毕，需要刷新当前模块
		$.post(report.refresh+"&dsid="+dsid+"&templet="+templet+"&mid="+mid,{struct: decodeURIComponent(data)},function(data){
			$('#'+mid).empty().html(data);	
		});		
	}
}
var report = {
		save:"/apps/report/design/save.html?rid=",
		submit:"/apps/report/design/values.html?rid=",
		refresh:"/apps/report/design/refresh.html?rid="	,
		updatefilter:"/apps/report/design/updatefilter.html?rid=" ,
		designtype : "team" 
	}
var DesignHelper = {
		init:function(){
		},
		submit:function(target){
			if(!target){
				target = "#team-dataset" ;
			}
			$("#teamForm").attr("action", report.submit).attr("data-target",target).attr("data-callback", "DesignHelper.update").submit();
			hasData = true ;
		},
		update:function(div, data){
			$(div).empty().html(data) ;
			console.info("update !!!")
			//TeamHelper.refresh();
		},
}
var TeamHelper = {
	init:function(){
		//ukefu-field ukefu-tool
		$(".ukefu-team-sort").sortable({ 
			handle:".ukefu-team-sort-title",
			placeholder: "ui-state-highlight",
			stop:function(){
				TeamHelper.refresh();	
			}
		});	
	},
	submit:function(target){
		if(!target){
			target = "#team-dataset" ;
		}
		$("#teamForm").attr("action", report.submit).attr("data-target",target).attr("data-callback", "TeamHelper.update").submit();
		hasData = true ;
	},
	update:function(div, data){
		$(div).empty().html(data) ;
		TeamHelper.refresh();
	},
	refresh:function(){		
		$("#teamForm").attr("action" , report.refresh).attr("data-target","#team-chart").removeAttr("data-callback").submit();
		$('#m').val('');$("#d").val('');$("#f").val('');
	},
	save:function(target){
		$("#teamForm").attr("action" , report.save).attr("data-target",target).attr("data-callback", "TeamHelper.updateOpenner").submit();		
	},
	updateOpenner:function(div , data){
		eval(data) ;
		if(window.opener != null && window.opener.report != null){
			var msg = window.opener.setModelValue($('#mid').val() ,model , $("#dsid").val());	
			if(msg == 1){			
				alertMsg("页面保存成功！");			
				hasData= false;
			}else{
				alertMsg("页面保存失败！");	
			}
		}else{
			if(localStorage){
				localStorage.setItem($('#mid').val(), model);
				localStorage.setItem($('#mid').val()+"_dsid", $('#dsid').val());
				alertMsg("页面保存成功！");	
				hasData = false ;
			}else{
				alertMsg("页面保存失败，未找到布局页面！");	
			}
		}
	},
	changeDataset:function(div,data){
		$(div).empty().html(data);										
		DialogHelper.close();
		TeamHelper.refresh();
	},
	changeformvalue:function(target , data){
		$(target).val(data);
	},
	savedimrename:function(target , id){
		var value = $(target).val() ;
		if(value != ""){
			var jsonstr = Base64.decode($("#dim_json_"+id).val());
			var json = JSON.parse(jsonstr);
			json.title = value ;
			$('#name_'+id).text(value);
			$("#dim_json_"+id).val(Base64.encode(JSON.stringify(json))) ;
			$('#t').val("page");
			TeamHelper.refresh();
			DialogHelper.close();
		}
	},
	savebutton:function(target  , btntp, id){
		var value = $(target).val() ;
		var btntpvalue = $(btntp).val();
		if(value != ""){
			var jsonstr = Base64.decode($("#dim_json_"+id).val());
			var json = JSON.parse(jsonstr);
			json.reportid = value ;
			json.templetid = btntpvalue;
			$("#dim_json_"+id).val(Base64.encode(JSON.stringify(json))) ;
			$('#t').val("page");
			TeamHelper.refresh();
			DialogHelper.close();
		}
	},
	savemeasurerename:function(target , id){
		var value = $(target).val() ;
		if(value != ""){
			var jsonstr = Base64.decode($("#measure_json_"+id).val());
			var json = JSON.parse(jsonstr);
			json.title = value ;
			$('#name_'+id).text(value);
			$("#measure_json_"+id).val(Base64.encode(JSON.stringify(json))) ;
			$('#t').val("page");
			TeamHelper.refresh();
			DialogHelper.close();
		}
	},
	savefilterrename:function(target , codetarget , id){
		var value = $(target).val() ;
		if(value != ""){
			var jsonstr = Base64.decode($("#filter_json_"+id).val());
			var json = JSON.parse(jsonstr);
			json.title = value ;
			var codevalue = $(codetarget).val() ;
			if(codevalue != ""){
				json.code = $(codetarget).val() ;
			}
			$('#name_'+id).text(value);
			$("#filter_json_"+id).val(Base64.encode(JSON.stringify(json))) ;
			$('#t').val("page");
			$("#teamForm").attr("action" , report.updatefilter).attr("data-target","#team-filter").attr("data-callback" , "TeamHelper.updatefilter").submit();
			DialogHelper.close();
		}
	},
	changeformat:function(format , interval , id , target){
		if(id != ""){
			var jsonstr = Base64.decode($(id).val());
			var json = JSON.parse(jsonstr);
			json.format = format ;
			json.interval = interval ;
			$(id).val(Base64.encode(JSON.stringify(json))) ;
			$('#t').val("page");
			$('.dropdown-menu .format').removeClass("format");
			$(target).parent().addClass("format") ;
			TeamHelper.refresh();
		}
	},
	changeagg:function(aggregation , id , target){
		if(id != ""){
			var jsonstr = Base64.decode($(id).val());
			var json = JSON.parse(jsonstr);
			json.aggregation = aggregation ;
			$(id).val(Base64.encode(JSON.stringify(json))) ;
			$('#t').val("page");
			$('.dropdown-menu .agg').removeClass("agg");
			$(target).parent().addClass("agg") ;
			TeamHelper.refresh();
		}
	},
	changenumberformat:function(format , id , target){
		if(id != ""){
			var jsonstr = Base64.decode($(id).val());
			var json = JSON.parse(jsonstr);
			json.format = format ;
			$(id).val(Base64.encode(JSON.stringify(json))) ;
			$('#t').val("page");
			$('.dropdown-menu .number').removeClass("number");
			$(target).parent().addClass("number") ;
			TeamHelper.refresh();
		}
	},
	changeorder:function(order , id , target){
		if(id != ""){
			var jsonstr = Base64.decode($(id).val());
			var json = JSON.parse(jsonstr);
			json.order = order ;
			$(id).val(Base64.encode(JSON.stringify(json))) ;
			$('#t').val("page");
			$('.dropdown-menu .number').removeClass("number");
			$(target).parent().addClass("number") ;
			
			$("#teamForm").attr("action" , report.updatefilter).attr("data-target","#team-filter").attr("data-callback" , "TeamHelper.updatefilter").submit();
		}
	},
	changefilterformat:function(format , interval, id , target){
		if(id != ""){
			var jsonstr = Base64.decode($(id).val());
			var json = JSON.parse(jsonstr);
			json.format = format ;
			json.interval = interval;
			$(id).val(Base64.encode(JSON.stringify(json))) ;
			$('#t').val("page");
			$('.dropdown-menu .number').removeClass("number");
			$(target).parent().addClass("number") ;
			
			$("#teamForm").attr("action" , report.updatefilter).attr("data-target","#team-filter").attr("data-callback" , "TeamHelper.updatefilter").submit();
		}
	},
	changemodeltype:function(modeltype , id , target){
		if(id != ""){
			var jsonstr = Base64.decode($(id).val());
			var json = JSON.parse(jsonstr);
			json.modeltype = modeltype ;
			if(modeltype == 'select'){
				json.convalue = "auto" ;
			}
			$(id).val(Base64.encode(JSON.stringify(json))) ;
			$('#t').val("page");
			$('.dropdown-menu .number').removeClass("number");
			$(target).parent().addClass("number") ;
			$("#teamForm").attr("action" , report.updatefilter).attr("data-target","#team-filter").attr("data-callback" , "TeamHelper.updatefilter").submit();
		}
	},
	changefiltervaluetype:function(valuefiltertype , id , target){
		if(id != ""){
			var jsonstr = Base64.decode($(id).val());
			var json = JSON.parse(jsonstr);
			json.valuefiltertype = valuefiltertype ;
			$(id).val(Base64.encode(JSON.stringify(json))) ;
			$('#t').val("page");
			$('.dropdown-menu .number').removeClass("number");
			$(target).parent().addClass("number") ;
			$("#teamForm").attr("action" , report.updatefilter).attr("data-target","#team-filter").attr("data-callback" , "TeamHelper.updatefilter").submit();
		}
	},
	changefilterdisplay:function(display , id , target){
		if(id != ""){
			var jsonstr = Base64.decode($(id).val());
			var json = JSON.parse(jsonstr);
			json.display = display ;
			$(id).val(Base64.encode(JSON.stringify(json))) ;
			$('#t').val("page");
			$('.dropdown-menu .number').removeClass("number");
			$(target).parent().addClass("number") ;
			$("#teamForm").attr("action" , report.updatefilter).attr("data-target","#team-filter").attr("data-callback" , "TeamHelper.updatefilter").submit();
		}
	},
	changedefaultvaluerule:function(defaultvaluerule  , filter , type, id , target){
		if(id != ""){
			var jsonstr = Base64.decode($(id).val());
			var json = JSON.parse(jsonstr);
			json.defaultvaluerule = defaultvaluerule ;

			if(defaultvaluerule == "none"){
				json.defaultvalue = "";
				json.startvalue = "" ;
				json.endvalue = "" ;
			}else if(defaultvaluerule == "current"){
				json.defaultvaluerule = "current";
				if(type == "compare"){
					json.defaultvalue = $(filter+"_value").val();
					json.startvalue = "" ;
					json.endvalue = "" ;					
				}else{
					json.defaultvalue = "";
					json.startvalue = $(filter+"_start").val() ;
					json.endvalue = $(filter+"_end").val() ;
				}
			}

			$(id).val(Base64.encode(JSON.stringify(json))) ;
			$('#t').val("page");
			$('.dropdown-menu .number').removeClass("number");
			$(target).parent().addClass("number") ;
			$("#teamForm").attr("action" , report.updatefilter).attr("data-target","#team-filter").attr("data-callback" , "TeamHelper.updatefilter").submit();
		}
	},	
	changeabsolute:function(id , target){
		if(id != ""){
			var jsonstr = Base64.decode($(id).val());
			var json = JSON.parse(jsonstr);
			$('#t').val("page");
			$('.dropdown-menu .number').removeClass("number"); 			

			if(json.absolute == "true"){
				json.absolute = "false" ;
			}else{
				json.absolute = "true" ;
				$(target).parent().addClass("number") ;
			}
			$(id).val(Base64.encode(JSON.stringify(json))) ;
			
			$("#teamForm").attr("action" , report.updatefilter).attr("data-target","#team-filter").attr("data-callback" , "TeamHelper.updatefilter").submit();
		}
	},
	updatefilter:function(div , data){
		$('#team-filter').empty().html(data);
		TeamHelper.refresh();
		$('#m').val('');$("#d").val('');$("#f").val('');	
	}
}
var DialogHelper = {
	close : function(){
		layer.closeAll();
	}
}

function Template(id , name , type , url , desc , handle , prop){
	this.id =  id ;
	this.name = name ;
	this.type = type ;
	this.url = url ;
	this.desc = desc ;
	this.prop = prop ;
	
	this.getProp = function(){
		var propPage ;
		if(prop){
			propPage = prop ;
		}else if(type && type.prop){
			propPage = type.prop ;
		}
		return propPage ;
	}
	this.handle = function(){
		return ElementHelper.getRowHandle(id)
	}
}
function TemplateType(id , name , prop){
	this.id = id ;
	this.name = name ;
	this.prop = prop ;
}

function setModelValue(div ,data , filter , dsid){
	return IoHelper.setModelValue(div ,data , filter, dsid );
}