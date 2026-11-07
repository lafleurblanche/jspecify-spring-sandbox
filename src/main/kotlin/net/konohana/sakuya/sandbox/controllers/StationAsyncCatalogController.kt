package net.konohana.sakuya.sandbox.controllers

import kotlinx.coroutines.flow.Flow
import net.konohana.sakuya.sandbox.models.StationCatalogEntry
import net.konohana.sakuya.sandbox.services.StationAsyncCatalogService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Coroutines ([suspend] / [Flow]) を活用した非同期駅カタログ REST コントローラー。
 *
 * @property catalogService 非同期カタログ照会サービス
 */
@RestController
@RequestMapping("/api/async/stations")
class StationAsyncCatalogController(
    private val catalogService: StationAsyncCatalogService,
) {

    /**
     * 単一駅情報取得エンドポイント (suspend)。
     *
     * @param staCode 駅コード
     * @return 駅情報 (200 OK)、または 404 Not Found
     */
    @GetMapping("/{staCode}")
    suspend fun getStation(@PathVariable staCode: String): ResponseEntity<StationCatalogEntry> {
        val entry = catalogService.fetchStationByCode(staCode)
        return if (entry != null) {
            ResponseEntity.ok(entry)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    /**
     * 系統別駅情報ストリーミングエンドポイント (Flow)。
     *
     * @param sector 系統コード
     * @return 駅情報のストリーム (application/x-ndjson)
     */
    @GetMapping(path = ["/stream"], produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun streamStations(@RequestParam sector: String): Flow<StationCatalogEntry> {
        return catalogService.streamStationsBySector(sector)
    }
}
