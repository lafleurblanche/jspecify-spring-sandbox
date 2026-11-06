package net.konohana.sakuya.sandbox.domain;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * データベースを使用しないインメモリ発券データ管理リポジトリ。
 * <p>
 * スレッドセーフな採番および状態保持を提供します。
 * パッケージ単位で {@code @NullMarked} が適用されているため、戻り値や引数はデフォルトで非nullです。
 * </p>
 */
@Repository
public class InMemoryTicketRepository {

    private final AtomicInteger sequenceGenerator = new AtomicInteger(0);
    private final Map<String, String> ticketStore = new ConcurrentHashMap<>();

    /**
     * 次の採番シーケンス番号を取得します。
     *
     * @return インクリメントされたシーケンス番号
     */
    public int nextSequence() {
        return sequenceGenerator.incrementAndGet();
    }

    /**
     * 発券情報を保存します。
     *
     * @param ticketId チケット識別子
     * @param staCode  対象の駅コード
     */
    public void save(String ticketId, String staCode) {
        ticketStore.put(ticketId, staCode);
    }

    /**
     * チケット識別子から関連付けられた駅コードを取得します。
     *
     * @param ticketId 検索対象のチケット識別子
     * @return 該当する駅コード。存在しない場合は {@code null}
     */
    public @Nullable String findStaCodeById(String ticketId) {
        return ticketStore.get(ticketId);
    }

    /**
     * 現在保存されているチケット総数を取得します。
     *
     * @return 保存件数
     */
    public int count() {
        return ticketStore.size();
    }
}
