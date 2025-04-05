resource "kubernetes_job" "liquibase" {
  depends_on = [
    helm_release.clickhouse_cluster,
  ]

  metadata {
    name      = "liquibase-job"
    namespace = local.namespace_services
  }

  spec {
    ttl_seconds_after_finished = "360"

    template {
      metadata {
        labels = {
          job = "liquibase"
        }
      }

      spec {
        container {
          name              = "liquibase"
          image             = "nadberezny/flink-sessionizer-liquibase:latest-arm"
          image_pull_policy = "Always"

          env {
            name  = "CLICKHOUSE_JDBC_CONNECTION_URL"
            value = "jdbc:clickhouse://chi-clickhouse-cluster-default-0-0.cluster-services.svc.cluster.local:8123"
          }

          env {
            name  = "KAFKA_URL"
            value = local.kafka_bootstrap_server
          }

          env {
            name  = "KAFA_SESSIONS_TOPIC"
            value = local.sessions_topic
          }

          env {
            name  = "KAFA_ATTRIBUTED_ORDERS_TOPIC"
            value = local.attributed_orders_topic
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
    backoff_limit = 190
  }
}
