# SQL查找是否"存在"<sup><a href="#ref1">[1]</a></sup>

## 常见写法

业务代码中，需要根据一个或多个条件，查询<font color=red>是否存在记录，不关心有多少条记录</font>。普遍的SQL及代码写法如下：

SQL语句：

```sql
SELECT count(*) FROM table WHERE a = 1 AND b = 2
```

Java语句：

```java
int nums = xxDao.countXxxxByXxx(params);
if ( nums > 0 ) {
  // 当存在时，执行这里的代码
} else {
  // 当不存在时，执行这里的代码
}
```

## 优化方案

SQL语句：

```sql
SELECT 1 FROM table WHERE a = 1 AND b = 2 LIMIT 1
```

Java语句：

```java
Integer exist = xxDao.existXxxxByXxx(params);
if ( NULL != exist ) {
  // 当存在时，执行这里的代码
} else {
  // 当不存在时，执行这里的代码
}
```

SQL不再使用`count`，而是改用`LIMIT 1`，<font color=red>让数据库查询时遇到一条就返回，不要再继续查找还有多少条了</font>。

业务代码中直接判断是否非空即可。





























































# 参考资料

<span name="ref1">[1] [SQL查找是否"存在"，别再count了！](https://mp.weixin.qq.com/s/TuxT8--CtZYC2yWxuchWSA)</span>

