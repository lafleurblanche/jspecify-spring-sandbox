package net.konohana.sakuya.sandbox.bridge;

import java.util.List;

public final class KotlinBridgeConsumer {

    private KotlinBridgeConsumer() {}

    public static String callDirectStatic(String prefix) {
        // @JvmStatic により直接呼び出し可能
        return TicketCodeGenerator.generateDirect(prefix);
    }

    public static String callCompanionMethod(String prefix) {
        // アノテーションなしのため Companion 経由
        return TicketCodeGenerator.Companion.generateDefault(prefix);
    }

    public static String callWithDefaults(TicketCodeGenerator generator, String routeId) {
        // @JvmOverloads により引数省略版が解決可能
        return generator.createRouteCode(routeId);
    }

    public static List<String> callMangledMethod(List<String> list) {
        // @JvmName("filterValidStrings") で指定した名称で呼ぶ
        return TicketCodeGeneratorKt.filterValidStrings(list);
    }
}
