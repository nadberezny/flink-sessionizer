resource "helm_release" "clickhouse_operator" {
  name       = "clickhouse-operator"
  repository = "https://docs.altinity.com/clickhouse-operator/"
  chart      = "altinity-clickhouse-operator"
  namespace  = "kube-system"

  depends_on = [
    module.k3d_cluster
  ]
}
