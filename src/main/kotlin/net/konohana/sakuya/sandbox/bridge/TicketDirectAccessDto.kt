package net.konohana.sakuya.sandbox.bridge

/**
 * @JvmField の検証クラス
 */
class TicketDirectAccessDto(
    // 通常のプロパティ: Javaからは getNormalField() 経由でアクセス
    val normalField: String = "NORMAL",

    // @JvmField 付与: Getter が生成されず、Java から public final String として直接参照
    @JvmField
    val directReadOnlyField: String = "DIRECT_READ_ONLY",

    // var に付与: Getter/Setter が生成されず、Java から public String として直接代入可能
    @JvmField
    var directMutableField: String = "DIRECT_MUTABLE",
) {
    companion object {
        // companion object 内のプロパティ
        // 通常は Companion.getDEFAULT_PREFIX() になるが、@JvmField で static public final フィールド化
        @JvmField
        val STATIC_PREFIX: String = "QCTTTHB"
    }
}
