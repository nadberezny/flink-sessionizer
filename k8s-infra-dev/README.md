# Streaming K8s dev infra
Create local k8s cluster, install infrastructure on top of it and deploy streaming job.

## Infrastructure overview:
We are using [k3d](https://k3d.io/v5.4.9/) as a wrapper for k3s K8s distribution. 
For load balancing `nginx` is used instead of default `traefik`.
For the s3 storage we use [minio](https://min.io).
Currently, we expose only `akhq`. All other services needs to be exposed by port forwarding.

## Requirements:
terraform + helm + docker + k3d

## Quickstart
*commands listed below should be executed in current directory, ie. `k8s-infra-dev`*

0. Choose your CPU Architecture
If you are running ARM machine set `docker_platform = "linux/amd64"` in `locals.tf`
1. Initialize Terraform:  
`terraform init`  
2. Apply Terraform:  
`terraform apply -auto-approve`
Applying for the first time takes some time due to the images being pulled. 
*In case of failure caused by a timeout re-run terraform apply*

## Access ui consoles
1. **akhq** is already exposed on:  
`localhost:3080`
2. **flink**  
`kubectl port-forward --context k3d-flink svc/flink-rest 8081:8081` 
3. **minio/s3**  
`kubectl port-forward --context k3d-flink svc/minio 9001:9001`  
*user: admin password: adminadmin*

## Accessing Kafka
1. Forward Kafka PROXY listener
`kubectl port-forward --context k3d-flink svc/kafka 9094:9094`
2. Now you can connect to Kafka with e.g.
`kafka-topics --bootstrap-server localhost:9094 --list`

## Redeploy streaming job
First import new flink image:  
`terraform apply -replace=null_resource.import_flink_image -auto-approve`  
And then redeploy job:  
`terraform apply -replace=helm_release.flink_job -auto-approve`

## Suspend cluster
In order to suspend cluster run:  
`k3d cluster stop flink`  
to resume cluster:  
`k3d cluster start flink`  

## Destroy and start from scratch
1. Remove *terraform.tfstate* files:  
`rm terraform.tfstate*`
2. Destroy cluster:  
`k3d cluster delete flink`  
3. Init & Apply Terraform
