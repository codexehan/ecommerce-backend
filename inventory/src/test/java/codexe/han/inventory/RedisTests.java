package codexe.han.inventory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void getNonExist(){
        System.out.println(redisTemplate.opsForValue().increment("sssss",-1));
    }

    @Test
    public void watchNonExist() {
        redisTemplate.watch("ddddd");
        Integer res = (Integer) redisTemplate.opsForValue().get("ddddd");
        if (res == null) {
            redisTemplate.setEnableTransactionSupport(true);
            redisTemplate.multi();
            redisTemplate.opsForValue().set("ddddd", 1);
            redisTemplate.exec();

        }
    }
}
