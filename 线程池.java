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










