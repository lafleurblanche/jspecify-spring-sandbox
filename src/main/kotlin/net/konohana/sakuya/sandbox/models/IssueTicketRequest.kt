package net.konohana.sakuya.sandbox.models

/**
 * チケット発券リクエストを表す DTO。
 *
 * @property prefix 発券プレフィックス（例: "QC"）
 * @property distanceKm 乗車予定の営業キロ数
 * @property staCode 対象駅コード
 * @property customBaseFare 基本運賃の上書き指定値（未指定時は null）
 */
data class IssueTicketRequest(
    val prefix: String,
    val distanceKm: Int,
    val staCode: String,
    val customBaseFare: Int? = null,
)
