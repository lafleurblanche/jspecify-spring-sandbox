# jspecify-spring-sandbox

Java 25、Spring Boot 4、Kotlin 2.x のハイブリッド構成における **JSpecify 1.0**（Null安全性標準仕様）および **JetBrains Annotations**（`@Contract(pure = true)`）、ならびに **JVM相互運用アノテーション**（`@JvmStatic`, `@JvmOverloads`, `@JvmSynthetic`, `@JvmName`, `@JvmField`, `@JvmInline`）の挙動検証用サンドボックスプロジェクトです。

---

## 目的

Java と Kotlin の相互運用において長年の課題であった「プラットフォーム型（`T!`）」による型安全性の低下を防ぎ、さらに Java から Kotlin を呼び出す際の見え方・呼び出し制御を実証・検証します。

1. **`@NullMarked` によるデフォルト Non-Null 化**:
   * `package-info.java` を用いてパッケージ単位で一括適用し、個別のアノテーション付与漏れによるプラットフォーム型の発生を防止。
2. **`@Nullable` の明示と Kotlin 側の型推論**:
   * 型使用位置（`TYPE_USE`）での null 許容宣言が、Kotlin 側で正しく `T?` として厳格に解釈されることを確認。
3. **`@NullUnmarked` による局所的エスケープ**:
   * `@NullMarked` 配下において、レガシー DTO や ORM/シリアライズ都合で nullability を強制したくないクラス・メソッドのみを未指定（プラットフォーム型）へ安全に回帰。
4. **`@Contract(pure = true)` による純粋関数の保証**:
   * 副作用のない計算ロジックに対し、戻り値の破棄（デッドコード）を IDE（IntelliJ IDEA）や静的解析ツールが正しく警告・検知できるかの検証。
5. **Spring Boot 4 / Kotlin コンパイラ厳格モードの親和性**:
   * `-Xjsr305=strict` を有効化し、JSpecify 仕様に準拠した Spring エコシステムとの境界検査。
6. **Java から Kotlin を呼び出す際の JVM アノテーション制御**:
   * `@JvmStatic`（クラス直下の静的メソッド化）
   * `@JvmOverloads`（デフォルト引数の多重定義生成）
   * `@JvmSynthetic`（Java コンパイラからのアクセサ・メソッド隠蔽）
   * `@JvmName`（型消去対策・Java 向け別名定義）
   * `@JvmField`（Getter/Setter を生成せず public フィールド化）
   * `@JvmInline`（Value Class のインライン展開とマングリング制御）

---

## 技術スタック

- **Language**: Java 25 / Kotlin 2.3+
- **Framework**: Spring Boot 4.1.2 (Spring MVC / WebClient)
- **Build Tool**: Gradle (Kotlin DSL)
- **Code Quality & Documentation**:
  * [Spotless 7.0.2](https://github.com/diffplug/spotless) (`ktlint 1.5.0` + `google-java-format 1.25.2`)
  * [Dokka 2.0.0](https://kotl.in/dokka) (V2 Plugin Mode)
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
| **データモデル層 (`models`)** | `@NullUnmarked` | `@NullMarked` の影響を局所的に解除し、外部連携用 DTO をプラットフォーム型に戻す。 |
| **相互運用検証層 (`bridge`)** | `@JvmStatic` | Kotlin の `companion object` メソッドを Java から直接呼べる静的メソッドとして公開。 |
| | `@JvmOverloads` | デフォルト引数を持つ Kotlin メソッドを Java から引数省略して呼べるようにオーバーロード生成。 |
| | `@JvmSynthetic` | Kotlin 専用 DSL やプロパティ Getter/Setter を Java コンパイラ（`javac`）から完全遮蔽。 |
| | `@JvmName` | バイトコード上のメソッド名を変更し、型消去によるシグネチャ重複解消や Value Class のマングリング回避。 |
| | `@JvmField` | プロパティのアクセサ生成を抑制し、Java から public フィールドとして直接アクセス可能にする。 |
| | `@JvmInline` | Value Class によりゼロアロケーションで型安全な識別子を表現。 |
| **Kotlin サービス・API層 (`services`, `controllers`)** | Kotlin (`-Xjsr305=strict`) | Java の型をプラットフォーム型（`!`）ではなく、厳格な `String` / `String?` として受領・処理。 |

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
│   │   │   ├── FareCalculationEngine.java  # @Contract(pure=true) 純粋計算エンジン
│   │   │   └── InMemoryTicketRepository.java# インメモリ状態管理リポジトリ
│   │   └── models/
│   │       ├── package-info.java            # models 配下を @NullMarked 化
│   │       ├── LegacyStationDto.java        # @NullMarked 適用 DTO
│   │       └── UnmarkedLegacyDto.java       # @NullUnmarked 適用 DTO
│   └── kotlin/net/konohana/sakuya/sandbox/
│       ├── SandboxApplication.kt            # Spring Boot エントリポイント
│       ├── bridge/
│       │   ├── StationId.kt                 # @JvmInline value class
│       │   ├── StationRouteResolver.kt      # Value Class 引数マングリング検証
│       │   ├── SyntheticPropertyHolder.kt   # @get:JvmSynthetic / @set:JvmSynthetic プロパティ遮蔽
│       │   ├── TicketCodeFilter.kt          # @JvmName による型消去対策関数
│       │   ├── TicketCodeGenerator.kt       # @JvmStatic, @JvmOverloads, @JvmSynthetic
│       │   └── TicketDirectAccessDto.kt     # @JvmField による生フィールドアクセス
│       ├── configs/
│       │   └── CorsWebConfig.kt             # Web API 向け CORS 構成
│       ├── controllers/
│       │   ├── GlobalExceptionHandler.kt    # RFC 7807 準拠の共通例外ハンドラー
│       │   └── TicketIssueController.kt     # 発券 REST API コントローラー
│       ├── models/
│       │   ├── ApiErrorResponse.kt          # 共通エラーレスポンス DTO
│       │   ├── IssueTicketRequest.kt        # 発券リクエスト DTO
│       │   ├── IssueTicketResponse.kt       # 発券レスポンス DTO
│       │   └── TicketIssueResult.kt         # 内部発券結果 Data Class
│       └── services/
│           ├── StationExternalLookupService.kt # WebClient による外部駅検証スタブ
│           └── TicketIssueService.kt        # アプリケーションサービス層
└── test/
    └── kotlin/net/konohana/sakuya/sandbox/
        ├── controllers/
        │   └── TicketIssueControllerTest.kt # API・CORS・バリデーション結合テスト
        ├── JSpecifyNullnessContractTest.kt  # JSpecify による Null 契約・プラットフォーム型排除テスト
        ├── JvmFieldContractTest.kt          # @JvmField フィールド直アクセス検証テスト
        ├── JvmInlineContractTest.kt         # @JvmInline アンボックス & マングリング検証テスト
        ├── JvmSyntheticPropertyVisibilityTest.kt # プロパティ Getter/Setter 遮蔽テスト
        ├── JvmSyntheticVisibilityTest.kt    # バイトコード上の ACC_SYNTHETIC 属性検証テスト
        ├── KotlinBridgeConsumerTest.kt      # Java ⇄ Kotlin 相互運用アノテーション検証テスト
        └── NullUnmarkedDtoContractTest.kt   # @NullUnmarked による未指定回帰検証テスト

```

---

## 開発・検証コマンド

### 1. コードフォーマット検証・自動適用 (Spotless)

Java（`google-java-format`）および Kotlin（`ktlint`）のコードフォーマット検証と自動修正を行います。

```bash
# フォーマット違反のチェック
./gradlew spotlessCheck

# フォーマット違反の自動修正・適用
./gradlew spotlessApply

```

### 2. API ドキュメント生成 (Dokka V2)

Kotlin と Java の双方の KDoc / JavaDoc を解析し、統一フォーマットの HTML ドキュメントを出力します。

```bash
# Dokka HTML ドキュメントの生成
./gradlew dokkaGenerate

```

> 生成されたドキュメントは `build/dokka/html/index.html` に出力されます。

### 3. テスト & 一括品質検証

```bash
# 全単体・結合テストの実行
./gradlew test

# テスト + Spotless フォーマット検証の一括実行
./gradlew check

```
