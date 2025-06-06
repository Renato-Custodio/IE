#!/bin/bash

source ./access.sh

# Terraform Ollama
cd Quarkus-Terraform/ollama
terraform init
terraform taint aws_instance.exampleOllamaConfiguration
terraform apply -auto-approve
cd ../..

#echo Ollama
cd Quarkus-Terraform/ollama
#terraform state show 'aws_instance.exampleDeployQuarkus' |grep public_dns
echo "MICROSERVICE Ollama IS AVAILABLE HERE:"
addressMS="$(terraform state show aws_instance.exampleOllamaConfiguration |grep public_dns | sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
echo "http://"$addressMS":11434/api/generate"
echo
cd ../..

