# RASA中文聊天机器人项目



---

**RASA 开发中文指南系列博文：**

- [Rasa中文聊天机器人开发指南(1)：入门篇](https://jiangdg.blog.csdn.net/article/details/104328946)
- [Rasa中文聊天机器人开发指南(2)：NLU篇](https://jiangdg.blog.csdn.net/article/details/104530994)
- [Rasa中文聊天机器人开发指南(3)：Core篇](https://jiangdg.blog.csdn.net/article/details/105434136)
- Rasa中文聊天机器人开发指南(4)：RasaX篇
- Rasa中文聊天机器人开发指南(5)：Action篇

**注：本系列博客翻译自[Rasa官方文档](https://rasa.com/docs/rasa/)，并融合了自己的理解和项目实战，同时对文档中涉及到的技术点进行了一定程度的扩展，目的是为了更好的理解Rasa工作机制。与本系列博文配套的项目GitHub地址：[ChitChatAssistant](https://github.com/jiangdongguo/ChitChatAssistant)，欢迎`star`和`issues`，我们共同讨论、学习！**

---


# 1. 安装rasa

## 1.1 环境要求

- python 3.6 +
- mitie
- jieba

## 1.2 安装步骤

**1. 安装rasa**

```shell
# 当前版本为1.9.5
# 该命令运行时间较长，会安装完所有的依赖
pip --default-timeout=500 install -U rasa
```

**2. 安装mitie**

```shell
# 在线安装Mitie
pip install git+https://github.com/mit-nlp/MITIE.git
pip install rasa[mitie]  # 注：由于第一步始终没成功过，没尝试过这个命令的意义
```
&emsp;由于自己在线安装尝试了很多次都拉不下来，因此只能走离线安装的方式，有三个步骤：

- 首先，下载[MITIE源码](https://github.com/mit-nlp/MITIE)和中文词向量模型[total_word_feature_extractor_zh.dat(密码：p4vx)](https://pan.baidu.com/s/1kNENvlHLYWZIddmtWJ7Pdg)，这里需要将该模型拷贝到创建的python项目data目录下(可任意位置)，后面训练NLU模型时用到；

- 其次，安装[Visual Studio 2017](https://blog.csdn.net/qq_42276781/article/details/88594870) ，需要勾选“`用于 CMake 的 Visual C++ 工具`”，因为编译MITIE源码需要Cmake和Visual C++环境。安装完毕后，将`C:\Program Files (x86)\Microsoft Visual Studio\2017\Community\Common7\IDE\CommonExtensions\Microsoft\CMake\CMake\bin`添加到环境变量中，再重启电脑使之生效；

- 从Pycharm的命令终端进行Mitie源码根目录，执行下面的命令：

  > python setup.py build  
  > python setup.py install
  

**3. 安装jieba**  

```shell
# 安装Jieba中文分词
pip install jieba
```

# 2. 训练模型  

&emsp;当所有样本和配置文件准备好后，接下来就是训练模型了，打开命令终端执行下面的命令，该命令会同时训练NLU和Core模型。

- 使用MITIE

```shell
python -m rasa train --config configs/config.yml --domain configs/domain.yml --data data/
```

- 使用Supervised_Embedding

```bash
python -m rasa train --config configs/zh_jieba_supervised_embeddings_config.yml --domain configs/domain.yml --data data/
```

- 使用MITIE+Supervised_Embedding

```bash
python -m rasa train --config configs/zh_jieba_mitie_embeddings_config.yml --domain configs/domain.yml --data data/
```

# 3. 运行服务  

**（1）启动Rasa服务**

&emsp;在命令终端，输入下面命令：

```shell
# 启动rasa服务
# 该服务实现自然语言理解(NLU)和对话管理(Core)功能
# 注：该服务的--port默认为5005，如果使用默认则可以省略
python -m rasa run --port 5005 --endpoints configs/endpoints.yml --credentials configs/credentials.yml --debug
```

**（2）启动Action服务**

在命令终端，输入下面命令：

```shell
# 启动action服务
# 注：该服务的--port默认为5055，如果使用默认则可以省略
Python -m rasa run actions --port 5055 --actions actions --debug 
```

**（3）启动server.py服务**

```shell
python server.py
```

当**Rasa Server**、**Action Server**和**Server.py**运行后，在浏览器输入测试：

` http://127.0.0.1:8088/ai?content=查询广州明天的天气`

终端调用效果为：

![](https://img-blog.csdnimg.cn/20200227153932228.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L0FuZHJFeHBlcnQ=,size_16,color_FFFFFF,t_70)



# 4. 更新日志


**（1）V1.0.2020.02.15**

- 创建项目，模型训练成功；
- 前端访问Rasa服务器正常响应；
- 对接图灵闲聊机器人、心知天气API，便于测试；

**（2）V1.1.2020.02.27**

- 优化NLU样本，尝试使用同义词、正则、查找表；
- 改进supervised_embeddings，实体提取和意图识别明显提高，训练速度加快很多；
- 完成`Rasa中文聊天机器人开发指南(2)：NLU篇`文章撰写；

**（3）V1.2.2020.04.10**

- 使用Interactive Learning构建样本；
- 新增MITIE+supervised_embeddings管道，并训练相应的模型；
- 新增身份查询案例；
- 将Rasa版本升级到1.9.5，解决win10使用tensorflow出现的异常(详见`入门篇`)；
- 完成`Rasa中文聊天机器人开发指南(3)：Core篇`文章撰写；

# 5. License

> ```
> Copyright 2020 Jiangdongguo
> 
> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at
> 
>    http://www.apache.org/licenses/LICENSE-2.0
> 
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.
> ```

