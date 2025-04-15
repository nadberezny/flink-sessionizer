resource "confluent_environment" "development" {
  display_name = "Development"
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
}

resource "confluent_kafka_topic" "topics" {
  for_each = toset(["orders"])

  kafka_cluster {
    id = confluent_kafka_cluster.basic.id
  }

  topic_name    = each.key
  rest_endpoint = confluent_kafka_cluster.basic.rest_endpoint

  credentials {
    key    = var.confluent_kafka_key
    secret = var.confluent_kafka_secret
  }
}
