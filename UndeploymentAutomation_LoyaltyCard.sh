#!/bin/bash


source ./access.sh


# #Terraform - Quarkus loyaltycard
cd Quarkus-Terraform/loyaltycard
terraform destroy -auto-approve
cd ../..

