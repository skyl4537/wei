# springboot项目集成logback

> 无需添加依赖

```xml
<parent>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-parent</artifactId>
   <version>2.1.5.RELEASE</version>
</parent>
```

在`spring-boot-starter-parent`中已经加入了logback的依赖信息

```xml
<logback.version>1.2.3</logback.version>
```

所以无需添加log依赖，即可直接使用

springboot项目默认加载logback-spring.xml配置文件

# configuration

>scan属性

当此属性设置为true时，配置文件如果发生改变，将会被重新加载，默认值为true。

> scanPeriod属性

设置监测配置文件是否有修改的时间间隔，如果没有给出时间单位，默认单位是毫秒。当scan为true时，此属性生效。默认的时间间隔为1分钟。

> debug属性

 当此属性设置为true时，将打印出logback内部日志信息，实时查看logback运行状态。默认值为false。

# configuration-contextName子节点

> 用来设置上下文名称`用处？？？？？？`

每个logger都关联到logger上下文，默认上下文名称为default

# configuration-property子节点

> 用来定义变量值

它有两个属性name和value，通过<property>定义的值会被插入到logger上下文中，可以使“${property-name}”来获取变量。

```xml
<configuration scan="true" scanPeriod="60 seconds" debug="false"> 
　　　<property name="APP_Name" value="myAppName" /> 
　　　<contextName>${APP_Name}</contextName> 
　　　<!--其他配置省略--> 
</configuration>
```

#configuration-appender子节点

> 负责写日志的组件，它有两个必要属性name和class

name指定appender名称，class指定appender的全限定名

##ConsoleAppender  控制台打印

```xml
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    ....
</appender>
```

### encoder子节点

>对日志进行格式化

### target子节点

>字符串System.out(默认)或者System.err

## RollingFileAppender滚动记录文件

>先将日志记录到指定文件，当符合某个条件时，将日志记录到其他文件

### file

> 被写入的文件名，可以是相对目录，也可以是绝对目录，如果上级目录不存在会自动创建，没有默认值。

### append

>如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true。

### rollingPolicy

>当发生滚动时，决定RollingFileAppender的行为，涉及文件移动和重命名。

属性class定义具体的滚动策略类

class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy"： 最常用的滚动策略,根据时间来制定滚动策略

#### fileNamePattern

> 非活跃文档归档（类似历史文件）

归档的文件命名中必须包含文件名+“%d”转换符

“%d”可以包含一个java.text.SimpleDateFormat指定的时间格式，默认格式是 yyyy-MM-dd，“/”或者“\”会被当做目录分隔符

#### maxHistory

> 控制保留的归档文件的最大数量,超出数量就删除旧文件

假设设置每分钟滚动，且<maxHistory>是6，则6分钟之前的日志将会被删除

#### timeBasedFileNamingAndTriggeringPolicy

>按时间回滚的同时，按文件大小来回滚

class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP"

> maxFileSize子标签

超出maxFileSize设定的值，日志文件进行回滚

# configuration-logger

>用来设置某一个包或者具体的某一个类的日志打印级别、以及指定<appender>

> name属性

用来指定受此loger约束的某一个包或者具体的某一个类。

> level属性

用来设置打印级别，大小写无关

> addtivity属性

是否向上级logger传递打印信息。默认是true

<logger>可以包含零个或多个<appender-ref>元素，标识这个appender将会添加到这个logger中

#configuration-root

> 特殊的<logger>元素，只有一个level属性，name为root

<root>可以包含零个或多个<appender-ref>元素，标识这个appender将会添加到这个logger。

# filter

过滤器被添加到**<Appender>** 中

> LevelFilter级别过滤器

```xml
class="ch.qos.logback.classic.filter.LevelFilter"
```

根据日志级别进行过滤

DENY，日志将立即被抛弃不再经过其他过滤器；

NEUTRAL，有序列表里的下个过滤器过接着处理日志；

ACCEPT，日志会被立即处理，不再经过剩余过滤器。

如果日志级别等于配置级别，过滤器会根据onMath 和 onMismatch接收或拒绝日志

<level>:设置过滤级别

<onMatch>:用于配置符合过滤条件的操作

<onMismatch>:用于配置不符合过滤条件的操作

> ThresholdFilter临界值过滤器

```xml
class="ch.qos.logback.classic.filter.ThresholdFilter"
```

过滤掉低于指定临界值的日志

当日志级别<=临界值时，过滤器返回NEUTRAL；

当日志级别低于临界值时，日志会被拒绝。

# 实例

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- scan:当此属性设置为true时，配置文件如果发生改变，将会被重新加载，默认值为true -->
<!-- scanPeriod:设置监测配置文件是否有修改的时间间隔，如果没有给出时间单位，默认单位是毫秒。当scan为true时，此属性生效。默认的时间间隔为1分钟。 -->
<!-- debug:当此属性设置为true时，将打印出logback内部日志信息，实时查看logback运行状态。默认值为false。 -->
<configuration scan="true">
    <contextName>logback</contextName><!-- ?????? -->
    <!-- name的值是变量的名称，value的值时变量定义的值。通过定义的值会被插入到logger上下文中。定义变量后，可以使“${}”来使用变量。 -->
    <property name="log.path" value="/home/data1/logs"/>
    <property name="CONSOLE_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} %5p [%t] %c{0} - %m%n"/>
    <property name="FILE_PATTERN" value="%d{HH:mm:ss.SSS} [%5p] %t %c{0} - %msg%n"/>

    <!--输出到控制台-->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <!--此日志appender是为开发使用，只配置最底级别，控制台输出的日志级别是大于或等于此级别的日志信息-->
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>info</level>
        </filter>
        <encoder>
            <Pattern>${CONSOLE_PATTERN}</Pattern>
            <!-- 设置字符集 -->
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!--输出到文件-->

    <!-- 时间滚动输出 level为 DEBUG 日志 -->
    <appender name="controller_file" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 正在记录的日志文件的路径及文件名 -->
        <file>${log.path}/demo/controller/controller.log</file>
        <!--日志文件输出格式-->
        <encoder>
            <pattern>${FILE_PATTERN}</pattern>
            <charset>UTF-8</charset> <!-- 设置字符集 -->
        </encoder>
        <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 日志归档 -->
            <fileNamePattern>${log.path}/demo/controller/controller-%d{yyyy-MM-dd}.%i.zip
            </fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy
                class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>10KB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <!--日志文件保留天数-->
            <maxHistory>15</maxHistory>
        </rollingPolicy>
    </appender>

    <appender name="service_file" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 正在记录的日志文件的路径及文件名 -->
        <file>${log.path}/demo/service/service.log</file>
        <!--日志文件输出格式-->
        <encoder>
            <pattern>${FILE_PATTERN}</pattern>
            <charset>UTF-8</charset> <!-- 设置字符集 -->
        </encoder>
        <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 日志归档 -->
            <fileNamePattern>${log.path}/demo/service/service-%d{yyyy-MM-dd}.%i.zip
            </fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy
                class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <!--日志文件保留天数-->
            <maxHistory>15</maxHistory>
        </rollingPolicy>

    </appender>

    <appender name="util_file" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 正在记录的日志文件的路径及文件名 -->
        <file>${log.path}/demo/util/util.log</file>
        <!--日志文件输出格式-->
        <encoder>
            <pattern>${FILE_PATTERN}</pattern>
            <charset>UTF-8</charset> <!-- 设置字符集 -->
        </encoder>
        <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 日志归档 -->
            <fileNamePattern>${log.path}/demo/util/util-%d{yyyy-MM-dd}.%i.zip
            </fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy
                class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <!--日志文件保留天数-->
            <maxHistory>15</maxHistory>
        </rollingPolicy>
        <!-- 此日志文件只记录info级别的 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>info</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <appender name="SYSTEM_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 正在记录的日志文件的路径及文件名 -->
        <file>${log.path}/demo/SYSTEM_FILE/SYSTEM_FILE.log</file>
        <!--日志文件输出格式-->
        <encoder>
            <pattern>${FILE_PATTERN}</pattern>
            <charset>UTF-8</charset> <!-- 设置字符集 -->
        </encoder>
        <!-- 日志记录器的滚动策略，按日期，按大小记录 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 日志归档 -->
            <fileNamePattern>${log.path}/demo/SYSTEM_FILE/SYSTEM_FILE-%d{yyyyMMdd-HHmm}.%i.zip
            </fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy
                class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>10KB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <!--日志文件保留n个单位的日志，之前的日志将会被删除-->
            <maxHistory>5</maxHistory>
        </rollingPolicy>

    </appender>

    <root level="info">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="SYSTEM_FILE"/>
    </root>

    <!-- 可选节点; 设置某一个包或具体某一个类的日志打印级别,以及指定<appender> (覆盖root节点的输出级别) -->
    <!-- name: 受此logger约束的某一个包或具体某一个类 -->
    <!-- level: 日志级别, 默认继承上级的打级别 -->
    <!-- additivity: 是否向上级logger（root节点）传递打印信息. 默认是true -->
    <logger name="com.demo.blue.controller" level="debug" additivity="true">
        <appender-ref ref="controller_file"/>
    </logger>

    <logger name="com.demo.blue.service" level="warn" additivity="true">
        <appender-ref ref="service_file"/>
    </logger>
    <logger name="com.demo.blue.util" level="info" additivity="false">
        <appender-ref ref="util_file"/>
    </logger>
</configuration>
```

