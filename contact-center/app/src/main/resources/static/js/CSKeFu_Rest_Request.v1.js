// 统一处理请求restapi
function httpRequest(opts) {
    var postfix = '/api/';

    var urlPath = opts.path || '';
    var query = opts.query || '';

    var apiHost = window.location.origin;

    var payload = {};
    payload.url = apiHost + postfix + urlPath + query;
    payload.method = payload.method || 'POST';
    payload.contentType = 'application/json;charset=UTF-8';
    payload.headers = {
        authorization: $.cookie('authorization')
    };
    payload.dataType = 'json';

    payload.data = JSON.stringify(opts.data);

    var index = layer.msg('执行中，请稍候',{icon: 16,time:false,shade:0.8});

    return new Promise(function(resolve, reject) {
        $.ajax(payload)
            .done(function (data) {
                console.log('Rest api 返回的值：', data);
                layer.close(index);
                resolve(data);
            })
            .fail(function (jqXHR, textStatus ) {
                console.error('Rest api 返回error：', jqXHR);
                layer.close(index);
                reject(jqXHR)
            });

    });
}

// 操作成功的
function openSucc(msg) {
    layer.open({
        content: msg || '操作成功',
        icon: 1,
        skin: 'demo-class',
        success: function(layero, index){
            console.log(layero, index);
        }
    });
}

// 操作失败的
function openFail(status, reason) {
    if(status && status == 'AUTH_ERROR'){
        layer.confirm('会话过期，请重新登录！', {
            btn: ['是', '否'],
            icon: 2,
            title:'提示'
        }, function(index, layero){
            layer.close(index)
            top.location.href='/logout.html'
        }, function(index){
            // 取消方法
        });
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