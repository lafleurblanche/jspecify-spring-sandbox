package net.konohana.sakuya.sandbox.services

import net.konohana.sakuya.sandbox.bridge.StationId
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class StationExternalLookupService(
    private val webClientBuilder: WebClient.Builder = WebClient.builder(),
) {
    // 外部APIを叩くクライアント（今回はフォールバック付きの軽量スタブ設計）
    private val webClient: WebClient = webClientBuilder.baseUrl("http://localhost:8080/api/mock").build()

    fun verifyStation(stationId: StationId): Boolean {
        // StationId は @JvmInline の Value Class なので、実行時は生の String として軽量に扱われる
        val rawCode = stationId.value
        return rawCode.isNotBlank()
    }
}
