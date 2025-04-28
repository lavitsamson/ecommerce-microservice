package com.ecommerce.inventoryservice.kafka;

import com.ecommerce.inventoryservice.event.ProductCreatedEvent;
import com.ecommerce.inventoryservice.model.Inventory;
import com.ecommerce.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final InventoryRepository inventoryRepository;

    @KafkaListener(topics = "product-created", groupId = "inventory-group")
    public void consume(ProductCreatedEvent event) {
        log.info("Received ProductCreatedEvent: {}", event);

        // Add new inventory record
        Inventory inventory = Inventory.builder()
                .skuCode(event.getSkuCode())
                .quantity(event.getQuantity()) // Default quantity or whatever logic
                .build();

        inventoryRepository.save(inventory);

        log.info("Inventory created for product: {}", event.getSkuCode());
    }
}
