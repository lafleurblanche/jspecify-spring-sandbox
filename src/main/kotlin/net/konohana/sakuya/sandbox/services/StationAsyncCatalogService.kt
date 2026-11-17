package net.konohana.sakuya.sandbox.services

import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchangeOrNull
import org.springframework.web.reactive.function.client.bodyToFlow
import org.springframework.web.reactive.function.client.createExceptionAndAwait
import net.konohana.sakuya.sandbox.models.StationCatalogEntry

/**
 * [WebClient] と Kotlin Coroutines (`suspend` / [Flow]) を統合した非同期カタログ照会サービス。
 *
 * ノンブロッキング I/O により外部 API と非同期に通信し、404 Not Found 時の Null 安全ハンドリングや
 * NDJSON ストリーミングの受信を行います。
 *
 * @property webClient 通信用 WebClient インスタンス
 */
@Service
class StationAsyncCatalogService(
    private val webClient: WebClient,
) {

    /**
     * 指定された駅コードの情報を非同期に単一取得します。
     *
     * 該当駅が存在しない (HTTP 404) 場合は例外をスローせず、安全に `null` を返却します。
     * その他の 4xx / 5xx エラー時は [org.springframework.web.reactive.function.client.WebClientResponseException] をスローします。
     *
     * @param staCode 検索対象の駅コード
     * @return 取得した駅情報。存在しない場合は `null`
     * @throws org.springframework.web.reactive.function.client.WebClientResponseException 404 以外の HTTP エラーが発生した場合
     */
    suspend fun fetchStationByCode(staCode: String): StationCatalogEntry? =
        webClient.get()
            .uri("/api/v1/stations/{staCode}", staCode)
            .accept(MediaType.APPLICATION_JSON)
            .awaitExchangeOrNull { response ->
                when (response.statusCode()) {
                    HttpStatus.OK -> response.awaitBody<StationCatalogEntry>()
                    HttpStatus.NOT_FOUND -> null
                    else -> throw response.createExceptionAndAwait()
                }
            }

    /**
     * 指定された系統コードに属する駅一覧をストリーミング形式で受信します。
     *
     * [bodyToFlow] により、Reactor Flux から Kotlin [Flow] へのバックプレッシャー変換を行います。
     *
     * @param sectorCode 系統識別コード (例: "THB")
     * @return 駅情報の非同期ストリーム [Flow]
     */
    fun streamStationsBySector(sectorCode: String): Flow<StationCatalogEntry> =
        webClient.get()
            .uri("/api/v1/stations/stream?sector={sector}", sectorCode)
            .accept(MediaType.APPLICATION_NDJSON, MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlow<StationCatalogEntry>()
}
