function chatoperaLoad(url) {
    function injectLoader(node) {
        var newScript = document.createElement("SCRIPT");
        newScript.innerHTML = node.innerHTML;
        document.getElementsByTagName("HEAD").item(0).appendChild(newScript)
    }

    function append(parent, text) {
        if (typeof text === 'string') {
            var temp = document.createElement('div');
            temp.id = "chatoperaInject";
            temp.innerHTML = text;

            var nodes = temp.getElementsByTagName("script");
            injectLoader(nodes[0]);
            injectLoader(nodes[1]);

            parent.appendChild(temp);
        } else {
            parent.appendChild(text);
        }
    }

    var xhr;
    if (window.XMLHttpRequest) {
        xhr = new XMLHttpRequest();
    } else {
        xhr = new ActiveXObject('Microsoft.XMLHTTP');
    }
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            var status = xhr.status;
            if (status >= 200 && status < 300) {
                append(document.body, xhr.responseText);
            }
        }
    };

    xhr.open('GET', url);
    xhr.send(null);
}
