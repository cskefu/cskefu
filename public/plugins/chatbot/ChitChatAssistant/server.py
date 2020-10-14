"""
    server.py
    ~~~~~~~~~

    web server，定义前端调用接口

    :date: 2020-02-14 14:36:00
    :author: by jiangdg
"""

from flask import Flask, jsonify
from flask import request
import requests
import json
import logging

app = Flask(__name__)


@app.route('/', methods=['GET', 'POST'])
def test():
    return"测试"


@app.route('/ai', methods=['GET', 'POST'])
def webToBot():
    """
    前端调用接口
        路径：/ai
        请求方式：GET、POST
        请求参数：content
    :return: response rasa响应数据
    """
    content = request.values.get('content')
    if content is None:
        return 'empty input'
    response = requestRasabotServer('jiangdg', content)
    return response.text.encode('utf-8').decode("unicode-escape")


def requestRasabotServer(userid, content):
    """
        访问rasa服务
    :param userid: 用户id
    :param content: 自然语言文本
    :return:  json格式响应数据
    """
    params = {'sender': userid, 'message': content}
    botIp = '127.0.0.1'
    botPort = '5005'
    # rasa使用rest channel
    # https://rasa.com/docs/rasa/user-guide/connectors/your-own-website/#rest-channels
    # POST /webhooks/rest/webhook
    rasaUrl = "http://{0}:{1}/webhooks/rest/webhook".format(botIp, botPort)

    reponse = requests.post(
        rasaUrl,
        data=json.dumps(params),
        headers={'Content-Type': 'application/json'}
    )
    return reponse


if __name__ == '__main__':
    webIp = '127.0.0.1'
    webPort = '8088'

    print("##### webIp={}, webPort={}".format(webIp, webPort))
    # 初始化日志引擎
    fh = logging.FileHandler(encoding='utf-8', mode='a', filename='chitchat.log')
    logging.basicConfig(
        handlers=[fh],
        level=logging.INFO,
        format='%(asctime)s - %(levelname)s - %(message)s',
        datefmt='%a, %d %b %Y %H:%M:%S',
    )

    # 启动服务，开启多线程、debug模式
    # 浏览器访问http://127.0.0.1:8088/ai?content="你好"
    app.run(
        host=webIp,
        port=int(webPort),
        threaded=True,
        debug=True
    )
