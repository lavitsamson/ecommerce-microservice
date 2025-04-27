package com.ecommerce.orderservice.kafka;

import com.ecommerce.orderservice.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void sendMessage(OrderPlacedEvent event) {
        kafkaTemplate.send("orderPlacedTopic", event);
    }
}
