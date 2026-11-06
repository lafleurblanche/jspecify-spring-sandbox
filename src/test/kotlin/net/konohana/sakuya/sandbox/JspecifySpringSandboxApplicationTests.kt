package net.konohana.sakuya.sandbox

import net.konohana.sakuya.sandbox.domain.FareCalculationEngine
import net.konohana.sakuya.sandbox.domain.InMemoryTicketRepository
import net.konohana.sakuya.sandbox.models.IssueTicketRequest
import net.konohana.sakuya.sandbox.models.LegacyStationDto
import net.konohana.sakuya.sandbox.services.StationExternalLookupService
import net.konohana.sakuya.sandbox.services.TicketIssueService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * JSpecify による Null 契約および Java ⇄ Kotlin 境界におけるプラットフォーム型排除の検証テスト。
 */
class JSpecifyNullnessContractTest {

    // 依存関係を明示的にインスタンス化して注入
    private val ticketRepository = InMemoryTicketRepository()
    private val stationLookupService = StationExternalLookupService()
    private val ticketIssueService = TicketIssueService(ticketRepository, stationLookupService)

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
        // 最新の DTO 引数仕様に合わせて呼び出し
        val request = IssueTicketRequest(
            prefix = "QCTTTHB",
            distanceKm = 8,
            staCode = "FRTHB00102",
            customBaseFare = null,
        )
        val result = ticketIssueService.processIssue(request)

        assertEquals("QCTTTHB00001OW", result.ticketId)
        assertEquals(660, result.fare)
        assertEquals("02", result.stationSuffix)
        assertEquals("STA-FRTHB00102", result.stationId)
    }
}
