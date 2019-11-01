<div class="row">
	<div class="col-lg-12">
		<h1 class="site-h1" style="background-color:#FFFFFF;">
			系统数据字典列表<#if sysDicList??>（${sysDicList.totalElements!''}）</#if>
			<span style="float:right;">
				<button class="layui-btn layui-btn-small green" href="/admin/sysdic/add.html" data-toggle="ajax" data-width="550" data-title="创建新字典项">
					创建新字典项
				</button>
			</span>
		</h1>
		<div class="row" style="padding:5px;">
			<div class="col-lg-12">
				<table class="layui-table" lay-skin="line">
				  <colgroup>
						<col width="15%">
						<col width="15%">
						<col width="15%">
						<col width="20%">
						<col width="20%">
						<col width="13%">
					<col>
				  </colgroup>
				  <thead>
					<tr>
					  <th>名称</th>
					  <th>代码</th>
					  <th>系统字典</th>
					  <th>说明</th>
					  <th>创建时间</th>
					  <th style="white-space:nowrap;" nowrap="nowrap">操作</th>
					</tr> 
				  </thead>
				  <tbody>
					<#if sysDicList?? && sysDicList.content??>
					<#list sysDicList.content as dic>
					<tr>
					  <td>
							<#if dic.haschild?? && dic.haschild>
							<a href="/admin/sysdic/dicitem.html?id=${dic.id!''}&dicid=${dic.id!''}">${dic.name!''}</a>
							<#else>
							${dic.name!''}
							</#if>
							
					  </td>
					  <td>${dic.code!''}</td>
					  <td><#if dic.ctype?? && dic.ctype == "data">数据字典<#elseif dic.ctype?? && dic.ctype == "auth">权限字典<#elseif dic.ctype?? && dic.ctype == "resu">系统资源</#if></td>
					  <td>${dic.description!''}</td>
					  <td><#if dic.createtime??>${dic.createtime?string("yyyy-MM-dd HH:mm:ss")}</#if></td>
					  <td style="white-space:nowrap;" nowrap="nowrap">
							<a href="/admin/sysdic/edit.html?id=${dic.id!''}&p=${sysDicList.number+1}" data-width="550" data-toggle="ajax" title="编辑${dic.name!''}">
								<i class="layui-icon">&#xe642;</i>
								编辑
							</a>
							<a href="/admin/sysdic/delete.html?id=${dic.id!''}&p=${sysDicList.number+1}" data-toggle="tip" data-title="删除字典项同时会删除字典项下的所有子项，请确认是否删除字典项“${dic.name!''}”？" title="删除${dic.name!''}">
								<i class="layui-icon" style="color:red;">&#x1006;</i>
								删除
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
<script>
	layui.use(['laypage', 'layer'], function(){
		  var laypage = layui.laypage
		  ,layer = layui.layer;
		  <#if msg??>
		  top.layer.msg('${msg}');
		  </#if>
		  laypage({
				cont: 'page'
				,pages: <#if sysDicList??>${sysDicList.totalPages}<#else>0</#if> //总页数
				,curr:<#if sysDicList??>${sysDicList.number+1}<#else>0</#if>
				,groups: 5 //连续显示分页数
				,jump:function(data , first){
					if(!first){
						location.href = "/admin/sysdic/index.html?p="+data.curr ;
					}
				}
		   });
		});
</script>