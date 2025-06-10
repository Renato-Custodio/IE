#!/bin/bash

source ./access.sh

# #Terraform - Quarkus loyaltycard
cd Quarkus-Terraform/loyalty-card
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