package net.konohana.sakuya.sandbox.domain;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

/**
 * 運賃計算およびチケットコード整形を行う純粋計算ユーティリティクラス。
 * <p>
 * {@link org.jspecify.annotations.NullMarked} 配下のパッケージに属するため、
 * 明示的に {@link Nullable} が付与されていない型はすべて non-null として解釈されます。
 * また、{@link Contract#pure()} により副作用を持たない純粋関数であることを明示しています。
 * </p>
 */
public final class FareCalculationEngine {

    /** 基本運賃の初期値 */
    private static final int BASE_FARE = 500;

    /** 上限運賃が適用される営業キロの閾値 (km) */
    private static final int DISTANCE_THRESHOLD_KM = 10;

    /** 上限運賃 (円) */
    private static final int MAX_FARE_CAP = 1000;

    /** 1km あたりの加算運賃 (円) */
    private static final int FARE_PER_KM = 20;

    private FareCalculationEngine() {
        // インスタンス化防止
    }

    /**
     * 発券用の一意識別コードを整形します。
     *
     * @param prefix     プレフィックス識別子（例: {@code "QC"}）
     * @param sequenceNo 採番シーケンス番号
     * @return 5桁ゼロ埋めシーケンスとサフィックスを含むチケットコード
     */
    @Contract(pure = true)
    public static String formatTicketCode(String prefix, int sequenceNo) {
        return prefix + String.format("%05d", sequenceNo) + "OW";
    }

    /**
     * 営業キロおよび指定された基本運賃から合計運賃を計算します。
     * <p>
     * 営業キロが閾値（10km）以上の場合は、上限運賃（1000円）が適用されます。
     * {@code baseFare} が省略（{@code null}）された場合は、既定の基本運賃が採用されます。
     * </p>
     *
     * @param distanceKm 営業キロ
     * @param baseFare   カスタム基本運賃（指定しない場合は {@code null}）
     * @return 計算された運賃（円）
     */
    @Contract(pure = true)
    public static int calculateTotalFare(int distanceKm, @Nullable Integer baseFare) {
        int initialFare = (baseFare != null) ? baseFare : BASE_FARE;
        if (distanceKm >= DISTANCE_THRESHOLD_KM) {
            return MAX_FARE_CAP;
        }
        return initialFare + (distanceKm * FARE_PER_KM);
    }

    /**
     * 駅コードから末尾2桁のサフィックス文字列を抽出します。
     *
     * @param staCode 駅コード文字列
     * @return 抽出された末尾2桁のサフィックス。2文字未満の場合は {@code null}
     */
    @Contract(pure = true)
    public static @Nullable String extractStationSuffix(String staCode) {
        if (staCode.length() < 2) {
            return null;
        }
        return staCode.substring(staCode.length() - 2);
    }
}
