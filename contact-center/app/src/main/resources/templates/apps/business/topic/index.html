<link rel="stylesheet" href="/js/ztree/zTreeStyle/zTreeStyle.css">
<script src="/js/ztree/jquery.ztree.all.min.js"></script>
<div class="layui-side layui-bg-black">
	<div class="layui-side-scroll">
		<div class="row" style="height:100%;">
			<div class="col-lg-12" style="border-right:1px solid #e6e6e6;padding-right:0px;">
				<h1 class="site-h1" style="background-color:#FFFFFF;">
					知识分类
					<span style="float:right;">
						<button class="layui-btn layui-btn-small green" href="/apps/topic/type/add.html<#if curtype??>?type=${curtype.id!''}</#if>" data-toggle="ajax" data-height="360" data-width="550" data-title="新建分类">
							新建分类
						</button>
					</span>
				</h1>
				<div class="box">
					<ul id="topicTree" class="ztree"></ul>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="layui-body">
	<div class="layui-side-scroll">
		<div class="row" style="height:100%;">
			<div class="col-lg-12">
				<h1 class="site-h1" style="background-color:#FFFFFF;">
					“<#if curtype??>${curtype.name!''}<#else>默认</#if>”<#if topicList??>（${topicList.totalElements!''}）</#if>
					<div class="ukefu-bt-text-content" style="position: absolute;right: 5px;top: 0px;">
						<form class="layui-form" name="search" action="/apps/topic.html" method="get" style="float: left;">
							<#if curtype??><input type="hidden" name="type" value="${curtype.id!''}"></#if>
							<div class="layui-form-item">
								<div class="layui-inline">
									<div class="layui-input-inline">
										<input type="text" name="q" value="${q!''}" style="height: 30px;line-height: 30px;margin-top: 4px;" placeholder="请输入查询条件" autocomplete="off" class="layui-input">
										<i class="layui-icon" onclick="search.submit()" style="position: absolute;cursor: pointer;right: 3px;top: 4px;font-size: 20px;"></i>
									</div>
								</div>
							</div>
						</form>
						<#if curtype??>
						<button class="layui-btn layui-btn-small green" href="/apps/topic/type/edit.html<#if curtype??>?type=${curtype.id}</#if>" data-toggle="ajax" data-width="550" data-height="360" data-title="编辑分类">
							编辑分类
						</button>
						<button class="layui-btn layui-btn-small layui-btn-danger" href="/apps/topic/type/delete.html<#if curtype??>?id=${curtype.id!''}&type=${curtype.id}</#if>" data-toggle="tip" data-title="删除分类前，请确认目录下没有知识条目，请确认是否删除？">
							删除分类
						</button>
						<button class="layui-btn layui-btn-primary layui-btn-small" href="/apps/topic/area.html?id=${curtype.id!''}&type=${curtype.id}" data-toggle="ajax" data-width="450" data-height="400" data-title="地区设置">
							地区设置
						</button>
						</#if>
						<#if curtype??>
							<button class="layui-btn layui-btn-small green" href="/apps/topic/add.html<#if curtype??>?type=${curtype.id}</#if>" data-toggle="ajax" data-width="950" data-height="550" data-title="添加知识条目">
								新建知识
							</button>
							<div class="layui-btn-group ukefu-btn-group">
								<button class="layui-btn layui-btn-small" href="/apps/topic/imp.html?<#if curtype??>type=${curtype.id}</#if>" title="导入知识库" data-toggle="ajax" data-width="950" data-height="600">
									<i class="kfont">&#xe641;</i> 导入 
								</button>
							</div>
						</#if>
						
						<div class="layui-btn-group ukefu-btn-group">
							<button class="layui-btn layui-btn-small dropdown-menu">
								<i class="kfont">&#xe672;</i> 导出 
								<i class="layui-icon">&#xe625;</i>
							</button>
							<ul class="ukefu-dropdown-menu layui-anim layui-anim-upbit">
								<li>
									<a href="javascript:void(0)" id="batexp" title="导出知识库" target="_blank">
										<i class="kfont">&#xe672;</i>
										导出选中结果
									</a>
								</li>
								<li>
									<a href="/apps/topic/expsearch.html?1=1<#if q??>&q=${q!''}</#if><#if curtype??>&type=${curtype.id}</#if>&subtype=${subtype}" title="导出知识库" target="_blank">
										<i class="kfont">&#xe672;</i>
										导出当前搜索结果
									</a>
								</li>
								<li>
									<a href="/apps/topic/expall.html?<#if curtype??>type=${curtype.id}</#if>" title="导出联系人" target="_blank">
										<i class="kfont">&#xe672;</i>
										导出全部数据
									</a>
								</li>
								<li>
									<a href="javascript:void(0)" id="batdel" title="删除知识条目" target="_blank">
										<i class="kfont">&#xe672;</i>
										删除选中结果
									</a>
								</li>
							</ul>
						</div>
					</div>
				</h1>
				<div class="row" style="padding:5px;">
					<div class="col-lg-12">
						<table class="layui-table" lay-skin="line">
						  <colgroup>
						  	    <col width="1%">
								<col width="58%">
								<col width="15%">
								<col width="15%">
								<col width="10%">
								<col width="1%">
							<col>
						  </colgroup>
						  <thead>
							<tr>
							  <th style="width:1%;padding-right:0px;"></th>
							  <th>标题</th>
							  <th>有效期开始</th>
							  <th>有效期结束</th>
							  <th>热门</th>
							  <th style="white-space:nowrap;" nowrap="nowrap">操作</th>
							</tr> 
						  </thead>
						  <tbody>
						  	<#if topicList?? && topicList.content??>
						  	<#list topicList.content as topic>
							<tr>
							  <td style="width:1%;padding-right:0px;">
								<input type="checkbox" class="ids" name="ids" value="${topic.id!''}"/>
							  </td>
							  <td class="ukefu_topic_title">
									${(topic.title!'')?no_esc}
							  </td>
							  <td><#if topic.begintime??>${topic.begintime?string('yyyy-MM-dd')}</#if></td>
							  <td><#if topic.endtime??>${topic.endtime?string('yyyy-MM-dd')}</#if></td>
							  <td>
							  	<#if topic.top>
							  		<i class="layui-icon" style="color:#19a55d;">&#xe618;</i>
							  	</#if>
							  </td>
							  <td style="white-space: nowrap;">
							  		<a href="/apps/topic/edit.html?id=${topic.id!}<#if curtype??>&type=${curtype.id}</#if>" data-toggle="ajax" data-title="编辑知识条目" data-width="950" data-height="600">
							  			<i class="layui-icon">&#xe642;</i>
							  			编辑
							  		</a>
							  		<a href="/apps/topic/delete.html?id=${topic.id!''}<#if curtype??>&type=${curtype.id}</#if>" data-toggle="tip" data-title="请确认是否删除知识条目？">
							  			<i class="layui-icon" style="color:red;">ဆ</i>
							  			移除
							  		</a>
							  </td>
							</tr>
							</#list>
							</#if>
						  </tbody>
						</table>
					</div> 	   
				</div>
				<div class="row" style="padding:5px;">
					<div class="col-lg-12" id="page" style="text-align:center;"></div>
				</div>
			</div>	
		</div>
	</div>
</div>
<script>
	<#if msg?? &&  msg =="notempty">
	top.layer.alert("删除目录前先删除目录下的知识，清空后再试！");
	<#elseif msg?? && msg == 'k_type_exist'>
	top.layer.alert('分类名称重复，请重新填写', {icon: 2});
	</#if>
	layui.use(['laypage', 'layer'], function(){
		  var laypage = layui.laypage
		  ,layer = layui.layer;
		  
		  laypage({
				cont: 'page'
				,pages: <#if topicList??>${topicList.totalPages}<#else>0</#if> //总页数
				,curr:<#if topicList??>${topicList.number+1}<#else>0</#if>
				,groups: 5 //连续显示分页数
				,jump:function(data , first){
					if(!first){
						location.href = "/apps/topic.html?<#if q??>q=${q?url}&</#if><#if curtype??>type=${curtype.id}&</#if>p="+data.curr ;
					}
				}
		   });
		});
	$('#batexp').click(function(){
		var ids = "";
		var num = 0 ;
		$('.ids').each(function(){
			if($(this).prop("checked")){
				if(ids!=""){
					ids += "&" ;
				}
				ids += "ids=" + $(this).val();
				num++;
			}
		});
		if(num>0){
			$('#batexp').attr('href' , '/apps/topic/expids.html?'+ids)	;
		}else{
			top.layer.alert("请先选择需要导出的知识条目");
			$('#batexp').attr("href" , "javascript:void(0)");
		}
		
		
	});
	$('#batdel').click(function(){
		var ids = "";
		var num = 0 ;
		$('.ids').each(function(){
			if($(this).prop("checked")){
				if(ids!=""){
					ids += "&" ;
				}
				ids += "ids=" + $(this).val();
				num++;
			}
		});
		if(num>0){
			top.layer.confirm("请确认是否删除知识条目？", {icon: 3, title:'提示'}, function(index){
				top.layer.close(index);
				if(confirm){
					location.href = '/apps/topic/batdelete.html?<#if curtype??>type=${curtype.id!''}&</#if>'+ids ;
				}
			});
		}else{
			top.layer.alert("请先选择需要删除的知识条目");
			$('#batexp').attr("href" , "javascript:void(0)");
		}
		
		
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
		if(treeNode.topic){
			location.href = "/apps/topic.html?type="+treeNode.id;
		}else if(treeNode.id == "0"){
			location.href = "/apps/topic.html";
		}
	}

	var zNodes =[
	    { id:'0', pId:'00', name:"公共分类", open:true , topic : false}
	    <#if knowledgeTypeList??>
	    <#list knowledgeTypeList as topicType>
	    ,{ id:'${topicType.id!''}', pId:'${topicType.parentid!''}', name:"${topicType.name!''}", open:true , topic : true <#if topicType.area?? && topicType.area != "">,icon:"/images/area.png" </#if> }
	    </#list>
	    </#if>
	];
	
	$(document).ready(function(){
		var ztree = $.fn.zTree.init($("#topicTree"), setting, zNodes);
		<#if curtype??>
		var node = ztree.getNodeByParam('id', '${curtype.id!''}');//获取id为1的点  
		ztree.selectNode(node);//选择点  
		</#if>
	});
</SCRIPT>