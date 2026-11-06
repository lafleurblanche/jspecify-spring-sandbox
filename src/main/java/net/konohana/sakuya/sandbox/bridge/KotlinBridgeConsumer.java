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

    /**
     * @JvmField の直接フィールドアクセス検証
     */
    public static String verifyJvmFieldAccess(TicketDirectAccessDto dto) {
        // 1. 通常プロパティは Getter を経由
        String normal = dto.getNormalField();

        // 2. @JvmField 付与プロパティは Getter が存在せず、直接フィールドアクセス
        // [コンパイルエラー検証] dto.getDirectReadOnlyField(); // cannot find symbol
        String readOnly = dto.directReadOnlyField;

        // 3. var の @JvmField はフィールドへの直接再代入が可能
        dto.directMutableField = "MODIFIED_DIRECTLY";
        String mutable = dto.directMutableField;

        // 4. Companion 内の @JvmField は static フィールドとして参照可能
        String staticPrefix = TicketDirectAccessDto.STATIC_PREFIX;

        return normal + ":" + readOnly + ":" + mutable + ":" + staticPrefix;
    }

    /**
     * @JvmInline (Value Class) の Java からの呼び出し検証
     */
    public static String verifyValueClassInvocation(StationRouteResolver resolver, String rawId) {
        // @JvmName("resolveByRawStationId") を付与したメソッドは、
        // Value Class が展開された生の値（String）をそのまま渡して呼べる
        return resolver.resolveByRawStationId(rawId);

        // ※ アノテーションなしの resolver.resolve(...) は Java から見ると
        // "resolve-XXXX(String)" のようにマングリングされているため、Java 側から直感的には呼べない
    }
}
