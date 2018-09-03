/*point	点，用于点图的构建。
  path	路径，无序的点连接而成的一条线。
  line	线，点按照 x 轴连接成一条线，构成线图。
  area	填充线图跟坐标系之间构成区域图，也可以指定上下范围。
  interval	使用矩形或者弧形，用面积来表示大小关系的图形，一般构成柱状图、饼图等图表。
  polygon	多边形，可以用于构建热力图、地图等图表类型。
	schema	k线图，箱型图。
	edge	树图、流程图、关系图。
	heatmap	热力图。
	pointStack	层叠点图
	pointJitter	扰动点图
	pointDodge	分组点图
	intervalStack	层叠柱状图
	intervalDodge	分组柱状图
	intervalSymmetric	对称柱状图
	areaStack	层叠区域图
	schemaDodge	分组箱型图*/
var renderType = {
		"histogram":'intervalDodge',//柱状图
		"bar":'intervalDodge',//条形图
		"line":'line,point',//折线图
		"dotplot":'point',//点状图
		"area":'area,line',//面积图
		"pie":'intervalStack',//饼形图
		"ring":'intervalStack',//环形图
		"radar":'line,point',//雷达图
		"funnel":'intervalSymmetric',//漏斗图
		"pyramid":'intervalSymmetric',//金字塔
		"map":'polygon',//地图
}
var ChartAction = {
	renderChart:function(id ,data ,option ){
		 let chart;
		if(option.chartype &&  option.chartype == 'map'){
			// start: 计算地图的最佳宽高
		    let longitudeRange = data.range('longitude');
		    let lantitudeRange = data.range('lantitude');
		    let ratio = (longitudeRange[1] - longitudeRange[0]) / (lantitudeRange[1] - lantitudeRange[0]);
		    let width;
		    let height;
		    if (ratio > 1) {
		      width = $('#chart_'+id).width();
		      height = width / ratio;
		    } else {
		      width = 300 * ratio;
		      height = $('#chart_'+id).height();
		    }
		    chart = new G2.Chart({
				container: 'chart_'+id,
				width,
			      height,
			      padding: 0
			  });	
		    chart.tooltip({
		        showTitle: false
		      });
		}else{
			chart = new G2.Chart({
				container: 'chart_'+id,
				forceFit: true
			  });	
		}
		    
		  chart.source(data);
		  //格式化
		  chart.scale('value', {
		        formatter: val => {
			          val = number_format(option.format,val);
			          return val;
			        }
			      })
		  if(option.chartype &&  (option.chartype == 'funnel' ||  option.chartype == 'pyramid' || option.chartype == 'map' )){
			  //金字塔 漏斗图不显示
			  chart.axis(false);
		  }else{
			  let xoption = {};
			  if(option.chartype &&  option.chartype == 'radar'){
				  //雷达图
				  xoption ={
						    line: null,
						    tickLine: null,
						    grid: {
						      lineStyle: {
						        lineDash: null
						      },
						      hideFirstLine: false
						    }
						  } 
			  }else{
				  xoption = {
							//position: 'left',//位置 top、bottom、left、right
							label: {//样式
							      formatter: val => {
							        return val;
							      }
							    }
						  }  
			  }
			  if(option.chartype &&  option.chartype == 'map'){
				  
			  }else{
				  //x轴位置
				  chart.axis('key', xoption);
				  let yoption = {};
				  if(option.chartype &&  option.chartype == 'radar'){
					  //雷达图
					  yoption = {
								line: null,
								tickLine: null,
								grid: {
								  type: 'polygon',
								  lineStyle: {
									lineDash: null
								  },
								  alternateColor: 'rgba(0, 0, 0, 0.04)',
								}
							  }
				  }else{
					  yoption = {}
								//position: 'left',//位置 top、bottom、left、right
				  }
				  //y轴位置
				  chart.axis('value', yoption);
			  }
		  }
		  //是否显示图例
		  if(option.legen){
			  let legenalign = {};
			  legenalign['position'] = option.legenalign;
			  chart.legend(legenalign)
		  }else{
			  chart.legend(false)
		  }
		  //坐标系变换
		  //chart.coord();
		  //坐标x y转换
		  if( option.chartype &&  option.chartype == 'bar'){
			  //条形图
			  chart.coord().transpose();
		  }
		  if(option.chartype &&  option.chartype == 'pie'){
			  //饼形图
			  chart.coord('theta', {
				    radius: 0.75
				  });
		  }
		  if(option.chartype &&  option.chartype == 'radar'){
			  //雷达图
			  chart.coord('polar', {
				    radius: 0.8
				  });
		  }
		  if(option.chartype &&  option.chartype == 'ring'){
			  //环形图
			  chart.coord('theta', {
					radius: 0.75,
					innerRadius: 0.6
				  });
		  }
		  if(option.chartype &&  option.chartype == 'funnel'){
			  //漏斗图
			  chart.coord('rect').transpose().scale(1,-1);
		  }
		  if(option.chartype &&  option.chartype == 'pyramid'){
			  //金字塔图
			  chart.coord('rect').transpose();
		  }
		  //面积图
		  /*chart.tooltip({
			crosshairs: {
				  type: 'line'
				}
			  });*/
		  let charttype = renderType[option.chartype];
		  let charttypearr = charttype.split(",");
		  for (var i=0;i<charttypearr.length ;i++ ) 
		  { 
			  //创建图表的类型
			  var  chartobj = eval("chart."+charttypearr[i]+"()");
			 
			  if(option.chartype &&  option.chartype == 'pie'){
				  //饼形图
				  chartobj.position('value').color('key');
			  }else if(option.chartype &&  (option.chartype == 'funnel' ||  option.chartype == 'pyramid')){
				  chartobj.position('key*value').color('key'); 
				  //金字塔 漏斗图不显示
				  chartobj.shape('pyramid');
			  }else if(option.chartype &&  option.chartype == 'map' ){
				  chartobj.position('longitude*lantitude').color('value', '#BAE7FF-#1890FF-#0050B3'); 
			  }else{
				  chartobj.position('key*value').color('_name'); 
			  }
			  			  
			  if(charttypearr[i] == 'point'){
				  chartobj.shape('circle').style({
						stroke: '#fff',
						lineWidth: 1
					  })
			  }
			  
			  //显示数值
			  if(option.dataview){
				  if(option.chartype &&  option.chartype == 'map' ){
					  chartobj.label('key');
				  }else{
					  chartobj.label('value');
				  }
				  
			  }
			  //标题
			  /*chartobj.guide().text({
			      position: [ 'min', 'max'],
			      offsetY: 20,
			      content: name,
			      style: {
			        fontSize: 14,
			        fontWeight: 'bold'
			      }
			    });*/
		  } 		  		  
		  chart.render();
	}
}

var numberformat = function(formatstr,value){
	let format = formatstr;
	let val = value;
	if(!format || format == 'val'){
		return val;
	}
	if(format == "####"){
		return number_format(val,0,".","");
	}
	if(format == "0.0"){
		return number_format(val,1,".","");
	}
	if(format == "0.00"){
		return number_format(val,2,".","");
	}
	if(format == "0.000"){
		return number_format(val,3,".","");
	}
	if(format == "###,###"){
		return number_format(val,0,".",",");
	}
	if(format == "###,###.0"){
		return number_format(val,1,".",",");
	}
	if(format == "###,###.00"){
		return number_format(val,2,".",",");
	}
	if(format == "###,###.000"){
		return number_format(val,3,".",",");
	}
	if(format == "100%"){
		return number_format(val*100,0,".","")+"%";
	}
	if(format == "100.0%"){
		return number_format(val*100,1,".","")+"%";
	}
	if(format == "100.00%"){
		return number_format(val*100,2,".","")+"%";
	}
	return val;
}

function number_format(formatstr,value) {
	let format = formatstr;
	let number = value;
	let decimals;
	let dec_point;
	let thousands_sep;
	let sign = '';
	if(!format || format == 'val'){
		return value;
	}
	if(format == "####"){
		decimals = 0;
		dec_point = ".";
		thousands_sep = "";
	}
	if(format == "0.0"){
		decimals = 1;
		dec_point = ".";
		thousands_sep = "";
	}
	if(format == "0.00"){
		decimals = 2;
		dec_point = ".";
		thousands_sep = "";
	}
	if(format == "0.000"){
		decimals = 3;
		dec_point = ".";
		thousands_sep = "";
	}
	if(format == "###,###"){
		decimals = 0;
		dec_point = ".";
		thousands_sep = ",";
	}
	if(format == "###,###.0"){
		decimals = 1;
		dec_point = ".";
		thousands_sep = ",";
	}
	if(format == "###,###.00"){
		decimals = 2;
		dec_point = ".";
		thousands_sep = ",";
	}
	if(format == "###,###.000"){
		decimals = 3;
		dec_point = ".";
		thousands_sep = ",";
	}
	if(format == "100%"){
		number = number*100;
		decimals = 0;
		dec_point = ".";
		thousands_sep = "";
		sign = "%";
	}
	if(format == "100.0%"){
		number = number*100;
		decimals = 1;
		dec_point = ".";
		thousands_sep = "";
		sign = "%";
	}
	if(format == "100.00%"){
		number = number*100;
		decimals = 2;
		dec_point = ".";
		thousands_sep = "";
		sign = "%";
	}
    /*
    * 参数说明：
    * number：要格式化的数字
    * decimals：保留几位小数
    * dec_point：小数点符号
    * thousands_sep：千分位符号
    * */
    number = (number + '').replace(/[^0-9+-Ee.]/g, '');
    let n = !isFinite(+number) ? 0 : +number;
    let	prec = !isFinite(+decimals) ? 0 : Math.abs(decimals);
    let sep = (typeof thousands_sep === 'undefined') ? ',' : thousands_sep;
    let dec = (typeof dec_point === 'undefined') ? '.' : dec_point;
    let toFixedFix = function (n, prec) {
        	let k = Math.pow(10, prec);
            return '' + Math.floor(n * k) / k;
        };
    let s = (prec ? toFixedFix(n, prec) : '' + Math.floor(n)).split('.');
    let s0 = s[0];
    let s1 = s[1];
    let result = '', counter = 0;
    for (var i = s0.length - 1; i >= 0; i--) {
        counter++;
        result = s0.charAt(i) + result;
        if (!(counter % 3) && i != 0) { result = ',' + result; }
    }

    if ((s1|| '').length < prec) {
        s1 = s1 || '';
        let arr = new Array(prec - s1.length + 1);
        s1 += arr.join('0');
    }
    if(s1){
    	result = result + dec + "" + s1;
    }
    return result + sign;
}

//调用高德 api 绘制底图以及获取 geo 数据
const map = new AMap.Map('china', {
  zoom: 4
});
const colors = [ "#3366cc", "#dc3912", "#ff9900", "#109618", "#990099", "#0099c6", "#dd4477", "#66aa00", "#b82e2e", "#316395", "#994499", "#22aa99", "#aaaa11", "#6633cc", "#e67300", "#8b0707", "#651067", "#329262", "#5574a6", "#3b3eac" ];

var mapLoad = function(func){
	AMapUI.load(['ui/geo/DistrictExplorer', 'lib/$'], function(DistrictExplorer) {
		  // 创建一个实例
		  let districtExplorer = window.districtExplorer = new DistrictExplorer({
		    eventSupport: true, //打开事件支持
		    map: map
		  });	 	

		  //加载区域
		  function loadAreaNode(adcode, callback) {
		    districtExplorer.loadAreaNode(adcode, function(error, areaNode) {
		      if (error) {
		        if (callback) {
		          callback(error);
		        }
		        return;
		      }
		      let geoJson =  areaNode.getSubFeatures();
		      func(areaNode.getSubFeatures());
		      if (callback) {
		        callback(null, areaNode);
		      }
		    });
		  }
		  //全国
		  loadAreaNode(100000);
		});	
}

var MapChart = {
	init:function(id,data,option){
		
	},
	render:// 开始使用 G2 绘制地图
	  function(id,data,option) {
	    const dv = data;
	    // start: 计算地图的最佳宽高
	    const longitudeRange = dv.range('longitude');
	    const lantitudeRange = dv.range('lantitude');
	    const ratio = (longitudeRange[1] - longitudeRange[0]) / (lantitudeRange[1] - lantitudeRange[0]);
	    let width;
	    let height;
	    if (ratio > 1) {
	      width = $('#'+id).width();
	      height = width / ratio;
	    } else {
	      width = 300 * ratio;
	      height = $('#'+id).height();
	    }
	    var provinceChart;
	    // end: 计算地图的最佳宽高
	    provinceChart = new G2.Chart({
	      container: id,
	      width,
	      height,
	      padding: 0
	    });
	    provinceChart.source(dv);
	    provinceChart.axis(false);
	    provinceChart.tooltip({
	      showTitle: false,
	    });
	    provinceChart
	      .polygon()
	      .position('longitude*lantitude')
	      .label('key')
	      .color('value', '#BAE7FF-#1890FF-#0050B3');
	   
	    provinceChart.render();
	  }
	}

