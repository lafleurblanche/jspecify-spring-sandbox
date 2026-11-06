package net.konohana.sakuya.sandbox.bridge

/**
 * @JvmInline value class の検証クラス
 * Kotlin では StationId 型として扱われるが、バイトコード上は下層の String に展開される
 */
@JvmInline
value class StationId(val value: String) {
    init {
        require(value.isNotBlank()) { "StationId cannot be blank" }
    }

    fun formatted(): String = "STA-$value"
}
