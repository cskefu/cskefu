<div class="uk-layui-form">
	<form class="layui-form uk-form" action="/apps/topic/type/update.html">
		<input type="hidden" name="id" value="${knowledgeType.id!''}">
		<div class="layui-form-item">
			<label class="layui-form-label">分类名称：</label>
			<div class="layui-input-inline">
				<input type="text" name="name" required lay-verify="required" value="${knowledgeType.name!''}"
					placeholder="请输入分类名称" autocomplete="off" class="layui-input">
			</div>
		</div>
		<div class="layui-form-item">
			<div class="layui-inline">
				<label class="layui-form-label">上级分类：</label>
				<div class="layui-input-inline" style="position: relative;">
					<input type="hidden" id="parentid" name="parentid" value="<#if knowledgeType?? && knowledgeType.parentid??>${knowledgeType.parentid}<#else>0</#if>">
					<input type="text" name="parentname" id="parentname" required value="<#if knowledgeType.parentid?? && knowledgeType.parentid == '0'>公共分类<#else><#if knowledgeTypeList??><#list knowledgeTypeList as kt><#if knowledgeType.parentid == kt.id>${kt.name!''}</#if></#list></#if></#if>" lay-verify="required" autocomplete="off" readOnly="readonly" onClick="showMenu();"
						class="layui-input">
					<i class="layui-icon" style="position: absolute;right: 3px;top: 6px;font-size: 25px;${systemConfig.color!'color:#0096C4;'}" onClick="showMenu()">&#xe631;</i>
					<div id="menuContent" style="display:none; position: absolute;z-index:10000;width: 218px;" class="ukefu-ztree">
						<ul id="topicTypeTree" class="ztree" style="width:208px;"></ul>
					</div>
				</div>
			</div>
		</div>
		<div class="layui-form-button">
			<div class="layui-button-block">
				<button class="layui-btn" lay-submit lay-filter="formDemo">立即提交</button>
				<button type="reset" class="layui-btn layui-btn-primary">重置</button>
			</div>
		</div>
	</form>
</div>
 
<script>
//Demo
layui.use('form', function(){
  var form = layui.form();
  form.render(); //更新全部
});
</script>
<SCRIPT type="text/javascript">
	var setting = {
		data: {
			simpleData: {
				enable: true
			}
		},
		callback: {
			onClick: onClick
		}
	};
	
	function onClick(e, treeId, treeNode) {
		var zTree = $.fn.zTree.getZTreeObj("topicTypeTree"),
		nodes = zTree.getSelectedNodes(),
		v = "" , value = "";
		nodes.sort(function compare(a,b){return a.id-b.id;});
		for (var i=0, l=nodes.length; i<l; i++) {
			v += nodes[i].name + ",";
			if(value != ""){
				value = value + "," ;
			}
			value = nodes[i].id ;
		}
		if (v.length > 0 ) v = v.substring(0, v.length-1);
		$("#parentname").attr("value", v);
		$("#parentid").attr("value", value);
		hideMenu();
	}


	var zNodes =[
	    { id:'0', pId:'00', name:"公共分类", open:true , topic : false}
	    <#if knowledgeTypeList??>
	    <#list knowledgeTypeList as topicType>
	    <#if topicType.id != knowledgeType.id>
	    ,{ id:'${topicType.id!''}', pId:'${topicType.parentid!''}', name:"${topicType.name!''}", open:true , topic : true }
	    </#if>
	    </#list>
	    </#if>
	];
	$(document).ready(function(){
		var ztree = $.fn.zTree.init($("#topicTypeTree"), setting, zNodes);
		<#if knowledgeType??>
		var node = ztree.getNodeByParam('id', '${knowledgeType.parentid!''}');//获取id为1的点  
		ztree.selectNode(node);//选择点  
		</#if>
	});
	function showMenu() {
		$("#menuContent").css({left:"0px", top:"38px"}).show();

		$("body").bind("mousedown", onBodyDown);
	}
	function hideMenu() {
		$("#menuContent").hide();
		$("body").unbind("mousedown", onBodyDown);
	}
	function onBodyDown(event) {
		if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length>0)) {
			hideMenu();
		}
	}

</SCRIPT>