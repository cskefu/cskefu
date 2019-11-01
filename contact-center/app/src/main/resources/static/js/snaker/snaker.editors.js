(function($){
var snakerflow = $.snakerflow;

$.extend(true, snakerflow.editors, {
	inputEditor : function(){
		var _props,_k,_div,_src,_r;
		this.init = function(props, k, div, src, r){
			_props=props; _k=k; _div=div; _src=src; _r=r;
			$('<input style="width:97%;"/>').val(props[_k].value).change(function(){
				props[_k].value = $(this).val();
			}).appendTo('#'+_div);
			
			$('#'+_div).data('editor', this);
		}
		this.destroy = function(){
			$('#'+_div+' input').each(function(){
				_props[_k].value = $(this).val();
			});
		}
	},
	selectEditor : function(arg){
		var _props,_k,_div,_src,_r;
		this.init = function(props, k, div, src, r){
			_props=props; _k=k; _div=div; _src=src; _r=r;

			var sle = $('<select  style="width:100%;"/>').val(props[_k].value).change(function(){
				props[_k].value = $(this).val();
			}).appendTo('#'+_div);
			
			if(typeof arg === 'string'){
				$.ajax({
				   type: "GET",
				   url: arg,
				   success: function(data){
					  var opts = eval(data);
					 if(opts && opts.length){
						for(var idx=0; idx<opts.length; idx++){
							sle.append('<option value="'+opts[idx].value+'">'+opts[idx].name+'</option>');
						}
						sle.val(_props[_k].value);
					 }
				   }
				});
			}else {
				for(var idx=0; idx<arg.length; idx++){
					sle.append('<option value="'+arg[idx].value+'">'+arg[idx].name+'</option>');
				}
				sle.val(_props[_k].value);
			}
			
			$('#'+_div).data('editor', this);
		};
		this.destroy = function(){
			$('#'+_div+' input').each(function(){
				_props[_k].value = $(this).val();
			});
		};
	},
	assigneeEditor : function(arg){
		var _props,_k,_div,_src,_r;
		this.init = function(props, k, div, src, r){
			_props=props; _k=k; _div=div; _src=src; _r=r;
			$('<input style="width:70%;" readonly="true" id="dialogEditor"/>').click(function(){
				props[_k].value = $(this).val();
			}).val(props[_k].value).appendTo('#'+_div);
			$('<input type="hidden" id="dialogEditorValue"/>').click(function(){
				props['assignee'].value = $(this).val();
			}).val(props['assignee'].value).appendTo('#'+_div);
			$('<input style="width:24%;margin-left:5px;" type="button" value="选择"/>').click(function(){
				var value = props['assignee'].value ;
				$.ajax({
					url:arg,
					cache:false,
					data:"ids="+value,
					success: function(data){
						top.iframe = window.frameElement && window.frameElement.id || '';
						top.layerwin = top.layer.open({title:"请选择", type: 1, id: 'mainajaxwin', area:["750px" , "450px"] , maxmin: true, anim: 2,content: data});
					}
				});
			}).appendTo('#'+_div);

			$('#'+_div).data('editor', this);
		}
		this.destroy = function(){
			//
		}
	}
});

})(jQuery);