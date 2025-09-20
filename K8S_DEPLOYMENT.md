# Kubernetes デプロイメントガイド

## 事前準備

- Docker Desktop がインストールされていること
- Docker Desktop の Kubernetes を有効化していること
  - Docker Desktop の設定 → Kubernetes → Enable Kubernetes にチェック
- kubectlがインストールされていること

## ビルドとデプロイ手順

### 1. Dockerイメージのビルド

```bash
# Docker Desktop の場合、ローカルでビルドしたイメージをそのまま使用可能
docker build -t redisson-redis-cluster:latest .
```

### 2. Kubernetesリソースのデプロイ

```bash
# Redis のデプロイ
kubectl apply -f k8s/redis.yaml

# ConfigMapのデプロイ
kubectl apply -f k8s/configmap.yaml

# アプリケーションのデプロイ
kubectl apply -f k8s/deployment.yaml

# サービスのデプロイ
kubectl apply -f k8s/service.yaml

# または、一括でデプロイ
kubectl apply -f k8s/
```

### 3. デプロイメント状態の確認

```bash
# Podの状態確認
kubectl get pods

# サービスの確認
kubectl get services

# デプロイメントの確認
kubectl get deployments

# ログの確認
kubectl logs -l app=redisson-redis-cluster

# 特定のPodのログを確認
kubectl logs <pod-name>
```

### 4. アプリケーションへのアクセス

#### Docker Desktop の場合（port-forward）

```bash
# サービスへのポートフォワード
kubectl port-forward service/redisson-redis-cluster 8080:8080

# アプリケーションにアクセス
curl http://localhost:8080/actuator/health
```

#### LoadBalancer を使用する場合（Docker Desktop でも動作）

service.yamlのtypeをLoadBalancerに変更:

```yaml
spec:
  type: LoadBalancer
```

Docker Desktop では localhost でアクセス可能になります:

```bash
# サービスを更新
kubectl apply -f k8s/service.yaml

# External-IP が localhost になるまで待つ
kubectl get service redisson-redis-cluster -w

# アプリケーションにアクセス
curl http://localhost:8080/actuator/health
```

## 設定の更新

### アプリケーション設定の変更

1. `k8s/configmap.yaml`を編集
2. ConfigMapを更新:
   ```bash
   kubectl apply -f k8s/configmap.yaml
   ```
3. Podを再起動:
   ```bash
   kubectl rollout restart deployment redisson-redis-cluster
   ```

### スケーリング

```bash
# レプリカ数を変更
kubectl scale deployment redisson-redis-cluster --replicas=5
```

## クリーンアップ

```bash
# リソースの削除
kubectl delete -f k8s/

# または個別に削除
kubectl delete deployment redisson-redis-cluster
kubectl delete service redisson-redis-cluster
kubectl delete configmap redisson-redis-cluster-config
kubectl delete deployment redis
kubectl delete service redis-service
```

## トラブルシューティング

### Podが起動しない場合

```bash
# Podの詳細情報を確認
kubectl describe pod <pod-name>

# イベントを確認
kubectl get events --sort-by='.lastTimestamp'
```

### Redisへの接続エラー

```bash
# Redis Podが正常に動作しているか確認
kubectl get pods -l app=redis

# Redis サービスが正しく設定されているか確認
kubectl get service redis-service

# アプリケーションPodからRedisへの接続をテスト
kubectl exec -it <app-pod-name> -- sh
# Pod内で
nc -zv redis-service 6379
```

## Docker Desktop 固有の注意事項

- **メモリ制限**: Docker Desktop の設定でKubernetesに割り当てるメモリを調整可能
- **ローカルイメージ**: `imagePullPolicy: IfNotPresent` によりローカルイメージを優先的に使用
- **LoadBalancer**: Docker Desktop では自動的に localhost にマッピングされる
- **ストレージ**: Docker Desktop のボリュームは自動的に管理される

## 本番環境への考慮事項

1. **イメージレジストリ**: Docker HubやECR等のレジストリを使用
2. **Secret管理**: 機密情報はKubernetes Secretsで管理
3. **永続化**: Redisデータの永続化が必要な場合はPersistentVolumeを設定
4. **監視**: Prometheus、Grafana等でメトリクスを監視
5. **HPA**: Horizontal Pod Autoscalerでオートスケーリング設定
6. **リソース制限**: 適切なrequests/limitsを設定
