resource "helm_release" "grafana" {
  name       = "grafana"
  repository = "https://grafana.github.io/helm-charts"
  chart      = "grafana"
  version    = "8.6.4"

  create_namespace = true
  namespace = "cluster-services"

  values = [
    <<-EOF
    plugins:
      - grafana-clickhouse-datasource
    datasources:
      datasources.yaml:
        apiVersion: 1
        datasources:
        - name: ClickHouse
          type: grafana-clickhouse-datasource
          jsonData:
            defaultDatabase: getindata
            protocol: http
            port: 8123
            host: chi-clickhouse-cluster-default-0-0.cluster-services.svc.cluster.local
            username: default
            tlsSkipVerify: true
    EOF
  ]

  set {
    name  = "rbac.pspEnabled"
    value = "false"
  }

  set {
    name  = "rbac.create"
    value = "false"
  }

  set {
    name  = "persistence.enabled"
    value = "true"
  }

  set {
    type  = "string"
    name  = "adminPassword"
    value = "adminadmin"
  }

  set {
    type  = "string"
    name  = "adminUser"
    value = "admin"
  }

  # set {
  #   type  = "string"
  #   name  = "grafana\\.ini.server.root_url"
  #   value = "https://${local.grafana_host_domain}/"
  # }

  # set {
  #   type  = "string"
  #   name  = "ingress.enabled"
  #   value = "false"
  # }
  # set {
  #   name  = "ingress.hosts[0]"
  #   value = "localhost"
  # }
  #
  # set {
  #   type  = "string"
  #   name  = "ingress.annotations.kubernetes\\.io/ingress\\.class"
  #   value = "nginx"
  # }

  #
  # set {
  #   type  = "string"
  #   name  = "ingress.tls[0].secretName"
  #   value = "wildcard-internal"
  # }
  # set {
  #   name  = "ingress.tls[0].hosts[0]"
  #   value = local.grafana_host_domain
  # }
}
