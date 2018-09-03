<link rel="stylesheet" href="/js/ztree/zTreeStyle/zTreeStyle.css">
<script src="/js/ztree/jquery.ztree.all.min.js"></script>

<div class="layui-side layui-bg-black layui-layout-content ukefu-tab">
	<div class="row">
		<div class="col-lg-12">
			<h1 class="site-h1">
				模型分类
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
		loadURLWithTip("/apps/report/cube/pbcubelist.html?typeid="+treeNode.id,"#ukefu-cube-content" , null , null , true);
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
	});
</SCRIPT>