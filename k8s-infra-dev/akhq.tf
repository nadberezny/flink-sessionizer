resource "helm_release" "akhq" {
  name             = "akhq"
  repository       = "https://akhq.io/"
  chart            = "akhq"
  version          = "0.25.0"
  namespace        = local.namespace_services
  create_namespace = true
  recreate_pods    = true
  reset_values     = true
  atomic           = true


  set {
    name = "secrets.akhq.connections.kafka-cluster.properties.bootstrap\\.servers"
    value = join(
      "\\,",
      [
        local.kafka_bootstrap_server
      ]
    )
  }

  set {
    name  = "ingress.enabled"
    value = "true"
    type  = "string"
  }

  set {
    name  = "ingress.hosts[0]"
    value = "localhost"
  }

  set {
    name  = "ingress.ingressClassName"
    value = "nginx"
  }

  depends_on = [
    helm_release.kafka,
    helm_release.nginx-ingress
  ]
}
