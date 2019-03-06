@Controller/@EnableWebMvc/@RequestMapping/@RequestParam/@RequestBody/@ResponseBody/@PathVariable/SpringMVC中提供了java校验的API/@Valid
@ResponseStatus/@ControllerAdvice/@ExceptionHandler

- 1.spring MVC介绍{
	1.1 springMVC 请求流程
		1.页面请求发送至前端控制器DispatcherServlet
		2.DispatcherServlet查询一个或多个处理器映射器handller Mapping,找到相应的处理器；即控制器中的方法映射名
		3.DispatcherServlet将请求发送至控制器
		4.控制器接收请求并处理请求，将model模型及逻辑视图名返回给DispatcherServlet
		5.DispatcherServlet根据逻辑视图名发送给视图解析器view resolver ,view resolver返回真正的视图信息
		6.DispatcherServlet将model模型数据发送给视图渲染
		7.视图将使用模型数据渲染输出，输出会通过响应对象传递给客户端
	1.2 搭建SpringMVC
		1.配置DispatcherServlet
			DispatcherServlet是SpringMVC的核心。传统的方式DispatcherServlet会配置在web.xml文件中。
			也可使用java将DispatcherServlet配置在servlet容器中。
			实例：
			/*
			 * AbstractAnnotationConfigDispatcherServletInitializer 剖析
			 * 扩展AbstractAnnotationConfigDispatcherServletInitializer的任意类都会自动配置DispatcherServlet
			 * 和spring应用上下文，spring的应用上下文会位于应用程序的servlet上下文中。
			 * 
			 * 
			 * 在servlet3.0环境中，容器会在类路径下查找实现了javax.servlet.ServletContainerInitializer接口的类A，如果有则有实现类来配置servlet容器
			 * spring提供了SpringServletContainerInitializer实现了javax.servlet.ServletContainerInitializer，这个类返过来会查找实现了
			 * WebApplicationInitializer的类B，并将配置任务交由B来完成.spring 3.2引入了便利的WebApplicationInitializer的基础实现即
			 * AbstractAnnotationConfigDispatcherServletInitializer
			 * 
			 * 
			 */
			public class SpittrWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

				/**
				 * getRootConfigClasses()方法
				 * 返回带有@configuration注解的类将会用来配置ContextLoaderListener创建的应用上下文的bean
				 * 根配置定义在RootConfig中
				 */
				@Override
				protected Class<?>[] getRootConfigClasses() {
					return new Class<?>[] {RootConfig.class};
				}

				/**
				 * getServletConfigClasses()方法
				 * 返回带有@configuration注解的类将会用来定义DispatcherServlet应用上下文中的bean
				 * DispatcherServlet配置声明在WebConfig中
				 * DispatcherServlet 启动时会创建Spring应用上下文并加载配置文件或配置类中所声明的bean
				 */
				@Override
				protected Class<?>[] getServletConfigClasses() { 
					
					return new Class<?>[] {WebConfig.class};
				}

				@Override
				protected String[] getServletMappings() {//将DispatcherServlet映射到“/”
					return new String[] {"/"};
				}

			}
		2.启动SpringMVC	
			有多种方式配置DispatcherServlet，启用springMVC组件、xml中使用<mvc:annotation-driver/> 启用注解驱动
			创建springmvc配置可用带有@EnableWebMvc注解的类
			
			@Configuration
			@EnableWebMvc//启用 SpringMVC
			@ComponentScan("spitter.web") //启用组件扫描
			public class WebConfig extends WebMvcConfigurerAdapter{
				
				/**
				 * 配置JSP视图解析器
				 * 没有配置视图解析器的话spring默认会使用BeanNameViewResolver来查找ID与视图名称匹配的bean
				 * 
				 */
				public ViewResolver viewResolver(){
					InternalResourceViewResolver resolver = new InternalResourceViewResolver();
					resolver.setPrefix("/WEB-INF/views/");
					resolver.setSuffix(".jsp");
					resolver.setExposeContextBeansAsAttributes(true);
					return resolver;
				}
				/**
				 * 配置静态资源的处理
				 * 通过调用configurer.enable();要求DispatcherServlet将对静态资源的请求转发到servlet容器中默认的servlet上而不是DispatcherServlet本身来处理此类请求
				 */
				public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer){
					configurer.enable();
				}
	
			}
			
			@Configuration？？？？？？？？？？？？？？？？？？？
			@ComponentScan(basePackages={"spitter"},excludeFilters={@Filter(type=FilterType.ANNOTATION,value=EnableWebMvc.class)})
			public class RootConfig {

			}
			
}

- 2.控制器{
	
	1.简单控制器
		在SpringMVC中，控制器只是方法上添加了@RequestMapping注解的类，这个注解声明了方法要处理的请求
		/*
		 * @Controller是基于@Component的注解
		 */
		@Controller //声明一个控制器
		//同时接受"/","/homepage"的请求
		@RequestMapping({"/","/homepage"}) 
		public class HomeController {
			@RequestMapping(method=RequestMethod.GET)//请求路径和请求方式
			public String home(){
				System.out.println("home");
				return "home";//逻辑视图名称
			}
		}
		
	2.传递模型数据到视图
		数据访问接口
			public interface SpittleRepository {
				List<Spittle>findSpittles(long max,int count);
				Spittle findOne(long id);
				Spitter save(Spitter spitter);
				Spitter findByUserName(String userName);
			}
		
		模型
			模型1 
			public class Spittle {
			private Long id;
			private String message;
			private Date time;
			private Double latitude;
			private Double longitude;
			
			public Spittle(String message, Date time) {
				this(message, time, null, null);
			}

			public Spittle(String message, Date time, Double latitude, Double longitude) {
				super();
				this.id = null;
				this.message = message;
				this.time = time;
				this.latitude = latitude;
				this.longitude = longitude;
			}

			public Long getId() {
				return id;
			}

			public void setId(Long id) {
				this.id = id;
			}

			public String getMessage() {
				return message;
			}

			public void setMessage(String message) {
				this.message = message;
			}

			public Date getTime() {
				return time;
			}

			public void setTime(Date time) {
				this.time = time;
			}

			public Double getLatitude() {
				return latitude;
			}

			public void setLatitude(Double latitude) {
				this.latitude = latitude;
			}

			public Double getLongitude() {
				return longitude;
			}

			public void setLongitude(Double longitude) {
				this.longitude = longitude;
			}

			@Override
			public boolean equals(Object obj) {
				return EqualsBuilder.reflectionEquals(this, obj, "id","time");
			}

			@Override
			public int hashCode() {
				return HashCodeBuilder.reflectionHashCode(this,  "id","time");
			}
		}

		控制器
		@Controller
		public class SpittleController {
			
			private SpittleRepository spittleRepository;
			
			static final String LONG_MAX_VALUE_STRING = Long.MAX_VALUE+"";
			
			@Autowired
			public SpittleController(SpittleRepository spittleRepository){
				this.spittleRepository = spittleRepository;
			}
			
			/**
			 * Model 实际上是Map，model会传递给视图
			 */
			@RequestMapping(value="/spittles",method=RequestMethod.GET)
			public String spittles(Model model){
				//将spittle添加到模型中
				/*
					model.addAttribute 不指定key时，key会根据值的对象类型推断确定
				*/
				model.addAttribute(spittleRepository.findSpittles(Long.MAX_VALUE, 20));
				System.out.println("spittles");
				//返回视图名
				return "spittles";
			}
			
			/*
				没有返回视图名而是返回的对象实体
				默认将这个对象放入到模型中，模型的key会根据值的对象类型推断确定
				而逻辑视图名将灰根据请求路径推断出 /spittlesGet 视图名为spittlesGet
			*/
			@RequestMapping(value="/spittlesGet",method=RequestMethod.GET)
			public List<Spittle> spittlesGet(@RequestParam("max") long max,@RequestParam("count") int count){
				return spittleRepository.findSpittles(max, count);
			}
		}
		
		测试
		public class HomeControllerTest {
		//	@Test
			public void testHomePage()throws Exception{
				
				  /**
				 * 1、mockMvc.perform执行一个请求；
					2、MockMvcRequestBuilders.get("/user/1")构造一个请求
					3、ResultActions.andExpect添加执行完成后的断言
					4、ResultActions.andDo添加一个结果处理器，表示要对结果做点什么事情，比如此处使用MockMvcResultHandlers.print()输出整个响应结果信息。
					5、ResultActions.andReturn表示执行完成后返回相应的结果。
				 */
				HomeController homeController = new HomeController();
				MockMvc mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
				//搭建MockMvc 
				//对“/”执行get请求
				mockMvc.perform(MockMvcRequestBuilders.get("/"))
				//预期得到home视图
				.andExpect(MockMvcResultMatchers.view().name("home"));
			}
			
			/**
			 * 方法描述：传递模型数据到视图中
			 * @throws Exception
			 * 创建人:wei
			 * 创建时间:2018年8月10日 下午5:49:23
			 */
		//	@Test
			public void shouldShowRecentSpittles() throws Exception{
				List<Spittle> expectedSpittles = createSpittleList(20);
				/**
					mock(Class classToMock);
					mock(Class classToMock, String name)
					mock(Class classToMock, Answer defaultAnswer)
					mock(Class classToMock, MockSettings mockSettings)
					mock(Class classToMock, ReturnValues returnValues)
				 */
				//模拟对象 mockRepository 
				SpittleRepository mockRepository = mock(SpittleRepository.class);
				
				/**
				 * Mock对象的期望行为和返回值设定
				 * when(mock.someMethod()).thenReturn(value) 来设定 Mock 对象某个方法调用时的返回值
				 * when(mock.someMethod()).thenThrow(new RuntimeException) 的方式来设定当调用某个方法时抛出的异常。
				 * 
				 */
				when(mockRepository.findSpittles(Long.MAX_VALUE, 20))
						.thenReturn(expectedSpittles);
				
				SpittleController spittleController = new SpittleController(mockRepository);

				/**
				 * MockMvcBuilders.standaloneSetup(Object... controllers)：通过参数指定一组控制器
				 */
				MockMvc mockMvc = MockMvcBuilders.standaloneSetup(spittleController)
						.setSingleView(new InternalResourceView("/WEB-INF/views/spittles.jsp")).build();
				
				/**
				 *  perform：执行一个RequestBuilder请求，会自动执行SpringMVC的流程并映射到相应的控制器执行处理；
				 *  andExpect：添加ResultMatcher验证规则，验证控制器执行完成后结果是否正确；
				 * 	andDo：添加ResultHandler结果处理器，比如调试时打印结果到控制台；
				 *  andReturn：最后返回相应的MvcResult；然后进行自定义验证/进行下一步的异步处理；
				 */
				//对“/spittles” 发起get请求
				mockMvc.perform(MockMvcRequestBuilders.get("/spittles"))
					.andExpect(MockMvcResultMatchers.view().name("spittles"))
					.andExpect(MockMvcResultMatchers.model().attributeExists("spittleList"))
					.andExpect(MockMvcResultMatchers.model().attribute("spittleList",CoreMatchers.hasItems(expectedSpittles.toArray())))
					.andDo(MockMvcResultHandlers.print());
			}

			private List<Spittle> createSpittleList(int count) {
				List<Spittle> spittles = new ArrayList<Spittle>();
				for (int i = 0; i < count; i++) {
					spittles.add(new Spittle("Spittle" + i, new Date()));
				}
				return spittles;
			}
		}
}

- 3.接收请求参数{
	
	3.1查询参数
		@RequestParam接收查询的参数
		控制器
			@RequestMapping(value="/spittlesGet",method=RequestMethod.GET)
			public List<Spittle> spittlesGet(@RequestParam("max") long max,@RequestParam("count") int count){
			//	model.addAttribute(spittleRepository.findSpittles(Long.MAX_VALUE, 20));
			//	System.out.println("spittles");
				return spittleRepository.findSpittles(max, count);
			}
			
			//请求参数不存在则使用默认值@RequestParam defaultValue 属性
			@RequestMapping(value="/spittlesGetDefault",method=RequestMethod.GET)
			public List<Spittle> spittlesGetDefault(@RequestParam(value="max",defaultValue=LONG_MAX_VALUE_STRING) long max,@RequestParam(value="count",defaultValue="20") int count){
				System.out.println("max = "+max);
				System.out.println("count = "+count);
				return spittleRepository.findSpittles(max, count);
			}
			
		测试
		/**
		 * 方法描述：处理参数请求
		 * @throws Exception
		 * 创建人:wei
		 * 创建时间:2018年8月10日 下午5:48:59
		 */
		 //引入Mockito类
		import static org.mockito.Mockito.*;
		import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
		@Test
		public void shouldShowRecentSpittlesGet() throws Exception{
			
			//创建List<Spittle>列表
			List<Spittle> expectedSpittles = createSpittleList(20);
			/**
				mock(Class classToMock);
				mock(Class classToMock, String name)
				mock(Class classToMock, Answer defaultAnswer)
				mock(Class classToMock, MockSettings mockSettings)
				mock(Class classToMock, ReturnValues returnValues)
			 */
			//模拟对象 mockRepository 
			SpittleRepository mockRepository = mock(SpittleRepository.class);
			
			/**
			 * Mock对象的期望行为和返回值设定
			 * when(mock.someMethod()).thenReturn(value) 来设定 Mock 对象某个方法调用时的返回值
			 * when(mock.someMethod()).thenThrow(new RuntimeException) 的方式来设定当调用某个方法时抛出的异常。
			 * 当mockRepository调用findSpittles方法传入并且传入参数为findSpittles(Long.MAX_VALUE, 20)时,返回
			 * 创建List<Spittle>列表 expectedSpittles
			 */		
			when(mockRepository.findSpittles(Long.MAX_VALUE, 20))
					.thenReturn(expectedSpittles);
			
			//将mockRepository的参数注入到SpittleController中
			SpittleController spittleController = new SpittleController(mockRepository);

			/**
			 * MockMvcBuilders.standaloneSetup(Object... controllers)：通过参数指定一组控制器,模拟一个Mvc测试环境
			 * 通过build得到一个MockMvc
			 */
			MockMvc mockMvc = MockMvcBuilders.standaloneSetup(spittleController)
					.setSingleView(new InternalResourceView("/WEB-INF/views/spittles.jsp")).build();
			
			/**
			 *  调用MockMvc.perform(RequestBuilder requestBuilder)后将得到ResultActions
			 *  perform：执行一个RequestBuilder请求，会自动执行SpringMVC的流程并映射到相应的控制器执行处理；
			 * 	
			 * 	ResultActions:
			 *  andExpect：添加ResultMatcher验证规则，验证控制器执行完成后结果是否正确；
			 * 	andDo：添加ResultHandler结果处理器，比如调试时打印结果到控制台；
			 *  andReturn：最后返回相应的MvcResult；然后进行自定义验证/进行下一步的异步处理；
			 *  MockMvcRequestBuilders.get("/spittlesGet?max=238900&count=50")构造一个请求
			 *  ResultActions.andExpect添加执行完成后的断言
			 */
			//对“/spittles” 发起get请求
			mockMvc.perform(MockMvcRequestBuilders.get("/spittlesGet?max="+Long.MAX_VALUE+"&count=20"))
				.andExpect(MockMvcResultMatchers.view().name("spittlesGet"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("spittleList"))
				.andExpect(MockMvcResultMatchers.model().attribute("spittleList",CoreMatchers.hasItems(expectedSpittles.toArray())))
				.andDo(MockMvcResultHandlers.print());
			
			/**
			 * 测试@RequestParam(value="max",defaultValue=Long.MAX_VALUE+"")
			 * defaultValue 属性
			 */
			mockMvc.perform(MockMvcRequestBuilders.get("/spittlesGetDefault?max=&count="))
				.andExpect(MockMvcResultMatchers.view().name("spittlesGetDefault"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("spittleList"))
				.andExpect(MockMvcResultMatchers.model().attribute("spittleList",CoreMatchers.hasItems(expectedSpittles.toArray())))
				
		}
	3.2通过路径参数接收输入
		
		控制器
			/*
			请求路径中使用{}占位符，接收参数使用@PathVariable注解，
			如果@PathVariable没有value属性 占位符与方法参数名称相同
			*/
			@RequestMapping(value="/spittlesFindOne/{id}",method=RequestMethod.GET)
			public Spittle spittlesFindOne(@PathVariable("id") long id,Model model){
				return spittleRepository.findOne(id);
			}
			
		测试
		//	@Test
		public void testSpittle() throws Exception{
			Spittle expectdSpittle = new Spittle("hello", new Date());
			SpittleRepository mockRepository = mock(SpittleRepository.class);
			
			when(mockRepository.findOne(12345)).thenReturn(expectdSpittle);
			
			SpittleController spittleController = new SpittleController(mockRepository);
			
			MockMvc mockMvc = MockMvcBuilders.standaloneSetup(spittleController).build();
			
			mockMvc.perform(MockMvcRequestBuilders.get("/spittlesFindOne/12345"))
	//			.andExpect(MockMvcResultMatchers.view().name("spittlesFindOne"))
				.andExpect(MockMvcResultMatchers.model().attributeExists("spittle"))
				.andExpect(MockMvcResultMatchers.model().attribute("spittle", expectdSpittle))
				.andDo(MockMvcResultHandlers.print());
		}
}

- 4.处理表单{
	4.1编写表单
		控制器
		@Controller
		@RequestMapping("/spitter")
		public class SpitterController {

			private SpittleRepository spittleRepository;
			
			@Autowired
			public SpitterController(SpittleRepository spittleRepository){
				this.spittleRepository = spittleRepository;
			}
			
			@RequestMapping(value="/register",method=RequestMethod.GET)
			public String showRegisterForm(){
				return "registerForm";
			}
			
			/*
			 * 表单提交
			 * 
			 * java.lang.AssertionError: Redirected URL expected:</spitter/jbauer> but was:</spitter/null>
			 * 在processRegistration的参数时一个对象，当传入参数的是后，
			 * 是要默认调用参数的setter方法的，
			 * 所以，要在Spitter类中添加对应的setter方法。
			 */
			@RequestMapping(value="/register", method=RequestMethod.POST)
			public String processRegistration(Spitter spitter) {
				spittleRepository.save(spitter);
				return "redirect:/spitter/" + spitter.getUserName();
			}
		}
		
		测试类
		@Test
		public void shouldProcessRegisteration() throws Exception{
			SpittleRepository mockRepository = mock(SpittleRepository.class);
			
			Spitter unsave = new Spitter("jbauer", "24hours", "Jack", "Bauer");
			Spitter save = new Spitter(24L,"jbauer","24hours", "Jack", "Bauer");
			
			when(mockRepository.save(unsave)).thenReturn(save);
			
			/*
			 * 创建MVC对象时 一定要注意，是否是要调用接口地址的MVC类，SpittlerController
			 */
			SpitterController spittleController = new SpitterController(mockRepository);
			MockMvc mockMvc = MockMvcBuilders.standaloneSetup(spittleController).build();

			mockMvc.perform(MockMvcRequestBuilders.post("/spitter/register")  //Perform request
			.param("firstName", "Jack")
			.param("lastName", "Bauer")
			.param("userName", "jbauer")
			.param("password", "24hours"))
			.andExpect(MockMvcResultMatchers.redirectedUrl("/spitter/jbauer"))
			.andDo(MockMvcResultHandlers.print());
			
			/**
			 * 校验保存情况 此时比较两个对象默认是比较的对象的地址。 需要重写其比较的方法
			 * Argument(s) are different! 异常
			 * import org.apache.commons.lang3.builder.EqualsBuilder; import
			 * org.apache.commons.lang3.builder.HashCodeBuilder;
			 * 
			 * @Override 
			 * public boolean equals(Object that) { return
			 *           EqualsBuilder.reflectionEquals(this, that, "firstName",
			 *           "lastName", "username", "password", "email"); }
			 * 
			 * @Override 
			 * public int hashCode() { return
			 *           HashCodeBuilder.reflectionHashCode(this, "firstName",
			 *           "lastName", "username", "password", "email"); }
			 * 
			 */
			verify(mockRepository, atLeastOnce()).save(unsave);
		}
	
	4.2校验表单
		SpringMVC中提供了java校验的API，类路径下实现了API即可，比如hibernate validator
		java校验API定义了多个注解，注解使用在属性上
		java校验API所提供的校验注解
		注解				描述
		@AssertFalse		元素必须是boolean类型，并且值为false
		@AssertTrue			元素必须是boolean类型，并且值为true
		@DecimalMax			元素必须为数字，并且值要小于等于给定的BigDecimalString的值
		@DecimalMin			元素必须为数字，并且值要大于等于给定的BigDecimalString的值
		@Digits				元素必须为数字，值必须是指定的位数
		@Future				元素的值必须是将来的日期
		@Max				元素必须为数字，并且值要小于等于给定的的值
		@Min				元素必须为数字，并且值要大于等于给定的的值
		@NotNull			元素的值不为空
		@Null				元素的值必须为空
		@Past				元素的值必须是已过去的日期
		@Pattern			元素的值必须匹配给定的正则表达式
		@Size				元素的值必须是String、集合或数组，并且长度要符合给定的范围
		
		模型
		public class Spitter {
			private Long id;
			
			@NotNull //所注解元素的值不能为null
			@Size(min=5,max=16)//所注解的元素的值必须是String类型/集合/数组,并且它的长度要符号给定的范围
			private String userName;
			
			@NotNull //所注解元素的值不能为null
			@Size(min=5,max=25)
			private String password;
			
			@NotNull //所注解元素的值不能为null
			@Size(min=5,max=30)
			private String firstName;
			
			@NotNull //所注解元素的值不能为null
			@Size(min=5,max=30)
			private String lastName;
			
			public Spitter() {
				super();
			}

			public Spitter(Long id, String userName, String password, String firstName, String lastName) {
				super();
				this.setId(id);
				this.userName = userName;
				this.password = password;
				this.firstName = firstName;
				this.lastName = lastName;
			}

			public Spitter(String userName, String password, String firstName, String lastName) {
				super();
				this.userName = userName;
				this.password = password;
				this.firstName = firstName;
				this.lastName = lastName;
			}

			public String getUserName() {
				return userName;
			}

			public void setUserName(String userName) {
				this.userName = userName;
			}

			public String getPassword() {
				return password;
			}

			public void setPassword(String password) {
				this.password = password;
			}

			public String getFirstName() {
				return firstName;
			}

			public void setFirstName(String firstName) {
				this.firstName = firstName;
			}

			public String getLastName() {
				return lastName;
			}

			public void setLastName(String lastName) {
				this.lastName = lastName;
			}

			public Long getId() {
				return id;
			}

			public void setId(Long id) {
				this.id = id;
			}
			
			 @Override
			 public boolean equals(Object that) {
			   return EqualsBuilder.reflectionEquals(this, that, "firstName", "lastName", "username", "password", "email");
			 }
			 
			 @Override
			 public int hashCode() {
			   return HashCodeBuilder.reflectionHashCode(this, "firstName", "lastName", "username", "password", "email");
			 }
		}
	控制器
		/*
			添加@Valid注解，告知Spring 需要确保这个对象满足校验限制 
			Errors参数要紧跟带有@Valid注解参数的后面
			首先调用errors.hasErrors()检查是否有错误
		*/
		@RequestMapping(value="/registerVali", method=RequestMethod.POST)
		public String processRegistrationVali(@Valid Spitter spitter,Errors errors) {
			if(errors.hasErrors()){
				return "registerForm";
			}
			spittleRepository.save(spitter);
			return "redirect:/spitter/" + spitter.getUserName();
		}
	
}

- 5.Mockito测试{
	模拟对象
	Mockito.mock(Class<SpittleRepository> classToMock)
			mock(Class classToMock);
			mock(Class classToMock, String name)
			mock(Class classToMock, Answer defaultAnswer)
			mock(Class classToMock, MockSettings mockSettings)
			mock(Class classToMock, ReturnValues returnValues)
	
		SpittleRepository mockRepository = mock(SpittleRepository.class);
	
	Mock对象的期望行为和返回值设定 Mockito.when
		when(mock.someMethod()).thenReturn(value) 来设定 Mock 对象某个方法调用时的返回值
		when(mock.someMethod()).thenThrow(new RuntimeException) 的方式来设定当调用某个方法时抛出的异常。
		
		当mockRepository调用findSpittles方法传入并且传入参数为findSpittles(Long.MAX_VALUE, 20)时,返回
		创建List<Spittle>列表 expectedSpittles
		when(mockRepository.findSpittles(Long.MAX_VALUE, 20))
				.thenReturn(expectedSpittles);
	
	MockMvcBuilders.standaloneSetup(Object... controllers)：通过参数指定一组控制器,模拟一个Mvc测试环境通过build得到一个MockMvc
		SpittleController spittleController = new SpittleController(mockRepository);
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(spittleController)
				.setSingleView(new InternalResourceView("/WEB-INF/views/spittles.jsp")).build();
				
	/**
		 *  调用MockMvc.perform(RequestBuilder requestBuilder)后将得到ResultActions
		 *  perform：执行一个RequestBuilder请求，会自动执行SpringMVC的流程并映射到相应的控制器执行处理；
		 * 	
		 * 	ResultActions:
		 *  andExpect：添加ResultMatcher验证规则，验证控制器执行完成后结果是否正确；
		 * 	andDo：添加ResultHandler结果处理器，比如调试时打印结果到控制台；
		 *  andReturn：最后返回相应的MvcResult；然后进行自定义验证/进行下一步的异步处理；
		 *  MockMvcRequestBuilders.get("/spittlesGet?max=238900&count=50")构造一个请求
		 *  ResultActions.andExpect添加执行完成后的断言
		 */
		//对“/spittles” 发起get请求
		mockMvc.perform(MockMvcRequestBuilders.get("/spittlesGet?max="+Long.MAX_VALUE+"&count=20"))
			.andExpect(MockMvcResultMatchers.view().name("spittlesGet"))
			.andExpect(MockMvcResultMatchers.model().attributeExists("spittleList"))
			.andExpect(MockMvcResultMatchers.model().attribute("spittleList",CoreMatchers.hasItems(expectedSpittles.toArray())))
			.andDo(MockMvcResultHandlers.print());
				
	
		
}