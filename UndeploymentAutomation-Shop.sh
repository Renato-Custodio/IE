#!/bin/bash

source ./access.sh

# #Terraform - Quarkus shop
cd Quarkus-Terraform/shop
terraform destroy -auto-approve
cd ../..

# #Terraform - RDS
cd RDS-Terraform
terraform destroy -auto-approve
cd ..
