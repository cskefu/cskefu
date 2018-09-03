<div class="uk-layui-form">
    <form class="layui-form" method="post" action="/apps/report/update.html">
    	<input type="hidden" name="id" value="${report.id!''}">
        <div class="layui-form-item" style="margin-top:20px;">
            <div class="layui-inline">
                <label class="layui-form-label">标题：</label>
                <div class="layui-input-inline">
                    <input type="text" maxlength="200" name="name" required value="${report.name!''}" lay-verify="required" placeholder="请输入标题" autocomplete="off" class="layui-input">
                </div>
            </div>
        </div>
        <div class="layui-form-item" style="margin-top:20px;">
            <div class="layui-inline">
                <label class="layui-form-label">代码：</label>
                <div class="layui-input-inline">
                    <input type="text" maxlength="200" name="code" required value="${report.code!''}" lay-verify="required" placeholder="请输入代码" autocomplete="off" class="layui-input">
                </div>
            </div>
        </div>
        <div class="layui-form-item">
            <div class="layui-inline">
                <label class="layui-form-label" id="cusname">分类：</label>
                <div class="layui-input-inline">
                    <input type="hidden" id="dicid" name="dicid" value="<#if dataDic??>${dataDic.id!''}<#else>0</#if>">
                    <input type="text" name="dicname" id="dicname" required placeholder="报表分类" value="<#if dataDic??>${dataDic.name!''}<#else>报表分类</#if>" lay-verify="required" autocomplete="off" readOnly="readonly" onClick="showMenu();" class="layui-input">
                    <i class="layui-icon" style="position: absolute;right: 3px;top: 6px;font-size: 25px;${systemConfig.color!'color:#0096C4;'}" onClick="showMenu()">&#xe631;</i>
                    <div id="menuContent" style="display:none; position: absolute;z-index:10000;" class="ukefu-ztree">
                        <ul id="dataDicTree" class="ztree"></ul>
                    </div>
                </div>
            </div>
        </div>
        <div class="layui-form-item" style="margin-top:20px;">
            <label class="layui-form-label">备注：</label>
            <div class="layui-input-inline">
                <textarea name="description"  maxlength="500" id="description" autocomplete="off" lay-verify="required" required class="layui-input" resize="false" placeholder="请输入内容" style="height:150px;line-height:22px;resize: none;">${report.description!''}</textarea></div>
        </div>
        <div class="layui-form-button">
            <div class="layui-button-block">
                <button class="layui-btn" lay-submit lay-filter="formDemo">立即提交</button>
                <button type="reset" class="layui-btn layui-btn-primary">重置</button></div>
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
		var zTree = $.fn.zTree.getZTreeObj("dataDicTree"),
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
		$("#dicname").attr("value", v);
		$("#dicid").attr("value", value);
		hideMenu();
	}


	var zNodes =[
	             { id:'0', pId:'0', name:"报表分类", open:true }
	             <#if dataDicList??>
	             <#list dataDicList as dataDic>
	             ,{ id:'${dataDic.id!''}', pId:'${dataDic.parentid!''}', name:"${dataDic.name!''}", open:true }
	             </#list>
	             </#if>
	         ];
	$(document).ready(function(){
		var ztree = $.fn.zTree.init($("#dataDicTree"), setting, zNodes);
		<#if dataDic??>
		var node = ztree.getNodeByParam('id', '${dataDic.id!''}');//获取id为1的点  
		ztree.selectNode(node);//选择点  
		<#else>
		var node = ztree.getNodeByParam('id', '0');//获取id为1的点  
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