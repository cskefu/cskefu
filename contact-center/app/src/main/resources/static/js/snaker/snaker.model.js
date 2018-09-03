(function($){
	var snakerflow = $.snakerflow;

	$.extend(true,snakerflow.config.rect,{
		attr : {
		r : 8,
		fill : '#F6F7FF',
		stroke : '#03689A',
		"stroke-width" : 2
	}
	});

	$.extend(true,snakerflow.config.props.props,{
		name : {name:'name', label:'名称', value:'', editor:function(){return new snakerflow.editors.inputEditor();}},
		displayName : {name:'displayName', label:'显示名称', value:'', editor:function(){return new snakerflow.editors.inputEditor();}},
		expireTime : {name:'expireTime', label:'期望完成时间', value:'', editor:function(){return new snakerflow.editors.inputEditor();}}
	});


	$.extend(true,snakerflow.config.tools.states,{
		start : {
			showType: 'image',
			type : 'start',
			name : {text:'<<start>>'},
			text : {text:'start'},
			img : {src : 'images/48/start_event_empty.png',width : 48, height:48},
			attr : {width:50 ,heigth:50 },
			props : {
				name: {name:'name',label: '名称', value:'start', editor: function(){return new snakerflow.editors.inputEditor();}}
			}
		},
		end : {
			showType: 'image',
			type : 'end',
			name : {text:'<<end>>'},
			text : {text:'end'},
			img : {src : 'images/48/end_event_terminate.png',width : 48, height:48},
			attr : {width:50 ,heigth:50 },
			props : {
				name: {name:'name',label: '名称', value:'end', editor: function(){return new snakerflow.editors.inputEditor();}},
			    preInterceptors: {name:'preInterceptors', label : '前置拦截器', value:'', editor: function(){return new snakerflow.editors.inputEditor();}}
			}
		},
		task : {
			showType: 'text',
			type : 'task',
			name : {text:'<<task>>'},
			text : {text:'task'},
			img : {src : 'images/48/task_empty.png',width :48, height:48},
			props : {
				name: {name:'name',label: '名称', value:'', editor: function(){return new snakerflow.editors.inputEditor();}},
				displayName: {name:'displayName',label: '显示名称', value:'', editor: function(){return new snakerflow.editors.textEditor();}},
				form: {name:'form', label : '表单类型', value:'', editor: function(){return new snakerflow.editors.selectEditor([{name:'新增',value:'add'},{name:'编辑',value:'edit'},{name:'回复',value:'comment'},{name:'审批',value:'approval'}]);}},
				assignee: {name:'assignee', value:''},
				assigneeDisplay: {name:'assigneeDisplay', label: '参与者',key:'', value:'', editor: function(){return new snakerflow.editors.assigneeEditor('/res/bpm/organ.html');}},
				taskType: {name:'taskType', label : '任务类型', value:'', editor: function(){return new snakerflow.editors.selectEditor([{name:'主办任务',value:'Major'},{name:'协办任务',value:'Aidant'}]);}},
				performType: {name:'performType', label : '参与类型', value:'', editor: function(){return new snakerflow.editors.selectEditor([{name:'普通参与',value:'ANY'},{name:'会签参与',value:'ALL'}]);}},
			    reminderTime: {name:'reminderTime', label : '提醒时间', value:'', editor: function(){return new snakerflow.editors.inputEditor();}},
			    reminderRepeat: {name:'reminderRepeat', label : '提醒间隔', value:'', editor: function(){return new snakerflow.editors.inputEditor();}},
				expireTime: {name:'expireTime', label: '完成时间', value:'', editor: function(){return new snakerflow.editors.inputEditor();}},
				autoExecute: {name:'autoExecute', label : '自动执行', value:'', editor: function(){return new snakerflow.editors.selectEditor([{name:'否',value:'N'},{name:'是',value:'Y'}]);}}
			}
		},
		decision : {
			showType: 'image',
			type : 'decision',
			name : {text:'<<decision>>'},
			text : {text:'decision'},
			img : {src : 'images/48/gateway_exclusive.png',width :48, height:48},
			props : {
				name: {name:'name',label: '名称', value:'', editor: function(){return new snakerflow.editors.inputEditor();}},
				expr: {name:'expr',label: '决策表达式', value:'', editor: function(){return new snakerflow.editors.inputEditor();}}
			}
		},
		fork : {
			showType: 'image',
			type : 'fork',
			name : {text:'<<fork>>'},
			text : {text:'fork'},
			img : {src : 'images/48/gateway_parallel.png',width :48, height:48},
			props : {
				name: {name:'name',label: '名称', value:'', editor: function(){return new snakerflow.editors.inputEditor();}}
			}
		},
		join : {
			showType: 'image',
			type : 'join',
			name : {text:'<<join>>'},
			text : {text:'join'},
			img : {src : 'images/48/gateway_parallel.png',width :48, height:48},
			props : {
				name: {name:'name',label: '名称', value:'', editor: function(){return new snakerflow.editors.inputEditor();}}
			}
		}
	});
})(jQuery);