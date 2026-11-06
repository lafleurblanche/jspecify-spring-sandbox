# jspecify-spring-sandbox

Java 25、Spring Boot 4、Kotlin 2.x のハイブリッド構成における **JSpecify 1.0**（Null安全性標準仕様）および **JetBrains Annotations**（`@Contract(pure = true)`）の挙動検証用サンドボックスプロジェクトです。

---

## 目的

Java と Kotlin の相互運用において長年の課題であった「プラットフォーム型（`T!`）」による型安全性の低下を防ぎ、JSpecify 1.0 を用いて以下の挙動を実証・検証します。

1. **`@NullMarked` によるデフォルト Non-Null 化**:
   * `package-info.java` を用いてパッケージ単位で一括適用し、個別のアノテーション付与漏れによるプラットフォーム型の発生を防止。
2. **`@Nullable` の明示と Kotlin 側の型推論**:
   * 型使用位置（`TYPE_USE`）での null 許容宣言が、Kotlin 側で正しく `T?` として厳格に解釈されることを確認。
3. **`@Contract(pure = true)` による純粋関数の保証**:
   * 副作用のない計算ロジックに対し、戻り値の破棄（デッドコード）を IDE（IntelliJ IDEA）や静的解析ツールが正しく警告・検知できるかの検証。
4. **Spring Boot 4 / Kotlin コンパイラ厳格モードの親和性**:
   * `-Xjsr305=strict` を有効化し、JSpecify 仕様に準拠した Spring エコシステムとの境界検査。

---

## 技術スタック

* **Language**: Java 25 / Kotlin 2.2+
* **Framework**: Spring Boot 4.0.0
* **Build Tool**: Gradle (Kotlin DSL)
* **Specifications & Libraries**:
  * [JSpecify 1.0.0](https://jspecify.dev/) (`org.jspecify:jspecify`)
  * JetBrains Java Annotations (`org.jetbrains:annotations`)

---

## アーキテクチャとアノテーションの役割分担

| 担当レイヤー | 使用技術・アノテーション | 目的 |
|---|---|---|
| **Java ドメイン計算層** | `@NullMarked` (`package-info.java`) | パッケージ配下の全メソッド・引数・戻り値をデフォルトで `@NonNull` 扱いにする。 |
| | `@Nullable` | null を返却／許容する箇所のみをピンポイントで明示（Kotlin 側で `T?` に強制）。 |
| | `@Contract(pure = true)` | 副作用を持たない純粋関数であることを宣言し、戻り値の無視を警告。 |
| **Kotlin サービス層** | Kotlin 2.x (`-Xjsr305=strict`) | Java の型をプラットフォーム型（`!`）ではなく、厳格な `String` / `String?` として受領・処理。 |

---

## 検証コードの構造

```text
src/
├── main/
│   ├── java/net/konohana/sakuya/sandbox/
│   │   ├── package-info.java            # @NullMarked の適用
│   │   └── FareCalculationEngine.java  # @Contract(pure = true) + JSpecify
│   └── kotlin/net/konohana/sakuya/sandbox/
│       ├── SandboxApplication.kt        # Spring Boot エントリポイント
│       └── TicketIssueService.kt        # Java 層の Non-Null / Nullable を受領するサービス
└── test/
    └── kotlin/net/konohana/sakuya/sandbox/
        └── JSpecifyNullnessContractTest.kt # プラットフォーム型排除と契約挙動の検証テスト

# テストの実行（Java 25 Toolchain を使用）
./gradlew test
