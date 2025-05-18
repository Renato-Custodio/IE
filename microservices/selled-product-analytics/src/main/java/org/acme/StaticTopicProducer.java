package org.acme;

import io.vertx.mutiny.mysqlclient.MySQLPool;
import jakarta.inject.Inject;
import org.acme.api.ApiSelledProductAnalyticsRequest;
import org.acme.model.Message;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;


public class StaticTopicProducer extends Thread  {

    private static final String SELLED_PRODUCT_BY_COUPON_TOPIC_NAME = "SelledProductByCoupon";
    private static final String SELLED_PRODUCT_BY_CUSTOMER_TOPIC_NAME = "SelledProductByCustomer";
    private static final String SELLED_PRODUCT_BY_LOCATION_TOPIC_NAME = "SelledProductByLocation";
    private static final String SELLED_PRODUCT_BY_LOYALTY_CARD_TOPIC_NAME = "SelledProductByLoyaltyCard";
    private static final String SELLED_PRODUCT_BY_SHOP_TOPIC_NAME = "SelledProductByShop";

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticTopicProducer.class);

    private final String kafkaServers;
    private final ApiSelledProductAnalyticsRequest selledProductAnalytics;

    @Inject
    io.vertx.mutiny.mysqlclient.MySQLPool client;

    public StaticTopicProducer(
        final String kafkaServers,
        final MySQLPool client,
        final ApiSelledProductAnalyticsRequest selledProductAnalytics)
    {
        this.kafkaServers = kafkaServers;
        this.client = client;
        this.selledProductAnalytics = selledProductAnalytics;
    }

    public void run() 
	{
	    try 
		{
            Properties properties = new Properties();
            properties.put("bootstrap.servers", kafkaServers);
            properties.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
            properties.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
            properties.put("group.id", "your-group-id");

            KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

            //Producing message to kafka topic SelledProductByCoupon
            final Message couponMessage = createSelledProductByCouponMessage(
                selledProductAnalytics.idPurchase(),
                selledProductAnalytics.idCoupon());
            this.sendMessage(couponMessage, SELLED_PRODUCT_BY_COUPON_TOPIC_NAME, producer);

            //Producing message to kafka topic SelledProductByCustomer
            final Message customerMessage = createSelledProductByCustomerMessage(
                selledProductAnalytics.idPurchase(),
                selledProductAnalytics.idCustomer());
            this.sendMessage(customerMessage, SELLED_PRODUCT_BY_CUSTOMER_TOPIC_NAME, producer);

            //Producing message to kafka topic SelledProductByLocation
            final Message locationMessage = createSelledProductByLocationMessage(
                selledProductAnalytics.idPurchase(),
                selledProductAnalytics.location());
            this.sendMessage(locationMessage, SELLED_PRODUCT_BY_LOCATION_TOPIC_NAME, producer);

            //Producing message to kafka topic SelledProductByLoyaltyCard
            final Message loyaltyCardMessage = createSelledProductByLoyaltyCardMessage(
                selledProductAnalytics.idPurchase(),
                selledProductAnalytics.idLoyaltyCard());
            this.sendMessage(loyaltyCardMessage, SELLED_PRODUCT_BY_LOYALTY_CARD_TOPIC_NAME, producer);

            //Producing message to kafka topic SelledProductByShop
            final Message shopMessage = createSelledProductByShopMessage(
                selledProductAnalytics.idPurchase(),
                selledProductAnalytics.idShop());
            this.sendMessage(shopMessage, SELLED_PRODUCT_BY_SHOP_TOPIC_NAME, producer);
        }
        catch (Exception e) {
            LOGGER.warn("Exception caught in producer: {}", e.getMessage());
        }
    }

    private Message createSelledProductByCouponMessage(
        final Long idPurchase,
        final Long idCoupon)
    {
        final Message message = new Message();
        message.setIdPurchase(idPurchase);
        message.setIdCoupon(idCoupon);
        message.setSeqKey(SELLED_PRODUCT_BY_COUPON_TOPIC_NAME + "_" + String.valueOf(((Double) (Math.random() * 10)).intValue()));
        message.setAsText(
            "{\"SelledProductAnalytics\":{" +
                "\"idPurchase\":\"" + idPurchase + "\"," +
                "\"idCoupon\":\"" + idCoupon + "\"" +
            "}}"
        );
        return message;
    }

    private Message createSelledProductByCustomerMessage(
        final Long idPurchase,
        final Long idCustomer)
    {
        final Message message = new Message();
        message.setIdPurchase(idPurchase);
        message.setIdCustomer(idCustomer);
        message.setSeqKey(SELLED_PRODUCT_BY_CUSTOMER_TOPIC_NAME + "_" + String.valueOf(((Double) (Math.random() * 10)).intValue()));
        message.setAsText(
            "{\"SelledProductAnalytics\":{" +
                "\"idPurchase\":\"" + idPurchase + "\"," +
                "\"idCustomer\":\"" + idCustomer + "\"" +
                "}}"
        );
        return message;
    }

    private Message createSelledProductByLocationMessage(
        final Long idPurchase,
        final String location)
    {
        final Message message = new Message();
        message.setIdPurchase(idPurchase);
        message.setLocation(location);
        message.setSeqKey(SELLED_PRODUCT_BY_LOCATION_TOPIC_NAME + "_" + String.valueOf(((Double) (Math.random() * 10)).intValue()));
        message.setAsText(
            "{\"SelledProductAnalytics\":{" +
                "\"idPurchase\":\"" + idPurchase + "\"," +
                "\"location\":\"" + location + "\"" +
                "}}"
        );
        return message;
    }

    private Message createSelledProductByLoyaltyCardMessage(
        final Long idPurchase,
        final Long idLoyaltyCard)
    {
        final Message message = new Message();
        message.setIdPurchase(idPurchase);
        message.setIdLoyaltyCard(idLoyaltyCard);
        message.setSeqKey(SELLED_PRODUCT_BY_LOYALTY_CARD_TOPIC_NAME + "_" + String.valueOf(((Double) (Math.random() * 10)).intValue()));
        message.setAsText(
            "{\"SelledProductAnalytics\":{" +
                "\"idPurchase\":\"" + idPurchase + "\"," +
                "\"idLoyaltyCard\":\"" + idLoyaltyCard + "\"" +
                "}}"
        );
        return message;
    }

    private Message createSelledProductByShopMessage(
        final Long idPurchase,
        final Long idShop)
    {
        final Message message = new Message();
        message.setIdPurchase(idPurchase);
        message.setIdShop(idShop);
        message.setSeqKey(SELLED_PRODUCT_BY_SHOP_TOPIC_NAME + "_" + String.valueOf(((Double) (Math.random() * 10)).intValue()));
        message.setAsText(
            "{\"SelledProductAnalytics\":{" +
                "\"idPurchase\":\"" + idPurchase + "\"," +
                "\"idShop\":\"" + idShop + "\"" +
                "}}"
        );
        return message;
    }

    private void sendMessage(
        final Message msg,
        final String topicName,
        final KafkaProducer<String,String> producer)
    {
        LOGGER.info("Sending message {} to topic {}", msg.getAsText(), topicName);
        String seqKey = msg.getSeqKey();
        ProducerRecord<String, String> record = new ProducerRecord<>(topicName, seqKey, msg.getAsText());
        producer.send(record);
        LOGGER.info("Sent message with key {}", seqKey);
    }
}
