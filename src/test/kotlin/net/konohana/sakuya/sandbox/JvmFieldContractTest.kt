package net.konohana.sakuya.sandbox

import java.lang.reflect.Modifier
import net.konohana.sakuya.sandbox.bridge.KotlinBridgeConsumer
import net.konohana.sakuya.sandbox.bridge.TicketDirectAccessDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class JvmFieldContractTest {

    private val clazz = TicketDirectAccessDto::class.java

    @Test
    fun `verify JvmField generates public field without getter`() {
        // 1. public フィールドが存在するか検証
        val field = clazz.getDeclaredField("directReadOnlyField")
        assertNotNull(field)
        assertTrue(Modifier.isPublic(field.modifiers), "フィールドは public であるべき")
        assertTrue(Modifier.isFinal(field.modifiers), "val なので final であるべき")

        // 2. Getter メソッドが生成されていないことを検証
        val getter = clazz.declaredMethods.find { it.name == "getDirectReadOnlyField" }
        assertNull(getter, "Getter メソッドは生成されていてはならない")
    }

    @Test
    fun `verify companion JvmField generates public static final field`() {
        val staticField = clazz.getDeclaredField("STATIC_PREFIX")
        assertNotNull(staticField)
        assertTrue(Modifier.isPublic(staticField.modifiers))
        assertTrue(Modifier.isStatic(staticField.modifiers))
        assertTrue(Modifier.isFinal(staticField.modifiers))
    }

    @Test
    fun `verify Java consumer interacts directly with JvmFields`() {
        val dto = TicketDirectAccessDto()
        val result = KotlinBridgeConsumer.verifyJvmFieldAccess(dto)

        assertEquals("NORMAL:DIRECT_READ_ONLY:MODIFIED_DIRECTLY:QCTTTHB", result)
        assertEquals("MODIFIED_DIRECTLY", dto.directMutableField)
    }
}
