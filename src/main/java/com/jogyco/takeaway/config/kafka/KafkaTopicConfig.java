package com.jogyco.takeaway.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topic.name}")
    private String TOPIC;

    @Bean
    public NewTopic takeawayTopic() {
        return TopicBuilder.name(TOPIC)
                .build();
    }
}
