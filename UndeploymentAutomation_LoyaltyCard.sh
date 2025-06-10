#!/bin/bash

source ./access.sh

# #Terraform - Quarkus loyaltycard
cd Quarkus-Terraform/loyalty-card
terraform destroy -auto-approve
cd ../..

