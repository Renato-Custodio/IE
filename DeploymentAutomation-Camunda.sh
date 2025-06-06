#!/bin/bash

source ./access.sh


# Terraform - Camunda
cd Camunda-Terraform
terraform init
terraform taint aws_instance.exampleInstallCamundaEngine
terraform apply -auto-approve
cd ..

# Echo CAMUNDA
cd Camunda-Terraform
terraform state show aws_instance.exampleInstallCamundaEngine |grep public_dns
echo "CAMUNDA IS AVAILABLE HERE:"
addressCamunda="$(terraform state show aws_instance.exampleInstallCamundaEngine |grep public_dns| sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
echo "http://"$addressCamunda":8080/camunda"
echo
cd ..
