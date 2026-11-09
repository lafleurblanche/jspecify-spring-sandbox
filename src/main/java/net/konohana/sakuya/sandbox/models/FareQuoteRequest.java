package net.konohana.sakuya.sandbox.models;

import org.jspecify.annotations.Nullable;

/**
 * 運賃見積りリクエスト DTO。
 *
 * @param fromStaCode 出発駅コード
 * @param toStaCode   到着駅コード
 * @param customBaseFare カスタム基本運賃 (指定時のみ。未指定時は null)
 */
public record FareQuoteRequest(
    String fromStaCode,
    String toStaCode,
    @Nullable Integer customBaseFare
) {}
