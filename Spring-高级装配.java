1.配置profile bean{
	- javaConfig中配置profile{
		使用@Profile注解
			- 应用在配置类级别上
				@Configuration
				@Profile("dev")
				实例:{
					@Configuration
					@Profile("dev")
					public class DevelopmentProfileConfig {

						@Bean(destroyMethod="shutdown")
						public DataSource dataSource(){
							return (DataSource) new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
									.addScript("classpath:schema.sql")
									.addScript("classpath:test-data.sql").build();
						}
						
					}
				}
			- 与@Bean一同使用
				@Bean(destroyMethod="shutdown")
				@Profile("dev")
				实例:{
					@Configuration
					public class DataSourceConfig {
						
						@Bean(destroyMethod="shutdown")
						@Profile("dev")
						public DataSource embeddeddataSource(){
							return (DataSource) new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
									.addScript("classpath:schema.sql")
									.addScript("classpath:test-data.sql").build();
						}
						
						@Bean
						@Profile("prod")
						public DataSource jndidataSource(){
							
							JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
							jndiObjectFactoryBean.setJndiName("jdbc/myDS");
							jndiObjectFactoryBean.setResourceRef(true);
							jndiObjectFactoryBean.setProxyInterface(javax.sql.DataSource.class);
							
							return (DataSource) jndiObjectFactoryBean.getObject();
						}

					}
				}
	}
	- XML中配置profile{
		- 使用<beans>元素的profile属性
			实例:{
				<beans xmlns="http://www.springframework.org/schema/beans"
					xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
					xmlns:jdbc="http://www.springframework.org/schema/jdbc"
					xsi:schemaLocation="
					http://www.springframework.org/schema/beans 
					http://www.springframework.org/schema/beans/spring-beans-4.0.xsd 
					http://www.springframework.org/schema/jdbc
					http://www.springframework.org/schema/jdbc/spring-jdbc.xsd
					" profile="dev">
						<!-- prod 环境与之类似  profile="prod"-->
						<jdbc:embedded-database id="DataSource">
							<jdbc:script location = "classpath:schema.sql"/>
							<jdbc:script location = "classpath:test-data.sql"/>
						</jdbc:embedded-database>

				</beans>
			}
		- 同一个配置文件部署多种环境
			实例：{
				<beans xmlns="http://www.springframework.org/schema/beans"
						xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:jdbc="http://www.springframework.org/schema/jdbc"
						xmlns:jee="http://www.springframework.org/schema/jee" xmlns:p="http://www.springframework.org/schema/p"

						xmlns:util="http://www.springframework.org/schema/util"
						xsi:schemaLocation="
							http://www.springframework.org/schema/beans 
							http://www.springframework.org/schema/beans/spring-beans-4.0.xsd 
							http://www.springframework.org/schema/jdbc
							http://www.springframework.org/schema/jdbc/spring-jdbc.xsd
							http://www.springframework.org/schema/jee
							http://www.springframework.org/schema/jee/spring-jee.xsd
							
							">
						<!-- prod 环境与之类似 profile="prod" -->

						<beans profile="dev">

							<jdbc:embedded-database id="dataSource">
								<jdbc:script location="classpath:schema.sql" />
								<jdbc:script location="classpath:test-data.sql" />
							</jdbc:embedded-database>
						</beans>
						<beans profile="qa">
							<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource"
								destroy-method="close" p:url="jdbc:h2:tcp://dbserver/~/test"
								p:driverClassName="org.h2.Driver" p:username="sa" p:password="password"
								p:initialSize="20" p:maxActive="30" />
						</beans>

						<beans profile="prod">
							<jee:jndi-lookup id="dataSource" jndi-name="jdbc/myDataSource" resource-ref="true" proxy-interface="javax.sql.DataSource"/>
						</beans>

				</beans>
			}
	}
}
2.激活 profile{???
	- spring确定profile状态激活依赖两个独立的属性
		spring.profiles.active
		spring.profiles.default
		优先级别 active>default>其他
	- 多种方式设置active、default属性
		- 作为DispathcherServlet初始化参数
		- 作为web应用上下文参数
		- 作为JNDI条目
		- 作为环境变量
		- 作为jvm的系统属性
		- 在集成测试类上，使用@activeProfiles注解的设置
}
3.处理自动装配的歧义性{
	在spring上下文初始化bean时可能初始化多个id相同的bean,在使用这些bean时会出现无法正确匹配唯一bean而报错
	- 标示首选项{
		@primary将bean标记为首选项
		- 可以与@Component组合使用在组件扫描的bean上
			实例:{
				@Component
				@Primary
				public class IceCream implements Dessert{}
			}
				
		- 也可以与@Bean组合用在java配置的bean声明上
			实例：{
				@Configuration
				//@ComponentScan
				public class DessertConfig {
					@Bean
					@Primary
					public Dessert getDessert(){
						return new IceCream();
					}
				}
			}
		- XML配置<bean>元素primary属性
			实例:{
				<bean id="iceCream" class="com.primaryTest.IceCream" primary="true"/>
			}
	}
	- 限定自动装配的bean{
		@Qualifier注解限定符，可以与@Autowired和@Inject协同使用
		@Qualifier注解所设置的参数就是想要注入的bean的ID
		如果有多个bean使用了相同的@Qualifier注解限定符，那么spring上下文仍然确定唯一的bean而报错
		如果bean上使用了@Qualifier注解那么，在@Autowired注入bean时，使用bean的添加的限定符的名称和bean生成的ID都可以确定注入唯一的bean
		实例：{
			@RunWith(SpringJUnit4ClassRunner.class)
			@ContextConfiguration(classes = DessertConfig.class)
			public class DessertTest {

				@Autowired
				@Qualifier("cake")
			//	@Qualifier("cookies")
			//	@Cold
				private Dessert ds;
				
				@Test
				public void desTest(){
					ds.eat();
				}
			}
		}
	}
	- 创建自定义的限定符{
		可以不依赖与bean的ID作为限定符，使用自定的限定符来标注bean的唯一性
		可以创建多个自定义限定符来标注bean
		可以任意使用多个自定义注解的组合,缩小范围到只有一个bean
		创建自定义的限定符实例：{
			@Target({ElementType.CONSTRUCTOR,ElementType.FIELD,ElementType.METHOD,ElementType.TYPE})
			@Retention(RetentionPolicy.RUNTIME)
			@Qualifier
			public @interface Cold {

			}
			创建bean时使用自定义的限定符标注
			@Component
			@Cold
			public class IceCream implements Dessert{

				@Override
				public void eat() {
					System.out.println("eat IceCream");
					
				}

				public IceCream() {
					System.out.println("IceCream 被创建");
				}
			}
			
			注入限定的bean
			@RunWith(SpringJUnit4ClassRunner.class)
			@ContextConfiguration(classes = DessertConfig.class)
			public class DessertTest {
				@Autowired
				@Cold
				private Dessert ds;
				
				@Test
				public void desTest(){
					ds.eat();
				}
				
			}

		}
	}
}
4.bean的作用域{
	spring定义了多种作用域，可以基于这些作用域创建bean
	a)单例(singleton):在整个应用中，只创建bean的一个实例
	b)原型(prototype):每次注入或者通过spring上下文获取的时候，都会创建一个新的bean实例？？？原来的是否存在
		//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) 原型
	c)会话(session):在web应用中，为每个会话创建一个bean实例
		//@Scope(value=WebApplicationContext.SCOPE_SESSION,proxyMode=ScopedProxyMode.TARGET_CLASS)
		//@Scope(value=WebApplicationContext.SCOPE_SESSION,proxyMode=ScopedProxyMode.INTERFACES)
	d)请求(request)：在web应用中，为每个请求创建一个bean实例
		//@Scope(value=WebApplicationContext.SCOPE_REQUEST,proxyMode=ScopedProxyMode.INTERFACES)
		//@Scope(value=WebApplicationContext.SCOPE_REQUEST,proxyMode=ScopedProxyMode.TARGET_CLASS)
	@Scope{
		在bean上使用@Scope注解，声明bean的作用域,默认作用域为:单例
		实例：
			@Component
			//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) 原型
			//@Scope(value=WebApplicationContext.SCOPE_SESSION,proxyMode=ScopedProxyMode.TARGET_CLASS)
			@Scope(value=WebApplicationContext.SCOPE_REQUEST,proxyMode=ScopedProxyMode.INTERFACES)
			public class ShoppingCart {}
		使用xml配置
			<bean id="notepad" class="com.scopeTest.Notepad" scope="prototype"/>
		使用会话好和请求作用域  ?????????????????????????
			@Scope(value=WebApplicationContext.SCOPE_REQUEST,proxyMode=ScopedProxyMode.INTERFACES)
			@Scope(value=WebApplicationContext.SCOPE_SESSION,proxyMode=ScopedProxyMode.TARGET_CLASS)
			proxyMode属性，为bean创建一个代理，spring在加载时并不会将真正的bean注入，而是会注入一个bean的代理，这个代理与bean具有相同的方法
				当要调用bean的方法时，代理会对其进行懒解析并将调用委托给会话作用域内真正的bean。？？？？
				proxyMode=ScopedProxyMode.INTERFACES 表明代理要实现的接口类型的bean，并将调用委托给实现bean
				proxyMode=ScopedProxyMode.TARGET_CLASS 必须使用CGLIB来生成基于类的代理，表明以基于目标类扩展的方式创建代理
			作用域代理能够延迟注入请求和会话作用域的bean
			
		使用xml声明作用域代理
			声明aop命名空间
			xmlns:aop="http://www.springframework.org/schema/aop"
				http://www.springframework.org/schema/aop
				http://www.springframework.org/schema/aop/spring-aop.xsd
			<bean id="cart" class="com.scopeTest.ShoppingCart" scope="session">
				<!-- <aop:scope-proxy/> -->
				<aop:scope-proxy proxy-target-class="false"/>
			</bean>
			<aop:scope-proxy/>等同于@Scope注解的proxyMode属性，默认使用CGLib创建目标类的代理
			<aop:scope-proxy proxy-target-class="false"/> proxy-target-class="false 表明使用基于接口的代理
	}
	


	
}
5.运行时值注入{
	- 注入外部的值
		使用@PropertySource和Environment 
		@PropertySource声明属性源
		使用spring Environment来检索属性
			@Configuration
			@PropertySource("classpath:/com/soundsystem/app.properties") //声明属性源
			public class ExpressiveConfig {
				
				@Autowired
				Environment  env; 
				
				@Bean
				public BlankDisc disc(){
					return  new BlankDisc(
							env.getProperty("disc.title"), //检索属性值
							env.getProperty("disc.artist")
							);
				}
			}
		Environment的有四个getProperty()重载方法
			String getProperty(String key)
			String getProperty(String key,String defaultValue)
			T getProperty(String key,Class<T> type)
			T getProperty(String key,Class<T> type, T defaultValue)
		
	- 解析占位符
		spring一直支持属性定义到外部的属性的文件中，并使用占位符将值插入到bean，在spring装配中，占位符的形式为"${...}"
		XML 中配置,MyXml.xml
				<bean id="compactDisc" class="com.soundsystem.SgtPeppers" 
					 c:title="${disc.title}"
						c:artist="${disc.artist}"
				/>
			
			配置类
				@Configuration
				@PropertySource("classpath:/com/soundsystem/app.properties") //声明属性源
				@ImportResource("classpath:MyXml.xml")//将MyXml.xml中配置的bean注入到spring上下文
				public class ExpressiveConfig {}
				
		使用组件扫描和自动装配解析占位符，@value注解和PropertySourcesPlaceholderConfigurer配合使用
			使用PropertySourcesPlaceholderConfigurer因为它能够基于spring Environment及其属性源来解析占位符
			实例:
				@Component
				//@Primary
				public class BlankDisc implements CompactDisc {
					@Value("${disc.title}")//使用@value注入外部值
					private String title;
					
					@Value("${disc.artist}")
					private String artist;
					
					private List<String> tracks;


					@Override
					public void play() {
						System.out.println("Playing "+title+" by "+artist);
				//		for(String track:tracks){
				//			System.out.println("-track: "+track);
				//		}
					}

				}
				
				//bean注解方式注入PropertySourcesPlaceholderConfigurer
				配置类中
				@Configuration
				@PropertySource("classpath:/com/soundsystem/app.properties") //声明属性源
				//@ImportResource("classpath:MyXml.xml")
				@ComponentScan
				public class ExpressiveConfig {
					@Bean
					public static PropertySourcesPlaceholderConfigurer placeholderConfigurer(){
						return new PropertySourcesPlaceholderConfigurer();
					}
				}
				
				//xml方式注入PropertySourcesPlaceholderConfigurer
				声明context命名空间
				xmlns:context="http://www.springframework.org/schema/context"
				http://www.springframework.org/schema/context	
				http://www.springframework.org/schema/context/spring-context-4.0.xsd
				//加载PropertySourcesPlaceholderConfigurer
				<context:property-placeholder />
				
				配置类中
				@Configuration
				@PropertySource("classpath:/com/soundsystem/app.properties") //声明属性源
				@ImportResource("classpath:MyXml.xml")
				@ComponentScan
				public class ExpressiveConfig {}
				
	- 使用spring表达式语言进行装配 spring expression language spEL
		spring表达式要放到#{...}之中，属性占位符要放在${...}之中
			#{systemProperties['disc.title']}通过systemProperties对象引用系统属性
		在XML配置中，spELl表达式传入<prototype>或<constructor-arg>的value属性中，或将其作为p,c命名空间条目的值
			实例：
			<bean id="compactDisc" class="com.soundsystem.SgtPeppers" 
				 c:title="#{systemProperties['disc.title']}"
				 c:artist="#{systemProperties['disc.artist']}"
			/>
		
		spEL表达式可以用来表示浮点数、String、boolean、Integer
			#{3.141592653}，#{9.87E4}科学计数法 #{'hello'}，#{true}
		spEL表达式引用bean、属性和方法
			spEL表达式可以通过ID引用其他的bean
			#{compactDisc}spEL表达式引入bean id=compactDisc的bean
			#{compactDisc.artist}spEL表达式引入bean的属性
			#{compactDisc.play()}spEL表达式引入bean的方法
			对表达式返回是String类型的值可以调用String的方法进行运算
			#{compactDisc.play().toUpperCase()}compactDisc.play()返回String，调用String的toUpperCase()方法
			为了避免compactDisc.play()返回NullPointException，使用类型安全运算符 - "?.",在访问符号右边之前确保符号左边表达式不为null，否则不调用右边方法直接返回null
			#{compactDisc.play()?.toUpperCase()}
		在表达式中使用类型
			如果要在SpEL中访问类作用域的方法和常量的话，需要依赖"T()"运算符,"T()" 的结果是一个class对象，它能够访问目标类的静态方法和常量
			T(java.lang.Math) 返回java.lang.Math类
			T(java.lang.Math).PI 获取java.lang.Math类中静态常量PI值
			T(java.lang.Math).random()调用java.lang.Math类的静态方法random() 结果会是一个0~1的随机数
		SpELl运算符
			算数运算:+、-、*、/、%、^(乘方计算运算符)
			比较运算:(符号)<、>、==、<=、>=  (文本)lt、 gt、 eq、 le、 ge 
			逻辑运算:and、or、not、|
			条件运算:?:(ternary三目运算)、?:(Elvis)
			正则表达式:matches
			
			实例：
				#{2*T(java.lang.Math).PI*circle.radius} 圆的周长
				#{T(java.lang.Math).PI*circle.radius^2} 圆的面积
			当使用String类型值时，"+"表示连接符
				#{disc.title+'by'+disc.artist}
			比较运算符中符号运算符与文本运算符作用相同
				#{counter.total==100} 等价于 #{counter.total eq 100} 结果都是boolean值
			?:(ternary三目运算)，常用检查null，并用默认值来代替null
				#{counter.total>1000?"Winner!":"Loser"} counter.total>1000为true 返回Winner! 否则返回Loser
				#{disc.title?:'rattle'} 表达式判断disc.title是不是null，为null则返回rattle
			正则表达式
				#{admin.email matches '[a-zA-Z0-9.%+-]+@[a-zA-Z0-9.-]+\\.com'}
			计算集合
				引用集合中的某个元素
					#{jukebox.songs[4].title}计算id为jukebox的songs集合中的第五个元素的title属性
					#{jukebox.songs[T(java.lang.Math).random()*jukebox.songs.size()].title}随机选择一首歌的名字
					#{'this is a test'[3]}引用String字符串中的第四个字符's'
				查询运算符(.?[])对集合进行过滤
					#{jukebox.songs.?[artist eq 'Aerosmith']} 过滤得到songs集合中artist等于Aerosmith的歌曲
				查询运算符".^[]",".$[]"分别用来在集合中查询第一个和最后一个匹配项
					#{jukebox.songs.^[artist eq 'Aerosmith']}得到songs第一个artist属性等于Aerosmith的歌曲
				投影运算符".![]"把集合中每个元素的某个属性放到另外一个集合中
					#{jukebox.songs.![title]}得到songs中所有集合元素的title值
			
}





























