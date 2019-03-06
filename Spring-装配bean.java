
装配bean
	默认情况下,spring中的bean都是单例的
	配置spring容器
		- 显示配置{
			- java{
				javaConfig
				@Configuration
					表明这个类是配置类
					@Configuation等价于<Beans></Beans>
					@Bean等价于<Bean></Bean>
				@Bean
					Spring拦截带有@Bean注解的方法，并返回一个对象，该对象应注册到spring应用上下文中
					@Bean(name="lonelyHeartsClub") 通过name 属性来自定义命名id
					如果方法使用@Bean不标注别名，则默认为方法名称为id
					实例:
						@Configuration
						//@ComponentScan(basePackages = {"soundsystem","test"})
						//@ComponentScan(basePackageClasses = {SgtPeppers.class})
						public class CDPlayerConfig {
							@Bean
							public CompactDisc sgtPeppers() {
								return new SgtPeppers();
							}
							@Bean
							public CDPlayer cdPlayer(){
								return new CDPlayer(sgtPeppers());
							}
						}
						
						public class CDPlayer implements MediaPlayer {
							private CompactDisc cd;
							@Override
							public void play() {
								cd.play();
							}
							public CDPlayer(CompactDisc compactDisc){
								this.cd = compactDisc;
							}
						}
						public class SgtPeppers implements CompactDisc {
							private String title = "Sgt. peppers's Lonely Hearts Club Band";
							private String artist = "The Beatles";
							@Override
							public void play() {
								System.out.println("Playing "+title+" by "+ artist);
							}
						}
						
						测试类:
						@RunWith(SpringJUnit4ClassRunner.class)
						@ContextConfiguration(classes = CDPlayerConfig.class)
						public class CDPlayerTest {
							
						//	@Autowired
							private CompactDisc cd;
							
							@Autowired
						//	@Inject
							private MediaPlayer player;
							
						//	@Test
							public void cdShouldNotBeNull(){
								System.out.println(" cd = "+ cd + " 是否为null ："+ (cd==null));
								cd.play();
							}
							
							@Test
							public void play(){
								System.out.println(player==null);
								player.play();
							}
						}
					
			}
			- xml{
				XML不允许某个元素的多个属性具有相同的名字
				XML为Spring装配bean，必须要以<beans>元素为根，并声明多个模式(XSD)文件，这些文件定义了spring的XML元素
					<beans xmlns="http://www.springframework.org/schema/beans"
							xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
							xmlns:c="http://www.springframework.org/schema/c" 
							xsi:schemaLocation="http://www.springframework.org/schema/beans 
							http://www.springframework.org/schema/beans/spring-beans-4.0.xsd 
							http://www.springframework.org/schema/context	
							http://www.springframework.org/schema/context/spring-context-4.0.xsd
							">
				
				<bean class="com.soundsystem.SgtPeppers"/> 创建一个bean通过class属性来指定，并且要使用全限定的类名，没有
					明确指定ID，ID则为"com.soundsystem.SgtPeppers#0"0为计数形式
				<bean id="compactDisc" class="com.soundsystem.SgtPeppers"/> ID指定为compactDisc
				通过构造器注入bean{
					<constructor-arg>(构造器注入)元素进行DI注入bean{
						<bean id="cdPlayer" class="com.soundsystem.CDPlayer">
							<constructor-arg ref="compactDisc" />
						</bean>
						通过构造器将依赖bean注入
					}
					
					Spring c-命名空间进行DI注入bean 等同于<constructor-arg>{
						声明Spring c-命名空间
						
						<bean id="cdPlayer" class="com.soundsystem.CDPlayer" c:cd-ref="compactDisc"/>
						xmlns:c c命名空间声明 简化xml,提高可读性
						声明构造器参数
						c:cd-ref="compactDisc"
							c - 命名空间前缀
							cd - 构造器参数
							-ref - 注入bean的引用
							compactDisc - 要注入bean的id
						c:_0-ref="compactDisc"
							_0 - 表示参数的索引
					}
					
					装配集合{
						<set>、<list>是<constructor-arg>的子元素
						<value>用来指定列表中的每个元素
						<ref>元素可代替<value>
						<bean id="compactDisc" class="com.soundsystem.BlankDisc">
							<constructor-arg value="Sgt. Pepper's Lonely Hearts Club Band" />
							<constructor-arg value="The Beatles" />
							<!-- <constructor-arg><null/></constructor-arg> -->
							<constructor-arg>
								<list>
									<value>Sgt. Pepper's Lonely Hearts Club Band</value>
									<value>With a Little Help from My Friends</value>
									<value>Getting Better</value>
									<value>Fixing a Hole</value>
								</list>
							</constructor-arg>
							<constructor-arg>
								<list>
									<ref bean="sgtPeppers" />
									<ref bean="whiteAlbum" />
								</list>
							</constructor-arg>
						</bean>
						
						Spring util命名空间装配集合{
							声名spring util命名空间
							xmlns:util="http://www.springframework.org/schema/util"
							http://www.springframework.org/schema/util
							http://www.springframework.org/schema/util/spring-util-4.0.xsd
							
							<util:list id="trackList">
								<value>Sgt. Pepper's Lonely Hearts Club Band</value>
								<value>With a Little Help from My Friends</value>
								<value>Getting Better</value>
								<value>Fixing a Hole</value>
							</util:list>
							//进行list注入
							<bean id="compactDisc" class="com.soundsystem.BlankDisc">
								<constructor-arg value="Sgt. Pepper's Lonely Hearts Club Band" />
								<constructor-arg value="The Beatles" />
								<!-- <constructor-arg><null/></constructor-arg> -->
								<constructor-arg ref="trackList"/>
									
							</bean>
							
							<util:constant>引用某个类型的public static域，并将其暴露为bean
							<util:list>创建一个java.util.List类型的bean，其中包含值或者引用
							<util:map>创建一个java.util.Map类型的bean，其中包含值或者引用
							<util:properties>创建一个java.util.Properties类型的bean
							<util:property-path>引用一个bean的属性(或内嵌属性),并将其暴露为bean
							<util:set>创建一个java.util.Set类型的bean，其中包含值或者引用
						}
						
						
					}
				}
				通过属性注入bean{
					<property>注入bean{
						<bean id="cdPlayer" class="com.soundsystem.CDPlayer">
							<property name="cd" ref="compactDisc"/>
						</bean>
						<property>与<constructor-arg>使用类似<list>、<set>与<constructor-arg>用法一致
					}
					Spring p-命名空间进行DI注入bean 等同于<property>{
						声明Spring p命名空间				
						xmlns:p="http://www.springframework.org/schema/p"
						<bean id="cdPlayer" class="com.soundsystem.CDPlayer" p:cd-ref="compactDisc"/>
					}
					
				}
				

			}
		}

		- 隐式的bean发现机制和自动装配{
			- 隐式的bean发现机制和自动装配
			- 组件扫描
				@Component
					Spring 应用上下文中所有的bean都会给定一个ID，
					如果@Component没有标注别名ID则为类名首字母小写
					如果@Component("lonelyHeartsClub")那么这个类的ID为 lonelyHeartsClub
					
					可以由java的@Named代替
					
				实例：
					@Component("lonelyHeartsClub")
					public class SgtPeppers implements CompactDisc {}

				@ComponentScan 属性
					value - @ComponentScan("soundsystem") 只能指定扫描一个包
					basePackages - @ComponentScan(basePackages = {"soundsystem","test"}) 可以指定扫描多个包
								这种方式重构代码，指定基础包可能就会出现错误
					basePackageClasses - @ComponentScan(basePackageClasses = {SgtPeppers.class})
				实例：
					@Configuration
					//@ComponentScan(basePackages = {"soundsystem","test"})
					@ComponentScan(basePackageClasses = {SgtPeppers.class})
					public class CDPlayerConfig {}
			- 自动装配
				Spring 初始化bean后，会尽可能的满足bean的依赖
				@Autowired
					@Autowired匹配依赖bean的时候，假如bean有且只有一个bean满足一栏需求，则bean会被装配到依赖的类中，
						但没有找到匹配的bean或找到多个，在spring上下文创建的时候，会抛异常。未避免异常，可以将
						@Autowired的required属性设为false
						@Autowired(required=false) 如果没有匹配的bean,spring上下文让这个bean处于未装配状态，bean使用时，会报空指针异常
						
						@Autowired 可以被java的@Inject代替
						
					@Autowired可以能够使用在构造器上，也可以使用在setter方法上
					构造器-实例：
						@Component
						public class CDPlayer implements MediaPlayer {
							private CompactDisc cd;
							@Autowired
							public CDPlayer(CompactDisc cd) {
								this.cd = cd;
							}
							@Override
							public void play() {
								cd.play();
							}
						}
					setter方法-实例:
						@Component
						public class CDPlayer implements MediaPlayer {
							private CompactDisc cd;
							@Override
							public void play() {
								cd.play();
							}
							public CompactDisc getCd() {
								return cd;
							}
							@Autowired
							public void setCd(CompactDisc cd) {
								this.cd = cd;
							}
						}
		}
		
		- 混合装配bean{
			- javaConfig 引入XML配置{
				javaConfig中分离bean的配置{
					@Import将多个Config进行配置，解耦和
					@Import({CDPlayerConfig.class,CDConfig.class})
					@ImportResource 将XML配置bean注入到配置类中
					@ImportResource("classpath:MyXml.xml")
					实例:
					@Import({CDPlayerConfig.class,CDConfig.class})
					@ImportResource("classpath:MyXml.xml")
					public class SoundSystemConfig {
					}
										
					public class CDPlayerConfig {
						@Bean
						public CDPlayer cdPlayer(CompactDisc compactDisc){
							return new CDPlayer(compactDisc);
						}
					}
					
					public class CDConfig {
						@Bean
						public CompactDisc compactDisc(){
							return new SgtPeppers();
						}
					}
				}
			}
			- XML引入java配置类或引入另外的XML文件{
				<import>元素拆分XML，将多个XML自由配置引入
				<import resoune="MyXml.xml"/>
	
				将javaConfig配置类使用<bean>元素引入
				<bean class="com.soundsystem.SoundSystemConfig"/>
			}
		}
				

