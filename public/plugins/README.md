# 春松客服插件

本文件夹包含春松客服的开源的插件，每个插件授权参考各子目录。

## 规范

目前，春松客服插件安装是在命令行终端内完成：使用 Bash Shell 命令，链接插件源码到春松客服源码，然后编译打包 WAR 后得到插件功能。

> 提示：Windows 上使用 Git Bash 获得 Bash Shell, [https://git-scm.com/downloads](https://git-scm.com/downloads)。

不同的插件均遵循以下的规范。

### 插件安装

执行命令：

```
cd plugins/PLUGIN
./scripts/install.sh
```

其中，`PLUGIN` 是插件的名字。

### 卸载插件

执行命令：

```
cd plugins/PLUGIN
./scripts/uninstall.sh
```

## 应用打包

安装插件后，插件开发过程同[春松客服开发环境搭建](https://docs.chatopera.com/products/cskefu/osc/engineering.html)。

如果需要部署到系统测试、生产环境等，首先打包为 WAR 应用，方式如下：

```
cd contact-center
./admin/package.sh
```

春松客服的发布形式为 Docker 镜像，如果在更改源码后，也可以使用春松客服的 Docker 镜像构建脚本发布，参考[构建脚本](https://github.com/chatopera/cskefu/blob/osc/contact-center/admin/build.sh)。
