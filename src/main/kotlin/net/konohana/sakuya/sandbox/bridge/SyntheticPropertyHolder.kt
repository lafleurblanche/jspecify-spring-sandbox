package net.konohana.sakuya.sandbox.bridge

/**
 * プロパティの Getter / Setter に対する @JvmSynthetic 遮蔽検証用クラス
 */
class SyntheticPropertyHolder(
    var visibleTitle: String = "VISIBLE",

    @get:JvmSynthetic
    var writeOnlyFromJava: String = "WRITE_ONLY",

    @get:JvmSynthetic
    @set:JvmSynthetic
    var completelyHiddenProperty: String = "HIDDEN",

    @get:JvmSynthetic
    val hiddenValProperty: String = "HIDDEN_VAL",
)
