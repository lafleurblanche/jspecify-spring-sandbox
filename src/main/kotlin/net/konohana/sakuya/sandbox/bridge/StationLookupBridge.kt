package net.konohana.sakuya.sandbox.bridge

import java.util.Optional

/**
 * Java 側から安全に参照できる駅名解決ブリッジクラス。
 */
class StationLookupBridge {

    /**
     * Kotlin 側標準メソッド（Null 許容型を返却）。
     *
     * @param staCode 対象駅コード
     * @return 駅名。対象外プレフィックスの場合は null
     */
    fun findStationName(staCode: String): String? = if (staCode.startsWith("FRTHB")) {
        "Verified-${staCode.takeLast(2)}"
    } else {
        null
    }

    /**
     * Java 呼び出し向けファサードメソッド。
     *
     * バイトコード上の名称を `@JvmName("queryStationAsOptional")` に変更し、
     * Java 側から [Optional] を返す明示的な API として公開します。
     *
     * @param staCode 対象駅コード
     * @return 駅名を含む [Optional]
     */
    @JvmName("queryStationAsOptional")
    fun findStationNameAsOptional(staCode: String): Optional<String> = findStationName(staCode).toOptional()
}
