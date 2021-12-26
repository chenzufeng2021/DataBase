---
typora-copy-images-to: MyBatisPictures
---

# 动态 SQL

根据不同条件拼接 SQL 语句时要<font color=red>确保不能忘记添加必要的空格，还要注意去掉列表最后一个列名的逗号</font>。利用动态 SQL，可以彻底摆脱这种痛苦。

## if

```mysql
<select id="findActiveBlogLike" resultType="Blog">
    SELECT * FROM BLOG 
    WHERE state = ‘ACTIVE’
    
    <if test="title != null">
        AND title like #{title}
    </if>
    
    <if test="author != null and author.name != null">
        AND author_name like #{author.name}
    </if>
    
</select>
```



举个例子<sup><a href="#ref3">[3]</a></sup>：系统中医护人员需要根据特定条件筛选患者，比如住院号，床位，性别等。当然这些条件并不是必填的，具体的功能截图如下:

![图片](MyBatisPictures/动态SQL-if.webp)

以上截图中的条件筛选并不是必填的，因此我们不能在SQL中固定，<font color=red>要根据前端是否传值来判断是否需要加上这个条件</font>。那么此时查询语句如何写呢？

```xml
<select id ='selectPats' resultType='com.xxx.domain.PatientInfo'>
  select * from patient_info 
  where status = 1
  
    <!--前端传来的住院号iptNum（与JavaBean对应）不为null，表示需根据住院号筛选，此时Where语句需要加上这个条件-->
  <if test="iptNum!=null">
      and ipt_num=#{iptNum}
  </if>
  
  <!--床位号bedNum筛选-->
  <if test="bedNum!=null">
      and bed_num = #{bedNum}
  </if>
</select>
```

`<if>`标签中的属性`test`用来指定判断条件，那么问题来了，上面的例子中的`test`中判断条件都是一个条件，如果此时变成两个或者多个条件呢？和SQL的语法类似，`and`连接即可，如下：

```xml
<!--test中判断条件为多个--> 
<if test="bedNum!=null and bedNum!='' ">
      and bed_num=#{bedNum}
  </if>
```



## choose、when、otherwise

有时候，不想使用所有的条件，而只是想==从多个条件中选择一个==使用。

还是上面的例子改变一下：此时只能满足一个筛选条件，<font color=red>如果前端传来住院号就只按照住院号查找；如果传来床位号就只按照床位号筛选；如果什么都没传，就筛选所有在院的</font>。此时的查询如下：

```xml
<select id="selectPats" resultType="com.xxx.domain.PatientInfo">
  select * from patient_info where 1=1
  <choose>
    <!--住院号iptNum不为null时，根据住院号查找-->
    <when test="iptNum != null">
      AND ipt_num = #{iptNum}
    </when>
      
    <!--床位号不是NUll-->
    <when test="bedNum != null">
      AND bed_num = #{bedNum}
    </when>
      
    <otherwise>
      AND status = 1
    </otherwise>
  </choose>
</select>
```

MyBatis 提供了 `choose` 元素，按顺序判断 `when` 中的条件出否成立，如果有一个成立，则 `choose` 结束。当 `choose` 中所有 `when` 的条件都不满则时，则执行 `otherwise` 中的 sql。

类似于 Java 的 `switch` 语句，`choose` 为 `switch`，`when` 为 `case`，`otherwise` 则为`default`。

## where

对于`choose`标签的例子中的查询，如果去掉`where`后的`1=1`此时的SQL语句会变成什么样子，有三种可能的SQL，如下：

```xml
<select id="selectPats" resultType="com.xxx.domain.PatientInfo">
    select * from patient_info where AND ipt_num=#{iptNum};
</select>

<select id="selectPats" resultType="com.xxx.domain.PatientInfo">
    select * from patient_info where AND bed_num = #{bedNum};
</select>

<select id="selectPats" resultType="com.xxx.domain.PatientInfo">
    select * from patient_info where AND status=1;
</select>
```

如何解决呢？此时就要用到`where`这个标签了。

<font color=red>`where` 元素只会在子元素返回任何内容的情况下才插入 `WHERE` 子句。而且，若子句的开头为 `AND` 或 `OR`，`where` 元素也会将它们去除</font>。

```xml
<select id="selectPats" resultType="com.xxx.domain.PatientInfo">
  select * from patient_info
    <where>
        <choose>
          <!--住院号不为null时，根据住院号查找-->
          <when test="iptNum != null">
            AND ipt_num=#{iptNum}
          </when>
            
          <!--床位号不是NUll-->
          <when test="bedNum != null">
            AND bed_num = #{bedNum}
          </when>
            
          <otherwise>
            AND status=1
          </otherwise>
        </choose>
   </where>
</select>
```

和 where 元素等价的自定义 trim 元素为：

```mysql
<trim prefix="WHERE" prefixOverrides="AND |OR ">
  ...
</trim>
```

*prefixOverrides* 属性会<font color=red>忽略通过管道符分隔的文本序列</font>（注意此例中的==空格是必要的==）。上述例子会移除所有 *prefixOverrides* 属性中指定的内容，并且插入 *prefix* 属性中指定的内容。

## set（更新）

用于动态==更新==语句的类似解决方案叫做 *set*。*set* 元素可以用于动态包含需要更新的列，==忽略其它不更新的列==。

```xml
<!--示例1-->
<update id="updateStudent" parameterType="Object">
    UPDATE STUDENT
    SET NAME = #{name},
    MAJOR = #{major},
    HOBBY = #{hobby}
    WHERE ID = #{id};
</update>

<!--示例2-->
<update id="updateStudent" parameterType="Object">
    UPDATE STUDENT SET
    <if test="name!=null and name!='' ">
        NAME = #{name},
    </if>
    <if test="hobby!=null and hobby!='' ">
        MAJOR = #{major},
    </if>
    <if test="hobby!=null and hobby!='' ">
        HOBBY = #{hobby}
    </if>
    WHERE ID = #{id};
</update>
```

- 上面的例子1中没有使用 `if` 标签时，如果有一个参数为 `null`，都会导致错误。

- 当在例子2的 `update` 语句中使用 `if` 标签时，<font color=red>如果最后的 `if` 没有执行，则或导致逗号多余错误</font>。

使用 `set` 标签可以将<font color=red>动态地配置 `set` 关键字</font>，和<font color=red>剔除追加到条件末尾的任何不相关的逗号</font>。

使用 `set + if` 标签修改后，<font color=red>如果某项为 null 则不进行更新，而是保持数据库原值</font>：

```mysql
<update id="updateStudent" parameterType="Object">
    UPDATE STUDENT
    <set>
        <if test="name!=null and name!='' ">
            NAME = #{name},
        </if>
        
        <if test="hobby!=null and hobby!='' ">
            MAJOR = #{major},
        </if>
        
        <if test="hobby!=null and hobby!='' ">
            HOBBY = #{hobby}
        </if>
    </set>
    WHERE ID = #{id};
</update>
```



与 set 元素等价的自定义 trim 元素：

```mysql
<trim prefix="SET" suffixOverrides=",">
  ...
</trim>
```



## trim

Mybatis的**trim**标签一般用于去除sql语句中==多余==的`and`关键字，`逗号`，或者给sql语句前拼接 `where`、`set`以及`values(` 等==前缀==，或者添加`)`等==后缀==。可用于选择性插入、更新、删除或者条件查询等操作<sup><a href="#ref4">[4]</a></sup>。

以下是trim标签中涉及到的属性：

| 属性            | 描述                                                         |
| --------------- | ------------------------------------------------------------ |
| prefix          | 给sql语句拼接的前缀                                          |
| suffix          | 给sql语句拼接的后缀                                          |
| prefixOverrides | 去除sql语句前面的关键字或者字符，该关键字或者字符由prefixOverrides属性指定，假设该属性指定为"AND"，当sql语句的开头为"AND"，trim标签将会去除该"AND" |
| suffixOverrides | 去除sql语句后面的关键字或者字符，该关键字或者字符由suffixOverrides属性指定 |

### 使用trim标签去除多余的and关键字

```xml
<select id="findActiveBlogLike" resultType="Blog">
  SELECT * FROM BLOG 
  WHERE 
      <if test="state != null">
          state = #{state}
      </if> 
    
      <if test="title != null">
          AND title like #{title}
      </if>
    
      <if test="author != null and author.name != null">
          AND author_name like #{author.name}
      </if>
</select>
```

如果这些条件没有一个能匹配上会发生什么？最终这条 SQL 会变成这样：

```xml
SELECT * FROM BLOG
WHERE
```

如果仅仅第二个条件匹配又会怎样？这条 SQL 最终会是这样：

```xml
SELECT * FROM BLOG
WHERE 
AND title like ‘someTitle’
```

可以使用`where标签`来解决这个问题，<font color=red>where 元素只会在至少有一个子元素的条件返回 SQL 子句的情况下才去插入“WHERE”子句</font>。而且，<font color=red>若语句的开头为“AND”或“OR”，where 元素也会将它们去除</font>：

```xml
<select id="findActiveBlogLike" resultType="Blog">
  SELECT * FROM BLOG 
  <where> 
    <if test="state != null">
         state = #{state}
    </if> 
      
    <if test="title != null">
        AND title like #{title}
    </if>
      
    <if test="author != null and author.name != null">
        AND author_name like #{author.name}
    </if>
  </where>
</select>
```

`trim标签`也可以完成相同的功能：

```xml
<select id="findActiveBlogLike" resultType="Blog">
    SELECT * FROM BLOG 
    
    <trim prefix="WHERE" prefixOverrides="AND">
        <if test="state != null">
            state = #{state}
        </if> 

        <if test="title != null">
            AND title like #{title}
        </if>

        <if test="author != null and author.name != null">
            AND author_name like #{author.name}
        </if>
    </trim>
</select>    
```

### 使用trim标签去除多余的逗号

```xml
<inser id="insertRole" parameterType="com.example.role" useGeneratedKeys="true" keyProperty="id">
	INSERT INTO role (
            <if test="roleName != null">
                role_name,
            </if>
            <if test="note != null">
                note
            </if>
        )
        VALUES (
            <if test="roleName != null">
                #{roleName},
            </if>
            <if test="note != null">
                #{note}
            </if>
        )
</inser>
```

如果note条件没有匹配上，sql语句会变成如下：

```xml
INSERT INTO role(role_name,) VALUES(#{roleName},)
```

插入将会失败。

使用trim标签可以解决此问题：

```xml
INSERT INTO role 
<trim prefix="(" suffix=")" suffixOverride=",">
    <if test="roleName != null">
        role_name,
    </if>
    <if test="note != null">
        note
    </if>
</trim>
```



## foreach

`foreach`是用来对集合的遍历，这个和Java中的功能很类似。通常处理SQL中的`in`语句。

`foreach` 元素的功能非常强大，它允许你指定一个集合，声明可以在元素体内使用的==集合项==（`item`）和==索引==（`index`）变量。它也允许你指定==开头与结尾的字符串==以及==集合项迭代之间的分隔符==。这个元素也<font color=red>不会错误地添加多余的分隔符</font>。

你可以将任何可迭代对象（如 `List`、`Set` 等）、`Map` 对象或者`数组`对象作为集合参数传递给 foreach。当使用==可迭代对象==或者==数组==时，`index` 是==当前迭代的序号==，`item` 的值是==本次迭代获取到的元素==。当使用 `Map` 对象（或者 `Map.Entry` 对象的集合）时，`index` 是键，`item` 是值。

对集合进行遍历：

```mysql
<select id="selectPats" resultType="com.xxx.domain.PatientInfo">
  SELECT *
  FROM patient_info 
  WHERE ID in
  <foreach item="item" index="index" collection="list" open="(" separator="," close=")">
        #{item}
  </foreach>
</select>
```

标签中的各个属性的含义如下：

| 属性      | 含义                                     |
| :-------- | :--------------------------------------- |
| item      | 表示在迭代过程中==每一个元素==的别名     |
| index     | 表示在迭代过程中每次迭代到的位置（下标） |
| open      | 前缀，表示==该语句以什么开始==           |
| close     | 后缀，表示==该语句以什么结束==           |
| separator | 分隔符，表示迭代时每个元素之间以什么分隔 |

### collection 属性

1. 如果传入的是==单参数==且参数类型是一个==List==的时候，collection属性值为==list==；
2. 如果传入的是==单参数==且参数类型是一个==array数组==的时候，collection的属性值为==array==；
3. 如果传入的参数是==多个==的时候，就需要把它们封装成一个Map了，当然单参数也可以封装成map，实际上如果你在传入参数的时候，在breast里面也是会把它封装成一个Map的，map的key就是参数名，所以这个时候collection属性值就是传入的List或array对象在自己封装的map里面的==key==。

## SQL 片段

在实际开发中会遇到许多==相同的SQL==，比如根据某个条件筛选，这个筛选很多地方都能用到，我们可以将其抽取出来成为一个==公用的部分==，这样修改也方便，一旦出现了错误，只需要改这一处便能处处生效了，此时就用到了`<sql>`这个标签了。

当多种类型的查询语句的==查询字段==或者==查询条件==相同时，可以将其定义为==常量==，方便调用。为求 `<select>` 结构清晰也可将 sql 语句分解。如下：

```xml
<!-- 查询字段 -->
<sql id="Base_Column_List">
    ID, MAJOR, BIRTHDAY, AGE, NAME, HOBBY
</sql>

<!-- 查询条件 -->
<sql id="Example_Where_Clause">
    where 1=1
    <trim suffixOverrides=",">
        <if test="id != null and id !=''">
            and id = #{id}
        </if>
        <if test="major != null and major != ''">
            and MAJOR = #{major}
        </if>
        <if test="birthday != null ">
            and BIRTHDAY = #{birthday}
        </if>
        <if test="age != null ">
            and AGE = #{age}
        </if>
        <if test="name != null and name != ''">
            and NAME = #{name}
        </if>
        <if test="hobby != null and hobby != ''">
            and HOBBY = #{hobby}
        </if>
    </trim>
</sql>
```

## include

`include`用于引用`sql`标签定义的常量：

```xml
<select id="selectAll" resultMap="BaseResultMap">
    SELECT
    <include refid="Base_Column_List" />
    FROM student
    <include refid="Example_Where_Clause" />
</select>
```

`refid`这个属性就是指定`<sql>`标签中的`id`值（唯一标识）。

## Mybatis中如何避免魔数

何为`魔数`？

简单的说就是一个数字，一个只有你知道，别人不知道这个代表什么意思的数字。通常我们在Java代码中都会定义一个常量类专门定义这些数字。

比如获取医生和护士的权限，但是医生和护士的权限都不相同，在这条SQL中肯定需要根据登录的类型`type`来区分，比如`type=1`是医生，`type=2`是护士，估计一般人会这样写：

```xml
<if test="type!=null and type==1">
    -- ....获取医生的权限
</if>

<if test="type!=null and type==2">
    -- ....获取护士的权限
</if>
```

这样写一旦这个`type`代表的含义变了，那你是不是涉及到的SQL都要改一遍。

开发中通常定义一个常量类：

```java
package com.xxx.core.Constants;
public class CommonConstants{
    //医生
    public final static int DOC_TYPE = 1;

    //护士
    public final static int NUR_TYPE = 2;
}
```

此时的SQL可以写成：

```xml
<if test="type!=null and type==@com.xxx.core.Constants.CommonConstants@DOC_TYPE">
    -- ....获取医生的权限
</if>

<if test="type!=null and type==@com.xxx.core.Constants.CommonConstants@NUR_TYPE">
    -- ....获取护士的权限
</if>
```

就是`@`+`全类名`+`@`+`常量`。

## 如何引用其他XML中的SQL片段

实际开发中你可能遇到一个问题，比如这个`resultMap`或者这个`<sql>`片段已经在另外一个`xxxMapper.xml`中已经定义过了，此时当前的xml还需要用到，难不成我复制一份？小白什么也不问上来就复制了，好吧，后期修改来了，每个地方都需要修改了。难受不？

其实Mybatis中也是支持<font color=red>引用其他Mapper文件中的SQL片段</font>的。其实很简单，比如你在`com.xxx.dao.xxMapper`这个Mapper的XML中定义了一个SQL片段如下：

```xml
<sql id="Base_Column_List">
    ID, MAJOR, BIRTHDAY, AGE, NAME, HOBBY
</sql>
```

此时在`com.xxx.dao.PatinetDao`中的XML文件中需要引用：

```xml
<include refid="com.xxx.dao.xxDao.Base_Column_List"></include>
```

`<select>`标签中的`resultMap`同样可以这么引用！

# 批量处理<sup><a href="ref5">[5]</a></sup>

## 批量查询

### 传入ID查询

dao 接口：

```java
List<Situation> getByIds(String[] ids);
```

mapper.xml：

```xml
<select id="getByIds" parameterType="java.util.Arrays" resultMap="situationMap">
    SELECT *
    FROM situation s
    WHERE s.id in
    <foreach collection="array" open="(" separator="," close=")" index="index" item="item">
         #{item}
    </foreach>
</select>
```

### map传入多参数查询

dao 接口：

```java
 List<Alert> getByIds(Map<String, Object> map);
```

mapper.xml：

```xml
<select id="getByIds" parameterType="map" resultMap="situationMap">
    SELECT *
    FROM situation s
    WHERE s.id in
    <foreach index="index" item="item" open="("  separator="," close=")"  collection="ids">
        #{item}
    </foreach>
</select>
```

注意：此处 map 的 key 就是 foreach 中 collection 的 value

```java
Map<String, Object> map = new HashMap<>();
map.put("ids", ids.toString().split(","));
getByIds(map);
```

## 批量插入

dao 接口：

```java
void batchCreateRelation(List<CmdbxxxRelation> list);
```

mapper.xml：

```xml
<insert id="batchCreateRelation" parameterType="java.util.List">
    INSERT INTO cmdb_xxx_relation (
        source_type,
        source_target,
        dest_type,
        dest_target,
        dest_name,
        relation_type,
        relation_direction
    )
    VALUES
    <foreach collection="list" item="item" index="index" separator=",">
        (
            #{item.sourceType},
            #{item.sourceTarget},
            #{item.destType},
            #{item.destTarget},
            #{item.destName},
            #{item.relationType},
            #{item.relationDirection}
        )
    </foreach>
</insert>
```

## 批量更新

mybatis 的批量更新 主要依靠 foreach 标签拼接 sql 实现批量操作。

```xml
<update id="batchUpdateRawEventStatus" parameterType="java.util.List">
    UPDATE raw_event
    SET status = 1
    WHERE id in
    <foreach collection="list" item="item" index="index" open="(" separator="," close=")">
        ${item.id}
    </foreach>
</update>

<!-- 接收list参数，循环着组装sql语句，注意for循环的写法 separator=";" 代表着【每次循环完，在sql后面放一个分号】 -->
<update id="updateForeachByUserId" parameterType="java.util.List">
    <foreach collection="list" item="item" separator=";">
        UPDATE lp_user_test_batch
        SET
            user_name = #{item.userName},
            user_age = #{item.userAge},
            type = #{item.type},
            update_time = #{item.updateTime}
        WHERE 
            user_id = #{item.userId}
    </foreach>
</update>
```

## 批量删除

```xml
<!-- 批量删除用户 -->
<delete id="batchDelete" parameterType="java.util.List">
    DELETE FROM `test`.`tb_user` 
    WHERE id 
    IN
    <foreach collection="list" item="item" open="(" separator="," close=")">
        #{id}
    </foreach>
</delete>
```



# 实例

以User表为例<sup><a href="#ref2">[2]</a></sup>：

| id       | int     |
| -------- | ------- |
| username | varchar |
| sexual   | varchar |
| birth    | date    |
| address  | varchar |

根据 username 和 sexual 来查询数据。如果 username 为空，那么将只根据 sexual 来查询；反之只根据 username 来查询。

首先不使用动态SQL来书写：

```mysql
<select id = "selectUserByUserNameAndSexual" resultType = "User" parameterType = "com.example.entity.User">
	SELECT * FROM user
	WHERE username = #{username} AND sexual = #{sexual};
</select>
```

如果 `#{username}` 为空，那么查询结果也是空，如何解决这个问题呢？

## 查询

### if 语句

```mysql
<select id = "selectUserByUserNameAndSexual" resultType = "User" parameterType = "com.example.entity.User">
	SELECT * FROM user
	WHERE
		<if test = "username != null">
			username = #{username}
		</if>
		
		<if test = "sexual != null">
			AND sexual = #{sexual}
		</if>
</select>
```

如果 sexual 为 null，那么查询语句为：

```mysql
select * from user where username=#{username}
```

<font color=red>但是如果 usename 为空呢</font>？

那么查询语句为：

```mysql
select * from user where and sex=#{sex}
```

这是错误的 SQL 语句，如何解决呢？请看下面的 where 语句

### if + where 语句

```mysql
<select id = "selectUserByUserNameAndSexual" resultType = "User" parameterType = "com.example.entity.User">
	SELECT * FROM user
	<where>
		<if test = "username != null">
			username = #{username}
		</if>
		
		<if test = "sexual != null">
			AND sexual = #{sexual}
		</if>
	</where>
</select>
```

<font color=red>如果 where 标签包含的标签中有返回值，它就插入一个`where`。此外，如果标签返回的内容是以 AND 或 OR 开头的，则它会剔除掉</font>。

### trim 语句

用 trim 改写上面的 if + where 语句：

```mysql
<select id = "selectUserByUserNameAndSexual" resultType = "User" parameterType = "com.example.entity.User">
	SELECT * FROM user
	<trim prefix = "where" prefixOverrides = "and | or">
		<if test = "username != null">
			AND username = #{username}
		</if>
		
		<if test = "sexual != null">
			AND sexual = #{sexual}
		</if>
	</trim>
</select>
```

prefix：给sql语句拼接的前缀　　　　　　

prefixoverride：去掉and或者是or



### choose(when, otherwise) 语句

如果不想用到所有的查询条件，只想选择其中的一个，即<font color=red>查询条件有一个满足即可</font>，使用 choose 标签可以解决此类问题（类似于 Java 的 switch 语句）。

```mysql
<select id = "selectUserByChoose" resultType = "com.example.entity.User" parameterType = "com.example.entity.Use">
	SELECT * FROM user
	<where>
		<choose>
			<when test = "id != '' and id != null">
				id = #{id}
			</when>
			
			<when test = "username != '' and username != null">
				AND username = #{username}
			</when>
			
			<otherwise>
				AND sexual = #{sexual}
			</otherwise>
		</choose>
	</where>
</select>
```

也就是说，这里有三个条件，id、username、sexual，<font color=red>只能选择一个作为查询条件</font>：

- 如果 id 不为空，那么查询语句为：

  ```mysql
  select * from user where  id=?
  ```

  

- 如果 id 为空，那么看 username 是否为空

  - 如果不为空，那么语句为

    ```mysql
    select * from user where username=?
    ```

    

  - 如果 username 为空，那么查询语句为：

    ```mysql
    select * from user where sexual=?
    ```

    

## 更新

### if + set 语句

```mysql
<update id = "updateUserById" parameterType = "com.example.entity.User">
	UPDATE user
	<set>
		<if test = "username != null and username != ''">
			username = #{username},
		</if>
		
		<if test = "sexual != null and sexual != ''">
			sexual = #{sexual}
		</if>
	</set>
	WHERE id = #{id}
</update>
```

如果第一个条件 username 为空，那么 sql 语句为：

```mysql
update user u set u.sex=? where id=?
```

如果第一个条件不为空，那么 sql 语句为：

```mysql
update user u set u.username = ? ,u.sex = ? where id=?
```

### trim 语句

```mysql
<update id = "updateUserById" parameterType = "com.example.entity.User">
	UPDATE user
	<trim prefix = "set" suffixOverrides = ",">
		<if test = "username != null and username != ''">
			username = #{username},
		</if>
		<if test = "sexual != null and sexual != ''">
			sexual = #{sexual},
		</if>
	</trim>
	WHERE id = #{id}
</update>
```

suffix：给sql语句拼接的后缀　　

suffixoverride：去掉最后一个逗号

##  SQL 片段

有时候可能某个 sql 语句用的特别多，为了增加代码的重用性，简化代码，需要将这些代码抽取出来，然后使用时直接调用。

比如：假如需要经常根据用户名和性别来进行联合查询，那么就可以把这个代码抽取出来：

```mysql
<!-- 定义 sql 片段 -->
<sql id = "selectUserByUserNameAndSexualSQL">
    <if test = "username != null and username != ''">
        AND username = #{username}
    </if>
    <if test = "sexual != null and sexual != ''">
        AND sexual = #{sexual}
    </if>
</sql>
```

引用 sql 片段：

```mysql
<select id="selectUserByUsernameAndSex" resultType="user" parameterType="com.ys.po.User">
    select * from user
    <trim prefix="where" prefixOverrides="and | or">
        <!-- 引用 sql 片段，如果refid 指定的不在本文件中，那么需要在前面加上 namespace -->
        <include refid="selectUserByUserNameAndSexSQL"></include>
        <!-- 在这里还可以引用其他的 sql 片段 -->
    </trim>
</select>
```

注意：① 最好基于==单表==来定义 sql 片段，提高片段的可重用性

　　　② 在 sql 片段中最好==不要包括 where== 

## foreach 语句

查询 user 表中 id 分别为1、2、3的用户：

```mysql
select * from user where id = 1 or id = 2 or id = 3

select * from user where id in (1, 2, 3)
```

建立一个 UserVo 类，里面封装一个 `List<Integer> ids` 的属性：

```java
import java.util.List;
 
public class UserVo {
    // 封装多个用户的id
    private List<Integer> ids;
 
    public List<Integer> getIds() {
        return ids;
    }
 
    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }
}
```

用 foreach 来改写 select 语句：

```mysql
<select id="selectUserByListId" parameterType="com.ys.vo.UserVo" resultType="com.ys.po.User">
    select * from user
    <where>
        <!--
            collection：指定输入对象中的集合属性
            item：每次遍历生成的对象
            open：开始遍历时的拼接字符串
            close：结束时拼接的字符串
            separator：遍历对象之间需要拼接的字符串
            select * from user where 1=1 and (id=1 or id=2 or id=3)
          -->
        <foreach collection="ids" item="id" open="and (" separator="or" close=")">
            id=#{id}
        </foreach>
    </where>
</select>

<select id="selectUserByListId" parameterType="com.ys.vo.UserVo" resultType="com.ys.po.User">
        select * from user
        <where>
            <!--
                collection：指定输入对象中的集合属性
                item：每次遍历生成的对象
                open：开始遍历时的拼接字符串
                close：结束时拼接的字符串
                separator：遍历对象之间需要拼接的字符串
                select * from user where 1=1 and id in (1,2,3)
              -->
            <foreach collection="ids" item="id" open="and id in (" separator="," close=")">
                #{id}
            </foreach>
        </where>
</select>
```



# 参考资料

[1] [动态 SQL_MyBatis中文网](https://mybatis.net.cn/dynamic-sql.html)

<span name="ref2">[2] [mybatis 详解（五）------动态SQL](https://www.cnblogs.com/ysocean/p/7289529.html)</span>

<span name="ref3">[3] [Mybatis动态SQL，你真的会了吗](https://mp.weixin.qq.com/s?__biz=MzU3MDAzNDg1MA==&mid=2247484002&idx=1&sn=41cc146a68039730bfd964da7ee372d0&chksm=fcf4ddafcb8354b9ea8df508d085c6fcba66e2f3548381c20e3abcde08087a357ff7fc8702e3&scene=178&cur_album_id=1500819225232343046#rd)</span>

<span name="ref4">[4] [mybatis trim标签的使用](https://blog.csdn.net/wt_better/article/details/80992014)</span>

<span name="ref5">[5] [mybatis 的批量操作，查询、更新、插入](https://blog.csdn.net/u012373815/article/details/55061504)</span>

