# 创建springboot项目

```xml
<modelVersion>4.0.0</modelVersion>
<!--使用spring-boot-starter-parent只需指定springboot的版本号，在依赖其他starter的时候可以省略版本号的管理-->
<parent> <!-- 在pom文件中添加parent标签，加入spring-boot依赖 -->
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.5.RELEASE</version>
</parent>

<groupId>com.example</groupId>
<artifactId>demo</artifactId>
<version>0.0.1-SNAPSHOT</version>

<properties>
    <java.version>1.8</java.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope> 
        <!--运行时范围，runtime 依赖在运行和测试系统的时候需要，但在编译的时候不需要。比如可能在编译的时候只需要JDBC API JAR，而只有在运行的时候才需要JDBC驱动实现。-->
        <optional>true</optional> <!--55-->
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope> <!--55-->
    </dependency>
</dependencies>

<build>
    <plugins>
        <!--SpringBoot包含一个Maven插件，可以将项目打包为一个可执行JAR。-->
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

可以通过重写项目中的属性来修改各个依赖项的版本依赖。实例：

```xml
<properties>
	<spring-data-releasetrain.version>Fowler-SR2</spring-data-releasetrain.version>
</properties>
```

在不能使用parent的情况下使用dependencyManagement来引入springboot的依赖，通过使用scope=import

此种情况下不能使用properties属性来修改单个依赖

```xml
<dependencyManagement><!--？？？？？-->
	<dependencies>
		<dependency>
			<!-- Import dependency management from Spring Boot -->
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-dependencies</artifactId>
			<version>2.1.7.RELEASE</version>
			<type>pom</type>
			<scope>import</scope><!--？？？？？-->
		</dependency>
	</dependencies>
</dependencyManagement>
```

如果想要修改单个依赖只能在dependencyManagement中进行显示的添加依赖信息

```xml
<dependencyManagement>
	<dependencies>
		<!-- Override Spring Data release train provided by Spring Boot -->
		<dependency>
			<groupId>org.springframework.data</groupId>
			<artifactId>spring-data-releasetrain</artifactId>
			<version>Fowler-SR2</version>
			<type>pom</type>
			<scope>import</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-dependencies</artifactId>
			<version>2.1.7.RELEASE</version>
			<type>pom</type>
			<scope>import</scope>
		</dependency>
	</dependencies>
</dependencyManagement>
```

Starters的命名规则

springboot提供的的功能是以spring-boot-starter-*来命名

第三方集成springboot以thirdpartyproject-spring-boot-starter来命名



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

> 注解继承关系@SpringBootApplication = @EnableAutoConfiguration+@Configuration+@ComponentScan

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

例如:DruidDataSource数据源

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
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.1.10</version>
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

在默认配置文件中添加xml匹配路径，`多层路径是可以使用**代替，即com/test/**/*Mapper.xml`

```properties
mybatis.mapper-locations=classpath*:com/test/test/mapper/sqlXml/TestMapper.xml
```

在主配置程序文件中添加Mapper扫描，`扫描包范围不能太大，需指定mapper的最后一层路径地址`

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

application.properties和application.yml文件接受Spring样式的占位符${}

Maven使用@..@占位符。（可以通过设置名为resource.delimiter的maven属性来覆盖该属性。）???

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

# JPA

> 依赖

```xml
<!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <!--<version>2.1.6.RELEASE</version>-->
</dependency>
```

主配置文件配置jpa信息

```properties
spring.jpa.database=mysql
spring.jpa.show-sql=true
spring.jpa.generate-ddl=true
```

实体类

```java
@Data
@Entity //标注该类为实体类
@Table(name = "student") //映射到数据库中的表名
public class Student {

    /**
     * 表id主键字段
     * GeneratedValue id字段的自增方式
     */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    /**
     * 属性对应表中字段的名字，默认为属性名
     * 可以设置表中字段的一些属性信息
     */
    @Column(length=50)
    private String  name;

    @Column(length = 10)
    private int age;

    @Column(length = 255)
    private String address;

}
```

## JpaRepository

> 方法名映射查询数据库

```java
public interface StudentDao extends JpaRepository<Student,Integer> {

    Student findByName(String name);

    Student findByNameAndAge(String name,int age);

    List<Student> findByNameOrAge(String name, int age);
}
```

测试用例

```java
@Test
@Transactional
@Rollback(value = false)
public void saveStudent(){
    Student student = new Student();
    student.setId(1);
    student.setName("张三");
    student.setAddress("北京");
    student.setAge(20);
    studentDao.save(student);
}

@Test
public void findStudents(){
    List<Student> all = studentDao.findAll();
    System.out.println(Arrays.asList(all));
}

@Test
@Transactional
@Rollback(value = false)
public void saveStudents(){
    List<Student> stus = new ArrayList<>(2);
    Student lisi = new Student();
    lisi.setId(2);
    lisi.setName("李四");
    lisi.setAddress("北京");
    lisi.setAge(20);
    stus.add(lisi);

    Student summer = new Student();
    summer.setId(3);
    summer.setName("summer");
    summer.setAddress("北京");
    summer.setAge(20);
    stus.add(summer);
    studentDao.saveAll(stus);
}

@Test
public void findOneStudentByName(){
    Student summer = studentDao.findByName("summer");
    System.out.println(summer);
}

@Test
public void findByNameAndAge(){
    Student summer = studentDao.findByNameAndAge("summer", 10);
    System.out.println(summer);
}

@Test
public void findByNameOrAge(){
    List<Student> studentList = studentDao.findByNameOrAge("summer", 20);
    System.out.println(Arrays.asList(studentList));
}

@Test
public void findStudentsByAddress(){
    List<Student> studs = studentDao.findStudentsByAddress("北京");
    System.out.println(Arrays.asList(studs));
}
```

> 使用@Query注解

```java
public interface StudentDao extends JpaRepository<Student,Integer> {

    @Query(value = "from Student where address = ?1")
    List<Student> findStudentsByAddress(String address);

    @Query("from Student where age = ?2 and name = ?1")
    List<Student> findStudentsByNameAndAgeQuery(String name,int age);
}

```

测试实例

```java
@Test
public void findStudentsByAddress(){
    List<Student> studs = studentDao.findStudentsByAddress("北京");
    System.out.println(Arrays.asList(studs));
}

@Test
public void findStudentsByNameAndAgeQuery(){
    List<Student> studs = studentDao.findStudentsByNameAndAgeQuery("summer",20);
    System.out.println(Arrays.asList(studs));
}
```

> 使用原生sql `@query注解中添加nativeQuery = true属性`

```java
public interface StudentDao extends JpaRepository<Student,Integer> {
    @Query(value = "select * from student where id = ?",nativeQuery = true)
    List<Student> findAllBySql(int id);
}
```

 测试用例

```java
@Test
public void findAllBySql(){
    List<Student> allBySql = studentDao.findAllBySql(2);
    System.out.println(Arrays.asList(allBySql));
}
```

> 修改记录`@Modifying`

```java
public interface StudentDao extends JpaRepository<Student,Integer> {
    @Query("update Student set name = ?2 where id = ?1")
    @Modifying
    void updateStudentNameById(int id,String name);
}
```

注意一定要添加`@Modifying`注解，使用是必须添加`@Transactional`注解

```java
@Test
@Transactional
@Rollback(value = false)
public void updateStudentNameById(){
    studentDao.updateStudentNameById(1,"spring01");
}
```

注意该功能必须在有事务才能执行

## CrudRepository

创建接口继承CrudRepository

```java
public interface StudentCrud extends CrudRepository<Student,Integer> {

    @Query("from Student where id = ?1")
    Student findOne(int id);

}
```

> 使用CrudRepository保存实例

```java
@Test
public void saveStudentCrud(){
    Student student = new Student();
    student.setId(4);
    student.setName("ursh");
    student.setAge(25);
    student.setAddress("USA");
    studentCrud.save(student);
}
```

> 使用@Query注解

```java
public interface StudentCrud extends CrudRepository<Student,Integer> {
    @Query("from Student where id = ?1")
    Student findOne(int id);
}

@Test
public void findAllStudentsCrud(){
    Student student = studentCrud.findOne(4);
    System.out.println(student);
}
```

> 更新数据方式1

```java
@Test
public void updateStudentInfo(){
    Student student = studentCrud.findOne(4);
    System.out.println(student);
    student.setName("hero");
    studentCrud.save(student);
}
```

> 更新数据方式2

```java
/**
 * 加上事务后，student实例受事务的监控，一旦对象实例发生改变则数据库信息也将更新
 */
@Test
@Transactional
@Rollback(value = false)
public void updateStudentInfo2(){
    Student student = studentCrud.findOne(4);
    System.out.println(student);
    student.setName("haro");
}
```

## PagingAndSortingRepository

```java
public interface StudentPagingAndSortingRepository extends PagingAndSortingRepository<Student,Integer> {
}
```

> 分页

```java
//分页
@Test
public void studentPage1(){
    //当前页的索引。注意索引都是从 0 开始的
    int page = 0;

    //每页显示 3 条数据
    int size = 2;

    Pageable pageable = new PageRequest(page,size);

    Page<Student> all = studentPagingAndSortingRepository.findAll(pageable);
    long totalElements = all.getTotalElements();
    int totalPages = all.getTotalPages();
    List<Student> studentList = all.getContent();
    System.out.println("总数:"+totalElements+",总页数:"+totalPages+",详情:"+Arrays.asList(studentList));
}
```

> 排序

```java
@Test
public void studentSort(){
    Sort sort = new Sort(Sort.Direction.DESC,"id");
    Iterable<Student> all = studentPagingAndSortingRepository.findAll(sort);
    System.out.println(Arrays.asList(all));
    //        List<Student> all = studentPagingAndSortingRepository.findAll(sort);

}
```

# Redis

> 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

```

主配置文件配置redis信息

```properties
#redis
# Redis数据库索引（默认为0）
spring.redis.database=0
# Redis服务器地址
spring.redis.host=127.0.0.1
# Redis服务器连接端口
spring.redis.port=6379
# 连接池最大连接数（使用负值表示没有限制）
spring.redis.jedis.pool.max-active=200
# 连接池最大阻塞等待时间（使用负值表示没有限制
spring.redis.jedis.pool.max-wait=1s
# 连接池中的最大空闲连接
spring.redis.jedis.pool.max-idle=8
# 连接池中的最小空闲连接
spring.redis.jedis.pool.RedisAutoConfigurationRedisAutoConfigurationmin-idle=0
# 连接超时时间（毫秒）
spring.redis.timeout=1000ms
```

## RedisTemplate的自动配置原理

> RedisAutoConfiguration自动配置类

```java
@Configuration
@ConditionalOnClass({RedisOperations.class})
@EnableConfigurationProperties({RedisProperties.class})
@Import({LettuceConnectionConfiguration.class, JedisConnectionConfiguration.class})
public class RedisAutoConfiguration {
    public RedisAutoConfiguration() {
    }
    //添加判断如果IOC容器中没有redisTemplate的bean则创建该类，并注册到IOC容器中
    @Bean
    @ConditionalOnMissingBean(
        name = {"redisTemplate"}
    )
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
        RedisTemplate<Object, Object> template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    @ConditionalOnMissingBean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}

```

> 测试

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
public class RedisTest {
    //注入RedisTemplate实例
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test1(){
        this.redisTemplate.opsForValue().set("key", "test");
    }

    @Test
    public void getKey(){
        String key = redisTemplate.opsForValue().get("key").toString();
        System.out.println(key);
    }

    @Test
    public void saveObject(){
        Student student = new Student();
        student.setId(1);
        student.setName("lisa");
        student.setAddress("hongkong");
        student.setAge(16);
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.opsForValue().set("lisa",student);
    }

    @Test
    public void getObject(){
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        Object lisa = redisTemplate.opsForValue().get("lisa");
        System.out.println(lisa.toString());

    }

    @Test
    public void saveObjectForJSON(){
        Student student = new Student();
        student.setId(1);
        student.setName("poniu");
        student.setAddress("hongkong");
        student.setAge(5);
        redisTemplate.setValueSerializer(new
                Jackson2JsonRedisSerializer<>(Student.class));
        redisTemplate.opsForValue().set("poniu", student);
    }

    @Test
    public void test6(){
        this.redisTemplate.setValueSerializer(new
                Jackson2JsonRedisSerializer<>(Student.class));

        Student users =
                (Student)this.redisTemplate.opsForValue().get("poniu");
        System.out.println(users);
    }

}

```

简化spring项目的开发，简化spring对第三方库集成时的繁琐配置，实现一站式开发spring项目，为Spring开发提供一个更快、更广泛的入门体验。

# Spring Boot 2.1.7.RELEASE 版本支持

Java 8以上版本

 Spring Framework 5.1.9.RELEASE以上版本

Maven3.3+

Tomcat 9.0

servlet4.0

```java
public static void main(String[] args){
    //SpringApplication中传递Example.java来告诉SpringApplication该类为spring的主组件，args可以接受命令行传递的参数
    SpringApplication.run(Example.class, args);    
}

```

# 禁用功能

> 如果想禁用springboot中的指定配置项，可以使用exclude属性禁用该功能，或使用excludeName属性指定全类名

```java
@Configuration
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class MyConfiguration {
}
```

# Developer Tools

> springboot开发人员工具

springboot开发工具，当运行完全打包的应用程序时，将自动禁用开发人员工具

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional><!--????-->
</dependency>
```

# debug 方式启动springboot

```shell
$ java -jar myproject-0.0.1-SNAPSHOT.jar --debug
```

# CommandLineRunner 和 ApplicationRunner

> springboot 启动完成前SpringApplication.run(…)方法前会调用CommandLineRunner 和ApplicationRunner接口的实现

```java
@Component
public class MyBean implements CommandLineRunner {

    public void run(String... args) {
        // Do something...
    }

}
```

如果CommandLineRunner 的执行有顺序的，可以继承org.springframework.core.Ordered;

```java
@Component
public class MyRunnerStater implements CommandLineRunner, Ordered {
    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void run(String... args) throws Exception {
        // Do something...
    }
}
```

