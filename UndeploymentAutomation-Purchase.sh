#!/bin/bash

source ./access.sh

# #Terraform - Quarkus purchase
cd Quarkus-Terraform/purchase
terraform destroy -auto-approve
cd ../..

# #Terraform - RDS
cd RDS-Terraform
terraform destroy -auto-approve
cd ..

# Terraform - Kafka
cd Kafka
terraform destroy -auto-approve
cd ..