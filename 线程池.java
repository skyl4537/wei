1.ThreadPoolExecutor类构造器{
	public class ThreadPoolExecutor extends AbstractExecutorService {
		.....
		public ThreadPoolExecutor(int corePoolSize,int maximumPoolSize,long keepAliveTime,TimeUnit unit,
				BlockingQueue<Runnable> workQueue);
	 
		public ThreadPoolExecutor(int corePoolSize,int maximumPoolSize,long keepAliveTime,TimeUnit unit,
				BlockingQueue<Runnable> workQueue,ThreadFactory threadFactory);
	 
		public ThreadPoolExecutor(int corePoolSize,int maximumPoolSize,long keepAliveTime,TimeUnit unit,
				BlockingQueue<Runnable> workQueue,RejectedExecutionHandler handler);
	 
		public ThreadPoolExecutor(int corePoolSize,int maximumPoolSize,long keepAliveTime,TimeUnit unit,
			BlockingQueue<Runnable> workQueue,ThreadFactory threadFactory,RejectedExecutionHandler handler);
		...
	}
	
	2.参数说明{
		corePoolSize:默认情况下,在创建了线程池后，线程池中的线程数为0,task1来请求,会创建一个线程去执行任务，当线程池中的线程数目达到corePoolSize后，
				就会把到达的任务放到缓存队列当中；
		maximumPoolSize:在线程池中最多能创建多少个线程
		keepAliveTime：表示线程没有任务执行时最多保持多久时间会终止。默认情况下，只有当线程池中的线程数大于corePoolSize时，keepAliveTime才会起作用。
						但是如果调用了allowCoreThreadTimeOut(boolean)方法，在线程池中的线程数不大于corePoolSize时，keepAliveTime参数也会起作用，直到线程池中的线程数为0；

		workQueue：阻塞队列，用来存储等待执行的任务。一般使用LinkedBlockingQueue和SynchronousQueue
		threadFactory：线程工厂，主要用来创建线程
		handler：表示当拒绝处理任务时的策略，有以下四种取值：
					ThreadPoolExecutor.AbortPolicy:丢弃任务并抛出RejectedExecutionException异常。(常用) 
					ThreadPoolExecutor.DiscardPolicy：也是丢弃任务，但是不抛出异常。 
					ThreadPoolExecutor.DiscardOldestPolicy：丢弃队列最前面的任务，然后重新尝试执行任务（重复此过程）
					ThreadPoolExecutor.CallerRunsPolicy：由调用线程处理该任务 
	}
}

2.继承关系{
	ThreadPoolExecutor extends AbstractExecutorService 、AbstractExecutorService implements ExecutorService、 ExecutorService extends Executor
	public interface Executor {
		void execute(Runnable command);
	}
	
	execute()方法	在ThreadPoolExecutor进行了具体的实现，通过这个方法可以向线程池提交一个任务，交由线程池去执行。
	submit()方法	在AbstractExecutorService进行具体的实现，这个方法也是用来向线程池提交任务的，实际上是调用的execute()方法，它利用了Future来获取任务执行结果

}

3.线程池状态{
	RUNNING    创建线程池初始状态
	SHUTDOWN   调用了shutdown()方法，此时线程池不能够接受新的任务，它会等待所有任务执行完毕
	STOP	   调用了shutdownNow()方法，此时线程池不能接受新的任务，并且会去尝试终止正在执行的任务；
	TERMINATED 当线程池处于SHUTDOWN或STOP状态，并且所有工作线程已经销毁，任务缓存队列已经清空或执行结束后，线程池被设置为TERMINATED状态
}	

4.比较重要成员变量{
	private final BlockingQueue<Runnable> workQueue;              //任务缓存队列，用来存放等待执行的任务
	private final ReentrantLock mainLock = new ReentrantLock();   //线程池的主要状态锁，对线程池状态（比如线程池大小
																  //、runState等）的改变都要使用这个锁
	private final HashSet<Worker> workers = new HashSet<Worker>();  //用来存放工作集
	 
	private volatile long  keepAliveTime;    //线程存货时间   
	private volatile boolean allowCoreThreadTimeOut;   //是否允许为核心线程设置存活时间
	private volatile int   corePoolSize;     //核心池的大小（即线程池中的线程数目大于这个参数时，提交的任务会被放进任务缓存队列）
	private volatile int   maximumPoolSize;   //线程池最大能容忍的线程数
	 
	private volatile int   poolSize;       //线程池中当前的线程数
	 
	private volatile RejectedExecutionHandler handler; //任务拒绝策略
	 
	private volatile ThreadFactory threadFactory;   //线程工厂，用来创建线程
	 
	private int largestPoolSize;   //用来记录线程池中曾经出现过的最大线程数
	 
	private long completedTaskCount;   //用来记录已经执行完毕的任务个数
}

4.任务缓存队列及排队策略{
	workQueue的类型为BlockingQueue<Runnable>，通常可以取下面三种类型：
　　1）ArrayBlockingQueue：基于数组的先进先出队列，此队列创建时必须指定大小；
　　2）LinkedBlockingQueue：基于链表的先进先出队列，如果创建时没有指定此队列大小，则默认为Integer.MAX_VALUE；
　　3）synchronousQueue：这个队列比较特殊，它不会保存提交的任务，而是将直接新建一个线程来执行新来的任务。
}

5.ThreadPoolExecutor运行原理{
	corePoolSize   maximumPoolSize   task(任务总数)  poolSize(当前线程总数)
	poolSize < corePoolSize 时， 当任务处理慢时，则创建新的线程处理新的task
	poolSize == corePoolSize 时，将(task-corePoolSize)的任务放入workQueue中，等待处理
	task > corePoolSize+workQueue 时,当新任务加入workQueue失败时(任务缓存队列已满),则将线程数增大至 maximumPoolSize来处理添加缓存失败的任务
	task > maximumPoolSize+workQueue 根据拒绝策略执行  
}

6.TCP与UDP区别{
	1、TCP面向连接（如打电话要先拨号建立连接）;UDP是无连接的，即发送数据之前不需要建立连接
	2、TCP提供可靠的服务。也就是说，通过TCP连接传送的数据，无差错，不丢失，不重复，且按序到达;UDP尽最大努力交付，即不保证可靠交付
	3、TCP面向字节流，实际上是TCP把数据看成一连串无结构的字节流;UDP是面向报文的
	UDP没有拥塞控制，因此网络出现拥塞不会使源主机的发送速率降低（对实时应用很有用，如IP电话，实时视频会议等）
	4、每一条TCP连接只能是点到点的;UDP支持一对一，一对多，多对一和多对多的交互通信
	5、TCP首部开销20字节;UDP的首部开销小，只有8个字节
	6、TCP的逻辑通信信道是全双工的可靠信道，UDP则是不可靠信道
}

7.SimpleDateFormat线程不安全问题{
	https://www.cnblogs.com/java1024/p/8594784.html
	原因：
		SimpleDateFormat继承了DateFormat,在DateFormat中定义了一个protected属性的 Calendar类的对象：calendar。只是因为Calendar类的概念复杂，
		牵扯到时区与本地化等等，Jdk的实现中使用了成员变量来传递参数，这就造成在多线程的时候会出现错误。
		
		源码:
		 private StringBuffer format(Date date, StringBuffer toAppendTo,
                                FieldDelegate delegate) {
				// Convert input date to time field list
			calendar.setTime(date); //设置时间，在多线程的情况下有问题
			boolean useDateFormatSymbols = useDateFormatSymbols();

				for (int i = 0; i < compiledPattern.length; ) {
					int tag = compiledPattern[i] >>> 8;
				int count = compiledPattern[i++] & 0xff;
				if (count == 255) {
				count = compiledPattern[i++] << 16;
				count |= compiledPattern[i++];
				}

				switch (tag) {
				case TAG_QUOTE_ASCII_CHAR:
				toAppendTo.append((char)count);
				break;

				case TAG_QUOTE_CHARS:
				toAppendTo.append(compiledPattern, i, count);
				i += count;
				break;

				default:
						subFormat(tag, count, delegate, toAppendTo, useDateFormatSymbols);
				break;
				}
			}
				return toAppendTo;
			}
	解决方案：
		package com.peidasoft.dateformat;

		import java.text.DateFormat;
		import java.text.ParseException;
		import java.text.SimpleDateFormat;
		import java.util.Date;

		public class ConcurrentDateUtil {

			private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>() {
				@Override
				protected DateFormat initialValue() {
					return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				}
			};

			public static Date parse(String dateStr) throws ParseException {
				return threadLocal.get().parse(dateStr);
			}

			public static String format(Date date) {
				return threadLocal.get().format(date);
			}
		}
		
	说明：使用ThreadLocal, 也是将共享变量变为独享，线程独享肯定能比方法独享在并发环境中能减少不少创建对象的开销。如果对性能要求比较高的情况下，一般推荐使用这种方法。
	
		
#三者关系: Thread, ThreadLocal, ThreadLocalMap
	// Thread 中有个 ThreadLocal.ThreadLocalMap 类型的成员变量 threadLocals
	public class Thread implements Runnable {
		ThreadLocal.ThreadLocalMap threadLocals = null;
	}
	
	// ThreadLocalMap 是 ThreadLocal 的内部类. 它是一个Map, 它的Key是ThreadLocal类型对象!!
	public class ThreadLocal<T> {
		public void set(T value) {
			Thread t = Thread.currentThread();
			ThreadLocalMap map = t.threadLocals; // 获取当前线程的 threadLocals 变量
			if (map != null) {
				map.set(this, value); // key -> ThreadLocal对象自身; value -> 局部变量
			} else {
				t.threadLocals = new ThreadLocalMap(this, value);
			}
		}
		
		public T get() {
			Thread t = Thread.currentThread();
			ThreadLocalMap map = t.threadLocals; //获取当前线程的 threadLocals 变量
			if (map != null) {
				ThreadLocalMap.Entry e = map.getEntry(this);
				if (e != null) {
					@SuppressWarnings("unchecked")
					T result = (T)e.value;
					return result;
				}
			}
			return null;
		}
		
		static class ThreadLocalMap /* <ThreadLocal<?>, Object> //自己加的,便于理解 */ { 
			//...
		}
	}
}

8.BIO与NIO{
	BIO：同步阻塞式IO，服务器实现模式为一个连接一个线程，即客户端有连接请求时服务器端就需要启动一个线程进行处理
	NIO：同步非阻塞式IO，服务器实现模式为一个请求一个线程，即客户端发送的连接请求都会注册到多路复用器上，
		 多路复用器轮询到连接有I/O请求时才启动一个线程进行处理。	
	
	各自应用场景
		（1）NIO适合处理连接数目特别多，但是连接比较短（轻操作）的场景，Jetty，Mina，ZooKeeper等都是基于java nio实现。
		（2）BIO方式适用于连接数目比较小且固定的场景，这种方式对服务器资源要求比较高，并发局限于应用中。
}

9.socket{
	1.client.setKeepAlive(true);
		服务端设置keeplive为true,当客户端没有发送任何数据过来，超过一个时间(看系统内核参数配置)，
		那么服务端会发送一个ack探测包发到对方，探测双方的TCP/IP连接是否有效(对方可能断点，断网)。
		如果不设置，那么客户端宕机时，服务器永远也不知道客户端宕机了，仍然保存这个失效的连接。
		建议开发者使用的另一种比keepalive更好的解决方案是修改超时设置套接字选项。
	2.client.setOOBInline(false);client.sendUrgentData(0);
		setOOBInline默认为false
		表示是否支持发送一个字节的TCP紧急数据
		为 false的这种情况下, 当接收方收到紧急数据时不作任何处理, 直接将其丢弃.
		sendUrgentData和setOOBInline配合使用
		用于发送一个字节的 TCP紧急数据
	
}

10.tomcat{
	Tomcat顶层架构
		一个Tomcat中只有一个Server，一个Server可以包含多个Service，一个Service只有一个Container，但是可以有多个Connectors，
		这是因为一个服务可以有多个连接，如同时提供Http和Https链接，也可以提供向相同协议不同端口的连接，
		多个 Connector 和一个 Container 就形成了一个 Service
		
		-server
			-Service（多个）
				-Connector（多个）
				-Container(1个)
	
		Connector用于接受请求并将请求封装成Request和Response来具体处理；
		Container用于封装和管理Servlet，以及具体处理request请求；
		
	Connector的结构
		-Connector
			-ProtocolHandler
				-Endpoint
					-Acceptor(用于监听请求)
					-AsyncTimeout(用于检查异步Request的超时)
					-Handler(Handler用于处理接收到的Socket，在内部调用Processor进行处理)
				-Processor
				-Adapter
		
		ProtocolHandler处理请求，不同的ProtocolHandler代表不同的连接类型，
			比如：Http11Protocol使用的是普通Socket来连接的，Http11NioProtocol使用的是NioSocket来连接的
			
			Endpoint用来处理底层Socket的网络连接,用来实现TCP/IP协议的
			Processor用于将Endpoint接收到的Socket封装成Request,用来实现HTTP协议的
			Adapter用于将Request交给Container进行具体的处理,将请求适配到Servlet容器进行具体的处理。
			
	Container架构
		-Engine
		-Host
		-Context
		-Wrapper
		
		Engine：引擎，用来管理多个站点，一个Service最多只能有一个Engine； 
		Host：代表一个站点，也可以叫虚拟主机，通过配置Host就可以添加站点；整个webapps就是一个Host站点
		Context：代表一个应用程序，对应着平时开发的一套程序，或者一个WEB-INF目录以及下面的web.xml文件； 
		Wrapper：每一Wrapper封装着一个Servlet；
		
		Container处理请求是使用责任链模式，
			责任链模式是指在一个请求处理的过程中有很多处理者依次对请求进行处理，每个处理者负责做自己相应的处理，
				处理完之后将处理后的请求返回，再让下一个处理着继续处理。
		Container包含四个子容器，而这四个子容器对应的BaseValve分别在：StandardEngineValve、StandardHostValve、StandardContextValve、StandardWrapperValve。
		Connector在接收到请求后会首先调用Container最顶层容器的Pipeline来处理,
		Container责任链调用流程：
		EngineValve1...->StandardEngineValve->HostValve1...StandardHostValve->ContextValve1...StandardContextValve->WrapperValve1...->StandardWrapperValve
		
		当执行到StandardWrapperValve的时候，会在StandardWrapperValve中创建FilterChain，并调用其doFilter方法来处理请求，
		这个FilterChain包含着我们配置的与请求相匹配的Filter和Servlet，其doFilter方法会依次调用所有的Filter的doFilter方法和Servlet的service方法，
		这样请求就得到了处理！
		
		当所有的Pipeline-Valve都执行完之后，并且处理完了具体的请求，这个时候就可以将返回的结果交给Connector了，
		Connector在通过Socket的方式将结果返回给客户端。
}

Object{
	Java最基础和核心的类，在编译时会自动导入；
	public native int hashCode();
		hashCode根据一定的规则和对象相关的信息生成的一个散列值
		重写hashCode()方法的基本规则：
			同一个对象多次调用hashCode()，返回的值应相同
			两个对象通过equals()比较相同时，hashCode()返回的值也应相同
	public boolean equals(Object obj) 
		比较两个对象的内存地址是否相等
	protected native Object clone() throws CloneNotSupportedException;
		快速创建一个已有对象的副本
		Object.clone()方法返回一个Object对象
		clone方法首先会判对象是否实现了Cloneable接口，若无则抛出CloneNotSupportedException
		object类默认的拷贝为浅拷贝
			浅拷贝
				Body重写clone()，Head不重写clone()
				static class Body implements Cloneable{
					public Head head;
					@Override
					protected Object clone() throws CloneNotSupportedException {
						return super.clone();
					}
				}
				static class Head /*implements Cloneable*/{
					public  Face face;
					public Head() {}
					public Head(Face face){this.face = face;}
				} 
				
			不彻底的深拷贝
				Body重写clone()，Head重写clone() Face不重写clone()
				static class Body implements Cloneable{
					public Head head;
					@Override
					protected Object clone() throws CloneNotSupportedException {
						return super.clone();
					}
				}
				static class Head /*implements Cloneable*/{
					public  Face face;
					public Head() {}
					public Head(Face face){this.face = face;}
					@Override
					protected Object clone() throws CloneNotSupportedException {
						return super.clone();
					}
				} 
				static class Face{}
			深拷贝
				Body重写clone()，Head重写clone() Face重写clone()
				static class Body implements Cloneable{
					public Head head;
					@Override
					protected Object clone() throws CloneNotSupportedException {
						return super.clone();
					}
				}
				static class Head /*implements Cloneable*/{
					public  Face face;
					public Head() {}
					public Head(Face face){this.face = face;}
					@Override
					protected Object clone() throws CloneNotSupportedException {
						return super.clone();
					}
				} 
				static class Face{
					@Override
					protected Object clone() throws CloneNotSupportedException {
						return super.clone();
					}
				}
			
			
	public String toString() {
		return getClass().getName() + "@" + Integer.toHexString(hashCode());
	}
		
	protected void finalize() throws Throwable { }
		垃圾回收器准备释放内存的时候，会先调用finalize()。
		(1).  对象不一定会被回收。
	    (2).垃圾回收不是析构函数。
        (3).垃圾回收只与内存有关。
        (4).垃圾回收和finalize()都是靠不住的，只要JVM还没有快到耗尽内存的地步，它是不会浪费时间进行垃圾回收的。
}

集合{
	collection接口: 每种容器的存储方式不同，存储方式称之：数据结构
	|	add(Object o)
	|		集合中存放的都是对象的引用(地址)
	|	retainAll(Collection<?> c);
	|		arr1.retainAll(arr2);集合arr1与arr2交集;arr1只剩交集 arr2不变
	|	removeAll(Collection<?> c);集合arr1与arr2差集;arr1只剩差集 arr2不变
	|		arr1.removeAll(arr2);
	|	在集合元素使用Iterator进行遍历时，不可以使用集合对象的方法操作集合中的元素,会发生异常
	|----List
	|	|  List特有方法，凡是可以操作角标的方法都是list特有的方法
	|   |  增 add(index,element),addAll(index,element) 删 remove(index) 改 set(index,element) 
	|   |  查 get(index),subList(from,to),listIterator();
	|   |  
	|	|
	|	|----ArrayList
	|	|----LinkedList
	|	|----Vertor
	|	
	| 	
	|----Set
		|----HashSet
		|----TreeSet
		
}

常量池、栈、堆的比较{
	JAVA中，有六个不同的地方可以存储数据
	1.寄存器
	2.栈   
	3.堆
	4.静态域
	5.常量池
	6.非RAM存储
	
	String s1 = "china";
	String s2 = "china";
	String s3 = "china";
	String ss1 = new String("china");
	String ss2 = new String("china");
	String ss3 = new String("china");
	
	栈						  常量池
							 _________	
	s1		------------->	|         |
	s2		------------->  | "china" |	
	s3		------------->  |_________|
									
									堆          
									
	ss1		------------->|new String("china"); |
	                      |---------------------|
	ss2		------------->|new String("china"); |
	                      |---------------------|
	ss3		------------->|new String("china"); |
	                      |---------------------|
}

HashMap{
	HashMap 是一个散列表，存储的内容是键值对(key-value)映射。HashMap中的映射是无序的。线程不安全。key、value都可以为null
	public class HashMap<K,V> extends AbstractMap<K,V> implements Map<K,V>, Cloneable, Serializable
	
}



https://elf8848.iteye.com/blog/1739598
集合总结网址 http://www.cnblogs.com/skywang12345/p/3323085.html










