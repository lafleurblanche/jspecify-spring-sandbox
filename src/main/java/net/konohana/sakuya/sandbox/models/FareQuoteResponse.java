package net.konohana.sakuya.sandbox.models;

/**
 * 運賃見積りレスポンス DTO。
 *
 * @param fromStaCode 出発駅コード
 * @param toStaCode   到着駅コード
 * @param totalFare   算出運賃
 * @param note        特記事項
 */
public record FareQuoteResponse(
    String fromStaCode,
    String toStaCode,
    int totalFare,
    String note
) {}
