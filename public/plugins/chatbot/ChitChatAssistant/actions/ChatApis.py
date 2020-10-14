# 访问图灵机器人openapi
# -*- coding: utf-8 -*-
"""
    ChatApis.py
    ~~~~~~~~~

    图灵机器人(公司)闲聊系统API对接
    免费版只限每天调用100次，需联外网

    :date: 2020-02-10 15:56:00
    :author: by jiangdg
"""
import requests
import json


def get_response(msg):
    """
        访问图灵机器人openApi

        :param msg 用户输入的文本消息
        :return string or None
    """
    apiurl = "http://openapi.tuling123.com/openapi/api/v2"
    # 构造请求参数实体
    params = {"reqType": 0,
              "perception": {
                  "inputText": {
                      "text": msg
                  }
              },
              "userInfo": {
                  "apiKey": "ca7bf19ac0e644c38cfbe9d6fdc08de1",
                  "userId": "439608"
              }}
    # 将表单转换为json格式
    content = json.dumps(params)

    # 发起post请求
    r = requests.post(url=apiurl, data=content, verify=False).json()
    print("r = " + str(r))

    # 解析json响应结果
    # {'emotion':{
    #               'robotEmotion': {'a': 0, 'd': 0, 'emotionId': 0, 'p': 0},
    #               'userEmotion': {'a': 0, 'd': 0, 'emotionId': 10300, 'p': 0}
    #            },
    #  'intent': {
    #       'actionName': '',
    #       'code': 10004,
    #       'intentName': ''
    #       },
    #  'results': [{'groupType': 1, 'resultType': 'text', 'values': {'text': '欢迎来到本机器人的地盘。'}}]}
    code = r['intent']['code']
    if code == 10004 or code == 10008:
        message = r['results'][0]['values']['text']
        return message
    return None

