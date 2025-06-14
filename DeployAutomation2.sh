#!/bin/bash

source ./access.sh

# Terraform - Camunda
cd Camunda-Terraform
terraform init
terraform apply -auto-approve
cd ..

# Update config file
# Terraform 1 - Kong
cd KongTerraform
terraform init
terraform apply -auto-approve
cd ..

# Showing all the PUBLIC_DNSs
echo CAMUNDA - 
cd Camunda-Terraform
terraform state show aws_instance.exampleInstallCamundaEngine |grep public_dns
echo "CAMUNDA IS AVAILABLE HERE:"
addressCamunda="$(terraform state show aws_instance.exampleInstallCamundaEngine |grep public_dns| sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
echo "http://"$addressCamunda":8080/camunda"
echo
cd ..

echo "KONG IS AVAILABLE HERE:" 
cd KongTerraform
addressKong="$(terraform state show aws_instance.exampleInstallKong |grep public_dns | sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
echo "http://"$addressKong":8000/"
echo
cd ..