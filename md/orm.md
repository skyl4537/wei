# SQL注入、占位符拼接符

> 什么是SQL注入

用户在网页上输入sql命令后，后台接收后没有进行处理，而直接按照sql命令运行

> Mybatis中的占位符和拼接符

```sh
占位符
#{}表示一个占位符号，通过#{}把parameterType传入的内容通过preparedStatement向占位符中设置值，自动进行java类型和jdbc类型转换，#{}可以有效防止sql注入。
#{}可以接收简单类型值或pojo属性值。如果parameterType传输单个简单类型值，#{}括号中可以是value或其它名称。
```

```sh
拼接符
${}表示拼接sql串，通过${}可以将parameterType 传入的内容直接拼接在sql中且不进行jdbc类型转换，${}可以接收简单类型值或pojo属性值，如果parameterType传输单个简单类型值，${}括号中只能是value。
```

> 为什么PreparedStatement 有效的防止sql注入？

Java提供了 Statement、PreparedStatement 和 CallableStatement三种方式来执行查询语句，其中 Statement 用于通用查询， PreparedStatement 用于执行参数化查询，而 CallableStatement则是用于存储过程。

使用PreparedStatement的参数化的查询可以阻止大部分的SQL注入

```sh
在使用参数化查询的情况下，数据库系统（eg:MySQL）不会将参数的内容视为SQL指令的一部分来处理，而是在数据库完成SQL指令的编译后，才套用参数运行，因此就算参数中含有破坏性的指令，也不会被数据库所运行。
即SQL语句在程序运行前已经进行了预编译,当运行时动态地把参数传给PreprareStatement时，即使参数里有敏感字符如 or '1=1'、数据库也会作为一个参数一个字段的属性值来处理而不会作为一个SQL指令。
```

> PreparedStatement比 Statement 更快

使用 PreparedStatement SQL语句会预编译在数据库系统中。执行计划同样会被缓存起来，它允许数据库做参数化查询。为了获得性能上的优势，应该使用参数化sql查询而不是字符串追加的方式。

下面两个SELECT 查询，第一个SELECT查询就没有任何性能优势。

```java
SQL Query 1:字符串追加形式的PreparedStatement
String loanType = getLoanType();
PreparedStatement prestmt = conn.prepareStatement("select banks from loan where loan_type=" + loanType);
'执行顺序：sql拼接->编译->执行'

SQL Query 2：使用参数化查询的PreparedStatement
PreparedStatement prestmt = conn.prepareStatement("select banks from loan where loan_type=?");
prestmt.setString(1,loanType);
'执行顺序：编译->参数设置->执行'
```

