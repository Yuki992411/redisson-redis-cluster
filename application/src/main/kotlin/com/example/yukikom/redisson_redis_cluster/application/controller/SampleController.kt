package com.example.yukikom.redisson_redis_cluster.application.controller

import com.example.yukikom.redisson_redis_cluster.application.annotation.PreventDuplicate
import com.example.yukikom.redisson_redis_cluster.application.service.SampleService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * # サンプルコントローラー
 *
 * 重複リクエスト防止機能の動作検証用APIエンドポイント
 *
 * ## 動作検証用curlコマンド
 *
 * ### 1. 単一リクエスト（/api/sample1 - 重複防止あり）
 * ```bash
 * curl -i -X POST http://localhost:8080/api/sample1
 * ```
 * **期待結果**: `200 OK`（5秒後にレスポンス）
 *
 * ### 2. 並行リクエスト（/api/sample1 - 重複防止あり）
 * ```bash
 * curl -i -X POST http://localhost:8080/api/sample1 &
 * curl -i -X POST http://localhost:8080/api/sample1 &
 * wait
 * ```
 * **期待結果**:
 * - 1つ目: `200 OK`（5秒後）
 * - 2つ目: `409 Conflict`（即座に返却）
 *
 * ### 3. 並行リクエスト（/api/sample2 - 重複防止なし）
 * ```bash
 * curl -i -X POST http://localhost:8080/api/sample2 &
 * curl -i -X POST http://localhost:8080/api/sample2 &
 * wait
 * ```
 * **期待結果**: 両方とも`200 OK`（並行処理で5秒後）
 */
@RestController
@RequestMapping("/api")
class SampleController(
    private val sampleService: SampleService,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/sample1")
    @PreventDuplicate // Uses default timeout from configuration
    fun processSampleWithDuplicatePrevention(): ResponseEntity<Void> {
        logger.debug("Processing sample1 request with duplicate prevention")
        sampleService.processSample()
        return ResponseEntity.ok().build()
    }

    @PostMapping("/sample2")
    fun processSampleWithoutDuplicatePrevention(): ResponseEntity<Void> {
        logger.debug("Processing sample2 request without duplicate prevention")
        sampleService.processSample()
        return ResponseEntity.ok().build()
    }
}
