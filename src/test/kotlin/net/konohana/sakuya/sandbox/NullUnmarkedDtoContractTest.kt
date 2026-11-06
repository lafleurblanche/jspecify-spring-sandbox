package net.konohana.sakuya.sandbox

import net.konohana.sakuya.sandbox.models.LegacyStationDto
import net.konohana.sakuya.sandbox.models.UnmarkedLegacyDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class NullUnmarkedDtoContractTest {

    @Test
    fun `NullMarked DTO enforces non-null on unmarked properties`() {
        val markedDto = LegacyStationDto("STA001", null)

        // staCode は @NullMarked により厳格な非null String
        val staCode: String = markedDto.staCode
        assertEquals("STA001", staCode)

        // staName は @Nullable のため String?
        val staName: String? = markedDto.staName
        assertNull(staName)
    }

    @Test
    fun `NullUnmarked DTO relaxes non-null constraint allowing nullable assignment without compile error`() {
        // @NullUnmarked により、Java 側の戻り値はプラットフォーム型（String!）に戻る
        val unmarkedDto = UnmarkedLegacyDto(null, "LEGACY_01")

        // 1. プラットフォーム型のため、Kotlin 側で String? として安全に代入・受領可能
        val nullablePayload: String? = unmarkedDto.rawPayload
        assertNull(nullablePayload)

        // 2. プラットフォーム型のため、Kotlin 側で String（非null）とみなして代入することも文法上可能
        // ただし値が null の場合は Kotlin の型アサーションチェックで NPE が発生する
        val legacyCode: String = unmarkedDto.legacyCode
        assertEquals("LEGACY_01", legacyCode)
    }

    @Test
    fun `NullUnmarked DTO allows passing null to setters without Kotlin compiler error`() {
        val unmarkedDto = UnmarkedLegacyDto("INITIAL", "CODE")

        // 引数もプラットフォーム型（String!）扱いのため、Kotlin 側から null を渡してもコンパイルエラーにならない
        unmarkedDto.rawPayload = null
        assertNull(unmarkedDto.rawPayload)
    }
}
