package net.konohana.sakuya.sandbox.models

/**
 * 外部カタログ API から取得する駅情報エントリ DTO。
 *
 * @property staCode 駅識別コード (例: "FRTHB00101")
 * @property staName 駅名表示文字列
 * @property active 利用可能フラグ
 */
data class StationCatalogEntry(
    val staCode: String,
    val staName: String,
    val active: Boolean = true,
)
