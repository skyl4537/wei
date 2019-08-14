#HashMap

HashMap是无序的散列表，存储内容为键值对，是线程不安全的，key-value允许为null.

HashMap有5个重要的成员变量

1. node类型的table数组，HashMap中键值对存放于这个数组中，node是一个单链表。默认数组大小为16

2. Size是HashMap保存键值对的个数

3. loadFactory加载因子，默认为0.75f

4. thresthod阀值，判断是否需要调整Hashmap的容量.阀值=容量*加载因子,当HashMap的size>阀值时，将对HashMap的容量进行扩充。扩充为原来的2倍。

5. modCount修改次数。对HashMap的修改都会增加这个值，是用来实现fail-fast机制。使用volatile修饰，保证多线程间的可见性。

造成Hash冲突的原因，在对HashMap进行添加数据的时，根据Key的hashcode值来决定该键值对添加到table中的哪个位置，如果两个Key的hashcode相同就会出现hash冲突，HashMap使用单链表来进行拉链式存储，解决hash冲突。

# Fail-fast

Fail-fast是在对集合对象使用迭代器进行遍历时，其他线程对这个集合对象进行修改，则会报ConCurrentModificationException异常，即为fail-fast，原因是在使用迭代器对集合对象的进行遍历的时候，会将集合对象当前的modCount记录下来，每获取值的时候都会将当前的modCount修改次数与记录的值做对比，如不相同则抛异常。

#HashTable

HashTable是无序的散列表，存储内容为键值对，是线程安全的，key-value不允许为null，实现上与HashMap相同，只是在方法上加了synchronized关键字，将方法改为同步机制，实现线程安全的。

 # HashSet

HashSet是继续HashMap实现的，在进行添加元素时，对元素的key和value进行判空操作。

# synchronized

原理:一个对象有且只有一个同步锁；调用对象的synchronized关键字即可获取到对象的同步锁；不同线程间对同步锁的获取是互斥的，只能被一个线程获取到。

Synchronized有3个规则

1. 线程1调用对象A的synchronized修饰的方法A1时，其他线程不可访问该方法

2. 线程1调用对象A的synchronized修饰的方法A1时，其他线程可访问其他没有synchronized修饰的方法

3. 线程1调用对象A的synchronized修饰的方法A1时，其他线程不可访问对象A的其他synchronized修饰的其他方法

实例锁：锁在对象上，关键字为synchronized

全局锁：锁在类上，关键字为static synchronized，与对象的个数无关

 # 线程池ThreadpoolExecutor

有7个重要的参数

1.核心线程数；2.最大线程数；3.线程最大空闲时间；4.空闲时间的时间单位；5.线程工厂；6.任务队列；7拒绝策略

运行原理：初始创建1个新的线程池，当前线程数为0，有任务需要处理时，会创建一个线程处理任务，假设任务需要长期处理，当任务添加到当前线程>核心线程数的时候，将任务添加到任务等待队列中，当任务等待队列也填满时，将线程数扩充到最大线程数，新扩充的线程处理新添加的任务，当最大线程数都在处理任务时，再向线程池中添加的任务将会根据拒绝策略进行任务的拒绝。常用的有四种线程池cache、fixed、schedule、single都是根据ThreadpoolExecutor进行的特殊定制

# TCP和UDP

TCP是面向连接的可靠传输，TCP有3次握手和4次挥手 

UDP是非面向连接的不可靠传输

TCP只能1对1进行数据传输；UDP可以1对1、1对多、多对多进行数据的传输

TCP 3次握手，就像打电话，首先A要拨通B的号码，就是我们所说的目标ip,port,拨通后A说喂，此时A不能确定自己的连接是够正常，B回复A喂时，A确定自己的连接是正常的，但B不能确定自己的连接是否正常，所以A回复你好我是A，此时B也能确定自己的连接正常，谈话开始。

TCP 4次挥手，A我说完了A就进入了FIN_WAIT_1状态，B说稍等，A进入FIN_WAIT_2状态，B则进入CLOSE_WAIT状态，B又说我也说完了，B进入了LAST_ACK状态，A说那挂了吧，B关闭进入CLOSE状态，A进入TIME_WAIT状态

 # JVM

堆：用来存放对象信息，JVM中只有一个堆，被所有线程共享，是一个不连续的内存空间

栈：由系统分配，速度快，是一个连续的内存空间，栈以栈帧为单位，每个线程会分配一个栈，该栈为线程私有，不可共享，每个方法的调用会分配一个栈帧，栈的存储特性为：先进后出，后进先出

方法区：方法区是一个特殊的堆，JVM只有一个方法区，被所有线程共享，用于存储类，常量等相关信息

 # 集合

集合分为两大类collection和Map

Collection有两大分支，有序的List和无序的set，List的实现有ArrayList、LinkedList、Vactor，set的实现由HashSet和TreeSet

ArrayList为动态数组，是线程不安全的，有两个重要的成员变量

1. object[] elementData数组，用来存放数据，

2. Size，为当前列表中存放数据的总数，还有一个隐含变量modCount修改次数，在父类AbstractList中定义，对集合进行修改时，则会对该值进行+1操作，用来实现fail-fast机制。

当向集合中添加数据的时候，首先判断size+1的值是否大于数组的长度，大于则对数组进行扩充，创建一个新的数组大小为原数组的1.5倍，再将数据copy到新的数组中，将数组对象的引用指向新的数组对象。

与之类似的Vactor，实现方式相同，只是方法上添加了synchronized关键字，添加同步机制，改为线程安全的机制

还有一个线程安全的List是copyOnwriteArrayList，与Vactor不同的是，在对集合修改时进行加锁，读取数组时不加锁，修改时将原来的数组copy一份，修改copy的数组，再将数组的引用指向copy的新的数组

#LinkedList 

LinkedList双向链表，线程不安全，有3个重要的参数

First ，last，size，first为链表的第一个元素，last为链表的最后一个元素，size为当前链表中元素的个数

在对链表指定位置进行查找时，首先对比index的值是否大于size/2,小于则从前往后查，大于则从后往前查。LinkedList中add和offer方法相同都是向链表的最末端添加数据，push则是向链表最前端添加数据。Pop、remove、poll都是删除链表的第一个参数，peek则是获取链表的第一个元素。

 # cookie与session的区别

1.保存位置:cookie将数据保存在客户端，session将数据保存在服务器端

2.安全性：cookie不安全，可以对客户端的cookie信息解析，并进行cookie欺骗，考虑安全性应考虑使用session

3.性能：因为session存放在服务器端，当访问增多时则会影响服务器性能，考虑性能应使用cookie

4.保存数据大小：cookie保存数据不能超多4K，很多浏览器也限制一个站点最多保存20个cookie

 # 请求转发/请求重定向

1.请求次数:forward只发一次请求,redirect 多次请求

2.地址栏变化:forward 显示第一次请求的路径地址,redirect 显示最后一次响应的路径地址

3.request:forward是同一个request对象，redirect是不同的request对象

4.站点访问:forward只能在同一个应用内进行请求转发,redirect 可以在不同的应用内进行请求重定向

5./表示:forward: / 代表的是当前 WEB 应用的根目录 <http://127.0.0.1:8080/helloworld>,redirect: / 代表的是当前 WEB 站点的根目录. 例：<http://127.0.0.1:8080/>

l