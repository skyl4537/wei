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
})

5.springboot对静态资源的映射{？？？
	classpath:/
}

6.thymeleaf用法{
	pom文件依赖{
		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
	}
	
	把html文件放在classpath:/templates/文件夹下，thymeleaf可以自动渲染
	
	导入thymeleaf的名称空间
		<html xmlns:th="http://www.thymeleaf.org">
		
	thymeleaf语法规则
		th:text				改变当前元素的文本内容
		th:任意html属性 	替换html原生属性值
			<div th:id="${hello}" th:class="${hello}" th:text="${hello}"></div>
		
		表达式语法
			${}		获取值
			*{}		选择表达式与${}功能相同，补充配合th:object=${session.user} 使用 *{firstName} * 表示 ${session.user}
			#{}		获取国际化内容
			@{}		定义url链接 th:href=@{/pa(k1=v1,k2=v2)}  th:href=@{/pa(k1=v1,k2=v2)} /代表当前项目
			~{}		片段引入表达式
			
		th:each 每次遍历都会生成当前标签
		<h3 th:each="user:${users}" th:text="${user}"></h3>
		行内写法
			[[ ]] = th:text ; [( )] = th:utext 

}

7.扩展springmvc功能{
	
	即保留了springboot自动的配置，也可以用我们自定义的配置
	@Configuration
	public class MyMvcConfig extends WebMvcConfigurerAdapter{...}
	
	springmvc的所有自动配置信息都在 WebMvcAutoConfiguration配置类中
	使用@EnableWebMvc该注解则表示，完全使用自己配置的springmvc配置，springboot中自动配置的mvc将不起作用，不建议使用

}

8.错误页面定制{
	ErrorMvcAutoConfiguration 错误处理的自动配置类
	这个配置文件给容器添加了下面的组件
	1.DefaultErrorAttributes
		页面徐解析的错误数据模型进行封装
	2.BasicErrorController
		处理/error 请求
		@Controller
		@RequestMapping("${server.error.path:${error.path:/error}}")
		public class BasicErrorController extends AbstractErrorController {
			@RequestMapping(produces = MediaType.TEXT_HTML_VALUE) //html 
			public ModelAndView errorHtml(HttpServletRequest request,
					HttpServletResponse response) {
				HttpStatus status = getStatus(request);
				Map<String, Object> model = Collections.unmodifiableMap(getErrorAttributes(
						request, isIncludeStackTrace(request, MediaType.TEXT_HTML)));
				response.setStatus(status.value());
				ModelAndView modelAndView = resolveErrorView(request, response, status, model);
				return (modelAndView != null) ? modelAndView : new ModelAndView("error", model);
			}

			@RequestMapping//json
			public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
				Map<String, Object> body = getErrorAttributes(request,
						isIncludeStackTrace(request, MediaType.ALL));
				HttpStatus status = getStatus(request);
				return new ResponseEntity<>(body, status);
			}
		}
		
		getErrorAttributes(request, isIncludeStackTrace(request, MediaType.TEXT_HTML))
		这个方法最终调用DefaultErrorAttributes进行数据解析，封装模型数据
	3.ErrorPageCustomizer
		注册错误页面
		@Override
		public void registerErrorPages(ErrorPageRegistry errorPageRegistry) {
			ErrorPage errorPage = new ErrorPage(this.dispatcherServletPath
					.getRelativePath(this.properties.getError().getPath()));
			errorPageRegistry.addErrorPages(errorPage);
		}
		
		this.properties.getError().getPath()的默认为：
		@Value("${error.path:/error}")
		private String path = "/error";
		则会触发 /error请求
	4.DefaultErrorViewResolver
		将BasicErrorController中 /error返回的ModelAndView进行解析，
		1.可以匹配的模板引擎(即目录中有templates/error/错误状态码.html)，则使用模板页面
		2.查找静态资源下是否有可匹配的页面，则使用静态页面
		3.默认的Spel表达式的html页面
		从上到下 优先级由高到低
	发生系统错误时，会触发ErrorPageCustomizer
	
	执行步骤:3-> 2-> 1-> 4
	
	
	在错误信息中添加自定义信息
		1.继承DefaultErrorAttributes,覆写getErrorAttributes方法
		@Component
		public class MyErrorAttributes extends DefaultErrorAttributes {
			@Override
			public Map<String, Object> getErrorAttributes(WebRequest webRequest,
														  boolean includeStackTrace) {
				Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest,
						includeStackTrace);
				errorAttributes.put("company","wei");
				Map<String,Object> ext = ( Map<String,Object>)webRequest.getAttribute("ext", 0);
				errorAttributes.put("ext",ext);
				return errorAttributes;
			}
		}
		2.将错误信息放置request.setAttribute()中
		@ControllerAdvice
		public class MyException {

			@ExceptionHandler(UserNotExistException.class)
			public String  handlerException(Exception e, HttpServletRequest request){
				Map<String,Object>map = new HashMap<>();
				request.setAttribute("javax.servlet.error.status_code",500);
				map.put("cdoe","userNotExist");
				map.put("message",e.getMessage());

				request.setAttribute("ext",map);

				return "foward:/error";
			}

		}	
	
}

9.docker{
	
	安装docker
		docker yum install
	
	启动docker
		systemctl start docker 
		
	关闭docker
		systemctl stop docker 
	
	查看docker版本
		docker -v
	
	搜索镜像
		docker search name
	
	下载镜像
		docker pull ubuntu:13.10(版本信息),不写则获取最新的镜像
	
	查看本地有哪些镜像
		docker images
	
	删除镜像
		docker rmi image_id
	
	启动镜像容器
		docker run --name mytomcat01 -d tomcat:TAG -p 80:8080
		
		--name	自定义软件的名称
		-d		后台运行
		-p		端口映射	(主机端口)80:8080(容器端口) 通过主机端口映射可访问容器中的端口
		run		每执行一次run则会创建一个容器，一个镜像可启动多个容器，每个容器独立且互不干扰
	
	停止/开启 容器
		docker stop/start containerName/containerID
	
	查看有哪些容器在运行
		docker ps
		
	查看所有的容器
		docker ps -a
		
	删除容器
		docker rm containerName/containerID
		删除的容器必须是在关闭的状态才可删除
		
	查看容器日志
		docker logs containerName/containerID
	
	查看防火墙状态
		service firewalld status 
	
	关闭防火墙
		service firewalld stop 
	
	mysql启动容器
		docker run --name mysql01 -d mysql -p 33306:3306 -e MYSQL_ROOT_PASSWORD=123456
		mysql在下载镜像启动容器时不指定密码在启动容器时则会异常退出
	
}

10.数据访问{
	pom.xml文件中引入
		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
		<dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
		
	通过配置文件修改配置参数信息
		spring.datasource.username=bluecardsoft
		spring.datasource.password=#$%_BC13439677375
		spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
		spring.datasource.url=jdbc:mysql://192.168.8.8:33306/test0601?serverTimezone\=GMT%2B8
		
		
		
		The server time zone value 'ÖÐ¹ú±ê×¼Ê±¼ä' is unrec
		serverTimezone\=GMT%2B8,时区,不配置时区在获取连接时报错
		
	springboot默认使用的com.zaxxer.hikari.HikariDataSource 数据源
	
	数据源相关配置都在 DataSourceProperties 里，
	
	自动配置相关的信息在org.springframework.boot.autoconfigure.jdbc包中
		DataSourceConfiguration类是根据配置信息来生成数据源
		使用spring.datasource.type属性来指定使用哪种数据源类型，默认可以支持四种:
			1.org.apache.tomcat.jdbc.pool.DataSource
			2.com.zaxxer.hikari.HikariDataSource
			3.org.apache.commons.dbcp2.BasicDataSource
			4.自定义数据源
				/**
				 * Generic DataSource configuration.
				 */
				@ConditionalOnMissingBean(DataSource.class)
				@ConditionalOnProperty(name = "spring.datasource.type")
				static class Generic {

					@Bean
					public DataSource dataSource(DataSourceProperties properties) {
						//使用DataSourceBuilder创建数据源,利用反射机制来创建相应type的数据源，并绑定相关属性
						return properties.initializeDataSourceBuilder().build();
					}
				}
				
				例如: c3p0数据源
				
	Druid
		使用Druid自定义数据源类型
		application.properties中指定数据源类型
			spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
		pom文件添加依赖
			<dependency>
				<groupId>com.alibaba</groupId>
				<artifactId>druid</artifactId>
				<version>1.1.14</version>
			</dependency>
			
		
		配置类
		@Configuration
		public class DruidConfig {
			//使配置文件中的配置生效
			@ConfigurationProperties(prefix = "spring.datasource")
			@Bean
			public DataSource druid(){
				return new DruidDataSource();
			}

			//添加druid的servlet
			@Bean
			public ServletRegistrationBean servletRegistrationBean(){
				ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(),"/druid/*");

				Map<String,Object> initParam =  new HashMap<>();
				initParam.put("loginUsername","admin");
				initParam.put("loginPassword","1");

				bean.setInitParameters(initParam);

				return bean;
			}

			//添加druid的Filter
			@Bean
			public FilterRegistrationBean webStatFilter(){
				FilterRegistrationBean bean = new FilterRegistrationBean();

				bean.setFilter(new WebStatFilter());
				Map<String,Object> initParam =  new HashMap<>();
				initParam.put("exclusions","*.js,*.css,/druid/*");


				bean.setInitParameters(initParam);
				bean.setUrlPatterns(Arrays.asList("/*"));

				return bean;


			}
		}
		
		
	mybatis整合{
		1.引入
		<dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>1.3.1</version>
        </dependency>
		
		使用mybatis注解版{
			注解开启驼峰命名，添加配置
			@Configuration
			public class MyMyBatisConfig {

				@Bean
				public ConfigurationCustomizer configurationCustomizer(){
					return new ConfigurationCustomizer(){
						@Override
						public void customize(org.apache.ibatis.session.Configuration configuration) {
							configuration.setMapUnderscoreToCamelCase(true);
						}
					};
				}

			}
			
			使用MapperScan批量扫描所有的Mapper接口
			@MapperScan(value="com.wei.**.mapper")
			
		}
		
		mybatis xml版{
			1.创建接口类FlowerMapper
			2.创建xml文件与接口向对应FlowerMapper.xml
			3.使用MapperScan批量扫描所有的Mapper接口
				@MapperScan(value="com.wei.**.mapper") 
			4.配置文件中添加mybatis配置信息
				mybatis.config-location=classpath:mybatis-config.xml
				mybatis.mapper-locations=classpath:/mapper/*.xml
		}
	}
}



			
			
			
			
			
			
			