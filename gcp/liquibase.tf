resource "google_cloud_run_v2_job" "liquibase_clickhouse_job" {
  name     = "liquibase-clickhouse"
  location = "europe-west4"

  template {
    template {
      containers {
        image = "docker.io/nadberezny/flink-sessionizer-liquibase:latest"
        env {
          name  = "CLICKHOUSE_JDBC_CONNECTION_URL"
          value = "jdbc:clickhouse://j788slwiv7.europe-west4.gcp.clickhouse.cloud:8443?user=default&password=ZO7~oPHDWtLK9&ssl=true"
        }
        resources {
          limits = {
            memory = "512Mi"
            cpu    = "1"
          }
        }
      }
    }
  }
}
