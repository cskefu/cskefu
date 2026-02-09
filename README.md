# 春松客服

春松客服是一个企业客服系统解决方案，通过模块化完成企业客户服务工作的运营，包括座席工作台、联系人管理、访客渠道和会话管理等。

## 服务模式

目前，春松客服包括两个产品和服务模式：

**企业版具备更友好的代码授权、功能以及服务，更可靠稳定，企业版更适合企业使用！**

| 模式 | EE / 企业版 | OSS / 开源版 |
| --- | --- | --- |
| 收费模式 | 根据服务项目进行[商业洽谈](https://www.chatopera.com/mail.html) | 启动服务后导入[授权证书](https://store.chatopera.com/product/cskefu001) |  
| 软件迭代 | 官方团队迭代开发 | 通过[开源社区](https://www.cskefu.com/)形式 |
| 技术支持 | 官方技术团队商业支持 | 通过开源社区[提交工单](https://github.com/cskefu/cskefu/issues) | 
| 部署及运维 | 官方技术团队商业支持 | 根据开源社区文档，[文档中心](https://docs.cskefu.com/docs/) |
| 使用培训 | 客服系统及二次开发的培训由官方团队支持 | 购买[春松客服大讲堂](https://docs.cskefu.com/docs/osc/training) |


## 产品版本

| 代号 | 版本 | 状态 | 说明 | 
| --- | --- | --- | --- | 
| v10 | EE / 企业版 | Active / 活跃中 （*推荐） | [链接](https://docs.cskefu.com/docs/) |
| v9 | EE / 企业版 | Sunset / 维护终止 | [购买地址（一次性付费、永久授权、包含源码）](https://store.chatopera.com/product/cskfv9) |
| v8.x | OSS / 开源版 | Sunset / 维护终止 ([GitHub](https://github.com/cskefu/cskefu/tree/develop) \| [Gitee](https://gitee.com/cskefu/cskefu/tree/develop/)) | **对于侵权行为，我们将积极通过法律途径进行维权，在 2023 ~ 2025 年间，已经送达了 ~20 件诉讼案例，在广东、上海、北京多地进行了诉讼。** |

# 春松客服 V9

**春松客服 v9 是一个闭源客服系统，目前官方已经终止维护，但是因为春松客服 v9 以 v8 为基础升级而来，依然沿用了 Java, SpringBoot 技术栈，为众多开发者所喜欢和使用，因为我们继续提供 v9 的商业产品，但是主要是开发者（或客户）自行维护。**

## 产品演示

春松客服 v9 的演示视频：

- [一个简单的客服流程](https://www.bilibili.com/video/BV1YVatzFEKb?p=1)
- [春松客服的特色](https://www.bilibili.com/video/BV1YVatzFEKb?p=2)
- [搭建配置客服团队，实现角色，组织机构](https://www.bilibili.com/video/BV1YVatzFEKb?p=3)
- [渠道管理，春松客服的 H5 网页渠道配置及使用](https://www.bilibili.com/video/BV1YVatzFEKb?p=4)
- [座席工作台的使用，多种工具，提升座席人员的工作效率](https://www.bilibili.com/video/BV1YVatzFEKb?p=5)
- [配置使用机器人客服，应用大语言模型 LLM，RAG，提升服务质量、效率](https://www.bilibili.com/video/BV1YVatzFEKb?p=6)
- [高级座席实现系统监控、质检、报表](https://www.bilibili.com/video/BV1YVatzFEKb?p=7)

产品演示 DEMO:

| 项 | 值 | 描述 |
| --- | --- | --- |
| URL 地址 | https://v9.cskefu.com/ | 单点登录，后登录用户自动登出前面的用户 |
| Admin 用户名 | admin | 超级管理员 |
| Admin 密码 | admin1234 | 超级管理员密码 |
| 座席 用户名 | zhangsan | 可以接待访客的座席 |
| 座席 密码 | agent1234 | 座席密码 |
| 访客 H5 聊天 | URL | [https://v9.cskefu.com/testclient.html](https://v9.cskefu.com/testclient.html) |

*提示：演示环境定时清理重置，有可能造成不稳定；演示环境可能有同时登入的账号，被登出不是 BUG；请不要上传敏感信息，LOGO 等到演示环境。

---

# 春松客服 V8 - OSS 开源版

[![GitHub Stargazers](https://img.shields.io/github/stars/chatopera/cskefu.svg?style=social&label=Star&maxAge=2592000)](https://github.com/cskefu/cskefu/stargazers) [![GitHub Forks](https://img.shields.io/github/forks/chatopera/cskefu.svg?style=social&label=Fork&maxAge=2592000)](https://github.com/cskefu/cskefu/network/members) [![License](https://cdndownload2.chatopera.com/cskefu/licenses/chunsong1.0.svg)](https://www.cskefu.com/licenses/v1.html "开源许可协议") [![GitHub Issues](https://img.shields.io/github/issues/chatopera/cskefu.svg)](https://github.com/cskefu/cskefu/issues) [![GitHub Issues Closed](https://img.shields.io/github/issues-closed/chatopera/cskefu.svg)](https://github.com/cskefu/cskefu/issues?q=is%3Aissue+is%3Aclosed) [![docker](https://img.shields.io/docker/pulls/chatopera/contact-center.svg "Docker Pulls")](https://hub.docker.com/r/chatopera/contact-center/) <!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-37-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->


## 开发者列表 ✨

:evergreen_tree: 春松客服是开源的智能客服系统，于 2018 年 9 月由 [Chatopera](https://www.chatopera.com) 发布，在开源社区协作中优化和完善，春松客服属于[春松客服开源社区](https://github.com/cskefu/cskefu#%E6%98%A5%E6%9D%BE%E5%AE%A2%E6%9C%8D%E5%BC%80%E6%BA%90%E7%A4%BE%E5%8C%BA)。

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/mukaiu"><img src="https://avatars.githubusercontent.com/u/7746790?v=4?s=50" width="50px;" alt="Mukaiu"/><br /><sub><b>Mukaiu</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/commits?author=mukaiu" title="Code">💻</a> <a href="#infra-mukaiu" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://www.linkedin.com/in/hai-liang-wang/"><img src="https://avatars.githubusercontent.com/u/3538629?v=4?s=50" width="50px;" alt="Hai Liang W."/><br /><sub><b>Hai Liang W.</b></sub></a><br /><a href="#plugin-hailiang-wang" title="Plugin/utility libraries">🔌</a> <a href="#financial-hailiang-wang" title="Financial">💵</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/shih945"><img src="https://avatars.githubusercontent.com/u/29646781?v=4?s=50" width="50px;" alt="SHIH"/><br /><sub><b>SHIH</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/commits?author=shih945" title="Code">💻</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/luruiGit"><img src="https://avatars.githubusercontent.com/u/49265205?v=4?s=50" width="50px;" alt="luruiGit"/><br /><sub><b>luruiGit</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/commits?author=luruiGit" title="Code">💻</a></td>
      <td align="center" valign="top" width="11.11%"><a href="http://enze5088.github.io"><img src="https://avatars.githubusercontent.com/u/14285786?v=4?s=50" width="50px;" alt="Enze"/><br /><sub><b>Enze</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/commits?author=enze5088" title="Code">💻</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://blog.dengchao.fun"><img src="https://avatars.githubusercontent.com/u/16363180?v=4?s=50" width="50px;" alt="邓超"/><br /><sub><b>邓超</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/commits?author=DevDengChao" title="Code">💻</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/Happy5"><img src="https://avatars.githubusercontent.com/u/53087368?v=4?s=50" width="50px;" alt="Happy5"/><br /><sub><b>Happy5</b></sub></a><br /><a href="#ideas-Happy5" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://www.csdn.net"><img src="https://avatars.githubusercontent.com/u/3679798?v=4?s=50" width="50px;" alt="kyle"/><br /><sub><b>kyle</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/commits?author=kylezhang" title="Code">💻</a> <a href="#talk-kylezhang" title="Talks">📢</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/xianliwang"><img src="https://avatars.githubusercontent.com/u/52594347?v=4?s=50" width="50px;" alt="xianliwang"/><br /><sub><b>xianliwang</b></sub></a><br /><a href="#video-xianliwang" title="Videos">📹</a> <a href="https://github.com/cskefu/cskefu/commits?author=xianliwang" title="Tests">⚠️</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/lihang2016"><img src="https://avatars.githubusercontent.com/u/23203931?v=4?s=50" width="50px;" alt="lihang2016"/><br /><sub><b>lihang2016</b></sub></a><br /><a href="#ideas-lihang2016" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/live-in-the-moment"><img src="https://avatars.githubusercontent.com/u/62800943?v=4?s=50" width="50px;" alt="live-in-the-moment"/><br /><sub><b>live-in-the-moment</b></sub></a><br /><a href="#ideas-live-in-the-moment" title="Ideas, Planning, & Feedback">🤔</a> <a href="https://github.com/cskefu/cskefu/issues?q=author%3Alive-in-the-moment" title="Bug reports">🐛</a> <a href="https://github.com/cskefu/cskefu/commits?author=live-in-the-moment" title="Tests">⚠️</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/ArioWei"><img src="https://avatars.githubusercontent.com/u/41034256?v=4?s=50" width="50px;" alt="ArioWei"/><br /><sub><b>ArioWei</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/commits?author=ArioWei" title="Tests">⚠️</a></td>
      <td align="center" valign="top" width="11.11%"><a href="http://www.youkefu.cn"><img src="https://avatars.githubusercontent.com/u/48078408?v=4?s=50" width="50px;" alt="优客服"/><br /><sub><b>优客服</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/commits?author=youkefu" title="Code">💻</a> <a href="https://github.com/cskefu/cskefu/commits?author=youkefu" title="Tests">⚠️</a> <a href="#business-youkefu" title="Business development">💼</a> <a href="#design-youkefu" title="Design">🎨</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/lecjy"><img src="https://avatars.githubusercontent.com/u/9280760?v=4?s=50" width="50px;" alt="lecjy"/><br /><sub><b>lecjy</b></sub></a><br /><a href="#ideas-lecjy" title="Ideas, Planning, & Feedback">🤔</a> <a href="#talk-lecjy" title="Talks">📢</a> <a href="#mentoring-lecjy" title="Mentoring">🧑‍🏫</a> <a href="#maintenance-lecjy" title="Maintenance">🚧</a> <a href="https://github.com/cskefu/cskefu/commits?author=lecjy" title="Code">💻</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/xl111"><img src="https://avatars.githubusercontent.com/u/64338718?v=4?s=50" width="50px;" alt="徐。。"/><br /><sub><b>徐。。</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/commits?author=xl111" title="Code">💻</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/viaco2ove"><img src="https://avatars.githubusercontent.com/u/8044837?v=4?s=50" width="50px;" alt="viaco2ove"/><br /><sub><b>viaco2ove</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/commits?author=viaco2ove" title="Code">💻</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/understanding"><img src="https://avatars.githubusercontent.com/u/2801277?v=4?s=50" width="50px;" alt="understanding"/><br /><sub><b>understanding</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/commits?author=understanding" title="Tests">⚠️</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/MQPearth"><img src="https://avatars.githubusercontent.com/u/32632796?v=4?s=50" width="50px;" alt="MQPearth"/><br /><sub><b>MQPearth</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/commits?author=MQPearth" title="Tests">⚠️</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/SkorpiosL"><img src="https://avatars.githubusercontent.com/u/32902343?v=4?s=50" width="50px;" alt="SkorpiosL"/><br /><sub><b>SkorpiosL</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/commits?author=SkorpiosL" title="Tests">⚠️</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/always-China"><img src="https://avatars.githubusercontent.com/u/49581101?v=4?s=50" width="50px;" alt="hua"/><br /><sub><b>hua</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/commits?author=always-China" title="Code">💻</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/wq11123"><img src="https://avatars.githubusercontent.com/u/40993206?v=4?s=50" width="50px;" alt="wq11123"/><br /><sub><b>wq11123</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/commits?author=wq11123" title="Tests">⚠️</a> <a href="#video-wq11123" title="Videos">📹</a> <a href="#ideas-wq11123" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/MouMouQQ"><img src="https://avatars.githubusercontent.com/u/101631131?v=4?s=50" width="50px;" alt="MouMouQQ"/><br /><sub><b>MouMouQQ</b></sub></a><br /><a href="#ideas-MouMouQQ" title="Ideas, Planning, & Feedback">🤔</a> <a href="https://github.com/cskefu/cskefu/commits?author=MouMouQQ" title="Tests">⚠️</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/tigerun"><img src="https://avatars.githubusercontent.com/u/17540364?v=4?s=50" width="50px;" alt="Tigerun"/><br /><sub><b>Tigerun</b></sub></a><br /><a href="#ideas-tigerun" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/yangbailiang"><img src="https://avatars.githubusercontent.com/u/50096675?v=4?s=50" width="50px;" alt="yangbailiang"/><br /><sub><b>yangbailiang</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/issues?q=author%3Ayangbailiang" title="Bug reports">🐛</a> <a href="https://github.com/cskefu/cskefu/commits?author=yangbailiang" title="Tests">⚠️</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/lokywang"><img src="https://avatars.githubusercontent.com/u/28672424?v=4?s=50" width="50px;" alt="lokywang"/><br /><sub><b>lokywang</b></sub></a><br /><a href="#ideas-lokywang" title="Ideas, Planning, & Feedback">🤔</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/jichoucc"><img src="https://avatars.githubusercontent.com/u/87190214?v=4?s=50" width="50px;" alt="jichoucc"/><br /><sub><b>jichoucc</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/issues?q=author%3Ajichoucc" title="Bug reports">🐛</a> <a href="https://github.com/cskefu/cskefu/commits?author=jichoucc" title="Tests">⚠️</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/wuyongyin"><img src="https://avatars.githubusercontent.com/u/20410234?v=4?s=50" width="50px;" alt="wuyongyin"/><br /><sub><b>wuyongyin</b></sub></a><br /><a href="#ideas-wuyongyin" title="Ideas, Planning, & Feedback">🤔</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/wangdayan"><img src="https://avatars.githubusercontent.com/u/62323175?v=4?s=50" width="50px;" alt="Claire"/><br /><sub><b>Claire</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/commits?author=wangdayan" title="Tests">⚠️</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/zc1813400107"><img src="https://avatars.githubusercontent.com/u/46372405?v=4?s=50" width="50px;" alt="super"/><br /><sub><b>super</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/commits?author=zc1813400107" title="Code">💻</a> <a href="https://github.com/cskefu/cskefu/commits?author=zc1813400107" title="Documentation">📖</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/xiaobo9"><img src="https://avatars.githubusercontent.com/u/1284376?v=4?s=50" width="50px;" alt="xiaobo9"/><br /><sub><b>xiaobo9</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/commits?author=xiaobo9" title="Code">💻</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/zhangchanglong"><img src="https://avatars.githubusercontent.com/u/3481828?v=4?s=50" width="50px;" alt="zhangchanglong"/><br /><sub><b>zhangchanglong</b></sub></a><br /><a href="#eventOrganizing-zhangchanglong" title="Event Organizing">📋</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://samzong.me"><img src="https://avatars.githubusercontent.com/u/13782141?v=4?s=50" width="50px;" alt="Samzong Lu"/><br /><sub><b>Samzong Lu</b></sub></a><br /><a href="#eventOrganizing-SAMZONG" title="Event Organizing">📋</a> <a href="#projectManagement-SAMZONG" title="Project Management">📆</a> <a href="#design-SAMZONG" title="Design">🎨</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/halfray"><img src="https://avatars.githubusercontent.com/u/8181982?v=4?s=50" width="50px;" alt="halfray"/><br /><sub><b>halfray</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/issues?q=author%3Ahalfray" title="Bug reports">🐛</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/kely33"><img src="https://avatars.githubusercontent.com/u/134681303?v=4?s=50" width="50px;" alt="kely33"/><br /><sub><b>kely33</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/issues?q=author%3Akely33" title="Bug reports">🐛</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/zjpzjp"><img src="https://avatars.githubusercontent.com/u/11382248?v=4?s=50" width="50px;" alt="websir"/><br /><sub><b>websir</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/commits?author=zjpzjp" title="Code">💻</a></td>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/guodong"><img src="https://avatars.githubusercontent.com/u/32507511?v=4?s=50" width="50px;" alt="guodong"/><br /><sub><b>guodong</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/commits?author=CollectBugs" title="Code">💻</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="11.11%"><a href="https://github.com/zhangjian4"><img src="https://avatars.githubusercontent.com/u/23302214?v=4?s=50" width="50px;" alt="zhangjian"/><br /><sub><b>zhangjian</b></sub></a><br /><a href="https://github.com/cskefu/cskefu/issues?q=author%3Azhangjian4" title="Bug reports">🐛</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

## 功能介绍

<!-- <img src="./public/assets/cskefu-2021-08-22-8.22.09PM.jpg" width="900"> -->

春松客服提供的开源代码，即[CSKeFu](https://github.com/cskefu/cskefu)，包含多个开箱即用的模块：

- 账号及组织机构管理：按组织、角色分配账号权限

- 坐席监控：设置坐席监控角色的人员可以看到并干预访客会话

- 联系人和客户管理：CRM 模块，管理联系人和客户，细粒度维护客户信息，自定义标签和打标签，记录来往历史等

- 网页渠道组件：一分钟接入对话窗口，支持技能组、邀请和关联联系人等

- Facebook 渠道组件：快速接入 [Facebook Messenger](https://www.messenger.com/) 渠道，通过 Messenger 支持 Facebook 粉丝页、[Shopify](https://www.shopify.com/) 等海外社交、电商平台

- 坐席工作台：汇聚多渠道访客请求，坐席根据策略自动分配，自动弹屏，转接等

- 机器人客服：与[Chatopera 云服务](/products/chatbot-platform/index.html)集成

- 企业聊天：支持企业员工在春松客服系统中群聊和私聊

- 质检：历史会话、服务小结、服务反馈及相关报表

了解功能详细介绍，参考[文档中心](https://docs.cskefu.com/)。

## 快速开始

### 春松客服用户使用指南

- 快速的了解和介绍春松客服
- 快速的查找和春松客服相关的材料

下载[《春松客服用户使用指南》](https://www.cskefu.com/moment/825.html/)。

### 安装部署

支持云原生环境，容器化一键部署，现在就使用春松客服！参考[《私有部署文档》](http://docs.cskefu.com/docs/deploy)。

### 系统初始化

部署后，进行系统初始化，为组织设定部门、权限、账号等，参考[《系统初始化文档》](https://docs.cskefu.com/docs/initialization)。

### 运维

备份、升级、回滚等运维工作，参考[《系统维护文档》](https://docs.cskefu.com/docs/osc/maintainence)。

### 运营使用指南

关于产品的具体使用说明，请参考[《春松客服文档》](https://docs.cskefu.com)。

### 立即上线机器人客服

超过 85% 的春松客服企业客户通过 Chatopera 云服务上线机器人客服！7x24 小时在线，接待访客，辅助人工坐席，提升 10 倍工作效率。Chatopera 机器人平台包括知识库、多轮对话、意图识别和语音识别等组件，标准化聊天机器人开发。

- [集成 Chatopera 云服务](https://docs.cskefu.com/docs/work-chatbot/bot-agent)
- [设定知识库、对话技能：欢迎语、按钮、图文消息等](https://docs.cskefu.com/docs/work-chatbot/message-types)

<details>
<summary>展开查看更多机器人客服介绍</summary>
<p>

<p align="center">
  <b>应用场景示例</b><br>
  <img src="https://github.com/cskefu/cskefu/raw/develop/public/assets/screenshot-20210908-184522.png" width="800">
</p>

支持企业 OA 智能问答、HR 智能问答、智能客服和网络营销等场景。企业 IT 部门、业务部门借助 Chatopera 云服务快速让聊天机器人上线！
上线机器人客服的两个方式：1）Chatopera 云服务，按量付费，提供每日免费额度；2）私有部署。

</p>
</details>

## 必读文档

- 了解春松客服采用的开源许可协议，参考[文档](https://www.cskefu.com/2023/06/25/chunsong-public-license-1-0/)
- 了解春松客服的开发计划，参考[文档](https://github.com/cskefu/cskefu/issues)
- 如何提交反馈、文档，参考[文档](./CONTRIBUTING.md)
- 如何成为春松客服开发者，参考[文档](https://docs.cskefu.com/docs/osc/devonboard/)
- 如何提交代码，参考[文档](https://docs.cskefu.com/docs/osc/contribution)

### 工单

遇到任何软件使用的问题，先在[工单历史记录](https://github.com/cskefu/cskefu/issues)中查询。
如果没有找到相似问题，使用下面的链接创建新的工单 -

- [Help: 开发环境搭建、功能咨询和使用问题等](https://github.com/cskefu/cskefu/issues/new?assignees=hailiang-wang&labels=help-wanted&template=1_help.md&title=Title%3A+%E7%94%A8%E4%B8%80%E5%8F%A5%E8%AF%9D%E9%99%88%E8%BF%B0%E4%BA%8B%E6%83%85%EF%BC%8C%E4%BF%9D%E8%AF%81%E8%A8%80%E7%AE%80%E6%84%8F%E8%B5%85%EF%BC%8C%E6%AF%94%E5%A6%82%E9%97%AE%E9%A2%98%E7%AE%80%E8%BF%B0%E5%8F%8A+root+cause+%E6%97%A5%E5%BF%97%E8%AF%AD%E5%8F%A5%EF%BC%8C%E6%9B%B4%E5%AE%B9%E6%98%93%E8%8E%B7%E5%BE%97%E5%B8%AE%E5%8A%A9)
- [Bug: 提交软件缺陷](https://github.com/cskefu/cskefu/issues/new?assignees=hailiang-wang&labels=bug&template=2_bug_report.md&title=Title%3A+%E7%94%A8%E4%B8%80%E5%8F%A5%E8%AF%9D%E9%99%88%E8%BF%B0%E4%BA%8B%E6%83%85%EF%BC%8C%E4%BF%9D%E8%AF%81%E8%A8%80%E7%AE%80%E6%84%8F%E8%B5%85%EF%BC%8C%E6%AF%94%E5%A6%82%E9%97%AE%E9%A2%98%E7%AE%80%E8%BF%B0%E5%8F%8A+root+cause+%E6%97%A5%E5%BF%97%E8%AF%AD%E5%8F%A5%EF%BC%8C%E6%9B%B4%E5%AE%B9%E6%98%93%E8%8E%B7%E5%BE%97%E5%B8%AE%E5%8A%A9)
- [Requirement: 描述新需求、反馈建议](https://github.com/cskefu/cskefu/issues/new?assignees=hailiang-wang&labels=requirement&template=3_requirement.md&title=Title%3A+%E7%94%A8%E4%B8%80%E5%8F%A5%E8%AF%9D%E9%99%88%E8%BF%B0%E4%BA%8B%E6%83%85%EF%BC%8C%E4%BF%9D%E8%AF%81%E8%A8%80%E7%AE%80%E6%84%8F%E8%B5%85%EF%BC%8C%E6%AF%94%E5%A6%82%E9%97%AE%E9%A2%98%E7%AE%80%E8%BF%B0%E5%8F%8A+root+cause+%E6%97%A5%E5%BF%97%E8%AF%AD%E5%8F%A5%EF%BC%8C%E6%9B%B4%E5%AE%B9%E6%98%93%E8%8E%B7%E5%BE%97%E5%B8%AE%E5%8A%A9)
- [Profiling: 瓶颈分析、性能优化建议和安全漏洞等](https://github.com/cskefu/cskefu/issues/new?assignees=hailiang-wang&labels=profiling&template=4_profiling.md&title=Title%3A+%E7%94%A8%E4%B8%80%E5%8F%A5%E8%AF%9D%E9%99%88%E8%BF%B0%E4%BA%8B%E6%83%85%EF%BC%8C%E4%BF%9D%E8%AF%81%E8%A8%80%E7%AE%80%E6%84%8F%E8%B5%85%EF%BC%8C%E6%AF%94%E5%A6%82%E9%97%AE%E9%A2%98%E7%AE%80%E8%BF%B0%E5%8F%8A+root+cause+%E6%97%A5%E5%BF%97%E8%AF%AD%E5%8F%A5%EF%BC%8C%E6%9B%B4%E5%AE%B9%E6%98%93%E8%8E%B7%E5%BE%97%E5%B8%AE%E5%8A%A9)

### 开发者文档

- 开发环境搭建
  - [安装依赖和启动数据库等](https://docs.cskefu.com/docs/osc/engineering)
  - [IDE 配置和使用之 IntelliJ IDEA](https://docs.cskefu.com/docs/osc/ide_intelij_idea)
  - [IDE 配置和使用之 VSCode](https://docs.cskefu.com/docs/osc/ide_vscode)
- 定制开发技能
  - [系统集成之 RestAPIs](https://docs.cskefu.com/docs/osc/restapi)
  - [从零开始学习定制春松客服技能：春松客服大讲堂 PPT 课件及视频](https://store.chatopera.com/product/cskfdjt19)
  - [掌握春松客服前端框架 Pugjs，介绍及使用注意事项](https://blog.csdn.net/samurais/article/details/114576611)
- [提交代码](https://docs.cskefu.com/docs/osc/contribution)

### 商业洽淡

春松客服提供商业技术支持，春松客服有企业版。
相关功能咨询、报价、DEMO 联系我们。

[https://www.chatopera.com/mail.html](https://www.chatopera.com/mail.html)

## 鸣谢

[Amazon AWS 赞助春松客服服务器资源 5W RMB（2021 年度）](https://aws.amazon.com)

[IBM Cloud 赞助春松客服服务器资源 12W US Dollar（2019 年度）](https://cloud.ibm.com/)

[QingCloud 赞助春松客服服务器资源 1W RMB（2018 年度）](https://www.qingcloud.com/)

## 开源许可协议

Copyright (2018-2024) <a href="https://www.chatopera.com/" target="_blank">Beijing Huaxia Chunsong Technology Co., Ltd.</a>

[Chunsong Public License, version 1.0](https://docs.cskefu.com/licenses/v1.html)

![image](./public/assets/screenshot-20220323-163051.jpg)
