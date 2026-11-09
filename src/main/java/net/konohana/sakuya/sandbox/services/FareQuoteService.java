package net.konohana.sakuya.sandbox.services;

import java.util.Optional;
import net.konohana.sakuya.sandbox.domain.FareCalculationEngine;
import net.konohana.sakuya.sandbox.domain.StationCatalogLookupEngine;
import net.konohana.sakuya.sandbox.models.FareQuoteRequest;
import net.konohana.sakuya.sandbox.models.FareQuoteResponse;
import org.springframework.stereotype.Service;

/**
 * Java 側運賃見積りアプリケーションサービス。
 *
 * package-info.java の @NullMarked により、すべての引数・戻り値は非 null 契約となります。
 */
@Service
public class FareQuoteService {

    /**
     * 見積りリクエストを処理し、運賃見積りレスポンスを生成します。
     *
     * @param request 見積りリクエスト
     * @return 算出された見積り結果
     */
    public FareQuoteResponse calculateQuote(FareQuoteRequest request) {

        // 1. 駅コードの妥当性・表示名解決
        Optional<String> fromNameOpt = StationCatalogLookupEngine.findStationName(request.fromStaCode());
        String fromDisplayName = StationCatalogLookupEngine.resolveDisplayName(request.fromStaCode(), fromNameOpt);

        // 2. 距離計算 (疑似距離: 駅番号下2桁の差分)
        int dist = calculateDistance(request.fromStaCode(), request.toStaCode());

        // 3. 運賃計算 (static メソッドとしてクラス名から直接呼び出し)
        int fare = FareCalculationEngine.calculateTotalFare(dist, request.customBaseFare());

        String note = String.format("From %s (%d km)", fromDisplayName, dist);
        return new FareQuoteResponse(request.fromStaCode(), request.toStaCode(), fare, note);
    }

    private int calculateDistance(String fromCode, String toCode) {
        try {
            int fromNum = Integer.parseInt(fromCode.substring(Math.max(0, fromCode.length() - 2)));
            int toNum = Integer.parseInt(toCode.substring(Math.max(0, toCode.length() - 2)));
            return Math.max(1, Math.abs(toNum - fromNum));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return 5;
        }
    }
}
