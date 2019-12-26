# String

##valueOf(int i)

```java
public static String valueOf(int i) {
    return Integer.toString(i);
}
```

## intern()

判断这个常量是否存在于常量池。

如果存在，则直接返回地址值（只不过地址值分为两种情况，1是堆中的引用，2是本身常量池的地址）

​	如果是引用，返回引用地址指向的堆空间对象地址值

​	如果是常量，则直接返回常量池常量的地址值，

如果不存在，

​	将当前对象引用复制到常量池,并且返回的是当前对象的引用（这个和上面最开始的字符串创建分析有点不同）

```java
@Test
public void testString1(){
    //常量池创建 "a","bc"两个常量，堆中创建a对象、bc对象 s1对象，三个对象,此时常量池中并没有"abc"
    String s1 = new String("a")+new String("bc");
    //“abc”在常量池中此时不存在，将s1引用对象的地址存放在常量池中
    s1.intern();
    //常量池中已存在 并且返回的是 s1引用对象的地址
    String s2 = "abc";
    //此时 s1和s2的地址都是 s1对象的引用地址
    System.out.println( s1 == s2); //1
}

@Test
public void testStr1(){
    //常量池中创建"a"，堆中创建ss1对象
    String ss1 = new String("a");
    //此时常量池中已存在"a",ss1.intern() 返回 “a”常量池地址
    ss1.intern();
    //常量池中已存在
    String ss2 = "a";
    //ss1为堆中地址，ss2为常量池中地址
    System.out.println( ss1 == ss2);
}

@Test
public void testStr2(){
    //常量池创建"1","23",堆中创建23对象，s1对象，此时常量池中并没有"123"
    String s1 = "1"+new String("23");
    //常量池中创建“123”
    String s2 = "123";
    //s1.intern() 执行时发现"123"已存在，则返回常量池
    System.out.println(s1.intern() == s2);
}
```

# Integer

## parseInt(String s)

```java
public static int parseInt(String s) throws NumberFormatException {
    return parseInt(s,10);
}
```

 parseInt(s,10); 10表示进制，将字符串转化为10进制Integer类型。先判断s的第一个字符是不是符号，再进行转化

# equals()和hashcode()

正常情况下，equals()方法比较的是对象在内存中的值，如果值相同，那么Hashcode值也应该相同。

如果重写了equals()方法，也应该要重写hashcode()的方法。

hashCode 是用于散列数据的快速存取，如利用 HashSet/HashMap/Hashtable 类来存储数据时，都会根据存储对象的 hashCode 值来进行判断是否相同的

如果两个对象equals为true，而hashcode不同，再使用hashset等存储时，相同的对象则会存储为两条。



