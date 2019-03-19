1.概念性的东西{
	Servlet作用？
        Servlet容器是什么？
        Servlet是否线程安全？
        .......生命周期？分别作用？调用几次
        
        转发和重定向区别？ 至少4个
        转发和重定向时 / 表示什么
}
        
0.复习要点{
        滤掉 -> 5,6,7,9,19
        重点 -> 3,4,8,10,11,13
}

1.servlet{
	在web.xml，servlet节点下添加子节点
	<init-param>
  		<param-name>user</param-name>
  		<param-value>sa</param-value>
  	</init-param>
	<init-param>
  		<param-name>password</param-name>
  		<param-value>12345</param-value>
  	</init-param>
	
	在servlet实现类中，init方法获取初始化参数值
	
	第一种:根据name键值获取{
		String user = servletConfig.getInitParameter("user");
		String password = servletConfig.getInitParameter("password");
	}
	
	第二种:获取name键值数组{
		Enumeration<String> initParameterNames = servletConfig.getInitParameterNames();
		while(initParameterNames.hasMoreElements()) {
			String name = initParameterNames.nextElement();
			String value = servletConfig.getInitParameter(name);
			System.out.println("while name = "+name+" ; value = "+value);
		}
	}
		
	<load-on-startup>-1</load-on-startup>
		load-on-startup 可以指定 Serlvet 被创建的时机. 若为负数, 则在第一次请求时被创建.若为 0 或正数, 则在当前 WEB 应用被Serlvet 容器加载时创建实例, 且数组越小越早被创建. 

	<servlet-mapping>
		一个<servlet>可以映射多个<servlet-mapping>
		<url-pattern> 一种格式是“*.扩展名”，另一种格式是以正斜杠（/）开头并以'/*'结尾
		
}

2.Servlet 生命周期{
	由Serlvet容器负责调用
	1.构造器()
	2.init()
	3.service()
	4.destroy()
}

3.servletContext当前应用上下文{
	ServletConfig: 封装了 Serlvet 的配置信息, 并且可以获取 ServletContext 对象
	servletContext应用上下文，应用于整个应用 是应用的全局参数
	给应用添加全局参数配置，在web.xml，添加context-param子节点
	<context-param>
		<param-name>jdbc</param-name>
		<param-value>mysql</param-value>
	</context-param>
	<context-param>
		<param-name>driver</param-name>
		<param-value>com.mysql.jdbc.Driver</param-value>
	</context-param>
	
	在servlet容器应用启动时，初始化参数，即tomcat启动时，初始化应用上下文参数
	
	获取应用上下文方法
	ServletContext servletContext = servletConfig.getServletContext();
	
	获取当前web应用名称
	String contextPath = servletContext.getContextPath();
	
	获取应用上下文初始化参数方法与servlet获取初始化方法相同
	
	获取当前web应用的某个文件在服务器上的绝对路径  
	String realPath = servletContext.getRealPath("/note.txt");
	
	获取类配置文件{
		类路径的相对路径
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream resourceAsStream = classLoader.getResourceAsStream("jdbc.properties");
		
		web应用程序的相对路径
		InputStream resourceAsStream2 = servletContext.getResourceAsStream("/WEB-INF/classes/jdbc.properties");
		System.out.println("2."+resourceAsStream2);
	}
}

4.service{
	ServletRequest{
		根据请求参数的名字，返回请求参数的数组
		String[] parameterValues = req.getParameterValues("interesting");
		for (String interesting : parameterValues) {
			System.out.println("####"+"interesting = "+interesting);
		}
		
		获取请求参数的Map
		Map<String, String[]> parameterMap = req.getParameterMap();
		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			System.out.println("****"+entry.getKey()+":"+Arrays.asList(entry.getValue()));
		}
		
		HttpServletRequest: 是 SerlvetRequest 的子接口. 针对于 HTTP 请求所定义.
		HttpServletRequest httpServlet = (HttpServletRequest) req;
		
		///helloworld/loginServlet 获取到当前应用的相对访问路径
		String requestURI = httpServlet.getRequestURI();
		System.out.println("requestURI = "+requestURI);
		
		//http://localhost:8080/helloworld/loginServlet 获取当前访问的整个路径
		StringBuffer requestURL = httpServlet.getRequestURL();
		System.out.println("requestURL = "+requestURL.toString());
		
		//获取当前访问的请求参数    get请求
		String queryString = httpServlet.getQueryString();
		System.out.println("queryString = "+queryString);
		
		// /loginServlet
		String servletPath = httpServlet.getServletPath();
		System.out.println("servletPath = "+servletPath);
	}
	
	ServletResponse{
		设置响应的内容类型: 
			response.setContentType("application/msword");
	}
	
}

5.JSP{
	jsp页面应放在应用程序除WEB-INF以外的路径下
	在jsp页面中写java 代码，要在body节点下<% %>中写
	在jsp头部添加引入
	<%@page  import="java.util.Date" %>
	<%
		Date date = new Date();
		System.out.println(date);
	%>
	
	jsp隐含变量：没有声明就可以使用的对象. JSP页面一共有 9 个隐含对象. 
	①. request: HttpServletRequest 的一个对象. *
	②. response: HttpServletResponse 的一个对象(在 JSP 页面中几乎不会调用 response 的任何方法.)

	③. pageContext: 页面的上下文, 是 PageContext 的一个对象. 可以从该对象中获取到其他 8 个隐含对象. 也可以从中获取到当前
	页面的其他信息. (学习自定义标签时使用它) *
	④. session: 代表浏览器和服务器的一次会话, 是 HttpSession 的一个对象. 后面详细学习. *

	⑤. application: 代表当前 WEB 应用. 是 ServletContext 对象. *
	⑥. config: 当前 JSP 对应的 Servlet 的 ServletConfig 对象. 若需要访问当前 JSP 配置的初始化参数, 
	需要通过映射的地址才可以.

	映射 JSP:

	  <servlet>
		<servlet-name>hellojsp</servlet-name>
		<jsp-file>/hello.jsp</jsp-file>
		<init-param>
			<param-name>test</param-name>
			<param-value>testValue</param-value>
		</init-param>
	  </servlet>
	  
	  <servlet-mapping>
		<servlet-name>hellojsp</servlet-name>
		<url-pattern>/hellojsp</url-pattern>  	
	  </servlet-mapping>
	  
	⑦. out: JspWriter 对象. 调用 out.println() 可以直接把字符串打印到浏览器上. *
	⑧. page: 指向当前 JSP 对应的 Servlet 对象的引用, 但为 Object 类型, 只能调用 Object 类的方法(几乎不使用) 

	⑨. exception: 在声明了 page 指令的 isErrorPage="true" 时, 才可以使用. *

	<%@ page isErrorPage="true" %>

	pageContext, request, session, application(对属性的作用域的范围从小到大)
	out, response, config, page 
	exception
	
}

6.jsp表达式{
	<%= date %>直接输出变量到浏览器
	<%! %>内可声明方法
	
}

7.setAttribute{
	<%
		//pageContext 属性的作用范围仅限于当前页面
		pageContext.setAttribute("pageContextAttr", "pageContextValue");
	
		//request 属性的作用范围仅限于同一个请求
		request.setAttribute("requestAttr", "requestValue");
		
		//session 属性作用范围仅限于本次会话
		session.setAttribute("sessionAttr", "sessionValue");
		
		//application 属性作用范围使用与当前web应用 
		application.setAttribute("applicationAttr", "applicationValue");
	%>
}

8.请求转发/请求重定向{
	1.请求次数
		forward 只发送一次请求
		request.getRequestDispatcher("/testServlet").forward(request, response);
		redirect 发送二次请求
		response.sendRedirect("testServlet");
	2.地址栏变化
		forward 显示第一次请求的地址路径
		redirect 最后一次响应的请求路径
	3.request
		forward 同一个request对象
		redirect 不同的request对象
	4.
		forward只能在当前应用进行请求转发
		redirect 可以是不同的应用进行重定向
		
	5.
		forward: / 代表的是当前 WEB 应用的根目录 http://127.0.0.1:8080/helloworld
		redirect: / 代表的是当前 WEB 站点的根目录. 例：http://127.0.0.1:8080/
}

9.jsp指令{
	1.jsp指令基本语法格式
		<%@ 指令 属性="值" 属性="值" 属性="值"......%>
		属性名大小写敏感
		包含 page include taglib三个指令
	
	2.page指令
		page指令 用于配置页面的各种属性，作用于整个jsp页面，最好放在jsp页面的起始位置
		<%@ page language="java" contentType="text/html; charset=UTF-8"
		pageEncoding="UTF-8"%>
		
		import属性 可以导入使用的类，一些遗憾的包可以不用引入
		import javax.servlet.*;
		import javax.servlet.http.*;
		import javax.servlet.jsp.*;
		
		session属性 session="true|false" 当前页面是否允许使用session，访问当前页面是否产生HTTPSession对象
		
		errorPage:跳转错误页面是请求转发的机制
		errorPage="错误页面当前页面的相对路径" 当前页面出现错误的实际相应页面 /表示当前web应用的根目录
		isErrorPage="true|false" 之前当前页面是否为错误处理页面，即是否可以使用隐含对象exception，建议将该放入WEB-INF路径下，不可直接访问
		
		contentType:指定当前jsp页面的响应类型和字符编码，实际调用的是 response.setAttribute("text/html; charset=UTF-8");
		
		pageEncoding:指定当前jsp页面的字符编码，与contentType的编码格式相同
		
		isELIgnored:指定当前页面是否可使用EL表达式
		
		在web.xml中配置错误页面两种方式:
		//根据errorcode指定错误代码访问的错误页面 /表示当前web应用的根目录
		 <error-page>
			<error-code>404</error-code>
			<location>/WEB-INF/error/error.jsp</location>
		  </error-page>
		  //根据错误类型指定访问错误页面 
		  <error-page>
			<exception-type>java.lang.ArithmeticException</exception-type>
			<location>/WEB-INF/error/error.jsp</location>
		  </error-page>
		  
	3.include指令
		静态包含 file="相对路径" /表示当前web应用的根目录，只生成一个 jsp对应的java文件，即源码包含
		<%@ include file="b.jsp" %> 在a.jsp 声明的变量 在b.jsp中可以直接使用
	4.<jsp:标签> 
		动态包含，生成两个jsp对应java文件
		<jsp:include page="b.jsp"></jsp:include>
		通过方法进行包含
		org.apache.jasper.runtime.JspRuntimeLibrary.include(request, response, "b.jsp", out, false);
		
		<jsp:forward page="b.jsp"></jsp:forward>
		相当于：request.getRequestDispatcher("/testServlet").forward(request, response);
		
		<jsp:param value="AAAAParam" name="user"/>
			在<jsp:forward>和<jsp:include> 都可使用
			
			<jsp:forward page="b.jsp">
				<jsp:param value="AAAAParam" name="user"/>
			</jsp:forward>
			
			b.jsp 获取参数 <%=request.getParameter("user") %>	
	5.tomcat配置中文
		http://localhost:8080/docs/config/http.html	
}

10.cookie{
	1.cookie跟踪会话的一种方式，cookie默认是会话级别的，在客户端保存信息
	2.创建cookie并保存的方式
		Cookie cookie = new Cookie(name,value);
		response.addCookie(cookie);
	3.获取cookie对象及值
		Cookie[] cookies = request.getCookies();
		if(null!=cookies&&cookies.length>0){
			for(Cookie cke:cookies){
				out.println("cookieName : "+cke.getName()+" ; cookieValue : "+cke.getValue());
				out.println("<br>");
			}
		}
	4.设置cookie时效
		设置cookie存活的最大时间，单位s，0表示立马删除该cookie，是负数表示该cookie永远不会被存储
		cookie.setMaxAge(30);
	5.设置cookie的作用范围
		cookie的作用范围：只能用在当前目录及子目录下，不能作用域父级目录,使用cookie.setPath设置cookie的作用范围
		 /表示WEB站点根目录
		cookie.setPath(request.getContextPath());
}

11.HttpSession{
	HttpSession
		采用session机制在服务器端跟踪保存客户端信息
		session通过sessionID来区分不同的客户端，系统默认输出一个名为JSESSIONID的cookie，即session cookie
		保存sessionID采用cookie的方式，
	持久化session cookie
		<%
			Cookie cookie = new Cookie("JSESSIONID", session.getId());
			cookie.setMaxAge(10);
			response.addCookie(cookie);
		%>
	
	HttpSession生命周期
		创建HttpSession
			session="false"，表示禁用session的隐含变量，不是不可用session，可以显示的声明session
			
			session.jsp是访问服务器的第一个请求，且session.jsp中 page 设置session="false"，<%=request.getSession(false) %>则不会创建session对象
				request.getSession(false)，则返回null
				
			本次会话已经创建了session则不会创建新的session，使用本次会话的session而不会重新创建新的session对象
			
			servlet创建session，只有调request.getSession()/request.getSession(boolean)才会创建HttpSession对象
				request.getSession(boolean),false没有则返回null，有则直接返回 | true 一定返回session对象，没有则创建新的session返回 
			
			服务器检查本次请求是否有sessionID,有则使用，无则创建
			浏览器禁用cookie时，每次都会创建新的session对象
			浏览器禁用cookie时,可以采用encodeUrl的方式保存sessionID，<form action="<%=response.encodeUrl("hello.jsp") %>" method="post">
				sessionID会显示在地址栏上。
			
		销毁HttpSession
			调用session.invalidate();方法
			服务器卸载当前web应用
			超过HTTPSession过期时间，默认1800s,半小时 获取session的有效时间 session.getMaxInactiveInterval();
			在web.xml中配置session过期时间
			 <session-config>
				<session-timeout>30</session-timeout>
			</session-config>
			关闭浏览器并不会销毁session对象
	HttpSession相关API
		setAttribute,getAttribute,invalidate(),getMaxInactiveInterval(),setMaxInactiveInterval(),getId()...
		
	
}

12.绝对路径{
	相对于当前web应用根路径的路径，即任何的路径都带上contextPath
	
	/ 代表什么,
		代表的是当前 WEB 应用的根路径: 若 / 所在的命令或方法需被 WEB 服务器解析, 而不是直接打给浏览器, 则 / 代表 WEB 应用的根路径. 
		代表的是站点的根目录: 若 / 直接交由浏览器解析, / 代表的就是站点的根路径, 此时必须加上 contextPath
}

13.表单重复提交{
	造成表单重复提交的情况
		1.网络延迟
		2.浏览器回退后点击提交
		3.转发的页面点击F5
	避免表单重复提交
		1.创建随机数token
		2.创建session
		3.将随机数token放置session中
		4.创建隐藏域也将随机数放入隐藏域中
		5.提交form表单
		
		1.对比session中token值与request中的值是否相同
			1.1相同则将session中的值移除
			1.2不同则提示用户表单重复提交
		2.判断参数合法
		3.业务处理
}

14.EL表达式{
	el表达式 ${}
		${sessionScope.customer.name}
	[]特殊字符时使用
		${sessionScope.["com.bluecard.entity.Customer"]}
	el变量
		${username},如果没有指定范围则从 page --> request --> session --> application 中依次查找，
		若没有找到则返回为null。${sessionScope.username}如果指定了范围，则在指定范围内查找属性
		username:${param.username }和<%=request.getParameter("username")%>获取参数的方式相同
		el可以自动进行类型转化
	el中的隐含对象
		1.pageContext
		2.pageScope 		取得page范围内属性所对应的值
		3.requestScope 		类似
		4.sessionScope		类似
		5.applicationScope	类似
		6.param				同request.getParameter("username")，回传String类型值
		7.paramValues		获取一组请求参数
		8.cookie			同request.getCookies()
		9.header			类似
		10.headerValues
		11.initParam		同ServletContext.getInitParameter(name)
		12.pageContext		
}

15.jstl{
	1.c:out{
		1.可以对敏感字符自动转换<br>
		2.default value值为空时显示default的值<br>
		3.escapeXml 默认false 是否进行特殊字符转义<br>
		<%
			request.setAttribute("book", "<<java>>");
		%>

		<c:out value="${requestScope.books }" default="js"></c:out>
	}

	2.c:set{
		1.可以在指定范围内放一个属性，属性值也可是el表达式，等同pageContext.setAttribute("name", "wei");<br>
		2.target:为javabean的属性赋值<br>
		3.target、value:支持el表达式<br>

		<c:set var="name" value="wei" scope="page"></c:set>
		<%--
			pageContext.setAttribute("name", "wei");
		--%>
		<c:set var="subject" value="${param.subject}" scope="session"></c:set>
		subject:${sessionScope.subject}
		<br>

		<%
			Customer cust = new Customer();
			cust.setId(1001);

			request.setAttribute("cust", cust);
		%>

		id:${requestScope.cust.id }
		<br>
		<c:set target="${requestScope.cust }" property="id"
			value="${param.id }"></c:set>
		修改id:${requestScope.cust.id }
	}

	3.c:remove{
		删除指定域的指定属性
	 
		<br>
		<c:set var="date" value="2019-01-01" scope="session"></c:set>
		date:${sessionScope.date }
		<br>
		<c:remove var="date"/>	
		date:--*${sessionScope.date }*--
	}
	 
	4.c:if{
		1.只有if无else<br>
		2.可以将判断结果保存，以便后续使用<br>
		<c:set var="age" value="20" scope="request"></c:set>
		<c:if test="${requestScope.age>18 }" var="isAdult">
			成年
		</c:if>
	}
	
	5.c:choose--c:when..c:otherwise{
		可以实现 if... else if...else if...else...<br>
		c:when,c:otherwise不能脱离c:choose标签单独使用<br>
		c:otherwise必须在c:when之后使用<br>
	
		 <c:choose>
			<c:when test="${param.age>60 }">
				老年人
			</c:when>
			<c:when test="${param.age>25 }">
				中年
			</c:when>
			<c:when test="${param.age>18 }">
				青年
			</c:when>
			<c:when test="${param.age>12 }">
				青少年
			</c:when>
			<c:otherwise>
				少年
			</c:otherwise>
		 </c:choose>
	}
	
	6.c:foreach{
		1.var 用来存放成员变量 不支持el0 String类型<br>
		2.items 被迭代的对象 支持el 执行的数据类型 Array Collection Iterator Eunmeration Map String <br>
		3.varStatus <br>
		4.begin 开始位置<br>
		5.end 结束位置<br>
		6.step 每次迭代间隔数<br>
		<c:forEach var="i" begin="1" end="10" step="2">
			${i }
		</c:forEach>
		<br>
		<%
			List<Customer> custs = new ArrayList<Customer>();
			custs.add(new Customer(1,"AA"));
			custs.add(new Customer(2,"BB"));
			custs.add(new Customer(3,"CC"));
			custs.add(new Customer(4,"DD"));
			request.setAttribute("custs", custs);
		%>
		varStatus 
		1.index,当前参数的索引<br>
		2.count,当前参数是第几个<br>
		3.first,当前参数是否是第一个<br>
		4.last,当前参数是否是最后一个<br>
		<c:forEach var="custs" items="${requestScope.custs}" varStatus="status">
			${status.index}--${status.count}-${status.first}-${status.last}--${custs.id }---${custs.name}<br>
		</c:forEach>
		
		<h5>遍历Map</h5>
		<%
			Map<String ,Customer> custMap = new HashMap<String ,Customer>();
			custMap.put("a",new Customer(1,"AA"));
			custMap.put("b",new Customer(2,"BB"));
			custMap.put("c",new Customer(3,"CC"));
			custMap.put("d",new Customer(4,"DD"));
			custMap.put("e",new Customer(5,"EE"));
			request.setAttribute("custMap", custMap);
		%>
		<c:forEach var="cust" items="${requestScope.custMap}">
			${cust.key } ---- ${cust.value } --  ${cust.value.id }--- ${cust.value.name }<br>
		</c:forEach>
		<h5>遍历数组</h5>
		<%
			String[] names = new String[]{"AAA","BBB","CCC"};
			request.setAttribute("names", names);
		%>
		
		<c:forEach var="name" items="${requestScope.names }">
			${name }----
		</c:forEach>
	}
	
}

16.Filter{
	1.Filter是JavaWEB 的一个重要组件, 可以对发送到 Servlet 的请求/响应进行拦截.
		Filter为单例
	
	2.web.xml 文件中配置与servlet类似
		<filter>
			<filter-name>helloFilter</filter-name>
			<filter-class>com.blue.filter.HelloFilter</filter-class>
			<init-param>
				<param-name>username</param-name>
				<param-value>sa</param-value>
			</init-param>
		</filter>

		<filter-mapping>
			<filter-name>helloFilter</filter-name>
			<url-pattern>/filter"/*"</url-pattern> // /filter/*
		</filter-mapping>
	3.Filter 相关的 API
		public void init(FilterConfig arg0) 
			servlet容器加载应用时调用一次
		public void destroy() 
			servlet容器关闭应用时调用一次
		public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			拦截后在此方法内做逻辑处理
		FilterChain  Filter 链. 多个 Filter 可以构成一个 Filter 链
			doFilter(ServletRequest request, ServletResponse response): 把请求传给 Filter 链的下一个 Filter
				该方法为同步方法
			若当前 Filter 是 Filter 链的最后一个 Filter, 将把请求给到目标 Serlvet(或 JSP)	
			多个 Filter 拦截的顺序和 <filter-mapping> 配置的顺序有关, 靠前的先被调用. 
	4.filter-mapping
		<filter-name>指定被拦截的servlet的名称
		<dispatcher> 指定被拦截的请求方式  默认request
			request 通过get post 请求方式
			include 通过页面包含的方式
			forward 请求转发的方式
			error 错误页面的方式
			 <filter-mapping>
				<filter-name>HelloFilter</filter-name>
				<url-pattern>/filter/test.jsp</url-pattern>
				<dispatcher>FORWARD</dispatcher>
				<dispatcher>REQUEST</dispatcher>
			  </filter-mapping>
			
			可以设置多个dispatcher设置指定filter对资源的多种调用方式的拦截
		对一个filter可以有多个filter-mapping的映射
	5.案例
		1.不缓存页面的过滤器
			@Override
			public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
					throws IOException, ServletException {
				response.setDateHeader("Expires", -1);
				response.setHeader("Cache-Control", "no-cache");
				response.setHeader("Pragma", "no-cache");
				
				chain.doFilter(request, response);
			}
			
		2.配置编码格式
			web.xml中配置
			 <context-param>
				<param-name>encoding</param-name>
				<param-value>UTF-8</param-value>
			  </context-param>
			  <filter>
				<display-name>EncodingFilter</display-name>
				<filter-name>EncodingFilter</filter-name>
				<filter-class>com.blue.filter.EncodingFilter</filter-class>
			  </filter>
			  <filter-mapping>
				<filter-name>EncodingFilter</filter-name>
				<url-pattern>/encode'/*</url-pattern>
			  </filter-mapping>
			@Override
			public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
					throws IOException, ServletException {
					String encoding = getFilterConfig().getServletContext().getInitParameter("encoding");
					request.setCharacterEncoding(encoding);
					chain.doFilter(request, response);
			}
			
	6.HttpServletRequestWrapper、HttpServletResponseWrapper ？？？？？？
		HttpServletRequestWrapper 类实现了 HttpServletRequest 接口中的所有方法,
		这些方法的内部实现都是仅仅调用了一下所包装的的 request 对象的对应方法
		public Enumeration getAttributeNames() {
			return this.request.getAttributeNames();
		} 
		
		可对想要修改 HttpServletRequest 或者 HttpServletResponse方法时 进行重写

}

17.Listener{
	1.ServletContextListener
		应用被servlet容器加载时创建，应用被servlet容器销毁时销毁
		主要作用:初始化应用的参数，加载数据库，加载springIOC容器
		需在web.xml中配置
		实现ServletContextListener接口
		public class ServletContextTest implements ServletContextListener {
			public void contextDestroyed(ServletContextEvent arg0)  { 
				System.out.println("contextDestroyed destroy...");
			}
			public void contextInitialized(ServletContextEvent arg0)  { 
				System.out.println("contextInitialized init...");
			}
		}
		
		在web.xml中配置
		<listener>
			<listener-class>com.wei.listener.ServletContextTest</listener-class>
		</listener>
	2.ServletRequestListener
		发一个请求时被调用，响应后被销毁
		需在web.xml中配置
		@Override
		public void requestDestroyed(ServletRequestEvent arg0) {
			System.out.println("requestDestroyed");
		}

		@Override
		public void requestInitialized(ServletRequestEvent arg0) {
			System.out.println("requestInitialized");
		}
	3.HttpSessionListener
		session被创建时调用，session被销毁后调用
		需在web.xml中配置
		@Override
		public void sessionCreated(HttpSessionEvent arg0) {
			System.out.println("sessionCreated");
		}

		@Override
		public void sessionDestroyed(HttpSessionEvent arg0) {
			System.out.println("sessionDestroyed");
		}
}

18.文件上传{
	1.jsp需注意一下点
		enctype默认 application/x-www-form-urlencoded，enctype=“multipart/form-data”，表示表单以二进制传输数据 
		<form action="UploadServlet" method="get" enctype="multipart/form-data">
		File:<input type="file" name="file"/>
	
	2.服务器端接收文件
	public class UploadServlet extends HttpServlet {
		private static final long serialVersionUID = 1L;

		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
			//判断本次请求是否是二进制方式传递信息
			boolean multipartContent = ServletFileUpload.isMultipartContent(request);
			System.out.println(multipartContent);
			
			DiskFileItemFactory factory = new DiskFileItemFactory();
			//内存文件存储大小，超出则写入到磁盘临时文件下
			factory.setSizeThreshold(1024*500);
			
			//缓存的临时文件
			File tempFile = new File("d:\\tempFile");
			factory.setRepository(tempFile);
			
			ServletFileUpload upload = new ServletFileUpload(factory);
			
			//可接受的文件的总大小 不能超过5M
			upload.setFileSizeMax(1024*1024*5);
			
			try {
				List<FileItem> parseRequest = upload.parseRequest(request);
				for(FileItem item:parseRequest) {
					//判断是否是表单域
					if(item.isFormField()) {
						String fieldName = item.getFieldName();
						String string = item.getString();
						System.out.println(fieldName +" --- "+string);
					}else {//文件域
						String fieldName  = item.getFieldName();
						String fileName = item.getName();
						String contentType = item.getContentType();
						long size = item.getSize();
						boolean inMemory = item.isInMemory();
						
						System.out.println("fieldName = "+fieldName);
						System.out.println("fileName = "+fileName);
						System.out.println("contentType = "+contentType);
						System.out.println("size = "+size);
						System.out.println("inMemory = "+inMemory);
						
						InputStream in = item.getInputStream();
						
						byte[] bs = new byte[1024];
						int len = 0;
						
						fileName = "d:\\files\\"+fileName;
						System.out.println("fileName = "+fileName);
						OutputStream os = new FileOutputStream(fileName);
						
						while((len= in.read(bs))!= -1) {
							os.write(bs, 0, len);
						}
						os.close();
						in.close();
						
					}
				}
			} catch (FileUploadException e) {
				e.printStackTrace();
			}
		
		}

	}
	
}

19.国际化{
	1.Locale
		@Test
		public void testLocale() {
			/*
			 * 1.Locale 表示国家和地区的类
			 * 2.在web应用中可以通过request.getLocale()获取
			 */
			//1.第一种创建Locale的方式
			Locale locale = Locale.CHINA;
			System.out.println(locale.getDisplayCountry());
			System.out.println(locale.getLanguage());
			
			//第二种创建方式
			locale = new Locale("en", "US");
			System.out.println(locale.getDisplayCountry());
			System.out.println(locale.getLanguage());
		}
	2.DateFormat
		@Test
		public void testDateFormat() { 
			/*
			 * 1. 若只希望通过 DateFormat 把一个 Date 对象转为一个字符串, 则可以通过 DateFormat 的工厂方法来获取 DateFormat 对象
			 * 2. 可以获取只格式化 Date 的 DateFormat 对象: getDateInstance(int style, Locale aLocale) 
			 * 3. 可以获取只格式化 Time 的 DateFormat 对象: getTimeInstance(int style, Locale aLocale) 
			 * 4. 可以获取既格式化 Date, 也格式化 Time 的 DateFormat 对象: 
			 * getDateTimeInstance(int dateStyle, int timeStyle, Locale aLocale) 
			 * 5. 其中 style 可以取值为: DateFormat 的常量: SHORT, MEDIUM, LONG, FULL. Locale 则为代表国家地区的 Locale 对象
			 * 6. 通过 DateFormat 的 format 方法来格式化个 Date 对象到字符串. 
			 * 
			 * 7. 若有一个字符串, 如何解析为一个 Date 对象呢 ? 
			 * I. 先创建 DateFormat 对象: 创建 DateFormat 的子类 SimpleDateFormat 对象
			 * SimpleDateFormat(String pattern). 
			 * 其中 pattern 为日期, 时间的格式, 例如: yyyy-MM-dd hh:mm:ss
			 * II. 调用 DateFormat 的 parse 方法来解析字符串到 Date 对象.  
			 */
			Date date = new Date();
			Locale locale = Locale.CHINA;
			
			//2019-1-28 14:33:54
			DateFormat instance = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, locale);
			System.out.println(instance.format(date));
			
			//2019年1月28日 下午02时33分54秒
			instance = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
			System.out.println(instance.format(date));
			
			//2019年1月28日 星期一 下午02时33分54秒 CST
			instance = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, locale);
			System.out.println(instance.format(date));
		}
		@Test
		public void testDateFormat2() throws ParseException {
			//将字符串转化为date类型
			String str = "1992-08-16 08:16:00";
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date parse = dateFormat.parse(str);
			//Sun Aug 16 08:16:00 CST 1992
			System.out.println(parse);
		}
	3.NumberFormat
		/**
		 * NumberFormat: 格式化数字到数字字符串, 或货币字符串的工具类
		 * 1. 通过工厂方法获取 NumberFormat 对象
		 * NumberFormat.getNumberInstance(locale); //仅格式化为数字的字符串
		 * NumberFormat.getCurrencyInstance(locale); //格式为货币的字符串
		 * 
		 * 2. 通过 format 方法来进行格式化
		 * 3. 通过 parse 方法把一个字符串解析为一个 Number 类型. 
		 */
		@Test
		public void testNumberFormat() throws ParseException {
			double d = 123456789.543d;
			Locale locale = Locale.US;
			NumberFormat format = NumberFormat.getNumberInstance(locale);
			String format2 = format.format(d);
			System.out.println(format2);
			NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(locale);
			String format3 = currencyInstance.format(d);
			System.out.println(format3);
			
			double parse = (double) currencyInstance.parse(format3);
			System.out.println(parse);
		}
	4.MessageFormat
		@Test
		public void testMessageFormat() {
			//占位符
			String str = "Date: {0}, Salary: {1}";
			
			Locale locale = Locale.CHINA;
			
			Date date = new Date();
			double sal = 12345.12;
			
			DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
			String dateStr = dateFormat.format(date);
			
			NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
			String salStr = numberFormat.format(sal);
			
			String result = MessageFormat.format(str, dateStr, salStr);
			System.out.println(result); 
		}
	
}

20.cookie和session的区别{
	1、cookie数据存放在客户的浏览器上，session数据放在服务器上。
	2、cookie不是很安全，别人可以分析存放在本地的cookie并进行cookie欺骗，考虑到安全应当使用session。
	3、session会在一定时间内保存在服务器上。当访问增多，会比较占用你服务器的性能，考虑到减轻服务器性能方面，应当使用cookie。
	4、单个cookie保存的数据不能超过4K，很多浏览器都限制一个站点最多保存20个cookie。
	5、可以考虑将登陆信息等重要信息存放为session，其他信息如果需要保留，可以放在cookie中。
}


