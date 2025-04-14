resource "clickhouse_service" "streaming_demo" {
  name       	= "streaming-demo"
  cloud_provider = "gcp"
  region     	= "europe-west4"
  idle_scaling   = true
  idle_timeout_minutes = 5
  min_replica_memory_gb = 8
  max_replica_memory_gb = 8
  password  = var.ch_service_password
  ip_access = [
    {
      source  	= "0.0.0.0/0"
      description = "Anywhere"
    }
  ]
}
