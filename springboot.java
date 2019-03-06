1.pom文件依赖{
	<parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.0.RELEASE</version>
    </parent>
	spring-boot-starter-parent的父类为:
	<parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-dependencies</artifactId>
        <version>2.1.0.RELEASE</version>
        <relativePath>../../spring-boot-dependencies</relativePath>
    </parent>
	spring-boot-dependencies中<properties>对jar依赖版本信息进行管理
	
	spring-boot-starter:spring boot场景启动器
	spring-boot-starter-web:springboot使用web模块，即将web模块正常运行所依赖的组件进行加载
}

2.主程序类{
	1.必须标注@SpringBootApplication注解{
		@SpringBootApplication
				===>@SpringBootConfiguration
					===>@Configuration
						===>@Component(spring配置类)
				===>@EnableAutoConfiguration(开启自动配置功能)
					===>@AutoConfigurationPackage(自动配置包)
						将主程序所在包及所有子包里的组件扫描到spring容器中
						===>@Import(AutoConfigurationPackages.Registrar.class)
							由AutoConfigurationPackages.Registrar决定导入哪些组件
							static class Registrar implements ImportBeanDefinitionRegistrar, DeterminableImports {
								@Override
								public void registerBeanDefinitions(AnnotationMetadata metadata,
										BeanDefinitionRegistry registry) {
									register(registry, new PackageImport(metadata).getPackageName());
								}}
					===>@Import(AutoConfigurationImportSelector.class)扫描包组件选择器
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
						
						selectImports该方法返回一系列组件List<String> configurations，其中configurations中包含很多自动配置类
						List<String> configurations = SpringFactoriesLoader.loadFactoryNames(
													getSpringFactoriesLoaderFactoryClass(), getBeanClassLoader());
							会将类路径下org.springframework.boot.autoconfigure.EnableAutoConfiguration包中
							META-INF/spring.factories配置文件中的
							org.springframework.boot.autoconfigure.EnableAutoConfiguration的所有配置导入到容器中，自动配置类就可以生效了
	}
}

3.配置文件{
	springboot全局配置文件：
		springboot配置文件支持properties和yml两种形式
		配置文件的名称固定:
			application.properties
			application.yml
		同时存在application.properties和application.yml文件时默认使用application.properties
		
	yml语法{
		k:(空格)v: 表示一对键值对
		以空格缩进来表示层级关系，左对齐的都是同一层级的属性
		属性和值大小写敏感
		
		值的写法
			k: v: 字面量 
			字符串默认不用添加双引号或者单引号
			"":双引号不会转义字符串中的特殊字符 name: "zhangsan \n lisi" 输出结果 zhangsan 换行 lisi
			'': 会转义特殊字符，特殊字符只会当做一个字符串处理 name: "zhangsan \n lisi" 输出结果 zhangsan \n lisi
		
		对象的写法
			person:(类)
			  lastName(属性): hello(值)
			  age: 18
		
	}
	
	@PropertySource 读取配置文件,只能读取后缀为.properties的文件，只能标注在类上
		@PropertySource(value={"classpath:person.properties"})
		
	
	@ConfigurationProperties
		默认从全局配置文件中获取值
		将本类中的所有属性与配置文件中的相关配置进行绑定
		prefix = "person" 配置文件中那个下面的所有属性进行一一映射
		不支持spring表达式语言
		支持JSR303数据校验
		 
		@Component
		@ConfigurationProperties(prefix = "person")
		@Validated
		public class Person {
			@Email
			private String lastName;
			...
		}
		application.yml配置文件中配置
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
		   
	@value将配置文件中的值赋给对象
		即为property标签中value的属性值,因此支持
			${key} 从环境变量和配置文件中读取值
			#{SpEL}
		 <bean class="com.wei.bean.Person">
			 <property name="lastName" value="?"></property>
		 </bean>
	
		默认读取application.properties也可使用@PropertySource注解指定配置文件中读取值
		@PropertySource(value={"classpath:person.properties"})
		@Component
		//@ConfigurationProperties(prefix = "person")
		public class Person {
			@Value("${person.last-name}")
			private String lastName;
		}
	
	@ImportResource导入自定义配置文件，使配置文件中的配置生效
		@ImportResource(locations={"classpath:spring-xml.xml"})配合@Configuration一起使用
	
	
	在配置文件中有提示可使用,添加文件处理器依赖
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-configuration-processor</artifactId>
			<optional>true</optional>
		</dependency>

	
	配置文件占位符
		1.随机数
			${random.value}/${random.int}
		2.占位符获取之前配置过的值，如果没找到则使用默认值
			person.last-name=koko${random.uuid}
			person.dog.name=${person.last-name:koko}_dog
			如果未找到person.last-name，则使用koko作为默认值
	profile多配置文件
		文件名称为:application-{profile}.properties/yml
		默认使用的是application.properties
		在有yml和properties都有的情况下默认使用properties配置文件，即便yml中有配置dev的环境，也会优先使用 dev.properties配置文件
		
		多配置激活指定profile
			a) 在application.properties配置文件中指定激活哪种环境
				spring.profiles.active=dev dev必须与application-dev.properties文件名中的dev相同
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
	配置文件加载位置
		springboot会扫描一下位置的配置文件来使用
		 -file:./config/
		 -file:./
		 -classpath:/config/
		 -classpath:/
		 优先级由高到低，高优先级覆盖低优先级配置，互补
		 
		 修改默认配置文件的位置：
		 spring.config.location=D:/config
}

4.日志{
	日志门面: 日志框架的抽象层,一般选用slf4j
	日志实现: 日志框架的抽象层的具体实现,一般选用logback
	
	springboot选用slf4j+logback
	
	日志统一的思想:
		1.将系统中其他日志框架排除
		2.用中间包替换原有的日志框架
		3.导入slf4j的其他实现
	
	springboot日志模块依赖
		<dependency>
		  <groupId>org.springframework.boot</groupId>
		  <artifactId>spring-boot-starter-logging</artifactId>
		  <version>2.1.0.RELEASE</version>
		  <scope>compile</scope>
		</dependency>
	
	日志格式
		#通用格式
		%t -> 线程名;   
		%m->日志主体;   
		%n->平台换行符;
		%r -> 自应用启动到输出该log信息耗费的毫秒数
		%p -> 日志级别 -> %5p -> 5字符长度,左边补空格
		%d -> 时间及格式 -> %d{yyyy-MMM-dd HH:mm:ss,SSS} -> 2002-10-18 22:10:28,921
		
		//1.不输入: 表示输出完整的<包名+类名>
		//2.输入0: 表示只输出<类名>
		//3.输入其他数字: 表示输出小数点最后边点号之前的字符数量
		%c -> %c{length} -> length有三种情况(↑) -> 类全名
	日志配置文件
		logback.xml: 直接被日志框架加载
		logback-spring.xml: 跳过日志框架,直接被SpringBoot加载,可以使用高级 springProfile 功能
	
}





		