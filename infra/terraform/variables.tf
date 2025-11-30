variable "kubeconfig_path" {
  type        = string
  default     = "Path to kubeconfig file"
  description = "~/.kube/config"
}

variable "namespace" {
  type        = string
  default     = "Kubernetes namespace for devStream-portfolio"
  description = "devstream"
}
