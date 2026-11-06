package net.konohana.sakuya.sandbox

import net.konohana.sakuya.sandbox.domain.FareCalculationEngine
import net.konohana.sakuya.sandbox.models.LegacyStationDto
import net.konohana.sakuya.sandbox.services.TicketIssueService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class JSpecifyNullnessContractTest {

    private val ticketIssueService = TicketIssueService()

    @Test
    fun `FareCalculationEngine formatTicketCode returns non-null String`() {
        // @NullMarked により、プラットフォーム型(String!)ではなく厳格な非null String として解決される
        val code: String = FareCalculationEngine.formatTicketCode("QCTTTHB", 123)
        assertEquals("QCTTTHB00123OW", code)
    }

    @Test
    fun `FareCalculationEngine calculateTotalFare properly handles nullable baseFare`() {
        // 第2引数が @Nullable Integer のため、Kotlin の Int? がスムーズに渡せる
        val defaultFare = FareCalculationEngine.calculateTotalFare(5, null)
        assertEquals(600, defaultFare)

        val customFare = FareCalculationEngine.calculateTotalFare(5, 700)
        assertEquals(800, customFare)

        val cappedFare = FareCalculationEngine.calculateTotalFare(12, null)
        assertEquals(1000, cappedFare)
    }

    @Test
    fun `FareCalculationEngine extractStationSuffix returns nullable String`() {
        // @Nullable のため、Kotlin側では厳格に String? 型
        val validSuffix: String? = FareCalculationEngine.extractStationSuffix("FRTHB00101")
        assertEquals("01", validSuffix)

        val invalidSuffix: String? = FareCalculationEngine.extractStationSuffix("F")
        assertNull(invalidSuffix)
    }

    @Test
    fun `LegacyStationDto correctly preserves nullable and non-null fields`() {
        val dto = LegacyStationDto("FRTHB00101", null)
        val staCode: String = dto.staCode
        val staName: String? = dto.staName

        assertEquals("FRTHB00101", staCode)
        assertNull(staName)
    }

    @Test
    fun `TicketIssueService integration works without platform type leaks`() {
        val result = ticketIssueService.issueTicket(
            prefix = "QCTTTHB",
            sequenceNo = 456,
            distanceKm = 8,
            staCode = "FRTHB00102",
            customBaseFare = null,
        )

        assertEquals("QCTTTHB00456OW", result.ticketId)
        assertEquals(660, result.fare)
        assertEquals("02", result.stationSuffix)
    }
}
