<!-- <#if (quickReplyList?? && quickReplyList?size gt 0) || (pubQuickTypeList?? && pubQuickTypeList?size gt 0)>
<ul class="ztree" id="quickTypeTree"></ul>
<#else>
	<div style="padding: 50px 0 50px;">
		<div class="ukefu-empty">
			<i class="layui-icon"></i>
			<div style="">还没有快捷回复</div>
		</div>
	</div>
</#if> -->
<ul class="ztree" id="quickTypeTree"></ul>
<SCRIPT type="text/javascript">
	var setting = {
		data: {
			simpleData: {
				enable: true
			}
		},
		view: {
			showLine: false,
			fontCss: getFontCss
		},
		callback: {
			onDblClick: onDblClick,
			onClick: onClick
		}
	};
	function getFontCss(treeId, treeNode) {
		return (!!treeNode.highlight) ? {color:"red", "font-weight":"400"} : {color:"#333", "font-weight":"normal"};
	}
	function onClick(e, treeId, treeNode) {
		if(editor != null && treeNode.pri == true){
			updateTarget(treeNode.id ,treeNode.type ) ;
			
			$('#pri_btn_group').show();
		}else{
			$('#pri_btn_group').hide();
		}
	}
	
	function updateTarget(id , type){
		$('#quick_type_add').attr("href","/agent/quickreply/addtype.html?typeid="+id) ;
		$('#quick_type_edit').attr("href","/agent/quickreply/edittype.html?id="+id) ;
		$('#quick_add').attr("href","/agent/quickreply/add.html?parentid="+id) ;
		$('#quick_edit').attr("href","/agent/quickreply/edit.html?id="+id) ;
		
		if(id == "1"){
			$('#quick_type_edit').hide();
			$('#quick_edit').hide();
		}else{
			if(type == true){
				$('#quick_type_edit').show();
				$('#quick_edit').hide();
			}else{
				$('#quick_type_edit').hide();
				$('#quick_edit').show();
			}
		}
	}
	
	function onDblClick(e, treeId, treeNode) {
		if(editor != null && treeNode.type == false){
			var quickReply = treeNode.name ;
			if(editor.count("text") == 0 ){
				getQuickReplyContent( treeNode.id,editor)
			}else{
				top.layer.confirm("您已经输入了内容，覆盖当前输入框中内容？", {icon: 3, title:'覆盖提示'}, function(index){
					top.layer.close(index);
					getQuickReplyContent( treeNode.id,editor)
				});
			}
		}else if(editor != null && treeNode.pri == true){
			$('#pri_btn_group').show();
		}else{
			$('#pri_btn_group').hide();
		}
	}

	function getQuickReplyContent(id , editor){
		loadURL("/agent//quickreply/content.html?id="+id , null , function(data){
			editor.html(data);
		})
	}

	var zNodes =[
	    { id:'1', pId:'00', name:"个人分类", open:true  , pri:true}
	    ,{ id:'0', pId:'00', name:"公共分类", open:true  , pri:false}
	    <#if pubQuickTypeList??>
	    <#list pubQuickTypeList as pubQuckType>
	    ,{ id:'${pubQuckType.id!''}', pId:'${pubQuckType.parentid!''}', name:"${pubQuckType.name!''}", open:true , type : true , pri:<#if pubQuckType.quicktype == "pri">true<#else>false</#if>}
	    <#if quickReplyList??>
		    <#list quickReplyList as quickReply>
		    <#if quickReply.cate?? && (quickReply.cate == pubQuckType.id)>
		    ,{ id:'${quickReply.id!''}', pId:'${quickReply.cate!''}', name:"${(quickReply.title!'')?replace('\r\n','<br/>')?no_esc}", open:true ,  icon:"/images/quickreply.png" , type : false  , pri:<#if pubQuckType.quicktype == "pri">true<#else>false</#if>}
		    </#if>
		    </#list>
	    </#if>
	    </#list>
	    </#if>
	    <#if quickReplyList??>
		    <#list quickReplyList as quickReply>
		    <#if quickReply.cate?? && quickReply.cate == '1'>
		    ,{ id:'${quickReply.id!''}', pId:'${quickReply.cate!''}', name:"${(quickReply.title!'')?replace('\r\n','<br/>')?no_esc}", open:true ,  icon:"/images/quickreply.png" , type : false  , pri:true}
		    </#if>
		    </#list>
	    </#if>
	];
	var ztree ;
	$(document).ready(function(){
		ztree = $.fn.zTree.init($("#quickTypeTree"), setting, zNodes);
		<#if quickType??>
		var node = ztree.getNodeByParam('id', '${quickType.id!''}');//获取id为1的点  
		ztree.selectNode(node);//选择点  
		updateTarget("${quickType.id!''}" , node.type) ;
		</#if>
	});
	function search(value){
		if(ztree!=null){
			var lastNodeList = ztree.getNodesByParamFuzzy("highlight", "true");
			for( var i=0, l=lastNodeList.length; i<l; i++) {
				updateNodes(lastNodeList[i] , false);
			}
			var nodeList = ztree.getNodesByParamFuzzy("name", value);
			for( var i=0, l=nodeList.length; i<l; i++) {
				if(value != ""){
					updateNodes(nodeList[i] , true);
				}else{
					updateNodes(nodeList[i] , false);
				}
			}
			
		}
	}
	function updateNodes(node , highlight) {
		if(ztree!=null){
			if(node.type == true){
				node.highlight = false;
				ztree.updateNode(node);
			}else{
				node.highlight = highlight;
				ztree.updateNode(node);
			}
		}
	}
	
</SCRIPT>