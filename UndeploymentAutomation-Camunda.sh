#!/bin/bash

source ./access.sh

# Terraform - Camunda
cd Camunda-Terraform
terraform destroy -auto-approve
cd ../..