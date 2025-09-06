#!/bin/bash

echo "🧹 Удаляем приложение из Kubernetes..."

kubectl delete -f ingress.yaml 2>/dev/null || true
kubectl delete -f services.yaml 2>/dev/null || true
kubectl delete -f deployments.yaml 2>/dev/null || true
kubectl delete -f configmap.yaml 2>/dev/null || true
kubectl delete -f pvc.yaml 2>/dev/null || true
kubectl delete -f namespace.yaml 2>/dev/null || true

echo "✅ Очистка завершена!"