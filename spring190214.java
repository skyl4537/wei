1.理论知识{
	IOC：控制反转，是一种开发思想
		由IOC容器创建bean，并将管理bean的生命周期和依赖关系
	DI:IOC思想的具体实现
	<bean>:受spring管理的一个JavaBean对象
		id:bean的唯一标识，在整个spring容器中唯一，不可重复
		class:指定JavaBean的全类名，通过反射创建对象
		<property>给对象的属性赋值
			name:指定属性名，指set方法的属性名
			value：属性值
		
	获取person对象
		ApplicationContext act = new ClassPathXmlApplicationContext("applicationContextConfig.xml");
		Person person = (Person)act.getBean("person");
		person = act.getBean(Person.class);
		person = act.getBean("person", Person.class); 
}

2.DI依赖注入方式{
	1.<!-- DI依赖注入方式：setter方法注入 -->
	<bean id="car" class="com.di.Car">
		<property name="brand" value="法拉利"></property>
		<property name="crop" value="意大利"></property>
		<property name="price" value="5000000"></property>
	</bean>
	
	<property name="price" value="5000000"></property> 相当于调用了javabean的setter方法
	
	2.<!-- 
		DI依赖注入方式：构造器注入
		index:指定参数位置	
		type:指定参数的类型
	 -->
	<bean id="car1" class="com.di.Car">
		<constructor-arg value="保时捷" index="0"></constructor-arg>
		<constructor-arg value="德国" index="1"></constructor-arg>
		<constructor-arg value="6000000" index="2" type="java.lang.Double"></constructor-arg>
	</bean>
	
	3.p命名空间
	xmlns:p="http://www.springframework.org/schema/p"
	<!-- p命名空间，本质是使用setter方法进行依赖注入 -->
	<bean id="car3" class="com.di.Car" p:brand="福特" p:crop="长安" p:price="200000"/>
	
	4.xml特殊字符处理使用<![CDATA[字符串]]>
	<bean id="book" class="com.di.Book">
		<property name="bookid" value="1001"> </property>
		<property name="bookName">
			<value><![CDATA[<<<jajjaa*&%@W()>>>>]]></value>
		</property>
	</bean>
	
	5.配置文件引入内部类
	<bean id="person" class="com.di.Person">
		<property name="id" value="202"></property>
		<property name="name" value="董画"></property>
		<property name="car">
			<bean class="com.di.Car">
				<property name="brand" value="Mini"></property>
				<property name="crop" value="一汽"></property>
				<property name="price" value="105550"></property>
			</bean>
		</property>
	</bean>
	
	6.给级联属性赋值
	<bean id="person1" class="com.di.Person">
		<property name="id" value="202"></property>
		<property name="name" value="董画"></property>
		<property name="car" ref="car"></property>
		<!-- 级联属性赋值 -->
		<property name="car.speed" value="300"></property>
	</bean>
	
	7.list赋值
	<bean id="personList" class="com.di.PersonList">
		<property name="name" value="curry"></property>
		<property name="cars">
			<list>
				<ref bean="car"/>
				<ref bean="car1"/>
				<ref bean="car3"/>
			</list>
		</property>
	</bean>
	
	8.map赋值
	<bean id="personMap" class="com.di.PersonMap">
		<property name="name" value="KD"></property>
		<property name="cars">
			<map>
				<entry key="AA" value-ref="car"></entry>
				<entry key="BB" value-ref="car1"></entry>
				<entry key="CC" value-ref="car3"></entry>
			</map>
		</property>
	</bean>
	
	9.定义集合bean
	<util:list id="carList">
		<ref bean="car"/>
		<ref bean="car1"/>
		<ref bean="car2"/>
	</util:list>
	
	10.bean与bean的继承关系 
	parent属性 abstract修饰的bean不能被实现
	<bean id="address"  abstract="true">
		<property name="city" value="beijing"></property>
		<property name="street" value="xierqi"></property>
	</bean>
	
	<bean id="address1" class="com.relation.Address" parent="address">
		<property name="street" value="shangdi"></property>
	</bean>
}

3.bean{
	1.bean在spring的声明周期{
		1.创建bean对象
		2.给bean对象属性赋值
		3.调用初始化方法
		4.使用bean对象
		5.调用销毁方法
	}
	
	2.bean的作用域{
		singleton 单例  整个IOC只创建一个bean实例，共享该实例,ioc容器初始化时创建该实例
		prototype 原型  每次调用都会创建一个新的实例
		request   请求	每次http请求都会创建一个新的实例
		session   会话  每次会话都会创建一个新的实例
	}
	
	3.bean的后置处理器{
		对IOC容器中所有的bean起作用
		必须实现BeanPostProcessor接口
		/**
		 * 在bean生命周期的初始化方法调用之前调用
		 * Object bean: 正在被创建的bean
		 * String beanName ：正在被创建的bean的id
		 */
		public Object postProcessBeforeInitialization(Object arg0, String arg1) throws BeansException {}
		/**
		 * 在bean生命周期的初始化方法调用之后调用
		 * Object bean: 正在被创建的bean
		 * String beanName ：正在被创建的bean的id
		 */
		public Object postProcessAfterInitialization(Object arg0, String arg1) throws BeansException {}
	}
	
	4.bean配置引用外部属性文件{
		1.创建properties类型的属性文件，文件以key=value的形式给属性赋值
		2.配置文件中添加PropertyPlaceholderConfigurer的bean
			<context:property-placeholder />其内部引用了PropertyPlaceholderConfigurer的实现 两者用法相同
			都是将属性配置文件中的值，放入到系统配置内存中
			
			<!-- <context:property-placeholder location="classpath:jdbc.properties"/>  -->
		
			<bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
				<property name="location" value="classpath:jdbc.properties"></property>
			</bean>
			
			<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
				<property name="driverClass" value="${jdbc.driverClass}"></property>
				<property name="jdbcUrl" value="${jdbc.url}"></property>
				<property name="user" value="${jdbc.username}"></property>
				<property name="password" value="${jdbc.password}"></property>
			</bean>
	}
	
	5.bean的自动装配{
		
		使用autowire属性
			byName:使用bean的属性名与<bean> id 进行匹配
			byType：使用bean属性的类型与<bean> class进行匹配，如果匹配了多个，则抛异常
		
		<bean id="person" class="com.autowired.Person" autowire="byType" >
			<property name="name" value="lixin"></property>
		</bean>
		
		<bean id="car" class="com.autowired.Car">
			<property name="brand" value="奔驰"></property>
			<property name="price" value="500000"></property>
		</bean>
		
		<bean id="address" class="com.autowired.Address">
			<property name="province" value="henan"></property>
			<property name="city" value="puyang"></property>
		</bean>
		
	}
	
	6.注解方式配置bean{
		必须导入spring-aop-4.0.0.RELEASE.jar
		配置文件中必须要进行包扫描，将bean在IOC容器中进行管理
			<context:component-scan base-package="com.annotation"></context:component-scan>
		注解表明的类,与在配置文件中<bean>声明作用相同。注解表明的类的id，默认为：类名首字母小写，也可使用value属性进行修改
			@Controller(value="uc")
			
		只扫描指定注解使用<context:include-filter> use-default-filters="false"
			<context:component-scan base-package="com.annotation"　use-default-filters="false">
				<context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
			</context:component-scan>
		排除不需要的扫描的注解,使用<context:exclude-filter>
			<context:component-scan base-package="com.annotation">
				<context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
			</context:component-scan>
	}
	
}

4.@Autowired{
	完成bean属性的自动装配
	工作机制:	
				首先会使用byType的方式进行自动装配，如果能唯一匹配，则装配成功， 
	            如果匹配到多个兼容类型的bean, 还会尝试使用byName的方式进行唯一确定. 
	            如果能唯一确定，则装配成功，如果不能唯一确定，则装配失败，抛出异常. 
	   
	   默认情况下， 使用@Autowired标注的属性必须被装配，如果装配不了，也会抛出异常. 
	   可以使用required=false来设置不是必须要被装配. 
	   
	   如果匹配到多个兼容类型的bean，可以使用@Qualifier来进一步指定要装配的bean的id值 。
	   
	   @Autowired @Qualifier 注解即可在成员变量上，也可以加在对应的set方法上.. 
}

5.动态代理{
	动态代理类，是在运行过程中动态生成的，在内存中
	1.jdk动态代理{
		1.Proxy 所有代理类的父类，专门用于生成代理类或代理对象
			用于生成代理类的Class对象
			public static Class<?> getProxyClass(ClassLoader loader, Class<?>... interfaces)
			用于生成代理对象及 Class对象的实例
			public static Object newProxyInstance(ClassLoader loader,Class<?>[] interfaces,InvocationHandler h)
		2.InvocationHandler 完成动态代理的整个过程
			public Object invoke(Object proxy, Method method, Object[] args)throws Throwable;
	}
	
	2.动态代理思想{
		1.目标对象,要代理谁
		2.如何获取代理对象
		3.代理要做什么
	}
	
	3.动态实现原理{
		public class $Proxy0 extends Proxy implements ArithmeticCalculator{
			protected $Proxy0(InvocationHandler h) {
				super(h);
			}
			
			@Override
			public int add(int i, int j) {
				return super.h.invoke(proxy, method, args);
			}
			...
		}
	}
	
	4.实例{
		package com.aop;

		import java.lang.reflect.InvocationHandler;
		import java.lang.reflect.Method;
		import java.lang.reflect.Proxy;
		import java.util.Arrays;

		/**
		 *
		 *JDK代理
		 *	1.Proxy 所有代理类的父类，专门用于生成代理类或代理对象
		 *		用于生成代理类的Class对象
		 *		public static Class<?> getProxyClass(ClassLoader loader, Class<?>... interfaces)
		 *		用于生成代理对象及 Class对象的实例
		 *		public static Object newProxyInstance(ClassLoader loader,Class<?>[] interfaces,InvocationHandler h)
		 *
		 *	2.InvocationHandler 完成动态代理的整个过程
		 *		public Object invoke(Object proxy, Method method, Object[] args)throws Throwable;
		 */
		public class ArithmeticCalculatorProxy {
			
			//动态代理： 目标对象，如何获取代理对象  代理要做什么
			
			//目标对象
			private ArithmeticCalculator target;
			
			public ArithmeticCalculatorProxy(ArithmeticCalculator target) {
				this.target = target;
			}
			
			//获取代理对象
			public Object getProxy() {
				Object proxy = null;
				
				/**
				 * loader: ClassLoader对象，类加载器对象
				 * interfaces:提供目标对象的所有接口，让代理对象与目标对象有相同的方法
				 * h:InvocationHandler 接口
				 */
				ClassLoader classLoader = target.getClass().getClassLoader();
				Class<?>[] interfaces = target.getClass().getInterfaces();
				proxy = Proxy.newProxyInstance(classLoader, interfaces, new InvocationHandler() {
					
					/**
					 * invoke:代理对象调用代理方法时，会回调invoke方法
					 * proxy:代理对象
					 * method：正在被调用的方式
					 * args：正在被调用方法的参数
					 */
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						String methodName = method.getName();
						System.out.println("LoggerProxy==> The method "+methodName+" begin with "+Arrays.asList(args));
						Object result = method.invoke(target, args);
						System.out.println("LoggerProxy==> The method "+methodName+" end with "+result);
						return result;
					}
				});
				
				return proxy;
			} 

		}

	}
}

6.Aop{
	
	1.连接点:目标类(要代理的类)方法
	2.通知:在目标类方法的前或者后等
	3.切点 execution的表达式即为切点
		切点表达式	execution([权限修饰符] [返回值类型] [简单类名/全类名] [方法名]([参数列表]))
	4.在Spring中启用AspectJ注解支持
		1.配置文件添加
			<aop:aspectj-autoproxy>
		2.用AspectJ注解声明切面
			AopArith类为切面
			@Component
			@Aspect
			public class AopArith {
				//获取返回值时，returning的值，必须和参数的名称相同
				@AfterReturning(value = "execution( * com.aop.annocation.*.*(..))",returning="result")
				public void returnLog(JoinPoint joinPoint,Object result) {
					//获取方法名
					String methodName = joinPoint.getSignature().getName();
					//获取方法参数
					Object[] args = joinPoint.getArgs();
					System.out.println("loggerAop===>The method :"+methodName+" end with "+result);
					
				}
				
				//ProceedingJoinPoint是JoinPoint的子类
				@Around(value = "execution( * com.aop.annocation.*.*(..))")
				public Object aroundLog(ProceedingJoinPoint joinPoint) {
					
					Object proceed;
					String methodName = joinPoint.getSignature().getName();
					try {
						Object[] args = joinPoint.getArgs();
						System.out.println("loggerAop===>The method :"+methodName+" begin with "+Arrays.asList(args));
						proceed = joinPoint.proceed();
						System.out.println("loggerAop===>The method :"+methodName+" end with "+proceed);
						return proceed;
					} catch (Throwable e) {
						System.out.println(e);
					}finally {
						System.out.println("loggerAop===>The method :"+methodName+" end");
					}
					return null;
				}
							
			}
			
		3.使用@order(int)注解设置切面的优先级
			不指定order参数，默认为Integer.MAX_VALUE,值越小优先级越高
			
		4.重用切入点表达式
			定义切点
				@Pointcut("execution( * com.aop.annocation.*.*(..))")
				public void declarePointCut() {};
			重复使用切点
				@Before("declarePointCut()")
				public void beforeLog(JoinPoint joinPoint) {
					String methodName = joinPoint.getSignature().getName();
					Object[] args = joinPoint.getArgs();
					System.out.println("loggerAop===>The method :"+methodName+" begin with "+Arrays.asList(args));	
				}
	
}

7.事务{
	1.事务的四个关键属性{
		1.原子性
			事务中的所有操作，同时成功或同时失败
		2.一致性
			数据执行事务前正确，执行事务后仍然正确
		3.隔离性
			多个事务在并发过程中不会相互干扰
		4.持久性
			事务执行完成之后，数据的修改被永久的保存下来，不会因为系统等错误而受到影响
	}
	
	2.spring声明式事务{
		声明式事务
			通过配置的形式，基于AOP的方式，动态地把管理事务的代码作用在目标方法上
		
		Spring的核心事务管理抽象是PlatformTransactionManager，主要实现类有:
			1)DataSourceTransactionManager：在应用程序中只需要处理一个数据源，而且通过JDBC存取。
			2)JtaTransactionManager：在JavaEE应用服务器上用JTA(Java Transaction API)进行事务管理
			3)HibernateTransactionManager：用Hibernate框架存取数据库
		
		启动事务
			<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
				<property name="dataSource" ref="dataSource"></property>
			</bean>
			
			<!-- 
				开启事务注解
				transaction-manager:指定事务管理器，
				如果事务管理器的bean id="transactionManager" 
					则可省去 transaction-manager="transactionManager"的配置，
				如果不是，则必须显示的配置事务管理器的id值
			-->
			<tx:annotation-driven transaction-manager="transactionManager"/>
		@Transactional
			@Transactional注解既可以标注在方法上，也可以标注到类上，标注到类上，表示当前类中的所有方法都起作用
							标注到方法上表示只对当前方法起作用。
							优先级 方法标注 > 类标注
		
		事务管理属性设置
			事务的传播行为propagation:一个事务B方法被另外一个事务A方法调用时，B事务如何使用事务
				Propagation.REQUIRED: 使用A事务，默认值
				Propagation.REQUIRES_NEW：挂起A事务，创建新的事务B来使用
			事务的隔离级别isolation:
				Isolation.READ_COMMITTED:默认,读已提交
				Mysql的隔离级别 
				1	读未提交	脏读			事务B可以读到事务A未提交的数据，A事务有可能回滚
				2	读已提交	不可重复读		主要描述修改操作。x=10,事务A将其修改为3,但A未提交,事务B读出x=10,A提交后,B再次读x=3	
				4	可重复读	幻读			主要描述插入操作，x=10,事务A将x修改3,但A未提交,事务B读x时,并未读到,事务c插入y=20,B再读则读出y=10;
				8	串行化		效率低			A事务在执行时，任何事务都不可操作数据库，只能排队等待
			
			事务的回滚与不会滚rollbackFor,rollbackForClassName
				默认情况下，spring会对所有的运行时异常进行回滚
				遇到那种异常进行回滚
					rollbackFor					Class[] rollbackFor={AccountException.class,......}
					rollbackForClassName		String[] rollbackForClassName={"AccountException",......}
				遇到那种异常不回滚
					noRollbackFor
					noRollbackForClassName
			事务的只读设置readOnly
				true		只读，不可进行修改操作，也可进行修改操作，但spring不会对此次操作进行加锁，spring在对数据进行修改操作进行加锁，
								与事务的隔离级别相关，只读不加锁效率更高
				false		非只读，默认值，
			事务的超时设置timeout
				设置事务在强制回滚之前可以占用的时间
			
	}
		
}

8.@Scheduled{
	spring支持注解时声明定时任务。单线程执行定时任务
	this.localExecutor = Executors.newSingleThreadScheduledExecutor();
	Spring配置定时任务
		在spring配置文件中加入xmlns:task="http://www.springframework.org/schema/task"
		开启注解扫描<context:component-scan base-package="com.wei.task"></context:component-scan>
		启用定时任务注解<task:annotation-driven/>
	参数
		cron:cron表达式
			cron表达式语法:[秒] [分] [小时] [日] [月] [周] [年],[年]不是必须的域，可以省略[年]，则一共6个域
			序号	说明	必填	允许填写的值	允许的通配符
			1		秒		是		0-59			, - * /
			2		分		是		0-59			, - * /
			3		时		是		0-23			, - * /
			4		日		是		1-31			, - * /
			5		月		是		1-12 / JAN-DEC	, - * ? / L W
			6		周		是		1-7 or SUN-SAT	, - * ? / L #
			7		年		否		1970-2099		, - * /
			* 表示所有值
			? 表示不指定值。使用的场景为不需要关心当前设置这个字段的值 例:0 0 0 10 * ? 每月10号触发，不关心周
			- 表示区间 例 0 0 10-12 * * ？ 每天10，11,12点触发
			, 表示指定多个值 例 0 26,29,33 * * * ? 在26分、29分、33分执行一次
			/ 用于递增触发 例:*/5 * * * * ? 每隔5秒执行一次
			L 表示最后的意思 例: 0 0 23 L * ? 每月最后一天23点执行一次
			W 表示离指定日期的最近那个工作日 例: 0 0 0 15w * ? , 每月15号最近的那个工作日触发(周一至周五)
			# 序号(表示每月的第几个周几) 例：0 0 0 0 * 6#3 在每月的第三个周六 
		fixedDelay
			上一次执行完毕时间点之后多长时间再执行
		fixedDelayString
			@Scheduled(fixedDelayString = "5000")
			支持占位符,占位符的使用(配置文件中有配置：time.fixedDelay=5000)
			@Scheduled(fixedDelayString = "${time.fixedDelay}")
		fixedRate
			上一次开始执行时间点之后多长时间再执行
		fixedRateString
			与fixedRate意思相同，用法与fixedDelayString相同，
		initialDelay
			第一次延迟多长时间后再执行
		initialDelayString
			与 initialDelay 意思相同，支持占位符
	
}

9.@Async{
	基于@Async标注的方法，称之为异步方法，这些方法将在执行的时候，将会在独立的线程中被执行，调用者无需等待它的完成
	
}

