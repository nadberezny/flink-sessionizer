resource "kubernetes_job" "liquibase" {
  depends_on = [
    helm_release.clickhouse_cluster
  ]

  metadata {
    name = "liquibase-job"
    namespace = local.namespace_services
  }

  spec {
    template {
      metadata {
        labels = {
          job = "liquibase"
        }
      }

      spec {
        container {
          name  = "liquibase"
          image = "nadberezny/flink-sessionizer-liquibase:latest-arm"
          image_pull_policy = "Always"

          env {
            name  = "CLICKHOUSE_JDBC_CONNECTION_URL"
            value = "jdbc:clickhouse://chi-clickhouse-cluster-default-0-0-0.chi-clickhouse-cluster-default-0-0.cluster-services.svc.cluster.local:8123"
          }

          resources {
            requests = {
              cpu    = "250m"
              memory = "256Mi"
            }
          }
        }
        restart_policy = "Never"
      }
    }
    backoff_limit = 0
  }
}
