find . -name ".terraform" -exec rm -r .terraform '{}' \;

cd microservices/Purchase	
mvn clean
cd ../..

cd microservices/customer	
mvn clean
cd ../..

cd microservices/loyaltycard	
mvn clean
cd ../..

cd microservices/shop
mvn clean
cd ../..
