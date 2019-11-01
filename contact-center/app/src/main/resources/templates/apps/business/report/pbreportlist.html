<div class="row">
		<div class="col-lg-12">
			<h1 class="site-h1" style="background-color:#FFFFFF;">
				<#if dataDic??>
				${dataDic.name!''}
				<#else>
				报表列表
				</#if>
				<#if reportList??>（${reportList.totalElements}）</#if>
			</h1>
			<div class="row" style="padding:5px;">
				<div class="col-lg-12">
					<table class="layui-table" lay-skin="line">
					  <colgroup>
					  		<col width="1%">
							<col width="40%">
							<col width="24%">
							<col width="30%">
							<col width="1%">
						<col>
					  </colgroup>
					  <thead>
						<tr>
						  <th style="width:1%;padding-right:0px;">
						  	<input type="checkbox" id="checkall" />
						  </th>
						  <th>名称</th>
						  <th>版本</th>
						  <th>创建时间</th>
						  <th style="white-space:nowrap;" nowrap="nowrap">操作</th>
						</tr> 
					  </thead>
					  <tbody>
						<#if reportList?? && reportList.content??>
						<#list reportList.content as report>
						<tr>
						  <td style="width:1%;padding-right:0px;">
							<input type="checkbox" class="ids" name="ids" value="${report.id!''}"/>
						  </td>
						  <td><a href="/apps/report/view.html?id=${report.id!''}" target="_blank"> 
								<#if report.name?? && report.name?length gt 100>${report.name[0..100]}...<#else>${report.name!''}</#if>(${report.code!''})
						  		</a>
						  </td>
						  <td>
								${report.dataversion!''} 
							</td>
						  <td>${report.createtime?string("yyyy-MM-dd HH:mm:ss")}</td>
						  <td style="white-space:nowrap;" nowrap="nowrap">
								<a href="/apps/report/pbdelete.html?id=${report.id!''}" style="margin-left:10px;" data-toggle="tip" data-title="请确认是否删除报表？">
									<i class="layui-icon" style="color:red;">&#x1006;</i>
									删除
								</a>
						  </td>
						</tr>
						</#list>
						<#else>
						<tr>
							<td colspan="20" style="height:400px;">
								<div class="ukefu-empty" style="background: none">
									<i class="layui-icon">&#xe63a;</i>
									<div style="">还没有记录</div>
								</div>
							</td>
						</tr>
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
<SCRIPT>
	layui.use(['laypage', 'layer'], function(){
		  var laypage = layui.laypage
		  ,layer = layui.layer;
		  
		  laypage({
				cont: 'page'
				,pages: <#if reportList??>${reportList.totalPages}<#else>0</#if> //总页数
				,curr:<#if reportList??>${reportList.number+1}<#else>0</#if>
				,groups: 5 //连续显示分页数
				,jump:function(data , first){
					if(!first){
						loadURL("/apps/report/pbreportlist.html?p="+data.curr+"&dicid=<#if dataDic??>${dataDic.id!''}<#else>0</#if>","#ukefu-datadic-content");
					}
				}
		   });
	});
</SCRIPT>