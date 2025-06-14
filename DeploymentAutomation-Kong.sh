#!/bin/bash

source ./access.sh
source ./kongVars.sh

# Terraform - Kong
cd KongTerraform
terraform init
terraform taint aws_instance.exampleInstallKong
terraform apply -auto-approve
cd ..

# Echo Kong
cd KongTerraform
terraform state show aws_instance.exampleInstallKong |grep public_dns
echo "KONG IS AVAILABLE HERE:"
addressKong="$(terraform state show aws_instance.exampleInstallKong |grep public_dns| sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
echo "http://"$addressKong":8000/"
echo
cd ..
