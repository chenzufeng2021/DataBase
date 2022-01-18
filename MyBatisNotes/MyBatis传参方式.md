# 单个属性

## 不使用@param参数注解

单个属性的传参比较简单，可以是任意形式的，比如`#{a}`、`#{b}`或者`#{param1}`，**但是为了开发规范，尽量<font color=red>使用和入参一样</font>**。

Mapper如下：

```java
UserInfo selectByUserId(String userId);
```

XML如下：

```xml
<select id="selectByUserId" resultType="cn.cb.demo.domain.UserInfo">
    SELECT * FROM user_info 
    WHERE user_id = #{userId} AND status = 1
</select>
```

注意：当查询参数为一个单一属性且没有使用注解时，xml中查询字段`#{}`内可以为任意值。

## 使用@param参数注解

Mapper如下：

```java
UserInfo selectByUserId(@Param("userId") String userId);
```

XML如下：

```xml
<select id="selectByUserId" resultType="cn.cb.demo.domain.UserInfo">
    SELECT * FROM user_info 
    WHERE user_id = #{userId} 
    AND status = 1
</select>
```

没有必要使用`@param`参数注解！



# 多个属性

## 使用索引

多个参数可以使用类似于索引的方式传值，比如`#{param1}`对应第一个参数，`#{param2}`对应第二个参数……

Mapper方法如下：

```java
UserInfo selectByUserIdAndStatus(String userId, Integer status);
```

XML如下：

```xml
<select id="selectByUserIdAndStatus" resultType="cn.cb.demo.domain.UserInfo">
    SELECT * FROM user_info  
    WHERE user_id = #{param1} 
    AND status = #{param2}
</select>
```

注意：此种方式不推荐开发中使用。

## 使用@Param

`@Param`（org.apache.ibatis.annotations.Param）这个注解用于指定key，一旦指定了key，在SQL中即可对应的key入参。即，使用`@Param`注解显式地告诉 Mybatis 参数的名字，这样在xml中就可以按照参数名去引用了。

Mapper方法如下：

```java
UserInfo selectByUserIdAndStatus(@Param("userId") String userId, @Param("status") Integer status);
```

XML如下：

```xml
<select id="selectByUserIdAndStatus" resultType="cn.cb.demo.domain.UserInfo">
    SELECT * FROM user_info 
    WHERE user_id = #{userId} 
    AND status = #{status}
</select>
```

注意：xml中的`userId`对应的是`@Param("userId")`的`userId`！

## 使用Map

Mybatis底层就是将入参转换成`Map`，入参传Map当然也行，此时`#{key}`中的`key`就对应Map中的`key`。

Mapper中的方法如下：

```java
UserInfo selectByUserIdAndStatusMap(Map<String, Object> map);
```

XML如下：

```xml
<select id="selectByUserIdAndStatusMap" resultType="cn.cb.demo.domain.UserInfo">
    SELECT * FROM user_info  
    WHERRE user_id = #{userId} 
    AND status = #{status}
</select>
```

## 单个实体封装多个参数

多个参数可以使用实体类封装，此时对应的`key`就是属性名称，注意<font color=red>一定要有`get`方法</font>。

Mapper方法如下：

```java
UserInfo selectByEntity(UserInfoReq userInfoReq);
```

XML如下（参数的引用直接使用JavaBean的字段）：

```xml
<select id="selectByEntity" resultType="cn.cb.demo.domain.UserInfo">
    SELECT * FROM user_info 
    WHERE user_id = #{userId} 
    AND status = #{status}
</select>
```

实体类如下：

```java
@Data
public class UserInfoReq {
    private String userId;
    private Integer status;
}
```



# 单个实体类

## 使用@param参数注解

实体类如下：

```java
@Data
public class UserInfoReq {
    private String userId;
    private Integer status;
}
```

Mapper方法如下：

```java
UserInfo selectByEntity(@Param("userInfoReq") UserInfoReq userInfoReq);
```

XML如下（参数的引用直接使用JavaBean的字段）：

```xml
<select id="selectByEntity" resultType="cn.cb.demo.domain.UserInfo">
    SELECT * FROM user_info 
    WHERE user_id = #{userInfoReq.userId} 
    AND status = #{userInfoReq.status}
</select>
```



## 不使用@param参数注解

实体类如下：

```java
@Data
public class UserInfoReq {
    private String userId;
    private Integer status;
}
```

Mapper方法如下：

```java
UserInfo selectByEntity(UserInfoReq userInfoReq);
```

XML如下（参数的引用直接使用JavaBean的字段）：

```xml
<select id="selectByEntity" resultType="cn.cb.demo.domain.UserInfo">
    SELECT * FROM user_info 
    WHERE user_id = #{userId} 
    AND status = #{status}
</select>
```

注意：单个实体类作为参数，不使用@param参数注解时，==不能使用==`#{param1.userId}`

```xml
<select id="selectByUserIdAndStatus" resultType="cn.cb.demo.domain.UserInfo">
    SELECT * FROM user_info  
    WHERE user_id = #{param1.userId} 
    AND status = #{param1.status}
</select>
```



# 单个实体类 + 多个单独参数

controller：

```java
// 前端以【参数的形式：userId=，sex=，age=】传递User属性和age
@GetMapping("/getUserInfo")
public User getUserInfo(User user, Integer age) {
    userService.getUserInfo(user, age);
}
```

dao：

```java
User getUserInfo(@Param("userInfo") User user, @Param("age") Integer age);
```

xml：

```xml
<!--查询-->
<select id="getUserInfo" resultType="com.demo.elegant.pojo.User">
    SELECT userId FROM users
    WHERE 
        userId = #{userInfo.userId} 
    AND sex = #{userInfo.sex} 
    AND age = #{age};
</select>
```

注意：`userInfo.userId`

如果参数为<font color=red>多个实体bean，则用各自的参数value值名字区分</font>。



# 多个实体类

## 使用@Param

dao：

```java
List<Items> selectItembyparam(@Param("items") Items items, @Param("user") Users user);
```

xml：

```xml
<select id="selectItembyparam3" resultMap="BaseResultMap">
   SELECT *
   FROM  todo_items
   WHERE user_id = #{user.userId}
   AND   priority = #{items.priority}
 </select>
```

注意：

- 当使用@param注解JavaBean对象时，@Param注解==括号内==的参数为==形参==，xml内的查询字段应该是==形参.bean属性==！
- xml中`user`对应于`@Param("user")`中`user`！



## 不使用@Param

dao：

```ja
List<Items> selectItembyparam(Items items, Users user);
```

xml：

```xml
<select id="selectItembyparam" resultMap="BaseResultMap">
        SELECT *
        FROM  todo_items
        WHERE priority = #{param1.priority}
        AND   user_id = #{param2.userId}
 </select>
```



# List 传参

List传参也是比较常见的，通常是SQL中的`in`。

Mapper方法如下：

```java
List<UserInfo> selectList( List<String> userIds);
```

XML如下：

```xml
<select id="selectList" resultMap="userResultMap">
    select * from user_info where status=1
    and user_id in
    <foreach collection="list" item="item" open="(" separator="," close=")" >
        #{item}
    </foreach>
</select>
```

# 数组传参

这种方式类似List传参，依旧使用`foreach`语法。

Mapper方法如下：

```java
List<UserInfo> selectList( String[] userIds);
```

XML如下：

```xml
<select id="selectList" resultMap="userResultMap">
    select * from user_info where status=1
    and user_id in
    <foreach collection="array" item="item" open="(" separator="," close=")" >
        #{item}
    </foreach>
</select>
```

# 参数类型为对象中包含集合

JavaBean：

```java
@Data
public class Department {
    private Long id;

    private String deptName;

    private String descr;

    private Date createTime;

    List<Employee> employees;
}
```

controller：

```java
@PostMapping("findByDepartment")
public ResultMsg findByDepartment(@RequestBody Department department)
{
    List result= employeeDao.findByDepartment(department);
    return ResultMsg.getMsg(result);
}
```

dao：

```java
List <Employee> findByDepartment(@Param("department") Department department);
```

xml：

注意：`department.id`、`department.employees`

```xml
<select id="findByDepartment" resultMap="BaseResultMap" parameterType="com.example.demo.po.Department">
    SELECT * FROM employee 
    WHERE dept_id = #{department.id} 
    AND age IN
    <foreach collection="department.employees" open="(" separator="," close=")" item="employee">
        #{employee.age}
    </foreach>
</select>
```



# useActualParamName

```properties
mybatis.configuration.use-actual-param-name=true
```

允许使用==方法签名中的名称==作为==语句参数名称==，默认为true。

- 如果`useActualParamName`设置为`true`时，
  传递参数需要使用`#{arg0.bean属性}`—`#{argn.bean属性}`或者`#{param1.bean属性}`—`#{paramn.bean属性}`；
- 如果`useActualParamName`设置为`false`时，，传递参数需要使用`#{0.bean属性}`—`#{n.bean属性}`或者`#param1.bean属性}`—`#{paramn.javabean属性}`

# 参考资料

[1] [Mybatis的几种传参方式，你了解多少](https://mp.weixin.qq.com/s?__biz=MzU3MDAzNDg1MA==&mid=2247484563&idx=1&sn=3fdc7c71b08a5ef1d1d09735b8974f74&chksm=fcf4db5ecb8352484c8d6cd1ef44723ac7159da18bb166575b2238f896bcd3e00159961e3f5c&scene=178&cur_album_id=1500819225232343046#rd)

[2] [mybatis 传递参数的7种方法](https://blog.csdn.net/bdqx_007/article/details/94836637?spm=1001.2101.3001.6650.4&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7Edefault-4.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromBaidu%7Edefault-4.nonecase)

[3] [Mybatis 传参的各种姿势，看这一篇就足够](https://blog.csdn.net/qq_35387940/article/details/104778001)

[4] [MyBatis-@param注解详解](https://blog.csdn.net/xuonline4196/article/details/87994394)（重要）

[5] [关于Mybatis的@Param注解](https://www.cnblogs.com/libin6505/p/10036765.html)

