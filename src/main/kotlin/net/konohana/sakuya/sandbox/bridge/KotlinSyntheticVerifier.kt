package net.konohana.sakuya.sandbox.bridge

/**
 * Kotlin 側から SyntheticPropertyHolder へのアクセス可否を検証するオブジェクト。
 */
object KotlinSyntheticVerifier {

    fun runVerification() {
        println("=== Kotlin 側からの検証開始 ===")

        val holder = SyntheticPropertyHolder(
            visibleTitle = "INIT_VISIBLE",
            writeOnlyFromJava = "INIT_WRITE_ONLY",
            completelyHiddenProperty = "INIT_HIDDEN",
            hiddenValProperty = "INIT_HIDDEN_VAL"
        )

        // --------------------------------------------------
        // 1. visibleTitle (通常プロパティ)
        // --------------------------------------------------
        holder.visibleTitle = "KOTLIN_VISIBLE"
        println("[Kotlin 成功] visibleTitle: ${holder.visibleTitle}")

        // --------------------------------------------------
        // 2. writeOnlyFromJava (@get:JvmSynthetic)
        // --------------------------------------------------
        // Java からは呼べなかった Getter も Kotlin からは通常通り参照可能
        holder.writeOnlyFromJava = "KOTLIN_WRITE_ONLY"
        println("[Kotlin 成功] writeOnlyFromJava: ${holder.writeOnlyFromJava}")

        // --------------------------------------------------
        // 3. completelyHiddenProperty (@get:JvmSynthetic, @set:JvmSynthetic)
        // --------------------------------------------------
        // Java からは完全遮蔽されていたが、Kotlin からは通常の var プロパティとして読み書き可能
        holder.completelyHiddenProperty = "KOTLIN_HIDDEN_UPDATED"
        println("[Kotlin 成功] completelyHiddenProperty: ${holder.completelyHiddenProperty}")

        // --------------------------------------------------
        // 4. hiddenValProperty (@get:JvmSynthetic, val)
        // --------------------------------------------------
        // val のため再代入は不可だが、読み取りは通常通り可能
        println("[Kotlin 成功] hiddenValProperty: ${holder.hiddenValProperty}")

        println("=== Kotlin 側からの検証終了 (全プロパティアクセス成功) ===\n")
    }
}
