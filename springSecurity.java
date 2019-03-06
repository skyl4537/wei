spring Security是基于spring的应用程序提供声明式安全保护的安全框架
1.spring Security{
	1.1过滤web请求
		spring Security借助一系列的servlet Filter来提供各种安全性功能
		DelegatingFilterProxy是一个特殊的servlet Filter
		
		传统配置在web.xml 中添加
		<!-- spring security 过滤器 -->
		<filter>
		   <filter-name>springSecurityFilterChain</filter-name>
		   <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
		   <init-param>
				<param-name>contextConfigLocation</param-name>
				<param-value>/WEB-INF/spring-security.xml</param-value>
		   </init-param>
		</filter>
		<filter-mapping>
		   <filter-name>springSecurityFilterChain</filter-name>
		   <url-pattern>/</url-pattern>
		</filter-mapping>
		DelegatingFilterProxy会将过滤逻辑交给springSecurityFilterChain
		
		
		java配置DelegatingFilterProxy
		AbstractSecurityWebApplicationInitializer实现了WebApplicationInitializer，因此spring会发现它
		并用他在web容器中注册DelegatingFilterProxy
		import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
		public class SecurityWebInitializer extends AbstractSecurityWebApplicationInitializer {}
		
	1.2安全性配置
		spring Security 必须配置在一个实现了WebSecurityConfigurer或者继承了WebSecurityConfigurerAdapter的bean中，在spring已不应用上下文中
		任何实现了WebSecurityConfigurer的bean都可以用来配置spring Security
	
		/*
		 * @EnableWebMvcSecurity中配置了一个参数解析器，这样的话就能够通过带有@AuthenticationPrincipal注解
		 * 的参数获得用户的principle，他同时还配置了一个bean，在使用spring表单绑定标签库定义表单时，这个bean会自动添加
		 * 一个隐藏的跨站请求伪造(cross-site request forgery CSRF)token输入域
		 */

		/**
		 * @EnableWebSecurity中包号@Configuration注解，此类不需要加@Configuration注解
		 * @EnableWebSecurity 注解将会启动web安全功能，但其本身并没有什么用处，spring Security必须配置在一个实现了
		 * WebSecurityConfigurer的bean中，或者扩展WebSecurityConfigurerAdapter。在spring应用上下文中，任何实现了
		 * WebSecurityConfigurer的bean都可以用来配置spring security
		 */
		@EnableWebSecurity
		//@EnableWebMvcSecurity// 启用springmvc安全性
		public class MySecurityConfig extends WebSecurityConfigurerAdapter {

			@Override
			protected void configure(HttpSecurity http)protected void configure(HttpSecurity http) throws Exception {

		//		//默认的configure(HttpSecurity http)方法等同于下面的配置
		//		http.authorizeRequests().anyRequest().authenticated().and().formLogin().and().httpBasic();
		//      定义请求的授权规则

				http.authorizeRequests()//ExpressionUrlAuthorizationConfigurer.ExpressionInterceptUrlRegistry
						.antMatchers("/").permitAll()
						.antMatchers("/level1/**").hasRole("VIP1")
						.antMatchers("/level2/**").hasRole("VIP2")
						.antMatchers("/level3/**").hasRole("VIP3");
		//                .and()
		//                //.requiresSecure()设置安全通道，即访问“/level4/**”的请求都视为需要安全通道，并自动将请求重定向到HTTPS上
		//                .requiresChannel()
		//                .antMatchers("/level4/**")
		//                .requiresSecure()
		//                .and()
				//requiresInsecure()访问“/”的请求，将视为不需要安全通道，将请求重定向到不安全的通道上
		//                .requiresChannel()
		//                .antMatchers("/").requiresInsecure()
		//                .and()
				/**
				 * 禁止CSRF防护功能
				 * spring Security 3.2后默认开启CSRF防护
				 */
		//                .csrf()
		//                .disable();


				/**
				 * 启用默认的登录页
				 * 如果没有登录，或者没有权限则进入到登录界面。默认登录路径为 /login
				 * 重定向到/login?error表示登录失败
				 * loginPage("/myLogin")自定义登录地址，
				 * usernameParameter("user")自定义用户名字段名称
				 * passwordParameter("pwd")自定义密码字段名称
				 */
		//       http.formLogin().loginPage("/myLogin").usernameParameter("user").passwordParameter("pwd");
				http.formLogin();
				/*
					开启自动配置的注销功能，会访问一个/logout的请求，来清空session
					注销成功会返回/login?logout页面
				 */
				http.logout();
				/**
				 * 配置注销成功后返回的路径地址
				 * 注销成功返回首页
				 */
		//        .logoutSuccessUrl("/");

				//开启记住我功能
				/**
				 * 登录成功以后将cookie值发送给浏览器保存，以后登录带上cookie值，只要通过检查即可免登陆，
				 * 如果点击注销则会删除cookie
				 */
				http.rememberMe();

			}

			@Override
			protected void configure(AuthenticationManagerBuilder auth) throws Exception {
				/*
				 * withUser()方法为内存用户存储添加新的用户，该方法返回UserDetailsManagerConfigurer.UserDetailsBuilder
				 * 对象，该对象提供了多种配置用户信息的方法
				 *
				 * and()方法能够将多个用户配置链接起来
				 * roles()方法是authorities()方法的缩写，roles()方法给定的值都会添加一个“ROLE_”前缀，并将其作为权限授予用户
				 * inMemoryAuthentication()启用内存用户存储
				 */
				auth.inMemoryAuthentication()
						.passwordEncoder(new MyPasswordEncoder()).withUser("zhangsan").password("123456").roles("VIP1")
						.and()
						.withUser("lisi").password("123456").roles("VIP2")
						.and()
						.withUser("wangwu").password("123456").roles("VIP3");


				/**
				 * 关系型数据库认证
				 * passwordEncoder() 方法可以接受spring security中的passwordEncoder接口的任意实现
				 * spring Security 的加密模块实现了三种
				 * 1.BCryptPasswordEncoder
				 * 2.NoOpPasswordEncoder
				 * 3.StandardPasswordEncoder
				 *
				 * 也可以实现PasswordEncoder接口自定义加密
				 */
		//		auth.jdbcAuthentication().dataSource(dataSource)
		//		.usersByUsernameQuery("select username,password,true from Spitter where username=?")
		//		.authoritiesByUsernameQuery("select username,'ROLE_USER' from Spitter where username=?")
		//		.passwordEncoder(new StandardPasswordEncoder("password"))
		//		.passwordEncoder(new MyPasswordEncoder("password"));
		//		.groupAuthoritiesByUsername(query); 自定义群组权限

			}
		}
	2.拦截请求{
		对于每个请求进行精细的安全性控制的关键在于重载protected void configure(HttpSecurity http)方法
		HttpSecurity对象可以在多方面配置HTTP的安全性
		http.authorizeRequests()//方法返回ExpressionUrlAuthorizationConfigurer.ExpressionInterceptUrlRegistry对象
		该对象定义保护路径的配置方法有
			方法  										能够做什么
			access(String)								如果给定的spel表达式计算结果为true，就允许访问
			anonymous()									允许匿名访问
			authenticated()								允许认证过的用户访问
			denyAll()									无条件拒绝所有访问
			fullAuthenticated()							如果用户是完整认证(不是通过remember-me功能认证)，允许访问
			hasAnyAuthority(String...)					如果用户具体给定权限的一种，允许访问
			hasAnuRole(String...)						如果用户具体给定角色的一种，允许访问
			hasAuthority(String)						用户具备给定权限，允许访问
			hasIpAddress(String)						请求来自给定ip地址，允许访问
			hasRole(String)								用户具备给定的角色，允许访问
			not()										对其他访问方法的结果求反
			permitAll()									无条件允许访问
			rememberMe()								通过remember-me功能认证的，允许访问
			antMatchers()								指定需要进行认证的请求路径
		
		这些方法会根据给定的顺序发挥作用，将最为具体的请求路径放在前面，而不具体的路径放在后面
		
		2.1使用spring表达式进行安全配置
			spring Security通过一些安全性相关的表达式扩展了spring表达式语言
				安全表达式								计算结果
				authentication							用户的认证对象
				denyAll 								结果始终false
				hasAnuRole(list of roles)				用户被授权了列表中的任意的指定角色,结果为true
				hasRole(role)							用户被授予了指定的角色，结果为true
				hasIPAddress(IP Address)				请求为指定IP结果为true
				isAnonymous()							当前用户为匿名用户，结果为true
				isAuthenticated()						当前用户进行了认证，结果为true
				isFullyAuthenticated()					当前用户是完整认证(不是通过remember-me功能认证),结果为true
				isRememberMe()							当前用户通过remember-me功能认证的，结果为true
				permitAll()								结果始终为true
				principal								用户的principal对象
		http.authorizeRequests().antMatchers("/level4/**").access("hasRole('VIP4') and hasIpAddress('192.168.8.8')");
	}
	3.保护视图{
		3.1使用springSecurity的JSP标签库
			JSP标签												作用	
			<security:accesscontrollist>						用户通过访问控制列表授予了指定的权限，渲染该标签体中的内同
			<security:authentication>							渲染当前用户认证对象的详细信息
			<security:authorize>								用户授予了特定的权限或者spel表达式的计算结果为true，渲染该标签体的内同
			
			声明JSP标签库
			<% taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
		
	}
		
}