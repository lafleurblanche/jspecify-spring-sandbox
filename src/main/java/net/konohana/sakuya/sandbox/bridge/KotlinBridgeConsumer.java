package net.konohana.sakuya.sandbox.bridge;

import java.util.List;

public final class KotlinBridgeConsumer {

    private KotlinBridgeConsumer() {}

    public static String callDirectStatic(String prefix) {
        return TicketCodeGenerator.generateDirect(prefix);
    }

    public static String callCompanionMethod(String prefix) {
        return TicketCodeGenerator.Companion.generateDefault(prefix);
    }

    public static String callWithDefaults(TicketCodeGenerator generator, String routeId) {
        return generator.createRouteCode(routeId);
    }

    public static List<String> callMangledMethod(List<String> list) {
        // TicketCodeFilterKt に参照先を変更
        return TicketCodeFilterKt.filterValidStrings(list);
    }

    public static void verifyPropertyVisibility(SyntheticPropertyHolder holder) {
        holder.setVisibleTitle("NEW_TITLE");
        holder.setWriteOnlyFromJava("UPDATED");
    }

    /**
     * `@JvmSynthetic` の遮蔽検証
     * 以下のコメントアウトを解除すると、javac のコンパイルエラーが発生することを確認できる。
     */
    public static void attemptToCallSynthetic(TicketCodeGenerator generator) {
        // [コンパイルエラーの検証]
        // 以下をアンコメントすると、IDE 上で赤波線になり、
        // "./gradlew compileJava" で "cannot find symbol: method internalKotlinDsl(...)" が発生する。
        //
        // generator.internalKotlinDsl(() -> {});
    }
}
