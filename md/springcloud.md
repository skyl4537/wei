 # 问题总结

> RPC

remote Procedure Call 远程过程调用，一种技术思想，通过网络传输，在不需要了解底层通讯技术的情况下，完成跨服务间方法或接口的调用。RPC 的核心功能主要有 5 个部分组成，分别是：客户端、客户端 Stub（客户端存根）、网络传输模块、服务端 Stub（服务端存根）、服务端等。

![](assets/rpc.jpg)

rpc调用过程:

1.客户端通过本地调用的方式调用服务

2.客户端存根(Client Stub)接收到调用请求后负责将方法、入参等信息序列化(组装)成能够进行网络传输的消息体

3.客户端存根(Client Stub)找到远程的服务地址，并且将消息通过网络发送给服务端。

4.服务端存根(Server Stub)收到消息后进行解码(反序列化操作)。

5.服务端存根(Server Stub)根据解码结果调用本地的服务进行相关处理

6.服务端(Server)本地服务业务处理。

7.处理结果返回给服务端存根(Server Stub)。

8.服务端存根(Server Stub)序列化结果。

9.服务端存根(Server Stub)将结果通过网络发送至消费方。

10.客户端存根(Client Stub)接收到消息，并进行解码(反序列化)。

11.服务消费方得到最终结果。

> 什么是微服务

化整为零，一种将单个应用程序，作为一个小型的服务开发的方法

>微服务之间是如何进行独立通讯的

微服务之间通过restful API进行通讯，底层基于http通讯

> springcloud和Dubbo的区别

dubbo基于RPC远程过程调用

springcloud基于RESTful API调用

> springboot与SpringCloud的理解

springcloud是一个基于springboot实现的一系列框架的集合，包括eureka、hystrix、ribbon、fegin、zuul、config等框架的集合

> 什么是服务熔断？什么是服务降级

> 微服务的优缺点是什么？

> 你所知道的微服务技术栈有哪些

> eureka和zookeeper都可以提供服务注册与发现，区别是什么



eureka服务,服务端，客户端都需向eureka注册器注册自身信息

ribbon  添加在客户端，客户端对多服务端的访问进行负载均衡

ribbon  需配合RestTemplate 才可实现负载 

Feign 负载均衡

Fegin+hystrix熔断降级处理

HystrixDashboard 服务监控，只能通过客户端进行监控，不能直接监控服务端

zuul网关 @EnableZuulProxy

# 父项目依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.5.RELEASE</version>
    </parent>
    
    <modules>
        <module>wei_eureka</module>
    </modules>

    <groupId>com.bluecard.wei</groupId>
    <artifactId>wei_parent</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging><!--父项目打包类型必须为pom类型-->

    <properties>
        <!--项目编码格式-->
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <!--项目编译jdk版本-->
        <maven.compiler.source>1.8</maven.compiler.source>
        <!--项目运行环境版本-->
        <maven.compiler.target>1.8</maven.compiler.target>
        <!--springcloud版本-->
        <spring-cloud.version>Greenwich.SR2</spring-cloud.version>
    </properties>

    <!--父项目对子项目中版本依赖管理-->
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <!--将spring-cloud-dependencies的所有依赖都下载下来-->
                <type>pom</type>
                <!--scope=import只能用在dependencyManagement里面,且仅用于type=pom的dependency-->
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope> <!--只在运行时起作用-->
            <!--该属性为true,表示子项目必须显示的引入对devtools的依赖，否则子项目不会引入这个依赖 -->
            <optional>true</optional><!--可选依赖-->
        </dependency>
    </dependencies>

</project>
```

# eureka子项目

## 依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
</dependencies>
```

## 配置文件

```properties
server.port=9011

#该eureka服务在集群中的的应用名称
eureka.instance.hostname=weiEureka1

#只把当前程序当做eureka-server 而不充当eureka-client的角色 防止将自身当做Eureka客户端
eureka.client.fetch-registry=false
eureka.client.register-with-eureka=false
#配置Eureka注册中心的地址
eureka.client.service-url.defaultZone=http://weiEureka2:9012/eureka

```

## 主启动类

```java
@SpringBootApplication
//开启eureka服务器功能
@EnableEurekaServer
public class EurekaApplication9011 {
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication9011.class,args);
    }
}
```

# API子项目

## 依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>wei_parent</artifactId>
        <groupId>com.bluecard.wei</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>wei_api</artifactId>
    <dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!--添加feign 负载均衡依赖-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
    </dependencies>
</project>
```

## 实体类

```java
//自动生成无参构造器
@NoArgsConstructor
//自动添加属性set get toString方法
@Data
//setter返回的this而不是void,可以进行级联调用
@Accessors(chain = true)
public class Book {
    private int bookId;
    private String bookName;
    private double bookPrice;
    private String bookAuthor;
}
```

## 服务接口

```java
@Component
//value为服务端注册到eureka注册中心的spring.application.name的值
@FeignClient(value = "BOOKSHOP",fallbackFactory = UserFallbackFactory.class)//FeignClient?????
public interface UserService {

    @RequestMapping(value = "/shop/book/{id}")
    public Book findBookById(@PathVariable("id") int id);
}
```

## hystrix熔断降级

```java
@Component//必须添加此注解
public class UserFallbackFactory implements FallbackFactory<UserService> {
    @Override
    public UserService create(Throwable throwable) {
        return new UserService() {
            @Override
            public Book findBookById(int id) {
                return new Book().setBookId(id).setBookName("无").setBookAuthor("无").setBookPrice(0).setDbSource("无");
            }
        };
    }
}
```

`执行maven install 将api的jar添加到maven仓库`

# 服务端子项目

## 依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>wei_parent</artifactId>
        <groupId>com.bluecard.wei</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>wei_bookshop1</artifactId>

    <dependencies>
        <!--添加自定义api包依赖 版本信息根据项目的版本自动引入-->
        <dependency>
            <groupId>com.bluecard.wei</groupId>
            <artifactId>wei_api</artifactId>
            <version>${project.version}</version>
        </dependency>
        <!--引入该依赖后，如果不指定eureka服务器地址将会启用默认地址
        http://localhost:8761/eureka/
        ，连接不到则会报错-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <!--mybatis druid自己主动向springboot做兼容 不在springboot管理范围内 需添加版本信息-->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.0.1</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.17</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!--
			spring-cloud-config client 端需引入此依赖
		-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <artifactId>maven-war-plugin</artifactId>
                <version>3.0.0</version>
            </plugin>
        </plugins>
        <resources><!--???-->
            <resource>
                <directory>src/main/java</directory>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
            </resource>
        </resources>
    </build>
</project>
```

## 配置文件

> application.properties springboot默认配置文件

```properties
server.port=8011

spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://127.0.0.1:33306/test0601?useSSL\=false&serverTimezone=GMT%2B8
spring.application.name=bookshop

mybatis.mapper-locations=classpath*:com/bluecard/wei/**/*Mapper.xml

#配置Eureka注册中心的地址
eureka.client.service-url.defaultZone=http://localhost:9010/eureka
#服务实例在eureka界面显示版本号
eureka.instance.instance-id=bookshop1client
#服务实例在eureka界面显示ip:port地址
eureka.instance.prefer-ip-address=true
```

> bootstrap.properties系统配置文件 配合springcloud-config使用

```properties
#在git上配置文件的名称，在程序启动时会访问config-server，server将从git上拉取配置文件到本地
spring.cloud.config.name=bookshop
#在git上所在分支
spring.cloud.config.label=master
#config-server的访问地址
spring.cloud.config.uri=http://192.168.5.23:4001
```

> git@bookshop.properties

```properties
#自动随机获取端口，必须=0，否则注册eureka服务时端口注册的与程序本身不一致
server.port=0

spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://127.0.0.1:33306/test0601?useSSL=false&serverTimezone=GMT%2B8
spring.application.name=bookshop

mybatis.mapper-locations=classpath*:com/bluecard/wei/**/*Mapper.xml

#配置Eureka注册中心的地址
eureka.client.service-url.defaultZone=http://localhost:9010/eureka
#服务实例在eureka界面显示版本号,当jar多次启动时，端口自动分配，实例名随机生成，才能实现负载均衡，相同实例名会覆盖
eureka.instance.instance-id=${spring.application.name}:${random.int[10000,19999]}
#服务实例在eureka界面显示ip:port地址
eureka.instance.prefer-ip-address=true

#仪表板访问配置
management.endpoints.web.exposure.include=*
```



## 主启动类

```java
@SpringBootApplication
@MapperScan(value = {"com.bluecard.*.mapper"})
//仅适用于eureka的服务发现
@EnableEurekaClient
//可使用其他的服务发现
@EnableDiscoveryClient
public class Shop1App {
    public static void main(String[] args) {
        SpringApplication.run(Shop1App.class, args);
    }
}
```

## controller

```java
@RestController
public class Shop1Controller {

    @Autowired
    private Shop1Service shop1Service;

    @RequestMapping(value = "/shop/book/{id}")
    public Book findById(@PathVariable("id") int id){
        Book book = shop1Service.findBookById(id);
        //当服务端出现错误时，一定要throw异常，客户端收到异常后，会进行熔断处理
        if(null==book){
            throw new RuntimeException("该ID：" + id + "没有没有对应的信息");
        }
        return book;
    }
}
```



# 客户端子项目

## 依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>wei_parent</artifactId>
        <groupId>com.bluecard.wei</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>wei_user1</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>com.bluecard.wei</groupId>
            <artifactId>wei_api</artifactId>
            <version>${project.version}</version>
        </dependency>
        <!--消费者必须向eureka服务注册，必须添加ribbon才能进行负载均衡-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <!--添加 hystrix的客户端依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
    </dependencies>

</project>
```

## 配置文件

```properties
server.port=7001

eureka.client.register-with-eureka=true
eureka.client.service-url.defaultZone=http://localhost:9010/eureka

#服务熔断+降级必须添加此配置
feign.hystrix.enabled=true

#服务监控开启允许访问
management.endpoints.web.exposure.include=*
```

## 主启动类

```java
@SpringBootApplication
//注册feign客户端
@EnableFeignClients
//启动Hystrix客户端
@EnableHystrix
public class User1App {
    public static void main(String[] args) {
        SpringApplication.run(User1App.class,args);
    }
}
```

## controller

```java
@RestController
public class UserController {
    /**
     * UserService为api中定义的FeignClient的接口
     */
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/user/book/{id}")
    public Book findBookById(@PathVariable("id") int id){
        return userService.findBookById(id);
    }
}
```

# 监控子项目

## 依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>wei_parent</artifactId>
        <groupId>com.bluecard.wei</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>wei_dashboard</artifactId>

    <dependencies>
        <!--添加 服务监控依赖-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
        </dependency>
    </dependencies>
</project>
```

## 配置文件

```properties
server.port=6001
```

## 主启动类

```java
@SpringBootApplication
//开启服务监控
@EnableHystrixDashboard
public class DashApp {
    public static void main(String[] args) {
        SpringApplication.run(DashApp.class,args);
    }
}
```

监控首页地址

```http
http://localhost:6001/hystrix
```

监控客户端运行情况地址

```http
http://127.0.0.1:7001/actuator/hystrix.stream
```

# 网关子项目

## 依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>wei_parent</artifactId>
        <groupId>com.bluecard.wei</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>wei_gate</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
    </dependencies>

</project>
```

## 配置文件

```properties
server.port=5001

spring.application.name=gateclient
#配置eureka注册中心
eureka.client.service-url.defaultZone=http://localhost:9010/eureka
eureka.instance.instance-id=gateclient
eureka.instance.prefer-ip-address=true

#zuul
#配置请求URL的请求规则，指定Eureka注册中心中的服务id，转发请求头（默认过滤请求头）
zuul.routes.shop.path=/book/**
zuul.routes.shop.service-id=BOOKSHOP
zuul.routes.shop.custom-sensitive-headers=true

```

## 主启动类

```java
@SpringBootApplication
//开启网关代理
@EnableZuulProxy
@EnableEurekaClient
public class GateApp {
    public static void main(String[] args) {
        SpringApplication.run(GateApp.class,args);
    }
}
```

根据配置网关访问服务端地址

```http
http://127.0.0.1:5001/book/shop/book/1
```

# config服务子项目

> 依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>wei_parent</artifactId>
        <groupId>com.bluecard.wei</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>wei_configserver</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>
    </dependencies>

    <build>
        <finalName>wei-configserver</finalName>
    </build>

</project>
```

> 配置文件

```properties
server.port=4001
spring.application.name=configserver

#git地址
spring.cloud.config.server.git.uri=https://gitee.com/WWArcher/wei-config.git
#git登录用户名
spring.cloud.config.server.git.username=1209873345@qq.com
#git登录密码
spring.cloud.config.server.git.password=wei@863086239
```

> 主启动类

```java
@SpringBootApplication
@EnableConfigServer
public class ConfigApp {
    public static void main(String[] args) {
        SpringApplication.run(ConfigApp.class,args);
    }
}
```

# eureka-server集群

> eureka-server端

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
</dependencies>
```

> eureka-server-9011配置文件

```properties
server.port=9011
#集群中的名字
eureka.instance.hostname=weiEureka1

#只把当前程序当做eureka-server 而不充当eureka-client的角色 防止将自身当做Eureka客户端
eureka.client.fetch-registry=false
eureka.client.register-with-eureka=false
#配置Eureka注册中心的地址，将自己注册到别的eureka服务中心，构建eureka集群
eureka.client.service-url.defaultZone=http://weiEureka2:9012/eureka
```

> eureka-server-9011主启动类

```java
@EnableEurekaServer
```

> eureka-server-9012配置文件

```properties
server.port=9012

eureka.instance.hostname=weiEureka2
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false

eureka.client.service-url.defaultZone=http://weiEureka1:9011/eureka
```

> eureka-server-9012主启动类

```java
@EnableEurekaServer
```

# eureka-server单机

> 依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
</dependencies>
```

> 配置文件

```properties
eureka.instance.hostname=localhost
eureka.client.fetch-registry=false
eureka.client.register-with-eureka=false
```

> 主配置类

```java
@EnableEurekaServer
```

# eureka-client

> 依赖

```xml
<!--引入该依赖后，如果不指定eureka服务器地址将会启用默认地址
        http://localhost:8761/eureka/
        ，连接不到则会报错-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

> 配置文件

```properties
spring.application.name=bookshop
#配置Eureka注册中心的地址

#单机版
eureka.client.service-url.defaultZone=http://localhost:9010/eureka

#集群版
#eureka.client.service-url.defaultZone=http://localhost:9011/eureka,http://localhost:9012/eureka

#服务实例在eureka界面显示版本号
eureka.instance.instance-id=bookshop1client
#服务实例在eureka界面显示ip:port地址
eureka.instance.prefer-ip-address=true
```

> 主启动类

```java
@EnableEurekaClient
```

# Fegin+hystrix

> 依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

> feign&客户端  = 服务降级+熔断处理配置

```properties
#服务熔断+降级必须添加此配置
feign.hystrix.enabled=true
```

> 主启动类

```java
@EnableFeignClients
```

> 定义与服务端controller类相同定义的方法的接口

`@FeignClient中添加服务端的微服务名称，fallbackFactory为降级熔断处理`

```java
@Component
//value为服务端注册到eureka注册中心的spring.application.name的值
@FeignClient(value = "BOOKSHOP",fallbackFactory = UserFallbackFactory.class)//FeignClient?????
public interface UserService {

    @RequestMapping(value = "/shop/book/{id}")
    public Book findBookById(@PathVariable("id") int id);
}
```

```java
@Component//必须添加此注解
public class UserFallbackFactory implements FallbackFactory<UserService> {
    @Override
    public UserService create(Throwable throwable) {
        return new UserService() {
            @Override
            public Book findBookById(int id) {
                return new Book().setBookId(id).setBookName("无").setBookAuthor("无").setBookPrice(0).setDbSource("无");
            }
        };
    }
}
```

# HystrixDashboard

> 监控服务程序

> 依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
</dependency>
```

> 主启动类

```java
@EnableHystrixDashboard
```

> 客户端

> 依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

> 配置文件

```properties
#服务监控开启允许访问
management.endpoints.web.exposure.include=*
```

> 主启动类

```java
@EnableHystrix
```

# zuul

> zuul微服务依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

> 配置文件

```properties
spring.application.name=gateclient

eureka.client.service-url.defaultZone=http://localhost:9010/eureka
eureka.instance.instance-id=gateclient
eureka.instance.prefer-ip-address=true

#zuul
#配置请求URL的请求规则，指定Eureka注册中心中的服务id，转发请求头（默认过滤请求头）
zuul.routes.shop.path=/book/**
zuul.routes.shop.service-id=BOOKSHOP
zuul.routes.shop.custom-sensitive-headers=true

zuul.routes.user.path=/user/**
zuul.routes.user.service-id=USERCLIENT
zuul.routes.user.custom-sensitive-headers=true
```

> 主启动类

```java
@EnableZuulProxy
@EnableEurekaClient
```

# config-server

> 依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

> 配置文件

```properties
#仓库路径地址
spring.cloud.config.server.git.uri=https://gitee.com/WWArcher/wei-config.git
#git登录用户名
spring.cloud.config.server.git.username=1209873345@qq.com
#git登录密码
spring.cloud.config.server.git.password=wei@863086239
```

> 主启动类

```java
@EnableConfigServer
```

git上创建仓库，并复制仓库路径地址，配置在config-server的配置文件中

# config-client

> 依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

> resources下创建bootstrap.properties系统级别配置文件

applicaiton是用户级的资源配置项

bootstrap是系统级的，优先级更加高

```properties
#git仓库中配置文件的名称
spring.cloud.config.name=bookshop
#所属git分支
spring.cloud.config.label=master
#config-server访问路径地址
spring.cloud.config.uri=http://192.168.5.23:4001
```

> 负载均衡配置文件配置

```properties
#自动随机获取端口，必须=0，否则注册eureka服务时端口注册的与程序本身不一致
server.port=0
spring.application.name=bookshop
#服务实例在eureka界面显示版本号,当jar多次启动时，端口自动分配，实例名随机生成，才能实现负载均衡，相同实例名会覆盖
eureka.instance.instance-id=${spring.application.name}:${random.int[10000,19999]}
```



