package net.konohana.sakuya.sandbox.bridge

import java.util.Optional

/**
 * [Optional] を Kotlin の Null 許容型（[T]?）へ変換します。
 *
 * @receiver 変換元の [Optional]
 * @return 値が存在する場合はその値、空の場合は null
 */
fun <T : Any> Optional<T>.toNullable(): T? = orElse(null)

/**
 * Kotlin の Null 許容値を Java 向けに [Optional] へ変換します。
 *
 * @receiver 変換元の値
 * @return 値が存在する場合は [Optional.of]、null の場合は [Optional.empty]
 */
fun <T : Any> T?.toOptional(): Optional<T> = Optional.ofNullable(this)

/**
 * 静的呼び出し用のファサードオブジェクト。
 */
object OptionalExtensions {

    @JvmStatic
    fun <T : Any> unwrapToNullable(optional: Optional<T>): T? = optional.toNullable()

    @JvmStatic
    fun <T : Any> wrapToOptional(value: T?): Optional<T> = value.toOptional()
}
