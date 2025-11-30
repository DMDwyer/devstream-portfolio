output "namespace" {
    description = "The Kubernetes namespace created for devstream"
    value       = kubernetes_namespace.devstream.metadata[0].name
}