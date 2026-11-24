package net.konohana.sakuya.sandbox.bridge

import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaGetter
import kotlin.reflect.jvm.javaSetter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Kotlin 側からのアクセサ可視性・挙動テスト")
class KotlinSyntheticPropertyTest {

    @Test
    @DisplayName("1. Kotlin プロパティ構文での全プロパティの読み書き検証")
    fun testDirectPropertyAccess() {
        val holder = SyntheticPropertyHolder(
            visibleTitle = "INIT_TITLE",
            writeOnlyFromJava = "INIT_WRITE",
            completelyHiddenProperty = "INIT_HIDDEN",
            hiddenValProperty = "INIT_VAL"
        )

        // visibleTitle
        holder.visibleTitle = "KOTLIN_TITLE"
        assertEquals("KOTLIN_TITLE", holder.visibleTitle)

        // writeOnlyFromJava (Java では呼べない Getter も Kotlin からは読める)
        holder.writeOnlyFromJava = "KOTLIN_WRITE"
        assertEquals("KOTLIN_WRITE", holder.writeOnlyFromJava)

        // completelyHiddenProperty (Java からは完全不可視だが Kotlin からは var として扱える)
        holder.completelyHiddenProperty = "KOTLIN_HIDDEN"
        assertEquals("KOTLIN_HIDDEN", holder.completelyHiddenProperty)

        // hiddenValProperty (val のため読み取りのみ)
        assertEquals("INIT_VAL", holder.hiddenValProperty)
    }

    @Test
    @DisplayName("2. completelyHiddenProperty (var) の KProperty メタデータ・Synthetic フラグ検証")
    fun testCompletelyHiddenPropertyReflection() {
        val kClass = SyntheticPropertyHolder::class

        val hiddenProp = kClass.memberProperties.find { it.name == "completelyHiddenProperty" }
        assertNotNull(hiddenProp, "Kotlin リフレクションからは @JvmSynthetic プロパティも取得可能である必要がある")

        val instance = SyntheticPropertyHolder(completelyHiddenProperty = "REFLECTION_VAL")
        assertEquals("REFLECTION_VAL", hiddenProp?.getter?.call(instance))

        // Getter の検証
        val backingJavaGetter = hiddenProp?.javaGetter
        assertNotNull(backingJavaGetter)
        assertTrue(backingJavaGetter!!.isSynthetic, "Getter に ACC_SYNTHETIC が付与されている必要がある")

        // Setter の検証: var なので KMutableProperty に安全キャスト可能
        val mutableProp = hiddenProp as? KMutableProperty<*>
        assertNotNull(mutableProp, "var プロパティのため KMutableProperty にキャスト可能である必要がある")

        val backingJavaSetter = mutableProp?.javaSetter
        assertNotNull(backingJavaSetter)
        assertTrue(backingJavaSetter!!.isSynthetic, "Setter に ACC_SYNTHETIC が付与されている必要がある")
    }

    @Test
    @DisplayName("3. hiddenValProperty (val) の KProperty 検証: Setter 不在および Getter の Synthetic 検証")
    fun testHiddenValPropertyReflection() {
        val kClass = SyntheticPropertyHolder::class

        val valProp = kClass.memberProperties.find { it.name == "hiddenValProperty" }
        assertNotNull(valProp, "Kotlin リフレクションから hiddenValProperty が取得可能である必要がある")

        val instance = SyntheticPropertyHolder(hiddenValProperty = "VAL_REFLECTION")
        assertEquals("VAL_REFLECTION", valProp?.getter?.call(instance))

        // 1. Getter の検証: ACC_SYNTHETIC が付与されていること
        val backingJavaGetter = valProp?.javaGetter
        assertNotNull(backingJavaGetter, "背後の Java Getter が存在する必要がある")
        assertTrue(backingJavaGetter!!.isSynthetic, "Getter に ACC_SYNTHETIC が付与されている必要がある")

        // 2. Setter 不在の検証 (Kotlin 型システム上)
        // val プロパティであるため、KMutableProperty へのキャストは null (失敗) になること
        assertFalse(
            valProp is KMutableProperty<*>,
            "val プロパティは KMutableProperty のインスタンスであってはならない"
        )
        val mutableProp = valProp as? KMutableProperty<*>
        assertNull(mutableProp, "KMutableProperty へのキャスト結果は null である必要がある")

        // 3. Setter 不在の検証 (Callable Reference による型安全な検証)
        // SyntheticPropertyHolder::hiddenValProperty は KProperty1 (不変) であり、
        // コンパイルレベルで Setter や javaSetter の呼び出しが遮断されている
        val callableProp = SyntheticPropertyHolder::hiddenValProperty
        assertNotNull(callableProp.javaGetter)
        assertTrue(callableProp.javaGetter!!.isSynthetic)
    }
}
