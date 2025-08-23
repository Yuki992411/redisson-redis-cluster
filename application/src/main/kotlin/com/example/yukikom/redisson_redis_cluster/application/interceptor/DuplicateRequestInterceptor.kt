package com.example.yukikom.redisson_redis_cluster.application.interceptor

import com.example.yukikom.redisson_redis_cluster.application.annotation.PreventDuplicate
import com.example.yukikom.redisson_redis_cluster.infrastructure.config.RedisProperties
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import java.util.concurrent.TimeUnit

@Component
class DuplicateRequestInterceptor(
    private val redissonClient: RedissonClient,
    private val redisProperties: RedisProperties,
) : HandlerInterceptor {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val activeLocks = ThreadLocal<RLock?>()

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        if (handler !is HandlerMethod) {
            return true
        }

        val annotation = handler.getMethodAnnotation(PreventDuplicate::class.java) ?: return true

        val lockKey = "lock:api:${request.requestURI}"
        val lock = redissonClient.getLock(lockKey)
        val timeout =
            if (annotation.lockTimeoutSeconds > 0) {
                annotation.lockTimeoutSeconds
            } else {
                redisProperties.lock.defaultTimeoutSeconds
            }

        logger.debug("Attempting to acquire lock for key: {}", lockKey)

        val acquired =
            try {
                lock.tryLock(0, timeout, TimeUnit.SECONDS)
            } catch (e: InterruptedException) {
                logger.error("Thread interrupted while acquiring lock", e)
                Thread.currentThread().interrupt()
                false
            } catch (e: Exception) {
                logger.error("Error acquiring lock for key: {}", lockKey, e)
                false
            }

        if (!acquired) {
            logger.info("Duplicate request detected for URI: {}", request.requestURI)
            response.status = HttpStatus.CONFLICT.value()
            response.contentType = "application/json;charset=UTF-8"
            response.writer.write("{\"error\":\"Duplicate request in progress\"}")
            return false
        }

        activeLocks.set(lock)
        logger.debug("Lock acquired for key: {}", lockKey)
        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?,
    ) {
        val lock = activeLocks.get()
        if (lock != null && lock.isHeldByCurrentThread) {
            try {
                lock.unlock()
                logger.debug("Lock released for key: {}", lock.name)
            } catch (e: Exception) {
                logger.error("Error releasing lock", e)
            } finally {
                activeLocks.remove()
            }
        }
    }
}
