#!/bin/bash

# Скрипт деплоя приложения в Kubernetes

set -e

echo "🚀 Начинаем деплой приложения в Kubernetes..."

# Создаем namespace
echo "📦 Создаем namespace..."
kubectl apply -f namespace.yaml

# Создаем Persistent Volume Claim
echo "💾 Создаем PVC для PostgreSQL..."
kubectl apply -f pvc.yaml

# Ждем пока PVC будет готов
echo "⏳ Ждем готовности PVC..."
sleep 5

# Деплоим все компоненты
echo "🐘 Запускаем все deployments..."
kubectl apply -f deployments.yaml

# Функция для ожидания готовности пода
wait_for_pod() {
    local app_label=$1
    local timeout=60
    local attempt=0

    echo "⏳ Ждем запуска $app_label..."

    while [ $attempt -lt $timeout ]; do
        # Проверяем, есть ли вообще под с таким лейблом
        if ! kubectl get pod -n warehouse-app -l app=$app_label &> /dev/null; then
            echo "❌ Под с лейблом app=$app_label не найден"
            return 1
        fi

        # Проверяем готовность
        if kubectl get pod -n warehouse-app -l app=$app_label -o jsonpath='{.items[*].status.conditions[?(@.type=="Ready")].status}' | grep -q "True"; then
            echo "✅ $app_label запущен и готов"
            return 0
        fi

        attempt=$((attempt + 5))
        sleep 5
        echo "Ожидание $app_label: $attempt/$timeout секунд"
    done

    echo "❌ Таймаут ожидания $app_label"
    kubectl describe pod -n warehouse-app -l app=$app_label
    kubectl logs -n warehouse-app -l app=$app_label
    return 1
}

# Ожидаем компоненты в правильном порядке
wait_for_pod "postgres"
wait_for_pod "zookeeper"
wait_for_pod "kafka"
sleep 10  # Даем Kafka время на инициализацию
wait_for_pod "zeebe"
wait_for_pod "currencies-service"
wait_for_pod "warehouse-app"

echo "🔗 Применяем сервисы..."
kubectl apply -f services.yaml

echo "✅ Все компоненты запущены!"
echo "📊 Проверяем статус:"
kubectl get pods -n warehouse-app -o wide

echo "🌐 Сервисы:"
kubectl get svc -n warehouse-app

echo "🚀 Для доступа к приложению:"
echo "kubectl port-forward -n warehouse-app svc/warehouse-app 8080:8080"
echo "Затем откройте http://localhost:8080"