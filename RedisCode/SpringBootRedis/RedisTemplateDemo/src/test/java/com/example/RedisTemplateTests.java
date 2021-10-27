package com.example;

import com.example.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * @author chenzufeng
 * @date 2021/10/27
 * @usage RedisTemplateTests
 */
@SpringBootTest(classes = RedisTemplateDemoApplication.class)
public class RedisTemplateTests {
    /**
     * 注入RedisTemplate（Key和Value都是Object）
     * 必须将对象序列化才能存入redis中
     */
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testRedisTemplate() {
        User user = new User();
        user.setId(UUID.randomUUID().toString())
                .setName("zufeng")
                .setAge(28)
                .setBirth(new Date());
        redisTemplate.opsForValue().set("user", user);
        User user1 = (User) redisTemplate.opsForValue().get("user");
        System.out.println(user1);
    }

    /**
     * 查看key的序列化策略
     */
    @Test
    public void testKeySerializer() {
        RedisSerializer keySerializer = redisTemplate.getKeySerializer();
        System.out.println(keySerializer);
    }

    @Test
    public void testSetKeySerializer() {
        // 修改Key序列化方案：修改为String类型序列
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        User user = new User();
        user.setId(UUID.randomUUID().toString())
                .setName("zufeng")
                .setAge(28)
                .setBirth(new Date());
        redisTemplate.opsForValue().set("user", user);
        User user1 = (User) redisTemplate.opsForValue().get("user");
        System.out.println(user1);

        // 查看所有的key
        Set keys = redisTemplate.keys("*");
        keys.forEach(key -> System.out.println(key));
    }

    @Test
    public void testOther() {
        // 修改Key序列化方案：修改为String类型序列
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        User user = new User();
        user.setId(UUID.randomUUID().toString())
                .setName("zufeng")
                .setAge(28)
                .setBirth(new Date());

        // 下列操作只有一个key
        redisTemplate.opsForList().leftPush("userList", user);
        redisTemplate.opsForSet().add("userSet", user);
        redisTemplate.opsForZSet().add("userZSet", user, 100);
    }

    @Test
    public void testSetHashKey() {
        // 修改Key序列化方案：修改为String类型序列
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 修改hash key序列化策略
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        User user = new User();
        user.setId(UUID.randomUUID().toString())
                .setName("zufeng")
                .setAge(28)
                .setBirth(new Date());

        redisTemplate.opsForHash().put("maps", "user", user);
    }
}
