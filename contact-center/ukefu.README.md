#优客服
优客服官方技术支持QQ群（555834343，目前项目已发布v3.8.0版本，加入QQ群可以了解最新进度和技术问题咨询。）：

[![输入图片说明](http://git.oschina.net/uploads/images/2017/0123/001823_7efad50c_1200081.png "在这里输入图片标题")](http://shang.qq.com/wpa/qunwpa?idkey=637134af30a27220211c843d801ada14700aca69ee8f4acf13f795fe38ea7b94)

优客服，是一个多渠道融合的客户支持服务平台，包含WebIM，微信，电话，邮件，短信等接入渠道、智能机器人

#访问地址
DEMO访问地址：[优客服（UCKeFu-WebIM）](http://uk.ukewo.cn/) ， 访问账号：admin，密码：123456

#优客服组件结构
![输入图片说明](http://git.oschina.net/uploads/images/2017/0315/075025_c1add38b_1200081.png "在这里输入图片标题")


 **1. WebIM在线客服** 
优客服提供WebIM功能，在线坐席能够通过工作台操作界面，接收来自WebIM的咨询请求，优客服通过整合多个渠道来源，让坐席在同一个工作界面上处理来自PC端、移动端、微信端，微博等渠道的服务请求。
 **2. 社交媒体** 
接入微信和微博渠道，将社交媒体渠道的的咨询请求接入进入 优客服 坐席工作平台，让客服统一响应和受理
 **3. 邮件、短信** 
多种邮件处理方式，能够将邮箱的消息转为坐席的待处理任务，可以将待处理任务或邮件转为工单


项目组成：

 **1. 前端：LayUI + Freemarker**
 
 **1. 后端：Spring Boot**

 **1. 数据库：MySQL+Elasticsearch** 

项目运行方式：

### 1.  将代码拉取下来

### 1. 编译pom.xml文件，下载好jar包
本项目有四个依赖包，jave 和Mondrian，aliyun-java-sdk-core、aliyun-java-sdk-dysmsapi通过以下指令加入到本地Mavenue仓库：


1、mvn install:install-file -Dfile=src/main/resources/WEB-INF/lib/jave-1.0.2.jar -DgroupId=lt.jave -DartifactId=jave -Dversion=1.0.2 -Dpackaging=jar

2、mvn install:install-file -Dfile=src/main/resources/WEB-INF/lib/mondrian-3.7.0.jar -DgroupId=mondrian -DartifactId=mondrian -Dversion=3.7.0 -Dpackaging=jar

3、执行添加 阿里云jar 
mvn install:install-file -Dfile=src/main/resources/WEB-INF/lib/aliyun-java-sdk-core-3.3.1.jar -DgroupId=com.aliyun -DartifactId=aliyun-java-sdk-core -Dversion=3.3.1 -Dpackaging=jar

4、阿里大鱼jar
mvn install:install-file -Dfile=src/main/resources/WEB-INF/lib/aliyun-java-sdk-dysmsapi-1.0.0.jar -DgroupId=com.aliyun -DartifactId=aliyun-java-sdk-dysmsapi -Dversion=1.0.0 -Dpackaging=jar
 **确保两个依赖都安装成功** 

### 1. 将项目按照maven格式配置好
### 1. 将ukefu.sql脚本在mysql数据库里运行，创建数据库和表


### 1. 配置项目中的application.properties文件中的数据库连接

### 2. 将生成的WAR文件部署到 Tomcat 即可。


运行一下查看效果吧！


 **优客服将会分版本实现全部的功能，V1.0中将包含以下部分功能：** 
 **1、后台管理**
系统后台管理功能，包括系统用户管理，客服坐席管理，系统角色管理、组织机构管理，WebIM接入管理，接入设置
 **2、WebIM在线客服**
访客管理、访客邀请、WebIM网站端多风格切换，访客用户唯一身份识别与跟踪，老用户识别，IP与地理位置识别转换，访客轨迹，访客停留记录，访客实时对话，通信消息，表情包，客户消息多媒体类型消息处理；坐席与用户统一路由排队（ACD），实时提示坐席当前客户正在输入的内容，坐席状态切换、坐席绩效管理
 **3、联系人管理**
公共联系人，私有联系人，联系人贡献与分配
 **4、联络记录**
坐席与客户之间的通信记录，包含WebIM对话，微信对话（V2.0功能）等
 **5、常用语（FAQ）维护**
公共常用语维护（话术），坐席私有常用语维护


优客服全渠道客服系统产品介绍       
优客服，是一个全渠道融合的客户支持服务平台，聚合8大客服渠道，帮助各种行业各种规模的企业建立完整客服体系。
通过将邮件、短信、电话语音、WebIM在线客服、微信、微博、H5页面、APP接口8个渠道来源的客户服务请求与对话汇聚在一个管理平台，用统一的方式来响应和支撑客户服务。
1、渠道融合，建立统一客服模式
 
•  语音
接入企业的呼叫中心，让客服在一个管理界面接听呼入的电话并可以方便快捷的呼出，通过工单记录和后续跟进客户
• WebIM在线客服
让您的客服在一个节目和各个渠道来源的用户对话，实时沟通，并能够为对话记录质检和创建业务请求方便后续跟进和处理
•  社交媒体
集成微信和微博渠道，将社交网络的服务请求集中到优客服，客服坐席统一响应和受理
•  邮件短信
将发至客服邮箱的邮件转为服务请求，通过任务受理和追踪，并能够通过发送邮件或短信快速回复客户客户
2、业务支撑，提供快速服务请求
 
•  简单易用的界面与交互
为客户和坐席人员提供友好的操作界面，方便通过标准方式进行高效沟通
•  跨部门协同解决客户问题
连接您的所有职能人员，从销售客服到技术支持
•   问题流转与变更全纪录
客户的所有交谈记录和问题工单处理事件都会完整记录，方便任何客服接手问题处理，有效解决客户问题
3、数据分析，深入了解客服业务状况
 
• 全渠道数据概况一目了然
了解所有渠道的客户服务状况、来源比例，了解响应时间与服务时间，按不同维度统计
• 优化客服坐席资源分配
按照不同的维度统计更细致的客户坐席服务变化趋势，查看每一天的坐席绩效
• 客群分类，建立客户全景式图
集成企业内部客户数据，分析客户价值，创建客户标签

 **微信接入渠道演示微信公众号** 
![输入图片说明](https://gitee.com/uploads/images/2018/0330/155011_a68bba9e_1200081.jpeg "qrcode_for_gh_b21c83fa3ff9_258.jpg")

优客服部分产品组件截图

![输入图片说明](http://git.oschina.net/uploads/images/2017/0314/203140_36044898_1200081.png "在这里输入图片标题")
![输入图片说明](http://git.oschina.net/uploads/images/2017/0314/203147_5a832431_1200081.png "在这里输入图片标题")
![输入图片说明](http://git.oschina.net/uploads/images/2017/0314/203155_24ca7ba3_1200081.png "在这里输入图片标题")
![输入图片说明](http://git.oschina.net/uploads/images/2017/0314/203208_1c20aabe_1200081.png "在这里输入图片标题")
![输入图片说明](http://git.oschina.net/uploads/images/2017/0314/203218_78ff4169_1200081.png "在这里输入图片标题")
![输入图片说明](http://git.oschina.net/uploads/images/2017/0314/203225_01f116e7_1200081.png "在这里输入图片标题")
![输入图片说明](http://git.oschina.net/uploads/images/2017/0314/203236_7d782c9e_1200081.png "在这里输入图片标题")
![输入图片说明](http://git.oschina.net/uploads/images/2017/0314/203349_829a3ac2_1200081.png "在这里输入图片标题")
![输入图片说明](http://git.oschina.net/uploads/images/2017/0314/203359_533aa230_1200081.png "在这里输入图片标题")
![输入图片说明](http://git.oschina.net/uploads/images/2017/0314/203408_e27accb5_1200081.png "在这里输入图片标题")
![输入图片说明](http://git.oschina.net/uploads/images/2017/0314/203416_47aac57f_1200081.png "在这里输入图片标题")
![输入图片说明](http://git.oschina.net/uploads/images/2017/0314/203426_a4df219a_1200081.png "在这里输入图片标题")
![输入图片说明](http://git.oschina.net/uploads/images/2017/0314/203433_1172bbb7_1200081.png "在这里输入图片标题")
![输入图片说明](http://git.oschina.net/uploads/images/2017/0314/203445_4ba5491a_1200081.png "在这里输入图片标题")
![输入图片说明](http://git.oschina.net/uploads/images/2017/0314/203452_99b88f79_1200081.png "在这里输入图片标题")
![输入图片说明](http://git.oschina.net/uploads/images/2017/0314/203459_26a875f5_1200081.png "在这里输入图片标题")
![输入图片说明](http://git.oschina.net/uploads/images/2017/0314/203505_5e9309a1_1200081.png "在这里输入图片标题")
![输入图片说明](http://git.oschina.net/uploads/images/2017/0314/203512_fd235387_1200081.png "在这里输入图片标题")