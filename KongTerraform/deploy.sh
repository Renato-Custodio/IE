#!/bin/bash
echo "Starting..."

sudo yum install -y docker

sudo service docker start

sudo docker network create kong-net

sudo docker run -d --name kong-database \
  --network=kong-net \
  -p 5432:5432 \
  -e "POSTGRES_USER=kong" \
  -e "POSTGRES_DB=kong" \
  -e "POSTGRES_PASSWORD=kongpass" \
  postgres:13

sudo docker run --rm --network=kong-net \
-e "KONG_DATABASE=postgres" \
-e "KONG_PG_HOST=kong-database" \
-e "KONG_PG_PASSWORD=kongpass" \
-e "KONG_PASSWORD=test" \
kong/kong-gateway:3.9.0.0 kong migrations bootstrap

sudo docker run -d --name kong-gateway \
--network=kong-net \
-e "KONG_DATABASE=postgres" \
-e "KONG_PG_HOST=kong-database" \
-e "KONG_PG_USER=kong" \
-e "KONG_PG_PASSWORD=kongpass" \
-e "KONG_PROXY_ACCESS_LOG=/dev/stdout" \
-e "KONG_ADMIN_ACCESS_LOG=/dev/stdout" \
-e "KONG_PROXY_ERROR_LOG=/dev/stderr" \
-e "KONG_ADMIN_ERROR_LOG=/dev/stderr" \
-e "KONG_ADMIN_LISTEN=0.0.0.0:8001, 0.0.0.0:8444 ssl" \
-e "KONG_ADMIN_GUI_URL=http://localhost:8002" \
-e KONG_LICENSE_DATA \
-p 8000:8000 \
-p 8443:8443 \
-p 8001:8001 \
-p 8002:8002 \
-p 8445:8445 \
-p 8003:8003 \
-p 8004:8004 \
-p 127.0.0.1:8444:8444 \
kong/kong-gateway:3.9.0.0

echo "Customer IP: ${ip_customer}"
echo "Purchase IP: ${ip_purchase}"
echo "Shop IP: ${ip_shop}"
echo "Loyalty Card IP: ${ip_loyalty_card}"
echo "Cross Selling Recommendation IP: ${ip_cross_selling_recommendation}"
echo "Discount Coupon IP: ${ip_discount_coupon}"
echo "Selled Product Analytics IP: ${ip_selled_product_analytics}"
echo "Discount Analysis AI IP: ${ip_discount_analysis_ai}"


until curl -s http://localhost:8001 > /dev/null; do
  echo "Waiting for Kong to be ready..."
  sleep 5
done


# check ip_customer
if [ -n "${ip_customer}" ]; then
  # To create a Service to redirect to a microservice
  curl -i -X POST --url http://localhost:8001/services/ \
    --data "name=customer-service" \
    --data "url=http://${ip_customer}:8080/Customer"

  # To create a Route to that Service
  curl -i -X POST http://localhost:8001/services/customer-service/routes --data 'name=customer-route' --data 'paths[]=/customer'
else
  echo "Variable ip_customer is empty"
fi


# check ip_purchase
if [ -n "${ip_purchase}" ]; then
  # To create a Service to redirect to a microservice
  curl -i -X POST --url http://localhost:8001/services/ \
    --data "name=purchase-service" \
    --data "url=http://${ip_purchase}:8080/Purchase"

  # To create a Route to that Service
  curl -i -X POST http://localhost:8001/services/purchase-service/routes --data 'name=purchase-route' --data 'paths[]=/purchase'
else
  echo "Variable ip_purchase is empty"
fi


# check ip_shop
if [ -n "${ip_shop}" ]; then
  # To create a Service to redirect to a microservice
  curl -i -X POST --url http://localhost:8001/services/ \
    --data "name=shop-service" \
    --data "url=http://${ip_shop}:8080/Shop"

  # To create a Route to that Service
  curl -i -X POST http://localhost:8001/services/shop-service/routes --data 'name=shop-route' --data 'paths[]=/shop'
else
  echo "Variable ip_shop is empty"
fi


# check ip_loyalty_card
if [ -n "${ip_loyalty_card}" ]; then
  # To create a Service to redirect to a microservice
  curl -i -X POST --url http://localhost:8001/services/ \
    --data "name=loyaltyCard-service" \
    --data "url=http://${ip_loyalty_card}:8080/LoyaltyCard"

  # To create a Route to that Service
  curl -i -X POST http://localhost:8001/services/loyaltyCard-service/routes --data 'name=loyaltyCard-route' --data 'paths[]=/loyaltyCard'
else
  echo "Variable ip_loyalty_card is empty"
fi


# check ip_cross_selling_recommendation
if [ -n "${ip_cross_selling_recommendation}" ]; then
  # To create a Service to redirect to a microservice
  curl -i -X POST --url http://localhost:8001/services/ \
    --data "name=crossSellingRecommendation-service" \
    --data "url=http://${ip_cross_selling_recommendation}:8080/CrossSellingRecommendation"

  # To create a Route to that Service
  curl -i -X POST http://localhost:8001/services/crossSellingRecommendation-service/routes --data 'name=crossSellingRecommendation-route' --data 'paths[]=/crossSellingRecommendation'
else
  echo "Variable ip_cross_selling_recommendation is empty"
fi


# check ip_discount_coupon
if [ -n "${ip_discount_coupon}" ]; then
  # To create a Service to redirect to a microservice
  curl -i -X POST --url http://localhost:8001/services/ \
    --data "name=discountCoupon-service" \
    --data "url=http://${ip_discount_coupon}:8080/DiscountCoupon"

  # To create a Route to that Service
  curl -i -X POST http://localhost:8001/services/discountCoupon-service/routes --data 'name=discountCoupon-route' --data 'paths[]=/discountCoupon'
else
  echo "Variable ip_discount_coupon is empty"
fi


# check ip_selled_product_analytics
if [ -n "${ip_selled_product_analytics}" ]; then
  # To create a Service to redirect to a microservice
  curl -i -X POST --url http://localhost:8001/services/ \
    --data "name=selledProductAnalytics-service" \
    --data "url=http://${ip_selled_product_analytics}:8080/SelledProductAnalytics"

  # To create a Route to that Service
  curl -i -X POST http://localhost:8001/services/selledProductAnalytics-service/routes --data 'name=selledProductAnalytics-route' --data 'paths[]=/selledProductAnalytics'
else
  echo "Variable ip_selled_product_analytics is empty"
fi


# check ip_discount_analysis_ai
if [ -n "${ip_discount_analysis_ai}" ]; then
  # To create a Service to redirect to a microservice
  curl -i -X POST --url http://localhost:8001/services/ \
    --data "name=ollama-service" \
    --data "url=http://${ip_discount_analysis_ai}:11434/api/generate" \
    --data "connect_timeout=180000" \
    --data "read_timeout=180000" \
    --data "write_timeout=180000"

  # To create a Route to that Service
  curl -i -X POST http://localhost:8001/services/ollama-service/routes --data 'name=ollama-route' --data 'paths[]=/ollama'
else
  echo "Variable ollama is empty"
fi


echo "Finished."


