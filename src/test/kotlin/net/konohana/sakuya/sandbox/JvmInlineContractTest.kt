package net.konohana.sakuya.sandbox

import net.konohana.sakuya.sandbox.bridge.KotlinBridgeConsumer
import net.konohana.sakuya.sandbox.bridge.StationId
import net.konohana.sakuya.sandbox.bridge.StationRouteResolver
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class JvmInlineContractTest {

    @Test
    fun `verify value class unboxes to underlying type in Java method parameter`() {
        val resolverClazz = StationRouteResolver::class.java

        // 1. アノテーションなしのメソッドは名前にマングリング（ハイフン等のハッシュ）が含まれる
        val mangledMethod = resolverClazz.declaredMethods.find { it.name.startsWith("resolve-") }
        assertNotNull(mangledMethod, "Value Class を受け取るメソッドはマングリングされる")
        // 引数の型は StationId ではなく String にアンボックスされている
        assertEquals(String::class.java, mangledMethod!!.parameterTypes[0])

        // 2. @JvmName を付けたメソッドは明示された名前で解決可能
        val explicitMethod = resolverClazz.declaredMethods.find { it.name == "resolveByRawStationId" }
        assertNotNull(explicitMethod)
        assertEquals(String::class.java, explicitMethod!!.parameterTypes[0])
    }

    @Test
    fun `verify Kotlin caller uses StationId type-safely`() {
        val resolver = StationRouteResolver()
        val id = StationId("001")

        // Kotlin 側では StationId として型安全に渡せる
        val result = resolver.resolve(id)
        assertEquals("ROUTE-FOR-STA-001", result)
    }

    @Test
    fun `verify Java consumer invokes unboxed method via JvmName`() {
        val resolver = StationRouteResolver()
        // Java 側からは展開された String を渡して呼び出せる
        val result = KotlinBridgeConsumer.verifyValueClassInvocation(resolver, "002")
        assertEquals("EXPLICIT-FOR-STA-002", result)
    }
}
