resource "helm_release" "clickhouse_cluster" {
  chart            = "../k8s-helm/clickhouse-cluster"
  name             = "clickhouse-cluster"
  namespace        = local.namespace_services
  create_namespace = true

  lifecycle {
    prevent_destroy = false
  }

  depends_on = [
    module.k3d_cluster,
    helm_release.clickhouse_operator
  ]

  set {
    name  = "patchNonce" # Changing value effects in update when no other changes are detected
    value = 0
  }

  set {
    name  = "clusterName"
    value = "demo"
  }

  set {
    name  = "chVersionNo"
    value = "25.3"
  }

  set {
    name  = "shardsCount"
    value = 1
  }

  set {
    name  = "replicasCount"
    value = 1
  }

  set {
    name  = "chOperatorPassword"
    value = "clickhouse_operator_password"
  }
}
