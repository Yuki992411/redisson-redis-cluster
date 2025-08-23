package com.example.yukikom.redisson_redis_cluster.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "redis")
data class RedisProperties(
    val singleServer: SingleServerConfig = SingleServerConfig(),
    val lock: LockConfig = LockConfig(),
) {
    data class SingleServerConfig(
        val address: String = "redis://localhost:6379",
        val connectionMinimumIdleSize: Int = 1,
        val connectionPoolSize: Int = 10,
        val timeout: Int = 3000,
        val retryAttempts: Int = 3,
        val retryInterval: Int = 1500,
    )

    data class LockConfig(
        val defaultTimeoutSeconds: Long = 30,
    )
}
