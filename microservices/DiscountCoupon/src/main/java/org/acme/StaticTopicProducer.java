package org.acme;

import io.vertx.mutiny.sqlclient.Tuple;
import jakarta.inject.Inject;
import org.acme.model.Message;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Properties;


public class StaticTopicProducer extends Thread  {
    private static final String TOPIC_NAME = "DiscountCoupon";

    private String kafkaServers;

    @Inject
    io.vertx.mutiny.mysqlclient.MySQLPool client;

    public StaticTopicProducer(String kafkaServersReceived, io.vertx.mutiny.mysqlclient.MySQLPool clientReceived)
    {
        kafkaServers = kafkaServersReceived;
        client = clientReceived;
        // Will likely receive information about what to produce
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

            final Long idLoyaltyCard = 1L;
            final Long idShop = 1L;
            final String discountType = "Percentage";
            final LocalDateTime expiryDate = LocalDateTime.from(Instant.now());

            //Storing entity in db
            this.persistDiscountCoupon(idLoyaltyCard, idShop, discountType, expiryDate);

            //Producing message to kafka topic
            final Message message = createMessage(idLoyaltyCard, idShop, discountType, expiryDate);
            this.sendMessage(message, producer);
        }
        catch (Exception e) 
		{  System.out.println("Exception is caught");  }
    }

    private Message createMessage(
        final Long idLoyaltyCard,
        final Long idShop,
        final String discountType,
        final LocalDateTime expiryDate)
    {
        final Message message = new Message();
        message.setIdLoyaltyCard(idLoyaltyCard);
        message.setIdShop(idShop);
        message.setDiscountType(discountType);
        message.setExpiryDate(expiryDate);
        message.setSeqKey(TOPIC_NAME + "_" + String.valueOf(((Double) (Math.random() * 10)).intValue()));
        message.setAsText(
            "{\"Discount_Coupon\":{" +
                "\"idLoyaltyCard\":\"" + idLoyaltyCard + "\"," +
                "\"idShop\":\"" + idShop + "\"," +
                "\"discountType\":\"" + discountType + "\"," +
                "\"expiryDate\":\"" + expiryDate + "\"" +
            "}}");
        return message;
    }

    private void sendMessage(Message msg, KafkaProducer<String, String> producer)
    {
        System.out.println("This is the message to send = " + msg.getAsText());
        String seqKey = msg.getSeqKey();
        System.out.println("Sending new message to Kafka, to the topic = " + TOPIC_NAME + ", with key=" + seqKey);
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, seqKey, msg.getAsText());
        producer.send(record);
        System.out.println("Sent...");
    }

    private void persistDiscountCoupon(
        final Long idLoyaltyCard,
        final Long idShop,
        final String discountType,
        final LocalDateTime expiryDate)
    {
        client.preparedQuery("INSERT INTO DiscountCoupons(idLoyaltyCard, idShop, discount, Datetime) VALUES (?,?,?,?)")
            .execute(Tuple.of(idLoyaltyCard, idShop, discountType, expiryDate)).await().indefinitely();
    }
}
