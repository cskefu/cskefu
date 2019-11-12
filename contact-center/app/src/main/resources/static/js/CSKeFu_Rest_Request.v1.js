// 获得cookie
function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++)
    {
        var c = ca[i].trim();
        if (c.indexOf(name)==0) return c.substring(name.length,c.length);
    }
    return "";
}

// 统一处理请求restapi
function restApiRequest(opts) {
    var postfix = '/api/';

    var urlPath = opts.path || '';
    var query = opts.query || '';
    var silent = opts.silent || false;

    var apiHost = window.location.origin;

    var payload = {};
    payload.url = apiHost + postfix + urlPath + query;
    payload.method = payload.method || 'POST';
    payload.contentType = 'application/json;charset=UTF-8';
    payload.headers = {
        authorization: getCookie('authorization').trim()
    };
    payload.dataType = 'json';
    payload.data = JSON.stringify(opts.data);
    var popup = null;

    if(!silent){
        popup = layer.msg('执行中，请稍候',{icon: 16,time:false,shade:0.8});
    }

    return new Promise(function(resolve, reject) {
        $.ajax(payload)
            .done(function (data) {
                // console.log('Rest api 返回的值：', data);
                if(!silent) layer.close(popup);
                if(data.status){
                    // not reject or resolve, expected user login again.
                    return handleRestApiFail(data.status);
                }
                resolve(data);
            })
            .fail(function (jqXHR, textStatus ) {
                console.error('Rest api 返回error：', jqXHR);
                if(!silent) layer.close(popup);
                reject(jqXHR)
            });

    });
}

// 操作成功的
function handleRestApiSucc(msg) {
    layer.msg( msg || '操作成功',{icon: 1, time: 1000})
}

// 操作失败的
function handleRestApiFail(status, reason) {
    if(status && status === 'AUTH_ERROR'){
        layer.msg('会话过期，请重新登录！',{icon: 2, time: 3000});
        setTimeout(function(){
            // 执行登出
            window.location.href = "/logout.html";
        }, 3000);
    } else  {
        layer.open({
            content: reason || '操作失败',
            icon: 2,
            skin: 'demo-class',
            error: function(layero, index){
                console.log(layero, index);
            }
        });
    }
}
