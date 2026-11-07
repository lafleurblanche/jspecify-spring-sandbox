package net.konohana.sakuya.sandbox.bridge

import java.util.Optional

/**
 * Java 側から安全に参照できる駅名解決ブリッジクラス。
 */
class StationLookupBridge {

    /**
     * Kotlin 側内部メソッド（Null 許容型を返却）。
     */
    fun queryInternal(staCode: String): String? {
        return if (staCode.startsWith("FRTHB")) {
            "Verified-${staCode.takeLast(2)}"
        } else {
            null
        }
    }

    /**
     * Java 呼び出し向けファサードメソッド。
     *
     * `@JvmName` を付与し、Kotlin の [String]? を [Optional] にラップして返却します。
     *
     * @param staCode 対象駅コード
     * @return 駅名を含む [Optional]
     */
    @JvmName("queryStationAsOptional")
    fun queryStationAsOptional(staCode: String): Optional<String> {
        return queryInternal(staCode).toOptional()
    }
}
