resource "helm_release" "flink_job" {
  chart            = "../k8s-helm/flink"
  name             = "flink-sessionizer"
  namespace        = local.namespace_applications
  create_namespace = true

  values = [
    file("../k8s-helm/flink/values.yaml")
  ]

  set {
    name  = "image.pullPolicy"
    value = "Always"
  }

  set {
    name  = "image.repository"
    value = local.flink_image_name
  }

  set {
    name  = "image.tag"
    value = local.flink_image_tag
  }

  set {
    name  = "kafka.bootstrapServers"
    value = local.kafka_bootstrap_server
  }

  set {
    name  = "s3.endpoint"
    value = "http://minio.${local.namespace_services}.svc.cluster.local:9000"
  }

  set {
    name  = "s3.accessKey"
    value = local.minio_user
  }

  set {
    name  = "s3.secretKey"
    value = local.minio_password
  }

  set {
    name  = "ha.dir"
    value = "s3://${local.flink_ha_dir}"
  }

  set {
    name  = "state.checkpointsDir"
    value = "s3://${local.flink_savepoints_dir}"
  }

  set {
    name  = "state.savepointsDir"
    value = "s3://${local.flink_savepoints_dir}"
  }

  set {
    name  = "secrets.mountPath"
    value = "/mnt/secrets-store"
  }

  depends_on = [
    helm_release.minio,
    helm_release.kafka,
    helm_release.flink_operator,
  ]
}
