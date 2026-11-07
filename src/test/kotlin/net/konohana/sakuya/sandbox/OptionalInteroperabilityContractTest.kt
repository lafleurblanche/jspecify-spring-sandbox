package net.konohana.sakuya.sandbox

import net.konohana.sakuya.sandbox.bridge.KotlinBridgeConsumer
import net.konohana.sakuya.sandbox.bridge.OptionalExtensions
import net.konohana.sakuya.sandbox.bridge.StationLookupBridge
import net.konohana.sakuya.sandbox.bridge.toNullable
import net.konohana.sakuya.sandbox.bridge.toOptional
import net.konohana.sakuya.sandbox.domain.StationCatalogLookupEngine
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.Optional

/**
 * Java ドメイン層の [Optional] と Kotlin の Null 許容型（[T]?）の相互運用検証テスト。
 */
class OptionalInteroperabilityContractTest {

    private val bridge = StationLookupBridge()

    @Test
    fun `verify Java Optional converts smoothly to Kotlin Nullable using toNullable`() {
        // 1. 値が存在するケース
        val presentOptional: Optional<String> = StationCatalogLookupEngine.findStationName("FRTHB00101")
        assertTrue(presentOptional.isPresent)

        val stationName: String? = presentOptional.toNullable()
        assertEquals("試験駅A01", stationName)

        // 2. 空のケース（null に安全に変換され、エルビス演算子が利用可能）
        val emptyOptional: Optional<String> = StationCatalogLookupEngine.findStationName("UNKNOWN_CODE")
        assertFalse(emptyOptional.isPresent)

        val fallbackName: String = emptyOptional.toNullable() ?: "Default Station"
        assertEquals("Default Station", fallbackName)
    }

    @Test
    fun `verify Kotlin Nullable converts to Java Optional using toOptional`() {
        // 1. null 値を Optional.empty() として Java メソッドに渡す
        val nullNotice: String? = null
        val emptyOptNotice: Optional<String> = nullNotice.toOptional()

        val resolvedDefault = StationCatalogLookupEngine.resolveDisplayName("UNKNOWN_CODE", emptyOptNotice)
        assertEquals("Unknown Station", resolvedDefault)

        // 2. non-null 値を Optional.of(...) として Java メソッドに渡す
        val customNotice: String? = "Special Shuttle Stop"
        val customOptNotice: Optional<String> = customNotice.toOptional()

        val resolvedCustom = StationCatalogLookupEngine.resolveDisplayName("UNKNOWN_CODE", customOptNotice)
        assertEquals("Special Shuttle Stop", resolvedCustom)
    }

    @Test
    fun `verify OptionalExtensions utility functions`() {
        val opt = Optional.of("Sample")
        val unwrapped: String? = OptionalExtensions.unwrapToNullable(opt)
        assertEquals("Sample", unwrapped)

        val wrapped: Optional<String> = OptionalExtensions.wrapToOptional("Sample")
        assertTrue(wrapped.isPresent)
        assertEquals("Sample", wrapped.get())

        val wrappedNull: Optional<String> = OptionalExtensions.wrapToOptional(null)
        assertFalse(wrappedNull.isPresent)
    }

    @Test
    fun `verify Kotlin facade returning Optional integrates cleanly via Java consumer`() {
        // 1. Kotlin 側からは通常のメソッド (findStationName: String?) として呼べる
        val kotlinResult: String? = bridge.findStationName("FRTHB00101")
        assertEquals("Verified-01", kotlinResult)

        // 2. Java 側 (KotlinBridgeConsumer) から @JvmName("queryStationAsOptional") 経由で呼べることを検証
        val presentResult: Optional<String> = KotlinBridgeConsumer.callBridgeAsOptional(bridge, "FRTHB00101")
        assertTrue(presentResult.isPresent)
        assertEquals("Verified-01", presentResult.get())

        val emptyResult: Optional<String> = KotlinBridgeConsumer.callBridgeAsOptional(bridge, "INVALID_PREFIX")
        assertFalse(emptyResult.isPresent)
        assertNull(emptyResult.toNullable())
    }
}
