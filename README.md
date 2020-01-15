[![Docker Layers](https://images.microbadger.com/badges/image/chatopera/contact-center:develop.svg)](https://microbadger.com/images/chatopera/contact-center:develop "Get your own image badge on microbadger.com") [![Docker Version](https://images.microbadger.com/badges/version/chatopera/contact-center:develop.svg)](https://microbadger.com/images/chatopera/contact-center:develop "Get your own version badge on microbadger.com") [![Docker Pulls](https://img.shields.io/docker/pulls/chatopera/contact-center.svg)](https://hub.docker.com/r/chatopera/contact-center/) [![Docker Stars](https://img.shields.io/docker/stars/chatopera/contact-center.svg)](https://hub.docker.com/r/chatopera/contact-center/) [![Docker Commit](https://images.microbadger.com/badges/commit/chatopera/contact-center:develop.svg)](https://microbadger.com/images/chatopera/contact-center:develop "Get your own commit badge on microbadger.com")

<p align="center">
  <b>春松客服QQ交流群：185659917， <a href="https://jq.qq.com/?_wv=1027&k=5I1cJLP" target="_blank">点击链接加入群聊</a></b><br>
  <img src="https://user-images.githubusercontent.com/3538629/44917177-432d9700-ad6a-11e8-9420-46b0281073e6.png" width="200">
</p>

# 春松客服: 多渠道智能客服

春松客服帮助企业快速而低成本的获得好用的智能客服系统。

<img src="https://user-images.githubusercontent.com/3538629/61031891-fc311900-a3f2-11e9-80cf-c8d0700538a0.png" width="600">

春松客服是 Chatopera 自主研发以及基于且增强其它开源软件的方式实现的，春松客服会不断增强客服系统的智能化，这包括利用自然语言处理、机器学习和语音识别等技术让客服工作更有效率、客服满意度更高、成本更低。

**开源项目地址：** [https://github.com/chatopera/cosin](https://github.com/chatopera/cosin)

**开发环境搭建：** [https://github.com/chatopera/cosin/wiki/春松客服：开发环境](https://github.com/chatopera/cosin/wiki/%E6%98%A5%E6%9D%BE%E5%AE%A2%E6%9C%8D%EF%BC%9A%E5%BC%80%E5%8F%91%E7%8E%AF%E5%A2%83)

## 内容结构

|                                                                                     |                                                                                                           |                                                                                                           |
| ----------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------- |
| [产品文档](https://github.com/chatopera/cosin#%E4%BA%A7%E5%93%81%E6%96%87%E6%A1%A3) | [在线培训课程](https://github.com/chatopera/cosin#%E5%9C%A8%E7%BA%BF%E5%9F%B9%E8%AE%AD%E8%AF%BE%E7%A8%8B) | [用户案例](https://github.com/chatopera/cosin#%E7%94%A8%E6%88%B7%E6%A1%88%E4%BE%8B)                       |
| [开发文档](https://github.com/chatopera/cosin#%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3) | [产品截图](https://github.com/chatopera/cosin#%E4%BA%A7%E5%93%81%E6%88%AA%E5%9B%BE)                       | [产品体系](https://github.com/chatopera/cosin#%E4%BA%A7%E5%93%81%E4%BD%93%E7%B3%BB)                       |
| [立即部署](https://github.com/chatopera/cosin#%E7%AB%8B%E5%8D%B3%E9%83%A8%E7%BD%B2) | [鸣谢](https://github.com/chatopera/cosin#%E9%B8%A3%E8%B0%A2)                                             | [开源许可协议](https://github.com/chatopera/cosin#%E5%BC%80%E6%BA%90%E8%AE%B8%E5%8F%AF%E5%8D%8F%E8%AE%AE) |

## 产品演示

- 坐席工作台

[http://cc.chatopera.com/](http://cc.chatopera.com/)

| **登录账号** | **密码**  |
| ------------ | --------- |
| admin        | admin1234 |

- 网页端访客程序

[http://cc.chatopera.com/testclient.html](http://cc.chatopera.com/testclient.html)

## 基础模块

春松客服提供多个开箱即用的供企业免费使用的模块：

- 账号及组织机构管理：按组织、角色分配账号权限

- 坐席监控：设置坐席监控角色的人员可以看到并干预访客会话

- 联系人和客户管理：细粒度维护客户信息，自定义标签和打标签，记录来往历史等

- 网页聊天组件：一分钟接入对话窗口，支持技能组、邀请和关联联系人等

- 坐席工作台：汇聚多渠道访客请求，坐席根据策略自动分配，自动弹屏，转接等

- 机器人客服：集成 Chatopera 云服务，通过插件形式安装，插件也以开源形式提供，[查看插件源码](./public/plugins)。

- 企业聊天：支持企业员工在春松客服系统中群聊和私聊

- 质检：历史会话、服务小结、服务反馈及相关报表

## 付费模块

**春松客服从 v5.0.0 版本开始提供如下付费模块：**

- 呼叫中心：全解决方案，包括呼入和呼出，可选的硬件语音网关，自动外呼，监听和报表等

- Skype 渠道：通过 Skype 账号与用户进行沟通

付费模块代码并不开源，并且使用插件的形式安装在源码中，[请联系“商务洽谈”](https://www.chatopera.com/mail.html)获得插件和商业支持。

## 产品文档

关于产品的具体使用说明，请参考[文档中心](https://docs.chatopera.com/products/cskefu/index.html)。

## 在线培训课程

[春松客服大讲堂](https://ke.qq.com/course/464050)是面向企业 CTO、客服主管、客服及客服系统开发运维人员的在线培训课程，由浅入深的介绍春松客服上线企业智能客服系统、运营客服工作和二次开发的知识。

详情查看[课程介绍及优惠方案](https://github.com/chatopera/cosin/wiki/%E6%98%A5%E6%9D%BE%E5%AE%A2%E6%9C%8D%E5%A4%A7%E8%AE%B2%E5%A0%82)。

<p align="center">
  <b><a href="https://github.com/chatopera/cosin/wiki/%E6%98%A5%E6%9D%BE%E5%AE%A2%E6%9C%8D%E5%A4%A7%E8%AE%B2%E5%A0%82" target="_blank">主讲老师介绍</a></b><br>
  <a href="https://github.com/chatopera/cosin/wiki/%E6%98%A5%E6%9D%BE%E5%AE%A2%E6%9C%8D%E5%A4%A7%E8%AE%B2%E5%A0%82" target="_blank">
      <img src="https://user-images.githubusercontent.com/3538629/71793716-fd2cc580-3078-11ea-99b1-d86dba4475ae.png" width="800">
  </a>
</p>

## 用户案例

<p align="center">
  <b><a href="https://www.chatopera.com/customer.html" target="_blank">TA们都选择春松客服</a></b><br>
  <a href="https://www.chatopera.com/customer.html" target="_blank">
      <img src="https://user-images.githubusercontent.com/3538629/71613944-f7039880-2be3-11ea-8287-9e82f09e436f.png" width="900">
  </a>
</p>

## 社区这样评价春松客服

```
项目代码写的挺好的，容易维护，是不错的开源项目。
```

-- 海洋 (深圳银之杰项目经理)

```
贵公司是我碰到的最有爱的公司啦，这么好的产品授权竟然是Apache。
```

-- 上海某特种气体公司 IT 部客服系统负责人

```
我要在APP内集成，我看了好多项目了，就你们这个最好，基本就是一个商用化的项目。
```

-- Engine X (某二手车出售平台技术负责人)

## 开发文档

<p align="center">
  <b><a href="https://github.com/chatopera/cosin/wiki" target="_blank">开发文档</a></b><br>
  <a href="https://github.com/chatopera/cosin/wiki" target="_blank">
      <img src="https://user-images.githubusercontent.com/3538629/44992890-38be0800-afcb-11e8-8fde-a5a671d29764.png" width="300">
  </a>
</p>

## 产品截图

<p align="center">
  <b>欢迎页</b><br>
  <img src="https://user-images.githubusercontent.com/3538629/44915395-6bff5d80-ad65-11e8-817a-8abb812fb5ee.png" width="900">
</p>

<p align="center">
  <b>坐席工作台</b><br>
  <img src="https://user-images.githubusercontent.com/3538629/44915582-eb8d2c80-ad65-11e8-8876-86c8b5bb5cc7.png" width="900">
</p>

<p align="center">
  <b>坐席监控</b><br>
  <img src="https://user-images.githubusercontent.com/3538629/44915711-432b9800-ad66-11e8-899b-1ea02244925d.png" width="900">
</p>

<p align="center">
  <b>外呼计划</b><br>
  <img src="https://user-images.githubusercontent.com/3538629/44915831-ab7a7980-ad66-11e8-88a5-a2cd23b8c689.png" width="900">
</p>

<p align="center">
  <b>通话记录</b><br>
  <img src="https://user-images.githubusercontent.com/3538629/44915218-feebc800-ad64-11e8-90fc-36ce96b0c09a.png" width="900">
</p>

<p align="center">
  <b>集成客服机器人</b><br>
  <img src="https://user-images.githubusercontent.com/3538629/51080565-4b82df00-1719-11e9-8cc4-dbbec0459224.png" width="900">
</p>

<p align="center">
  <b>客服机器人应答</b><br>
  <img src="https://user-images.githubusercontent.com/3538629/51080567-50479300-1719-11e9-85d8-d209370c9d10.png" width="900">
</p>

<p align="center">
  <b>更多功能，敬请期待 ...</b><br>
  <img src="https://user-images.githubusercontent.com/3538629/44916014-28a5ee80-ad67-11e8-936a-a2cdbe62f529.png" width="900">
</p>

## 产品体系

<p align="center">
  <b>观看视频介绍</b><br>
  <a href="https://pan.baidu.com/s/1tqxqfYSvtjDGhh6bDQ-Vog" target="_blank">
      <img src="https://user-images.githubusercontent.com/3538629/45403926-6a039b80-b68f-11e8-86e2-5d1f04e3a7c7.png" width="900">
  </a>
</p>

## 立即部署

- 公有云版

通过青云 AppCenter 部署，青云 AppCenter 是开发运维一体化(DevOps)管理企业应用的平台，Chatopera 的春松客服在 2018 年 10 月登录 AppCenter，并借助 PaaS 平台强大的计算能力实现计算节点集群、存储节点 HADR。从而保证了服务高可靠性、高性能、动态伸缩、一键备份和一键回滚等功能。

青云 AppCenter 以其提供的资源秒级计算特点，企业使用 AppCenter 中的春松客服应用，可以按需付费，灵活升配和降配，Chatopera 也非常推荐客户使用青云服务。

<p align="center">
  <b>春松客服 on QingCloud</b><br>
  <a href="https://appcenter.qingcloud.com/apps/app-zdh88kz7/%E6%98%A5%E6%9D%BE%E5%AE%A2%E6%9C%8D" target="_blank">
      <img src="https://user-images.githubusercontent.com/3538629/47984143-a17f4900-e110-11e8-95c9-d8302e000c34.png" width="900">
  </a>
</p>

更为详细的部署文档见[春松客服上架青云 AppCenter](https://github.com/chatopera/cosin/wiki/%E6%98%A5%E6%9D%BE%E5%AE%A2%E6%9C%8D%E4%B8%8A%E6%9E%B6%E9%9D%92%E4%BA%91AppCenter)。

- 私有部署版

参考部署[开源社区版本文档](https://github.com/chatopera/cosin/wiki/%E6%9C%8D%E5%8A%A1%E5%99%A8%E9%83%A8%E7%BD%B2)。

## 鸣谢

[FreeSWITCH 中国社区](http://www.freeswitch.org.cn/)

## 开源许可协议

Copyright (2018-2019) <a href="https://www.chatopera.com/" target="_blank">北京华夏春松科技有限公司</a>

[Apache License Version 2.0](https://github.com/chatopera/cosin/blob/master/LICENSE)

[![chatoper banner][co-banner-image]][co-url]

[co-banner-image]: https://user-images.githubusercontent.com/3538629/42383104-da925942-8168-11e8-8195-868d5fcec170.png
[co-url]: https://www.chatopera.com
