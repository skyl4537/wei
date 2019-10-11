# insert

> 插入数据形式

```mysql
#单条执行 set方式
insert into b set id=1,`name`='b1',deptId='11';
```

```mysql
#values单条形式
INSERT INTO `test0806`.`b` (`id`, `name`, `deptId`) VALUES ('1', 'b1', '11');
```

```mysql
#select方式插入，select字段类型须与要插入表的字段类型相同
# id+(SELECT MAX(id) from b) 解决id自增不能负责的插入的情况
insert into b (id,`name`,deptId) SELECT id+(SELECT MAX(id) from b),`name`,deptId from b;
```

# GROUP BY column HAVING

> 分组查询

GROUP BY column  having 条件

```mysql
#以name字段分组，查询相同name总数>1的最大id的集合
select MAX(id) max_id from  b GROUP BY `name` HAVING COUNT(`name`) >1;
```

```mysql
#删除重复记录
#如果不将查询结果集复制给t表，直接使用结果集，会出现如下错误：[Err] 1093 - You can't specify target table 'b' for update in FROM clause
#select t.max_id from (....) 更新这个表的同时又查询了这个表，查询这个表的同时又去更新了这个表，可以理解为死锁。mysql不支持这种更新查询同一张表的操作

delete from b where id in(
    select t.max_id from (
        select MAX(id) max_id from  b GROUP BY `name` HAVING COUNT(`name`) >1
    ) t
);
```



