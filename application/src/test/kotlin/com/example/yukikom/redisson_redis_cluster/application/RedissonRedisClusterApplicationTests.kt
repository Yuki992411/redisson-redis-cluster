package com.example.yukikom.redisson_redis_cluster.application

import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.redisson.api.RedissonClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Import(RedissonRedisClusterApplicationTests.TestRedisConfig::class)
class RedissonRedisClusterApplicationTests {
    @TestConfiguration
    class TestRedisConfig {
        @Bean
        fun mockRedissonClient(): RedissonClient = mock(RedissonClient::class.java)
    }

    @Test
    fun contextLoads() {
    }
}
