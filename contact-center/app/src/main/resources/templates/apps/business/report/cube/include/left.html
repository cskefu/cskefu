<link rel="stylesheet" href="/js/ztree/zTreeStyle/zTreeStyle.css">
<script src="/js/ztree/jquery.ztree.all.min.js"></script>

<div class="layui-side layui-bg-black layui-layout-content ukefu-tab">
	<div class="row">
		<div class="col-lg-12">
			<h1 class="site-h1">
				模型分类
				<span style="float:right;">
					<button class="layui-btn layui-btn-small green" id="addtypebtn"  href="/apps/report/cube/type/add.html" data-toggle="ajax" data-height="450" data-width="950" data-title="创建分类">
						<i class="layui-icon">&#xe608;</i>
						创建分类
					</button>
				</span>
			</h1>
			<div class="box">
				<ul class="ztree" id="cubeTypeTree"></ul>
			</div>
		</div>
	</div>
</div>

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
		$("#addtypebtn").attr("href",'/apps/report/cube/type/add.html?typeid='+treeNode.id);
		loadURLWithTip("/apps/report/cube/list.html?typeid="+treeNode.id,"#ukefu-cube-content" , null , null , true);
	}

	var zNodes =[
	    { id:'0', pId:'0', name:"模型分类", open:true }
	    <#if pubCubeTypeList??>
	    <#list pubCubeTypeList as pubCubeType>
	    ,{ id:'${pubCubeType.id!''}', pId:'${pubCubeType.parentid!''}', name:"${pubCubeType.name!''}", open:true }
	    </#list>
	    </#if>
	];
	
	$(document).ready(function(){
		var ztree = $.fn.zTree.init($("#cubeTypeTree"), setting, zNodes);
		<#if cubeType??>
		var node = ztree.getNodeByParam('id', '${cubeType.id!''}');//获取id为1的点  
		ztree.selectNode(node);//选择点  
		</#if>
		layui.use('layer', function(){
			var layer = layui.layer;
			<#if msg?? && msg == 'ct_type_exist'>
			layer.alert('分类名称重复，请重新填写', {icon: 2});
		  </#if>
		});
	});
</SCRIPT>