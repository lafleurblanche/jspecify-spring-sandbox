package net.konohana.sakuya.sandbox.bridge;

import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Java 側からのアクセサ可視性・挙動テスト")
class JavaSyntheticPropertyTest {

    @Test
    @DisplayName("1. visibleTitle: Java から Getter / Setter 共に通常呼出可能")
    void testVisibleTitle() throws NoSuchMethodException {
        SyntheticPropertyHolder holder = new SyntheticPropertyHolder(
            "INIT_TITLE", "INIT_WRITE", "INIT_HIDDEN", "INIT_VAL"
        );

        // 直接呼び出し確認
        assertEquals("INIT_TITLE", holder.getVisibleTitle());
        holder.setVisibleTitle("UPDATED_TITLE");
        assertEquals("UPDATED_TITLE", holder.getVisibleTitle());

        // バイトコードフラグ検証: synthetic でないこと
        Method getter = SyntheticPropertyHolder.class.getMethod("getVisibleTitle");
        Method setter = SyntheticPropertyHolder.class.getMethod("setVisibleTitle", String.class);
        assertFalse(getter.isSynthetic(), "getVisibleTitle は synthetic であってはならない");
        assertFalse(setter.isSynthetic(), "setVisibleTitle は synthetic であってはならない");
    }

    @Test
    @DisplayName("2. writeOnlyFromJava: Setter は直接呼出可能だが、Getter は isSynthetic が true")
    void testWriteOnlyFromJava() throws NoSuchMethodException {
        SyntheticPropertyHolder holder = new SyntheticPropertyHolder(
            "INIT_TITLE", "INIT_WRITE", "INIT_HIDDEN", "INIT_VAL"
        );

        // Setter は直接呼出可能
        holder.setWriteOnlyFromJava("UPDATED_BY_JAVA");

        // getMethod は public メソッドを取得できるが、isSynthetic() が true であることを確認
        Method getter = SyntheticPropertyHolder.class.getMethod("getWriteOnlyFromJava");
        Method setter = SyntheticPropertyHolder.class.getMethod("setWriteOnlyFromJava", String.class);

        assertTrue(getter.isSynthetic(), "getWriteOnlyFromJava には ACC_SYNTHETIC が付与されている必要がある");
        assertFalse(setter.isSynthetic(), "setWriteOnlyFromJava は通常の public メソッドである必要がある");
    }

    @Test
    @DisplayName("3. completelyHiddenProperty: Getter / Setter 共に isSynthetic が true")
    void testCompletelyHiddenProperty() throws NoSuchMethodException {
        Method getter = SyntheticPropertyHolder.class.getMethod("getCompletelyHiddenProperty");
        Method setter = SyntheticPropertyHolder.class.getMethod("setCompletelyHiddenProperty", String.class);

        assertTrue(getter.isSynthetic(), "getCompletelyHiddenProperty は synthetic である必要がある");
        assertTrue(setter.isSynthetic(), "setCompletelyHiddenProperty は synthetic である必要がある");
    }

    @Test
    @DisplayName("4. hiddenValProperty: val のため Setter は存在せず、Getter は isSynthetic が true")
    void testHiddenValProperty() throws NoSuchMethodException {
        // Getter の synthetic 検証
        Method getter = SyntheticPropertyHolder.class.getMethod("getHiddenValProperty");
        assertTrue(getter.isSynthetic(), "getHiddenValProperty は synthetic である必要がある");

        // Setter はクラス内にそもそも存在しないことを検証
        assertThrows(
            NoSuchMethodException.class,
            () -> SyntheticPropertyHolder.class.getMethod("setHiddenValProperty", String.class),
            "val プロパティには Setter が生成されてはならない"
        );
    }
}
