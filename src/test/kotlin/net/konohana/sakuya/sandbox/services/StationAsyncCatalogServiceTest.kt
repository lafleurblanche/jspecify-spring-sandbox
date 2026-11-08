package net.konohana.sakuya.sandbox.services

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

/**
 * [StationAsyncCatalogService] の Coroutines / Flow 非同期通信テスト。
 */
class StationAsyncCatalogServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: StationAsyncCatalogService

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val webClient = WebClient.builder()
            .baseUrl(mockWebServer.url("/").toString())
            .build()

        service = StationAsyncCatalogService(webClient)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `fetchStationByCode returns entry when response is 200 OK`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(
                    """
                    {
                        "staCode": "FRTHB00101",
                        "staName": "試験駅A01",
                        "active": true
                    }
                    """.trimIndent(),
                ),
        )

        val result = service.fetchStationByCode("FRTHB00101")

        assertNotNull(result)
        assertEquals("FRTHB00101", result?.staCode)
        assertEquals("試験駅A01", result?.staName)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/v1/stations/FRTHB00101", recordedRequest.path)
    }

    @Test
    fun `fetchStationByCode returns null on 404 Not Found without throwing exception`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("{\"error\": \"Not Found\"}"),
        )

        val result = service.fetchStationByCode("UNKNOWN")
        assertNull(result)
    }

    @Test
    fun `streamStationsBySector streams elements into Kotlin Flow`() = runTest {
        val ndjsonBody = """
            {"staCode":"FRTHB00101","staName":"試験駅A01","active":true}
            {"staCode":"FRTHB00102","staName":"試験駅A02","active":true}
            {"staCode":"FRTHB00103","staName":"試験駅A03","active":false}
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_NDJSON_VALUE)
                .setBody(ndjsonBody),
        )

        val flow = service.streamStationsBySector("THB")
        val collectedList = flow.toList()

        assertEquals(3, collectedList.size)
        assertEquals("試験駅A01", collectedList[0].staName)
        assertEquals("試験駅A02", collectedList[1].staName)
        assertEquals("試験駅A03", collectedList[2].staName)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/v1/stations/stream?sector=THB", recordedRequest.path)
    }
}
