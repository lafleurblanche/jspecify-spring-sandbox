package net.konohana.sakuya.sandbox.controllers

import net.konohana.sakuya.sandbox.models.FareQuoteRequest
import net.konohana.sakuya.sandbox.models.FareQuoteResponse
import net.konohana.sakuya.sandbox.services.FareQuoteService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

/**
 * Kotlin コントローラーから Java サービス ([FareQuoteService]) への相互連携テスト。
 * static import を排し、クラス名修飾による呼び出しで統一しています。
 */
@ExtendWith(MockitoExtension::class)
class KotlinToJavaCrossBridgeTest {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var fareQuoteService: FareQuoteService

    @BeforeEach
    fun setUp() {
        val controller = KotlinCrossBridgeController(fareQuoteService)
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

    @Test
    fun `verify Kotlin controller delegates to Java service and returns Java record response`() {
        val mockResponse = FareQuoteResponse(
            "FRTHB00101",
            "FRTHB00103",
            600,
            "From Sakuragaoka (2 km)",
        )

        Mockito.`when`(fareQuoteService.calculateQuote(ArgumentMatchers.any(FareQuoteRequest::class.java)))
            .thenReturn(mockResponse)

        val requestJson = """
            {
                "fromStaCode": "FRTHB00101",
                "toStaCode": "FRTHB00103",
                "customBaseFare": 500
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/cross/kotlin-to-java/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.fromStaCode").value("FRTHB00101"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.toStaCode").value("FRTHB00103"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalFare").value(600))
            .andExpect(MockMvcResultMatchers.jsonPath("$.note").value("From Sakuragaoka (2 km)"))
    }
}
