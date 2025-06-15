source ./access.sh

# Terraform - Quarkus purchase
cd Quarkus-Terraform/purchase
echo
echo Destrying purchase
echo
terraform destroy -auto-approve
cd ../..

# Terraform - Quarkus customer
cd Quarkus-Terraform/customer
echo
echo Destrying customer
echo
terraform destroy -auto-approve
cd ../..

# Terraform - Quarkus shop
cd Quarkus-Terraform/shop
echo
echo Destrying shop
echo
terraform destroy -auto-approve
cd ../..

# Terraform - Quarkus loyaltycard
cd Quarkus-Terraform/loyalty-card
echo
echo Destrying loyalty-card
echo
terraform destroy -auto-approve
cd ../..

# Terraform - Quarkus discountCoupon
cd Quarkus-Terraform/discount-coupon
echo
echo Destrying discount-coupon
echo
terraform destroy -auto-approve
cd ../..

# Terraform - Quarkus cross-selling-recommendation
cd Quarkus-Terraform/cross-selling-recommendation
echo
echo Destrying cross-selling-recommendation
echo
terraform destroy -auto-approve
cd ../..

# Terraform - Quarkus selled-product-analytics
cd Quarkus-Terraform/selled-product-analytics
echo
echo Destrying selled-product-analytics
echo
terraform destroy -auto-approve
cd ../..

# Terraform - Kafka
cd Kafka
echo
echo Destrying Kafka
echo
terraform destroy -auto-approve
cd ..

# Terraform - RDS
cd RDS-Terraform
echo
echo Destrying RDS
echo
terraform destroy -auto-approve
cd ..

echo Finished