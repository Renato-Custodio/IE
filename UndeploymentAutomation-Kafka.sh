#!/bin/bash

source ./access.sh

# Terraform - Kafka
cd Kafka
terraform destroy -auto-approve
cd ..