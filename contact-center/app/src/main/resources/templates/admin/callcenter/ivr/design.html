<script src="/js/jquery-3.1.0.min.js"></script>
<script src="/js/jquery.orgchart.js"></script>
<link rel="stylesheet" href="/css/jquery.orgchart.css">
<div class="row" style="height:100%;">
	<div class="col-lg-12" style="border-right:1px solid #e6e6e6;padding-right:0px;">
		<h1 class="site-h1" style="background-color:#FFFFFF;">
			IVR菜单设置，接入号码：${extention.extention!''}
			<span style="float:right;">
				<button class="layui-btn layui-btn-small green" href="/admin/callcenter/acl/add.html?hostid=${pbxHost.id!''}" data-toggle="ajax" data-width="750" data-title="添加ACL策略">
					添加IVR菜单
				</button>
			</span>
		</h1>
		<div class="row">
			<div class="col-lg-12">
				<div class="ukefu-ivrchat" id="chart-container"></div>
			</div>	
		</div>
	</div>	
</div>
<script language="javascript">
	var datascource = {
	    'name': '分机号：${extention.extention!''}',
	    'title': '${extention.description!''}',
	    'children': [{
	        'name': 'DEMO IVR',
	        'title': '欢迎语音：welcome.wav<br/>按键错误：ivr_error.wav<br/>退出语音：goodbye.wav<br/>放音重复：最大3次<br/>超时时间：2000毫秒',
	        'children': [{
	            'name': '按键：1',
	            'title': '欢迎语音：subwelcome.wav<br/>按键错误：ivr_error.wav<br/>退出语音：goodbye.wav<br/>放音重复：最大3次<br/>超时时间：2000毫秒',
	            'children':[{
	    	        'name': '按键：1',
	    	        'title': '转接到分机：5050（预订酒店）'
	    	    },
	    	    {
	    	        'name': '按键：2',
	    	        'title': '转接到分机：5060（预订机票）'
	    	    }]
	        },
	        {
	            'name': '按键：2',
	            'title': '欢迎语音：order.wav<br/>按键错误：ivr_error.wav<br/>退出语音：goodbye.wav<br/>放音重复：最大3次<br/>超时时间：2000毫秒',
	            'children': [{
	                'name': '按键：1',
	                'title': '转接到分机：5070（订单查询）'
	            },
	            {
	                'name': '按键：2',
	                'title': '转接到分机：5080（机票查询）'
	            }]
	        },
	        {
	            'name': '按键：3',
	            'title': '欢迎语音：sup.wav<br/>按键错误：ivr_error.wav<br/>退出语音：goodbye.wav<br/>放音重复：最大3次<br/>超时时间：2000毫秒',
	            'children': [{
	                'name': '按键：1',
	                'title': '二次输入：卡号<br/>转接到分机：6070（目的地查询）'
	            },
	            {
	                'name': '按键：2',
	                'title': '转接到分机：6080（投诉）'
	            }]
	        }]
	    }]
	};

	$('#chart-container').orgchart({
	    'data': datascource,
	    'nodeContent': 'title',
	    'draggable': true,
	    'dropCriteria': function($draggedNode, $dragZone, $dropZone) {
	        if ($draggedNode.find('.content').text().indexOf('manager') > -1 && $dropZone.find('.content').text().indexOf('engineer') > -1) {
	            return false;
	        }
	        return true;
	    }
	}).children('.orgchart').on('nodedropped.orgchart',
	function(event) {
	    console.log('draggedNode:' + event.draggedNode.children('.title').text() + ', dragZone:' + event.dragZone.children('.title').text() + ', dropZone:' + event.dropZone.children('.title').text());
	});
</script>
