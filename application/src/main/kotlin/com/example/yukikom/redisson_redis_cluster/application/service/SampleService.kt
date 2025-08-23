package com.example.yukikom.redisson_redis_cluster.application.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SampleService {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val processingTimeMillis = 5000L

    fun processSample() {
        logger.info("Sample processing started")

        Thread.sleep(processingTimeMillis)

        logger.info("Sample processing completed")
    }
}
