<div class="row">
	<div class="col-lg-12">
		<h1 class="site-h1" style="background-color:#FFFFFF;">
			元数据列表<#if sysDicList??>（${metadataList.totalElements!''}）</#if>
			<span style="float:right;">
				<button class="layui-btn layui-btn-small green" href="/admin/metadata/imptb.html" data-toggle="ajax" data-width="550" data-title="创建新字典项">
					导入元数据
				</button>
				<a class="layui-btn layui-btn-small layui-btn-danger" href="javascript:void(0)" id="batdel" data-toggle="tip" data-title="删除数据表项同时会删除数据表元数据下的所有内容，请确认是否继续操作”？" title="删除数据表">
					批量删除
				</a>
			</span>
		</h1>
		<div class="row" style="padding:5px;">
			<div class="col-lg-12">
				<table class="layui-table" lay-skin="line">
				  <colgroup>
				  		<col width="1%">
						<col width="25%">
						<col width="15%">
						<col width="10%">
						<col width="24%">
						<col width="15%">
					<col>
				  </colgroup>
				  <thead>
					<tr>
					  <th>
					  	  <input type="checkbox" id="all" onclick="$('.ids').prop('checked' , $(this).prop('checked'));"/>
					  </th>
					  <th>中文名</th>
					  <th>表名</th>
					  <th>类型</th>
					  <th>ES数据</th>
					  <th>创建时间</th>
					  <th style="white-space:nowrap;" nowrap="nowrap">操作</th>
					</tr> 
				  </thead>
				  <tbody>
					<#if metadataList?? && metadataList.content??>
					<#list metadataList.content as table>
					<tr>
						<td style="width:1%;">
							<input type="checkbox" class="ids" name="ids" value="${table.id!''}"/>
						</td>
					  <td>
					  	<a href="/admin/metadata/table.html?id=${table.id!''}">
						 ${table.name!''}
						 </a>	
					  </td>
					  <td>${table.tablename!''}	</td>
					  <td><#if table.tabletype?? && table.tabletype == "1">数据表<#else>SQL</#if></td>
					  <td>
					  	<#if table.fromdb?? && table.fromdb == true>
					  		<i class="layui-icon" style="color:#19a55d;">&#xe618;</i>
					  		${uKeFuDic[table.listblocktemplet!''].name!''}
					  		${uKeFuDic[table.previewtemplet!''].name!''}
					  	</#if>
					  </td>
					  <td><#if table.createtime??>${table.createtime?string("yyyy-MM-dd HH:mm:ss")}</#if></td>
					  <td style="white-space:nowrap;width:1%;" nowrap="nowrap">
							<a href="/admin/metadata/edit.html?id=${table.id!''}&p=${metadataList.number+1}" data-width="550" data-height="320" data-toggle="ajax" title="编辑${table.name!''}">
								<i class="layui-icon">&#xe642;</i>
								编辑
							</a>
							<a href="/admin/metadata/delete.html?id=${table.id!''}&p=${metadataList.number+1}" data-toggle="tip" data-title="删除数据表项同时会删除数据表元数据下的所有内容，请确认是否删除数据表“${table.name!''}”？" title="删除${table.name!''}">
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
	$(document).ready(function(){
		$('#batdel').click(function(){
			var ids = "";
			$('.ids').each(function(){
				if($(this).prop("checked")){
					if(ids!=""){
						ids += "&" ;
					}
					ids += "ids=" + $(this).val();
				}
			});
			$('#batdel').attr('href' , '/admin/metadata/batdelete.html?'+ids)	;
			
		});
		
	});	
	layui.use(['laypage', 'layer'], function(){
		  var laypage = layui.laypage
		  ,layer = layui.layer;
		  <#if msg??>
		  top.layer.msg('${msg}');
		  </#if>
		  laypage({
				cont: 'page'
				,pages: <#if metadataList??>${metadataList.totalPages}<#else>0</#if> //总页数
				,curr:<#if metadataList??>${metadataList.number+1}<#else>0</#if>
				,groups: 5 //连续显示分页数
				,jump:function(data , first){
					if(!first){
						location.href = "/admin/metadata/index.html?p="+data.curr ;
					}
				}
		   });
		});
</script>