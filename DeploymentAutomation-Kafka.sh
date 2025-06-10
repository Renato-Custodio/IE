#!/bin/bash

source ./access.sh

# Terraform - Kafka
cd Kafka
terraform init
terraform apply -auto-approve
esc=$'\e'
addresskafka="$(terraform state show 'aws_instance.exampleCluster[0]'|grep public_dns | sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
cd ..

# Echo Kafka
cd Kafka
echo "KAFKA IS AVAILABLE HERE:"
echo ""$addresskafka""
echo
cd ..
