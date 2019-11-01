# cc-chatbot
Chatopera智能问答引擎的Java SDK.
https://docs.chatopera.com/

支持

* 创建聊天机器人
* 查询聊天机器人列表
* 更新聊天机器人画像
* 查询聊天机器人使用情况
* 管理和检索多轮对话
* 管理和检索知识库
* 检索意图识别


# 配置

使用maven，需要配置Chatopera的Nexus OSS仓库，具体见[文档](https://github.com/chatopera/cosin/wiki/%E6%98%A5%E6%9D%BE%E5%AE%A2%E6%9C%8D%EF%BC%9A%E5%BC%80%E5%8F%91%E7%8E%AF%E5%A2%83#%E4%BF%AE%E6%94%B9maven2%E9%85%8D%E7%BD%AE)。

```
<dependency>
    <groupId>com.chatopera.chatbot</groupId>
    <artifactId>sdk</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

# API

## Chatbot v1

聊天机器人类，构造参数(tcp协议，hostname, 端口, 版本)

### Chatbot#getChatbots
获取聊天机器人列表，支持检索查询，分页

### Chatbot#getChatbot
通过聊天机器人ID获得聊天机器人详情

### Chatbot#conversation
与指定的聊天机器人进行多轮对话

### Chatbot#faq
与指定的聊天机器人进行知识库问答


# 测试

```
mvn test
```

# 示例

```
Chatbot cb = new Chatbot("http", "lhc-dev", 8003, "v1");
JSONObject resp = cb.conversation("co_bot_1", "sdktest", "华夏春松在哪里", false);
```

返回值参考 [智能问答引擎文档](https://docs.chatopera.com/chatbot-engine.html)。


## 开源许可协议

Copyright (2018) <a href="https://www.chatopera.com/" target="_blank">北京华夏春松科技有限公司</a>

[Apache License Version 2.0](https://github.com/chatopera/cosin/blob/master/LICENSE)

[![chatoper banner][co-banner-image]][co-url]

[co-banner-image]: https://user-images.githubusercontent.com/3538629/42383104-da925942-8168-11e8-8195-868d5fcec170.png
[co-url]: https://www.chatopera.com

