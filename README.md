# jspecify-spring-sandbox

Java 25、Spring Boot 4、Kotlin 2.x のハイブリッド構成における **JSpecify 1.0**（Null安全性標準仕様）、**JetBrains Annotations**（`@Contract(pure = true)`）、**JVM相互運用アノテーション**、および **WebClient × Kotlin Coroutines / Flow によるノンブロッキング非同期ストリーミング** の挙動検証用サンドボックスプロジェクトです。

---

## 目的

Java と Kotlin の相互運用における型安全性（プラットフォーム型の排除）と、Spring Boot 4 上でのモダンな非同期・リアクティブ連携を包括的に実証・検証します。

1. **`@NullMarked` によるデフォルト Non-Null 化**:
   * `package-info.java` を用いてパッケージ単位で一括適用し、個別アノテーションの付与漏れによるプラットフォーム型（`T!`）の発生を防止。
2. **`@Nullable` と Kotlin Null 安全性の連動**:
   * 型使用位置（`TYPE_USE`）での null 許容宣言が、Kotlin 側で厳格な `T?` として解釈されることを確認。
3. **Java `Optional<T>` ⇄ Kotlin `T?` の境界設計**:
   * JSpecify 環境下における `Optional` 自身の Non-Null 保証。
   * 自前拡張関数（`toNullable()` / `toOptional()`）によるシームレスかつ Opt-in 不要な相互変換。
4. **WebClient と Kotlin Coroutines (`suspend`) / `Flow` の非同期統合**:
   * ブロッキング I/O を排除したノンブロッキング外部 API 呼び出し。
   * Reactor `Flux` から Kotlin `Flow` へのバックプレッシャー維持変換（`bodyToFlow`）と NDJSON ストリーミング。
   * `WebClientResponseException.NotFound` による 404 時の安全な `null` 解決。
5. **JVM 相互運用アノテーションによる Java 向け API 制御**:
   * `@JvmStatic`, `@JvmOverloads`, `@JvmSynthetic`, `@JvmName`, `@JvmField`, `@JvmInline`（Value Class）。
6. **決定論的な非同期テスト基盤**:
   * `MockWebServer` を用いた外部 API 依存のない高速かつ決定論的なコルーチン結合テスト（`runTest`）。

---

## 技術スタック

- **Language**: Java 25 / Kotlin 2.3+
- **Framework**: Spring Boot 4.1.2 (Spring MVC / Spring WebFlux)
- **Async & Reactive**:
  * Kotlin Coroutines Core & Reactor (`kotlinx-coroutines-core`, `kotlinx-coroutines-reactor`)
  * Spring WebClient (HTTP Client)
- **Build Tool**: Gradle (Kotlin DSL)
- **Code Quality & Documentation**:
  * [Spotless 7.0.2](https://github.com/diffplug/spotless) (`ktlint 1.5.0` + `palantirJavaFormat`)
  * [Dokka 2.0.0](https://kotl.in/dokka) (V2 Plugin Mode)
- **Specifications & Testing**:
  * [JSpecify 1.0.0](https://jspecify.dev/) (`org.jspecify:jspecify`)
  * JetBrains Java Annotations (`org.jetbrains:annotations:26.0.2`)
  * OkHttp MockWebServer 4.12.0
  * Kotlinx Coroutines Test 1.10.1

---

## アーキテクチャとアノテーションの役割分担

| 担当レイヤー | 使用技術・アノテーション | 目的 |
|---|---|---|
| **Java ドメイン計算層 (`domain`)** | `@NullMarked` (`package-info.java`) | パッケージ配下の全メソッド・引数・戻り値をデフォルトで `@NonNull` 扱いにする。 |
| | `@Nullable` | null を返却／許容する箇所のみをピンポイントで明示（Kotlin 側で `T?` に強制）。 |
| | `@Contract(pure = true)` | 副作用を持たない純粋関数であることを宣言し、戻り値の破棄を警告。 |
| | `Optional<T>` | カタログ検索等の欠損可能性を Non-Null な `Optional` で型安全に表現。 |
| **相互運用層 (`bridge`)** | `@JvmInline value class` | ゼロアロケーションで型安全な駅コード識別子（`StationId`）を表現。 |
| | `@JvmName` | Java 向け別名定義、型消去回避、Value Class 引数の名前マングリング回避。 |
| | `toNullable()` / `toOptional()` | `Optional<T>` と `T?` を安全に相互変換する拡張関数。 |
| | `@JvmSynthetic` / `@JvmField` | Java コンパイラからのアクセサ遮蔽、およびフィールド直アクセス化。 |
| **非同期・外部連携層 (`services`)** | `WebClient` + `suspend` | `awaitBodyOrNull<T>()` による単一リソースの非同期中断・取得。 |
| | `WebClient` + `Flow<T>` | `bodyToFlow<T>()` により Reactor `Flux` を Kotlin `Flow` へ変換しストリーミング。 |
| **Web API 層 (`controllers`)** | `@RestControllerAdvice` | RFC 7807 準拠の共通例外ハンドリング（400/500 の統一 JSON 返却）。 |
| | CORS WebMvcConfigurer | プリフライト（OPTIONS）およびクロスオリジン通信の制御。 |
| | `Flow` エンドポイント | `application/x-ndjson` メディアタイプによる非同期ストリーム直接返却。 |

---

## 非同期ストリーミングのシーケンス構成

```text
Client (NDJSON Stream Request)
   │
   ▼
[StationAsyncCatalogController] ── (return Flow<StationCatalogEntry>)
   │
   ▼
[StationAsyncCatalogService]
   │
   ├── webClient.get()
   │     .uri("/api/v1/stations/stream?sector={sector}")
   │     .accept(MediaType.APPLICATION_NDJSON)
   │     .retrieve()
   │     .bodyToFlow<StationCatalogEntry>()  <── Flux から Flow への変換
   │
   ▼
Upstream External API / MockWebServer

```

---

## ディレクトリ構成

```text
src/
├── main/
│   ├── java/net/konohana/sakuya/sandbox/
│   │   ├── bridge/
│   │   │   ├── package-info.java                # bridge 配下を @NullMarked 化
│   │   │   └── KotlinBridgeConsumer.java        # Java から Kotlin を呼ぶ検証クラス
│   │   ├── domain/
│   │   │   ├── package-info.java                # domain 配下を @NullMarked 化
│   │   │   ├── FareCalculationEngine.java      # @Contract(pure=true) 純粋計算エンジン
│   │   │   ├── InMemoryTicketRepository.java    # インメモリ状態管理リポジトリ
│   │   │   └── StationCatalogLookupEngine.java  # Optional 返却・受領エンジン
│   │   └── models/
│   │       ├── package-info.java                # models 配下を @NullMarked 化
│   │       ├── LegacyStationDto.java            # @NullMarked 適用 DTO
│   │       └── UnmarkedLegacyDto.java           # @NullUnmarked 適用 DTO
│   └── kotlin/net/konohana/sakuya/sandbox/
│       ├── SandboxApplication.kt                # Spring Boot エントリポイント
│       ├── bridge/
│       │   ├── OptionalExtensions.kt            # toNullable() / toOptional() 拡張
│       │   ├── StationId.kt                     # @JvmInline value class
│       │   ├── StationLookupBridge.kt           # @JvmName 経由の Optional ファサード
│       │   ├── StationRouteResolver.kt          # Value Class 引数マングリング検証
│       │   ├── SyntheticPropertyHolder.kt       # プロパティ Getter/Setter 遮蔽
│       │   ├── TicketCodeFilter.kt              # 型消去対策関数
│       │   ├── TicketCodeGenerator.kt           # @JvmStatic, @JvmOverloads
│       │   └── TicketDirectAccessDto.kt         # @JvmField による生フィールドアクセス
│       ├── configs/
│       │   └── CorsWebConfig.kt                 # CORS 構成
│       ├── controllers/
│       │   ├── GlobalExceptionHandler.kt        # 共通例外ハンドラー
│       │   ├── StationAsyncCatalogController.kt # suspend / Flow 非同期 REST API
│       │   └── TicketIssueController.kt         # 発券 REST API
│       ├── models/
│       │   ├── ApiErrorResponse.kt              # エラーレスポンス DTO
│       │   ├── IssueTicketRequest.kt            # 発券リクエスト DTO
│       │   ├── IssueTicketResponse.kt           # 発券レスポンス DTO
│       │   ├── StationCatalogEntry.kt           # 外部カタログ連携 DTO
│       │   └── TicketIssueResult.kt             # 内部発券結果 Data Class
│       └── services/
│           ├── StationAsyncCatalogService.kt    # WebClient × Coroutines 非同期サービス
│           ├── StationExternalLookupService.kt  # 外部駅検証スタブ
│           └── TicketIssueService.kt            # 発券アプリケーションサービス
└── test/
    └── kotlin/net/konohana/sakuya/sandbox/
        ├── controllers/
        │   └── TicketIssueControllerTest.kt     # 発券 API・CORS 結合テスト
        ├── services/
        │   └── StationAsyncCatalogServiceTest.kt# MockWebServer による Coroutines / Flow 検証
        ├── JSpecifyNullnessContractTest.kt      # JSpecify による Null 契約テスト
        ├── JvmFieldContractTest.kt              # @JvmField 検証テスト
        ├── JvmInlineContractTest.kt             # @JvmInline アンボックス検証テスト
        ├── JvmSyntheticPropertyVisibilityTest.kt# プロパティ遮蔽テスト
        ├── JvmSyntheticVisibilityTest.kt        # バイトコード ACC_SYNTHETIC テスト
        ├── KotlinBridgeConsumerTest.kt          # Java ⇄ Kotlin 相互運用テスト
        ├── NullUnmarkedDtoContractTest.kt       # @NullUnmarked 回帰検証テスト
        └── OptionalInteroperabilityContractTest.kt # Optional ⇄ Nullable 相互運用テスト

```

---

## 開発・検証コマンド

### 1. コードフォーマット検証・適用 (Spotless)

Java 25 環境下での安定動作のため、Java 側は `palantirJavaFormat`、Kotlin 側は `ktlint` を採用しています。

```bash
# フォーマット違反のチェック
./gradlew spotlessCheck

# フォーマット違反の自動修正・適用
./gradlew spotlessApply

```

### 2. ドキュメント生成 (Dokka V2)

Dokka Gradle Plugin V2 モードにより、JavaDoc および KDoc を統合した HTML ドキュメントを出力します。

```bash
# Dokka HTML ドキュメントの生成
./gradlew dokkaGenerate

```

> 生成結果は `build/dokka/html/index.html` に出力されます。

### 3. テストの個別実行

```bash
# 外部カタログ非同期通信（WebClient + Coroutines / Flow）のテスト
./gradlew test --tests "net.konohana.sakuya.sandbox.services.StationAsyncCatalogServiceTest"

# Java Optional ⇄ Kotlin Nullable 境界検証テスト
./gradlew test --tests "net.konohana.sakuya.sandbox.OptionalInteroperabilityContractTest"

# REST API・CORS・エラーハンドリング結合テスト
./gradlew test --tests "net.konohana.sakuya.sandbox.controllers.TicketIssueControllerTest"

```

### 4. 一括品質ゲート (`check`)

Spotless のフォーマット検証と全テストスイートを一括実行します。

```bash
./gradlew check

```
