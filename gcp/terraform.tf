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
  }
}
