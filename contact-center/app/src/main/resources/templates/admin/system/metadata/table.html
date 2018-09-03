<div class="row">
	<div class="col-lg-12">
		<h1 class="site-h1" style="background-color:#FFFFFF;">
			数据表结构列表<#if sysDicList??>（${propertiesList?size!''}）</#if>
			<span style="float:right;">
				<a class="layui-btn layui-btn-small layui-btn-danger" href="javascript:void(0)" id="batdel" data-toggle="tip" data-title="删除数据表项同时会删除数据表元数据下的所有内容，请确认是否继续操作”？" title="删除数据表">
					批量删除
				</a>
				<#if table?? && table.fromdb && table.listblocktemplet??>
				<a class="layui-btn layui-btn-small layui-btn-danger" href="/admin/metadata/clean.html?id=${table.id!''}" id="batdel" data-toggle="tip" data-title="清空${table.name!''}存储在ES数据表里的所有数据，请确认是否继续操作”？">
					清空ES数据
				</a>
				<a class="layui-btn layui-btn-small layui-btn-danger" href="/admin/metadata/synctoes.html?id=${table.id!''}" id="batdel" data-toggle="tip" data-title="同步${table.name!''}数据库表到ES表，请确认是否继续操作”？">
					同步数据库表到ES表
				</a>
				<a class="layui-btn layui-btn-small layui-btn-danger" href="/admin/metadata/synctodb.html?id=${table.id!''}" id="batdel" data-toggle="tip" data-title="同步${table.name!''}的ES表到数据库表，请确认是否继续操作”？">
					同步ES表到数据库表
				</a>
				</#if>
			</span>
		</h1>
		<div class="row" style="padding:5px;">
			<div class="col-lg-12">
				<table class="layui-table" lay-skin="line">
				  <thead>
					<tr>
					  <th>
					  	  <input type="checkbox" id="all" onclick="$('.ids').prop('checked' , $(this).prop('checked'));"/>
					  </th>
					  <th>标题</th>
					  <th>字段</th>
					  <th>校验</th>
					  <th>长度</th>
					  <th>关联</th>
					  <th>生成ID</th>
					  <th>多值</th>
					  <th>启用字典</th>
					  <th>禁用导入导出</th>
					  <th>流程变量</th>
					  <th style="white-space:nowrap;" nowrap="nowrap">操作</th>
					</tr> 
				  </thead>
				  <tbody>
					<#if propertiesList??>
					<#list propertiesList as prop>
					<tr>
					  <td style="width:1%;">
						<input type="checkbox" class="ids" name="ids" value="${prop.id!''}"/>
					  </td>
					  <td>
					  	${prop.name!''}	
					  </td>
					  <td>${prop.fieldname!''}（${prop.datatypename!''}）</td>
					  <td>${prop.defaultvaluetitle!''}</td>
					  <td>${prop.length!''}</td>
					  <td align="center"><#if prop.reffk == true><i class="layui-icon"  class="green">&#xe618;</#if></td>
					  <td align="center"><#if prop.pk == true><i class="layui-icon" class="green">&#xe618;</#if></td>
					  <td align="center"><#if prop.modits == true><i class="layui-icon" class="green">&#xe618;</#if></td>
					  
					  <td align="center"><#if prop.seldata == true><i class="layui-icon" title="${prop.seldatacode!''}" class="green">&#xe618;</#if></td>
					  <td align="center"><#if prop.impfield == true><i class="layui-icon">&#xe618;</#if></td>
					  <td align="center"><#if prop.systemfield == true><i class="layui-icon">&#xe618;</#if></td>
					  <td style="white-space:nowrap;width:1%;" nowrap="nowrap">
							<a href="/admin/metadata/properties/edit.html?id=${prop.id!''}&p=${metadataList.number+1}" data-width="650" data-height="460" data-toggle="ajax" title="编辑${table.name!''}">
								<i class="layui-icon">&#xe642;</i>
								编辑
							</a>
							<a href="/admin/metadata/properties/delete.html?id=${prop.id!''}&tbid=${prop.dbtableid!''}" data-toggle="tip" data-title="删除数据表项同时会删除数据表元数据下的所有内容，请确认是否删除数据表“${prop.name!''}”？" title="删除${prop.name!''}">
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
			$('#batdel').attr('href' , '/admin/metadata/properties/batdelete.html?tbid=${tbid!''}&'+ids)	;
			
		});
		
	});	
</script>
