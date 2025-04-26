package com.ecommerce.inventoryservice.controller;

import com.ecommerce.inventoryservice.service.InventoryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @GetMapping("/{skuCode}")
    public boolean isInStock(@PathVariable String skuCode) {
        return service.isInStock(skuCode);
    }
}
