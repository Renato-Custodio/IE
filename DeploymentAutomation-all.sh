#!/bin/bash

source ./access.sh

# Terraform - RDS
cd RDS-Terraform
terraform init
terraform apply -auto-approve
esc=$'\e'
addressDB="$(terraform state show aws_db_instance.example |grep address | sed "s/address//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
cd ..

# Terraform - Camunda
cd Camunda-Terraform
terraform init
terraform apply -auto-approve
cd ..


# Terraform - Kafka
cd Kafka
terraform init
terraform apply -auto-approve
esc=$'\e'
addresskafka="$(terraform state show 'aws_instance.exampleCluster[0]'|grep public_dns | sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
cd ..

DockerUsername="$(echo "$DockerUsername" | tr -d '\n\r')"
DockerImage="$(echo "$DockerImage" | tr -d '\n\r')"
DockerImageVersion="$(echo "$DockerImageVersion" | tr -d '\n\r')"

# Terraform - Quarkus Micro services changing the configuration of the DB connection, recompiling and packaging
cd microservices/purchase/src/main/resources
sed -i "/quarkus.datasource.reactive.url/d" application.properties
sed -i "/quarkus.container-image.group/d" application.properties
sed -i "/kafka.bootstrap.servers/d" application.properties
echo "quarkus.container-image.group=$DockerUsername" >> application.properties                                        
echo "quarkus.datasource.reactive.url=mysql://$addressDB:3306/quarkus_test_all_operations" >> application.properties                                        
echo "kafka.bootstrap.servers=$addresskafka:9092" >> application.properties
cd ../../..
DockerImage="$(grep -m 1 "<artifactId>" pom.xml|sed "s/<artifactId>//g"|sed "s/<\/artifactId>//g" |sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g")"
DockerImageVersion="$(grep -m 1 "<version>" pom.xml|sed "s/<version>//g"|sed "s/<\/version>//g" |sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g")"
./mvnw clean package
cd ../..

cd Quarkus-Terraform/purchase
sed -i "/sudo docker login/d" quarkus.sh
sed -i "/sudo docker pull/d" quarkus.sh
sed -i "/sudo docker run/d" quarkus.sh
echo "sudo docker login -u \"$DockerUsername\" -p \"$DockerPassword\"" >> quarkus.sh
echo "sudo docker pull $DockerUsername/$DockerImage:$DockerImageVersion" >> quarkus.sh
echo "sudo docker run -d --name $DockerImage -p 8080:8080 $DockerUsername/$DockerImage:$DockerImageVersion" >> quarkus.sh
terraform init
terraform taint aws_instance.exampleDeployQuarkus
terraform apply -auto-approve
cd ../..

# Terraform - Quarkus Micro services changing the configuration of the DB connection, recompiling and packaging
cd microservices/loyalty-card/src/main/resources
sed -i "/quarkus.datasource.reactive.url/d" application.properties
sed -i "/quarkus.container-image.group/d" application.properties
echo "quarkus.container-image.group=$DockerUsername" >> application.properties                                        
echo "quarkus.datasource.reactive.url=mysql://$addressDB:3306/quarkus_test_all_operations" >> application.properties                                        
cd ../../..
DockerImage="$(grep -m 1 "<artifactId>" pom.xml|sed "s/<artifactId>//g"|sed "s/<\/artifactId>//g" |sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g")"
DockerImageVersion="$(grep -m 1 "<version>" pom.xml|sed "s/<version>//g"|sed "s/<\/version>//g" |sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g")"
./mvnw clean package
cd ../..

cd Quarkus-Terraform/loyalty-card
sed -i "/sudo docker login/d" quarkus.sh
sed -i "/sudo docker pull/d" quarkus.sh
sed -i "/sudo docker run/d" quarkus.sh
echo "sudo docker login -u \"$DockerUsername\" -p \"$DockerPassword\"" >> quarkus.sh
echo "sudo docker pull $DockerUsername/$DockerImage:$DockerImageVersion" >> quarkus.sh
echo "sudo docker run -d --name $DockerImage -p 8080:8080 $DockerUsername/$DockerImage:$DockerImageVersion" >> quarkus.sh
terraform init
terraform taint aws_instance.exampleDeployQuarkus
terraform apply -auto-approve
cd ../..

# Terraform - Quarkus Micro services changing the configuration of the DB connection, recompiling and packaging
cd microservices/customer/src/main/resources
sed -i "/quarkus.datasource.reactive.url/d" application.properties
sed -i "/quarkus.container-image.group/d" application.properties
echo "quarkus.container-image.group=$DockerUsername" >> application.properties                                        
echo "quarkus.datasource.reactive.url=mysql://$addressDB:3306/quarkus_test_all_operations" >> application.properties                                        
cd ../../..
DockerImage="$(grep -m 1 "<artifactId>" pom.xml|sed "s/<artifactId>//g"|sed "s/<\/artifactId>//g" |sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g")"
DockerImageVersion="$(grep -m 1 "<version>" pom.xml|sed "s/<version>//g"|sed "s/<\/version>//g" |sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g")"
./mvnw clean package
cd ../..

cd Quarkus-Terraform/customer
sed -i "/sudo docker login/d" quarkus.sh
sed -i "/sudo docker pull/d" quarkus.sh
sed -i "/sudo docker run/d" quarkus.sh
echo "sudo docker login -u \"$DockerUsername\" -p \"$DockerPassword\"" >> quarkus.sh
echo "sudo docker pull $DockerUsername/$DockerImage:$DockerImageVersion" >> quarkus.sh
echo "sudo docker run -d --name $DockerImage -p 8080:8080 $DockerUsername/$DockerImage:$DockerImageVersion" >> quarkus.sh
terraform init
terraform taint aws_instance.exampleDeployQuarkus
terraform apply -auto-approve
cd ../..

# Terraform - Quarkus Micro services changing the configuration of the DB connection, recompiling and packaging
cd microservices/shop/src/main/resources
sed -i "/quarkus.datasource.reactive.url/d" application.properties
sed -i "/quarkus.container-image.group/d" application.properties
echo "quarkus.container-image.group=$DockerUsername" >> application.properties                                        
echo "quarkus.datasource.reactive.url=mysql://$addressDB:3306/quarkus_test_all_operations" >> application.properties                                        
cd ../../..
DockerImage="$(grep -m 1 "<artifactId>" pom.xml|sed "s/<artifactId>//g"|sed "s/<\/artifactId>//g" |sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g")"
DockerImageVersion="$(grep -m 1 "<version>" pom.xml|sed "s/<version>//g"|sed "s/<\/version>//g" |sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g")"
./mvnw clean package
cd ../..

cd Quarkus-Terraform/shop
sed -i "/sudo docker login/d" quarkus.sh
sed -i "/sudo docker pull/d" quarkus.sh
sed -i "/sudo docker run/d" quarkus.sh
echo "sudo docker login -u \"$DockerUsername\" -p \"$DockerPassword\"" >> quarkus.sh
echo "sudo docker pull $DockerUsername/$DockerImage:$DockerImageVersion" >> quarkus.sh
echo "sudo docker run -d --name $DockerImage -p 8080:8080 $DockerUsername/$DockerImage:$DockerImageVersion" >> quarkus.sh
terraform init
terraform taint aws_instance.exampleDeployQuarkus
terraform apply -auto-approve
cd ../..

# Terraform - Quarkus Micro services changing the configuration of the DB connection, recompiling and packaging
cd microservices/cross-selling-recommendation/src/main/resources
sed -i "/quarkus.datasource.reactive.url/d" application.properties
sed -i "/quarkus.container-image.group/d" application.properties
sed -i "/kafka.bootstrap.servers/d" application.properties
echo "quarkus.container-image.group=$DockerUsername" >> application.properties
echo "quarkus.datasource.reactive.url=mysql://$addressDB:3306/quarkus_test_all_operations" >> application.properties
echo "kafka.bootstrap.servers=$addresskafka:9092" >> application.properties
cd ../../..
DockerImage="$(grep -m 1 "<artifactId>" pom.xml|sed "s/<artifactId>//g"|sed "s/<\/artifactId>//g" |sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g")"
DockerImageVersion="$(grep -m 1 "<version>" pom.xml|sed "s/<version>//g"|sed "s/<\/version>//g" |sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g")"
./mvnw clean package
cd ../..

cd Quarkus-Terraform/cross-selling-recommendation
sed -i "/sudo docker login/d" quarkus.sh
sed -i "/sudo docker pull/d" quarkus.sh
sed -i "/sudo docker run/d" quarkus.sh
echo "sudo docker login -u \"$DockerUsername\" -p \"$DockerPassword\"" >> quarkus.sh
echo "sudo docker pull $DockerUsername/$DockerImage:$DockerImageVersion" >> quarkus.sh
echo "sudo docker run -d --name $DockerImage -p 8080:8080 $DockerUsername/$DockerImage:$DockerImageVersion" >> quarkus.sh
terraform init
terraform taint aws_instance.exampleDeployQuarkus
terraform apply -auto-approve
cd ../..

# Terraform - Quarkus Micro services changing the configuration of the DB connection, recompiling and packaging
cd microservices/discount-coupon/src/main/resources
sed -i "/quarkus.datasource.reactive.url/d" application.properties
sed -i "/quarkus.container-image.group/d" application.properties
sed -i "/kafka.bootstrap.servers/d" application.properties
echo "quarkus.container-image.group=$DockerUsername" >> application.properties
echo "quarkus.datasource.reactive.url=mysql://$addressDB:3306/quarkus_test_all_operations" >> application.properties
echo "kafka.bootstrap.servers=$addresskafka:9092" >> application.properties
cd ../../..
DockerImage="$(grep -m 1 "<artifactId>" pom.xml|sed "s/<artifactId>//g"|sed "s/<\/artifactId>//g" |sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g")"
DockerImageVersion="$(grep -m 1 "<version>" pom.xml|sed "s/<version>//g"|sed "s/<\/version>//g" |sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g")"
./mvnw clean package
cd ../..

cd Quarkus-Terraform/discount-coupon
sed -i "/sudo docker login/d" quarkus.sh
sed -i "/sudo docker pull/d" quarkus.sh
sed -i "/sudo docker run/d" quarkus.sh
echo "sudo docker login -u \"$DockerUsername\" -p \"$DockerPassword\"" >> quarkus.sh
echo "sudo docker pull $DockerUsername/$DockerImage:$DockerImageVersion" >> quarkus.sh
echo "sudo docker run -d --name $DockerImage -p 8080:8080 $DockerUsername/$DockerImage:$DockerImageVersion" >> quarkus.sh
terraform init
terraform taint aws_instance.exampleDeployQuarkus
terraform apply -auto-approve
cd ../..

# Terraform - Quarkus Micro services changing the configuration of the DB connection, recompiling and packaging
cd microservices/selled-product-analytics/src/main/resources
sed -i "/quarkus.datasource.reactive.url/d" application.properties
sed -i "/quarkus.container-image.group/d" application.properties
sed -i "/kafka.bootstrap.servers/d" application.properties
echo "quarkus.container-image.group=$DockerUsername" >> application.properties
echo "quarkus.datasource.reactive.url=mysql://$addressDB:3306/quarkus_test_all_operations" >> application.properties
echo "kafka.bootstrap.servers=$addresskafka:9092" >> application.properties
cd ../../..
DockerImage="$(grep -m 1 "<artifactId>" pom.xml|sed "s/<artifactId>//g"|sed "s/<\/artifactId>//g" |sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g")"
DockerImageVersion="$(grep -m 1 "<version>" pom.xml|sed "s/<version>//g"|sed "s/<\/version>//g" |sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g")"
./mvnw clean package
cd ../..

cd Quarkus-Terraform/selled-product-analytics
sed -i "/sudo docker login/d" quarkus.sh
sed -i "/sudo docker pull/d" quarkus.sh
sed -i "/sudo docker run/d" quarkus.sh
echo "sudo docker login -u \"$DockerUsername\" -p \"$DockerPassword\"" >> quarkus.sh
echo "sudo docker pull $DockerUsername/$DockerImage:$DockerImageVersion" >> quarkus.sh
echo "sudo docker run -d --name $DockerImage -p 8080:8080 $DockerUsername/$DockerImage:$DockerImageVersion" >> quarkus.sh
terraform init
terraform taint aws_instance.exampleDeployQuarkus
terraform apply -auto-approve
cd ../..

cd Quarkus-Terraform/ollama
terraform init
terraform taint aws_instance.exampleOllamaConfiguration
terraform apply -auto-approve
cd ../..

# Showing all the PUBLIC_DNSs
echo CAMUNDA - 
cd Camunda-Terraform
terraform state show aws_instance.exampleInstallCamundaEngine |grep public_dns
echo "CAMUNDA IS AVAILABLE HERE:"
addressCamunda="$(terraform state show aws_instance.exampleInstallCamundaEngine |grep public_dns| sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
echo "http://"$addressCamunda":8080/camunda"
echo
cd ..

cd Kafka
echo "KAFKA IS AVAILABLE HERE:"
echo ""$addresskafka""
echo
cd ..


# echo Quarkus - 
cd Quarkus-Terraform/purchase
# terraform state show 'aws_instance.exampleDeployQuarkus' |grep public_dns
echo "MICROSERVICE purchase IS AVAILABLE HERE:"
addressMS="$(terraform state show aws_instance.exampleDeployQuarkus |grep public_dns | sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
# set ip for kong
sed -i "/^ip_purchase=/c ip_purchase=\"$addressMS\"" ../../kongVars.sh
echo "http://"$addressMS":8080/q/swagger-ui/"
echo
cd ../..

# echo Quarkus - 
cd Quarkus-Terraform/customer
# terraform state show 'aws_instance.exampleDeployQuarkus' |grep public_dns
echo "MICROSERVICE customer IS AVAILABLE HERE:"
addressMS="$(terraform state show aws_instance.exampleDeployQuarkus |grep public_dns | sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
# set ip for kong
sed -i "/^ip_customer=/c ip_customer=\"$addressMS\"" ../../kongVars.sh
echo "http://"$addressMS":8080/q/swagger-ui/"
echo
cd ../..

# echo Quarkus - 
cd Quarkus-Terraform/shop
# terraform state show 'aws_instance.exampleDeployQuarkus' |grep public_dns
echo "MICROSERVICE shop IS AVAILABLE HERE:"
addressMS="$(terraform state show aws_instance.exampleDeployQuarkus |grep public_dns | sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
# set ip for kong
sed -i "/^ip_shop=/c ip_shop=\"$addressMS\"" ../../kongVars.sh
echo "http://"$addressMS":8080/q/swagger-ui/"
echo
cd ../..

# echo Quarkus - 
cd Quarkus-Terraform/loyalty-card
# terraform state show 'aws_instance.exampleDeployQuarkus' |grep public_dns
echo "MICROSERVICE loyalty-card IS AVAILABLE HERE:"
addressMS="$(terraform state show aws_instance.exampleDeployQuarkus |grep public_dns | sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
# set ip for kong
sed -i "/^ip_loyalty_card=/c ip_loyalty_card=\"$addressMS\"" ../../kongVars.sh
echo "http://"$addressMS":8080/q/swagger-ui/"
echo
cd ../..

# echo Quarkus -
cd Quarkus-Terraform/cross-selling-recommendation
# terraform state show 'aws_instance.exampleDeployQuarkus' |grep public_dns
echo "MICROSERVICE cross-selling-recommendation IS AVAILABLE HERE:"
addressMS="$(terraform state show aws_instance.exampleDeployQuarkus |grep public_dns | sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
# set ip for kong
sed -i "/^ip_cross_selling_recommendation=/c ip_cross_selling_recommendation=\"$addressMS\"" ../../kongVars.sh
echo "http://"$addressMS":8080/q/swagger-ui/"
echo
cd ../..

# echo Quarkus -
cd Quarkus-Terraform/discount-coupon
# terraform state show 'aws_instance.exampleDeployQuarkus' |grep public_dns
echo "MICROSERVICE discount-coupon IS AVAILABLE HERE:"
addressMS="$(terraform state show aws_instance.exampleDeployQuarkus |grep public_dns | sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
# set ip for kong
sed -i "/^ip_discount_coupon=/c ip_discount_coupon=\"$addressMS\"" ../../kongVars.sh
echo "http://"$addressMS":8080/q/swagger-ui/"
echo
cd ../..

# echo Quarkus -
cd Quarkus-Terraform/selled-product-analytics
# terraform state show 'aws_instance.exampleDeployQuarkus' |grep public_dns
echo "MICROSERVICE selled-product-analytics IS AVAILABLE HERE:"
addressMS="$(terraform state show aws_instance.exampleDeployQuarkus |grep public_dns | sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
# set ip for kong
sed -i "/^ip_selled_product_analytics=/c ip_selled_product_analytics=\"$addressMS\"" ../../kongVars.sh
echo "http://"$addressMS":8080/q/swagger-ui/"
echo
cd ../..

# echo Quarkus -
cd Quarkus-Terraform/ollama
# terraform state show 'aws_instance.exampleDeployQuarkus' |grep public_dns
echo "MICROSERVICE ollama IS AVAILABLE HERE:"
addressMS="$(terraform state show aws_instance.exampleDeployQuarkus |grep public_dns | sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
# set ip for kong
sed -i "/^ip_discount_analysis_ai=/c ip_discount_analysis_ai=\"$addressMS\"" ../../kongVars.sh
echo "http://"$addressMS":8080/q/swagger-ui/"
echo
cd ../..


# Terraform 1 - Kong
cd KongTerraform
terraform init
terraform apply -auto-approve
cd ..

cd RDS-Terraform
echo "RDS IS AVAILABLE HERE:"
terraform state show aws_db_instance.example |grep address
terraform state show aws_db_instance.example |grep port
echo
cd ..

echo "KONG IS AVAILABLE HERE:" 
cd KongTerraform
addressKong="$(terraform state show aws_instance.exampleInstallKong |grep public_dns | sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
echo "http://"$addressKong":8000/"
echo
cd ..