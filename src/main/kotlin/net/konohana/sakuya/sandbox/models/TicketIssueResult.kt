package net.konohana.sakuya.sandbox.models

/**
 * 乗車券の発券結果を表すデータクラス。
 */
data class TicketIssueResult(
    /** 発券された乗車券の識別ID */
    val ticketId: String,
    /** 運賃（円） */
    val fare: Int,
    /** 駅識別子のサフィックス */
    val stationSuffix: String,
)
