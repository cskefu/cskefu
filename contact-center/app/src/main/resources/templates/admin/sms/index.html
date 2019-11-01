<div class="row">
	<div class="col-lg-12">
		<h1 class="site-h1" style="background-color:#FFFFFF;">
			短信网关列表<#if smsList??>（${smsList.totalElements!''}）</#if>
			<span style="float:right;">
				<button class="layui-btn layui-btn-small green" href="/admin/sms/add.html" data-toggle="ajax" data-width="550" data-height="450" data-title="创建新网站">
					创建新短信网关
				</button>
			</span>
		</h1>
		<div class="row" style="padding:5px;">
			<div class="col-lg-12">
				<table class="layui-table" lay-skin="line">
				  <colgroup>
						<col width="10%">
						<col width="15%">
						<col width="10%">
						<col width="15%">
						<col width="10%">
						<col width="10%">
						<col width="20%">
						<col width="1%">
				  </colgroup>
				  <thead>
					<tr>
					  <th>名称</th>
					  <th>短信网关类型</th>
					  <th>访问的URL</th>
					  <th>模板ID</th>
					  <th>AppKey</th>
					  <th>参数</th>
					  <th>创建时间</th>
					  <th style="white-space:nowrap;" nowrap="nowrap">操作</th>
					</tr> 
				  </thead>
				  <tbody>
					<#if smsList?? && smsList??>
					<#list smsList.content as sms>
					<tr>
					  <td>
							${sms.name!''}
					  </td>
					 <td>${uKeFuDic[sms.smstype!'']!''}</td>
					  <td>${sms.url!''}</td>
					  <td>${sms.tpcode!''}</td>
					  <td>${sms.appkey!''}</td>
					  <td>${sms.moreparam!''}</td>
					  <td>${sms.createtime?string("yyyy-MM-dd HH:mm:ss")}</td>
					  <td style="white-space:nowrap;" nowrap="nowrap">
					  		<a href="/admin/sms/edit.html?id=${sms.id!''}" data-toggle="ajax"  data-width="550" data-height="450" data-title="编辑网站">
					  			<i class="layui-icon">&#xe642;</i>
					  			编辑
					  		</a>
					  		<a href="/admin/sms/delete.html?id=${sms.id!''}" style="margin-left:10px;" data-toggle="tip"  title="请确认是否删除邮件服务器？" >
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
	<#if execute?? && execute == "false">
	top.layer.alert('二次 验证密码设置失败，输入的密码错误，请重新操作！', {icon: 2});
	</#if>
	<#if msg??>
		<#if msg == '1'>
			top.layer.alert('二次验证密码设置成功', {icon: 1});
		<#elseif msg == '2'>
			top.layer.alert('二次验证密码设置失败，请确认两次输入的密码一致', {icon: 2});
		<#elseif msg == '3'>
			top.layer.alert('二次验证密码设置失败，旧的密码验证失败，请输入正确的密码', {icon: 2});
		</#if>
	</#if>
	layui.use(['laypage', 'layer'], function(){
		  var laypage = layui.laypage
		  ,layer = layui.layer;
		  
		  laypage({
				cont: 'page'
				,pages: <#if smsList??>${smsList.totalPages}<#else>0</#if> //总页数
				,curr:<#if smsList??>${smsList.number+1}<#else>0</#if>
				,groups: 5 //连续显示分页数
				,jump:function(data , first){
					if(!first){
						location.href = "/admin/sms/index.html?p="+data.curr ;
					}
				}
		   });
		});
</script>