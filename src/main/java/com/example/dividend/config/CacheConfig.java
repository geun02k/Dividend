package com.example.dividend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// Redis Cache 사용을위한 기본적인 빈 설정
// = redisConnectionFactory(), redisCacheManager() 빈 설정

@Configuration
@RequiredArgsConstructor
public class CacheConfig {

    @Value("${spring.redis.host}") // 서비스가 초기화되는 과정에서 application.yml에서 셋팅한 값으로 변경됨.
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    // 2. 캐시에 적용해 사용하기 위해서는 캐시매니저빈을 추가로 생성필요.
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        // redis config (RedisConfiguration) 정보 셋팅
        // 직렬화 과정에서 어떤 Serialize를 사용할 것인지 결정.
        // serializeKeysWith() : 키 직렬화
        // serializeValuesWith() : 값 직렬화
        RedisCacheConfiguration conf =
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeKeysWith(RedisSerializationContext
                                .SerializationPair
                                .fromSerializer(new StringRedisSerializer()))
                        .serializeValuesWith(RedisSerializationContext
                                .SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        // 생성된 RedisConfiguration 인스턴스로 CacheManager 빌드해 리턴
        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(conf)
                .build();
    }

    // 1. Redis 서버와의 연결을 위해 Redis Connection Factory Bean 초기화
    //    (Connection Factory Bean 생성)
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // redis config 정보 셋팅
        // 클러스터로 레디스 서버를 구성하는 경우 RedisClusterConfiguration 으로 생성
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
        conf.setHostName(this.host);
        conf.setPort(this.port);

        // 레투스 커넥션 팩토리에 설정정보를 넣어 인스턴스 생성
        return new LettuceConnectionFactory(conf);
    }
}
