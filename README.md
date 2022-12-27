<div align=right>

[主页](https://www.cskefu.com/)　|　[开源许可协议](https://www.cskefu.com/2022/06/24/cskefu-opensource-license/)　|　[邮件列表](https://lists.cskefu.com/cgi-bin/mailman/listinfo/dev)　|　[路线图](https://github.com/cskefu/cskefu/projects)　|　[文档中心](https://docs.cskefu.com)

</div>

# 春松客服：上线开源客服系统

中文版｜ [English](README_en.md)

## 项目目录说明

```
.
├── compose                       # 使用 Docker Compose 启动目录
│   ├── databases
│   │   ├── mysql
│   │   └── redis
│   ├── docker-compose.yml
│   └── README.md
├── README.md
├── server                        # 服务器端 Server 程序
│   ├── serving-foo               # serving 前缀的是 Application
│   │   ├── src
│   │   ├── bin
│   │   ├── config
│   │   ├── data
│   │   ├── Dockerfile
│   │   ├── logs
│   │   ├── pom.xml
│   │   └── README.md
│   ├── mod-bar                   # mod 前缀的是模块、Lib
│   │   ├── src
│   │   ├── bin
│   │   ├── config
│   │   ├── data
│   │   ├── pom.xml
│   │   ├── logs
│   │   └── README.md
│   ├── mod-biz
│   └── 服务端项目根目录
└── web                           # 前端服务程序
    └── web前端项目根目录
```

### server
backend modules and apps.

* module - sub module as a maven project.
* serving - an application such as spring boot app.

For each module and app, following a folder structure -

```
pom.xml # for a maven project
src/ # source codes
config / # config file sample
data/ # data used by this app
logs/ # logs dir
bin/ # scripts or binary generated with this app
    start.sh # start this app 
    compile.sh # compile the source code to binary
    package.sh # package up the file
    dev.sh # start the app in development mode
    build.sh # build the app as a docker image
    run.sh # run the app with docker image
    push.sh # push the docker image into docker registry
  
Dockerfile # Docker file to build this app as a docker image
```

### web

frontend services for web clients. 

### compose

YML and data dirs to run with docker-compose.


## 其他版本

* 当前稳定版：<https://github.com/cskefu/cskefu/tree/master>

* 其它历史版本：<https://github.com/cskefu/cskefu/tags>
