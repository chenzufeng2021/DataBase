# 使用 limit

对于 mysql 数据库可以使用 limit ，如：

```sql
select * from table limit 5; --返回前5行

select * from table limit 0, 5; --同上，返回前5行

select * from table limit 5, 10; --返回6-15行
```

# sql 分页

## service

接口：

```java
List<Student> queryStudentsBySql(int currPage, int pageSize);
```

实现类：

```java
public List<Student> queryStudentsBySql(int currPage, int pageSize) {
    Map<String, Object> data = new HashedMap();
    data.put("currIndex", (currPage-1)*pageSize);
    data.put("pageSize", pageSize);
    return studentMapper.queryStudentsBySql(data);
}
```

MyBatis接口：

```java
List<Student> queryStudentsBySql(Map<String, Object> data);
```

xml 文件：

```xml
<select id="queryStudentsBySql" parameterType="map" resultMap="studentmapper">
    select * from student limit #{currIndex} , #{pageSize}
</select>
```

# 拦截器分页

# SpringBoot集成pagehelper分页

参考资料：https://blog.csdn.net/gnail_oug/article/details/80229542

## 添加依赖

```xml
<!-- https://mvnrepository.com/artifact/com.github.pagehelper/pagehelper-spring-boot-starter -->
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>1.4.0</version>
</dependency>
```

## 配置文件

```properties
# 添加分页配置信息
pagehelper.helperDialect=mysql
pagehelper.reasonable=true
pagehelper.supportMethodsArguments=true
pagehelper.params=count=countSql
pagehelper.page-size-zero=true
```

## 修改接口

```java
/**
 * 查询所有学生信息（分页）
 * @return 学生信息列表
 */
@ApiOperation(value = "查询所有学生信息（分页）")
@GetMapping("/allStudentInfoPage")
@ApiImplicitParams({
        @ApiImplicitParam(name = "pageNo", value = "第几页", required = true, paramType = "path"),
        @ApiImplicitParam(name = "pageSize", value = "展示多少条数据", required = true, paramType = "path")
})
public List<Student> getAllStudentInfoPage(
        @RequestParam(defaultValue = "1") Integer pageNo,
        @RequestParam(defaultValue = "2") Integer pageSize
) {
    PageHelper.startPage(pageNo, pageSize);
    return studentService.queryAllStudent();
}
```

