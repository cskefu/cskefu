<div class="ukefu-entim-point ukefu-entim-msgbox" style="cursor: pointer;" onclick="top.closeentim();">
	<i class="layui-icon">&#xe645;</i>
	<#assign newmsg = 0>
	<#if recentUserList??>
	<#list recentUserList as recentUser>
		<#assign newmsg = recentUser.newmsg + newmsg>
	</#list>
	</#if>
	<span class="layui-anim layer-anim-05 animated" id="msgbox" data-newmsg="${newmsg}" style="<#if newmsg == 0>display:none;</#if>top: 15px;background-color: red;left: 0px;">${newmsg}</span>
	企业聊天
    <div class="putAway" id="putAway" title="收起" style="width:30px; height: 100%;float: right">
		<i class="layui-icon">&#xe602;</i>
	</div>
</div>
<style>
	.putAway:hover{
		background: rgba(0,0,0,0.2);
	}
</style>
<script>
	var hostname = location.hostname ;
	var socket = io.connect(location.protocol+"//"+top.hostname+":"+top.port+"/im/ent?userid=${user.id!''!''}&orgi=${user.orgi!''}");
	socket.on('connect',function(){
	})
	socket.on('message', function(data) {
		$('#msgbox').data("newmsg" , $('#msgbox').data("newmsg")+1).text($('#msgbox').data("newmsg")).show();
	});
	
	socket.on('disconnect',function() {
	});
	$("#putAway").click(function(e){
		top.isImIndex = true;
		top.closeentim();
		e.stopPropagation();
	})

</script>
