package com.ecommerce.inventoryservice.service;

import com.ecommerce.inventoryservice.repository.InventoryRepository;
import com.ecommerce.inventoryservice.model.Inventory;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private final InventoryRepository repository;

    public InventoryService(InventoryRepository repository) {
        this.repository = repository;
    }

    public boolean isInStock(String skuCode) {
        return repository.findBySkuCode(skuCode)
                .map(inventory -> inventory.getQuantity() > 0)
                .orElse(false);
    }
}
