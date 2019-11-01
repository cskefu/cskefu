<div class="row">
	<div class="col-lg-12">
		<h1 class="site-h1" style="background-color:#FFFFFF;">
			<#if quickType??>
			${quickType.name!''}
			<#else>
			问答列表
			</#if>
			<#if quickReplyList??>（${quickReplyList.totalElements}）</#if>
			<div style="float:right;" class="ukefu-bt-text-content">
				<#if quickType??>
				<button class="layui-btn layui-btn-small" href="/setting/quickreply/edittype.html?id=${quickType.id!''}" data-toggle="ajax" data-width="950" data-height="450" data-title="编辑分类">
					编辑分类
				</button>
				<button class="layui-btn layui-btn-small layui-btn-danger" href="/setting/quickreply/deletetype.html?id=${quickType.id!''}" data-toggle="tip" data-title="删除分类将会同时删除分类下的所有问答，请确认是否删除？">
					删除分类
				</button>
				</#if>
				<button class="layui-btn layui-btn-small green" href="/setting/quickreply/add.html<#if quickType??>?parentid=${quickType.id!''}</#if>" data-toggle="ajax" data-width="750" data-title="创建新问答">
					添加问答
				</button>
				<#if quickType??>
					<div class="layui-btn-group ukefu-btn-group">
						<button class="layui-btn layui-btn-small" href="/setting/quickreply/imp.html?<#if quickType??>type=${quickType.id}</#if>" title="导入快捷回复" data-toggle="ajax" data-width="950" data-height="600">
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
							<a href="javascript:void(0)" id="batexp" title="导出快捷回复" target="_blank">
								<i class="kfont">&#xe672;</i>
								导出选中结果
							</a>
						</li>
						<li>
							<a href="/setting/quickreply/expall.html?<#if quickType??>type=${quickType.id}</#if>" title="导出全部快捷回复数据" target="_blank">
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
						<col width="24%">
						<col width="54%">
						<col width="20%">
						<col width="1%">
					<col>
				  </colgroup>
				  <thead>
					<tr>
					  <th style="width:1%;padding-right:0px;">
					  	<input type="checkbox" id="checkall" />
					  </th>
					  <th>标题</th>
					  <th>内容</th>
					  <th>创建时间</th>
					  <th style="white-space:nowrap;" nowrap="nowrap">操作</th>
					</tr> 
				  </thead>
				  <tbody style="table-layout: fixed; word-break: break-all;">
					<#if quickReplyList?? && quickReplyList.content??>
					<#list quickReplyList.content as quickReply>
					<tr>
					  <td style="width:1%;padding-right:0px;">
						<input type="checkbox" class="ids" name="ids" value="${quickReply.id!''}"/>
					  </td>
					  <td style="width: 170px">
							<#if quickReply.title?? && quickReply.title?length gt 100>${quickReply.title[0..100]}...<#else>${quickReply.title!''}</#if>
					  </td>
					  <td><#if quickReply.content?? && quickReply.content?length gt 100>${quickReply.content[0..100]}...<#else>${quickReply.content!''}</#if></td>
					  <td>${quickReply.createtime?string("yyyy-MM-dd HH:mm:ss")}</td>
					  <td style="white-space:nowrap;" nowrap="nowrap">
							<a href="/setting/quickreply/edit.html?id=${quickReply.id!''}" data-toggle="ajax" data-width="750" data-title="编辑用户信息">
								<i class="layui-icon">&#xe642;</i>
								编辑
							</a>
							<a href="/setting/quickreply/delete.html?id=${quickReply.id!''}" style="margin-left:10px;" data-toggle="tip" data-title="请确认是否删除记录？">
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
								<div style="">还没有快捷回复记录</div>
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
<script>
$(document).ready(function(){
	var hide ;
	$('.dropdown-menu').on("click" , function(){
		var distance = getDistance(this);
		if(hide = true){
			$(this).closest(".ukefu-btn-group").addClass("open");
		}else{
			$(this).closest(".ukefu-btn-group").removeClass("open");
		}
		if(distance.right < 200){
			$(this).next().css("right" , "0px").css("left" , "auto");
		}
	}).hover(function(){
		hide = true ;
	} , function(){
		hide = false ;
		var btn = $(this); 
		setTimeout(function(){
			if(hide){
				$(btn).removeClass("open");
			}
		} , 500);
	});
	$('.ukefu-btn-group').hover(function(){
		$(this).addClass("open");
		$(this).find('.ukefu-dropdown-menu').css("right" , "0px").css("left" , "auto");
		hide = false ;
	} , function(){
		hide = true ;
		setTimeout(function(){
			if(hide){
				$(".ukefu-btn-group.open").removeClass("open");
			}
		} , 500);
	});
	
	$('#checkall').click(function(){
		var isCheckAll = $(this).prop('checked');
		if (isCheckAll) {  
               $("input[type='checkbox']").each(function() {  
                   this.checked = true;  
               });  
           } else {  
               $("input[type='checkbox']").each(function() {  
                   this.checked = false;  
               });  
           } 
	})
});
	layui.use(['laypage', 'layer'], function(){
		  var laypage = layui.laypage
		  ,layer = layui.layer;
		  
		  laypage({
				cont: 'page'
				,pages: <#if quickReplyList??>${quickReplyList.totalPages}<#else>0</#if> //总页数
				,curr:<#if quickReplyList??>${quickReplyList.number+1}<#else>0</#if>
				,groups: 5 //连续显示分页数
				,jump:function(data , first){
					if(!first){
						loadURL("/setting/quickreply/replylist.html?p="+data.curr+"&typeid=<#if quickType??>${quickType.id!''}<#else>0</#if>","#ukefu-quickreply-content");
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
				$('#batexp').attr('href' , '/setting/quickreply/expids.html?'+ids)	;
			}else{
				top.layer.alert("请先选择需要导出的快捷回复");
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
				top.layer.confirm("请确认是否删除快捷回复？", {icon: 3, title:'提示'}, function(index){
					top.layer.close(index);
					if(confirm){
						location.href = '/setting/quickreply/batdelete.html?<#if quickType??>type=${quickType.id!''}&</#if>'+ids ;
					}
				});
			}else{
				top.layer.alert("请先选择需要删除的快捷回复");
				$('#batexp').attr("href" , "javascript:void(0)");
			}
		});
</script>