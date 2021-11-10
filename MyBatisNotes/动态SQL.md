# 动态 SQL

根据不同条件拼接 SQL 语句时要确保不能忘记添加必要的空格，还要注意去掉列表最后一个列名的逗号。利用动态 SQL，可以彻底摆脱这种痛苦。

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

## choose、when、otherwise

有时候，不想使用所有的条件，而只是想==从多个条件中选择一个==使用。

传入了 “title” 就按 “title” 查找，传入了 “author” 就按 “author” 查找的情形。若两者都没有传入，就返回标记为 featured 的 BLOG：

```mysql
<select id="findActiveBlogLike" resultType="Blog">
    SELECT * FROM BLOG 
    WHERE state = ‘ACTIVE’
    
    <choose>
        <when test="title != null">
            AND title like #{title}
        </when>
        
        <when test="author != null and author.name != null">
            AND author_name like #{author.name}
        </when>
        
        <otherwise>
            AND featured = 1
        </otherwise>
        
    </choose>
    
</select>
```

## trim、where、set

```mysql
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

如果没有匹配的条件，这条 SQL 会变成：

```mysql
SELECT * FROM BLOG
WHERE
```

这会导致查询失败。如果匹配的只是第二个条件，这条 SQL 会变成：

```mysql
SELECT * FROM BLOG
WHERE
AND title like ‘someTitle’
```

这个查询也会失败。

### where

```mysql
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

<font color=red>where 元素只会在子元素返回任何内容的情况下才插入 “WHERE” 子句</font>。而且，若子句的开头为 “AND” 或 “OR”，where 元素也会将它们去除。

### trim

和 where 元素等价的自定义 trim 元素为：

```mysql
<trim prefix="WHERE" prefixOverrides="AND |OR ">
  ...
</trim>
```

*prefixOverrides* 属性会<font color=red>忽略通过管道符分隔的文本序列</font>（注意此例中的==空格是必要的==）。上述例子会移除所有 *prefixOverrides* 属性中指定的内容，并且插入 *prefix* 属性中指定的内容。

### set

用于动态更新语句的类似解决方案叫做 *set*。*set* 元素可以用于动态包含需要更新的列，==忽略其它不更新的列==。比如：

```mysql
<update id="updateAuthorIfNecessary">
    update Author
    <set>
        <if test="username != null">
            username=#{username},
        </if>
        
        <if test="password != null">
            password=#{password},
        </if>
        
        <if test="email != null">
            email=#{email},
        </if>
        
        <if test="bio != null">
            bio=#{bio}
        </if>
    </set>
    where id=#{id}
</update>
```

这个例子中，set 元素会<font color=red>动态地在行首插入 SET 关键字，并会删掉额外的逗号</font>（这些逗号是在使用条件语句给列赋值时引入的）。

与 set 元素等价的自定义 trim 元素：

```mysql
<trim prefix="SET" suffixOverrides=",">
  ...
</trim>
```

## foreach

对集合进行遍历：

```mysql
<select id="selectPostIn" resultType="domain.blog.Post">
    SELECT *
    FROM POST P
    WHERE ID in
    <foreach item="item" index="index" collection="list" open="(" separator="," close=")">
        #{item}
    </foreach>
</select>
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
<select id = "selectUserByUserNameAndSexual" 
	resultType = "User" parameterType = "com.example.entity.User">
	SELECT * FROM user
	WHERE username = #{username} AND sexual = #{sexual};
</select>
```

如果 `#{username}` 为空，那么查询结果也是空，如何解决这个问题呢？

## 查询

### if 语句

```mysql
<select id = "selectUserByUserNameAndSexual"
	resultType = "User" parameterType = "com.example.entity.User">
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
<select id = "selectUserByUserNameAndSexual"
	resultType = "User" parameterType = "com.example.entity.User">
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
<select id = "selectUserByUserNameAndSexual"
	resultType = "User" parameterType = "com.example.entity.User">
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

prefix：前缀　　　　　　

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

suffix：后缀　　

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

注意：①最好基于==单表==来定义 sql 片段，提高片段的可重用性

　　　②、在 sql 片段中最好==不要包括 where== 

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

