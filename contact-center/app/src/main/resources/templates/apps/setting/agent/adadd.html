<div class="uk-layui-form">
	<form class="layui-form" action="/setting/adv/save.html" method="post" enctype="multipart/form-data">
		<input type="hidden" name="adpos" value="${adpos!''}">
		<div class="layui-collapse">
			<div class="layui-colla-item">
				<h2 class="layui-colla-title">基本信息</h2>
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label" id="cusname">广告名称：</label>
							<div class="layui-input-inline">
								<input type="text" name="name" required  lay-verify="required" autocomplete="off" maxlength="100"
									class="layui-input">
							</div>
							<font color="red">*</font>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label" id="cusname">权重：</label>
							<div class="layui-input-inline ukefu-limit-height">
								<select name="weight">
									<#list 1..100 as inx>
									<option value="${inx}">${inx}</option>
									</#list>
								</select>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="layui-colla-item">
				<h2 class="layui-colla-title">扩展信息</h2>
				<div class="layui-colla-content layui-show">
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">广告类型：</label>
							<div class="layui-input-inline ukefu-limit-height" style="position: relative;padding-top:7px;">
								<input type="radio" name="adtype" value="image" lay-filter="image" title="图片">
								<input type="radio" name="adtype" value="text" lay-filter="text" title="文本" checked>
							</div>
						</div>
						<div class="layui-inline uckefu-inline">
							<label class="layui-form-label" id="cusname">提示文本：</label>
							<div class="layui-input-inline ukefu-limit-height">
								<input type="text" name="tiptext" required  lay-verify="required" autocomplete="off" maxlength="100"
									class="layui-input">
							</div>
						</div>
					</div>
					
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label">跳转地址：</label>
							<div class="layui-input-inline ukefu-limit-height" style="position: relative;padding-top:7px;width:664px;">
								<input type="text" name="url" required  lay-verify="required" autocomplete="off" maxlength="100"
									class="layui-input">
							</div>
						</div>
					</div>
					
					<div class="layui-form-item" id="text">
						<div class="layui-inline">
							<label class="layui-form-label">文本内容：</label>
							<div class="layui-input-inline" style="width: 664px;">
								<textarea name="content" placeholder="请输入广告文本内容（支持HTML）" class="layui-textarea" style="resize:none;" maxlength="180"></textarea>
							</div>
						</div>
					</div>
					
					<div class="layui-form-item" id="image" style="display:none;">
						<div class="layui-inline">
							<label class="layui-form-label">广告图片：</label>
							<div class="layui-input-inline" style="width: 670px;">
								<input type="text" name="imgtarget" id="imgtarget" readonly autocomplete="off" maxlength="100" onclick="$('#files').click()"
									class="layui-input" style="width:537px;float:left;margin-right:10px;">
								<div class="layui-box layui-upload-button" style="float:left;">
									<input type="file" name="imgfile" id="files"
											accept="image/*"
											class="layui-upload-file"
											onchange="$('#imgtarget').val($(this).val())"><span
											class="layui-upload-icon"><i class="layui-icon">&#xe608;</i>上传附件</span>
								</div>    
							</div>
						</div>
					</div>
				</div>
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
layui.use('form', function() {
	var form = layui.form();
	form.render(); //更新全部
	form.on("radio(image)" , function(data){
		if(data.elem.checked){
			$('#text').hide();
			$('#image').show();
		}
	});
	form.on("radio(text)" , function(data){
		if(data.elem.checked){
			$('#image').hide();
			$('#text').show();
		}
	});

	//监听提交
	form.on('submit(formDemo)', function(data){
		if(data.field.content.length>180){
			layer.msg("文本内容过长，最大长度为180个字符");
			return false
		}
	});
});
layui.use('upload', function(){
	var target = $(this).attr("data-target");
});
layui.use('element', function(){
	var element = layui.element();
	element.init();
});
</script>