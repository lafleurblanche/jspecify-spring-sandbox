package net.konohana.sakuya.sandbox

import net.konohana.sakuya.sandbox.bridge.TicketCodeGenerator
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.lang.reflect.Method

class JvmSyntheticVisibilityTest {

    @Test
    fun `verify method annotated with JvmSynthetic has ACC_SYNTHETIC flag in bytecode`() {
        val clazz = TicketCodeGenerator::class.java

        // 1. @JvmSynthetic が付与されたメソッドを取得
        // バイトコード上は存在する（リフレクションでは取得可能）
        val syntheticMethod: Method? = clazz.declaredMethods.find { it.name == "internalKotlinDsl" }
        assertNotNull(syntheticMethod, "internalKotlinDsl メソッドはバイトコード上に存在していなければならない")

        // 2. ACC_SYNTHETIC フラグが付与されていることを検証
        // このフラグがあるため、javac はこのメソッドを「存在しないもの」として扱い、Java からの参照を拒絶する
        assertTrue(
            syntheticMethod!!.isSynthetic,
            "internalKotlinDsl は @JvmSynthetic により isSynthetic が true でなければならない"
        )
    }

    @Test
    fun `verify standard Kotlin methods do not have ACC_SYNTHETIC flag`() {
        val clazz = TicketCodeGenerator::class.java

        // 通常のメソッド（@JvmSynthetic なし）を取得
        val normalMethod: Method? = clazz.declaredMethods.find { it.name == "createRouteCode" }
        assertNotNull(normalMethod)

        // 通常のメソッドは isSynthetic が false（Java から通常通り参照可能）
        assertFalse(
            normalMethod!!.isSynthetic,
            "通常のメソッドは isSynthetic が false であるべき"
        )
    }

    @Test
    fun `verify Kotlin caller can execute JvmSynthetic method normally`() {
        val generator = TicketCodeGenerator()
        var executed = false

        // Kotlin コンパイラ（kotlinc）はこのフラグを無視して呼び出しを許可する
        generator.internalKotlinDsl {
            executed = true
        }

        assertTrue(executed, "Kotlin 側からは通常通り実行可能")
    }
}
