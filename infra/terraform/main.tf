terraform {
    required_version = ">= 1.5.0"

    required_providers {
        kubernetes = {
            source  = "hashicorp/kubernetes"
            version = "-> 2.26"
        }
    }
}

provider "kubernetes" {
    config_path = var.kubeconfig_path
}

resource "kubernetes_namespace" "devstream" {
    metadata {
        name = var.namespace
    }
}

resource "kubernetes_config_map" "devstream_config" {
    metadata {
        name      = "devstream_config"
        namespace = kubernetes_namespace.devstream.metadata[0].name
    }

    data = {
        FEATURE_FLAGS_DEFAULT = "false"
    }
}