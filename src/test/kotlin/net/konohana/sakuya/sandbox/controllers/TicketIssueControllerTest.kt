package net.konohana.sakuya.sandbox.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.junit.jupiter.api.Test

@SpringBootTest
@AutoConfigureMockMvc
class TicketIssueControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should successfully issue ticket and return 201 Created`() {
        val payload = """
            {
                "prefix": "QCTTTHB",
                "distanceKm": 12,
                "staCode": "FRTHB00101",
                "customBaseFare": null
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/tickets/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.ticketId").isString)
            .andExpect(jsonPath("$.fare").value(1000))
            .andExpect(jsonPath("$.stationSuffix").value("01"))
            .andExpect(jsonPath("$.stationId").value("STA-FRTHB00101"))
    }

    @Test
    fun `should return 400 Bad Request when staCode is blank`() {
        // StationId の init { require(value.isNotBlank()) } により IllegalArgumentException が発生する
        val invalidPayload = """
            {
                "prefix": "QCTTTHB",
                "distanceKm": 5,
                "staCode": "   ",
                "customBaseFare": null
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/tickets/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").value("StationId cannot be blank"))
    }

    @Test
    fun `should handle CORS preflight request successfully`() {
        mockMvc.perform(
            options("/api/tickets/issue")
                .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
        )
            .andExpect(status().isOk)
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:3000"))
            .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"))
    }
}
