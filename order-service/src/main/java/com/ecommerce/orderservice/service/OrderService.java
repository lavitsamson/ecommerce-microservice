package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.client.InventoryServiceClient;
import com.ecommerce.orderservice.dto.OrderRequest;
import com.ecommerce.orderservice.dto.OrderLineItemsDto;
import com.ecommerce.orderservice.event.OrderPlacedEvent;
import com.ecommerce.orderservice.kafka.OrderProducer;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderLineItems;
import com.ecommerce.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryServiceClient inventoryServiceClient;
    private final OrderProducer orderProducer;

    public String placeOrder(OrderRequest orderRequest) {
        // Check if all items are in stock
        boolean allProductsInStock = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .allMatch(item -> inventoryServiceClient.isInStock(item.getSkuCode()));

        if (!allProductsInStock) {
            throw new IllegalArgumentException("One or more products are not in stock. Please try again later.");
        }

        // Proceed with saving the order
        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderDate(LocalDateTime.now())
                .orderLineItemsList(orderRequest.getOrderLineItemsDtoList()
                        .stream()
                        .map(this::mapToEntity)
                        .toList())
                .build();

        orderRepository.save(order);

        // Send Kafka Event
        orderProducer.sendMessage(new OrderPlacedEvent(order.getOrderNumber()));

        return "Order placed successfully!";
    }

    private OrderLineItems mapToEntity(OrderLineItemsDto dto) {
        return OrderLineItems.builder()
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .skuCode(dto.getSkuCode())
                .build();
    }
}
