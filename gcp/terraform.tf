terraform {
  backend "gcs" {
    bucket  = "gid-streaming-demo-platform"
    prefix  = "terraform/state"
  }

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 6.29.0"
    }
    clickhouse = {
      source  = "clickhouse/clickhouse"
      version = "3.0.0"
    }
    confluent = {
      source = "confluentinc/confluent"
      version = "2.24.0"
    }
  }
}
