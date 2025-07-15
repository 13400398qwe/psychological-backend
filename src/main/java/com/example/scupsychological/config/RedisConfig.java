package com.example.scupsychological.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfig {

    /**
     * 自定义一个 RedisTemplate 的 Bean，覆盖 Spring Boot 的默认配置
     * @param connectionFactory Spring Boot 自动配置好的连接工厂
     * @return 定制化的 RedisTemplate
     */
    @Bean
    @SuppressWarnings("all") // 压制所有警告，因为我们将要进行一些类型转换
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        // 1. 创建 RedisTemplate 对象
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 2. 创建 JSON 序列化工具
        Jackson2JsonRedisSerializer<Object> jacksonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域(field,get,set)，以及修饰符范围(ANY)
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非 final 修饰的。
        // 在 json 中会记录对象类型，以便反序列化时能准确地转换为原始类型
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSerializer.setObjectMapper(om);

        // 3. 创建 String 序列化工具
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // 4. 设置 Key 和 HashKey 的序列化方式为 String
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // 5. 设置 Value 和 HashValue 的序列化方式为 JSON
        template.setValueSerializer(jacksonSerializer);
        template.setHashValueSerializer(jacksonSerializer);

        // 6. 初始化 RedisTemplate
        template.afterPropertiesSet();

        return template;
    }
}