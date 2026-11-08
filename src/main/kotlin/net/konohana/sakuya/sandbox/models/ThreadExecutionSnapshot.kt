package net.konohana.sakuya.sandbox.models

/**
 * 処理が実行されたスレッドのコンテキスト情報を保持するスナップショット DTO。
 *
 * @property dispatcherName 使用されたディスパッチャまたは実行機構の識別名
 * @property threadName 実行スレッド名
 * @property isVirtualThread Java 25 仮想スレッド ([Thread.isVirtual]) かどうか
 * @property threadId 実行スレッド ID
 */
data class ThreadExecutionSnapshot(
    val dispatcherName: String,
    val threadName: String,
    val isVirtualThread: Boolean,
    val threadId: Long,
)
