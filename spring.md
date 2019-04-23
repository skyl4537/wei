# spring

Spring是一个开源框架，Spring的核心是控制反转（IoC）和面向切面（AOP）

# Spring的优点

1.**方便解耦，简化开发**

Spring就是一个大工厂（容器），可以将所有对象创建和依赖关系维护，交给Spring管理 
spring工厂是用于生成bean

2.**AOP编程的支持** 

Spring提供面向切面编程，可以方便的实现对程序进行权限拦截、运行监控等功能

3.**声明式事务的支持** 

只需要通过配置就可以完成对事务的管理，而无需手动编程

4.**方便程序的测试** 

Spring对Junit4支持，可以通过注解方便的测试Spring程序

5.**方便集成各种优秀框架** 

Spring不排斥各种优秀的开源框架，其内部提供了对各种优秀框架（如：Struts、Hibernate、MyBatis、Quartz等）的直接支持

6.**降低JavaEE API的使用难度** 

Spring 对JavaEE开发中非常难用的一些API（JDBC、JavaMail、远程调用等），都提供了封装，使这些API应用难度大大降低。

# 概念

Spring实现了工厂模式的工厂类，这个类名为 BeanFactory (实际上是一个接口),在程序中通常使用 BeanFactory 的子类 ApplicationContext.Spring 相当于一个大的工厂类,通过读取配置文件中的bean标签,实例化对应的类对象.

## IOC

控制反转，是一种开发思想。由IOC容器创建bean，并将管理bean的生命周期和依赖关系。

传统的资源查找方式是组件主动向容器发起查找资源请求, 作为回应, 容器适时的返回资源. 而应用了 IOC 之后, 则是容器主动地将资源推送给它所管理的组件,组件所要做的仅是选择一种合适的方式来接收资源. 这种行为也被称为查找的被动形式.

## DI

IOC思想的具体实现.

```xml
<bean>:受spring管理的一个JavaBean对象
	id:bean的唯一标识，在整个spring容器中唯一，不可重复
	class:指定JavaBean的全类名，通过反射创建对象
    <property>给对象的属性赋值
        name:指定属性名，指set方法的属性名
        value：属性值
```

## 获取IOC容器

### 非Web应用

```java
ApplicationContext act = new ClassPathXmlApplicationContext("applicationContextConfig.xml");
```

### Web应用

在 Web 应用被tomcat加载时创建IoC容器,然后放到 ServletContext 属性中,供其他模块使用。

tomcat启动时,默认加载'web.xml'文件,在此文件配置"applicationContext.xml"位置信息.

```xml
<context-param> 
    <param-name>contextConfigLocation</param-name>
	<param-value>classpath:applicationContext.xml</param-value> //Spring配置文件的名称和位置
</context-param>

<listener> //启动 IOC 容器的 ServletContextListener
	<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```

tomcat读取'web.xml'

```java
public void contextInitialized(ServletContextEvent sce) {
	ServletContext sc = sce.getServletContext();
	String config = sc.getInitParameter("contextConfigLocation");
			
	ApplicationContext context = new ClassPathXmlApplicationContext(config); //创建IOC容器
	sc.setAttribute("ApplicationContext", context); //把IOC容器放在 ServletContext 的一个属性中

	//Spring和web整合后,SpringMVC所有配置信息保存在 WebApplicationContext. (ApplicationContext-子类)
	ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(sc);
}
```

# DI依赖注入方式

## setter方法注入

```xml
<bean id="car" class="com.di.Car">
	<property name="brand" value="法拉利"></property>
	<property name="crop" value="意大利"></property>
	<property name="price" value="5000000"></property>
</bean>

<property name="price" value="5000000"></property> 相当于调用了javabean的setter方法
```

## 构造器注入

```xml
<!-- 
	DI依赖注入方式：构造器注入
	index:指定参数位置	
	type:指定参数的类型
 -->
<bean id="car1" class="com.di.Car">
	<constructor-arg value="保时捷" index="0"></constructor-arg>
	<constructor-arg value="德国" index="1"></constructor-arg>
	<constructor-arg value="6000000" index="2" type="java.lang.Double"></constructor-arg>
</bean>
```

## p命名空间

与property标签作用

```xml
<!-- 引入命名空间 -->
xmlns:p="http://www.springframework.org/schema/p"
<!-- p命名空间，本质是使用setter方法进行依赖注入 -->
<bean id="car3" class="com.di.Car" p:brand="福特" p:crop="长安" p:price="200000"/>
```

## xml特殊字符处理使用

```xml
<![CDATA[字符串]]> <!-- 对于特殊字符可直接使用不进行转义 -->
<bean id="book" class="com.di.Book">
	<property name="bookid" value="1001"> </property>
	<property name="bookName">
		<value><![CDATA[<<<jajjaa*&%@W()>>>>]]></value>
	</property>
</bean>
```

## 配置文件引入内部类

```xml
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
```

## 给级联属性赋值

```xml
<bean id="person1" class="com.di.Person">
	<property name="id" value="202"></property>
	<property name="name" value="董画"></property>
	<property name="car" ref="car"></property>
	<!-- 级联属性赋值 -->
	<property name="car.speed" value="300"></property>
</bean>
```

## list赋值

```xml
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
```

## map赋值

```xml
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
```

## 定义集合bean

```xml
<util:list id="carList">
	<ref bean="car"/>
	<ref bean="car1"/>
	<ref bean="car2"/>
</util:list>
```

## bean与bean的继承关系

```xml
parent属性 abstract修饰的bean不能被实现
<bean id="address"  abstract="true">
	<property name="city" value="beijing"></property>
	<property name="street" value="xierqi"></property>
</bean>

<bean id="address1" class="com.relation.Address" parent="address">
	<property name="street" value="shangdi"></property>
</bean>
```

# IOC-Bean

## IoC容器中bean的生命周期

(0).通过构造器或工厂方法创建 Bean 实例		//constuctor...

(1).为 Bean 的属性赋值和对其他 Bean 的引用	//setter...

(2).将 Bean 实例传递给 Bean 后置处理器的 postProcessBeforeInitialization 方法

(3).调用 Bean 的初始化方法 //init...

(4).将 Bean 实例传递给 Bean 后置处理器的 postProcessAfterInitialization 方法

(5).Bean 此时可以使用了 //Car [brand=Audi, price=720000.0]

(6).当容器关闭时, 调用 Bean 的销毁方法 //destroy...

## bean的后置处理器

对IOC容器中所有的bean起作用

必须实现BeanPostProcessor接口

```java
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
```

## bean的作用域

singleton 单例  整个IOC只创建一个bean实例，共享该实例,ioc容器初始化时创建该实例
prototype 原型  每次获取都会创建一个新的实例
request   请求	每次http请求都会创建一个新的实例
session   会话  每次会话都会创建一个新的实例

## bean配置引用外部属性文件

1.创建properties类型的属性文件，文件以key=value的形式给属性赋值

2.配置文件中添加PropertyPlaceholderConfigurer的bean

```xml
<context:property-placeholder />其内部引用了PropertyPlaceholderConfigurer的实现 两者用法相同,都是将属性配置文件中的值，放入到系统配置内存中.
<!-- 第一种配置 -->
<context:property-placeholder location="classpath:jdbc.properties"/>
<!-- 第二种配置 -->
<bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
	<property name="location" value="classpath:jdbc.properties"></property>
</bean>

<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
	<property name="driverClass" value="${jdbc.driverClass}"></property>
	<property name="jdbcUrl" value="${jdbc.url}"></property>
	<property name="user" value="${jdbc.username}"></property>
	<property name="password" value="${jdbc.password}"></property>
</bean>
```

## SpEL表达式

Spring表达式语言; 支持运行时查询和操作对象图的强大的表达式语言

(0).通过 bean 的 id 对 bean 进行引用

(1).调用方法以及引用对象中的属性

(2).计算表达式的值

(3).正则表达式的匹配

```xml
<bean class="com.x.pojo.Car" p:brand="Audi" p:price="720000" />
		<bean class="com.x.pojo.People" p:name="wang" 
			p:car="#{car}" //引用对象, 等价于 p:car-ref="car"
			p:pet="#{car.brand}" //引用对象的属性
			p:info="#{car.price > 300000 ? '金领':'白领'}"/> //三元运算符,单引号
```



## bean的自动装配

使用autowire属性

​	byName : 使用bean的属性名与bean id 进行匹配

​	byType : 使用bean属性的类型与bean class进行匹配，如果匹配了多个，则抛异常

```xml
class Person 
	name;
	Car;
	Address;

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
```

## 注解方式配置bean

1.必须导入spring-aop-4.0.0.RELEASE.jar

2.配置文件中必须要进行包扫描，将bean在IOC容器中进行管理

```xml
<context:component-scan base-package="com.annotation"></context:component-scan>
```

3.注解表明的类,与在配置文件中bean声明作用相同。注解表明的类的id，默认为：类名首字母小写，也可使用value属性进行修改

```java
@Controller(value="uc")
```

4.只扫描指定注解

```xml
<context:include-filter> use-default-filters="false"
<context:component-scan base-package="com.annotation"　use-default-filters="false">
	<context:include-filter type="annotation" expression="org.springframework.stereotype.Controller"/> 	
</context:component-scan>
```

5.排除不需要的扫描的注解

```xml
使用<context:exclude-filter></context:exclude-filter>
<context:component-scan base-package="com.annotation">
    <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller"/>
</context:component-scan>
```

6.spring 常用注解

@Component, @Respository, @Service, @Controller: 被Spring框架所扫描,并注入到IoC容器来进行管理

@Component	//通用注解(基础); 指明是一个bean,使用spring进行管理,通常配合@Bean使用
@Repository	//持久层注解; 具有将数据库操作抛出的原生异常,翻译转化为Spring持久层异常的功能.
@Controller	//控制层注解; 具有将请求进行转发,重定向的功能.
@Service	//业务逻辑层注解; 只是标注该类处于业务逻辑层.

## @Autowired

完成bean属性的自动装配

工作机制:

首先会使用byType的方式进行自动装配，如果能唯一匹配，则装配成功， 

如果匹配到多个兼容类型的bean, 还会尝试使用byName的方式进行唯一确定. 这时需使用@Qualifier 注解配合使用

如果能唯一确定，则装配成功，如果不能唯一确定，则装配失败，抛出异常. 

默认情况下， 使用@Autowired标注的属性必须被装配，如果装配不了，也会抛出异常.可以使用required=false来设置不是必须要被装配.

如果匹配到多个兼容类型的bean，可以使用@Qualifier来进一步指定要装配的bean的id值 。

@Autowired @Qualifier 注解即可在成员变量上，也可以加在对应的set方法上. 

## @Autowired, @Resource, @Inject区别

**@Autowired**

1、@Autowired是spring自带的注解，通过‘AutowiredAnnotationBeanPostProcessor’ 类实现的依赖注入；

2、@Autowired是根据**类型**进行自动装配的，如果需要按名称进行装配，则需要配合@Qualifier；

3、@Autowired有个属性为required，可以配置为false，如果配置为false之后，当没有找到相应bean的时候，系统不会抛错；

4、@Autowired可以作用在变量、setter方法、构造函数上。

 @Autowired 和 @Qualifier 结合使用时，自动注入的策略就从 byType 转变成 byName

需要注意的是@Autowired 可以对成员变量、方法以及构造函数进行注释，而 @Qualifier 的标注对象是成员变量、方法**入参**、构造函数**入参**。

```java
@Autowired
public void setDataSource(@Qualifier("myDataSource")  DataSource dataSource){
    super.setDataSource(dataSource);
}
```

**@Inject**

1、@Inject是JSR330 (Dependency Injection for Java)中的规范，需要导入javax.inject.Inject;实现注入。

2、@Inject是根据**类型**进行自动装配的，如果需要按名称进行装配，则需要配合@Named；

3、@Inject可以作用在变量、setter方法、构造函数上。

和@Autowired一样, **@Inject和 @Named结合使用时，自动注入的策略就从 byType 转变成 byName** 

```java
@Inject
public void setDataSource(@Named("myDataSource")  DataSource dataSource){
    super.setDataSource(dataSource);
}
```

**@Resource**

1、@Resource是JSR250规范的实现，需要导入javax.annotation实现注入。

2、@Resource是根据**名称**进行自动装配的，一般会指定一个name属性

3、@Resource可以作用在变量、setter方法上。

```java
@Resource(name="myDataSource")
public void setDataSource(DataSource dataSource){
    super.setDataSource(dataSource);
}
```

**总结：**

1、@Autowired是spring自带的，@Inject是JSR330规范实现的，@Resource是JSR250规范实现的，需要导入不同的包

2、@Autowired、@Inject用法基本一样，不同的是@Autowired有一个request属性

3、@Autowired、@Inject是默认按照类型匹配的，@Resource是按照名称匹配的

4、@Autowired如果需要按照名称匹配需要和@Qualifier一起使用，@Inject和@Name一起使用



# 动态代理

 动态代理类，是在运行过程中动态生成的，在内存中

## jdk动态代理

Proxy所有代理类的父类，专门用于生成代理类或代理对象

```java
//用于生成代理类的Class对象
public static Class<?> getProxyClass(ClassLoader loader, Class<?>... interfaces){}
//用于生成代理对象及 Class对象的实例
public static Object newProxyInstance(ClassLoader loader,Class<?>[] interfaces,InvocationHandler h){}

//InvocationHandler 完成动态代理的整个过程
public Object invoke(Object proxy, Method method, Object[] args)throws Throwable{};    
```

## 动态代理思想

1.目标对象,要代理谁

2.如何获取代理对象

3.代理要做什么

## 动态实现原理

```java
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
```

## 动态代理实例

```java
package com.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
/**
 *
 *JDK代理
 *	1.Proxy 所有代理类的父类，专门用于生成代理类或代理对象
 *	用于生成代理类的Class对象
 *	public static Class<?> getProxyClass(ClassLoader loader, Class<?>... interfaces)
 *	用于生成代理对象及 Class对象的实例
 *	public static Object newProxyInstance(ClassLoader loader,Class<?>[] interfaces,InvocationHandler h)
 *
 *	2.InvocationHandler 完成动态代理的整个过程
 *	public Object invoke(Object proxy, Method method, Object[] args)throws Throwable;
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
            }});		
			return proxy;
		} 
	}
}
```

# Aop

连接点:目标类(要代理的类)方法

通知:在目标类方法的前或者后等

切点 execution的表达式即为切点

切点表达式	execution(<权限修饰符> [返回值类型] [简单类名/全类名] [方法名]([参数列表]))

<权限修饰符> 可选

## spring切面5种通知

| 注解           | 名称     | 描述                                                         |
| -------------- | -------- | ------------------------------------------------------------ |
| @before        | 前置通知 | 在目标方法被调用前调用通知功能                               |
| @after         | 后置通知 | 在目标方法被调用完成后调用通知，不关心方法的返回值           |
| @AfterRunning  | 返回通知 | 在目标方法成功执行之后调用通知                               |
| @AfterThrowing | 异常通知 | 在目标方法抛出异常后调用通知                                 |
| @Around        | 环绕通知 | 通知包裹了被通知的方法，在被通知的方法调用之前和之后执行自定义的行为 |



## 在Spring中启用AspectJ注解支持

1.配置文件添加

```xml
<aop:aspectj-autoproxy>
```

2.用AspectJ注解声明切面

AopArith类为切面

```java
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
    @AfterThrowing(value = "ctrl()", throwing = "t")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable t) {
      	// Throwable是所有错误和异常类的超类,推荐
		log.info("the method: " + getAllName(joinPoint) + " throws: " + t);
	}
}
```

## 切面的优先级

使用@order(int)注解设置切面的优先级

不指定order参数，默认为Integer.MAX_VALUE,值越小优先级越高

前置通知,从小到大. 后置返回(通知),从大到小

| 通知            | 优先级                     |
| --------------- | -------------------------- |
| @Before         | @Order(5)  ---> @Order(10) |
| @After          | @Order(10) ---> @Order(5)  |
| @AfterReturning | @Order(10) ---> @Order(5)  |

## 重用切入点表达式

定义切点

```java
@Pointcut("execution( * com.aop.annocation.*.*(..))")
public void declarePointCut() {};
```

重复使用切点

```java
@Before("declarePointCut()")
public void beforeLog(JoinPoint joinPoint) {
	String methodName = joinPoint.getSignature().getName();
	Object[] args = joinPoint.getArgs();
	System.out.println("loggerAop===>The method :"+methodName+" begin with "+Arrays.asList(args));	
}

```

# 事务

## 事务的四个关键属性

1.原子性

事务中的所有操作，同时成功或同时失败

2.一致性

数据执行事务前正确，执行事务后仍然正确

3.隔离性

多个事务在并发过程中不会相互干扰

4.持久性

事务执行完成之后，数据的修改被永久的保存下来，不会因为系统等错误而受到影响

## spring声明式事务

声明式事务：通过配置的形式，基于AOP的方式，动态地把管理事务的代码作用在目标方法上

Spring的核心事务管理抽象是PlatformTransactionManager，主要实现类有:

1)DataSourceTransactionManager：在应用程序中只需要处理一个数据源，而且通过JDBC存取。

2)JtaTransactionManager：在JavaEE应用服务器上用JTA(Java Transaction API)进行事务管理

3)HibernateTransactionManager：用Hibernate框架存取数据库

### 启动事务

```xml
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
```

### @Transactional

@Transactional注解既可以标注在方法上，也可以标注到类上，标注到类上，表示当前类中的所有方法都起作用标注到方法上表示只对当前方法起作用。优先级 方法标注 > 类标注。

### 事务管理属性设置

#### 事务的传播行为

事务的传播行为propagation:一个事务B方法被另外一个事务A方法调用时，B事务如何使用事务

| 属性                     | 属性说明                   | 说明                           |
| ------------------------ | -------------------------- | ------------------------------ |
| Propagation.REQUIRED     | 使用已有事务               | 使用A事务，默认值              |
| Propagation.REQUIRES_NEW | 挂起当前事务，创建新的事务 | 挂起A事务，创建新的事务B来使用 |

#### 事务的隔离级别

事务的隔离级别isolation，默认：Isolation.READ_COMMITTED 读已提交

#### Mysql的隔离级别 

| 编号 | 功能     | 造成后果   | 描述                                                         |
| ---- | -------- | ---------- | ------------------------------------------------------------ |
| 1    | 读未提交 | 脏读       | 事务B可以读到事务A未提交的数据，A事务有可能回滚              |
| 2    | 读已提交 | 不可重复读 | 主要描述修改操作。x=10,事务A将其修改为3,但A未提交,事务B读出x=10,A提交后,B再次读x=3 |
| 4    | 可重复读 | 幻读       | 主要描述插入操作，x=10,事务A将x修改3,但A未提交,事务B读x时,并未读到,事务c插入y=20,B再读则读出y=10; |
| 8    | 串行化   | 效率低     | A事务在执行时，任何事务都不可操作数据库，只能排队等待        |

#### 事务的回滚与不会滚

事务的回滚与不会滚rollbackFor,rollbackForClassName

默认情况下，spring会对所有的运行时异常进行回滚，可配置遇到那种异常进行回滚或那种异常不会滚

| 属性                   | 描述                                                      |
| ---------------------- | --------------------------------------------------------- |
| rollbackFor            | Class[] rollbackFor={AccountException.class,......}       |
| rollbackForClassName   | String[] rollbackForClassName={"AccountException",......} |
| noRollbackFor          | Class[] rollbackFor={AccountException.class,......}       |
| noRollbackForClassName | String[] rollbackForClassName={"AccountException",......} |

#### 事务的只读

事务的只读设置readOnly

| 属性值 | 描述                                                         |
| ------ | ------------------------------------------------------------ |
| true   | 只读，不可进行修改操作，也可进行修改操作，但spring不会对此次操作进行加锁，spring在对数据进行修改操作进行加锁,与事务的隔离级别相关，只读不加锁效率更高 |
| false  | 非只读，默认值，                                             |



#### 事务的超时设置

事务的超时设置timeout，设置事务在强制回滚之前可以占用的时间

# @Scheduled

spring支持注解时声明定时任务。单线程执行定时任务

this.localExecutor = Executors.newSingleThreadScheduledExecutor();

## Spring配置定时任务

```xml
在spring配置文件中加入xmlns:task="http://www.springframework.org/schema/task"声明
开启注解扫描<context:component-scan base-package="com.wei.task"></context:component-scan>
启用定时任务注解<task:annotation-driven/>
```

## 参数说明

cron:cron表达式

cron表达式语法:[秒] [分] [小时] [日] [月] [周] [年],[年]不是必须的域，可以省略[年]，则一共6个域

| 序号 | 说明 | 必填 | 允许填写的值  | 允许的通配符  |
| ---- | ---- | ---- | ------------- | ------------- |
| 1    | 秒   | 是   | 0-59          | , - * /       |
| 2    | 分   | 是   | 0-59          | , - * /       |
| 3    | 时   | 是   | 0-23          | , - * /       |
| 4    | 日   | 是   | 1-31          | , - * /       |
| 5    | 月   | 是   | 1-12/JAN-DEC  | , - * ? / L W |
| 6    | 周   | 是   | 1-7 / SUN-SAT | , - * ? / L # |
| 7    | 年   | 否   | 1970-2099     | , - * /       |

## 通配符说明



| 通配符 | 说明                                                         |
| ------ | ------------------------------------------------------------ |
| *      | 表示所有值                                                   |
| ?      | 表示不指定值。使用的场景为不需要关心当前设置这个字段的值 例:0 0 0 10 * ? 每月10号触发，不关心周 |
| -      | 表示区间 例 0 0 10-12 * * ？ 每天10，11,12点触发             |
| ,      | 表示指定多个值 例 0 26,29,33 * * * ? 在26分、29分、33分执行一次 |
| /      | 用于递增触发 例:*/5 * * * * ? 每隔5秒执行一次                |
| L      | 表示最后的意思 例: 0 0 23 L * ? 每月最后一天23点执行一次     |
| W      | 表示离指定日期的最近那个工作日 例: 0 0 0 15w * ? , 每月15号最近的那个工作日触发(周一至周五) |
| #      | 序号(表示每月的第几个周几) 例：0 0 0 0 * 6#3 在每月的第三个周六 |

## Scheduled属性



| 属性               | 说明                                                         |
| ------------------ | ------------------------------------------------------------ |
| fixedDelay         | 上一次执行完毕时间点之后多长时间再执行                       |
| fixedDelayString   | @Scheduled(fixedDelayString = "5000")<br />支持占位符,占位符的使用(配置文件中有配置：time.fixedDelay=5000)@Scheduled(fixedDelayString = "${time.fixedDelay}") |
| fixedRate          | 上一次开始执行时间点之后多长时间再执行                       |
| fixedRateString    | 与fixedRate意思相同，用法与fixedDelayString相同              |
| initialDelay       | 第一次延迟多长时间后再执行                                   |
| initialDelayString | 与 initialDelay 意思相同，支持占位符                         |

# Spring ApplicationListener

​	 ApplicationContext事件机制是观察者设计模式的实现，通过ApplicationEvent类和ApplicationListener接口，可以实现ApplicationContext事件处理。

​	 如果容器中有一个ApplicationListener Bean，每当ApplicationContext发布ApplicationEvent时，ApplicationListener Bean将自动被触发。这种事件机制都必须需要程序显示的触发。

spring内置事件

| Spring 内置事件           | 描述                                                         |
| ------------------------- | ------------------------------------------------------------ |
| **ContextRefreshedEvent** | ApplicationContext 被初始化或刷新时，该事件被发布。这也可以在 ConfigurableApplicationContext接口中使用 refresh() 方法来发生。此处的初始化是指：所有的Bean被成功装载，后处理Bean被检测并激活，所有Singleton Bean 被预实例化，ApplicationContext容器已就绪可用 |
| **ContextStartedEvent**   | 当使用 ConfigurableApplicationContext （ApplicationContext子接口）接口中的 start() 方法启动 ApplicationContext 时，该事件被发布。你可以调查你的数据库，或者你可以在接受到这个事件后重启任何停止的应用程序。 |
| **ContextStoppedEvent**   | 当使用 ConfigurableApplicationContext 接口中的 stop() 停止 ApplicationContext 时，发布这个事件。你可以在接受到这个事件后做必要的清理的工作。 |
| **ContextClosedEvent**    | 当使用 ConfigurableApplicationContext 接口中的 close() 方法关闭 ApplicationContext 时，该事件被发布。一个已关闭的上下文到达生命周期末端；它不能被刷新或重启。 |
| **RequestHandledEvent**   | 这是一个 web-specific 事件，告诉所有 bean HTTP 请求已经被服务。只能应用于使用DispatcherServlet的Web应用。在使用Spring作为前端的MVC控制器时，当Spring处理用户请求结束后，系统会自动触发该事件。 |

代码实例:

```java
@Component
public class TestApplicationListener implements ApplicationListener<ContextRefreshedEvent>{
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        //ApplicationContext 初始化或刷新时触发该方法
    }
}
```

