- 1.springMVC配置替代方案{
	1.1自定义DispatcherServlet配置
		/*
			AbstractAnnotationConfigDispatcherServletInitializer除了必须重载的三个
			abstract方法getRootConfigClasses()/getServletConfigClasses()/getServletMappings()
			之外还有很多方法可以重载，可以实现额外的配置;customizeRegistration()方法，在DispatcherServlet
			注册到servlet容器后,调用的方法。并将servlet注册后得到的Registration.Dynamic传递进来，可以重载
			customizeRegistration()方法来对DispatcherServlet进行额外的配置
		*/
	实例:
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
		 * SpittrWebAppInitializer只是实现了AbstractAnnotationConfigDispatcherServletInitializer的三个抽象方法
		 * AbstractAnnotationConfigDispatcherServletInitializer本身还带有别的方法
		 * 
		 * DispatcherServlet的上下文仅仅是Spring MVC的上下文, 而ContextLoaderListener的上下文则对整个Spring都有效.
		 * 一般Spring web项目中同时会使用这两种上下文.  
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
			 * DispatcherServlet 启动时会创建SpringMVC应用上下文并加载配置文件或配置类中所声明的bean
			 */
			@Override
			protected Class<?>[] getServletConfigClasses() { 
				
				return new Class<?>[] {WebConfig.class};
			}

			@Override
			protected String[] getServletMappings() {//将DispatcherServlet映射到“/”
				return new String[] {"/"};
			}

			/**
			 * AbstractAnnotationConfigDispatcherServletInitializer将DispatcherServlet
			 * 注册到servlet容器中之后，就会调用customizeRegistration方法，并将servlet注册后得到的
			 * Registration.Dynamic传递进来，通过重载customizeRegistration方法可以对DispatcherServlet
			 * 进行额外的配置
			 */
			@Override
			protected void customizeRegistration(Dynamic registration) {
				/**
				 * 使用DispatcherServlet的registration来启用Multipart请求
				 */
				registration.setMultipartConfig(new MultipartConfigElement("/tmp/spittr/uploads"));
				
				/**
				 * setLoadOnStartup设置load-on-startup优先级
				 */
				registration.setLoadOnStartup(1);
				
				/**
				 * 设置初始化参数
				 */
				registration.setInitParameter("", "");
			}

			/**
			 * 为注册Filter并将其映射到DispatcherServlet，可重载getServletFilters()方法
			 */
			@Override
			protected Filter[] getServletFilters() {
				return new Filter[]{new MyFilter()};
			}
		}
		
	1.2添加其他的servlet和Filter
		/**
		 * 实现spring的WebApplicationInitializer接口，注册其他的servlet和filter
		 */
		public class MyServletInitializer implements WebApplicationInitializer {
			@Override
			public void onStartup(ServletContext servletContext) throws ServletException {
				/**
				 * 注册servlet
				 */
				Dynamic addServlet = servletContext.addServlet("myServlet",MyServlet.class);
				//映射servlet
				addServlet.addMapping("/custom/**");
				
				//注册Filter
				javax.servlet.FilterRegistration.Dynamic addFilter = servletContext.addFilter("myFilter", (Class<? extends Filter>) MyFilter.class);
				//添加Filter的映射路径
				addFilter.addMappingForUrlPatterns(null, false, "/custom/**");
			}
		}
		public class MyServlet implements Servlet{
			@Override
			public void destroy() {
				// TODO Auto-generated method stub
			}
			@Override
			public ServletConfig getServletConfig() {
				// TODO Auto-generated method stub
				return null;
			}
			@Override
			public String getServletInfo() {
				// TODO Auto-generated method stub
				return null;
			}
			@Override
			public void init(ServletConfig arg0) throws ServletException {
				// TODO Auto-generated method stub
			}
			@Override
			public void service(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException {
				// TODO Auto-generated method stub
			}
		}
		public class MyFilter implements Filter{
			@Override
			public void destroy() {
				// TODO Auto-generated method stub
			}
			@Override
			public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
					throws IOException, ServletException {
				// TODO Auto-generated method stub
			}
			@Override
			public void init(FilterConfig arg0) throws ServletException {
				// TODO Auto-generated method stub
			}

		}
	1.3在web.xml中声明DispatcherServlet
		<?xml version="1.0" encoding="UTF-8"?>
		<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xmlns="http://java.sun.com/xml/ns/javaee"
			xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
			id="WebApp_ID" version="2.5">
			<display-name>blueTest</display-name>
			<!-- 第一种配置方式 根据配置文件配置-->
			<!-- 设置根上下文配置文件位置 -->
			<context-param>
				<param-name>contextConfigLocation</param-name>
				<param-value>/WEB-INF/spring/root-context.xml</param-value>
			</context-param> 
			
			<!-- 	注册ContextLoaderListener -->
			<listener>
				<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
			</listener> 
			
			<!-- 注册 DispatcherServlet
			
				 DispatcherServlet会根据 servlet的名字找到一个文件,即appServlet,因此DispatcherServlet
				会从/WEB-INF/spring/appServlet/servlet-context.xml文件中加载应用上下文
				
				如果想指定DispatcherServlet配置文件的位置，可以在servlet中指定一个contextConfigLocation
				初始化参数，在servlet使用<init-param>标签 -->
			
			<servlet>
				<servlet-name>appServlet</servlet-name>
				<servlet-class>
					org.springframework.web.servlet.DispatcherServlet
				</servlet-class>
				
				<init-param>
					<param-name>contextConfigLocation</param-name>
					<param-value>/WEB-INF/spring/appServlet/servlet-context.xml</param-value>
				</init-param>
				<load-on-startup>1</load-on-startup>
			</servlet> 
			
			<!-- 将DispatcherServlet映射到"/" -->
			<servlet-mapping>
				<servlet-name>appServlet</servlet-name>
				<url-pattern>/</url-pattern>
			</servlet-mapping>
			
			<!-- 第二种配置方式  springMVC 使用基于java的spring配置-->
			
			<!-- 使用java配置 此处不需要配置,servlet中配置即可-->
			<!--<context-param>
				<param-name>contextClass</param-name>
				<param-value>org.springframework.web.context.support.AnnotationConfigWebApplicationContext</param-value>
			</context-param> -->
			
			<!-- 配置根配置类 此处不需要配置,servlet中配置即可-->
		<!--  	<context-param>
				<param-name>contextConfigLocation</param-name>
				<param-value>com.spittr.config.RootConfig</param-value>
			</context-param>  -->
			
			<!-- 注册ContextLoaderListener 此处不需要配置,servlet中配置即可-->
		<!-- <listener>
				<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
			</listener>  -->
			
			 <servlet>
				<servlet-name>appServlet2</servlet-name>
				<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
				<!-- 使用java配置 配置根配置类、注册ContextLoaderListener 只需要在servlet中注册即可，
					多出配置根配置类、注册ContextLoaderListener会出现重复配置，启动项目时会报错
				-->
				<init-param>
					<param-name>contextClass</param-name>
					<param-value>org.springframework.web.context.support.AnnotationConfigWebApplicationContext</param-value>
				</init-param>
				<!-- 指定DispatcherServlet配置类 -->
				<init-param>
					<param-name>contextConfigLocation</param-name>
					<param-value>com.spittr.config.WebConfig</param-value>
				</init-param>
				<load-on-startup>1</load-on-startup>
			</servlet>
			<servlet-mapping>
				<servlet-name>appServlet2</servlet-name>
				<url-pattern>/</url-pattern>
			</servlet-mapping> 
		</web-app>
		
	
}
- 2.处理异常{
	2.1spring提供了多种方式将异常转换为响应
		1.特定的spring异常将会自动映射为指定的http状态码
		2.异常上可以添加@ResponseStatus注解，从而将其映射为某种http状态码
		3.在方法上添加@ExceptionHandler注解，使其用来处理异常
	2.2spring的一些异常会默认映射为http状态码
		spring异常									HTTP状态码
		BindException								400 - bad request
		ConversionNotSupportException				500 - internal server error
		HttpMediaTypeNotAcceptableException			406 - not acceptable
		HttpMediaTypeNotSupportedException			415 - unsupported Media Type
		HttpMessageNotReadableException				400 - bed request
		HttpMessageNotWritableException				500 - internal server error
		HTTPRequestMethodNotSupportedException		405 - method not Allowed
		MethodArgumentNotValidException				400 - bad request
		MissServletRequestParameterException		400 - bad request
		MissServletRequestPartException				400 - bad request
		NoSuchRequestHandlingMethodException		404 - Not Found
		TypeMismatchException						400 - bad request
		
	2.3@ResponseStatus注解将异常映射为HTTP状态码
		实例:
			/**
			 * 
			 * Spring提供了多种方式将异常转换为响应
			 * 1.特定的spring异常将会自动映射为指定的http状态码
			 * 2.异常上可以添加@ResponseStatus注解,从而将其映射为某中http状态码
			 * 3.在方法上添加@ExceptionHandler注解，是其用来处理异常
			 *
			 */
			@ResponseStatus(value=HttpStatus.NOT_FOUND,reason="Spittle Not Found")
			public class SpittleNotFoundException extends NullPointerException{
			}
			
			Controller中
			
			@ResponseBody
			@RequestMapping(value="/spittlesFindOne/{id}",method=RequestMethod.GET)
			public Spittle spittlesFindOne(@PathVariable("id") long id,Model model){
				
				Spittle spittle = spittleRepository.findOne(id);
				if(spittle==null){
					System.out.println("test");
					throw new SpittleNotFoundException();
				}
				return spittle;
			}
		2.4处理同一Controller中所有处理器所抛出的异常@ExceptionHandler
			实例:
				处理所有的异常@ExceptionHandler(Exception.class)
				处理特有的一种异常@ExceptionHandler(SpittleNotFoundException.class)
				@ResponseBody
				@ExceptionHandler(Exception.class)
				public String handlerSpittle(){
					System.out.println("00000");
					return "Exception";
				}

			
}
- 3.为控制器添加通知{
	3.1@ControllerAdvice
		控制器通知是任意带有@ControllerAdvice注解的类，这个类会包含一个或多个一下类型的方法
			1.@ExceptionHandler 注解标注的方法
			2.@InitBinder 注解标注的方法
			3.@ModelAttribute 注解标注的方法
		@ControllerAdvice本身已经使用了@component，会自动被组件扫描获取到
		
		实例：
		@ControllerAdvice//定义控制器类
		public class ApplicationExceptionHandler{
			@ResponseBody
			@ExceptionHandler(Exception.class) //定义异常处理方法
			public String exceptionHandler(){
				System.out.println("返回error");
				return "error";
			}
		}
}
- 4.跨重定向请求传送数据{
	重定向：防止用户点击浏览器的刷新按钮或者后退箭头时，客户端重新执行危险的post请求
	当控制器的结果重定向的话，原始的请求就结束了，并发起一个新的GET请求，原始请求中所带有的模型数据也随着请求的结束一起消亡。
		在新的请求属性中，没有任何的模型数据。
	4.1通过URL模板进行重定向
		以路径变量和/或查询参数的形式传递数据
		实例:
			@RequestMapping(value="/register", method=RequestMethod.POST)
			public String processRegistration(Spitter spitter,Model model) {
				spittleRepository.save(spitter);
				model.addAttribute("userName", spitter.getUserName());
				model.addAttribute("id", spitter.getId());
				return "redirect:/spitter/get/" + spitter.getUserName();
			}
		如果userName="haha",id=24;则最终重定向的地址为:
		/spitter/get/haha?id=24
	4.2使用flash属性
		将之前已经得到的对象重定向到下个请求进行使用，Spring提供了将数据发送为flash属性的功能
		Spring提供了通过RedirectAttributes设置flash属性的方法
		实例：
			/*
			 * 表单提交
			 * 
			 * java.lang.AssertionError: Redirected URL expected:</spitter/jbauer> but was:</spitter/null>
			 * 在processRegistration的参数时一个对象，当传入参数的时候，
			 * 是要默认调用参数的setter方法的，
			 * 所以，要在Spitter类中添加对应的setter方法。
			 */
			@RequestMapping(value="/register", method=RequestMethod.POST)
			public String processRegistration(Spitter spitter,RedirectAttributes model) {
				spittleRepository.save(spitter);
				model.addAttribute("userName", spitter.getUserName());
		//		model.addAttribute("id", spitter.getId());
				//Spring提供了通过RedirectAttributes设置flash属性的方法
				model.addFlashAttribute("spitter", spitter);
				return "redirect:/spitter/get/" + spitter.getUserName();
			}
			
			@ResponseBody
			@RequestMapping(value="/get/{userName}", method=RequestMethod.GET)
			public String showSpitterProfile(@PathVariable String userName,Model model){
				//检查是否存在有key为spitter的model属性，如果有则不需要再次查询
				if(!model.containsAttribute("spitter")){
					Spitter spitter = spittleRepository.findByUserName(userName);
					model.addAttribute(spitter);
				}
				return "profile";
			}
}

-5.配置访问静态资源{
	<!-- 
    	配置访问静态资源
   	 	default-servlet-handler 在springMVC上下文中定义一个DefaultervletHttpRequestHandler，
   	 	它会对进入DispatcherServlet的请求进行筛选，若发现是没有进行映射的请求，就将该请求交由WEB应用服务器默认的
   	 	Servlet处理。若不是静态资源请求，才有DispatcherServlet继续处理。
   	 	
   	 	一般WEB应用服务器默认的Servlet的名称都是default。
   	 	若使用的WEB应用服务器默认的Servlet的名称不是default，则需要default-servlet-name属性显示的指定
    
		在springMVC配置文件中添加下面的配置
     -->
	 
    <mvc:default-servlet-handler />
	
}

-6.配置HiddenHttpMethodFilter{
	<!-- 配置HiddenHttpMethodFilter 把POST转为DELETE PUT请求 -->
	<filter>
		<filter-name>HiddenHttpMethodFilter</filter-name>
		<filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>
	</filter>

	<filter-mapping>
		<filter-name>HiddenHttpMethodFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
}