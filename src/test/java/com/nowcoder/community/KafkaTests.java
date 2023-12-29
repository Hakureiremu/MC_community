//package com.nowcoder.community;
//
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.dao.DataAccessException;
//import org.springframework.data.redis.core.BoundValueOperations;
//import org.springframework.data.redis.core.RedisOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.SessionCallback;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.TimeUnit;
//
////@SpringBootTest
//public class KafkaTests {
//
//    @Autowired
//    private KafkaProducer kafkaProducer;
//
//    @Test
//    public void testKafka(){
//        //生产者发消息主动调用
//        kafkaProducer.sendMessage("test", "hello");
//        kafkaProducer.sendMessage("test", "are you here?");
//
//        try{
//            Thread.sleep(1000 * 10);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//}
//
//@Component
//class KafkaProducer{
//    @Autowired
//    private KafkaTemplate kafkaTemplate;
//
//    public void sendMessage(String topic, String content){
//        kafkaTemplate.send(topic, content);
//    }
//}
//
//@Component
//class KafkaConsumer{
//    @KafkaListener(topics = ("test"))
//    public void handleMessage(ConsumerRecord consumerRecord){
//        System.out.println(consumerRecord.value());
//    }
//
//}