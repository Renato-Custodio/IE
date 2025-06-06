#!/bin/bash

source ./access.sh

# #Terraform - Quarkus ollama
cd Quarkus-Terraform/ollama
terraform destroy -auto-approve
cd ../..