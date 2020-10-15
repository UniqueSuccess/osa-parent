package com.goldencis.osa;

/**
 * Created by limingchao on 2018/10/11.
 */

import com.goldencis.osa.common.utils.RedisUtil;
import com.goldencis.osa.core.entity.Department;
import com.goldencis.osa.core.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void test() throws Exception {
        // 保存字符串
        stringRedisTemplate.opsForValue().set("k1:k2:k3", "111");
        Assert.assertEquals("111", stringRedisTemplate.opsForValue().get("aaa"));
    }

    @Test
    public void redisConfigTest() {
        redisUtil.set("test", "li");
        Assert.assertEquals("li", redisUtil.get("test"));
    }

    @Test
    public void hGetSetTest() {
        redisUtil.hset("HKey", "InnerKey", "v1");
        Assert.assertEquals("v1", redisUtil.hget("HKey", "InnerKey"));
    }

    @Test
    public void expireTest() {
        redisUtil.expire("aaa", 20);
        redisUtil.set("bbb", "222", 20);
    }

    @Test
    public void objectTest() {
        User user = new User();
        user.setGuid(UUID.randomUUID().toString());
        user.setUsername("li");
        user.setName("李");
        user.setCreateTime(LocalDateTime.now());
        Department department = new Department();
        department.setId(1);
        user.setDepartment(department);
        redisUtil.set("user", user);
        Object userRedis = redisUtil.get("user");
        System.out.println(userRedis.toString());
    }
}