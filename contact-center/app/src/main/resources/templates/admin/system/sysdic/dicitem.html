<div class="row">
	<div class="col-lg-12">
		<h1 class="site-h1" style="background-color:#FFFFFF;">
			${sysDic.name} 字典项<#if sysDicList??>（${sysDicList.totalElements!''}）</#if>
			<span style="float:right;">
				<button class="layui-btn layui-btn-small green" href="/admin/sysdic/dicitem/add.html?id=${sysDic.id!''}" data-toggle="ajax" data-width="550" data-height="450" data-title="创建新字典项">
					创建新字典项
				</button>
				
				<button class="layui-btn layui-btn-small green" href="/admin/sysdic/dicitem/batadd.html?id=${sysDic.id!''}" data-toggle="ajax" data-width="750" data-title="创建新字典项">
					批量创建字典项
				</button>
			</span>
		</h1>
		<div class="row" style="padding:5px;">
			<div class="col-lg-12">
				<table class="layui-table" lay-skin="line">
				  <colgroup>
						<col width="20%">
						<col width="10%">
						<col width="10%">
						<col width="10%">
						<col width="35%">
						<col width="10%">
					<col>
				  </colgroup>
				  <#if sysDic?? && sysDic.ctype?? && sysDic.ctype == "auth">
					  <thead>
						<tr>
						  <th>名称</th>
						  <th>代码</th>
						  <th>URL</th>
						  <th>菜单级别</th>
						  <th>样式</th>
						  <th style="white-space:nowrap;" nowrap="nowrap">操作</th>
						</tr> 
					  </thead>
					  <tbody>
						<#if sysDicList?? && sysDicList.content??>
						<#list sysDicList.content as dic>
						<tr>
						  <td>
							  <a href="/admin/sysdic/dicitem.html?id=${dic.id!''}&dicid=${dic.id!''}">${dic.name!''}</a>
						  </td>
						  <td>${dic.code!''}</td>
						  <td>${dic.url!''}</td>
						  <td>
						  	<#if dic.level?? && dic.level == "1">一级菜单</#if>
						  	<#if dic.level?? && dic.level == "2">二级菜单</#if>
						  	<#if dic.level?? && dic.level == "3">三级菜单</#if>
						  	<#if dic.level?? && dic.level == "4">按钮或链接</#if>
						  </td>
						  <td><#if dic.iconstr??>${dic.iconstr}</#if></td>
						  <td style="white-space:nowrap;" nowrap="nowrap">
								<a href="/admin/sysdic/dicitem/edit.html?id=${dic.id!''}&p=${sysDicList.number+1}" data-width="550" data-height="450" data-toggle="ajax" title="编辑${dic.name!''}">
									<i class="layui-icon">&#xe642;</i>
									编辑
								</a>
								<a href="/admin/sysdic/dicitem/delete.html?id=${dic.id!''}&p=${sysDicList.number+1}" data-toggle="tip" data-title="删除字典项同时会删除字典项下的所有子项，请确认是否删除字典项“${dic.name!''}”？" title="删除${dic.name!''}">
									<i class="layui-icon" style="color:red;">&#x1006;</i>
									删除
								</a>
						  </td>
						</tr>
						</#list>
						</#if>
					  </tbody>
				  <#else>
					  <thead>
						<tr>
						  <th>名称</th>
						  <th>代码</th>
						  <th>图标</th>
						  <th>显示代码</th>
						  <th>创建时间</th>
						  <th style="white-space:nowrap;" nowrap="nowrap">操作</th>
						</tr> 
					  </thead>
					  <tbody>
						<#if sysDicList?? && sysDicList.content??>
						<#list sysDicList.content as dic>
						<tr>
						  <td>
							  <a href="/admin/sysdic/dicitem.html?id=${dic.id!''}&dicid=${dic.id!''}">${dic.name!''}</a>
						  </td>
						  <td>${dic.code!''}</td>
						  <td><#if dic.iconskin?? && dic.iconskin != ''>
						  		<small class="ukefu-label" style="background-color:${dic.iconskin!''};">
						  			<#if dic.iconstr?? && dic.iconstr != "">
						  			<i class="${dic.ctype!''}">${dic.iconstr?no_esc}</i>
						  			</#if>
						  			${dic.name!''}
						  		</small>
						  	  </#if>
						  </td>
						  <td><#if dic.discode><i class="layui-icon" style="color:#19a55d;">&#xe618;</i></#if></td>
						  <td><#if dic.createtime??>${dic.createtime?string("yyyy-MM-dd HH:mm:ss")}</#if></td>
						  <td style="white-space:nowrap;" nowrap="nowrap">
								<a href="/admin/sysdic/dicitem/edit.html?id=${dic.id!''}&p=${sysDicList.number+1}" data-width="550" data-height="450" data-toggle="ajax" title="编辑${dic.name!''}">
									<i class="layui-icon">&#xe642;</i>
									编辑
								</a>
								<a href="/admin/sysdic/dicitem/delete.html?id=${dic.id!''}&p=${sysDicList.number+1}" data-toggle="tip" data-title="删除字典项同时会删除字典项下的所有子项，请确认是否删除字典项“${dic.name!''}”？" title="删除${dic.name!''}">
									<i class="layui-icon" style="color:red;">&#x1006;</i>
									删除
								</a>
						  </td>
						</tr>
						</#list>
						</#if>
					  </tbody>
				  </#if>
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
						location.href = "/admin/sysdic/dicitem.html?id=${sysDic.id!''}&p="+data.curr ;
					}
				}
		   });
		});
</script>