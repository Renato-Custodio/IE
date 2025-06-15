#!/bin/bash

source ./access.sh

# Terraform - Camunda
cd Camunda-Terraform
echo
echo Launching Camunda
echo
terraform init
terraform apply -auto-approve
cd ..

cd Quarkus-Terraform/ollama
echo
echo Launching ollama
echo
terraform init
terraform taint aws_instance.exampleOllamaConfiguration
terraform apply -auto-approve
cd ../..

# echo Quarkus -
cd Quarkus-Terraform/ollama
# terraform state show 'aws_instance.exampleOllamaConfiguration' |grep public_dns
echo "MICROSERVICE ollama IS AVAILABLE HERE:"
addressMS="$(terraform state show aws_instance.exampleOllamaConfiguration |grep public_dns | sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
# set ip for kong
sed -i "/^ip_discount_analysis_ai=/c ip_discount_analysis_ai=\"$addressMS\"" ../../kongVars.sh
echo "http://"$addressMS":8080/q/swagger-ui/"
echo
cd ../..

# Update Kong config file
source ./kongVars.sh
# Terraform - Kong
cd KongTerraform
echo
echo Launching Kong
echo
terraform init
terraform apply -auto-approve
cd ..

# Showing all the PUBLIC_DNSs 
cd Camunda-Terraform
# terraform state show aws_instance.exampleInstallCamundaEngine |grep public_dns
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