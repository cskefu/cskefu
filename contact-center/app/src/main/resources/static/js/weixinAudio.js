(function() {
	$.fn.weixinAudio = function(options) {
		var $this = $(this);
		var defaultoptions = {
			autoplay:false,
			src:'',
		};
		function Plugin($context) {
			//dom
			this.$context = $context;

			this.$Audio = $context.children('#media');
			this.Audio = this.$Audio[0];
			this.$audio_area = $context.find('#audio_area');
			this.$audio_length = $context.find('#audio_length');
			this.$audio_progress = $context.find('#audio_progress');
			//属性
			this.currentState = 'pause';
			this.time = null;
			this.settings = $.extend(true, defaultoptions, options);
			//执行初始化
			this.init();
		}
		Plugin.prototype = {
			init: function() {
				var self = this;
				self.updateTotalTime();
				self.events();
				// 设置src
				if(self.settings.src !== ''){
						self.changeSrc(self.settings.src);
				}
				// 设置自动播放
				if(self.settings.autoplay){
					self.play();
				}
			},
			play: function() {
				var self = this;
				if (self.currentState === "play") {
					self.pause();
					return;
				}
				self.Audio.play();
				clearInterval(self.timer);
				self.timer = setInterval(self.run.bind(self), 50);
				self.currentState = "play";
				self.$audio_area.addClass('playing');
			},
			pause: function() {
				var self = this;
				self.Audio.pause();
				self.currentState = "pause";
				clearInterval(self.timer);
				self.$audio_area.removeClass('playing');
			},
			stop:function(){

			},
			events: function() {
				var self = this;
				var updateTime;
				self.$audio_area.on('click', function() {
					self.play();
					if (!updateTime) {
						self.updateTotalTime();
						updateTime = true;
					}
				});
			},
			//正在播放
			run: function() {
				var self = this;
				self.animateProgressBarPosition();
				if (self.Audio.ended) {
					self.pause();
				}
			},
			//进度条
			animateProgressBarPosition: function() {
				var self = this,
					percentage = (self.Audio.currentTime * 100 / self.Audio.duration) + '%';
				if (percentage == "NaN%") {
					percentage = 0 + '%';
				}
				var styles = {
					"width": percentage
				};
				self.$audio_progress.css(styles);
			},
			//获取时间秒
			getAudioSeconds: function(string) {
				var self = this,
					string = string % 60;
				string = self.addZero(Math.floor(string), 2);
				(string < 60) ? string = string: string = "00";
				return string;
			},
			//获取时间分
			getAudioMinutes: function(string) {
				var self = this,
					string = string / 60;
				string = self.addZero(Math.floor(string), 2);
				(string < 60) ? string = string: string = "00";
				return string;
			},
			//时间+0
			addZero: function(word, howManyZero) {
				var word = String(word);
				while (word.length < howManyZero) word = "0" + word;
				return word;
			},
			//更新总时间
			updateTotalTime: function() {
				var self = this,
					time = self.Audio.duration,
					minutes = self.getAudioMinutes(time),
					seconds = self.getAudioSeconds(time),
					audioTime = minutes + ":" + seconds;
				self.$audio_length.text(audioTime);
			},
			//改变音频源
			changeSrc:function(src,callback){
				var self = this;
				self.pause();
				self.Audio.src = src;
				self.play();
				callback();
			},
		};
		var obj = {}
		// var instantiate = function() {
		// 	 new Plugin($(this));
		// }
		$this.each(function(index,element){
			obj['weixinAudio'+index] = new Plugin($(this));
		}); //多个执行返回对象

		return obj
	}
})(jQuery)