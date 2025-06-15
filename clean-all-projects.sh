find . -name ".terraform" -exec rm -r .terraform '{}' \;

cd microservices/purchase	
mvn clean
cd ../..

cd microservices/customer	
mvn clean
cd ../..

cd microservices/loyalty-card	
mvn clean
cd ../..

cd microservices/shop
mvn clean
cd ../..

cd microservices/cross-selling-recommendation
mvn clean
cd ../..

cd microservices/discount-coupon
mvn clean
cd ../..

cd microservices/selled-product-analytics
mvn clean
cd ../..