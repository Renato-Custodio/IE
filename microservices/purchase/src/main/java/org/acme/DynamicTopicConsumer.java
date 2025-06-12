package org.acme;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import org.json.*;

import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DynamicTopicConsumer extends Thread  {

    private final String kafkaServers;
    private final String topic;

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicTopicConsumer.class);

     @Inject
    io.vertx.mutiny.mysqlclient.MySQLPool client;
    

    public DynamicTopicConsumer(String topic, String kafkaServers, io.vertx.mutiny.mysqlclient.MySQLPool client)
    {
        this.topic = topic;
        this.kafkaServers = kafkaServers;
        this.client = client;
    }

    public void run() 
	{
	    try 
		{
            Properties properties = new Properties();
            properties.put("bootstrap.servers", kafkaServers);
            properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            properties.put("group.id", "your-group-id");
    
            try (Consumer<String, String> consumer = new KafkaConsumer<>(properties)) {
                consumer.subscribe(Collections.singletonList(topic));
    
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, String> record : records) {
                        LOGGER.info("Consuming record: {}", record);

                        String jsonString = record.value() ;
                        JSONObject obj = new JSONObject(jsonString);
                        String timestamp = obj.getJSONObject("Purchase_Event").getString("TimeStamp");
                        String price = obj.getJSONObject("Purchase_Event").getString("Price");
                        String product = obj.getJSONObject("Purchase_Event").getString("Product");
                        String supplier = obj.getJSONObject("Purchase_Event").getString("Supplier");
                        String shopName = obj.getJSONObject("Purchase_Event").getString("Shop");
                        String loyaltyCardId = obj.getJSONObject("Purchase_Event").getString("LoyaltyCard_ID");
                        String discountCouponId = obj.getJSONObject("Purchase_Event").getString("DiscountCoupon_ID");
                        String query = "INSERT INTO Purchases (date_time,price,product,supplier,shop_name,loyalty_card_id,discount_coupon_id) VALUES ("
                            + "'" + timestamp + "',"
                            + "'" + price + "',"
                            + "'" + product + "',"
                            + "'" + supplier + "',"
                            + "'" + shopName + "',"
                            + "'" + loyaltyCardId + "',"
                            + discountCouponId
                            + ")";

                        client.query(query).execute().await().indefinitely();
                    }
                }
            }    
        }
        catch (Exception e) {
            LOGGER.warn("Exception caught in consumer: {}", e.getMessage());
        }
    }
}
