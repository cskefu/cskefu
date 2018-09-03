<div class="uk-layui-form">
    <form class="layui-form uk-form" method="post" action="/agent/quickreply/save.html" data-toggle="ajax-form" method="post" data-inner="#quick_reply">
        <div class="layui-form-item">
            <div class="layui-inline">
                <label class="layui-form-label">标题：</label>
                <div class="layui-input-inline" style="width:520px;">
                    <input type="text" name="title" maxlength="200" required value="${quickreply.title!''}" lay-verify="required" placeholder="请输入标题" autocomplete="off" class="layui-input">
                </div>
            </div>
        </div>
        <div class="layui-form-item">
            <div class="layui-inline">
                <label class="layui-form-label" id="cusname">分类：</label>
                <div class="layui-input-inline" style="width:520px;">
                    <input type="hidden" id="cate" name="cate" value="<#if quickType??>${quickType.id!''}<#else>0</#if>">
                    <input type="text" name="catename" id="catename" required value="<#if quickType??><#if quickTypeList??><#list quickTypeList as qt><#if quickType.id == qt.id>${qt.name!''}</#if></#list></#if></#if>" lay-verify="required" autocomplete="off" readOnly="readonly" onClick="showMenu();" class="layui-input">
                    <i class="layui-icon" style="position: absolute;right: 3px;top: 6px;font-size: 25px;${systemConfig.color!'color:#0096C4;'}" onClick="showMenu()">&#xe631;</i>
                    <div id="menuContent" style="display:none; position: absolute;z-index:10000;" class="ukefu-ztree">
                        <ul id="quickTypeTree" class="ztree"></ul>
                    </div>
                </div>
            </div>
        </div>
        <div class="layui-form-item" style="margin-top:20px;">
            <label class="layui-form-label">内容：</label>
            <div class="layui-input-inline" style="width:520px;">
                <textarea name="content" id="content" autocomplete="off" maxlength="500" lay-verify="required" required class="layui-input" resize="false" placeholder="请输入内容" style="height:150px;line-height:22px;resize: none;">${quickreply.content!''}</textarea></div>
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
		var zTree = $.fn.zTree.getZTreeObj("quickTypeTree"),
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
		$("#catename").attr("value", v);
		$("#cate").attr("value", value);
		hideMenu();
	}


	var zNodes =[
	             { id:'0', pId:'0', name:"", open:true }
	             <#if quickTypeList??>
	             <#list quickTypeList as quickType>
	             ,{ id:'${quickType.id!''}', pId:'${quickType.parentid!''}', name:"${quickType.name!''}", open:false }
	             </#list>
	             </#if>
	         ];
	$(document).ready(function(){
		$.fn.zTree.init($("#quickTypeTree"), setting, zNodes);
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