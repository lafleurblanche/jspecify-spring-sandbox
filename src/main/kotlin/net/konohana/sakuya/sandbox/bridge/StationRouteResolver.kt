package net.konohana.sakuya.sandbox.bridge

/**
 * value class を引数に取るサービスクラス
 * Java から呼ぶ際、メソッド名がマングリングされる挙動を検証
 */
class StationRouteResolver {

    // Kotlin 側: resolve(id: StationId): String
    // Java バイトコード側: resolve-[mangledHash](String id) になる
    fun resolve(id: StationId): String {
        return "ROUTE-FOR-${id.formatted()}"
    }

    // Java から素直に呼びたい場合、@JvmName でマングリングを無効化してプレーンな Java メソッド名を与える
    @JvmName("resolveByRawStationId")
    fun resolveWithExplicitName(id: StationId): String {
        return "EXPLICIT-FOR-${id.formatted()}"
    }
}
