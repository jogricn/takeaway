package com.jogyco.takeaway.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducer {

    //@Autowired
    KafkaTemplate<String, EmployeeMessage> kafkaTemplate;
    @Value("${spring.kafka.topic.name}")
    private String topic;

    public KafkaProducer(KafkaTemplate<String, EmployeeMessage> kafkaTemplate){
        this.kafkaTemplate=kafkaTemplate;
    }

    public void send(EmployeeMessage employeeMessage){
        kafkaTemplate.send(topic, employeeMessage.getId(), employeeMessage);
    }

    public void send(String topicName, String key, EmployeeMessage employeeMessage) {
        var future = kafkaTemplate.send(topicName, key, employeeMessage);
        future.whenComplete((sendResult, exception) -> {
            if (exception != null) {
                log.error(exception.getMessage());
                future.completeExceptionally(exception);
            }
            else {
                future.complete(sendResult);
            }
            log.info("Employee with id {} was {}. Message offset is {}",
                    employeeMessage.getId(),
                    employeeMessage.getEvent(),
                    sendResult.getRecordMetadata().offset());
        });
    }
}