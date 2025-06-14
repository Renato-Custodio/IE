#!/bin/bash

source ./access.sh

# Terraform - RDS
cd RDS-Terraform
terraform init
terraform apply -auto-approve
esc=$'\e'
addressDB="$(terraform state show aws_db_instance.example |grep address | sed "s/address//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
cd ..

# Terraform - Kafka
cd Kafka
terraform init
terraform apply -auto-approve
esc=$'\e'
addresskafka="$(terraform state show 'aws_instance.exampleCluster[0]'|grep public_dns | sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
cd ..


# Terraform Customer
cd microservices/customer/src/main/resources
sed -i "/quarkus.datasource.reactive.url/d" application.properties
sed -i "/quarkus.container-image.group/d" application.properties
echo "quarkus.container-image.group=$DockerUsername" >> application.properties
echo "quarkus.datasource.reactive.url=mysql://$addressDB:3306/quarkus_test_all_operations" >> application.properties
cd ../../..
DockerImage="$(grep -m 1 "<artifactId>" pom.xml|sed "s/<artifactId>//g"|sed "s/<\/artifactId>//g" |sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g")"
DockerImageVersion="$(grep -m 1 "<version>" pom.xml|sed "s/<version>//g"|sed "s/<\/version>//g" |sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g")"
echo "Starting the clean package"
./mvnw clean package
echo "Ending the clean package"
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

# Terraform - Purchase
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

# Terraform LoyaltyCard
cd microservices/loyalty-card/src/main/resources
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

# Terraform - DiscountCoupon
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

# Terraform - Kong
cd KongTerraform
terraform init
terraform taint aws_instance.exampleInstallKong
terraform apply -auto-approve
cd ..

# Terraform - Camunda
cd Camunda-Terraform
terraform init
terraform taint aws_instance.exampleInstallCamundaEngine
terraform apply -auto-approve
cd ..

#echo Customer
cd Quarkus-Terraform/customer
#terraform state show 'aws_instance.exampleDeployQuarkus' |grep public_dns
echo "MICROSERVICE customer IS AVAILABLE HERE:"
addressMS="$(terraform state show aws_instance.exampleDeployQuarkus |grep public_dns | sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
echo "http://"$addressMS":8080/q/swagger-ui/"
echo
cd ../..

#echo Purchase
cd Quarkus-Terraform/purchase
#terraform state show 'aws_instance.exampleDeployQuarkus' |grep public_dns
echo "MICROSERVICE purchase IS AVAILABLE HERE:"
addressMS="$(terraform state show aws_instance.exampleDeployQuarkus |grep public_dns | sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
echo "http://"$addressMS":8080/q/swagger-ui/"
echo
cd ../..

# echo LoyaltyCard
cd Quarkus-Terraform/loyalty-card
#terraform state show 'aws_instance.exampleDeployQuarkus' |grep public_dns
echo "MICROSERVICE loyalty-card IS AVAILABLE HERE:"
addressMS="$(terraform state show aws_instance.exampleDeployQuarkus |grep public_dns | sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
echo "http://"$addressMS":8080/q/swagger-ui/"
echo
cd ../..

#echo DiscountCoupon
cd Quarkus-Terraform/discount-coupon
#terraform state show 'aws_instance.exampleDeployQuarkus' |grep public_dns
echo "MICROSERVICE discount-coupon IS AVAILABLE HERE:"
addressMS="$(terraform state show aws_instance.exampleDeployQuarkus |grep public_dns | sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
echo "http://"$addressMS":8080/q/swagger-ui/"
echo
cd ../..

# echo Kafka
cd Kafka
echo "KAFKA IS AVAILABLE HERE:"
echo ""$addresskafka""
echo
cd ..

# Echo Kong
cd KongTerraform
terraform state show aws_instance.exampleInstallKong |grep public_dns
echo "KONG IS AVAILABLE HERE:"
addressKong="$(terraform state show aws_instance.exampleInstallKong |grep public_dns| sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
echo "http://"$addressKong":8000/"
echo
cd ..

# Echo CAMUNDA
cd Camunda-Terraform
terraform state show aws_instance.exampleInstallCamundaEngine |grep public_dns
echo "CAMUNDA IS AVAILABLE HERE:"
addressCamunda="$(terraform state show aws_instance.exampleInstallCamundaEngine |grep public_dns| sed "s/public_dns//g" | sed "s/=//g" | sed "s/\"//g" |sed "s/ //g" | sed "s/$esc\[[0-9;]*m//g" )"
echo "http://"$addressCamunda":8080/camunda"
echo
cd ..

#echo RDS
cd RDS-Terraform
echo "RDS IS AVAILABLE HERE:"
terraform state show aws_db_instance.example |grep address
terraform state show aws_db_instance.example |grep port
echo
cd ..

