# jspecify-spring-sandbox

Java 25、Spring Boot 4、Kotlin 2.x のハイブリッド構成における **JSpecify 1.0**（Null安全性標準仕様）および **JetBrains Annotations**（`@Contract(pure = true)`）、ならびに **JVM相互運用アノテーション**（`@JvmStatic`, `@JvmOverloads`, `@JvmSynthetic`, `@JvmName`）の挙動検証用サンドボックスプロジェクトです。

---

## 目的

Java と Kotlin の相互運用において長年の課題であった「プラットフォーム型（`T!`）」による型安全性の低下を防ぎ、さらに Java から Kotlin を呼び出す際の見え方・呼び出し制御を検証します。

1. **`@NullMarked` によるデフォルト Non-Null 化**:
    * `package-info.java` を用いてパッケージ単位で一括適用し、個別のアノテーション付与漏れによるプラットフォーム型の発生を防止。
2. **`@Nullable` の明示と Kotlin 側の型推論**:
    * 型使用位置（`TYPE_USE`）での null 許容宣言が、Kotlin 側で正しく `T?` として厳格に解釈されることを確認。
3. **`@Contract(pure = true)` による純粋関数の保証**:
    * 副作用のない計算ロジックに対し、戻り値の破棄（デッドコード）を IDE（IntelliJ IDEA）や静的解析ツールが正しく警告・検知できるかの検証。
4. **Spring Boot 4 / Kotlin コンパイラ厳格モードの親和性**:
    * `-Xjsr305=strict` を有効化し、JSpecify 仕様に準拠した Spring エコシステムとの境界検査。
5. **Java から Kotlin を呼び出す際の JVM アノテーション制御**:
    * `@JvmStatic`（クラス直下の静的メソッド化）、`@JvmOverloads`（デフォルト引数の多重定義生成）、`@JvmSynthetic`（Java からの隠蔽）、`@JvmName`（型消去対策・メソッド名変更）の効果を検証。

---

## 技術スタック

- **Language**: Java 25 / Kotlin 2.3+
- **Framework**: Spring Boot 4.1.2
- **Build Tool**: Gradle (Kotlin DSL)
- **Specifications & Libraries**:
    * [JSpecify 1.0.0](https://jspecify.dev/) (`org.jspecify:jspecify`)
    * JetBrains Java Annotations (`org.jetbrains:annotations:26.0.2`)

---

## アーキテクチャとアノテーションの役割分担

| 担当レイヤー | 使用技術・アノテーション | 目的 |
|---|---|---|
| **Java ドメイン計算層 (`domain`)** | `@NullMarked` (`package-info.java`) | パッケージ配下の全メソッド・引数・戻り値をデフォルトで `@NonNull` 扱いにする。 |
| | `@Nullable` | null を返却／許容する箇所のみをピンポイントで明示（Kotlin 側で `T?` に強制）。 |
| | `@Contract(pure = true)` | 副作用を持たない純粋関数であることを宣言し、戻り値の無視を警告。 |
| **相互運用検証層 (`bridge`)** | `@JvmStatic` | Kotlin の `companion object` メソッドを Java から直接呼べる静的メソッドとして公開。 |
| | `@JvmOverloads` | デフォルト引数を持つ Kotlin メソッドを Java から引数省略して呼べるようにオーバーロード生成。 |
| | `@JvmSynthetic` | Kotlin 専用 DSL やラムダを Java 側のコード補完・参照から完全隠蔽。 |
| | `@JvmName` | バイトコード上のメソッド名を変更し、型消去によるシグネチャ重複を解消。 |
| **Kotlin サービス層 (`services`)** | Kotlin (`-Xjsr305=strict`) | Java の型をプラットフォーム型（`!`）ではなく、厳格な `String` / `String?` として受領・処理。 |

---

## 検証コードの構造

```text
src/
├── main/
│   ├── java/net/konohana/sakuya/sandbox/
│   │   ├── bridge/
│   │   │   ├── package-info.java            # bridge 配下を @NullMarked 化
│   │   │   └── KotlinBridgeConsumer.java    # Java から Kotlin を呼ぶ検証クラス
│   │   ├── domain/
│   │   │   ├── package-info.java            # domain 配下を @NullMarked 化
│   │   │   └── FareCalculationEngine.java  # @Contract(pure=true) 純粋計算エンジン
│   │   └── models/
│   │       ├── package-info.java            # models 配下を @NullMarked 化
│   │       └── LegacyStationDto.java        # Java 側 DTO（必要に応じて @NullUnmarked 検証）
│   └── kotlin/net/konohana/sakuya/sandbox/
│       ├── SandboxApplication.kt            # Spring Boot エントリポイント
│       ├── bridge/
│       │   └── TicketCodeGenerator.kt       # @JvmStatic, @JvmOverloads, @JvmSynthetic, @JvmName
│       ├── models/
│       │   └── TicketIssueResult.kt         # Kotlin Data Class
│       └── services/
│           └── TicketIssueService.kt        # Kotlin 側サービス層
└── test/
    └── kotlin/net/konohana/sakuya/sandbox/
        ├── JSpecifyNullnessContractTest.kt  # JSpecify による Null 契約・プラットフォーム型排除テスト
        └── KotlinBridgeConsumerTest.kt      # Java ⇄ Kotlin 相互運用アノテーション検証テスト

# テストの実行（Java 25 Toolchain を使用）
./gradlew test
