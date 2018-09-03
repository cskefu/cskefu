/**
 *权限数据采集 
 */
$(document).ready(function(){
	$('a').on("click" , function(){
		var title = $(this).data("title");
		var child = $(this).find("i");
		var iconstr , icontext;
		if(child.length > 0){
			iconstr = child[0].outerHTML ;
			icontext = child[0].innerHTML ; 
		}
		if(typeof title == "undefined"){
			title = $(this).text();
		}
		var href = $(this).data("href");
		if(typeof href == "undefined"){
			href = $(this).attr("href");
		}
		$.ajax({
			url:"/admin/auth/event.html?title="+encodeURIComponent(title)+"&url="+encodeURIComponent(href)+"&iconstr="+iconstr+"&icontext="+encodeURIComponent(icontext),
			cache:false,
			success: function(data){
				top.layer.open({
					title: "权限数据采集", 
					type: 1, 
					maxmin: true, 
					anim: 2,
					id: 'mainajaxwin', 
					area : [ "750px", "400px" ],
					content: data}
				);
			}
		});
	});
});