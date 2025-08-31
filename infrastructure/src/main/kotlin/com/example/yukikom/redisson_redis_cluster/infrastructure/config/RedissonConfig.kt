package com.example.yukikom.redisson_redis_cluster.infrastructure.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(RedisProperties::class)
class RedissonConfig(
    private val redisProperties: RedisProperties,
) {
    @Bean
    @ConditionalOnProperty(name = ["redis.enabled"], havingValue = "true", matchIfMissing = true)
    fun redissonClient(): RedissonClient {
        val config =
            Config().apply {
                useSingleServer().apply {
                    address = redisProperties.singleServer.address
                    connectionMinimumIdleSize = redisProperties.singleServer.connectionMinimumIdleSize
                    connectionPoolSize = redisProperties.singleServer.connectionPoolSize
                    timeout = redisProperties.singleServer.timeout
                    retryAttempts = redisProperties.singleServer.retryAttempts
                    retryInterval = redisProperties.singleServer.retryInterval
                }
            }

        return Redisson.create(config)
    }
}
