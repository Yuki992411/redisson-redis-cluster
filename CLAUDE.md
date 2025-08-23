# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## プロジェクト概要

Spring Boot 3.5.5 と Kotlin 1.9.25 を使用した Redisson Redis Cluster プロジェクトです。Java 21 をターゲットとし、オニオンアーキテクチャに基づいたマルチモジュール構成になっています。分散ロックを使用した重複リクエスト防止機能を実装しています。

## オニオンアーキテクチャ構成

このプロジェクトは以下の 3 つのモジュールで構成されています：

-   **domain**: ドメインコア（エンティティ、ドメインサービス、リポジトリインターフェース）
-   **infrastructure**: 外部システムとの統合（Redis、データベース、リポジトリ実装）
-   **application**: アプリケーションサービス、ユースケース、コントローラー、プレゼンテーション層（実行可能 JAR）

## ビルドコマンド

### アプリケーションのビルド

```bash
# 全モジュールのビルド
./gradlew build

# 特定モジュールのビルド
./gradlew :application:build
./gradlew :domain:build
```

### アプリケーションの実行

```bash
# applicationモジュールの実行
./gradlew :application:bootRun
```

### テストの実行

```bash
# 全モジュールのテスト実行
./gradlew test

# 特定モジュールのテスト実行
./gradlew :application:test
./gradlew :domain:test

# 特定のテストクラスの実行
./gradlew :application:test --tests "com.example.yukikom.redisson_redis_cluster.application.RedissonRedisClusterApplicationTests"
```

### クリーンビルド

```bash
./gradlew clean build
```

### 依存関係の確認

```bash
# 全モジュールの依存関係
./gradlew dependencies

# 特定モジュールの依存関係
./gradlew :application:dependencies
```

### コードスタイルチェック（ktlint）

```bash
# コードスタイルチェック
./gradlew ktlintCheck

# 自動フォーマット
./gradlew ktlintFormat

# ベースライン生成（既存の違反を無視）
./gradlew ktlintGenerateBaseline
```

### 静的解析（detekt）

```bash
# 静的解析実行
./gradlew detekt

# 設定ファイル生成（必要に応じて）
./gradlew detektGenerateConfig

# ベースライン生成（既存の違反を無視）
./gradlew detektBaseline
```

## モジュール間の依存関係（オニオンアーキテクチャ）

```
application → infrastructure → domain
       ↘             ↗
         domain
```

-   **application**: infrastructure, domain に依存
-   **infrastructure**: domain のみに依存
-   **domain**: 他のモジュールに依存しない（最内層）

## 技術スタック

-   **フレームワーク**: Spring Boot 3.5.5
-   **言語**: Kotlin 2.0.21
-   **ビルドツール**: Gradle 8.14.3
-   **Java**: Java 21 (toolchain)
-   **Redis**: Redisson 3.24.3
-   **テストフレームワーク**: JUnit 5
-   **コードフォーマッター**: ktlint 1.7.1 (Gradle plugin 12.1.2)
-   **静的解析**: detekt 1.23.8 (デフォルト設定)

## パッケージ構造（オニオンアーキテクチャ）

各モジュールは以下の構造を持ちます：

```
domain/ (Domain Core - 最内層)
  └── src/main/kotlin/com/example/yukikom/redisson_redis_cluster/domain/
      ├── model/      # エンティティとドメインオブジェクト
      └── repository/ # リポジトリインターフェース（抽象）

infrastructure/ (Infrastructure Layer)
  └── src/main/kotlin/com/example/yukikom/redisson_redis_cluster/infrastructure/
      ├── config/     # Spring設定クラス、外部ライブラリ設定
      └── repository/ # リポジトリ実装（具象）

application/ (Application Services + Presentation Layer)
  └── src/main/kotlin/com/example/yukikom/redisson_redis_cluster/application/
      ├── service/    # アプリケーションサービス
      ├── usecase/    # ユースケース
      ├── controller/ # REST コントローラー（プレゼンテーション層）
      ├── dto/        # データ転送オブジェクト
      └── RedissonRedisClusterApplication.kt # エントリーポイント
```

## 重要な注意事項

-   パッケージ名は`com.example.yukikom.redisson_redis_cluster`を使用（ハイフンではなくアンダースコア）
-   Kotlin compiler オプションで`-Xjsr305=strict`が有効になっており、null 安全性が厳格に適用されます
-   実行可能 JAR は application モジュールでのみ生成されます
-   オニオンアーキテクチャの依存関係の方向を守ってください：
    -   ドメイン層（domain）は最内層で、外部に依存しない
    -   インフラ層（infrastructure）はドメイン層のみに依存
    -   アプリケーション層（application）は外側の層として、内側の層に依存可能

## 設定管理

### application.yaml

設定は YAML 形式で管理され、`@ConfigurationProperties`を使用して型安全に読み込まれます。

```yaml
redis:
    single-server:
        address: redis://localhost:6381
        connection-pool-size: 10
    lock:
        default-timeout-seconds: 30

logging:
    level:
        com.example.yukikom.redisson_redis_cluster: DEBUG
```

### ログ設定

-   **ログフレームワーク**: SLF4J + Logback
-   **ログフォーマット**: `%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n`
-   **ログレベル**: アプリケーション(DEBUG)、Redisson(INFO)、Spring(INFO)
-   **設定ファイル**: `logback-spring.xml`

## コーディング規約

### ロギング

-   **必須**: すべてのログ出力は`logger`を使用すること（`println`や`System.out`は使用禁止）
-   **ログレベルの使い分け**:
    -   `ERROR`: 例外やエラー状態
    -   `INFO`: 重要な処理の開始・完了
    -   `DEBUG`: デバッグ情報（開発環境のみ）

### エラーハンドリング

-   例外は適切にキャッチし、ログ出力する
-   InterruptedException では必ず`Thread.currentThread().interrupt()`を呼ぶ

### Redisson 設定

-   Single Server Mode を使用（クラスターモードは現在未対応）
-   分散ロックのタイムアウトはデフォルト 30 秒（`@PreventDuplicate`アノテーションでカスタマイズ可能）

## Docker 環境

### Redis 起動

```bash
docker compose up -d
```

### Redis 初期化（必要に応じて）

```bash
bash scripts/init-redis-cluster.sh
```

## 共通基盤機能

### 重複リクエスト防止

-   `@PreventDuplicate`アノテーションをコントローラーメソッドに付与
-   Redisson の分散ロックを使用して API 単位でロック管理
-   ロックキー形式: `lock:api:{request.requestURI}`
-   重複リクエスト時は HTTP 409 (Conflict)を返却

### Interceptor

-   `DuplicateRequestInterceptor`: 重複リクエスト防止の実装
-   `/api/**`パスに対して自動的に適用
-   ThreadLocal でロック管理を行い、リクエスト完了時に確実に解放
