package net.konohana.sakuya.sandbox.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import net.konohana.sakuya.sandbox.models.ThreadExecutionSnapshot
import org.springframework.stereotype.Service
import java.util.concurrent.Executors

/**
 * Java 25 仮想スレッドと Kotlin Coroutines の実行コンテキストを調査・比較する検証サービス。
 */
@Service
class ThreadExecutionProbeService {

    /**
     * 仮想スレッド用エグゼキュータをラップしたカスタム CoroutineDispatcher。
     */
    private val virtualThreadDispatcher = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()

    /**
     * 呼び出し元スレッドのコンテキストスナップショットを取得します。
     *
     * @param label 呼び出し元の識別ラベル
     * @return 実行スレッド情報
     */
    fun captureCurrentContext(label: String): ThreadExecutionSnapshot {
        val currentThread = Thread.currentThread()
        return ThreadExecutionSnapshot(
            dispatcherName = label,
            threadName = currentThread.name,
            isVirtualThread = currentThread.isVirtual,
            threadId = currentThread.threadId(),
        )
    }

    /**
     * Kotlin 標準の [Dispatchers.IO] 上で処理を実行した際のスレッド情報を取得します。
     *
     * @return [Dispatchers.IO] のスナップショット
     */
    suspend fun probeDefaultIoDispatcher(): ThreadExecutionSnapshot = withContext(Dispatchers.IO) {
        captureCurrentContext("Dispatchers.IO")
    }

    /**
     * Kotlin 標準の [Dispatchers.Default] (CPU バウンド向け) 上で処理を実行した際のスレッド情報を取得します。
     *
     * @return [Dispatchers.Default] のスナップショット
     */
    suspend fun probeDefaultCpuDispatcher(): ThreadExecutionSnapshot = withContext(Dispatchers.Default) {
        captureCurrentContext("Dispatchers.Default")
    }

    /**
     * Java 25 仮想スレッドをバッキングとするコルーチンディスパッチャ上で実行した際のスレッド情報を取得します。
     *
     * @return 仮想スレッドディスパッチャのスナップショット
     */
    suspend fun probeVirtualThreadDispatcher(): ThreadExecutionSnapshot = withContext(virtualThreadDispatcher) {
        captureCurrentContext("VirtualThreadDispatcher")
    }
}
