# 动态 SQL

根据不同条件拼接 SQL 语句时要确保不能忘记添加必要的空格，还要注意去掉列表最后一个列名的逗号。利用动态 SQL，可以彻底摆脱这种痛苦。

# if

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

# choose、when、otherwise

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

# trim、where、set

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

## where

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

## trim 

和 where 元素等价的自定义 trim 元素为：

```mysql
<trim prefix="WHERE" prefixOverrides="AND |OR ">
  ...
</trim>
```

*prefixOverrides* 属性会<font color=red>忽略通过管道符分隔的文本序列</font>（注意此例中的==空格是必要的==）。上述例子会移除所有 *prefixOverrides* 属性中指定的内容，并且插入 *prefix* 属性中指定的内容。

## set

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

# foreach

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



# 参考资料

https://mybatis.net.cn/dynamic-sql.html