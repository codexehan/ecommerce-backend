package codexe.han.inventory.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {
    /*@Bean
    JedisConnectionFactory jedisConnectionFactory() {
       *//* RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration("172.28.2.22", 6379);
   //     redisStandaloneConfiguration.setPassword();
        return new JedisConnectionFactory(redisStandaloneConfiguration);*//*

        JedisConnectionFactory jedisConFactory
                = new JedisConnectionFactory();
        jedisConFactory.setHostName("172.28.2.22");
        jedisConFactory.setPort(6379);
        return jedisConFactory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }*/
    //spring boot版本问题经常会导致bean创建失败
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName("172.28.2.22");
        return jedisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, Long> redisTemplate() {
        RedisTemplate<String, Long> template = new RedisTemplate<String, Long>();
        /*template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<Long>(Long.class));*/
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }
}
