package net.konohana.sakuya.sandbox

import net.konohana.sakuya.sandbox.bridge.KotlinBridgeConsumer
import net.konohana.sakuya.sandbox.bridge.TicketCodeGenerator
import net.konohana.sakuya.sandbox.bridge.filterValid
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KotlinBridgeConsumerTest {

    @Test
    fun `verify JvmStatic and companion object invocation from Java`() {
        // @JvmStatic により直接静的メソッドとして呼べる
        val directResult = KotlinBridgeConsumer.callDirectStatic("QC")
        assertEquals("QC99999OW", directResult)

        // Companion 経由で呼ぶ
        val companionResult = KotlinBridgeConsumer.callCompanionMethod("QC")
        assertEquals("QC00000OW", companionResult)
    }

    @Test
    fun `verify JvmOverloads allows parameter omission from Java`() {
        val generator = TicketCodeGenerator()

        // Java側から引数省略（デフォルト引数）で呼べる
        val defaultCallResult = KotlinBridgeConsumer.callWithDefaults(generator, "1")
        assertEquals("FRTHB001", defaultCallResult)

        // Kotlin側から全引数を指定して直接呼ぶパターン
        val customCallResult = generator.createRouteCode("1", subSystem = "SECT", enablePadding = false)
        assertEquals("FRSECT1", customCallResult)
    }

    @Test
    fun `verify JvmName resolves type erasure method signatures`() {
        // Java側（KotlinBridgeConsumer）から @JvmName("filterValidStrings") で呼ぶ
        val inputStrings = listOf("APPLE", "  ", "BANANA")
        val filteredFromJava = KotlinBridgeConsumer.callMangledMethod(inputStrings)
        assertEquals(listOf("APPLE", "BANANA"), filteredFromJava)

        // Kotlin側からは同名関数（オーバーロード）として自然に呼べる
        val validStrings = filterValid(listOf("A", "", "B"))
        val validNumbers = filterValid(listOf(1, -2, 3, 0))

        assertEquals(listOf("A", "B"), validStrings)
        assertEquals(listOf(1, 3), validNumbers)
    }

    @Test
    fun `verify JvmSynthetic executes normally within Kotlin context`() {
        val generator = TicketCodeGenerator()
        var executed = false

        // Java からは不可視だが、Kotlin からは問題なく呼べる
        generator.internalKotlinDsl {
            executed = true
        }

        assertEquals(true, executed)
    }
}
