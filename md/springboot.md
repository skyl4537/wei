# 创建springboot项目

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.5.RELEASE</version>
</parent>
```

 在pom文件中添加parent标签，加入spring-boot依赖

# 打包

```xml
<packaging>war</packaging>
    <!-- war 需要部署的项目-->
    <!-- jar 内部调用或者是作服务使用-->
```

# 添加web模块

在依赖中添加

```xml
<dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

可去除内置tomcat

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>      
```

去除tomcat模块后想要使用web原生的api需要导入servlet-api依赖

```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>servlet-api</artifactId>
    <version>2.5</version>
    <scope>provided</scope> 
</dependency>
```

scope可以使用5个值： 

compile，缺省值，适用于所有阶段，会随着项目一起发布。如果没有提供一个范围，那该依赖的范围就是编译范围。编译范围依赖在所有的classpath 中可用，同时它们也会被打包。

provided，类似compile，期望JDK、容器或使用者会提供这个依赖。如servlet.jar。 例如， 如果你开发了一个web 应用，你可能在编译 classpath 中需要可用的Servlet API 来编译一个servlet，但是你不会想要在打包好的WAR 中包含这个Servlet API；这个Servlet API JAR 由你的应用服务器或者servlet 容器提供。已提供范围的依赖在编译classpath （不是运行时）可用。它们不是传递性的，也不会被打包。

runtime，只在运行时使用，如JDBC驱动，适用运行和测试阶段。 

test，只在测试时使用，用于编译和运行测试代码。不会随项目发布。 

system，类似provided，需要显式提供包含依赖的jar，Maven不会在Repository中查找它。不推荐使用

# 添加主配置类

```javascript
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }

}
```

去除内置tomcat时，需继承SpringBootServletInitializer类，是外部tomcat能正常启动项目

```java
public class MyWebApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }
}
```

继承SpringBootServletInitializer的类位置与Application主配置类必须在同级目录下

可将两个类合并为一个

```java
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }
}
```

# @SpringBootApplication

> 注解继承关系

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = {
      @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
      @Filter(type = FilterType.CUSTOM,
            classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {...}
```

> SpringBootConfiguration注解

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
public @interface SpringBootConfiguration {

}
```

@Configuration表示该类为配置类

> EnableAutoConfiguration类

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {}
```

会导入AutoConfigurationImportSelector类，包扫描组件选择器

```java
public class AutoConfigurationImportSelector
		implements DeferredImportSelector, BeanClassLoaderAware, ResourceLoaderAware,
		BeanFactoryAware, EnvironmentAware, Ordered {
	public String[] selectImports(AnnotationMetadata annotationMetadata) {
		if (!isEnabled(annotationMetadata)) {
			return NO_IMPORTS;
		}
		AutoConfigurationMetadata autoConfigurationMetadata = AutoConfigurationMetadataLoader
				.loadMetadata(this.beanClassLoader);
		AutoConfigurationEntry autoConfigurationEntry = getAutoConfigurationEntry(
				autoConfigurationMetadata, annotationMetadata);
		return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
	}
    protected AutoConfigurationEntry getAutoConfigurationEntry(
			AutoConfigurationMetadata autoConfigurationMetadata,
			AnnotationMetadata annotationMetadata) {
		if (!isEnabled(annotationMetadata)) {
			return EMPTY_ENTRY;
		}
		AnnotationAttributes attributes = getAttributes(annotationMetadata);
		List<String> configurations = getCandidateConfigurations(annotationMetadata,
				attributes);
		configurations = removeDuplicates(configurations);
		Set<String> exclusions = getExclusions(annotationMetadata, attributes);
		checkExcludedClasses(configurations, exclusions);
		configurations.removeAll(exclusions);
		configurations = filter(configurations, autoConfigurationMetadata);
		fireAutoConfigurationImportEvents(configurations, exclusions);
		return new AutoConfigurationEntry(configurations, exclusions);
	}
            
    protected List<String> getCandidateConfigurations(AnnotationMetadata metadata,
			AnnotationAttributes attributes) {
		List<String> configurations = SpringFactoriesLoader.loadFactoryNames(
				getSpringFactoriesLoaderFactoryClass(), getBeanClassLoader());
		Assert.notEmpty(configurations,
				"No auto configuration classes found in META-INF/spring.factories. If you "
						+ "are using a custom packaging, make sure that file is correct.");
		return configurations;
	}
}

SpringFactoriesLoader.loadFactoryNames
会将类路径下org.springframework.boot.autoconfigure.EnableAutoConfiguration包中
META-INF/spring.factories配置文件中的
org.springframework.boot.autoconfigure.EnableAutoConfiguration的所有配置导入到容器中，自动配置类就可以生效了

```

> AutoConfigurationPackage注解

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(AutoConfigurationPackages.Registrar.class)
public @interface AutoConfigurationPackage {
}
```

会引入AutoConfigurationPackages.Registrar类,该类实现了ImportBeanDefinitionRegistrar类，向spring容器注册bean

```java
static class Registrar implements ImportBeanDefinitionRegistrar, DeterminableImports {

		@Override
		public void registerBeanDefinitions(AnnotationMetadata metadata,
				BeanDefinitionRegistry registry) {
			register(registry, new PackageImport(metadata).getPackageName());
		}

		@Override
		public Set<Object> determineImports(AnnotationMetadata metadata) {
			return Collections.singleton(new PackageImport(metadata));
		}

	}
```

# dataSource配置

添加依赖

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.1.10</version>
</dependency>
```

springboot默认使用的com.zaxxer.hikari.HikariDataSource 数据源

数据源相关配置都在 DataSourceProperties 里

自动配置相关的信息在org.springframework.boot.autoconfigure.jdbc包中

DataSourceConfiguration类是根据配置信息来生成数据源

使用spring.datasource.type属性来指定使用哪种数据源类型，默认可以支持四种:

```xml
1.org.apache.tomcat.jdbc.pool.DataSource
2.com.zaxxer.hikari.HikariDataSource
3.org.apache.commons.dbcp2.BasicDataSource
4.自定义数据源
```

> 自定义数据源原理

```java
@ConditionalOnMissingBean(DataSource.class)
@ConditionalOnProperty(name = "spring.datasource.type")
static class Generic {

	@Bean
	public DataSource dataSource(DataSourceProperties properties) {
		//使用DataSourceBuilder创建数据源,利用反射机制来创建相应type的数据源，并绑定相关属性
		return properties.initializeDataSourceBuilder().build();
	}
}
```

例如:c3p0数据源

```properties
#Druid
#使用Druid自定义数据源类型
#application.properties中指定数据源类型
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
```

pom文件添加依赖

```xml
<dependency>
	<groupId>com.alibaba</groupId>
	<artifactId>druid</artifactId>
	<version>1.1.14</version>
</dependency>
```

默认配置文件中配置DataSource信息

```properties
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.username=bluecardsoft
spring.datasource.password=#$%_BC13439677375
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://127.0.0.1:33306/parkcloud?useSSL\=false&serverTimezone=GMT%2B8
```

数据源配置不需要额外的配置，直接在默认文件中进行配置，springboot会自动生成配置源，并将其注入到spring容器中

# mybatis配置

引入依赖

```xml
 <dependency>
      <groupId>org.mybatis.spring.boot</groupId>
      <artifactId>mybatis-spring-boot-starter</artifactId>
      <version>2.0.1</version>
 </dependency>
```

加入插件的作用 打包时将xml资源打包jar或war包中

```xml
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
```

在默认配置文件中添加xml匹配路径

```properties
mybatis.mapper-locations=classpath*:com/test/test/mapper/sqlXml/TestMapper.xml
```

在主配置程序文件中添加Mapper扫描

```java
@MapperScan("com.bluecard.*.mapper")
```

mapper.xml文件中添加dtd模板

```xml
<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd"> <!-- ???-->
<mapper namespace="com.test.test.mapper.TestMapper">
    <select id="testOne" resultType="String">
        select number from cloud_park limit 1;
    </select>
</mapper>
```

# 配置文件

>springboot配置文件支持properties和yml两种形式

配置文件的名称固定:

```xml
application.properties
application.yml
```

## yml语法

k:(空格)v: 表示一对键值对

以空格缩进来表示层级关系，左对齐的都是同一层级的属性

属性和值大小写敏感

值的写法

```xml
k: v 字面量 
字符串默认不用添加双引号或者单引号
"":双引号不会转义字符串中的特殊字符 name: "zhangsan \n lisi" 输出结果 zhangsan 换行 lisi
'': 会转义特殊字符，特殊字符只会当做一个字符串处理 name: "zhangsan \n lisi" 输出结果 zhangsan \n lisi
```

对象的写法

```yml
person:(类)
	lastName(属性): hello(值)
	age: 18
```

## profile多配置文件

>文件名称为:application-{profile}.properties/yml，默认使用的是application.properties

在有yml和properties都有的情况下默认使用properties配置文件，即便yml中有配置dev的环境，也会优先使用 dev.properties配置文件

> 多配置激活指定profile

```properties
#a) 在application.properties配置文件中指定激活哪种环境
spring.profiles.active=dev 
#dev必须与application-dev.properties文件名中的dev相同
```

```yaml
b) yml文件多配置编写
---表示yml文件间隔符

server:
  port: 8090
spring:
  profiles:
	active: dev
---
server:
  port: 80
spring:
  profiles: dev

---
server:
	 port: 8083
spring:
  profiles: prod
```

## 配置文件加载位置

springboot会扫描一下位置的配置文件来使用

```properties
-file:./config/
-file:./
-classpath:/config/
-classpath:/
优先级由高到低，高优先级覆盖低优先级配置，互补
		 
修改默认配置文件的位置：
spring.config.location=D:/config
```







# @PropertySource 

> 读取配置文件,只能读取后缀为.properties的文件，该注解只能标注在类上

```java
@PropertySource(value={"classpath:person.properties"})
```

# @ConfigurationProperties

>默认从全局配置文件中获取值

将本类中的所有属性与配置文件中的相关配置进行绑定

prefix = "person" 配置文件中那个下面的所有属性进行一一映射

不支持spring表达式语言

支持JSR303数据校验

```java
@Component
@ConfigurationProperties(prefix = "person")
@Validated
public class Person {
	@Email
	private String lastName;
	...
}
```

application.yml配置文件中配置

```yml
person:
	lastName: hello
	age: 18
	boss: false
	birth: 2019/03/01
	maps: {k1: v1 , k2: hh}
	lists:
		- haha
		- jiji
		- nvnv
	dog:
		name: koko
		age: 1
	list2:[cat,dog,pig]
```

# @value

>将配置文件中的值赋给对象

即为property标签中value的属性值,因此支持

```xml
${key} 从环境变量和配置文件中读取值
#{SpEL}
<bean class="com.wei.bean.Person">
	<property name="lastName" value="?"></property>
</bean>
```

默认读取application.properties也可使用@PropertySource注解指定配置文件中读取值

```java
@PropertySource(value={"classpath:person.properties"})
@Component
//@ConfigurationProperties(prefix = "person")
public class Person {
    @Value("${person.last-name}")
    private String lastName;
}
```

# @ImportResource

>导入自定义配置文件，使配置文件中的配置生效

```java
@ImportResource(locations={"classpath:spring-xml.xml"})配合@Configuration一起使用
```

在主配置类中添加@ImportResource注解，使自定义配置文件中的配置生效，不常使用

如果在编写自定义配置文件时，配置文件想要使用提示，可引入下面的依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

# thymeleaf

pom文件依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

`把html文件放在classpath:/templates/文件夹下，thymeleaf可以自动渲染`

需导入thymeleaf的名称空间

```html
<html xmlns:th="http://www.thymeleaf.org">
```

## thymeleaf语法规则

> th:text	改变当前元素的文本内容

th:任意html属性 	替换html原生属性值

```html
<div th:id="${hello}" th:class="${hello}" th:text="${hello}"></div>
```

>表达式语法

```xml
${}		获取值
*{}		选择表达式与${}功能相同，补充配合th:object=${session.user} 使用 *{firstName} * 表示 ${session.user}
#{}		获取国际化内容
@{}		定义url链接 th:href=@{/pa(k1=v1,k2=v2)}  th:href=@{/pa(k1=v1,k2=v2)} /代表当前项目
~{}		片段引入表达式
```

>th:each 每次遍历都会生成当前标签

```html
<h3 th:each="user:${users}" th:text="${user}"></h3>
```

> 行内写法

```html
[[ ]] = th:text ; [( )] = th:utext 
```

