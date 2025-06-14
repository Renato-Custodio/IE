source ./access.sh

# Terraform - Quarkus purchase
cd Quarkus-Terraform/purchase
echo Destrying purchase
terraform destroy -auto-approve
cd ../..

# Terraform - Quarkus customer
cd Quarkus-Terraform/customer
echo Destrying customer
terraform destroy -auto-approve
cd ../..

# Terraform - Quarkus shop
cd Quarkus-Terraform/shop
echo Destrying shop
terraform destroy -auto-approve
cd ../..

# Terraform - Quarkus loyaltycard
cd Quarkus-Terraform/loyalty-card
echo Destrying loyalty-card
terraform destroy -auto-approve
cd ../..

# Terraform - Quarkus discountCoupon
cd Quarkus-Terraform/discount-coupon
echo Destrying discount-coupon
terraform destroy -auto-approve
cd ../..

# Terraform - Quarkus cross-selling-recommendation
cd Quarkus-Terraform/cross-selling-recommendation
echo Destrying cross-selling-recommendation
terraform destroy -auto-approve
cd ../..

# Terraform - Quarkus selled-product-analytics
cd Quarkus-Terraform/selled-product-analytics
echo Destrying selled-product-analytics
terraform destroy -auto-approve
cd ../..

# Terraform - Kafka
cd Kafka
echo Destrying Kafka
terraform destroy -auto-approve
cd ..

# Terraform - RDS
#cd RDS-Terraform
#echo Destrying RDS
#terraform destroy -auto-approve
#cd ..

echo Finished