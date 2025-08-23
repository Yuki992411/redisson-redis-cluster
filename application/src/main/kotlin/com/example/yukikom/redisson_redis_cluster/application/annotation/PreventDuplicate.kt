package com.example.yukikom.redisson_redis_cluster.application.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class PreventDuplicate(
    // -1 means use default from configuration
    val lockTimeoutSeconds: Long = -1,
)
