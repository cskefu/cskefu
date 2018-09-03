<div class="uk-layui-form">
	<form class="layui-form uk-form" action="/admin/area/update.html">
		<input type="hidden" name="area" id="area" value="${area.area!''}">
		<input type="hidden" name="id" id="id" value="${area.id!''}">
		<div class="layui-form-item">
			<label class="layui-form-label">名称：</label>
			<div class="layui-input-inline">
				<input type="text" name="name" required lay-verify="required"
					placeholder="请输入区域名称" autocomplete="off" class="layui-input" value="${area.name!''}">
			</div>
			<div class="layui-form-mid layui-word-aux">区域名称，例如：华南、华北</div>
		</div>
		<div class="layui-form-item">
			<label class="layui-form-label">地区：</label>
			<div class="layui-input-inline">
				<ul id="areaTree" class="ztree"></ul>
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
<SCRIPT type="text/javascript">
		var setting = {
			check: {
				enable: true
			},
			data: {
				simpleData: {
					enable: true
				}
			},
			callback: {
				onCheck: zTreeOnCheck
			}
		};
		
		//获取所有选中节点的值
	    function zTreeOnCheck() {
	        var treeObj = $.fn.zTree.getZTreeObj("areaTree");
	        var root = treeObj.getNodesByParam("id" , "${sysarea.id!''}");
	        var nodes = root[0].children;
	        var msg = "";
	        for (var i = 0; i < nodes.length; i++) {
	        	var node = nodes[i] ; 
	        	var check  = true;
	        	var children = node.children ;
	        	var citys = "";
	        	if(children && children.length > 0){
		        	for(j = 0 ; j < children.length ; j ++){
		        		if(children[j].checked == false){
		        			check = false  ;
		        		}else{
		        			if(citys != ""){
		        				citys += "," ;
		        			}
		        			citys = citys + children[j].name ;
		        		}
		        	}
	        	}else if(nodes[i].checked == false){
	        		check = false ;
	        	}
	        	if(check == true){
		        	if(msg!='' && nodes[i].name != ''){
						msg = msg+"," ;	        		
		        	}
		            msg += nodes[i].name;
	            }else{
	            	if(msg!='' && citys != ''){
						msg = msg+"," ;	        		
		        	}
	            	msg += citys;
	            }
	        }
	        $("#area").val(msg);
	    }    


		var zNodes =[
		    <#if sysarea??>{ id:'${sysarea.id!''}', pId:'0', name:"地区", open:true , value : ""}</#if>
		    <#if cacheList??>
		    <#list cacheList as cachearea>
			,{ id:'${cachearea.id}', pId:'${cachearea.parentid!''}', name:"${cachearea.name!''}",city:false , open:false <#if area.area?index_of(cachearea.name) gte 0>,checked:true</#if>}
			<#if areaList?? && cachearea.name?index_of('市') lt 0>
			<#list areaList as city>
			<#if city.parentid == cachearea.id>
			,{ id:'${city.id}', pId:'${city.parentid!''}', name:"${city.name!''}",city:true , open:false <#if area.area?index_of(city.name) gte 0>,checked:true</#if>}
			</#if>
			</#list>
			</#if>
			</#list>
			</#if>
		];
		
		$(document).ready(function(){
			$.fn.zTree.init($("#areaTree"), setting, zNodes);
		});
	</SCRIPT>
