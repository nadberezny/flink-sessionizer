variable "ch_organization_id" {
  type = string
}

variable "ch_token_key" {
  type = string
}

variable "ch_token_secret" {
  type = string
}

variable "ch_service_password" {
  type = string
  sensitive   = true
}

variable "confluent_cloud_key" {
  type = string
}

variable "confluent_cloud_secret" {
  type = string
}

variable "confluent_kafka_key" {
  type = string
}

variable "confluent_kafka_secret" {
  type = string
}
