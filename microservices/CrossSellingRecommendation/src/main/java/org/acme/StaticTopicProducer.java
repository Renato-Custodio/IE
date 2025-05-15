package org.acme;

import org.acme.model.Message;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.*;

import jakarta.inject.Inject;


public class StaticTopicProducer extends Thread  {
    private static final String TOPIC_NAME = "CrossSellingRecommendation";

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

            final String name = "dummyName";
            final Message message = createMessage(name);
            this.sendMessage(message, producer);
            String query = "INSERT INTO CrossSellingRecommendations (name) VALUES ("
                + "'" + name + "',"
                + ")";

            client.query(query).execute().await().indefinitely();
        }
        catch (Exception e) 
		{  System.out.println("Exception is caught");  }
    }

    private Message createMessage(final String name) {
        final Message message = new Message();
        message.setName(name);
        message.setSeqkey(TOPIC_NAME + "_" + String.valueOf(((Double) (Math.random() * 10)).intValue()));
        message.setAsText(
            "{\"CrossSellingRecommendation\":{" +
                "\"name\":\"" + name + "\"," +
            "}}"
        );
        return message;
    }

    private void sendMessage(Message msg, KafkaProducer<String, String> producer)
    {
        System.out.println("This is the message to send = " + msg.getAsText());
        String seqKey = msg.getSeqkey();
        System.out.println("Sending new message to Kafka, to the topic = " + TOPIC_NAME + ", with key=" + seqKey);
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, seqKey, msg.getAsText());
        producer.send(record);
        System.out.println("Sent...");
    }
}
