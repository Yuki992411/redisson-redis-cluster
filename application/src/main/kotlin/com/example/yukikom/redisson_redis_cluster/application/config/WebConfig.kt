package com.example.yukikom.redisson_redis_cluster.application.config

import com.example.yukikom.redisson_redis_cluster.application.interceptor.DuplicateRequestInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val duplicateRequestInterceptor: DuplicateRequestInterceptor,
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry
            .addInterceptor(duplicateRequestInterceptor)
            .addPathPatterns("/api/**")
    }
}
