# 后端技术栈

- Java21
- Spring Boot 3
- Spring Cloud两种方案，采纳方案一
    - 方案一：Spring Cloud，docker-compose方案
        - 注册中心 Nacos
        - 声明式服务调用 OpenFeign
        - 负载均衡 Ribbon/LoadBanlancer 待定
        - 配置中心 Nacos
        - 服务容错 Sentinel
        - 链路追踪 Skywalking
    - 方案二：Spring Cloud Kubernetes，kubernetes方案
        - 注册中心 Kubernetes注册中心
        - 声明式服务调用 OpenFeign/Square 待定
        - 负载均衡 Kubernetes负载均衡/Ribbon/LoadBanlancer 待定
        - 配置中心 Kubernetes配置中心
        - 服务容错 Sentinel
        - 链路追踪 Skywalking
- Spring Security + Oauth2.0
- MyBatis-Flex
- Webflux、 Tomcat
- Jackson，不使用Fastjson
- redisson
- mysql 8.0
- HikariCP，不使用Druid
- Smart-doc，不使用Swagger
- RabbitMQ
- Seata
- jakarta.validation.Valid + org.springframework.validation.annotation.Validated

# 各服务使用的端口

```
cskefu-web-gateway: 8080
cskefu-manager-service: 8081
cskefu-auth-service: 8082
cskefu-plugin-service: 8083
cskefu-channel-wechat-service: 8084
cskefu-websocket-service: 10000

```

# 模块及服务划分约定

**统一的、简单的、一致的风格更有利于代码的维护，在模块及服务划分和模块及服务命名上遵守以下约定。如不能请及时在开发者群反馈。**

所有-service结尾代表一个微服务，-gateway结尾代表一个聚合网关，-domain结尾代表一个需要和其他服务或其他业务共用的实体类。

```
contact-backend
│
├─cskefu-assistant-module 此目录下都是辅助模块（如工具类），不包含任何微服务，模块数量依据需要添加
│  │
│  ├─cskefu-assistant-base 基础辅助模块
│  │
│  └─cskefu-assistant-mvc  mvc相关辅助模块
│
├─cskefu-auth 认证和鉴权，不要随意添加微服务
│  │
│  ├─cskefu-auth-domain
│  │
│  └─cskefu-auth-service
│
├─cskefu-channel 渠道接入，不同渠道用不同的，不要随意添加微服务
│  │
│  ├─cskefu-channel-service xxx渠道微服务，目录以cskefu-channel-开头
│  │
│  ├─cskefu-channel-wechat-domain 微信渠道
│  │
│  ├─cskefu-channel-wechat 微信渠道业务逻辑
│  │
│  ├─cskefu-channel-xxx-domain xxx微信渠道
│  │
│  └─cskefu-channel-xxx xxx渠道业务逻辑
│
├─cskefu-manager 核心控制，如基础配置、时效管理、会话分配，不要随意添加更多服务
│  │
│  ├─cskefu-manager-domain
│  │
│  └─cskefu-manager-service
│
├─cskefu-plugin 插件后端功能，建议尽可能的只使用一个微服务，不同插件放在不同的包中
│  │
│  ├─cskefu-plugin-domain
│  │
│  └─cskefu-plugin-service
│
└─cskefu-websocket
    │
    ├─cskefu-websocket-domain
    │
    └─cskefu-websocket-service 客户端的WebSocket直接和这个服务连接
```

# 依赖、包、和类的约定

**统一的、简单的、一致的风格更有利于代码的维护，在依赖、包、和类的划分及命名上遵守以下约定。如不能请及时在开发者群反馈。**

## 依赖的管理

- 所有的依赖统一在cskefu-backend/pom.xml中dependencyManagement标签中管理，groupId为com.cskefu的依赖不需要遵守此约定；
-

除cskefu-backend/pom.xml的properties标签外，所有pom.xml中不允许写版本号，所有的版本号统一在cskefu-backend/pom.xml的properties标签中声明，groupId为com.cskefu的依赖不需要遵守此约定；

- 尽可能少的引入外部依赖。

因此，所有pom中请加入以下内容

```xml
<parent>
    <groupId>com.cskefu</groupId>
    <artifactId>cskefu-backend</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</parent>
```

如果包中有java代码，其对应的pom请加入以下内容

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.cskefu</groupId>
            <artifactId>cskefu-backend</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## 包和类的组织

类的全路径为com.cskefu.www.xxx.yyy.zzz.ooo.java，其中com.cskefu.xxx.yyy.zzz为包名，ooo为类名，即包最多6层，启动类放在com.cskefu下。

```
src
  └─main
      ├─java
      │  └─com
      │      └─cskefu
      │          ├─CskefuXXXApplication.java 启动类
      │          └─sys
      │             ├─SysUserController.java
      │             ├─SysUser.java        不需要和其他业务公用的实体类，如果需要公用，请放到对应的-domain下
      │             ├─SysUserService.java
      │             ├─SysUserServiceImpl.java
      │             ├─UserMapper.java
      │             └─UserMapper.xml
      └─resources
         └─application.properties 配置文件推荐使用properties，但不强制，后面会考虑扩展使用json格式
```

不要使用下面这种方式

```
├─controller
│     ├─SysUserController.java
│     ├─SysRoleController.java
│     └─SysPermissionService.java
└─service
      ├─SysUserService.java
      ├─SysRoleService.java
      └─SysPermissionService.java  
```

## 类的命名

- 按照包和类的组织中描述的方式命名，实体类不区分VO、BO、DO、POJO、DTO等，也不要使用这些后缀；
- 枚举类统一以Enum结尾；
- 工具类以Utils结尾，提供private无参构造函数，并在构造函数中抛出异常，参考JacksonUtils，阻止类被实例化；
- 除非必要，不需要定义异常类；

# 数据库变更

涉及到表结构的变更，或必要的初始化数据，请及时同步到contact-backend/v9.mysql.init.sql中。

# 其他

**统一的、简单的、一致的风格更有利于代码的维护，请遵守以下约定。如不能请及时在开发者群反馈。**

- 禁止使用Apache PropertyUtils、Apache BeanUtils等工具类，尽量不要使用Spring BeanUtils、Cglib BeanCopier，建议手写；
- 不要使用xxxMapper.xml文件，使用MyBatis注解代替；
- 不建议使用代码生成器，如果使用了，请删除不需要的代码；
