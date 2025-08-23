package com.example.yukikom.redisson_redis_cluster.application

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.example.yukikom.redisson_redis_cluster"])
class RedissonRedisClusterApplication

fun main(args: Array<String>) {
	runApplication<RedissonRedisClusterApplication>(*args)
}
