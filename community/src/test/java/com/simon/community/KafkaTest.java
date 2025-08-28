package com.simon.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author zhengx
 * @version 1.0
 */
@SpringBootTest(classes = CommunityApplication.class)
public class KafkaTest {

    @Autowired
    KafkaProducer kafkaProducer;

    @Test
    public void testKafka(){
        kafkaProducer.sendMessage("test","你好");
        kafkaProducer.sendMessage("test","在吗");

        try {
            Thread.sleep(10*1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

@Component
class KafkaProducer{
    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void sendMessage(String topic,String message){
        kafkaTemplate.send(topic,message);
    }
}

@Component
class KafkaConsumer{

    @KafkaListener(topics = "test")
    public void handleMessage(ConsumerRecord record){
        System.out.println(record.value());
    }
}
