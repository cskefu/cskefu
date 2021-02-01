[![Docker Layers](https://images.microbadger.com/badges/image/chatopera/contact-center:develop.svg)](https://microbadger.com/images/chatopera/contact-center:develop "Get your own image badge on microbadger.com") [![Docker Version](https://images.microbadger.com/badges/version/chatopera/contact-center:develop.svg)](https://microbadger.com/images/chatopera/contact-center:develop "Get your own version badge on microbadger.com") [![Docker Pulls](https://img.shields.io/docker/pulls/chatopera/contact-center.svg)](https://hub.docker.com/r/chatopera/contact-center/) [![Docker Stars](https://img.shields.io/docker/stars/chatopera/contact-center.svg)](https://hub.docker.com/r/chatopera/contact-center/) [![Docker Commit](https://images.microbadger.com/badges/commit/chatopera/contact-center:develop.svg)](https://microbadger.com/images/chatopera/contact-center:develop "Get your own commit badge on microbadger.com")

# 春松客服: 多渠道智能客服

[https://www.cskefu.com](https://www.cskefu.com/)

快速获得好用的开源智能客服系统。

[CSKeFu](https://github.com/chatopera/cskefu) is a Customer Support System for Enterprises in a High Performance Low-Touch way, open source for the world by [Chatopera Inc](https://www.chatopera.com/).

<img src="https://static-public.chatopera.com/assets/images/cskefu/cskefu-yellow-bg.png" width="600">

春松客服是 Chatopera 自主研发以及基于且增强其它开源软件的方式实现的，春松客服会不断增强客服系统的智能化，这包括利用自然语言处理、机器学习和语音识别等技术让客服工作更有效率、客服满意度更高。

**开源项目地址：** [Gitee](https://gitee.com/chatopera/cskefu) | [CodeChina](https://codechina.csdn.net/chatopera/cskefu) | [GitHub](https://github.com/chatopera/cskefu)

**开发环境搭建：** [https://docs.chatopera.com/products/cskefu/osc/engineering.html](https://docs.chatopera.com/products/cskefu/osc/engineering.html)

**官方博客：** [https://blog.chatopera.com/](https://blog.chatopera.com/)

**产品更新：** [观看 v6 版本介绍视频](https://www.bilibili.com/video/BV1Tf4y1q7us/) | [其它更新日志](./CHANGELOG.md)

## 媒体报道

<img src="https://static-public.chatopera.com/assets/images/cskefu/cskefu-gpv-2.png" height = "220" div align=right />

- [GitHub 上获点赞最多的开源客服系统春松客服入驻 CODE CHINA](https://mp.weixin.qq.com/s/wGCFj9Hs1uVuTQCTqH0IWg)

- [Coscon'19 中国开源年会春松客服主题分享](https://www.shangyexinzhi.com/article/351121.html)

- [春松客服荣获 GVP 企业级开源项目认证](http://www.ctiforum.com/news/guonei/578988.html)

---

## 产品演示

### 坐席工作台

[https://cc.chatopera.com/](https://cc.chatopera.com/)

| **登录账号** | **密码**  |
| ------------ | --------- |
| admin        | admin1234 |

### 网页端访客程序

[https://cc.chatopera.com/testclient.html](http://cc.chatopera.com/testclient.html)

## 用户案例

<p align="center">
  <b><a href="https://www.chatopera.com/customer.html" target="_blank">TA们都选择春松客服</a></b><br>
  <a href="https://www.chatopera.com/customer.html" target="_blank">
      <img src="https://static-public.chatopera.com/assets/images/71613944-f7039880-2be3-11ea-8287-9e82f09e436f.png" width="900">
  </a>
</p>

### 企业用户这样评价我们

```
The collaboration is efficient, very professional. The software is steady with high quality services.
```

-- Michael, Founder & CEO, [麥睿資訊](https://www.maideax.com/)

```
系统功能强大，免费且响应及时，太赞了！贵公司是我碰到的最有爱的公司啦，这么好的产品授权竟然是Apache。
```

-- 小赵，技术员，上海某特种气体公司 IT 部客服系统负责人

```
技术支持力度大，产品更新很快！
```

-- 阿伟，电商客服技术总监，某跨境电商企业

## 功能介绍

### 基础模块

春松客服提供多个开箱即用的供企业免费使用的模块：

- 账号及组织机构管理：按组织、角色分配账号权限

- 坐席监控：设置坐席监控角色的人员可以看到并干预访客会话

- 联系人和客户管理：细粒度维护客户信息，自定义标签和打标签，记录来往历史等

- 网页聊天组件：一分钟接入对话窗口，支持技能组、邀请和关联联系人等

- 坐席工作台：汇聚多渠道访客请求，坐席根据策略自动分配，自动弹屏，转接等

- 机器人客服：集成 [Chatopera 云服务](https://bot.chatopera.com)，利用 Chatopera 强大的聊天机器人解决方案，提升客户服务工作中的自动化、智能化；机器人客服插件既能通过知识库联想，知识库快捷支持坐席人员，也可以直接为访客提供查询、数据收集等功能；通过插件形式安装，插件也以开源形式提供，[查看插件源码](./public/plugins)。

- 企业聊天：支持企业员工在春松客服系统中群聊和私聊

- 质检：历史会话、服务小结、服务反馈及相关报表

### 付费模块

**春松客服从 v5.0.0 版本开始提供如下付费模块：**

- 呼叫中心：全解决方案，包括呼入和呼出，可选的硬件语音网关，自动外呼，监听和报表等

- Skype 渠道：通过 Skype 账号与用户进行沟通

付费模块代码并不开源，并且使用插件的形式安装在源码中，[请联系“商务洽谈”](https://www.chatopera.com/mail.html)获得插件和商业支持。

## 产品文档

关于产品的具体使用说明，请参考[文档中心](https://docs.chatopera.com/products/cskefu/index.html)。

### 产品截图

<p align="center">
  <b>欢迎页</b><br>
  <img src="https://static-public.chatopera.com/assets/images/cskefu/cskefu-screen-1.jpg" width="900">
</p>

<details>
<summary>展开查看更多产品截图</summary>
<p>

<p align="center">
  <b>坐席工作台</b><br>
  <img src="https://static-public.chatopera.com/assets/images/44915582-eb8d2c80-ad65-11e8-8876-86c8b5bb5cc7.png" width="900">
</p>

<p align="center">
  <b>坐席监控</b><br>
  <img src="https://static-public.chatopera.com/assets/images/44915711-432b9800-ad66-11e8-899b-1ea02244925d.png" width="900">
</p>

<p align="center">
  <b>外呼计划</b><br>
  <img src="https://static-public.chatopera.com/assets/images/44915831-ab7a7980-ad66-11e8-88a5-a2cd23b8c689.png" width="900">
</p>

<p align="center">
  <b>通话记录</b><br>
  <img src="https://static-public.chatopera.com/assets/images/44915218-feebc800-ad64-11e8-90fc-36ce96b0c09a.png" width="900">
</p>

<p align="center">
  <b>集成客服机器人</b><br>
  <img src="https://static-public.chatopera.com/assets/images/51080565-4b82df00-1719-11e9-8cc4-dbbec0459224.png" width="900">
</p>

<p align="center">
  <b>客服机器人应答</b><br>
  <img src="https://static-public.chatopera.com/assets/images/51080567-50479300-1719-11e9-85d8-d209370c9d10.png" width="900">
</p>

<p align="center">
  <b>更多功能，敬请期待 ...</b><br>
  <img src="https://static-public.chatopera.com/assets/images/44916014-28a5ee80-ad67-11e8-936a-a2cdbe62f529.png" width="900">
</p>

</p>
</details>

## 运营及定制开发培训

### 开发文档

<p align="center">
  <b><a href="https://docs.chatopera.com/products/cskefu/osc/engineering.html" target="_blank">开发文档</a></b><br>
  <a href="https://docs.chatopera.com/products/cskefu/osc/engineering.html" target="_blank">
      <img src="https://static-public.chatopera.com/assets/images/44992890-38be0800-afcb-11e8-8fde-a5a671d29764.png" width="300">
  </a>
</p>

### 在线培训视频

[春松客服大讲堂](https://docs.chatopera.com/products/cskefu/osc/training.html)是面向企业 CTO、客服主管、客服及客服系统开发运维人员的在线培训课程，由浅入深的介绍春松客服上线企业智能客服系统、运营客服工作和二次开发的知识。

详情查看[课程介绍及优惠方案](https://docs.chatopera.com/products/osc/cskefu/osc/training.html)。

<p align="center">
  <b><a href="https://docs.chatopera.com/products/cskefu/osc/training.html" target="_blank">主讲老师介绍</a></b><br>
  <a href="https://docs.chatopera.com/products/cskefu/osc/training.html" target="_blank">
      <img src="https://static-public.chatopera.com/assets/images/djt-teacher-hain.jpg" width="800">
  </a>
</p>

## 认证开发者

春松客服开发者是具备春松客服定制化开发智能客服系统技能，具备丰富的专业经验的软件工程师，由 Chatopera 通过访谈的形式确认其在某行业或某企业内完成春松客服上线、定制化春松客服。

| Avatar                                                                                            | Name | GitHub                            | Talk                                                    | Intro.                                                                                                                                   |
| ------------------------------------------------------------------------------------------------- | ---- | --------------------------------- | ------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| <img src="https://static-public.chatopera.com/assets/images/cskefu/github_lecjy.jpg" width="100"> | 刘勇 | [lecjy](https://github.com/lecjy) | [报道](http://www.ctiforum.com/news/guonei/579599.html) | 目前工作于湖北武汉一个电商企业，曾就职于京东海外电商平台，负责客户系统维护，对于电商客服领域有丰富的工作经验，尤其是面向东南亚出海业务。 |

寻找基于春松客服搭建智能客服系统的客户，包括但不限于部署、系统集成和定制开发等，可以优先考虑联系以上认证开发者，Chatopera 团队会持续维护基础模块、云服务和机器人客服，提升春松客服上利用人工智能、机器学习和自动化流程服务。

**寻找开发者合作智能客服项目，社区共建，携手共赢！**

- 组织或个人，在春松客服主页展示为认证开发者
- 春松客服官方推荐项目机会
- 专访并通过官方渠道曝光

填写申请：[https://www.wjx.top/jq/93397428.aspx](https://www.wjx.top/jq/93397428.aspx)

## 用户交流群

<p align="center">
  <b>在 Chatopera 客户群中也包括其他用户，请不要发送敏感信息。讨论与 Chatopera 产品和服务相关的事宜</b><br>
<img src="https://static-public.chatopera.com/assets/images/Chatopera_wecom_customer_group_qr.png" width="600">
</p>

## 立即部署

- 私有部署版

参考部署[开源社区版本文档](https://docs.chatopera.com/products/cskefu/deploy.html)。

## 鸣谢

[FreeSWITCH 中国社区](http://www.freeswitch.org.cn/)

## 开源许可协议

Copyright (2018-2020) <a href="https://www.chatopera.com/" target="_blank">北京华夏春松科技有限公司</a>

[Apache License Version 2.0](https://github.com/chatopera/cskefu/blob/master/LICENSE)

[![chatoper banner][co-banner-image]][co-url]

[co-banner-image]: https://static-public.chatopera.com/assets/images/42383104-da925942-8168-11e8-8195-868d5fcec170.png
[co-url]: https://www.chatopera.com

## Chatopera 云服务

[https://bot.chatopera.com/](https://bot.chatopera.com/)

[Chatopera 云服务](https://bot.chatopera.com)是一站式实现聊天机器人的云服务，按接口调用次数计费。Chatopera 云服务是 [Chatopera 机器人平台](https://docs.chatopera.com/products/chatbot-platform/index.html)的软件即服务实例。在云计算基础上，Chatopera 云服务属于**聊天机器人即服务**的云服务。

Chatopera 机器人平台包括知识库、多轮对话、意图识别和语音识别等组件，标准化聊天机器人开发，支持企业 OA 智能问答、HR 智能问答、智能客服和网络营销等场景。企业 IT 部门、业务部门借助 Chatopera 云服务快速让聊天机器人上线！

<details>
<summary>展开查看 Chatopera 云服务的产品截图</summary>
<p>

<p align="center">
  <b>自定义词典</b><br>
  <img src="https://static-public.chatopera.com/assets/images/64530072-da92d600-d33e-11e9-8656-01c26caff4f9.png" width="800">
</p>

<p align="center">
  <b>自定义词条</b><br>
  <img src="https://static-public.chatopera.com/assets/images/64530091-e41c3e00-d33e-11e9-9704-c07a2a02b84e.png" width="800">
</p>

<p align="center">
  <b>创建意图</b><br>
  <img src="https://static-public.chatopera.com/assets/images/64530169-12018280-d33f-11e9-93b4-9db881cf4dd5.png" width="800">
</p>

<p align="center">
  <b>添加说法和槽位</b><br>
  <img src="https://static-public.chatopera.com/assets/images/64530187-20e83500-d33f-11e9-87ec-a0241e3dac4d.png" width="800">
</p>

<p align="center">
  <b>训练模型</b><br>
  <img src="https://static-public.chatopera.com/assets/images/64530235-33626e80-d33f-11e9-8d07-fa3ae417fd5d.png" width="800">
</p>

<p align="center">
  <b>测试对话</b><br>
  <img src="https://static-public.chatopera.com/assets/images/64530253-3d846d00-d33f-11e9-81ea-86e6d47020d8.png" width="800">
</p>

<p align="center">
  <b>机器人画像</b><br>
  <img src="https://static-public.chatopera.com/assets/images/64530312-6442a380-d33f-11e9-869c-85fb6a835a97.png" width="800">
</p>

<p align="center">
  <b>系统集成</b><br>
  <img src="https://static-public.chatopera.com/assets/images/64530281-4ecd7980-d33f-11e9-8def-c53251f30138.png" width="800">
</p>

<p align="center">
  <b>聊天历史</b><br>
  <img src="https://static-public.chatopera.com/assets/images/64530295-5856e180-d33f-11e9-94d4-db50481b2d8e.png" width="800">
</p>

</p>
</details>

<p align="center">
  <b>立即使用</b><br>
  <a href="https://bot.chatopera.com" target="_blank">
      <img src="https://static-public.chatopera.com/assets/images/64531083-3199aa80-d341-11e9-86cd-3a3ed860b14b.png" width="800">
  </a>
</p>
