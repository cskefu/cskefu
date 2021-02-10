 <div class="clearfix chat-block" <%if(data.type == 'writing'){%>id="writing"<%}%>>
    <div class="<% if(data.calltype == '呼出'){%>chat-right<%}else{%>chat-left<%}%>">
        <img alt="" src="<% if(data.headimgurl && data.headimgurl !=''){%><%=data.headimgurl%><%}else{%>/im/img/user.png<%} %>" class="user-img" style="width:45px;height:45px;">
        <div class="chat-message">
            <% if(data.userid == data.cususerid) {%>
            <span class="<% if(data.calltype == '呼出'){%>rateright<%}else{%>rateleft<%}%>"><i class="layui-icon iconclick" id=<%=data.id%>  name="nolabe" style="cursor:pointer;font-size: 30px; color: #aaaaaa;">&#x1005;</i></span>
            <span class="time"><%=data.createtime%></span>
            <span class="user"><%=data.username%></span>
            <span class="<% if(data.calltype == '呼出'){%>rateleft<%}else{%>rateright<%}%>"><i class="layui-icon iconclick" id=<%=data.id%>  name="nolabe"  style="cursor:pointer;font-size: 30px; color: #aaaaaa;">&#x1005;</i></span>
            <% }else{%>
            <span class="<% if(data.calltype == '呼出'){%>rateright<%}else{%>rateleft<%}%>"><i class="layui-icon iconclick" id=<%=data.id%>  name="nolabe" style="cursor:pointer;font-size: 30px; color: #aaaaaa;">&#x1005;</i></span>
            <span class="user"><% if(data.intervented && data.supervisorname){%><%=data.supervisorname%><%}else{%><%=data.username%><%}%></span>
            <span class="time"><%=data.createtime%></span>
            <span class="<% if(data.calltype == '呼出'){%>rateleft<%}else{%>rateright<%}%>"><i class="layui-icon iconclick" id=<%=data.id%>  name="nolabe" style="cursor:pointer;font-size: 30px; color: #aaaaaa;">&#x1005;</i></span>
            <%}%>
        </div>
        <div class="<% if(data.calltype == '呼出'){%>chatting-right<%}else{%>chatting-left<%}%>">
            <i class="userarrow"></i>
            <div class="chat-content">
                <% if(data.msgtype == 'image'){ %>
                <a href="/agent/message/image.html?id=<%:=data.id%>" data-toggle="ajax" data-width="950" data-height="600" title="图片"><img src="<%:=data.message%>" class="ukefu-media-image" id="<%:=data.id%>"></a>
                <% }else if(data.msgtype == 'cooperation'){ %>
                <a href="/agent/message/image.html?t=review&id=<%:=data.message%>" data-toggle="ajax" data-width="950" data-height="600"  title="图片">
                    系统发送了一个协作邀请
                </a>
                <% }else if(data.msgtype == 'file'){ %>
                <div class="ukefu-message-file">
                    <div class="ukefu-file-icon">
                        <i class="kfont">&#xe61e;</i>
                    </div>
                    <div class="ukefu-file-desc">
                        <a href="<%:=data.message%>" target="_blank">
                            <div><%:=data.filename%></div>
                            <div><%:=(data.filesize/1024).toFixed(3)%>Kb</div>
                        </a>
                    </div>
                </div>
                <% }else if(data.msgtype == 'location'){ %>
                <div><%:=data.message%></div>
                <div class="ukefu-map" id="map_<%:=data.id%>"></div>
                <script type='text/javascript'>
                    var map = new BMap.Map("map_<%:=data.id%>");
                    var ggPoint = new BMap.Point(<%:=data.locy%>, <%:=data.locx%>);
                    MapUtil.convert(map,ggPoint , "<%:=data.message%>" , <%:=data.scale%>) ;
                    <\/script>

                    <% }else if(data.msgtype == 'voice'){ %>
                    <p class="weixinAudio" id="voice_media_<%:=data.id%>" style="<% if(data.duration > 30) {%>width:300px;<% }else{ %>width:<%:=50+data.duration* 10%>px;<%}%>">
                            <audio src="<%:=data.message%>" id="media" width="1" height="1" preload></audio>
                        <span id="audio_area" class="db audio_area"  title="<%:=data.expmsg%>">
                            <span class="audio_wrp db">
                            <span class="audio_play_area">
                            <i class="icon_audio_default"></i>
                            <i class="icon_audio_playing"></i>
                            </span>
                            <span class="audio_length tips_global"><%:=data.duration %>秒</span>
                            <span id="audio_progress" class="progress_bar" style="width: 0%;"></span>
                            </span>
                            </span>
                            </p>
                            <% if(data.expmsg != ''){ %>
                        <div class="ukefu-asr"><%:=data.expmsg%></div>
                                <%}%>
                    <script type='text/javascript'>
                            $('#voice_media_<%:=data.id%>').weixinAudio({
                                autoplay:false
                            });
                    <\/script>
                        <%}else{%>
                    <%:=data.message%>
                            <%}%>
                </div>
            </div>
        </div>
    </div>