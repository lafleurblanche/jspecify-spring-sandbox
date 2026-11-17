package net.konohana.sakuya.sandbox.bridge;

/**
 * Java 側から SyntheticPropertyHolder へのアクセス可否を検証するクラス。
 */
public final class JavaSyntheticVerifier {

    private JavaSyntheticVerifier() {
        // インスタンス化防止
    }

    public static void runVerification() {
        System.out.println("=== Java 側からの検証開始 ===");

        SyntheticPropertyHolder holder = new SyntheticPropertyHolder(
            "INIT_VISIBLE",
            "INIT_WRITE_ONLY",
            "INIT_HIDDEN",
            "INIT_HIDDEN_VAL"
        );

        // --------------------------------------------------
        // 1. visibleTitle (通常プロパティ)
        // --------------------------------------------------
        // Getter / Setter 共に通常の public final メソッドとして呼出可能
        String title = holder.getVisibleTitle();
        holder.setVisibleTitle("UPDATED_TITLE");
        System.out.println("[Java 成功] visibleTitle: " + holder.getVisibleTitle());

        // --------------------------------------------------
        // 2. writeOnlyFromJava (@get:JvmSynthetic)
        // --------------------------------------------------
        // Setter にはフラグが付いていないため、Java から値の更新が可能
        holder.setWriteOnlyFromJava("UPDATED_BY_JAVA");
        System.out.println("[Java 成功] writeOnlyFromJava: Setter 呼出完了");

        // 【コンパイルエラー検証】
        // Getter は ACC_SYNTHETIC が付与されているため、javac から不可視。
        // コメントを解除すると: cannot find symbol method getWriteOnlyFromJava()
        // String writeOnlyVal = holder.getWriteOnlyFromJava();

        // --------------------------------------------------
        // 3. completelyHiddenProperty (@get:JvmSynthetic, @set:JvmSynthetic)
        // --------------------------------------------------
        // 【コンパイルエラー検証】
        // Getter / Setter 共に ACC_SYNTHETIC のため、Java からは一切アクセス不可。
        // コメントを解除すると双方 cannot find symbol となる。
        // holder.setCompletelyHiddenProperty("JAVA_SET");
        // String hiddenVal = holder.getCompletelyHiddenProperty();

        // --------------------------------------------------
        // 4. hiddenValProperty (@get:JvmSynthetic, val)
        // --------------------------------------------------
        // 【コンパイルエラー検証】
        // val のため元々 Setter は存在せず、Getter も ACC_SYNTHETIC で隠蔽。
        // コメントを解除すると: cannot find symbol method getHiddenValProperty()
        // String valHidden = holder.getHiddenValProperty();

        System.out.println("=== Java 側からの検証終了 (全構文チェック通過) ===\n");
    }
}
