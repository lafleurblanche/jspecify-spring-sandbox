package net.konohana.sakuya.sandbox.bridge

class TicketCodeGenerator {

    companion object {
        fun generateDefault(prefix: String): String = "${prefix}00000OW"

        @JvmStatic
        fun generateDirect(prefix: String): String = "${prefix}99999OW"
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
