package com.ecommerce.inventoryservice.kafka;

import com.ecommerce.inventoryservice.event.StockDeductEvent;
import com.ecommerce.inventoryservice.model.Inventory;
import com.ecommerce.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockDeductConsumer {

    private final InventoryRepository inventoryRepository;

    @KafkaListener(topics = "stock-deduct-topic", groupId = "inventory-group")
    public void handleStockDeductEvent(StockDeductEvent event) {
        log.info("Received stock deduction request: {}", event);

        Inventory inventory = inventoryRepository.findBySkuCode(event.getSkuCode())
                .orElseThrow(() -> new RuntimeException("SKU not found: " + event.getSkuCode()));

        if (inventory.getQuantity() >= event.getQuantity()) {
            inventory.setQuantity(inventory.getQuantity() - event.getQuantity());
            inventoryRepository.save(inventory);
            log.info("Stock deducted for SKU: {}", event.getSkuCode());
        } else {
            log.error("Not enough stock to deduct for SKU: {}", event.getSkuCode());
        }
    }
}
