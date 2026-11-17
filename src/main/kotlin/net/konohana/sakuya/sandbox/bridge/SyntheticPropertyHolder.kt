package net.konohana.sakuya.sandbox.bridge

/**
 * プロパティの Getter / Setter に対する `@JvmSynthetic` 遮蔽検証用クラス。
 *
 * 【@JvmSynthetic の基本仕様】
 * 付与された要素に JVM バイトコードレベルで `ACC_SYNTHETIC` フラグを設定する。
 * - Java コンパイラ (javac): 合成要素とみなしてシンボル探索・コード補完から除外（コンパイルエラーとなる）。
 * - Kotlin コンパイラ (kotlinc): `@Metadata` を参照するためフラグを無視し、通常通りアクセス可能。
 */
class SyntheticPropertyHolder(
    /**
     * 【通常プロパティ（比較基準）】
     * - Kotlin: 読み書き可能 (`holder.visibleTitle`)
     * - Java:   読み書き可能 (`holder.getVisibleTitle()` / `holder.setVisibleTitle(...)`)
     * - JVM:    Getter / Setter 共に通常の public final メソッドとして生成される。
     */
    var visibleTitle: String = "VISIBLE",

    /**
     * 【Java からの書き込み専用（Write-Only）プロパティ】
     * - Kotlin: 読み書き可能 (`holder.writeOnlyFromJava`)
     * - Java:   書き込みのみ可能 (`holder.setWriteOnlyFromJava(...)` のみ可視)
     *           ※ `holder.getWriteOnlyFromJava()` は javac から不可視（呼ぶとコンパイルエラー）。
     * - JVM:    Getter のみに `ACC_SYNTHETIC` が付与され、Setter は通常の public メソッド。
     * - 用途:   Java Beans 仕様に存在する「Setter のみ提供し、内部状態を読ませない」API 設計の再現。
     */
    @get:JvmSynthetic
    var writeOnlyFromJava: String = "WRITE_ONLY",

    /**
     * 【Java 側から完全遮蔽されたプロパティ】
     * - Kotlin: 読み書き可能 (`holder.completelyHiddenProperty`)
     * - Java:   アクセス不可（Getter / Setter 共に javac から不可視）。
     *           ※ `holder.getCompletelyHiddenProperty()` / `setCompletelyHiddenProperty(...)` 共にコンパイルエラー。
     * - JVM:    Getter / Setter の双方に `ACC_SYNTHETIC` が付与される。
     *           ※ バッキングフィールド自体は通常の private フィールド（リフレクション等では検知可能）。
     * - 用途:   Kotlin 側では便利なプロパティ構文を維持しつつ、Java 側の補完候補ノイズや誤操作を完全排除する。
     */
    @get:JvmSynthetic
    @set:JvmSynthetic
    var completelyHiddenProperty: String = "HIDDEN",

    /**
     * 【Java 側から完全遮蔽されたイミュータブル（val）プロパティ】
     * - Kotlin: 読み取り可能 (`holder.hiddenValProperty`)
     * - Java:   アクセス不可。
     *           ※ `val` のため元々 Setter は生成されず、Getter を遮蔽したことで Java から完全に呼べなくなる。
     * - JVM:    Getter のみに `ACC_SYNTHETIC` が付与される。
     * - 用途:   Kotlin 固有のイミュータブルな内部状態を Java API 面から完全に隠す。
     */
    @get:JvmSynthetic
    val hiddenValProperty: String = "HIDDEN_VAL",
)
