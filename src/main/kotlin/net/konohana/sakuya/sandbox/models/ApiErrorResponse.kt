package net.konohana.sakuya.sandbox.models

import java.time.Instant

/**
 * API エラーレスポンス共通 DTO。
 *
 * @property status HTTP ステータスコード
 * @property error エラー種別概要
 * @property message 詳細メッセージ
 * @property timestamp 発生日時 (ISO-8601 UTC)
 */
data class ApiErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: Instant = Instant.now(),
)
