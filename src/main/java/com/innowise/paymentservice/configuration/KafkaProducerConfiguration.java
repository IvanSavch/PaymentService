package com.innowise.paymentservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.paymentservice.model.entity.Payment;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfiguration {
    @Bean
    public ProducerFactory<String, Payment> producerFactory(ObjectMapper objectMapper) {
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        JsonSerializer<Payment> serializer = new JsonSerializer<>(objectMapper);
        serializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(
                configProperties,
                new StringSerializer(),
                serializer);
    }

    @Bean
    public KafkaTemplate<String, Payment> kafkaTemplate(ProducerFactory<String, Payment> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
