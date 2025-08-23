# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## プロジェクト概要

Spring Boot 3.5.5とKotlin 1.9.25を使用したRedisson Redis Clusterプロジェクトです。Java 21をターゲットとし、オニオンアーキテクチャに基づいたマルチモジュール構成になっています。

## オニオンアーキテクチャ構成

このプロジェクトは以下の3つのモジュールで構成されています：

- **domain**: ドメインコア（エンティティ、ドメインサービス、リポジトリインターフェース）
- **infrastructure**: 外部システムとの統合（Redis、データベース、リポジトリ実装）
- **application**: アプリケーションサービス、ユースケース、コントローラー、プレゼンテーション層（実行可能JAR）

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

## モジュール間の依存関係（オニオンアーキテクチャ）

```
application → infrastructure → domain
       ↘             ↗
         domain
```

- **application**: infrastructure, domainに依存
- **infrastructure**: domainのみに依存
- **domain**: 他のモジュールに依存しない（最内層）

## 技術スタック

- **フレームワーク**: Spring Boot 3.5.5
- **言語**: Kotlin 1.9.25
- **ビルドツール**: Gradle 8.14.3
- **Java**: Java 21 (toolchain)
- **Redis**: Redisson 3.24.3
- **テストフレームワーク**: JUnit 5

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

- パッケージ名は`com.example.yukikom.redisson_redis_cluster`を使用（ハイフンではなくアンダースコア）
- Kotlin compilerオプションで`-Xjsr305=strict`が有効になっており、null安全性が厳格に適用されます
- 実行可能JARはapplicationモジュールでのみ生成されます
- オニオンアーキテクチャの依存関係の方向を守ってください：
  - ドメイン層（domain）は最内層で、外部に依存しない
  - インフラ層（infrastructure）はドメイン層のみに依存
  - アプリケーション層（application）は外側の層として、内側の層に依存可能