<div class="clearfix chat-block" <%if(data.type == 'writing'){%>id="writing"<%}%>>
    <div class="<% if(data.calltype == '呼出'){%>chat-left<%}else{%>chat-right<%}%>">
        <img alt="" src="<% if(data.calltype == '呼出'){%>/images/agent.png<%}else{%><% if(data.headimgurl && data.headimgurl !=''){%><%=data.headimgurl%><%}else{%>/im/img/user.png<%} %><%} %>" class="user-img" style="width:45px;height:45px;">
        <div class="chat-message">
            <span class="user"><%=data.username%></span>
            <span class="time"><%=data.createtime%></span>
        </div>
        <div class="<% if(data.calltype == '呼出'){%>chatting-left<%}else{%>chatting-right<%}%>">
            <i class="arrow"></i>
            <%if(data.type == 'writing'){%>
                <div class="chat-writing-message" title="正在输入...">
                    <span class="loading">
                        <span></span>
                        <span></span>
                        <span></span>
                        <span></span>
                    </span>
                </div>
            <%}%>
        </div>
    </div>
</div>