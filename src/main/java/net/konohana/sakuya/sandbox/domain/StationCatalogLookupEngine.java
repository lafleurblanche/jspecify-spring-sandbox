package net.konohana.sakuya.sandbox.domain;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Contract;

/**
 * 駅コードと駅名カタログの対応関係を管理するドメインルックアップエンジン。
 *
 * <p>{@link org.jspecify.annotations.NullMarked} 配下に属するため、
 * {@link Optional} 自身およびその型引数はデフォルトで non-null です。
 * 値が存在しない可能性がある検索結果は、{@code null} ではなく空の {@link Optional} として返却します。
 * </p>
 */
public final class StationCatalogLookupEngine {

    private static final Map<String, String> CATALOG_STORE = new ConcurrentHashMap<>();

    static {
        CATALOG_STORE.put("FRTHB00101", "試験駅A01");
        CATALOG_STORE.put("FRTHB00102", "試験駅A02");
        CATALOG_STORE.put("FRTHB00103", "試験駅A03");
    }

    private StationCatalogLookupEngine() {
        // インスタンス化防止
    }

    /**
     * 指定された駅コードに対応する駅名を取得します。
     *
     * @param staCode 検索対象の駅コード
     * @return 駅名を含む {@link Optional}。未登録の場合は {@link Optional#empty()}
     */
    @Contract(pure = true)
    public static Optional<String> findStationName(String staCode) {
        return Optional.ofNullable(CATALOG_STORE.get(staCode));
    }

    /**
     * カスタムデフォルト名を考慮した駅表示名を解決します。
     *
     * @param staCode       対象駅コード
     * @param defaultNotice 代替表示名を含む {@link Optional}
     * @return 解決された表示名（常に non-null）
     */
    @Contract(pure = true)
    public static String resolveDisplayName(String staCode, Optional<String> defaultNotice) {
        return findStationName(staCode)
            .or(() -> defaultNotice)
            .orElse("Unknown Station");
    }
}

