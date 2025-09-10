#!/bin/bash

docker stop $(docker ps -aq) 2>/dev/null || true
docker rm $(docker ps -aq) 2>/dev/null || true

docker rmi -f $(docker images -q) 2>/dev/null || true

docker volume rm $(docker volume ls -q) 2>/dev/null || true

docker network rm $(docker network ls -q) 2>/dev/null || true

kubectl delete all --all -n warehouse-app

kubectl delete pvc --all -n warehouse-app
kubectl delete configmap --all -n warehouse-app

kubectl delete namespace warehouse-app

# Остановить все запущенные контейнеры
docker stop $(docker ps -aq)

# Удалить все контейнеры
docker rm $(docker ps -aq)
#
# Вместо простых команд build:
docker build --platform linux/amd64 -t yamatasuper/warehouse-app:latest .
docker build --platform linux/amd64 -t yamatasuper/currencies-service:latest .
docker push yamatasuper/warehouse-app:latest
docker push yamatasuper/currencies-service:latest

# Если кластер не создан, создайте его
kind create cluster --name warehouse

# Или если кластер уже существует но не запущен
kind start cluster --name warehouse



echo "🧹 Удаляем приложение из Kubernetes..."

kubectl delete -f ingress.yaml 2>/dev/null || true
kubectl delete -f services.yaml 2>/dev/null || true
kubectl delete -f deployments.yaml 2>/dev/null || true
kubectl delete -f configmap.yaml 2>/dev/null || true
kubectl delete -f pvc.yaml 2>/dev/null || true
kubectl delete -f namespace.yaml 2>/dev/null || true

echo "✅ Очистка завершена!"

 Скрипт деплоя приложения в Kubernetes

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
    local timeout=300
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