package org.acme;

import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Tuple;
import org.acme.model.Message;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.*;

import jakarta.inject.Inject;

public class StaticTopicProducer extends Thread {
    private static final String TOPIC_NAME = "SelledProductAnalytics";

    private String kafkaServers;

    private SelledProductAnalytics SelledProductAnalytics;

    @Inject
    io.vertx.mutiny.mysqlclient.MySQLPool client;

    public StaticTopicProducer(
            final String kafkaServersReceived,
            final MySQLPool clientReceived,
            final SelledProductAnalytics SelledProductAnalytics) {
        kafkaServers = kafkaServersReceived;
        client = clientReceived;
        this.SelledProductAnalytics = SelledProductAnalytics;
    }

    public void run() {
        try {
            Properties properties = new Properties();
            properties.put("bootstrap.servers", kafkaServers);
            properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            properties.put("group.id", "your-group-id");

            KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

            // Producing message to kafka topic
            final Message message = createMessage(
                    SelledProductAnalytics.idCustomer,
                    SelledProductAnalytics.idShop, SelledProductAnalytics.idPurchase,
                    SelledProductAnalytics.idLoyaltyCard, SelledProductAnalytics.idCoupon,
                    SelledProductAnalytics.location);
            this.sendMessage(message, producer);

            // Storing entity in db
            this.persistSelledProductAnalytics(
                    SelledProductAnalytics.idCustomer,
                    SelledProductAnalytics.idShop, SelledProductAnalytics.idPurchase,
                    SelledProductAnalytics.idLoyaltyCard, SelledProductAnalytics.idCoupon,
                    SelledProductAnalytics.location);
        } catch (Exception e) {
            System.out.println("Exception is caught");
        }
    }

    private Message createMessage(
            Long idCustomer, Long idShop, Long idPurchase, Long idLoyaltyCard,
            Long idCoupon, String location) {
        final Message message = new Message();
        message.setIdCustomer(idCustomer);
        message.setIdShop(idShop);
        message.setIdPurchase(idPurchase);
        message.setIdLoyaltyCard(idLoyaltyCard);
        message.setIdCoupon(idCoupon);
        message.setLocation(location);
        message.setSeqKey(TOPIC_NAME + "_" + String.valueOf(((Double) (Math.random() * 10)).intValue()));
        message.setAsText(
                "{\"SelledProductAnalytics\":{" +
                        "\"loyaltyCardId\":\"" + idCustomer + "\"," +
                        "\"shopIds\":\"" + idShop + "\"" +
                        "}}");
        return message;
    }

    private void sendMessage(Message msg, KafkaProducer<String, String> producer) {
        System.out.println("This is the message to send = " + msg.getAsText());
        String seqKey = msg.getSeqKey();
        System.out.println("Sending new message to Kafka, to the topic = " + TOPIC_NAME + ", with key=" + seqKey);
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, seqKey, msg.getAsText());
        producer.send(record);
        System.out.println("Sent...");
    }

    private void persistSelledProductAnalytics(
            Long idCustomer, Long idShop, Long idPurchase, Long idLoyaltyCard,
            Long idCoupon, String location) {
        final String shopIdsStr = location;
        client.preparedQuery("INSERT INTO DiscountCoupons(loyalty_card_id,shop_ids) VALUES (?,?)")
                .execute(Tuple.of(idCustomer, shopIdsStr)).await().indefinitely();
    }
}
