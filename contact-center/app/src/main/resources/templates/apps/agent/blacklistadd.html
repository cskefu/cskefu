<div class="uk-layui-form">
	<form class="layui-form" action="/agent/blacklist/save.html" method="post">
		<input type="hidden" name="agentuserid" value="${agentuserid!''}">
		<input type="hidden" name="agentserviceid" value="${agentserviceid!''}">
		<input type="hidden" name="userid" value="${userid!''}">
		
		<div class="layui-collapse">
			<div class="layui-colla-item">
				<h2 class="layui-colla-title">黑名单信息</h2>
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label" id="cusname">拉黑方式：</label>
							<div class="layui-input-inline" style="margin-top:9px;">
								访客ID（${userid!''}）
							</div>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label" id="cusname">时长：</label>
							<div class="layui-input-inline ukefu-limit-height">
								<select name="controltime">
									<#list 1..24 as inx>
									<option value="${inx}">${inx}小时</option>
									</#list>
									<#list 1..30 as inx>
									<option value="${inx*24}">${inx}天</option>
									</#list>
									<#list 1..12 as inx>
									<option value="${inx*24*30}">${inx}月</option>
									</#list>
									<option value="0">永久</option>
								</select>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="layui-colla-item">
				<h2 class="layui-colla-title">扩展信息</h2>
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">拉黑原因：</label>
							<div class="layui-input-inline" style="width: 664px;">
								<textarea name="description" placeholder="拉黑原因" class="layui-textarea" style="resize:none;" maxlength="255"></textarea>
							</div>
						</div>
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
layui.use('form', function() {
	var form = layui.form();
	form.render(); //更新全部
});
layui.use('element', function(){
	var element = layui.element();
	element.init();
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
		$("#parentname").attr("value", v);
		$("#parentid").attr("value", value);
		hideMenu();
	}


	var zNodes =[
	             { id:'0', pId:'0', name:"问答分类", open:true }
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
