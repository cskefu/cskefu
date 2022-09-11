以下为概述，各版本发布详情，请访问[春松客服专栏博客](https://chatopera.blog.csdn.net/)。

# 7.0.1

- SQL 数据库升级脚本：v6 到 v7 的 Rolling Upgrade 脚本

# 7.0.0

在该版本中，前端开发的效率比之前提高了 10倍！整个春松客服的前端得到了彻底的重构，数十万行代码被重写：使用 PugJS 重构 Freemarker 相关，达到彻底替换的目的。

现在，开源社区的开发者们，可以基于 v7 来定制您的客服系统了！

[Blog](https://chatopera.blog.csdn.net/article/details/113786505)

[Commits](https://github.com/cskefu/cskefu/issues/406)

# 6.0.0

重要提示：本次升级未提供 v5 到 v6 的自动脚本 migration 或 rolling upgrade，请搭建春松客服新实例！因为本次调整设计到数据库，无法设定默认值，所以没有这部分脚本。切记不要在生产环境直接从 v5 升级到 v6。

- 发布全新组织机构管理模块，支持一个超级管理员和多管理员，每个管理员管理其所在组织机构的用户、坐席、对话、渠道等
- 基于新的组织机构管理隔离数据，优化资源权限

# 5.1.1

- 兼容最新的 Chatopera 机器人平台（<https://bot.chatopera.com>）

# 5.0.0

- 优化服务启动速度，重写核心代码
- Fix Bugs
