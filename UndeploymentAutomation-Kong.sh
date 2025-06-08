#!/bin/bash

source ./access.sh

# Terraform - Kong
cd KongTerraform
terraform destroy -auto-approve
cd ../..