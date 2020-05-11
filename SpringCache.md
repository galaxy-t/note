# SpringCache
    
    Spring 提供的缓存框架 , 一般基于 SpringBoot 我都是直接缓存如 Redis
    
### 配置

    引入 spring data redis 的依赖
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    
    解决乱码的配置 , RedisTemplate 这玩意具体怎么着我忘了 , 貌似某本书上说的这玩意会乱码 , 确实是会乱码 , 换个就好了 , 以下是一个配置文件 , 直接引入就可以了
    import com.fasterxml.jackson.annotation.JsonAutoDetect;
    import com.fasterxml.jackson.annotation.PropertyAccessor;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import org.springframework.cache.CacheManager;
    import org.springframework.cache.annotation.CachingConfigurerSupport;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.data.redis.cache.RedisCacheConfiguration;
    import org.springframework.data.redis.cache.RedisCacheManager;
    import org.springframework.data.redis.connection.RedisConnectionFactory;
    import org.springframework.data.redis.core.RedisTemplate;
    import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
    import org.springframework.data.redis.serializer.RedisSerializationContext;
    import org.springframework.data.redis.serializer.RedisSerializer;
    import org.springframework.data.redis.serializer.StringRedisSerializer;
    
    
    /**
     * SpringCache 配置
     */
    @Configuration
    public class CacheConfig extends CachingConfigurerSupport {
    
        @Bean(name="redisTemplate")
        public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
    
            RedisTemplate<String, String> template = new RedisTemplate<>();
            RedisSerializer<String> redisSerializer = new StringRedisSerializer();
    
            Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
            ObjectMapper om = new ObjectMapper();
            om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
            jackson2JsonRedisSerializer.setObjectMapper(om);
    
            template.setConnectionFactory(factory);
            //key序列化方式
            template.setKeySerializer(redisSerializer);
            //value序列化
            template.setValueSerializer(jackson2JsonRedisSerializer);
            //value hashmap序列化
            template.setHashValueSerializer(jackson2JsonRedisSerializer);
    
            return template;
        }
        @Bean
        public CacheManager cacheManager(RedisConnectionFactory factory) {
            RedisSerializer<String> redisSerializer = new StringRedisSerializer();
            Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
    
            // 配置序列化
            RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
            RedisCacheConfiguration redisCacheConfiguration = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer));
    
            RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
                    .cacheDefaults(redisCacheConfiguration)
                    .build();
            return cacheManager;
        }
    }
    
### 启动

    启动类添加 @EnableCaching 注解来启动 SpringCache
    
### 使用

    @Cacheable(value = "CacheVo",key = "#id")
    先检查缓存中是否存在
    若存在则直接使用缓存中的数据
    若不存在则先进行查询然后再存入缓存中
    
    @CachePut(value = "cacheVo",key = "#id")
    跟 Cacheable 类似 , 但每次都会进入方法中执行一遍
    
    @CacheEvict(value = "cacheVo",key = "#id")
    删除缓存
    每次都会进入到方法中去
    
### 注

#### key 的使用

    @Cacheable(value = "USER", key = "#id")
    public String getUserBaseInfo(Long id)
    此时存入redis 中的 key 的格式应该是 USER::1234 ,假设传入的 id 为 1234
    
    如果传入的是对象,如
    @CachePut(value = RedisKey.CACHE_USER_INFO, key = "#baseInfoDto.id")
    public String editUserInfo(UserBaseInfoDto baseInfoDto)
    如上所示 , 也可以直接引用到对象中的 id
    
#### SpringCache 的注解必须使用到 public 方法上

#### 同一个类中的 a 方法去调用拥有 SpringCache 注解的 b 方法 , b 方法的缓存注解不会生效
    
    