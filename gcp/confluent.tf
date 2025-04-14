resource "confluent_environment" "development" {
  display_name = "Development"

  lifecycle {
    prevent_destroy = false
  }
}

resource "confluent_kafka_cluster" "basic" {
  display_name = "streaming_demo"
  availability = "SINGLE_ZONE"
  cloud        = "GCP"
  region       = "europe-west4"
  basic {}

  environment {
    id = confluent_environment.development.id
  }

  lifecycle {
    prevent_destroy = false
  }
}
