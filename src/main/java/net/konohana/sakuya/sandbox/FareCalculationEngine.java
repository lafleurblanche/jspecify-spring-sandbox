package net.konohana.sakuya.sandbox;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

public final class FareCalculationEngine {

    private static final int BASE_FARE = 500;
    private static final int DISTANCE_THRESHOLD_KM = 10;
    private static final int MAX_FARE_CAP = 1000;

    private FareCalculationEngine() {}

    /**
     * 純粋関数（pure = true）: 副作用なし。
     * @NullMarked により引数・戻り値は自動で non-null（Kotlin: (String, Int) -> String）
     */
    @Contract(pure = true)
    public static String formatTicketCode(String prefix, int sequenceNo) {
        return prefix + String.format("%05d", sequenceNo) + "OW";
    }

    /**
     * 営業キロ計算（上限キャップあり）
     * 引数 baseFare は null 許容とし、null の場合は BASE_FARE を採用
     */
    @Contract(pure = true)
    public static int calculateTotalFare(int distanceKm, @Nullable Integer baseFare) {
        int initialFare = (baseFare != null) ? baseFare : BASE_FARE;
        if (distanceKm >= DISTANCE_THRESHOLD_KM) {
            return MAX_FARE_CAP;
        }
        return initialFare + (distanceKm * 20);
    }

    /**
     * 駅コードから末尾2桁のサフィックスを抽出
     * @Nullable を付与しているため、Kotlin 側では厳格に String? として扱われる
     */
    @Contract(pure = true)
    public static @Nullable String extractStationSuffix(String staCode) {
        if (staCode.length() < 2) {
            return null;
        }
        return staCode.substring(staCode.length() - 2);
    }
}
