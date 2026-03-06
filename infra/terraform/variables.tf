variable "kubeconfig_path" {
  type        = string
  description     = "Path to kubeconfig file"
  default = "~/.kube/config"
}

variable "namespace" {
  type        = string
  description     = "Kubernetes namespace for devStream-portfolio"
  default = "devstream"
}

variable "image_tag" {
  type        = string
  description = "Docker image tag to deploy — set from CI via github.sha"
  default     = "latest"
}
