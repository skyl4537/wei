
///----------------<<<<常用工具类>>>>--------------------------------------------
#常用工具
	public class SystemUtil {
		//获取项目class路径
		public static String getClassPath() throws FileNotFoundException {
			// ClassUtils.getDefaultClassLoader().getResource("").getPath();//等同
			return ResourceUtils.getURL("classpath:").getPath();
		}

		//获取项目根路径
		public static String getFilePath() throws FileNotFoundException {
			return ResourceUtils.getURL("").getPath();
		}
		
		//系统换行
		public static String newline() {
			return System.getProperty("line.separator");
		}
	}
	
#常用解析
	0.org.apache.commons.lang3.StringUtils
		isEmpty(); //为空判断标准是: str==null 或 str.length()==0
		isBlank(); //在isEmpty()的基础上增加: 制表符,换行符,换页符,回车符...等

	1.保留两位有效小数
		double num = 12.1250/12.1251;
		String num0 = String.format("%.2f", num);// 12.13/12.13
		String num1 = new DecimalFormat("#0.00").format(num);// 12.12/12.13
		
		//DecimalFormat特殊字符说明
		//	"0"指定位置不存在数字则显示为0: 123.123 ->0000.0000 ->0123.1230
		//	"#"指定位置不存在数字则不显示: 123.123 ->####.####  ->123.123
		//	"."小数点
		//	"%"会将结果数字乘以100 后面再加上% 123.123 ->#.00%  ->12312.30%

	2.使用占位符拼接字符串
		String domain = "www.baidu.com";  
		int iVisit = 0;  

		syso(String.format("该域名%s被访问了%s次.", domain , iVisit));
		syso(MessageFormat.format("该域名{0}被访问了{1}次.", domain , iVisit)); 
	
#通过类目获取类的对象
	@Component
	public class MyApplicationContextAware implements ApplicationContextAware {// 获取bean的工具类

		private static ApplicationContext context;

		// 实现ApplicationContextAware接口的回调方法，设置上下文环境
		@Override
		public void setApplicationContext(ApplicationContext context) throws BeansException {
			MyApplicationContextAware.context = context;
		}

		// 获取applicationContext
		public static ApplicationContext getApplicationContext() {
			return context;
		}

		// 通过name获取Bean.
		public static Object getBean(String name) {
			return context.getBean(name);

		}

		// 通过clazz获取Bean.
		public static <T> T getBean(Class<T> clazz) {
			return context.getBean(clazz);
		}

		// 通过name及clazz返回指定的Bean
		public static <T> T getBean(String name, Class<T> clazz) {
			return context.getBean(name, clazz);
		}
	}
	
#fastjson
	<dependency>
		<groupId>com.alibaba</groupId>
		<artifactId>fastjson</artifactId>
		<version>1.2.47</version>
	</dependency>

	/**
	 * QuoteFieldNames---输出key时是否使用双引号,默认为true
	 * WriteMapNullValue---是否输出值为null的字段,默认为false
	 * WriteNullNumberAsZero-—-数值字段如果为null,输出为0,而非null
	 * WriteNullListAsEmpty—–-List字段如果为null,输出为[],而非null
	 * WriteNullStringAsEmpty--—字符类型字段如果为null,输出为"",而非null (√)
	 * WriteNullBooleanAsFalse--–Boolean字段如果为null,输出为false,而非null
	 */
	JSON.toJSONString(list, SerializerFeature.WriteNullStringAsEmpty); //list -> JSONString
	
	#Demo
		JSONObject json = new JSONObject();
		System.out.println(json.getInteger("a")); //null
		System.out.println(json.getIntValue("a")); //0
	
	#X->String
		String json = JSON.toJSONString(list/map/bean);
		String json = JSON.toJSONString(list, true);//args1: json是否格式化(有空格和换行).
	
	#X->JSON (必须有get/set)
		JSONObject obj = JSON.parseObject(JSON.toJSONString(person));//javabean
		JSONObject obj = JSON.parseObject(JSON.toJSONString(map));//map
		JSONArray array = JSON.parseArray(JSON.toJSONString(list));//list

	#JSON->X (必须有空构造方法)
		Person person = JSON.parseObject(json, Person.class);
		Map map = JSON.parseObject(json, Map.class);
		List<Person> list = JSON.parseArray(json, Person.class);
	





















































































































///------------------<<<<HttpClient>>>>-------------------------------------------------------------
///HTML超链接<a/>只能用GET方式提交HTTP请求; HTML表单</form>则可以使用GET,POST两种方式提交HTTP请求.
#html表单
	//表单中存在各种类型的表单域标签, 如<input/>,<textarea/>及<select/>.
	//每一种表单域标签均有NAME与VALUE两种标签属性。这两个标签属性决定了表单提交时传送的属性名及相应的值。
	<form action="目标地址" method="发送方式" enctype="数据主体的编码方式">  
	    <!-- 各类型的表单域 -->  
	    <input name="NAME" value="VALUE"/>  
	    <textarea name="NAME">VALUE</textarea>  
	    <select name="NAME">  
	        <option value="VALUE" selected="selected"/>  
	    </select>  
	</form>
	
	//...待续
	https://blog.csdn.net/darxin/article/details/4944225

///"HTTP请求格式" 有两个位置或者说两种方式为request提供参数: request-line, request-body
#request-line -> 在请求行上通过URI直接提供参数.
	
	1在生成request对象时提供带参数的URI
		HttpUriRequest request = new HttpGet("http://localhost/index.html?p1=v1&p2=v2");  

	2.通过 URIUtils 工具类生成带参数的URI
		URI uri = URIUtils.createURI("http", "localhost", -1, "/index.html", "p1=v1&p2=v2", null);
		HttpUriRequest request = new HttpGet(uri);

	3.如果参数中含有中文需将参数进行URLEncoding处理
		String param = "p1=" + URLEncoder.encode("中国", "UTF-8") + "&p2=v2";
		URI uri = URIUtils.createURI("http", "localhost", 8080, "/sshsky/index.html", param, null);
		
	4.也可以使用HttpClient提供的工具类URLEncodedUtils进行参数的URLEncoding处理
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("p1", "中国"));
		params.add(new BasicNameValuePair("p2", "v2"));
		String param = URLEncodedUtils.format(params, "UTF-8");
		URI uri = URIUtils.createURI("http", "localhost", 8080, "/sshsky/index.html", param, null);
		
#request-body -> 在request-body中提供参数,只能用于POST请求.

	1.默认的HTML表单"application/x-www-form-urlencoded"
		<form action="http://localhost/index.html" method="POST">  
			<input type="text" name="p1" value="中国"/>  
			<input type="text" name="p2" value="v2"/>  
			<inupt type="submit" value="submit"/>  
		</form>

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("p1", "中国"));
		nvps.add(new BasicNameValuePair("p2", "v2"));
		HttpEntity entity = new UrlEncodedFormEntity(nvps, "UTF-8");

		HttpPost request = new HttpPost("http://localhost/index.html");
		request.setEntity(entity);
		
		//查看HTTP数据格式
		syso(entity.getContentType()); //Content-Type: application/x-www-form-urlencoded; charset=UTF-8
		syso(entity.getContentLength()); //39
		syso(EntityUtils.getContentCharSet(entity)); //UTF-8
		syso(EntityUtils.toString(entity)); //p1=%E4%B8%AD%E5%9B%BD&p2=v2
		// Content-Type: application/x-www-form-urlencoded; charset=UTF-8
		// 28
		// UTF-8
		// p2=v2&p1=%E4%BA%ACABC567

	2.上传文件的表单"multipart/form-data"
		<form action="http://localhost/index.html" method="POST" enctype="multipart/form-data">  
			<input type="text" name="p1" value="中国"/>  
			<input type="text" name="p2" value="v2"/>  
			<input type="file" name="p3"/>  
			<inupt type="submit" value="submit"/>  
		</form>  

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		//对于中文,必须更改编码格式,默认'ISO-8859-1'
		ContentType contentType = ContentType.create("text/plain", Charset.forName("UTF-8"));
		builder.addTextBody("p1", "中国", contentType);
		builder.addTextBody("p2", "v2", contentType);
		builder.addBinaryBody("dubbo_architecture.png", file);
		HttpEntity entity = builder.build();

		HttpPost request = new HttpPost("http://localhost/index.html");
		request.setEntity(entity);
		
		//查看HTTP数据格式
		System.out.println(entity.getContentType());
		System.out.println(entity.getContentLength());
		System.out.println(EntityUtils.getContentCharSet(entity));
		System.out.println(EntityUtils.toString(entity));
		// Content-Type: multipart/form-data; boundary=oMPnaLNLR4gWNfSSOLiBxnG9Jr3K3RNq9
		// 16130
		// null
		// --oMPnaLNLR4gWNfSSOLiBxnG9Jr3K3RNq9
		// Content-Disposition: form-data; name="p1"
		// Content-Type: text/plain; charset=UTF-8
		// Content-Transfer-Encoding: 8bit

		// 中国
		// --oMPnaLNLR4gWNfSSOLiBxnG9Jr3K3RNq9
		// Content-Disposition: form-data; name="p2"
		// Content-Type: text/plain; charset=UTF-8
		// Content-Transfer-Encoding: 8bit

		// v2
		// --oMPnaLNLR4gWNfSSOLiBxnG9Jr3K3RNq9
		// Content-Disposition: form-data; name="dubbo_architecture.png"; filename="dubbo_architecture.png"
		// Content-Type: application/octet-stream
		// Content-Transfer-Encoding: binary

		// PNG	......	

#HTTP响应
		try (CloseableHttpResponse hr = HttpClients.createDefault().execute(request)) {
			if (null != hr && HttpStatus.SC_OK == hr.getStatusLine().getStatusCode()) {
				//从response中取出HttpEntity对象
				String res = EntityUtils.toString(hr.getEntity(), "UTF-8");
			}
		}

#完整版
	//是否传输二进制流? 两种解决方案!
	public static String postBody(@NotNull String uri, Map<String, String> map, List<File> list) throws Exception {
		String res = "";

		HttpEntity entity;
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();// multipart/form-data
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();// application/x-www-form-urlencoded

		if (null != list && list.size() > 0) {// (1).传输二进制流
			if (null != map && map.size() > 0) {
				// Content-Type: text/plain; charset=UTF-8
				ContentType contentType = ContentType.create("text/plain", Charset.forName("UTF-8"));
				for (Entry<String, String> entry : map.entrySet()) {
					builder.addTextBody(entry.getKey(), entry.getValue(), contentType);
				}
			}
			list.stream().forEach(x -> builder.addBinaryBody(x.getName(), x));
			entity = builder.build();
		} else {// (2).不传输二进制流
			if (null != map && map.size() > 0) {
				for (Entry<String, String> entry : map.entrySet()) {
					nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			entity = new UrlEncodedFormEntity(nvps, "UTF-8");// charset=UTF-8
		}

		HttpPost hp = new HttpPost(uri);
		hp.setEntity(entity);

		System.out.println(entity.getContentType());
		System.out.println(entity.getContentLength());
		System.out.println(EntityUtils.getContentCharSet(entity));
		System.out.println(EntityUtils.toString(entity));

		try (CloseableHttpResponse hr = HttpClients.createDefault().execute(hp)) {
			if (null != hr && HttpStatus.SC_OK == hr.getStatusLine().getStatusCode()) {
				// 从response中取出HttpEntity对象
				res = EntityUtils.toString(hr.getEntity(), "UTF-8");
			}
		}
		return res;
	}























	