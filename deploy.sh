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
sleep 3

# Деплоим сервисы в правильном порядке
echo "🐘 Запускаем PostgreSQL deployment..."
kubectl apply -f deployments.yaml

echo "⏳ Ждем запуска PostgreSQL..."
kubectl wait --namespace=warehouse-app --for=condition=ready pod -l app=postgres --timeout=120s

echo "✅ PostgreSQL запущен"

echo "🦘 Запускаем Zookeeper..."
kubectl apply -f deployments.yaml

echo "📊 Ждем запуска Zookeeper..."
kubectl wait --namespace=warehouse-app --for=condition=ready pod -l app=zookeeper --timeout=120s

echo "✅ Zookeeper запущен"

echo "🚀 Запускаем Kafka..."
kubectl apply -f deployments.yaml

echo "📊 Ждем запуска Kafka..."
kubectl wait --namespace=warehouse-app --for=condition=ready pod -l app=kafka --timeout=120s

echo "✅ Kafka запущена"

echo "⚡ Запускаем Zeebe..."
kubectl apply -f deployments.yaml

echo "⏳ Ждем запуска Zeebe..."
sleep 10 # Даем Zeebe время на запуск
kubectl wait --namespace=warehouse-app --for=condition=ready pod -l app=zeebe --timeout=120s

echo "✅ Zeebe запущен"

echo "🔄 Запускаем Currencies Service..."
kubectl apply -f deployments.yaml

echo "⏳ Ждем запуска Currencies Service..."
kubectl wait --namespace=warehouse-app --for=condition=ready pod -l app=currencies-service --timeout=120s

echo "✅ Currencies Service запущен"

echo "🏢 Запускаем основное приложение..."
kubectl apply -f configmap.yaml
kubectl apply -f deployments.yaml

echo "⏳ Ждем запуска основного приложения..."
kubectl wait --namespace=warehouse-app --for=condition=ready pod -l app=warehouse-app --timeout=120s

echo "✅ Основное приложение запущено"

echo "🔗 Создаем сервисы..."
kubectl apply -f services.yaml

echo "✅ Сервисы созданы"

echo "📊 Проверяем статус всех подов:"
kubectl get pods -n warehouse-app -o wide

echo "🌐 Проверяем сервисы:"
kubectl get svc -n warehouse-app

echo "📋 Проверяем логи PostgreSQL:"
kubectl logs -n warehouse-app -l app=postgres --tail=10

echo "🚀 Для доступа к приложению используйте:"
echo "kubectl port-forward -n warehouse-app svc/warehouse-app 8080:8080"
echo "Затем откройте http://localhost:8080"

echo "✅ Деплой завершен!"