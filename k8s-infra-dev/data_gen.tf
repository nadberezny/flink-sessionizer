resource "helm_release" "data_gen" {
  chart            = "application"
  repository       = "https://stakater.github.io/stakater-charts"
  name             = "data-gen"
  namespace        = local.namespace_applications
  create_namespace = true
  recreate_pods    = true
  reset_values     = true
  verify           = false

  depends_on = [
    helm_release.kafka,
  ]

  set {
    name  = "applicationName"
    value = "data-gen"
  }

  set {
    name  = "deployment.replicas"
    value = "1"
  }


  set {
    name  = "deployment.image.pullPolicy"
    value = "Always"
  }

  set {
    name  = "deployment.image.repository"
    value = "nadberezny/flink-sessionizer-datagen"
  }

  set {
    name  = "deployment.image.tag"
    value = "latest"
    type  = "string"
  }

  set {
    name  = "deployment.securityContext.runAsUser"
    value = 999
  }

  set {
    name  = "deployment.env[0].name"
    value = "KAFKA_BOOTSTRAP_SERVERS"
  }

  set {
    name  = "deployment.env[0].value"
    value = local.kafka_bootstrap_server
  }

  set {
    name  = "deployment.env[1].name"
    value = "KAFKA_OUTPUT_TOPIC"
  }

  set {
    name  = "deployment.env[1].value"
    value = local.click_stream_topic
  }

  set {
    name  = "deployment.env[2].name"
    value = "FRONTEND_ID"
  }

  set {
    name  = "deployment.env[2].value"
    value = "TestMerchant1"
  }

  set {
    name  = "deployment.env[3].name"
    value = "SCHEDULE_USERS_INTERVAL"
  }

  set {
    name  = "deployment.env[3].value"
    value = "PT0.5S"
  }

  set {
    name  = "deployment.env[4].name"
    value = "MAX_ACTIVE_USERS"
  }

  set {
    name  = "deployment.env[4].value"
    value = "5"
    type  = "string"
  }

  set {
    name  = "deployment.env[5].name"
    value = "IS_DRY_RUN"
  }

  set {
    name  = "deployment.env[5].value"
    value = "false"
    type  = "string"
  }

  set {
    name  = "deployment.env[6].name"
    value = "PRODUCER_TYPE"
  }

  set {
    name  = "deployment.env[6].value"
    value = "KAFKA"
  }
}
