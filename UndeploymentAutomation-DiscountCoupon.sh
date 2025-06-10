#!/bin/bash

source ./access.sh

# #Terraform - Quarkus discount-coupon
cd Quarkus-Terraform/discount-coupon
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