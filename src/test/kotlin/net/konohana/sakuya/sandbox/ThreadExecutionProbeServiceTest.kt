package net.konohana.sakuya.sandbox

import kotlinx.coroutines.test.runTest
import net.konohana.sakuya.sandbox.services.ThreadExecutionProbeService
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Java 25 仮想スレッド (Virtual Threads) と Kotlin Coroutines ディスパッチャの協調挙動検証テスト。
 */
class ThreadExecutionProbeServiceTest {

    // Spring コンテキストを起動せず直接インスタンス化
    private val probeService = ThreadExecutionProbeService()

    @Test
    fun `verify Dispatchers_IO runs on standard platform threads, not virtual threads`() = runTest {
        val snapshot = probeService.probeDefaultIoDispatcher()

        // Kotlin 標準の Dispatchers.IO は固定スレッドプール (DefaultDispatcher-worker-...) 上で動作するため仮想スレッドではない
        assertFalse(
            snapshot.isVirtualThread,
            "Dispatchers.IO should be backed by platform threads, but was virtual.",
        )
        assertTrue(snapshot.threadName.contains("DefaultDispatcher-worker"))
    }

    @Test
    fun `verify Dispatchers_Default runs on platform threads for CPU-bound tasks`() = runTest {
        val snapshot = probeService.probeDefaultCpuDispatcher()

        assertFalse(
            snapshot.isVirtualThread,
            "Dispatchers.Default should be backed by platform threads, but was virtual.",
        )
        assertTrue(snapshot.threadName.contains("DefaultDispatcher-worker"))
    }

    @Test
    fun `verify VirtualThread dispatcher executes coroutine inside a true Java 25 virtual thread`() = runTest {
        val snapshot = probeService.probeVirtualThreadDispatcher()

        // asCoroutineDispatcher で仮想スレッド Executor を渡した場合は isVirtualThread が true となる
        assertTrue(
            snapshot.isVirtualThread,
            "Expected coroutine to run on a Java Virtual Thread, but was a platform thread.",
        )
    }

    @Test
    fun `verify Thread_isVirtual API detects virtual threads created directly`() {
        var isVirtual = false
        val thread = Thread.ofVirtual().start {
            isVirtual = Thread.currentThread().isVirtual
        }
        thread.join()

        assertTrue(isVirtual, "Thread.ofVirtual() must produce a virtual thread in Java 25.")
    }
}
