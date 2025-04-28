package com.ecommerce.orderservice.kafka;

import com.ecommerce.orderservice.event.OrderPlacedEvent;
import com.ecommerce.orderservice.event.StockDeductEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaOrderTemplate;
    private final KafkaTemplate<String, StockDeductEvent> kafkaStockDeductionTemplate;

    public void sendMessage(OrderPlacedEvent event) {
        kafkaOrderTemplate.send("orderPlacedTopic", event);
    }

    public void sendStockDeductEvent(StockDeductEvent event) {
        kafkaStockDeductionTemplate.send("stock-deduct-topic", event);
    }
}
