- 1.Aop术语{
	通知：通知被定义切面是什么以及何时使用
	spring切面5种通知：
		前置通知 - @before：在目标方法被调用前调用通知功能 
		后置通知 - @after：在目标方法被调用完成后调用通知，不关心方法的返回值 
		返回通知 - @AfterRunning：在目标方法成功执行之后调用通知
		异常通知 - @AfterThrowing：在目标方法抛出异常后调用通知
		环绕通知 - @Around：通知包裹了被通知的方法，在被通知的方法调用之前和之后执行自定义的行为
	连接点：在应用执行过点程中能够插入切面的点，即java bean(被通知类)中的所有方法即可称之为连接点
	切点：有助于缩小切面所通知的连接点的范围，定义了何处使用切面，切点的定义会匹配通知所要织入的一个点或多个点，
		通常使用明确的类名或方法名，也可利用正则表达式定义所匹配的类和方法来指定切点。即切点来筛选连接点。
	切面：通知+切点
	引入：允许我们向现有类中添加新的方法和属性
	织入：把切面应用到目标对象并创建新的代理对象的过程。切面在指定的连接点被织入到目标对象中。
		在目标对象的生命周期中有多个点可以织入：
			编译期，类加载期（切面在目标类加载到jvm时被织入），运行期
	Spring AOP构建在动态代理基础之上，因此，Spring对AOP的支持局限于方法拦截
	Spring在运行时通知对象，spring在运行期把切面织入到spring管理的bean中，代理类封装了目标类，并拦截被通知方法的调用
	
}

- 2.通过切点来选择连接点{
	spring仅支持AspectJ切点指示器的一个子集
	AspectJ指示器		描述
	arg()				限制连接点匹配参数为指定类型的执行方法
	@args()				限制连接点匹配参数由指定注解标注的执行方法
	execution()			用于匹配是连接点的执行方法
	this()				限制连接点匹配AOP代理的bean引用为指定类型的类
	target				限制连接点匹配目标对象为指定类型的类
	@target()			限制连接点匹配特定的执行对象，这些对象对应的类要具有指定类型的注解
	within()			限制连接点匹配指定的类型
	@within()			限制连接点匹配指定注解所标注的类型(当使用Spring AOP时，方法定义在由指定的注解所标注的类里)
	@annotation			限定匹配带有指定注解的连接点
	
	
	execution(<修饰符模式>?<返回类型模式><方法名模式>(<参数模式>)<异常模式>?)
	execution(* com.concert.*.perform(..))&& within(com.concert.*)
	* 不关心返回值类型
	com.concert.*.perform 指定包中的所有类中的perform方法
	(..)使用任意参数
	within(com.concert.*)当com.concert包下任意类的方法被调用时
	切点必须匹配所有的指示器才能正常执行
	&& 是把两个指示器连接在一起形成与(and)的关系
	||(or) !(not) 在XML中&有特殊的含义所以在XML中&&用 and来代替
	
	除指示器外spring引入了新的bean()指示器
	execution(* com.concert.*.perform(..)) and bean('woodstock')
	执行perform()方法时进行通知，但限定bean的id为'woodstock'

}

- 3.使用注解创建切面{
	- 定义切面
		@Aspect//使用@Aspect注解表明该类为一个切面
		@Component
		public class Audience {
			
			/*
			 * execution(<修饰符模式>?<返回类型模式><方法名模式>(<参数模式>)<异常模式>?)
			 * 除了返回类型模式、方法名模式和参数模式外，其它项都是可选的
			 */
			//定义命名的切点可重用
			@Pointcut("execution(* com.concert.*.perform(..))")
			public void perform(){}
			
			@Around("perform()")//环绕通知方法 被通知的方法调用之前和之后执行自定义的行为
			public void watchPerformance(ProceedingJoinPoint jp){
				try {
					System.out.println("Silence cell phones");
					System.out.println("Taking seats");
					jp.proceed();//一定要调用该方法，否则会阻塞对被通知方法的调用
					System.out.println("CLAP CLAP CLAP!!!");
				} catch (Throwable e) {
					System.out.println("Demanding a refund");
				}
			}
			
			
		//	@Before("execution(** com.concert.Performance.perform(..))")
		//	@Before("perform()")//目标方法被调用前调用
			public void silenceCellPhones(){//表演前
				System.out.println("Silence cell phones");
			}
			
		//	@Before("execution(** com.concert.Performance.perform(..))")
		//	@Before("perform()")
			public void takeSeat(){//表演前
				System.out.println("Taking seats");
			}
			
		//	@AfterReturning("execution(** com.concert.Performance.perform(..))")
		//	@AfterReturning("perform()")//方法成功执行后调用通知
			public void applause(){//表演后
				System.out.println("CLAP CLAP CLAP!!!");
			}
			
		//	@AfterThrowing("execution(** com.concert.Performance.perform(..))")
		//	@AfterThrowing("perform()")//目标方法抛出异常后调用
			public void demandRefund(){//表演失败之后
				System.out.println("Demanding a refund");
			}
		}

		指定@Aspect，spring并不能被视其为一个切面，需在javaConfig中启用自动代理功能@EnableAspectJAutoProxy
		
		@Configuration
		@ComponentScan
		@EnableAspectJAutoProxy //启用AspectJ自动代理
		public class ConcertConfig {}
		
		XML中启用自动代理功能
		<aop:aspectj-autoproxy/> <!-- 启动Aspectj自动代理 -->
		<bean id ="audience" class="com.concert.Audience"/> <!-- 声明 Audience bean-->
		
		不管哪种方式启用代理，AspectJ自动代理都会为使用@Aspect注解的bean创建一个代理，这个代理会围绕着所有该切面的切点所匹配的bean
		
	- 处理通知中的参数
		@Aspect
		public class TrackCounter {
			private Map<Integer, Integer> trackCounts = new HashMap<Integer, Integer>();
			/*
			 * args(trackNumber)中的参数名称必须与定义切面方法的参数名称相同即
			 * args(trackNumber) = public void trackPlayed(int trackNumber) = countTrack(int trackNumber)
			 * * 返回任意类型
			 * com.soundsystem.CompactDisc 方法所属的类型
			 * playTrack 方法
			 * int 接收int类型的参数
			 * args(trackNumber)指定参数
			 * args(trackNumber) 表明传递给playTrack()方法的int类型参数也会传到通知中
			 */

			@Pointcut("execution(* com.soundsystem.CompactDisc.playTrack(int))&&args(trackNumber)")
			public void trackPlayed(int trackNumber){}
			
			@Before("trackPlayed(trackNumber)")
			public void countTrack(int trackNumber){
				int currentCount = getPlayCount(trackNumber);
				trackCounts.put(trackNumber, currentCount+1);
			}

			public int getPlayCount(int trackNumber) {
				return trackCounts.containsKey(trackNumber)?trackCounts.get(trackNumber):0;
			}
			
		}
		
	- 配置类
		@Configuration
		//@ComponentScan
		@EnableAspectJAutoProxy
		public class TrackCounterConfig {

			@Bean
			public CompactDisc sgtPeppers(){
				
				BlankDisc disc = new BlankDisc();
				disc.setTitle("JoyChu");
				disc.setArtist("fantec");
				
				List<String> tracks = new ArrayList<String>();
				tracks.add("dong feng po");
				tracks.add("qi li xiang");
				tracks.add("long juan feng");
				tracks.add("dao xiang");
				tracks.add("ye qu");
				tracks.add("ye de di qi zhang");
				disc.setTracks(tracks);
				return disc;
			}
			
			@Bean
			public TrackCounter trackCounter(){
				return new TrackCounter();
			}
			
		}
	- 测试类
		@RunWith(SpringJUnit4ClassRunner.class)
		@ContextConfiguration(classes=TrackCounterConfig.class)
		public class TrackCounterTest {

		//	public final StandardOutputStreamLog log =  new StandardOutputStreamLog();
			
			@Autowired
			private CompactDisc disc;
			
			@Autowired
			private TrackCounter counter;
			
			@Test
			public void testTrackCounter(){
				disc.playTrack(1);
				disc.playTrack(2);
				disc.playTrack(3);
				disc.playTrack(3);
				disc.playTrack(4);
				disc.playTrack(4);
				disc.playTrack(4);
				
				System.out.println("1-----1="+counter.getPlayCount(1));
				System.out.println("2-----1="+counter.getPlayCount(2));
				System.out.println("3-----2="+counter.getPlayCount(3));
				System.out.println("4-----3="+counter.getPlayCount(4));
				System.out.println("5-----0="+counter.getPlayCount(5));
				System.out.println("6-----0="+counter.getPlayCount(6));
			}
		}
}

- 4.通过注解引入新的功能{
	当引入接口的方法被调用时，代理会把此调用委托给实现了新接口的某个其他对象
	使用注解@DeclareParents
	注解有三分部分组成：
		value属性：指定那种类型的bean要引入该接口。
		defaultImpl属性：指定为引入功能提供的实现类
		@DeclareParents：注解所标注的静态属性指明了要引入的新的接口类型
	
	新的功能
		public interface Encoreable {
			void performEncore();
		}
	新功能实现
		@Component
		public class DefaulttEncoreable implements Encoreable {
			@Override
			public void performEncore() {
				System.out.println("Encoreable - performEncore");
			}
		}
	引入新的功能
		为Performance的子类引入新接口的功能
		@Aspect
		@Component
		public class EncoreableIntroducer {
			
			/**
			 * @DeclareParents 将Encoreable接口引入到Performance bean中
			 * @DeclareParents 由三部分组成
			 * 		value：指定了哪种类型的bean要引入该接口， “+”表示该类型bean的所有子类，而不是该类型本身
			 * 				即所有实现了Performance的类
			 * 		defaultImpl：属性指定了为引入功能提供的实现类
			 * 		@DeclareParents：注解所标注的静态属性指明了要引入的新的接口
			 * 
			 */
			@DeclareParents(value="com.concert.Performance+",defaultImpl=DefaulttEncoreable.class)
			public static Encoreable encoreable;
		}
	配置类
		@Configuration
		@ComponentScan
		@EnableAspectJAutoProxy //启用AspectJ自动代理
		public class ConcertConfig {}
	
	测试类
		@RunWith(SpringJUnit4ClassRunner.class)
		@ContextConfiguration(classes = ConcertConfig.class)
		public class AopTest {
			@Autowired
			private Performance performance;
			@Test
			public void joyPerformanceTest(){
				//同一个bean，可以转化为不同的两个bean，具有两个bean的方法
				performance.perform();//原有功能
				Encoreable encoreable = (Encoreable)performance; //
				encoreable.performEncore();//新功能
			}
		}
}

- 5.XML中声明切面{
	AOP配置元素					用途
	<aop:advisor>				定义aop通知器
	<aop:after>					定义aop后置通知
	<aop:after-returning>		返回通知
	<aop:after-throwing>		异常通知
	<aop:around>				环绕通知
	<aop:aspect>				定义一个切面
	<aop:aspectj-autoproxy>		启动@AspectJ注解驱动的切面
	<aop:before>				前置通知
	<aop:config>				顶层aop配置元素，大对数的<aop:*>元素必须包含在<aop:config>元素内
	<aop:declare-parents>		以透明的方式为被通知对象引入新的接口
	<aop:pointcut>				定义一个切点
	
	XML配置
		<aop:aspectj-autoproxy/> <!-- 启动Aspectj自动代理 -->
		<bean id ="audience" class="com.concert.Audience"/> <!-- 声明 Audience bean-->
		<bean id ="trackCounter" class="com.soundsystem.TrackCounter"/> <!-- 声明 Audience bean-->
		
		<bean id="encoreableDelegate" class="com.concert.DefaulttEncoreable"/>
		
		<aop:config>
			<!-- 定义切面-->
			<aop:aspect ref="audience">
				<!-- 定义切点-->
				<aop:pointcut id="performancecut"
					expression="execution(* com.concert.Performance.perform(..))"/>
			<!--before也可定义pointcut属性  pointcut="execution(* com.concert.Performance.perform(..))" -->
				<aop:before 
					pointcut-ref="performancecut"
					method="silenceCellPhones"
				/>
				<aop:after-returning
					pointcut-ref="performancecut"
					method="applause"
				/>
				<aop:after-throwing
					pointcut-ref="performancecut"
					method="demandRefund"
				/>
				<aop:around
					pointcut-ref="performancecut"
					method="watchPerformance"
				/>
			</aop:aspect>
			<!-- 为通知传递参数 -->
			<aop:aspect ref="trackCounter">
				<aop:pointcut id="trackPlayed" 
					expression="execution(* com.soundsystem.CompactDisc.playTrack(int)) and args(track))"/>
					
				<aop:before 
					pointcut-ref="trackPlayed"
					method="countTrack"
				/>
			</aop:aspect>
			
			<!-- 通过切面引入新的功能    -->
			
			<!-- 	default-impl="com.concert.DefaulttEncoreable" 直接标识委托
					delegate-fef="encoreableDelegate" 间接使用委托 
					功能相同 区别在与delegate-fef使用的是spring bean 该可以被注入、通知等
					 -->
				<!-- 	delegate-fef="encoreableDelegate" -->
			<aop:aspect>
				<aop:declare-parents 
					types-matching="com.concert.Performance+"
					implement-interface="com.concert.Encoreable"
					default-impl="com.concert.DefaulttEncoreable"
				/>
			</aop:aspect>
		</aop:config>
		
}



























