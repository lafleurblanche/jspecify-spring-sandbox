package net.konohana.sakuya.sandbox.bridge

/**
 * [StationId] の解決処理を提供するサービスクラス。
 *
 * Value Class を引数に取るメソッドの Java 相互運用における名前マングリング挙動、
 * および `@JvmName` による明示的シグネチャの公開を検証します。
 */
class StationRouteResolver {

    /**
     * Kotlin 側から呼び出すための標準ルート解決メソッド。
     *
     * Java バイトコード上ではメソッド名がマングリング（ハッシュ付与）されます。
     *
     * @param id 解決対象の駅識別子
     * @return 解決されたルート文字列
     */
    fun resolve(id: StationId): String = "ROUTE-FOR-${id.formatted()}"

    /**
     * Java 側から名前マングリングを回避して直接呼び出すためのメソッド。
     *
     * バイトコード上は `resolveByRawStationId(String)` として公開されます。
     *
     * @param id 解決対象の駅識別子
     * @return 解決されたルート文字列
     */
    @JvmName("resolveByRawStationId")
    fun resolveWithExplicitName(id: StationId): String = "EXPLICIT-FOR-${id.formatted()}"
}
