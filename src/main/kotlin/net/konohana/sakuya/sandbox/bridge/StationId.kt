package net.konohana.sakuya.sandbox.bridge

/**
 * 駅識別子を表すインライン値クラス（Value Class）。
 *
 * `@JvmInline` により、実行時および JVM バイトコード上は内包する [String] に展開され、
 * オブジェクトアロケーションのオーバーヘッドなしに型安全性を保証します。
 *
 * @property value 駅コードの生文字列
 * @throws IllegalArgumentException 空白文字列が指定された場合
 */
@JvmInline
value class StationId(val value: String) {
    init {
        require(value.isNotBlank()) { "StationId cannot be blank" }
    }

    /**
     * 表示用にプレフィックスを付与した形式へフォーマットします。
     *
     * @return プレフィックス付き駅識別文字列
     */
    fun formatted(): String = "STA-$value"
}
