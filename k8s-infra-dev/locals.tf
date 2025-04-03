locals {
  namespace_applications = "stream"
  namespace_services     = "cluster-services"
  flink_operator_version = "1.10.0"
  flink_ha_dir           = "high-availability/flink"
  flink_checkpoints_dir  = "checkpoints/flink"
  flink_savepoints_dir   = "savepoints/flink"
  kafka_bootstrap_server = "kafka.${local.namespace_services}.svc.cluster.local:9092"
  minio_user             = "admin"
  minio_password         = "adminadmin"
  flink_image_name       = "nadberezny/flink-sessionizer"

  docker_platform = "linux/arm64"
  flink_image_tag = "latest"
}
