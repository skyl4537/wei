# JavaWeb三大组件

​	servlet、Filter、Listener

# servlet

```java
import javax.servlet.Servlet
```

Servlet是用来处理客户端请求的动态资源。

## servlet任务

​	1.接收请求数据

​	2.处理请求

​	3.完成响应

## serlvet配置

```xml
<servlet>
    <servlet-name>springMVC</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>classpath*:config/spring-mvc.xml</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
  </servlet>
```

serlvet的创建时机与<load-on-startup>有关，若为负数, 则在第一次请求时被创建.若为 0 或正数, 则在当前 WEB 应用被Serlvet 容器加载时创建实例, 且数组越小越早被创建。

## servlet-mapping

​	一个<servlet>可以映射多个<servlet-mapping>
​	<url-pattern> 一种格式是“*****.扩展名”，另一种格式是以正斜杠（/）开头并以'/*'结尾

## 给servlet添加初始化参数值

```xml
在web.xml，servlet节点下添加子节点
	<init-param>
  		<param-name>user</param-name>
  		<param-value>sa</param-value>
  	</init-param>
	<init-param>
  		<param-name>password</param-name>
  		<param-value>12345</param-value>
  	</init-param>
```

## 获取servlet初始化值

在servlet实现类中获取初始化值

### 根据name键值获取

```java
	String user = servletConfig.getInitParameter("user");
	String password = servletConfig.getInitParameter("password");
```

### 获取name键值数组

```java
Enumeration<String> initParameterNames = servletConfig.getInitParameterNames();
		while(initParameterNames.hasMoreElements()) {
			String name = initParameterNames.nextElement();
			String value = servletConfig.getInitParameter(name);
			System.out.println("while name = "+name+" ; value = "+value);
		}
```

## Servlet的生命周期方法

```java
public void init(ServletConfig config) throws ServletException {}
```

servlet的初始化方法，只在创建servlet实例时候调用一次，Servlet是单例的，整个服务器就只创建一个同类型Servlet。

```java
public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {}
```

servlet的处理请求方法，servlet被请求则调用service()方法，每请求一次则调用一次该方法

```java
public void destroy() {}
```

servlet销毁之前执行的方法，只执行一次，用于释放servlet占有的资源

Servlet 生命周期

```java
由Serlvet容器负责调用
1.构造器()
2.init()
3.service()
4.destroy()
```
##重要参数

### ServletConfig

封装了 Serlvet 的配置信息, 并且可以获取 ServletContext 对象

### servletContext

当前应用上下文，应用全局参数

给应用配置全局参数，在web.xml，添加context-param子节点

```xml
<context-param>
		<param-name>jdbc</param-name>
		<param-value>mysql</param-value>
	</context-param>
	<context-param>
		<param-name>driver</param-name>
		<param-value>com.mysql.jdbc.Driver</param-value>
	</context-param>
```

在servlet容器应用启动时，初始化参数，即tomcat启动时，初始化应用上下文参数

获取应用上下文方法

```java
ServletContext servletContext = servletConfig.getServletContext();
```

获取当前web应用名称

```java
String contextPath = servletContext.getContextPath();
```

获取应用上下文初始化参数

```java
String user = servletContext.getInitParameter("user");
Enumeration<String> initParameterNames = servletContext.getInitParameterNames();
```

获取当前web应用的某个文件在服务器上的绝对路径 

```java
String realPath = servletContext.getRealPath("/note.txt");
```

获取类配置文件

类路径的相对路径

```java
ClassLoader classLoader = getClass().getClassLoader();
		InputStream resourceAsStream = classLoader.getResourceAsStream("jdbc.properties");
```

web应用程序的相对路径

```java
InputStream resourceAsStream2 = servletContext.getResourceAsStream("/WEB-INF/classes/jdbc.properties");
		System.out.println("2."+resourceAsStream2);
```

## service()相关

根据请求参数的名字，返回请求参数的数组

```java
String[] parameterValues = req.getParameterValues("interesting");
		for (String interesting : parameterValues) {
			System.out.println("####"+"interesting = "+interesting);
		}
```

获取请求参数的Map

```java
Map<String, String[]> parameterMap = req.getParameterMap();
		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			System.out.println("****"+entry.getKey()+":"+Arrays.asList(entry.getValue()));
		}
```

HttpServletRequest: 是 SerlvetRequest 的子接口. 针对于 HTTP 请求所定义.http请求可进行强转

```java
HttpServletRequest httpServlet = (HttpServletRequest) req;
```

获取到当前应用的相对访问路径

```java
//输出结果为：/helloworld/loginServlet  /helloworld为项目
		String requestURI = httpServlet.getRequestURI();
		System.out.println("requestURI = "+requestURI);
```

获取当前访问的整个路径

```java
//输出结果为：http://localhost:8080/helloworld/loginServlet 
		StringBuffer requestURL = httpServlet.getRequestURL();
		System.out.println("requestURL = "+requestURL.toString());
```

获取当前访问的请求参数

```java
//获取当前访问的请求参数    get请求 获取到？后的字符串 ennn=a&k=jjj
		String queryString = httpServlet.getQueryString();
		System.out.println("queryString = "+queryString);
```

获取当前访问的路径

```java
// 输出结果为：/loginServlet
		String servletPath = httpServlet.getServletPath();
		System.out.println("servletPath = "+servletPath);
```

ServletResponse

可设置响应内容类型

```java
response.setContentType("application/msword");
```

# Filter

Filter是JavaWEB 的一个重要组件, 可以对发送到 Servlet 的请求/响应进行拦截.Filter也为单例，整个服务器就只创建一个同类型Filter

## filter任务

对发送到 Servlet 的请求/响应进行拦截

## filter配置

web.xml 文件中配置与servlet类似

```xml
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
			<url-pattern>/filter/*</url-pattern> 
		</filter-mapping>
```

## Filter 相关的 API

```java
public void init(FilterConfig arg0) ()
```

servlet容器加载应用时调用一次

```java
public void destroy() 
```

servlet容器关闭应用时调用一次

```java
public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
```

拦截后在此方法内做逻辑处理

FilterChain Filter 链. 多个 Filter 可以构成一个 Filter 链，doFilter(ServletRequest request, ServletResponse response): 把请求传给 Filter 链的下一个 Filter，该方法为同步方法。若当前 Filter 是 Filter 链的最后一个 Filter, 将把请求给到目标 Serlvet(或 JSP)。

多个 Filter 拦截的顺序和 <filter-mapping> 配置的顺序有关, 靠前的先被调用. 

## filter-mapping

<filter-name>指定被拦截的Filter的名称

<dispatcher> 指定被拦截的请求方式  默认request

​	request 通过get post 请求方式

​	include 通过页面包含的方式

​	forward 请求转发的方式

​	error 错误页面的方式

```xml
              <filter-mapping>
				<filter-name>HelloFilter</filter-name>
				<url-pattern>/filter/test.jsp</url-pattern>
				<dispatcher>FORWARD</dispatcher>
				<dispatcher>REQUEST</dispatcher>
			  </filter-mapping>
```

可以设置多个dispatcher设置指定filter对资源多种调用方式的拦截

对一个filter可以有多个filter-mapping的映射

## 案例

### 不缓存页面的过滤器

```java
@Override
			public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
					throws IOException, ServletException {
				response.setDateHeader("Expires", -1);
				response.setHeader("Cache-Control", "no-cache");
				response.setHeader("Pragma", "no-cache");
				
				chain.doFilter(request, response);
			}
```

### 配置编码格式

web.xml中配置

```xml
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
				<url-pattern>/encode/*</url-pattern>
			  </filter-mapping>
```

实现

```java
@Override
			public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
					throws IOException, ServletException {
					String encoding = getFilterConfig().getServletContext().getInitParameter("encoding");
					request.setCharacterEncoding(encoding);
					chain.doFilter(request, response);
			}
```

## HttpServletRequestWrapper

​	HttpServletRequest包装类，对HttpServletRequest对象值进行修改时可以使用extends HttpServletRequestWrapper类来进行对HttpServletRequest 对象中的数据进行重写，当修改HttpServletRequest 中 param参数时可使用此方式

​	HttpServletResponseWrapper类似

# Listener

监听器

## ServletContextListener

应用被servlet容器加载时创建，应用被servlet容器销毁时销毁

主要作用:初始化应用的参数，加载数据库，加载springIOC容器

需在web.xml中配置

```xml
<listener>
	<listener-class>com.wei.listener.ServletContextTest</listener-class>
</listener>
```



实现ServletContextListener接口

```java
public class ServletContextTest implements ServletContextListener {
			public void contextDestroyed(ServletContextEvent arg0)  { 
				System.out.println("contextDestroyed destroy...");
			}
			public void contextInitialized(ServletContextEvent arg0)  { 
				System.out.println("contextInitialized init...");
			}
		}
```

## ServletRequestListener

当发一个请求时被调用，响应后被销毁

覆写方法

```java
		@Override
		public void requestDestroyed(ServletRequestEvent arg0) {
			System.out.println("requestDestroyed");
		}

		@Override
		public void requestInitialized(ServletRequestEvent arg0) {
			System.out.println("requestInitialized");
		}
```

## HttpSessionListener

session被创建时调用，session被销毁后调用

覆写方法

```java
        @Override
		public void sessionCreated(HttpSessionEvent arg0) {
			System.out.println("sessionCreated");
		}

		@Override
		public void sessionDestroyed(HttpSessionEvent arg0) {
			System.out.println("sessionDestroyed");
		}
```

# JSP

jsp页面应放在应用程序除WEB-INF以外的路径下.

## 在jsp页面中写java 代码

要在body节点下<% %>中写

在jsp头部添加引入

```jsp
<%@page  import="java.util.Date" %>
	<%
		Date date = new Date();
		System.out.println(date);
	%>
```

## jsp隐含变量

隐含变量：可以不用在代码中显示的声明该变量

| 变量        | 说明                                                         |
| ----------- | ------------------------------------------------------------ |
| request     | HttpServletRequest 的一个对象                                |
| response    | HttpServletResponse 的一个对象                               |
| pageContext | 页面的上下文, 是 PageContext 的一个对象. 可以从该对象中获取到其他 8 个隐含对象. 也可以从中获取到当前页面的其他信息. |
| session     | 代表浏览器和服务器的一次会话, 是 HttpSession 的一个对象      |
| application | 代表当前 WEB 应用. 是 ServletContext 对象                    |
| config      | 当前 JSP 对应的 Servlet 的 ServletConfig 对象. 若需要访问当前 JSP 配置的初始化参数, 需要通过映射的地址. |
| out         | JspWriter 对象. 调用 out.println() 可以直接把字符串打印到浏览器上 |
| page        | 指向当前 JSP 对应的 Servlet 对象的引用, 但为 Object 类型, 只能调用 Object 类的方法 |
| exception   | 在声明了 page 指令的 isErrorPage="true" 时, 才可以使用       |

映射 JSP

```xml
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
```

pageContext, request, session, application(对属性的作用域的范围从小到大)

## jsp表达式

<%= date %>可直接输出变量到浏览器

<%! %>内可声明方法

## setAttribute

```jsp
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
```

## jsp指令

### jsp指令基本语法格式

<%@ 指令 属性="值" 属性="值" 属性="值"......%>

**属性名大小写敏感**

指令包含 page include taglib三个指令

### page指令

page指令 用于配置页面的各种属性，作用于整个jsp页面，最好放在jsp页面的起始位置

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8"
		pageEncoding="UTF-8"%>
```

#### import属性

import属性 可以导入使用的类，一些隐含的包可以不用引入

```java
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
```

#### session属性

session属性 session="true|false" 当前页面是否允许使用隐含session，访问当前页面是否产生HTTPSession对象

#### errorPage

跳转错误页面是请求转发的机制

errorPage="错误页面当前页面的相对路径"，当前页面出现错误的实际响应页面 /表示当前web应用的根目录

#### isErrorPage

isErrorPage="true|false"，指定当前页面是否为错误处理页面，即是否可以使用隐含对象exception，建议将该页面放入WEB-INF路径下，不可直接访问

#### contentType

指定当前jsp页面的响应类型和字符编码，实际调用的是 response.setAttribute("text/html; charset=UTF-8");

#### pageEncoding

指定当前jsp页面的字符编码，与contentType的编码格式相同

#### isELIgnored

指定当前页面是否可使用EL表达式

在web.xml中配置错误页面两种方式:

```xml
<!-- 根据errorcode指定错误代码访问的错误页面 /表示当前web应用的根目录 -->
		 <error-page>
			<error-code>404</error-code>
			<location>/WEB-INF/error/error.jsp</location>
		  </error-page>
		  <!--根据错误类型指定访问错误页面 -->
		  <error-page>
			<exception-type>java.lang.ArithmeticException</exception-type>
			<location>/WEB-INF/error/error.jsp</location>
		  </error-page>
```

### include指令

静态包含 

file="相对路径"，/表示当前web应用的根目录，只生成一个 jsp对应的java文件，即源码包含

<%@ include file="b.jsp" %> 在a.jsp 声明的变量 在b.jsp中可以直接使用。

## <jsp:标签>

### jsp:include

动态包含，生成两个jsp对应的java文件。

```jsp
<jsp:include page="b.jsp"></jsp:include>
```

具体实现:

```java
org.apache.jasper.runtime.JspRuntimeLibrary.include(request, response, "b.jsp", out, false);
```

### jsp:forward

页面重定向

```jsp
<jsp:forward page="b.jsp"></jsp:forward>
```

相当于

```java
request.getRequestDispatcher("/testServlet").forward(request, response);
```

### jsp:param

```jsp
<jsp:param value="AAAAParam" name="user"/>
```

在<jsp:forward>和<jsp:include> 都可使用

```jsp
<jsp:forward page="b.jsp">
	<jsp:param value="AAAAParam" name="user"/>
</jsp:forward>
			
b.jsp 获取参数 <%=request.getParameter("user") %>	
```

# cookie

cookie跟踪会话的一种方式，cookie默认是会话级别的，在客户端保存信息

