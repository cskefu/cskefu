<div class="uk-layui-form">
    <form class="layui-form uk-form" action="/admin/callcenter/extention/update.html" data-toggle="ajax-form" data-inner="#callCenterContent">
    	<input type="hidden" name="hostid" value="${pbxHost.id!''}">
    	<input type="hidden" name="id" value="${extention.id!''}">
        <div class="layui-form-item">
            <label class="layui-form-label">分机号：</label>
            <div class="layui-input-inline">
                <input type="text" name="extention" required lay-verify="required|number" value="${extention.extention!''}" placeholder="请输入分机号码" autocomplete="off" class="layui-input">
            </div>
        </div>
        
        <div class="layui-form-item">
            <label class="layui-form-label">备注：</label>
            <div class="layui-input-inline">
                <input type="text" name="description" value="${extention.description!''}" placeholder="请输入分机备注信息" autocomplete="off" class="layui-input">
            </div>
        </div>
       
        <div class="layui-form-item">
            <label class="layui-form-label">分机密码：</label>
            <div class="layui-input-inline">
                <input type="password" id="password" name="password" placeholder="请输入分机密码" autocomplete="off" class="layui-input">
            </div>
        </div>
        
        <div class="layui-form-item">
            <label class="layui-form-label">分机类型：</label>
            <div class="layui-input-inline">
            	<select id="extype" name="extype" required="" lay-verify="required">
					<option value="">请选择...</option>
					<option value="line" <#if extention.extype?? && extention.extype == "line">selected="selected"</#if>>直线</option>
					<option value="ivr" <#if extention.extype?? && extention.extype == "ivr">selected="selected"</#if>>IVR</option>
					<option value="business" <#if extention.extype?? && extention.extype == "business">selected="selected"</#if>>业务</option>
					<option value="skill" <#if extention.extype?? && extention.extype == "skill">selected="selected"</#if>>技能组</option>
					<option value="conference" <#if extention.extype?? && extention.extype == "conference">selected="selected"</#if>>会议</option>
					<option value="quene" <#if extention.extype?? && extention.extype == "quene">selected="selected"</#if>>队列</option>
				</select>    
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">绑定SIP网关：</label>
            <div class="layui-input-inline">
            	<select id="siptrunk" name="siptrunk">
					<option value="">请选择...</option>
					<#if sipTrunkListList??>
					<#list sipTrunkListList as sipTrunkInfo>
					<option value="${sipTrunkInfo.id!''}" <#if extention.siptrunk?? && extention.siptrunk == sipTrunkInfo.id>selected="selected"</#if>>${sipTrunkInfo.name!''}</option>
					</#list>
					</#if>
				</select>    
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">播报工号：</label>
            <div class="layui-input-inline" style="width:60px;">
                <input type="checkbox" name="playnum" lay-skin="switch" lay-text="开启|关闭" lay-filter="median_tip"  <#if extention.playnum>checked="checked"</#if>>
            </div>
            <div class="layui-input-inline" style="width:145px;<#if extention.playnum == false>display:none;</#if>" id="media">
                <select name="mediapath">
                	<option values="">请选择播报录音</option>
                	<#if mediaList??>
                	<#list mediaList as media>
                	<option value="${media.filename!''}" <#if extention.mediapath?? && extention.mediapath == media.filename>selected="selected"</#if>>${media.name!''}</option>
                	</#list>
                	</#if>
                </select>
            </div>
        </div>
        
        <div class="layui-form-item">
            <label class="layui-form-label">是否允许外呼：</label>
            <div class="layui-input-inline">
            	<input type="checkbox" name="callout" lay-skin="switch" lay-text="开启|关闭" <#if extention.callout>checked="checked"</#if>>    
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">是否录音：</label>
            <div class="layui-input-inline">
                <input type="checkbox" name="record" lay-skin="switch" lay-text="开启|关闭" <#if extention.record>checked="checked"</#if>>
            </div>
        </div>
        
        <div class="layui-form-item">
            <label class="layui-form-label">后处理：</label>
            <div class="layui-input-inline">
                <input type="checkbox" name="afterprocess" lay-skin="switch" lay-text="开启|关闭" <#if extention.afterprocess>checked="checked"</#if>>
            </div>
        </div>
        
        <div class="layui-form-item">
            <label class="layui-form-label">启用WebRTC：</label>
            <div class="layui-input-inline">
                <input type="checkbox" name="enablewebrtc" lay-skin="switch" lay-text="开启|关闭" <#if extention.enablewebrtc>checked="checked"</#if>>
            </div>
        </div>
        
        <div class="layui-form-button">
            <div class="layui-button-block">
                <button class="layui-btn" lay-submit lay-filter="formDemo">立即提交</button>
                <button type="reset" class="layui-btn layui-btn-primary">重置</button>
            </div>
        </div>
    </form>
</div>
<script>
	layui.use('form', function(){
	  var form = layui.form();
	  form.render();
	  form.on("switch(median_tip)" , function(data){
		  if(data.elem.checked == true){
			  $("#media").show();
		  }else{
			  $("#media").hide();
		  }
	  })
	});
</script>