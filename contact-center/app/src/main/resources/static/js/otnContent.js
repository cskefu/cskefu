(function ($) {
    $.fn.otnContent = function () {
        this.each(function () {
            var $this = $(this);
            if ($this.attr('value')) {
                var json = JSON.parse($this.attr('value'));
            } else {
                var json = {"type": "text", "content": "", "url": "/images/upload.png"}
            }
            var box = $('<div style="width: 310px;height: 135px;"></div>')
            var jsonInput = $('<input type="hidden" name="' + $this.attr('name') + '" value="' + json + '"/>')
            var select = $('<select style="width: 312px;display: block;margin-bottom: 2px"><option value="text">文本</option><option value="image">图片</option></select>')
            var textInput = $('<textarea style="width: 310px;height: 130px;border: 1px solid #ccc;resize:none;">' + json.content + '</textarea>')
            var imageBox = $('<div style="width: 100%;height: 130px;position: relative; border: 1px dashed #ccc"></div>')
            var imageInput = $(' <input type="file" style="width: 100%;height: 100%;position: absolute;top:0;left: 0;opacity: 0" accept="image/*"/>')
            var img = $('<img style="width: auto;margin: 0 auto;display: block" src="' + json.url + '"/>')
            var uploadText = $('<div style="width: 100%;height: 30px;text-align: center;color: #666">点击上传</div>')
            imageBox.append(imageInput)
            imageBox.append(img)
            imageBox.append(uploadText)
            box.append(textInput)
            box.append(imageBox)
            if (json.url == '/images/upload.png') {
                img.css('height', '100px');
                uploadText.show();
            } else {
                img.css('height', '130px');
                uploadText.hide();
            }
            if (json.type == 'text') {
                imageBox.hide()
                select.val("text");
            } else {
                textInput.hide()
                select.val("image");
            }
            imageInput.change(function () {
                var form = new FormData();
                form.append('imgFile', this.files[0]);
                $.ajax({
                    type: 'POST',
                    url: "/apps/messenger/otn/image/upload.html",
                    data: form,
                    processData: false,
                    contentType: false
                }).done(function (data) {
                    img.attr('src', data.url);
                    uploadText.hide();
                    json.url = data.url;
                    jsonInput.val(JSON.stringify(json));
                    jsonInput.trigger("change");
                    img.css('height', '130px');
                });
            });
            textInput.bind("input propertychange", function (event) {
                json.content = textInput.val();
                jsonInput.val(JSON.stringify(json));
                jsonInput.trigger("change");
            });
            select.change(function () {
                if (select.val() == 'image') {
                    textInput.hide();
                    imageBox.show()
                    json.type = 'image'
                } else {
                    textInput.show();
                    imageBox.hide();
                    json.type = 'text'
                }
                jsonInput.val(JSON.stringify(json));
                jsonInput.trigger("change");
            })
            jsonInput.val(JSON.stringify(json));
            jsonInput.trigger("change");
            $this.append(jsonInput)
            $this.append(select)
            $this.append(box)
        })
    };
})(jQuery);