package net.konohana.sakuya.sandbox.controllers

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import net.konohana.sakuya.sandbox.models.StationCatalogEntry
import net.konohana.sakuya.sandbox.services.StationAsyncCatalogService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.returnResult

/**
 * [StationAsyncCatalogController] に対する [WebTestClient] を用いた非同期エンドポイント結合テスト。
 *
 * コルーチン (suspend) の非同期レスポンス解決および [kotlinx.coroutines.flow.Flow] による
 * NDJSON ストリーミングの受信契約を検証します。
 */
class StationAsyncCatalogControllerIntegrationTest {

    private val catalogService: StationAsyncCatalogService = mockk()
    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun setUp() {
        val controller = StationAsyncCatalogController(catalogService)
        webTestClient = WebTestClient.bindToController(controller).build()
    }

    @Test
    fun `getStation returns 200 OK with station data when entry exists`() {
        val sampleEntry = StationCatalogEntry(
            staCode = "FRTHB00101",
            staName = "Sakuragaoka",
            active = true,
        )

        // suspend 関数のモックには coEvery を使用
        coEvery { catalogService.fetchStationByCode("FRTHB00101") } returns sampleEntry

        webTestClient.get()
            .uri("/api/async/stations/FRTHB00101")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody<StationCatalogEntry>()
            .isEqualTo(sampleEntry)
    }

    @Test
    fun `getStation returns 404 Not Found when service returns null`() {
        coEvery { catalogService.fetchStationByCode("UNKNOWN") } returns null

        webTestClient.get()
            .uri("/api/async/stations/UNKNOWN")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .expectBody().isEmpty
    }

    @Test
    fun `streamStations streams entries as NDJSON`() {
        val streamData = listOf(
            StationCatalogEntry("FRTHB00101", "Sakuragaoka", true),
            StationCatalogEntry("FRTHB00102", "Midorigawa", true),
            StationCatalogEntry("FRTHB00103", "Asahidai", false),
        )

        // Flow を返すメソッドのモックには flowOf を使用
        every { catalogService.streamStationsBySector("THB") } returns flowOf(*streamData.toTypedArray())

        val result = webTestClient.get()
            .uri("/api/async/stations/stream?sector=THB")
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult<StationCatalogEntry>()

        // Reactor StepVerifier 経由で各要素を順次アサート
        reactor.test.StepVerifier.create(result.responseBody)
            .expectNext(streamData[0])
            .expectNext(streamData[1])
            .expectNext(streamData[2])
            .verifyComplete()
    }
}
