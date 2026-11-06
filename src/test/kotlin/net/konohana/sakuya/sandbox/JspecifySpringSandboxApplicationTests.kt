package net.konohana.sakuya.sandbox

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull

@SpringBootTest
class JspecifySpringSandboxApplicationTests {

    @Test
    fun `formatTicketCode returns non-null string without platform types`() {
        val code: String = FareCalculationEngine.formatTicketCode("QCTTTHB", 123)
        assertEquals("QCTTTHB00123OW", code)
    }

    @Test
    fun `calculateTotalFare properly applies fallback when baseFare is null`() {
        val defaultFare = FareCalculationEngine.calculateTotalFare(5, null)
        assertEquals(600, defaultFare)

        val cappedFare = FareCalculationEngine.calculateTotalFare(12, null)
        assertEquals(1000, cappedFare)
    }

    @Test
    fun `extractStationSuffix returns nullable string handled safely by Kotlin`() {
        val validSuffix: String? = FareCalculationEngine.extractStationSuffix("FRTHB00101")
        assertEquals("01", validSuffix)

        val invalidSuffix: String? = FareCalculationEngine.extractStationSuffix("F")
        assertNull(invalidSuffix)
    }

}
