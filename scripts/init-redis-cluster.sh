#!/bin/bash
set -euo pipefail

COMPOSE_FILE="compose.yaml"

# カレントディレクトリに存在するかチェック
if [ ! -f "$COMPOSE_FILE" ]; then
  echo "Error: ${COMPOSE_FILE} がカレントディレクトリに見つかりません。"
  echo "このスクリプトは compose.yaml があるディレクトリで実行してください。"
  exit 1
fi

docker compose down --volumes
docker compose up -d

sleep 5

docker compose exec -T entei redis-cli --cluster create \
  entei:6379 raikou:6379 suicune:6379 --cluster-yes



