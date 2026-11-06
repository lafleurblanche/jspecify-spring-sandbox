package net.konohana.sakuya.sandbox.bridge

class TicketCodeGenerator {

    companion object {
        fun generateDefault(prefix: String): String {
            return "${prefix}00000OW"
        }

        @JvmStatic
        fun generateDirect(prefix: String): String {
            return "${prefix}99999OW"
        }
    }

    @JvmOverloads
    fun createRouteCode(
        routeId: String,
        subSystem: String = "THB",
        enablePadding: Boolean = true,
    ): String {
        val padded = if (enablePadding) routeId.padStart(3, '0') else routeId
        return "FR$subSystem$padded"
    }

    @JvmSynthetic
    fun internalKotlinDsl(block: () -> Unit) {
        block()
    }
}

@JvmName("filterValidStrings")
fun filterValid(items: List<String>): List<String> = items.filter { it.isNotBlank() }

@JvmName("filterValidNumbers")
fun filterValid(items: List<Int>): List<Int> = items.filter { it > 0 }
