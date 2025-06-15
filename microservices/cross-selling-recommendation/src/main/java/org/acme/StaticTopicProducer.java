package org.acme;

import io.vertx.mutiny.mysqlclient.MySQLPool;
import jakarta.inject.Inject;
import org.acme.api.ApiCrossSellingRecommendationRequest;
import org.acme.model.Message;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;


public class StaticTopicProducer extends Thread  {

    private static final String TOPIC_NAME = "CrossSellingRecommendation";
    private static final Logger LOGGER = LoggerFactory.getLogger(StaticTopicProducer.class);

    private final String kafkaServers;
    private final ApiCrossSellingRecommendationRequest crossSellingRecommendation;

    @Inject
    io.vertx.mutiny.mysqlclient.MySQLPool client;

    public StaticTopicProducer(
        final String kafkaServers,
        final MySQLPool client,
        final ApiCrossSellingRecommendationRequest crossSellingRecommendation)
    {
        this.kafkaServers = kafkaServers;
        this.client = client;
        this.crossSellingRecommendation = crossSellingRecommendation;
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

            //Producing message to kafka topic
            final Message message = createMessage(
                crossSellingRecommendation.idLoyaltyCard(),
                crossSellingRecommendation.idShops());
            this.sendMessage(message, producer);
        }
        catch (Exception e) {
            LOGGER.warn("Exception caught in producer: {}", e.getMessage());
        }
    }

    private Message createMessage(
        final Long loyaltyCardId,
        final List<Long> shopIds)
    {
        final Message message = new Message();
        message.setLoyaltyCardId(loyaltyCardId);
        message.setShopIds(shopIds);
        message.setSeqKey(TOPIC_NAME + "_" + String.valueOf(((Double) (Math.random() * 10)).intValue()));
        message.setAsText(
            "{\"CrossSellingRecommendation\":{" +
                "\"loyaltyCardId\":\"" + loyaltyCardId + "\"," +
                "\"shopIds\":\"" + shopIds + "\"" +
            "}}"
        );
        return message;
    }

    private void sendMessage(Message msg, KafkaProducer<String, String> producer) {
        LOGGER.info("Sending message {} to topic {}", msg.getAsText(), TOPIC_NAME);
        String seqKey = msg.getSeqKey();
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, seqKey, msg.getAsText());
        producer.send(record);
        LOGGER.info("Sent message with key {}", seqKey);
    }
}
