package net.konohana.sakuya.sandbox.models

/**
 * チケット発券完了レスポンスを表す DTO。
 *
 * @property ticketId 発行されたチケット一意識別コード
 * @property fare 計算された適用運賃（円）
 * @property stationSuffix 抽出された駅サフィックス
 * @property stationId 型安全にフォーマットされた駅識別文字列
 */
data class IssueTicketResponse(
    val ticketId: String,
    val fare: Int,
    val stationSuffix: String,
    val stationId: String,
)
