# Lib

默认使用 Maven 安装，本目录为备份。

```
    <repositories>
        <repository>
            <id>chatopera</id>
            <name>Chatopera Inc.</name>
            <url>https://nexus.chatopera.com/repository/maven-public/</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>true</enabled>
            </snapshots>
        </repository>
        ...
    </repositories>
```


## mondrian-3.7.0.jar

* 在 mondrian 的基础上调整，增加 UKTools，背景见 https://github.com/cskefu/cskefu/issues/878

```
<dependency>
  <groupId>mondrian</groupId>
  <artifactId>mondrian</artifactId>
  <version>3.7.0</version>
</dependency>
```
