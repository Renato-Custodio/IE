#!/bin/bash


source ./access.sh


# #Terraform - Quarkus loyaltycard
cd Quarkus-Terraform/shop
terraform destroy -auto-approve
cd ../..

