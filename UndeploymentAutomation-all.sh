#!/bin/bash


source ./access.sh

# #Terraform - Quarkus purchase
cd Quarkus-Terraform/purchase
terraform destroy -auto-approve
cd ../..

# #Terraform - Quarkus customer
cd Quarkus-Terraform/customer
terraform destroy -auto-approve
cd ../..

# #Terraform - Quarkus shop
cd Quarkus-Terraform/shop
terraform destroy -auto-approve
cd ../..

# #Terraform - Quarkus loyaltycard
cd Quarkus-Terraform/loyalty-card
terraform destroy -auto-approve
cd ../..

# #Terraform - Quarkus discountCoupon
cd Quarkus-Terraform/discount-coupon
terraform destroy -auto-approve
cd ../..

# #Terraform - RDS
cd RDS-Terraform
terraform destroy -auto-approve
cd ..

# #Terraform - Camunda
cd Camunda-Terraform
terraform destroy -auto-approve
cd ..

# # #Terraform - Kafka
cd Kafka
terraform destroy -auto-approve
cd ..

# Terraform - Kong
cd KongTerraform
terraform destroy -auto-approve
cd ..

# # #Terraform - Konga
# cd KongaTerraform
# terraform destroy -auto-approve
# cd ..