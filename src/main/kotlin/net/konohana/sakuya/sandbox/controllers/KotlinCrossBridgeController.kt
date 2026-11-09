package net.konohana.sakuya.sandbox.controllers

import net.konohana.sakuya.sandbox.models.FareQuoteRequest
import net.konohana.sakuya.sandbox.models.FareQuoteResponse
import net.konohana.sakuya.sandbox.services.FareQuoteService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Kotlin コントローラーから Java サービス ([FareQuoteService]) を呼び出す相互運用検証エンドポイント。
 *
 * @property fareQuoteService Java 側で実装された見積りサービス
 */
@RestController
@RequestMapping("/api/cross/kotlin-to-java")
class KotlinCrossBridgeController(
    private val fareQuoteService: FareQuoteService,
) {

    /**
     * Java サービスを呼び出して運賃見積りを算出します。
     *
     * @param request 見積りリクエスト (Java record)
     * @return 見積りレスポンス (Java record)
     */
    @PostMapping("/quote")
    fun requestQuote(@RequestBody request: FareQuoteRequest): ResponseEntity<FareQuoteResponse> {
        if (request.fromStaCode().isBlank() || request.toStaCode().isBlank()) {
            return ResponseEntity.badRequest().build()
        }
        val response = fareQuoteService.calculateQuote(request)
        return ResponseEntity.ok(response)
    }
}
