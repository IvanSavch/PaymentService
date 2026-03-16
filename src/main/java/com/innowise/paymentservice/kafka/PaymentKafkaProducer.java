package com.innowise.paymentservice.kafka;

import com.innowise.paymentservice.model.entity.Payment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentKafkaProducer {
    private final KafkaTemplate<String, Payment> kafkaTemplate;

    public PaymentKafkaProducer(KafkaTemplate<String, Payment> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void send(Payment payment){
        kafkaTemplate.send("CREATE_PAYMENT",payment);
    }
}
