<div class="row" style="height:100%;">
	<div class="col-lg-12" style="border-right:1px solid #e6e6e6;padding-right:0px;">
		<h1 class="site-h1" style="background-color:#FFFFFF;">
			ACL访问列表策略<#if aclList??>（${aclList?size}）</#if>
			<span style="float:right;">
				<button class="layui-btn layui-btn-small green" href="/admin/callcenter/acl/add.html?hostid=${pbxHost.id!''}" data-toggle="ajax" data-width="750" data-title="添加ACL策略">
					添加ACL策略
				</button>
			</span>
		</h1>
		<div class="row">
			<div class="col-lg-12">
				<div class="row" style="padding:5px;">
					<div class="col-lg-12">
						<table class="layui-table" lay-skin="line">
						  <colgroup>
								<col width="50%">
								<col width="10%">
								<col width="20%">
								<col width="1%">
								<col>
						  </colgroup>
						  <thead>
							<tr>
							  <th>名称</th>
							  <th>默认策略</th>
							  <th>创建时间</th>
							  <th style="white-space:nowrap;" nowrap="nowrap">操作</th>
							</tr> 
						  </thead>
						  <tbody>
							<#if aclList??>
							<#list aclList as acl>
							<tr>
							  <td>
								  <a href="/admin/callcenter/resource.html?hostid=${pbxHost.id!''}">${acl.name!''}</a>
							  </td>
							  <td>${acl.defaultvalue!''}</td>
							  
							  <td>${acl.createtime?string("yyyy-MM-dd HH:mm:ss")}</td>
							  <td style="white-space:nowrap;" nowrap="nowrap">
							  		<a href="/admin/callcenter/acl/edit.html?id=${acl.id!''}&hostid=${pbxHost.id!''}" data-toggle="ajax" data-width="750" data-title="编辑ACL">
							  			<i class="layui-icon">&#xe642;</i>
							  			编辑
							  		</a>
							  		<a href="/admin/callcenter/acl/delete.html?id=${acl.id!''}&hostid=${pbxHost.id!''}" data-callback="loadURL" data-target="#callCenterContent" style="margin-left:10px;" data-toggle="tip" data-title="请确认是否删除ACL策略？">
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
		  
		  laypage({
				cont: 'page'
				,pages: <#if userList??>${userList.totalPages}<#else>0</#if> //总页数
				,curr:<#if userList??>${userList.number+1}<#else>0</#if>
				,groups: 5 //连续显示分页数
				,jump:function(data , first){
					if(!first){
						location.href = "/admin/user/index.html?p="+data.curr ;
					}
				}
		   });
		});
</script>
