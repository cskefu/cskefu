/**
 * Created by yangjian on 17-9-18.
 */
(function($) {

	// 设置元素可用状态
	$.fn.enable = function() {
		$(this).addClass("active");
		$(this).removeAttr("disabled");
	}

	// 设置元素不可用状态
	$.fn.disable = function() {
		$(this).removeClass("active");
		$(this).attr("disabled", true);
	}

	var Canvas = function(options) {

		var configs = {
			width : 360,
			height : 300,
			save : function(data) {}
		}; //默认配置
		options = options || {};
		$.extend(configs, options);

		var canvas = $("#"+configs.canvasId)[0]; //画布
		canvas.width = configs.width;
		canvas.height = configs.height;
		var context = canvas.getContext("2d"); //绘图环境
		context.lineCap = "round";　//设置线条两端为圆弧
		context.lineJoin = "round";　//设置线条转折为圆弧
		//设置默认颜色
		setColor();
		var $prevBtn = $("#J_prevStep"); //上一步
		var $nextBtn = $("#J_nextStep"); //下一步
		var $clearBtn = $("#J_clearBoard"); //清空画板
		var drawing = false; //是否正在绘制
		var erasering = false; //是否正在擦除
		var prevSteps = []; //返回上一步操作集合
		var nextSteps = []; //恢复下一步操作集合
		var o = {}; //导出api对象

		$("#picBoard").css({
			width : configs.width + "px",
			height : configs.height + "px"
		});

		// 事件绑定
		canvas.onmousedown = startDrawing;
		canvas.onmouseup = stopDrawing;
		canvas.onmouseout = stopDrawing;
		canvas.onmousemove = doDrawing;
		$prevBtn.on("click", gotoPrevStep);
		$nextBtn.on("click", gotoNextStep);
		$clearBtn.on("click", clearBoard);

		// 清空设置
		$("#clearSetting").on("click", function() {
			context.lineWidth = 1;
			setColor($(".colorBar span:first").data("color"));
			
			context.shadowBlur = 0;
			alert("画笔已重新初始化，请重新配置画笔。");
		});

		//上传背景图片
		$("#J_canvas_bg").on("change", function() {

			if ($("#picBoard img").length > 0) {
				$("#picBoard img:eq(0)").attr("src", window.URL.createObjectURL(this.files[0]));
				return;
			}
			var $img = '<img src="'+window.URL.createObjectURL(this.files[0])+'" width="'+configs.width+'" height="'+configs.height+'" />';
			$("#picBoard").append($img);

			// 激活删除背景按钮
			$("#J_removeImg").enable();

		});

		// 删除背景图片
		$("#J_removeImg").on("click", function() {
			$("#picBoard").empty();
			$(this).disable();
		});

		//保存图片
		$('#J_saveImg').on("click", saveImage);

		// 设置笔刷大小
		$("#scrawl-main .brush-size").on("click", function() {

			context.restore(); //恢复到canvas的上一个状态
			context.lineWidth = parseInt($(this).text());
			erasering = false;

		});

		// 设置笔触虚化
		$("#scrawl-main .blur-size").on("click", function() {
			context.shadowBlur = parseInt($(this).text());
		});

		// 橡皮擦功能
		$("#scrawl-main .eraser-size").on("click", function() {

			if (erasering == true) {
				return;
			}
			erasering = true;
			context.save(); //保存canvas状态
			context.lineCap = "round";　//设置线条两端为圆弧
			context.lineJoin = "round";　//设置线条转折为圆弧
			context.lineWidth = 10;
			context.globalCompositeOperation = "destination-out";

		});

		//设置颜色
		$("#scrawl-main .colorBar span").on("click",function() {

			$("#scrawl-main .colorBar .active").removeClass("active");
			$(this).addClass("active");
			setColor($(this).data("color"));
		});

		// 开始绘制
		function startDrawing(e) {
			drawing = true;
			//记录上一步的数据
			prevSteps.push(context.getImageData(0, 0, configs.width, configs.height));
			// 创建一个新的绘图路径
			context.beginPath();
			// 把画笔移动到鼠标位置
			var offset = $(canvas).offset();
			context.moveTo(e.pageX - offset.left, e.pageY - offset.top);
		}

		// 停止绘制
		function stopDrawing() {

			//清空下一步的数据集合，从新开始记录
			nextSteps = [];
			if(drawing != false){
				canvas.toBlob(function(blob) {
					sendImageData(blob) ;
				}, 'image/png') ;
			}
			drawing = false;
			$nextBtn.disable();
			if (prevSteps.length == 1) {
				$prevBtn.enable();
				$clearBtn.enable();
			}
		}
		
		function sendImageData(blob){
			var fd = new FormData();
			fd.append('image', blob);
			$.ajax({
			    type: 'POST',
			    url: '/agent/message/image/upload.html?id='+msgid+"&fileid="+fileid,
			    data: fd,
			    processData: false,
			    contentType: false
			}).done(function(data) {
			       console.log("发送数据");
			});
		}

		//绘制图像
		function doDrawing(e) {
			if (drawing) {
				// 找到鼠标最新位置
				var offset = $(canvas).offset();
				var x = e.pageX - offset.left;
				var y = e.pageY - offset.top;
				// 画一条直线到鼠标最新位置
				context.lineTo(x, y);
				context.stroke();
			}
		}

		/**
		 * 返回上一步操作
		 */
		function gotoPrevStep() {
			if (prevSteps.length > 0) {
				//保存当前状态到下一步的操作历史库
				nextSteps.push(context.getImageData(0, 0, configs.width, configs.height));
				var popData = prevSteps.pop();
				context.putImageData(popData, 0, 0);
				$nextBtn.enable();

				if (prevSteps.length == 0) {
					$prevBtn.disable();
				}
			}
		}

		/**
		 * 恢复下一步操作
		 */
		function gotoNextStep() {
			if (nextSteps.length > 0) {
				//保存当前状态到上一步的操作历史库
				prevSteps.push(context.getImageData(0, 0, configs.width, configs.height));
				var imgData = nextSteps.pop();
				context.putImageData(imgData, 0, 0);
				$prevBtn.enable();

				if (nextSteps.length == 0) {
					$nextBtn.disable();
				}
			}
		}

		/**
		 * 清空画板
		 */
		function clearBoard() {
			context.clearRect(0, 0, context.canvas.width, context.canvas.height);
			prevSteps = [];
			nextSteps = [];

			$prevBtn.disable();
			$nextBtn.disable();
			$clearBtn.disable();
			
			canvas.toBlob(function(blob) {
				sendImageData(blob) ;
			}, 'image/png') ;
		}

		/**
		 * 设置画笔颜色
		 * @param color
		 */
		function setColor(color) {
			if (!color) {
				color = $(".colorBar .active:eq(0)").data("color");
			}
			context.strokeStyle = color;
			context.shadowColor = color;
		}

		/**
		 * 保存图片
		 */
		function saveImage() {

			if ($("#picBoard img").length > 0) {
				var image = new Image();
				image.src = $("#picBoard img:eq(0)").attr("src");
				image.onload = function() {
					context.save();
					context.shadowBlur = 0;
					context.shadowColor = '#FFF';
					context.globalCompositeOperation = "destination-atop";
					context.drawImage(this, 0, 0, configs.width, configs.height);
					context.restore();

					configs.save(canvas.toDataURL("image/png"));
				}

			} else {
				configs.save(canvas.toDataURL("image/png"));
			}
		}


		//要导出的API
		o.nextStep = gotoNextStep;
		o.prevStep = gotoNextStep;
		o.setColor = setColor;
		o.save = saveImage;
		return o;
	}


	window.Canvas = Canvas;
})(jQuery);