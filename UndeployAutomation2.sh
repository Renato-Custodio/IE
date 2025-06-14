#!/bin/bash

source ./access.sh
source ./kongVars.sh

# Terraform - ollama
cd Quarkus-Terraform/ollama
terraform destroy -auto-approve
cd ../..

# Terraform - Camunda
cd Camunda-Terraform
terraform destroy -auto-approve
cd ..

# Terraform - Kong
cd KongTerraform
terraform destroy -auto-approve
cd ..

sed -i "/^ip_customer=/c ip_customer=\"\"" kongVars.sh
sed -i "/^ip_purchase=/c ip_purchase=\"\"" kongVars.sh
sed -i "/^ip_shop=/c ip_shop=\"\"" kongVars.sh
sed -i "/^ip_loyalty_card=/c ip_loyalty_card=\"\"" kongVars.sh
sed -i "/^ip_cross_selling_recommendation=/c ip_cross_selling_recommendation=\"\"" kongVars.sh
sed -i "/^ip_discount_coupon=/c ip_discount_coupon=\"\"" kongVars.sh
sed -i "/^ip_selled_product_analytics=/c ip_selled_product_analytics=\"\"" kongVars.sh
sed -i "/^ip_discount_analysis_ai=/c ip_discount_analysis_ai=\"\"" kongVars.sh

echo Finished