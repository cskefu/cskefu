/**
 *权限数据采集 
 */
$(document).ready(function(){
	$('.ukefu-phone-number').addClass("ukefu-phone-class");
	$('.ukefu-phone-number').on("click" , function(){
		var phonenumber = $(this).text().trim();
		if(phonenumber != ''){
			top.layer.confirm('请确认是否拨打号码 ‘'+phonenumber+'’', {icon: 3, title:'拨打号码'}, function(index){
				top.layer.close(index);
				top.uKeFuSoftPhone.invite(phonenumber);
			});
		}
	});
});