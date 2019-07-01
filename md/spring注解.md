# 依赖

> 使用maven项目搭建spring项目，引入依赖时，不需要每个包都进行依赖,引入一个则其他相关的依赖都会引入

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.1.7.RELEASE</version>
</dependency>
```

# @Configuration

> 标注该类为配置类等同于配置文件

将配置类标注@Configuration注解，spring会知道该类为配置类

注解方式获取IOC容器,其中Config为配置类

```java
ApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
```

# @Bean

> 注解等同于xml文件中<bean>标签，表示给容器中注册一个组件

默认使用方法名作为bean的id，bean注解value属性可以对bean的ID进行自定义

# @ComponentScan

> 包扫描

value属性指定要扫描的包，返回值为string数组

```java
@AliasFor("basePackages")
String[] value() default {};
```

与<context:component-scan>标签相同@ComponentScan注解也可以使用过滤规则

excludeFilters排除包扫描返回值为Filter数组，按照指定规则排除那些组件

```java
ComponentScan.Filter[] excludeFilters() default {};
```

includeFilters按照指定规则包含哪些组件只包含时，需禁用默认的扫描规则，默认包含所有组件，禁用后才能实现只包含

```java
boolean useDefaultFilters() default true;
```

其中Filter也是一个注解

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Filter {
    @AliasFor("classes")
    Class<?>[] value() default {}; //按照类的规则进行过滤

    @AliasFor("value")
    Class<?>[] classes() default {};

    String[] pattern() default {};
}
```

Filter可指定条件进行过滤，可指定按照注解排除，也可按照类进行排除FilterType type() default FilterType.ANNOTATION; //默认按照注解进行过滤

常用的过滤规则

FilterType.ANNOTATION,按照指定注解进行过滤

FilterType.ASSIGNABLE_TYPE，按照指定类型过滤

FilterType.CUSTOM 使用自定义规则

自定义规则必须为TypeFilter的实现类

```java
public class MyFilter implements TypeFilter {

    /**
     * @param metadataReader 读取到的当前正在扫描的类信息
     * @param metadataReaderFactory 可以获取到其他任何类的信息
     * @return
     * @throws IOException
     */

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        //获取当前类注解的信息
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();

        //获取当前正在扫描类的类信息
        ClassMetadata classMetadata = metadataReader.getClassMetadata();

        //获取当前类的资源信息（类的路径等）
        Resource resource = metadataReader.getResource();

        //获取当前类的名称
        String className = classMetadata.getClassName();
        System.out.println("---->"+className);

        if(className.contains("er")){
            return true;
        }
		//返回false表示过滤条件不成功，不将bean注册到容器中
        return false;
    }
}
```

# @Scope

>在IOC容器中，加载的组件都是单实例

使用@Scope注解调整组件的作用域

Scope取值有四种

prototype:原型，每次获取都会创建一个新的bean，这些bean不受springIOC容器管理，调用时spring会帮助创建并初始化，但不会调用启销毁方法，
singleton：单例，默认，在IOC容器启动时会调用组件的构造方法来创建组件放入到IOC容器中，以后都是从IOC容器中获取
request:同一个请求一个实例
session:同一次会话一个实例

# @Lazy

>懒加载

针对单实例bean，调整单实例bean的创建时间，懒加载,容器启动时，不加载对象。第一次使用bean时创建对象

# @Conditional

> 条件加载组件，只有满足Conditional的条件才可以将组件注册到IOC容器中

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Conditional {
    Class<? extends Condition>[] value();
}
```

@Conditional中只有一个参数是Condition的数组

```java
@FunctionalInterface
public interface Condition {
    boolean matches(ConditionContext var1, AnnotatedTypeMetadata var2);
}
```

而Condition是一个数组需实现matches方法，matches方法返回true则会将组件注册到IOC容器中

# 给容器中注册组件方式

> 1.包扫描+组件标注注解（@Controller、@Service等）

> 2.@Bean - 将第三方包中得组件，使@Bean注解

> 3.使用@Import给容器中快速导入一个组件或一批组件

```java
@Import
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Import {
    Class<?>[] value();
}
```

@Import的value属性为Class<?>[]数组，该注解只能标注在类上
在配置类标注@Import({Color.class})即为将Color组件注册到IOC容器中
@Import方式注册的组件的ID为类的全类名

> 4.实现ImportSelector接口将实现类使用@Import注解注册到IOC容器中

```java
public interface ImportSelector {
    String[] selectImports(AnnotationMetadata var1);
}
```

selectImports()方法返回String[],即为将要导入IOC容器中类的全类名数组，即可批量导入组件

```java
public class MyImportSeletor implements ImportSelector {
    //返回值为要导入到IOC容器中的组件类
    /*
        AnnotationMetadata：当前标注@Import注解的类的所有注解信息及
     */
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{"com.wei.beans.Red"};
    }
}
```

在配置类标注@Import({MyImportSeletor.class}),看似导入的是MyImportSeletor类，但实际导入的是Red类，实现ImportSelector接口，会将selectImports接口中返回的所有全类名数组都会导入到容器中

> 5.实现ImportBeanDefinitionRegistrar接口将实现类使用@Import注解注册到IOC容器中

```java
public interface ImportBeanDefinitionRegistrar {
    void registerBeanDefinitions(AnnotationMetadata var1, BeanDefinitionRegistry var2);
}
```

实现ImportBeanDefinitionRegistrar接口，给容器中自己添加一些组件

```java
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

/*
    BeanDefinitionRegistry bean定义的注册类，所有的bean都在此处注册，可以使用beanDefinitionRegistry给容器中注册组件
        可以使用beanDefinitionRegistry的registerBeanDefinition手动注册组件

        注册组件时，需要添加一个BeanDefinition，此为一个接口
 */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata,
                                        BeanDefinitionRegistry beanDefinitionRegistry) {
        //判断当前注册的组件中是否用指定的类
        beanDefinitionRegistry.containsBeanDefinition("");
		//指定bean的定义信息
        RootBeanDefinition blue = new RootBeanDefinition(Yellow.class);
		//指定bean的名称
        beanDefinitionRegistry.registerBeanDefinition("yellow", blue);
    }
}
```

> 6.使用spring提供的FactoryBean工厂将组件注册到容器中

```java
public class ColorFactoryBean implements FactoryBean {

    //是否为单例 true为单实例 ；false为原型
    @Override
    public boolean isSingleton() {
        return true;
    }

    /*
     *  返回一个对象，该对象会添加到容器中,此为懒加载
     */
    @Override
    public Object getObject() throws Exception {
        System.out.println("ColorFactoryBean...getObject()");
        return new Color();
    }

    //返回类的类型
    @Override
    public Class<?> getObjectType() {
        return Color.class;
    }
}
```

将FactoryBean实现类使用@Bean注解添加到容器中

```java
@Bean
public ColorFactoryBean colorFactoryBean(){
    return new ColorFactoryBean();
}
```

在调用bean时，默认获取到的是工厂bean的getObject返回的bean

要获取工厂bean本身，需要给id前添加&标记

```java
Object bean = ctx.getBean("&colorFactoryBean");
```

原因：BeanFactory在定义时会给beanID添加一个前缀来标注自身

```java
public interface BeanFactory {
	String FACTORY_BEAN_PREFIX = "&"; //定义FactoryBean本身
	.....
}
```

# 指定bean的初始化和销毁方法

受springIOC容器管理的bean可指定其初始化和销毁方法

> 1.使用@Bean注解的属性来指定，在创建bean时指定其初始化和销毁方法

```java
@Bean(initMethod = "init",destroyMethod = "destory")
public Car car(){
	return new Car();
}
```

`spring在创建多实例bean时，spring只负责创建和初始化，销毁方法不会执行，需自行销毁，容器中不管理多实例bean`

```java
@Scope("prototype")
@Bean(initMethod = "init",destroyMethod = "destory")
public Car car(){
	return new Car();
}
```

>2.spring提供了初始化InitializingBean和销毁接口DisposableBean

```java
public interface InitializingBean {
    void afterPropertiesSet() throws Exception;
}
public interface DisposableBean {
    void destroy() throws Exception;
}
```

bean实现这两个接口，spring在启动时会执行该类的初始化，spring容器在关闭时会执行该类的销毁方法

实例:

```java
public class Cat implements InitializingBean, DisposableBean {...}
```

> 3.使用JSR250提供的注解对bean进行初始化和销毁

@PostConstruct //bean创建完成+赋值完成后，再执行此初始化方法

@PreDestroy //容器销毁bean之前执行销毁方法

```java
@Component
public class Pet {
    public Pet() {
        System.out.println("Pet...construct...");
    }

    //bean创建完成+赋值完成后，再执行此初始化方法
    @PostConstruct
    public void init(){
        System.out.println("Pet...init...");
    }

    //容器销毁bean之前执行销毁方法
    @PreDestroy
    public void destory(){
        System.out.println("Pet...destory...");
    }
}
```

> 4.实现BeanPostProcessor接口

postProcessBeforeInitialization: bean创建完成属性赋值完成后，init方法前调用

postProcessAfterInitialization: bean的init方法执行完成后调用

作用于整个IOC容器的所有bean

原理：

```java
this.populateBean(beanName, mbd, instanceWrapper);//给bean属性赋值
this.initializeBean(){
	//遍历所有的BeanPostProcessor实现类，若bean返回null则跳出循环
	this.applyBeanPostProcessorsBeforeInitialization(bean, beanName); //执行BeanPostProcessor的postProcessBeforeInitialization方法
	this.invokeInitMethods(beanName, wrappedBean, mbd);//执行bean的init方法
	this.applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);//执行BeanPostProcessor的postProcessAfterInitialization方法
}

protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {
        if (System.getSecurityManager() != null) {
            AccessController.doPrivileged(() -> {
                this.invokeAwareMethods(beanName, bean);
                return null;
            }, this.getAccessControlContext());
        } else {
            this.invokeAwareMethods(beanName, bean);
        }

        Object wrappedBean = bean;
        if (mbd == null || !mbd.isSynthetic()) {
            wrappedBean = this.applyBeanPostProcessorsBeforeInitialization(bean, beanName);
        }

        try {
            this.invokeInitMethods(beanName, wrappedBean, mbd);
        } catch (Throwable var6) {
            throw new BeanCreationException(mbd != null ? mbd.getResourceDescription() : null, beanName, "Invocation of init method failed", var6);
        }

        if (mbd == null || !mbd.isSynthetic()) {
            wrappedBean = this.applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
        }

        return wrappedBean;
    }
```

# 给spring bean属性赋值

>1.@Value注解给属性赋值

@value属性值可以为

基本数值，字符串

可以写SpEL表达式#{}

可以写${}，读取properties配置文件中的值

`读取properties配置文件的值时,需将配置文件加载到spring的运行环境中使用@PropertySource来加载配置文件，指明配置文件的路径地址`

```java
//配置文件的路径地址
@PropertySource("classpath:/properties.properties")
@Configuration
public class ConfigValue {

    @Bean
    public Person person(){
        return new Person();
    }
}
```

使用@PropertySource加载的配置信息，会将信息加载到Environment中。

```java
ConfigurableEnvironment environment = context.getEnvironment();
String property = environment.getProperty("person.nikeName");
```

```java
public class Person {
    @Value("张三丰")
    private String name;
    @Value("#{100-2}")
    private int age;
	//指明要获取properties配置文件中的key
    @Value("${person.nikeName}")
    private String nikeName;
	....
}
```

# @Autowired

1.默认按照属性的类型去IOC容器在中查找组件，即context.getBean(Bean.class);

2.在IOC查找到多个相同类型的组件时，会将`属性的名称作为id`在IOC中查找,context.getBean("bean")

required属性，默认为true，及必须装配该组件，没有则抛异常，false时，该组件可不装配，此时该组件为null

@Autowired可使用的场景，构造器、方法、参数、属性

```java
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface Autowired(){...}
```

标注在方法上:方法使用的参数，从IOC容器中获取(自定义类型的参数)

常用@Bean+方法参数，参数从容器中获取,@Autowired可以省略

```java
example:
@Autowired
public void setCar(Car car) {
	this.car = car;
}
@Bean
//    @Autowired 此处的注解可以省略
public Boss boss(Car car){
	return new Boss(car);
}
```

标注在有参构造器上:容器启动时，默认调用组件的无参构造器来创建对象,如果组件的只有有参构造器，且参数只有一个自定义参数，则可以省略@Autowired的标注，参数位置的组件也可从容器中获取到

```java
@Autowired
public Boss(Car car) {
	this.car = car;
}
//标注在参数上
public Boss( @Autowired Car car) {}

//上述两种@Autowired都可省略
```

# @Qualifier

>@Qualifier与 @Autowired一起使用

IOC容器中有多个相同类型bean时，指明bean ID来装配，而不是以属性名为bean的id在IOC容器中查找

# @Primary

> 在IOC容器中有多个类型的bean时，没有指明使用哪个bean的情况下，有该注解标注的bean为默认首选bean

优先级

@Qualifier>@Primary

# @Resource

>与@Autowired类似，可以对组件进行装配

根据属性的名称进行装配，不支持@Primary注解，没有required属性，有name属性，可以指明使用哪个ID的组件

# @Inject

>与@Autowired类似，支持@Primary和@Qualifier注解

没有required属性，需要导入javax.inject依赖关系

这些根据注解可以完成自动注入的功能生效的原因为:`AutowiredAnnotationBeanPostProcessor`该类解析完成自动装配的功能

# 获取spring底层组件

>自定义组件想要使用spring底层的一些组件（applicationcontext/beanFactory）,只需要让该类实现xxxAware组件即可，在对象创建时会调用xxxAware的接口的实现方法，并注入相关的组件

ApplicationContextAware:自动注入IOC容器

EmbeddedValueResolverAware 值解析器，可以对String字符串的值进行解析,支持${}获取环境变量和配置文件中的值，支持spEL表达式

BeanNameAware bean传入IOC容器中的名字

xxxAware会以接口方法回调的方式将组件传入到当前对象中

其实现原理这些接口都有对应的xxxAwareProcessor来实现，都是后置处理来的原理

```java
其实现原理这些接口都有对应的xxxAwareProcessor来实现，都是后置处理来的原理
@Component
public class Red implements ApplicationContextAware, BeanNameAware, EmbeddedValueResolverAware {

//    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("获取IOC容器Red:"+applicationContext);
//        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanName(String s) {
        System.out.println(s);
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        String s = stringValueResolver.resolveStringValue("你好：${os.name},#{10*10}");
        System.out.println(s);
    }
}
```

# @profile

> spring提供了根据当前的环境，动态激活组件的功能

@profile指定组件在指定的环境下才可以被激活

组件不指定@profile环境时，默认在任何环境下都加载

添加了@profile环境标识的组件，只有在指定的环境的情况下，才会被加载到IOC容器中，默认为default环境

```java
@PropertySource("classpath:/db.properties")
@Configuration
public class ConfigProfile implements EmbeddedValueResolverAware {

    @Value("${db.username}")
    private String user;
    @Value("${db.password}")
    private String password;

    @Value("${db.jdbcUrl}")
    private String jdbcUrl;

    private String driverClass;

    @Bean
    public DataSource dataSource() throws Exception{
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setDriverClass(driverClass);
        return dataSource;
    }

    @Profile("test")
    @Bean
    public DataSource dataSourceTest() throws Exception{
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setDriverClass(driverClass);
        return dataSource;
    }
    @Profile("prod")
    @Bean
    public DataSource dataSourceProd() throws Exception{
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setDriverClass(driverClass);
        return dataSource;
    }
    @Profile("dev")
    @Bean
    public DataSource dataSourceDev() throws Exception{
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setDriverClass(driverClass);
        return dataSource;
    }


    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        driverClass = stringValueResolver.resolveStringValue("${db.driverClass}");
    }
}
```

>使用代码的方式激活运行环境

启动创建IOC容器时使用无参构造器

```java
//1.创建IOC容器
AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
//2.设置需要激活的环境
context.getEnvironment().setActiveProfiles("dev");
//3.注册配置类
context.register(ConfigProfile.class);
//4.启动刷新容器
context.refresh();
//@Profile写在配置类上，只有在指定情况下整个配置类才能生效
```

# AOP

>动态代理：指在程序运行期间动态的将某段代码切入到指定方法的位置进行运行的编译方式

> 1.需要导入AOP依赖包

```xml
<!-- https://mvnrepository.com/artifact/org.springframework/spring-aspects -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aspects</artifactId>
    <version>5.1.8.RELEASE</version>
</dependency>
```

>2.定义一个切面类，该类需要动态的感知目标方法运行到哪里，然后执行

>3.通知方法

```java
前置通知:(@Before)目标方法执行之前运行
后置通知:(@After)目标方法执行之结束后运行,无论方法是正常结束还是异常结束都会运行该方法
返回通知:(@AfterReturning)目标方法正常返回之后运行
异常通知:(@AfterThrowing)目标方法运行出现异常后运行
环绕通知:动态代理，手动推进目标方法运行(proceedingJoinPoint.proceed();)
```

>4.给切面类的目标方法标注何时何地运行即通知注解

> 5.将切面类和目标方法所在的类加入到IOC容器中 

>6.在切面类上标注@Aspect注解，告诉IOC容器此类为切面类

>7.需要给配置文件中加入@EnableAspectJAutoProxy注解，自动开启AspectJ代理功能，启用基于注解的aop模式

# @AfterReturning

>获取目标方法的返回值

```java
@AfterReturning(value = "divPointCut()",returning = "back")
public void afterReturnDiv(JoinPoint joinPoint,int back){
   System.out.println("afterReturnDiv->"+joinPoint.getSignature().getName()+",返回结果:"+back);
}
```

注意：如果要在方法上使用JoinPoint joinPoint参数时，该参数必须写在该方法参数的第一位，否则会报错

public void afterReturnDiv(int back，JoinPoint joinPoint){} 这样写在运行时会报错

# AOP原理

>在纯注解模式下，只有标注了@EnableAspectJAutoProxy这个注解，aop才能起作用而

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({AspectJAutoProxyRegistrar.class})
public @interface EnableAspectJAutoProxy {
    boolean proxyTargetClass() default false;

    boolean exposeProxy() default false;
}
```

在该类中导入了 AspectJAutoProxyRegistrar类

```java
public class AspectJAutoProxyRegistrar implements ImportBeanDefinitionRegistrar{...}
```

利用AspectJAutoProxyRegistrar给容器注册AnnotationAwareAspectJAutoProxyCreator

继承与实现关系

```java
AnnotationAwareAspectJAutoProxyCreator 
	->AspectJAwareAdvisorAutoProxyCreator
		->AbstractAdvisorAutoProxyCreator
		 ->AbstractAutoProxyCreator implements SmartInstantiationAwareBeanPostProcessor
```

# 事务

>1.使用事务首先要配置数据库DataSource

将DataSource注册到IOC容器中

```java
@Bean
    public DataSource dataSource() throws Exception{
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setUser("bluecardsoft");
        dataSource.setPassword("#$%_BC13439677375");
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:33306/springtx?serverTimezone=GMT%2B8");
        dataSource.setDriverClass("com.mysql.cj.jdbc.Driver");
        return dataSource;
    }
```

>2.向容器中注入transactionManager

```java
@Bean
public PlatformTransactionManager transactionManager(DataSource dataSource){
    return new DataSourceTransactionManager(dataSource);
}
```

>3.在配置类型添加@EnableTransactionManagement注解开启事务

>4.在需要使用事务的方法上添加@Transactional注解

可以使用Transactional注解的属性rollbackFor来执行那种情况下进行事务回滚

# @EnableTransactionManagement

```java
@Import(TransactionManagementConfigurationSelector.class)
public @interface EnableTransactionManagement {...}
```

EnableTransactionManagement注解导入了TransactionManagementConfigurationSelector类

> TransactionManagementConfigurationSelector

向IOC容器中注册`AutoProxyRegistrar`、`ProxyTransactionManagementConfiguration`两个类

> AutoProxyRegistrar

```java
@Override
protected String[] selectImports(AdviceMode adviceMode) {
	switch (adviceMode) {
		case PROXY:
			return new String[] {AutoProxyRegistrar.class.getName(), ProxyTransactionManagementConfiguration.class.getName()};
		case ASPECTJ:
			return new String[] {TransactionManagementConfigUtils.TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME};
		default:
			return null;
	}
}
```

而AutoProxyRegistrar implements ImportBeanDefinitionRegistrar

```java
AutoProxyRegistrar.registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    Set<String> annTypes = importingClassMetadata.getAnnotationTypes();
    //获取到所有的注解集合，并判断注解集合中是否存在@EnableTransactionManagement注解
    /*
    如果@EnableTransactionManagement中的mode()返回的是AdviceMode.PROXY则向容器中注册InfrastructureAdvisorAutoProxyCreator类
    */
    AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
}
```

```java
AopConfigUtils.java

@Nullable
public static BeanDefinition registerAutoProxyCreatorIfNecessary(BeanDefinitionRegistry registry, @Nullable Object source) {
        return registerOrEscalateApcAsRequired(InfrastructureAdvisorAutoProxyCreator.class, registry, source);
    }
```

```java
InfrastructureAdvisorAutoProxyCreator继承关系

InfrastructureAdvisorAutoProxyCreator extends AbstractAdvisorAutoProxyCreator
	AbstractAdvisorAutoProxyCreator extends AbstractAutoProxyCreator
	AbstractAutoProxyCreator extends ProxyProcessorSupport implements SmartInstantiationAwareBeanPostProcessor, BeanFactoryAware
	SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor
	InstantiationAwareBeanPostProcessor extends BeanPostProcessor
```

InfrastructureAdvisorAutoProxyCreator利用后置处理器机制在对象创建之后，包装对象，返回一个代理对象,代理对象执行方法按照拦截器链挨个进行执行调用

> ProxyTransactionManagementConfiguration

向IOC中注册事务增强器BeanFactoryTransactionAttributeSourceAdvisor
BeanFactoryTransactionAttributeSourceAdvisor中需要注册`AnnotationTransactionAttributeSource`、`TransactionInterceptor`

> AnnotationTransactionAttributeSource事务注解解析器

```java
public AnnotationTransactionAttributeSource(boolean publicMethodsOnly) {
		this.publicMethodsOnly = publicMethodsOnly;
		this.annotationParsers = new LinkedHashSet<TransactionAnnotationParser>(2);
		this.annotationParsers.add(new SpringTransactionAnnotationParser());//事务注解解析器
		if (jta12Present) {
			this.annotationParsers.add(new JtaTransactionAnnotationParser());
		}
		if (ejb3Present) {
			this.annotationParsers.add(new Ejb3TransactionAnnotationParser());
		}
	}
```

SpringTransactionAnnotationParser中解析事务注解属性等信息，propagation、isolation、rollbackFor等

> TransactionInterceptor事务拦截器

保存了事务的属性、事务管理器，底层为方法拦截器MethodInterceptor，代理对象在执行方法时会执行方法拦截器，方法拦截器就行进行工作。

在目标方法执行时:

```java
执行拦截器链
事务拦截器
```

> 1.先获取事务相关的属性

> 2.在获取PlatformTransactionManager事务管理器，如果事务指定了事务管理的名称，则按照执行名称查找事务管理器，如果没有则按照类型在IOC容器中查找PlatformTransactionManager类型的事务管理器

在TransactionInterceptor类中执行invoke方法时，

```java
protected Object invokeWithinTransaction(Method method, Class<?> targetClass, final InvocationCallback invocation)
	throws Throwable {

		// If the transaction attribute is null, the method is non-transactional.判断事务属性信息
		final TransactionAttribute txAttr = getTransactionAttributeSource().getTransactionAttribute(method, targetClass);
		final PlatformTransactionManager tm = determineTransactionManager(txAttr);
		final String joinpointIdentification = methodIdentification(method, targetClass, txAttr);

		if (txAttr == null || !(tm instanceof CallbackPreferringPlatformTransactionManager)) {
			// Standard transaction demarcation with getTransaction and commit/rollback calls.
			TransactionInfo txInfo = createTransactionIfNecessary(tm, txAttr, joinpointIdentification);//创建事务
			Object retVal = null;
			try {
				// This is an around advice: Invoke the next interceptor in the chain.
				// This will normally result in a target object being invoked.
				retVal = invocation.proceedWithInvocation();
			}
			catch (Throwable ex) {
				// target invocation exception
				completeTransactionAfterThrowing(txInfo, ex);//事务回滚
				throw ex;
			}
			finally {
				cleanupTransactionInfo(txInfo);//清除事务信息
			}
			commitTransactionAfterReturning(txInfo);//事务提交
			return retVal;
		}

		else {
			// It's a CallbackPreferringPlatformTransactionManager: pass a TransactionCallback in.
			try {
				Object result = ((CallbackPreferringPlatformTransactionManager) tm).execute(txAttr,
						new TransactionCallback<Object>() {
							@Override
							public Object doInTransaction(TransactionStatus status) {
								TransactionInfo txInfo = prepareTransactionInfo(tm, txAttr, joinpointIdentification, status);
								try {
									return invocation.proceedWithInvocation();
								}
								catch (Throwable ex) {
									if (txAttr.rollbackOn(ex)) {
										// A RuntimeException: will lead to a rollback.
										if (ex instanceof RuntimeException) {
											throw (RuntimeException) ex;
										}
										else {
											throw new ThrowableHolderException(ex);
										}
									}
									else {
										// A normal return value: will lead to a commit.
										return new ThrowableHolder(ex);
									}
								}
								finally {
									cleanupTransactionInfo(txInfo);
								}
							}
						});

				// Check result: It might indicate a Throwable to rethrow.
				if (result instanceof ThrowableHolder) {
					throw ((ThrowableHolder) result).getThrowable();
				}
				else {
					return result;
				}
			}
			catch (ThrowableHolderException ex) {
				throw ex.getCause();
			}
		}
	}
```

> 3.执行目标方法

如果遇到异常，获取事务管理器，进行事务回滚

如果正常，获取事务管理器，进行事务提交

# BeanFactoryPostProcessor

BeanFactory的后置的处理器，在BeanFactory的标准初始化之后执行，此时所有的bean的定义信息已经保存，但bean实例还未创建

# BeanDefinitionRegistryPostProcessor

```java
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor{
    ...
}
```

继承BeanFactoryPostProcessor接口

BeanDefinitionRegistryPostProcessor在BeanFactoryPostProcessor实现类执行之前执行，可以向容器中添加bean定义信息

# ApplicationListener

> 监听容器中发布的事件，属于事件驱动模型开发

```java
@FunctionalInterface
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {
    void onApplicationEvent(E var1);
}

```

实现步骤：

```java
1.写一个监听器来监听某个事件（ApplicationEvent及其子类）
2.把监听器加入到IOC容器中
3.只要容器中有相关的事件发布就能监听到这个事件
4.发布事件
```

```java
    @Test
    public void testEvent(){
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(EventConfig.class);
        context.publishEvent(new ApplicationEvent(new String("hahahahah")) {
            @Override
            public Object getSource() {
                return super.getSource();
            }
        });
        context.close();
    }

```

# spring 总结

> 1.spring容器在启动时，先保存注册进来的bean的定义信息

bean的定义有两种方式

1.xml配置文件中使用bean标签创建bean定义信息

2.使用注解注册bean定义信息（@Bean、@service、@Controller等）

> 2.spring会在何时的时机创建bean对象

1.用到bean的时候创建，创建好后保存在容器中

2.统一创建所有bean时

> 3.后置处理器

bean在创建完成后，会使用各种后置处理器进行处理，来增强bean的功能

例如:@Autowired 注解就是利用后置处理器来处理自动注入

spring对bean的各种增强都是使用的后置处理器来对bean进行增强处理

> 4.事件驱动模型

ApplicationListener，用来做事件监听

# servlet3.0

> 仅支持tomcat7.0以上版本

spring应用启动时会加载WebApplicationInitializer接口下的所有组件，并为WebApplicationInitializer组件创建对象（这个组件对象非接口或抽象类）

WebApplicationInitializer的实现类：

> AbstractContextLoaderInitializer

```java
public abstract class AbstractContextLoaderInitializer implements WebApplicationInitializer {}
```

其中，该类会创建根容器createRootApplicationContext();

> AbstractDispatcherServletInitializer

```java
public abstract class AbstractDispatcherServletInitializer extends AbstractContextLoaderInitializer {}
```

创建web的ioc容器，createServletApplicationContext();

创建DispatcherServlet，createDispatcherServlet(servletAppContext);

将dispatcherServlet添加到web的servletContext中

```java
ServletRegistration.Dynamic registration = servletContext.addServlet(servletName, dispatcherServlet);
```

并设置dispatcherServlet的配置信息

```java
registration.setLoadOnStartup(1);
registration.addMapping(getServletMappings());
registration.setAsyncSupported(isAsyncSupported());
```

> AbstractAnnotationConfigDispatcherServletInitializer注解方式配置DispatcherServlet初始化器

```java
public abstract class AbstractAnnotationConfigDispatcherServletInitializer
		extends AbstractDispatcherServletInitializer {}
```

会创建根容器createRootApplicationContext()

1.先获取配置类getRootConfigClasses();从此方法中传入配置类信息，该方法为抽象方法，留给开发者实现

2.创建IOC容器

3.将配置类加载到IOC容器中

创建web的IOC容器createServletApplicationContext()

1.创建web的IOC容器new AnnotationConfigWebApplicationContext();

2.获取配置类，getServletConfigClasses();从此方法中传入配置类信息，该方法为抽象方法，留给开发者实现

3.将配置类信息注册到web的IOC容器中

> 以注解方式来启动springmvc

1.继承AbstractAnnotationConfigDispatcherServletInitializer类，

2.实现getRootConfigClasses()、getServletConfigClasses()抽象方法，

3.指定dispatchServlet配置信息，并将dispatcherServlet信息注册到servletContext中

注解方式的整体框图

![](../../../%E3%82%8F%E3%81%9F%E3%81%97/spring/md/assets/mvc-context-hierarchy.png)



注解方式配置springmvc时，主要分为两个容器，web的servlet容器和Root根容器，其职责如上图所示。两个容器形成互补容器，将所有的bean注入到spring容器中

# 自定义mvc配置

> @EnableWebMvc

开启springmvc可以自定义配置功能，该功能以之前xml中的<mvc:annotation-driven>相同

> WebMvcConfigurer

实现WebMvcConfigurer接口，可将自定义信息信息进行配置

springboot项目中WebMvcAutoConfigurationAdapter类对WebMvcConfigurer进行了实现，根据主配置类中的配置信息将bean注入到spring容器中

```java
public class MyWebMvcConfigurer implements WebMvcConfigurer {
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        //jsp("/WEB-INF/", ".jsp"); 默认前缀，后缀信息
        registry.jsp();
        //指定views所在地址
        registry.jsp("/WEB-INF/views",".jsp");


    }

    /**
     * 是否开启静态资源
     * <mvc:default-servlet-handler/>类似xml功能
     * @param configurer
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}

```

定义springmvc的一些自定义配置信息

# springmvc异步请求

> 返回Callable类型

```java
	@RequestMapping("/hello")
    public Callable<String> hello(HttpServletRequest request, HttpServletResponse response){
        System.out.println("主线程开始:"+Thread.currentThread()+"，时间："+System.currentTimeMillis());
        return ()->{
            System.out.println("开始:"+Thread.currentThread()+"，时间："+System.currentTimeMillis());
            sayHello();
            System.out.println("结束:"+Thread.currentThread()+"，时间："+System.currentTimeMillis());
            return "hello";
        };

    }

    public void sayHello() throws InterruptedException {
        TimeUnit.SECONDS.sleep(5);
    }
```

控制台打印

```console
主线程开始:Thread[http-nio-8080-exec-2,5,main]，时间：1561706936196
开始:Thread[task-1,5,main]，时间：1561706936201
结束:Thread[task-1,5,main]，时间：1561706941201
```

Spring MVC 3.2 introduced Servlet 3 based asynchronous request processing. Instead of returning a value, as usual, a controller method can now return a java.util.concurrent.Callable and produce the return value from a Spring MVC managed thread. Meanwhile the main Servlet container thread is exited and released and allowed to process other requests. Spring MVC invokes the Callable in a separate thread with the help of a TaskExecutor and when the Callable returns, the request is dispatched back to the Servlet container to resume processing using the value returned by the Callable

SpringMVC 3.2引入了基于servlet 3的异步请求处理。与往常不同，控制器方法现在可以返回java.util.concurrent.callable，并从SpringMVC管理的线程生成返回值。同时退出并释放主servlet容器线程，允许处理其他请求。SpringMVC在一个单独的线程中在taskExecutor的帮助下调用callable，当callable返回时，请求被发送回servlet容器，以使用callable返回的值恢复处理。

<https://docs.spring.io/spring/docs/5.0.2.RELEASE/spring-framework-reference/web.html#mvc-ann-async>

1.控制器返回callable

2.spring异步处理，将callable提交到taskExecutor,使用一个别的线程进行处理

3.DispatcherServlet和Filter’s 退出Servlet 容器，但response 依旧保留打开

4.callable返回结果，springmvc将请求重新派发给容器恢复之前的过程

5.根据callable的返回结果，spring 继续进行视图渲染等流程

> 返回DeferredResult类型





